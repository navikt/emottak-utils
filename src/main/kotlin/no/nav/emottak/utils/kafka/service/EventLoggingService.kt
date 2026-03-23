package no.nav.emottak.utils.kafka.service

import no.nav.emottak.utils.config.EventLogging
import no.nav.emottak.utils.kafka.client.EventPublisherClient
import no.nav.emottak.utils.kafka.model.EbmsMessageDetail
import no.nav.emottak.utils.kafka.model.Event

class EventLoggingService(
    private val eventLoggingConfig: EventLogging,
    private val kafkaPublisherClient: EventPublisherClient
) {

    suspend fun logEvent(event: Event) {
        kafkaPublisherClient.publishMessage(eventLoggingConfig.eventTopic, event.toByteArray())
            .getOrThrow()
    }

    suspend fun logMessageDetails(ebmsMessageDetail: EbmsMessageDetail) {
        kafkaPublisherClient.publishMessage(eventLoggingConfig.messageDetailsTopic, ebmsMessageDetail.toByteArray())
            .getOrThrow()
    }
}
