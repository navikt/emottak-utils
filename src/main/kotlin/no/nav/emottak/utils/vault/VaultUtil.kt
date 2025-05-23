package no.nav.emottak.utils.vault

import com.bettercloud.vault.SslConfig
import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.bettercloud.vault.VaultException
import com.bettercloud.vault.response.AuthResponse
import com.bettercloud.vault.response.LogicalResponse
import com.bettercloud.vault.response.LookupResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import no.nav.emottak.utils.environment.getEnvVar
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Timer
import java.util.TimerTask

class VaultUtil private constructor() {
    private val client: Vault
    private val timer: Timer = Timer("VaultScheduler", true)

    init {
        val vaultConfig = try {
            VaultConfig()
                .address(getEnvVar("VAULT_ADDR", "https://vault.adeo.no"))
                .token(vaultToken)
                .openTimeout(5)
                .readTimeout(30)
                .also {
                    if (IS_MOCKED) {
                        it.sslConfig(SslConfig().verify(false).build())
                    } else {
                        it.sslConfig(SslConfig().build())
                    }
                }
                .build()
        } catch (e: VaultException) {
            throw RuntimeException("Could not instantiate the Vault REST client", e)
        }
        client = Vault(vaultConfig, 1)

        // Verify that the token is ok
        val lookupSelf: LookupResponse
        try {
            lookupSelf = client.auth().lookupSelf()
        } catch (e: VaultException) {
            if (e.httpStatusCode == 403) {
                throw RuntimeException("The application's vault token seems to be invalid", e)
            } else {
                throw RuntimeException("Could not validate the application's vault token", e)
            }
        }

        if (lookupSelf.isRenewable) {
            class RefreshTokenTask : TimerTask() {
                override fun run() {
                    try {
                        logger.info("Refreshing Vault token (old TTL = ${client.auth().lookupSelf().ttl} seconds)")
                        val response: AuthResponse = client.auth().renewSelf()
                        logger.info("Refreshed Vault token (new TTL = ${client.auth().lookupSelf().ttl} seconds)")
                        timer.schedule(
                            RefreshTokenTask(),
                            suggestedRefreshInterval(response.authLeaseDuration * 1000)
                        )
                    } catch (e: VaultException) {
                        logger.error("Could not refresh the Vault token", e)
                        logger.warn("Waiting 5 secs before trying to refresh the Vault token")
                        timer.schedule(RefreshTokenTask(), 5000)
                    }
                }
            }
            logger.info("Starting a refresh timer on the vault token (TTL = ${lookupSelf.ttl} seconds)")
            timer.schedule(RefreshTokenTask(), suggestedRefreshInterval(lookupSelf.ttl * 1000))
        } else {
            logger.warn("Vault token is not renewable")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(VaultUtil::class.java)

        private const val VAULT_TOKEN_PROPERTY: String = "VAULT_TOKEN"
        private const val VAULT_TOKEN_PATH_PROPERTY: String = "VAULT_TOKEN_PATH"
        private const val MIN_REFRESH_MARGIN: Int = 10 * 60 * 1000 // 10 min in ms;

        private val instance: VaultUtil by lazy { VaultUtil() }

        private var IS_MOCKED = false
        fun setAsMocked() {
            println("VAULT is marked as mocked - SSL verification disabled")
            IS_MOCKED = true
        }

        fun getClient() = instance.client

        fun readVaultPathData(path: String): Map<String, String> =
            getClient().logical().read(path).extractData(logger)?.also {
                logger.info("Looked up vault path '$path'")
            } ?: throw RuntimeException("Failed to read vault path '$path'")

        fun readVaultPathResource(path: String, resource: String): String {
            val vaultData = readVaultPathData(path)
            return vaultData[resource]?.also {
                logger.info("Got vault resource '$resource' from vault path '$path'")
            } ?: throw RuntimeException("Failed to read vault path resource: '$path/$resource'. Available keys: ${vaultData.keys}")
        }

        /**
         * For looking up a Vault ServiceUser or Credential.
         *
         * Examples of Vault-paths to lookup with this method:
         * /serviceuser/data/dev/srv-ebms-payload
         * /oracle/data/dev/creds/emottak_q1-nmt3
         *
         * @property envVarVaultPath The name of environment variable that contains a Vault-path.
         * @property defaultVaultPath Default Vault-path if environment variable is not found.
         */
        fun getVaultCredential(envVarVaultPath: String, defaultVaultPath: String): VaultUser {
            val path = getEnvVar(envVarVaultPath, defaultVaultPath)
            val vaultData = readVaultPathData(path)
            val username = vaultData["username"] ?: throw RuntimeException("Failed to read 'username' from Vault (path: '$path'). Available keys: ${vaultData.keys}")
            val password = vaultData["password"] ?: throw RuntimeException("Failed to read 'password' from Vault (path: '$path'). Available keys: ${vaultData.keys}")
            return VaultUser(username, password)
        }

        // We should refresh tokens from Vault before they expire, so we add a MIN_REFRESH_MARGIN margin.
        // If the token is valid for less than MIN_REFRESH_MARGIN * 2, we use duration / 2 instead.
        private fun suggestedRefreshInterval(duration: Long): Long {
            return if (duration < MIN_REFRESH_MARGIN * 2) {
                duration / 2
            } else {
                duration - MIN_REFRESH_MARGIN
            }
        }

        private fun getProperty(propertyName: String): String? {
            return System.getProperty(propertyName, System.getenv(propertyName))
        }

        private val vaultToken: String
            get() {
                try {
                    if (!getProperty(VAULT_TOKEN_PROPERTY).isNullOrBlank()) {
                        return getEnvVar(VAULT_TOKEN_PROPERTY)
                    } else if (!getProperty(VAULT_TOKEN_PATH_PROPERTY).isNullOrBlank()) {
                        val encoded: ByteArray = Files.readAllBytes(Paths.get(getEnvVar(VAULT_TOKEN_PATH_PROPERTY)))
                        return String(encoded, charset("UTF-8")).trim { it <= ' ' }
                    } else if (Files.exists(Paths.get("/var/run/secrets/nais.io/vault/vault_token"))) {
                        val encoded: ByteArray =
                            Files.readAllBytes(Paths.get("/var/run/secrets/nais.io/vault/vault_token"))
                        return String(encoded, charset("UTF-8")).trim { it <= ' ' }
                    } else {
                        throw RuntimeException("Neither $VAULT_TOKEN_PROPERTY or $VAULT_TOKEN_PATH_PROPERTY is set")
                    }
                } catch (e: Exception) {
                    throw RuntimeException("Could not get a vault token for authentication", e)
                }
            }
    }
}

fun String.parseVaultJsonObject(field: String) = Json.parseToJsonElement(
    this
).jsonObject[field]!!.jsonPrimitive.content

fun String.toJson() =
    try {
        Json.parseToJsonElement(this)
            .jsonObject
            .mapValues { (_, jsonElement) ->
                jsonElement.jsonPrimitive.content
            }
    } catch (e: Exception) {
        null
    }

data class VaultUser(val username: String, val password: String)

private fun LogicalResponse.extractData(logger: Logger): Map<String, String>? {
    val rawData: String? = this.data?.get("data")
    val jsonData = rawData?.toJson()
    if (jsonData != null) {
        logger.info("Extracted 'data' from requested Vault-path.")
        return jsonData
    }
    return this.data
}
