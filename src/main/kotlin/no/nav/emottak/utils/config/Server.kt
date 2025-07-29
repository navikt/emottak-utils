package no.nav.emottak.utils.config

import kotlin.time.Duration

data class Server(
    val port: Port,
    val preWait: Duration
)

@JvmInline
value class Port(val value: Int)
