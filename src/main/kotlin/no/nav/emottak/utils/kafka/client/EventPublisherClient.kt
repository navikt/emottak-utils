package no.nav.emottak.utils.kafka.client

import io.github.nomisRev.kafka.publisher.KafkaPublisher
import no.nav.emottak.utils.config.Kafka
import no.nav.emottak.utils.config.toKafkaPublisherSettings
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

class EventPublisherClient(
    private val config: Kafka
) {
    private val kafkaPublisher = KafkaPublisher(config.toKafkaPublisherSettings())

    suspend fun publishMessage(topic: String, value: ByteArray): Result<RecordMetadata> =
        kafkaPublisher.publishScope {
            publishCatching(toProducerRecord(topic, value))
        }

    private fun toProducerRecord(topic: String, content: ByteArray): ProducerRecord<String, ByteArray> =
        ProducerRecord<String, ByteArray>(topic, content)
}
