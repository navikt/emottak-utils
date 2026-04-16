package no.nav.emottak.utils.mocks.vault

import com.bettercloud.vault.json.Json
import com.bettercloud.vault.json.JsonObject
import org.apache.commons.io.IOUtils
import org.eclipse.jetty.io.Content
import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.SecureRequestCustomizer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.SslConnectionFactory
import org.eclipse.jetty.util.ssl.SslContextFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Optional

object VaultTestUtils {
    private lateinit var server: Server

    fun initHttpsVaultMock(mock: VaultMock, port: Int = 9998) {
        server = Server()
        val sslContextFactory: SslContextFactory.Server = SslContextFactory.Server()
        sslContextFactory.keyStorePath = VaultTestUtils::class.java.classLoader
            .getResource("vault/keystore.jks")!!.toExternalForm()
        sslContextFactory.keyStorePassword = "password"
        sslContextFactory.keyManagerPassword = "password"
        val https = HttpConfiguration()
        https.addCustomizer(SecureRequestCustomizer())
        val sslConnector = ServerConnector(
            server,
            SslConnectionFactory(sslContextFactory, "http/1.1"),
            HttpConnectionFactory(https)
        )
        sslConnector.port = port
        server.connectors = arrayOf<Connector>(sslConnector)

        server.handler = mock

        server.start()
    }

    @Throws(Exception::class)
    fun shutdownVaultMock() {
        if (!this::server.isInitialized) return
        var attemptCount = 0
        while (!server.isStopped && attemptCount < 5) {
            attemptCount++
            server.stop()
            Thread.sleep(1000)
        }
    }

    fun readRequestBody(request: Request?): Optional<JsonObject> {
        if (null == request) return Optional.empty()
        try {
            val requestBuffer = StringBuilder()
            val reader = BufferedReader(InputStreamReader(Content.Source.asInputStream(request)))
            IOUtils.readLines(reader).forEach(requestBuffer::append)
            val string = requestBuffer.toString()
            return if (string.isEmpty()) Optional.empty() else Optional.of(Json.parse(string).asObject())
        } catch (e: IOException) {
            return Optional.empty()
        }
    }

    fun readRequestHeaders(request: Request?): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (null == request) return map
        for (header in request.headers) {
            map[header.name] = header.value
        }
        return map
    }
}
