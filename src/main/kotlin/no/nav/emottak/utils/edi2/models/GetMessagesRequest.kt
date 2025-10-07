package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class GetMessagesRequest(
    val receiverHerIds: List<Int>,
    val senderHerId: Int? = null,
    val businessDocumentId: String? = null,
    val includeMetadata: Boolean = false,
    val messagesToFetch: Int = 10,
    val orderBy: OrderBy = OrderBy.ASC
) {
    fun toUrlParams(): String {
        val params = mutableListOf<String>()

        if (receiverHerIds.isNotEmpty()) {
            receiverHerIds.forEach {
                params += "ReceiverHerIds=$it"
            }
        }

        senderHerId?.let {
            params += "SenderHerId=$it"
        }

        businessDocumentId?.let {
            params += "BusinessDocumentId=$it"
        }

        params += "IncludeMetadata=$includeMetadata"
        params += "MessagesToFetch=$messagesToFetch"
        params += "OrderBy=${orderBy.name}"

        return params.joinToString("&")
    }
}
