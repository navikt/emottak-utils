package no.nav.emottak.utils.edi2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import no.nav.emottak.utils.edi2.models.ApprecInfo
import no.nav.emottak.utils.edi2.models.ErrorMessage
import no.nav.emottak.utils.edi2.models.GetMessagesRequest
import no.nav.emottak.utils.environment.getEnvVar
import kotlin.uuid.Uuid

class EdiAdapterClient(clientProvider: () -> HttpClient) {
    private var httpClient = clientProvider.invoke()
    private val ediAdapterUrl = getEnvVar("EDI_ADAPTER_URL", "http://emottak-edi-adapter")

    suspend fun getApprecInfo(id: Uuid): Pair<List<ApprecInfo>?, ErrorMessage?> {
        val response = httpClient.get("$ediAdapterUrl/Messages/$id/apprec") {
            contentType(ContentType.Application.Json)
        }
        return handleResponse(response)
    }

    suspend fun getMessages(request: GetMessagesRequest): Pair<List<ApprecInfo>?, ErrorMessage?> {
        val response = httpClient.get("$ediAdapterUrl/Messages?${request.toUrlParams()}") {
            contentType(ContentType.Application.Json)
        }
        return handleResponse(response)
    }

    private suspend inline fun <reified T> handleResponse(httpResponse: HttpResponse): Pair<T?, ErrorMessage?> {
        return if (httpResponse.status == HttpStatusCode.OK) {
            Pair(httpResponse.body(), null)
        } else {
            Pair(null, httpResponse.body())
        }
    }
}
