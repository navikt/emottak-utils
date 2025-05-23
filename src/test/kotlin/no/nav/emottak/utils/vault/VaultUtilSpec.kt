package no.nav.emottak.utils.vault

import com.bettercloud.vault.SslConfig
import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.emottak.utils.mocks.vault.VaultMock
import no.nav.emottak.utils.mocks.vault.VaultTestUtils

class VaultUtilSpec : StringSpec(
    {
        val mockPort = 9998
        val vaultAddress = "https://127.0.0.1:$mockPort"
        val serviceuserVaultPath = "serviceuser/data/dev/srv-ebms-payload"
        val credentialVaultPath = "oracle/data/dev/creds/testuser"
        val dataVaultPath = "oracle/data/dev/test/temp"
        val dataNullVaultPath = "oracle/data/dev/test/null"
        val dataBracesVaultPath = "oracle/data/dev/test/braces"

        beforeSpec {
            System.setProperty("VAULT_ADDR", vaultAddress)
            System.setProperty("VAULT_TOKEN", "mock_token")

            println("=== Initializing VaultMock ===")
            val responses: Map<String, String> = mapOf(
                serviceuserVaultPath to "{\"data\": {\"data\": {\"username\":\"srv-ebms-payload\", \"password\":\"srv-ebms-payload-password\"}}}",
                credentialVaultPath to "{\"data\": {\"username\":\"testuser\", \"password\":\"my_password\"}}",
                dataVaultPath to "{\"data\": {\"data\": \"My data\"}}",
                dataNullVaultPath to "{\"data\": {\"name\":\"My Name\", \"data\": null}}",
                dataBracesVaultPath to "{\"data\": {\"data\": \"{ Hello World }\"}}"
            )
            VaultUtil.setAsMocked()
            VaultTestUtils.initHttpsVaultMock(VaultMock(200, responses), mockPort)
        }

        afterSpec {
            println("=== Stopping VaultMock ===")
            VaultTestUtils.shutdownVaultMock()
        }

        "Should retreive requested path from Vault" {
            val vaultConfig = VaultConfig()
                .address(vaultAddress)
                .token("mock_token")
                .sslConfig(SslConfig().verify(false))
                .build()
            val vault = Vault(vaultConfig, 1)
            val response = vault.logical().read(credentialVaultPath)
            response.restResponse.status shouldBe 200
            response.data shouldNotBe emptyMap<String, String>()
            response.data["username"] shouldBe "testuser"
            response.data["password"] shouldBe "my_password"
        }

        "Should retreive requested resource from Vault" {
            val requestedResource = VaultUtil.readVaultPathResource(credentialVaultPath, "username")
            requestedResource shouldBe "testuser"
        }

        "Should retreive serviceuser from Vault" {
            System.setProperty("SERVICEUSER_VAULT_PATH", serviceuserVaultPath)
            val vaultUser = VaultUtil.getVaultCredential("SERVICEUSER_VAULT_PATH", "ignored/default/path")
            vaultUser.username shouldBe "srv-ebms-payload"
            vaultUser.password shouldBe "srv-ebms-payload-password"
        }

        "Should retreive credential from Vault, even when environment variable for vault-path does not exists (using default instead)" {
            val vaultUser = VaultUtil.getVaultCredential("ENV_NOT_EXIST", credentialVaultPath)
            vaultUser.username shouldBe "testuser"
            vaultUser.password shouldBe "my_password"
        }

        "Should retreive data from Vault even when it's not a JSON-string" {
            val data = VaultUtil.readVaultPathResource(dataVaultPath, "data")
            data shouldBe "My data"
        }

        "Should retreive data resource from Vault, even when it's null" {
            val requestedResource = VaultUtil.readVaultPathData(dataNullVaultPath)
            requestedResource["name"] shouldBe "My Name"
            requestedResource["data"] shouldBe null
        }

        "Should retreive data with braces from Vault as a String when it's not a valid JSON" {
            val data = VaultUtil.readVaultPathResource(dataBracesVaultPath, "data")
            data shouldBe "{ Hello World }"
        }

        "Should throw RuntimeException when path not found" {
            val exception = shouldThrow<RuntimeException> {
                System.setProperty("NON_EXISTING_VAULT_PATH", "this/path/does/not/exist")
                VaultUtil.getVaultCredential("NON_EXISTING_VAULT_PATH", "default/path")
            }
            exception.message shouldBe "Failed to read 'username' from Vault (path: 'this/path/does/not/exist'). Available keys: []"
        }

        "Should throw RuntimeException when requested resource not found" {
            val exception = shouldThrow<RuntimeException> {
                VaultUtil.readVaultPathResource(credentialVaultPath, "non-existing-resource")
            }
            exception.message shouldBe "Failed to read vault path resource: '$credentialVaultPath/non-existing-resource'. Available keys: [password, username]"
        }
    }
)
