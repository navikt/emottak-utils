package no.nav.emottak.utils.kafka.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.nav.emottak.utils.serialization.InstantSerializer
import no.nav.emottak.utils.serialization.UuidSerializer
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.uuid.Uuid

@Serializable
data class Event(
    val eventType: EventType,
    @Serializable(with = UuidSerializer::class)
    val requestId: Uuid,
    val contentId: String? = null,
    val messageId: String,
    val eventData: String = "{}",
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = ZonedDateTime
        .now(ZoneId.of("Europe/Oslo"))
        .toInstant()
        .truncatedTo(ChronoUnit.MICROS)
) {
    fun toByteArray(): ByteArray {
        return jsonWithDefaults.encodeToString(this).toByteArray()
    }
}

val jsonWithDefaults = Json { encodeDefaults = true }
