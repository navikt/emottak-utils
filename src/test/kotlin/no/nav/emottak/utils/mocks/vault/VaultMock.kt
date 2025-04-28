package no.nav.emottak.utils.mocks.vault

import com.bettercloud.vault.json.JsonObject
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import java.util.Optional

/**
 * This class is used to mock out a Vault server in unit tests involving retry logic. As it extends Jetty's
 * `AbstractHandler`, it can be passed to an embedded Jetty server and respond to actual (albeit
 * localhost) HTTP requests.
 *
 *
 * This basic version simply responds to requests with a pre-determined HTTP status code and response body.
 * Example usage:
 *
 * <blockquote>
 * `val server = Server(8999)
 *  val responses : Map<String, String> = mapOf(
 *      "/oracle/data/dev/creds/testuser" to "{\"data\":{\"username\":\"testuser\", \"password\":\"my_password\"}}",
 * )
 * server.handler = VaultMock(200, responses)
 * server.start()
 *
 * val vaultConfig = VaultConfig().address("http://127.0.0.1:8999").token("mock_token").build()
 * val vault = Vault(vaultConfig)
 * val response = vault.logical().read("oracle/data/dev/creds/testuser")
 *
 * response.restResponse.status shouldBe 200
 * response.data["username"] shouldBe "testuser"
 * response.data["password"] shouldBe "my_password"
 *
 * VaultTestUtils.shutdownVaultMock(server)`
 * </blockquote>
 */
class VaultMock : AbstractHandler {
    private var mockStatus = 0
    private lateinit var mockResponses: Map<String, String>
    private var requestBody: JsonObject? = null
    private var requestHeaders: Map<String, String>? = null
    private var requestUrl: String? = null

    internal constructor()

    constructor(mockStatus: Int, mockResponses: Map<String, String>) {
        this.mockStatus = mockStatus
        this.mockResponses = mockResponses
    }

    override fun handle(target: String?, baseRequest: Request?, req: HttpServletRequest?, resp: HttpServletResponse?) {
        requestBody = VaultTestUtils.readRequestBody(req).orElse(null)
        requestHeaders = VaultTestUtils.readRequestHeaders(req)
        requestUrl = req!!.requestURL.toString()
        println("VaultMock responding to request: $requestUrl")
        baseRequest!!.isHandled = true
        resp!!.status = mockStatus
        resp.contentType = "application/json"
        val path = req.pathInfo.split("/v1/")[1]

        if (path == "auth/token/lookup-self") {
            resp.status = 200
            resp.writer.println("{\"data\": {\"policies\": []}}")
        } else if (mockResponses.containsKey(path)) {
            println("VaultMock is sending an HTTP $mockStatus code, with expected payload...")
            resp.writer.println(mockResponses[path])
        } else {
            println("WARN: Unexpected path: '$path'")
            resp.status = 404
            resp.writer.println("{\"errors\":[\"Path not found: '$path'\"]}")
        }
    }

    fun getRequestBody(): Optional<JsonObject> {
        return Optional.ofNullable(requestBody)
    }
}
