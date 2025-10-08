package no.nav.emottak.utils.edi2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import no.nav.emottak.utils.edi2.models.ApprecInfo
import no.nav.emottak.utils.edi2.models.ErrorMessage
import no.nav.emottak.utils.edi2.models.GetBusinessDocumentResponse
import no.nav.emottak.utils.edi2.models.GetMessagesRequest
import no.nav.emottak.utils.edi2.models.Message
import no.nav.emottak.utils.edi2.models.PostAppRecRequest
import no.nav.emottak.utils.edi2.models.PostMessageRequest
import no.nav.emottak.utils.edi2.models.StatusInfo
import no.nav.emottak.utils.environment.getEnvVar
import kotlin.uuid.Uuid

class EdiAdapterClient(clientProvider: () -> HttpClient) {
    private var httpClient = clientProvider.invoke()
    private val ediAdapterUrl = getEnvVar("EDI_ADAPTER_URL", "http://emottak-edi-adapter")

    suspend fun getApprecInfo(id: Uuid): Pair<List<ApprecInfo>?, ErrorMessage?> {
        val response = httpClient.get("$ediAdapterUrl/messages/$id/apprec") {
            contentType(ContentType.Application.Json)
        }
        return handleResponse(response)
    }

    suspend fun getMessages(getMessagesRequest: GetMessagesRequest): Pair<List<Message>?, ErrorMessage?> {
        val response = httpClient.get("$ediAdapterUrl/messages?${getMessagesRequest.toUrlParams()}") {
            contentType(ContentType.Application.Json)
        }
        return handleResponse(response)
    }

    suspend fun postMessages(postMessagesRequest: PostMessageRequest): Pair<String?, ErrorMessage?> {
        val response = httpClient.post("$ediAdapterUrl/messages") {
            contentType(ContentType.Application.Json)
            setBody(postMessagesRequest)
        }
        return handleResponse(response)
    }

    suspend fun getMessage(id: Uuid): Pair<Message?, ErrorMessage?> {
        val response = httpClient.get("$ediAdapterUrl/messages/$id") {
            contentType(ContentType.Application.Json)
        }
        return handleResponse(response)
    }

    suspend fun getBusinessDocument(id: Uuid): Pair<GetBusinessDocumentResponse?, ErrorMessage?> {
        val response = httpClient.get("$ediAdapterUrl/messages/$id/business-document") {
            contentType(ContentType.Application.Json)
        }
        return handleResponse(response)
    }

    suspend fun getMessageStatus(id: Uuid): Pair<List<StatusInfo>?, ErrorMessage?> {
        val response = httpClient.get("$ediAdapterUrl/messages/$id/status") {
            contentType(ContentType.Application.Json)
        }
        return handleResponse(response)
    }

    suspend fun postApprec(id: Uuid, apprecSenderHerId: Int, postMessageRequest: PostAppRecRequest): Pair<String?, ErrorMessage?> {
        val response = httpClient.post("$ediAdapterUrl/messages/$id/apprec/$apprecSenderHerId") {
            contentType(ContentType.Application.Json)
            setBody(postMessageRequest)
        }
        return handleResponse(response)
    }

    suspend fun markMessageAsRead(id: Uuid, herId: Int): Pair<Boolean?, ErrorMessage?> {
        val response = httpClient.put("$ediAdapterUrl/messages/$id/read/$herId") {
            contentType(ContentType.Application.Json)
        }
        return if (response.status == HttpStatusCode.NoContent) {
            Pair(true, null)
        } else {
            Pair(null, response.body())
        }
    }

    private suspend inline fun <reified T> handleResponse(httpResponse: HttpResponse): Pair<T?, ErrorMessage?> {
        return if (httpResponse.status == HttpStatusCode.OK || httpResponse.status == HttpStatusCode.Created) {
            Pair(httpResponse.body(), null)
        } else {
            Pair(null, httpResponse.body())
        }
    }
}
