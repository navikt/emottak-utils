package no.nav.emottak.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun String.parseOrGenerateUuid(): Uuid = try {
    Uuid.parse(this)
} catch (e: IllegalArgumentException) {
    Uuid.random()
}
