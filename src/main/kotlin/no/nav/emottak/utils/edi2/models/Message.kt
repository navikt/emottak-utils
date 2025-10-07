package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable
import no.nav.emottak.utils.serialization.InstantSerializer
import no.nav.emottak.utils.serialization.UuidSerializer
import java.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class Message(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid?,
    val contentType: String?,
    val receiverHerId: Int?,
    val senderHerId: Int?,
    val businessDocumentId: String?,
    @Serializable(with = InstantSerializer::class)
    val businessDocumentGenDate: Instant?,
    val isAppRec: Boolean?
)
