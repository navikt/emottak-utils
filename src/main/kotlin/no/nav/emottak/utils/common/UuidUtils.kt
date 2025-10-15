package no.nav.emottak.utils.common

import arrow.core.raise.catch
import kotlin.uuid.Uuid

fun String.parseOrGenerateUuid(): Uuid = catch({ Uuid.parse(this) }) { Uuid.random() }

fun String.isValidUuid(): Boolean = catch({ Uuid.parse(this); true }) { false }
