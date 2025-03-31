package no.nav.emottak.utils.kafka.service

import no.nav.emottak.utils.kafka.client.EventPublisherClient
import no.nav.emottak.utils.kafka.model.Event
import no.nav.emottak.utils.kafka.model.EventMessageDetails
import org.apache.kafka.clients.producer.RecordMetadata

class EventLoggingService(
    private val kafkaPublisherClient: EventPublisherClient
) {

    suspend fun logEvent(event: Event): Result<RecordMetadata> =
        kafkaPublisherClient.publishMessage(event.toByteArray())

    suspend fun logEventMessageDetails(eventMessageDetails: EventMessageDetails): Result<RecordMetadata> =
        kafkaPublisherClient.publishMessage(eventMessageDetails.toByteArray())
}
