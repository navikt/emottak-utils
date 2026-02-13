package no.nav.emottak.utils.kafka.model

import kotlinx.serialization.Serializable
import no.nav.emottak.utils.common.nowOsloToInstant
import no.nav.emottak.utils.serialization.InstantSerializer
import no.nav.emottak.utils.serialization.UuidSerializer
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.uuid.Uuid

@Serializable
data class EbmsMessageDetail(
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
    @Serializable(with = InstantSerializer::class)
    val sentAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val savedAt: Instant = nowOsloToInstant()
        .truncatedTo(ChronoUnit.MICROS)
) {
    fun toByteArray(): ByteArray {
        return jsonWithDefaults.encodeToString(this).toByteArray()
    }
}
