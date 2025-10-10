package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class GetBusinessDocumentResponse(
    val businessDocument: String,
    val contentType: String,
    val contentTransferEncoding: String
)
