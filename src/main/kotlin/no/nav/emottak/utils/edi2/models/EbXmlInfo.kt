package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class EbXmlInfo(
    val cpaId: String?,
    val conversationId: String?,
    val service: String?,
    val serviceType: String?,
    val action: String?,
    val senderRole: String?,
    val useSenderLevel1HerId: Boolean?,
    val receiverRole: String?,
    val applicationName: String?,
    val applicationVersion: String?,
    val middlewareName: String?,
    val middlewareVersion: String?,
    val compressPayload: Boolean?
)
