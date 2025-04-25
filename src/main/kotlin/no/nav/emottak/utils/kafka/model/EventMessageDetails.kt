package no.nav.emottak.utils.kafka.model

import kotlinx.serialization.Serializable
import no.nav.emottak.utils.serialization.InstantSerializer
import no.nav.emottak.utils.serialization.UuidSerializer
import java.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class EventMessageDetails(
    @Serializable(with = UuidSerializer::class)
    val requestId: Uuid,
    val cpaId: String,
    val conversationId: String,
    val messageId: String,
    val refToMessageId: String? = null,
    val fromPartyId: String,
    val fromRole: String? = null,
    val toPartyId: String,
    val toRole: String? = null,
    val service: String,
    val action: String,
    val refParam: String? = null,
    val sender: String? = null,
    @Serializable(with = InstantSerializer::class)
    val sentAt: Instant? = null
) {
    fun toByteArray(): ByteArray {
        return jsonWithDefaults.encodeToString(this).toByteArray()
    }
}
