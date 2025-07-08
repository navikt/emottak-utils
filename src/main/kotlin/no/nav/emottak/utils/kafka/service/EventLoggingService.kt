package no.nav.emottak.utils.kafka.service

import no.nav.emottak.utils.config.EventLogging
import no.nav.emottak.utils.kafka.client.EventPublisherClient
import no.nav.emottak.utils.kafka.model.EbmsMessageDetail
import no.nav.emottak.utils.kafka.model.Event
import org.apache.kafka.clients.producer.RecordMetadata

class EventLoggingService(
    private val eventLoggingConfig: EventLogging,
    private val kafkaPublisherClient: EventPublisherClient
) {

    suspend fun logEvent(event: Event): Result<RecordMetadata> =
        kafkaPublisherClient.publishMessage(eventLoggingConfig.eventTopic, event.toByteArray())

    suspend fun logMessageDetails(ebmsMessageDetail: EbmsMessageDetail): Result<RecordMetadata> =
        kafkaPublisherClient.publishMessage(eventLoggingConfig.messageDetailsTopic, ebmsMessageDetail.toByteArray())
}
