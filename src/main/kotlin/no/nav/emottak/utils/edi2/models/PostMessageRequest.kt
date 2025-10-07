package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class PostMessageRequest(
    val businessDocument: String,
    val contentType: String,
    val ebXmlOverrides: EbXmlInfo,
    val receiverHerIdsSubset: List<Int>?
)
