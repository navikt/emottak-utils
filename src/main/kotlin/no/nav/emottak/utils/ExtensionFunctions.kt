package no.nav.emottak.utils

import kotlin.uuid.Uuid

fun String.parseOrGenerateUuid(): Uuid = try {
    Uuid.parse(this)
} catch (e: IllegalArgumentException) {
    Uuid.random()
}
