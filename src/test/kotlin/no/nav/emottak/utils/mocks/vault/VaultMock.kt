package no.nav.emottak.utils.mocks.vault

import com.bettercloud.vault.json.JsonObject
import org.eclipse.jetty.http.HttpHeader
import org.eclipse.jetty.io.Content
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.util.Callback
import java.util.Optional

/**
 * This class is used to mock out a Vault server in unit tests involving retry logic. As it extends Jetty's
 * `Handler.Abstract`, it can be passed to an embedded Jetty server and respond to actual (albeit
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
 * val vault = Vault(vaultConfig, 1)
 * val response = vault.logical().read("oracle/data/dev/creds/testuser")
 *
 * response.restResponse.status shouldBe 200
 * response.data["username"] shouldBe "testuser"
 * response.data["password"] shouldBe "my_password"
 *
 * VaultTestUtils.shutdownVaultMock(server)`
 * </blockquote>
 */
class VaultMock(
    private var mockStatus: Int = 0,
    private var mockResponses: Map<String, String> = emptyMap()
) : Handler.Abstract() {
    private var requestBody: JsonObject? = null
    private var requestHeaders: Map<String, String>? = null
    private var requestUrl: String? = null

    override fun handle(request: Request, response: Response, callback: Callback): Boolean {
        requestBody = VaultTestUtils.readRequestBody(request).orElse(null)
        requestHeaders = VaultTestUtils.readRequestHeaders(request)
        requestUrl = request.httpURI.toString()
        println("VaultMock responding to request: $requestUrl")

        response.headers.put(HttpHeader.CONTENT_TYPE, "application/json")
        val path = request.httpURI.path.split("/v1/").last()

        val body: String
        if (path == "auth/token/lookup-self") {
            response.status = 200
            body = "{\"data\": {\"policies\": []}}"
        } else if (mockResponses.containsKey(path)) {
            println("VaultMock is sending an HTTP $mockStatus code, with expected payload...")
            response.status = mockStatus
            body = mockResponses[path]!!
        } else {
            println("WARN: Unexpected path: '$path'")
            response.status = 404
            body = "{\"errors\":[\"Path not found: '$path'\"]}"
        }

        Content.Sink.write(response, true, body, callback)
        return true
    }

    fun getRequestBody(): Optional<JsonObject> = Optional.ofNullable(requestBody)
}
