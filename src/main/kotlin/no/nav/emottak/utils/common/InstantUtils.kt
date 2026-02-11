package no.nav.emottak.utils.common

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

const val ZONE_ID_OSLO = "Europe/Oslo"

fun Instant.toOsloZone(): ZonedDateTime = atZone(zoneOslo())

fun nowOsloToInstant(): Instant = ZonedDateTime.now(zoneOslo()).toInstant()

fun zoneOslo(): ZoneId = ZoneId.of(ZONE_ID_OSLO)
