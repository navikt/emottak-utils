@file:OptIn(ExperimentalUuidApi::class)

package no.nav.emottak.utils.kafka.client

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.sksamuel.hoplite.Masked
import io.github.nomisRev.kafka.receiver.KafkaReceiver
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import no.nav.emottak.utils.config.Kafka
import no.nav.emottak.utils.config.KeystoreLocation
import no.nav.emottak.utils.config.KeystoreType
import no.nav.emottak.utils.config.SecurityProtocol
import no.nav.emottak.utils.config.TruststoreLocation
import no.nav.emottak.utils.config.TruststoreType
import no.nav.emottak.utils.kafka.KafkaSpec
import no.nav.emottak.utils.kafka.model.Event
import no.nav.emottak.utils.kafka.model.EventType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class EventPublisherClientSpec : KafkaSpec(
    {
        val testTopic = "test-topic"
        lateinit var settings: Kafka
        lateinit var publisher: EventPublisherClient

        beforeSpec {
            fun kafkaSettings(): Kafka = Kafka(
                bootstrapServers = container.bootstrapServers,
                securityProtocol = SecurityProtocol("PLAINTEXT"),
                keystoreType = KeystoreType(""),
                keystoreLocation = KeystoreLocation(""),
                keystorePassword = Masked(""),
                truststoreType = TruststoreType(""),
                truststoreLocation = TruststoreLocation(""),
                truststorePassword = Masked(""),
                groupId = "ebms-provider"
            )

            settings = kafkaSettings()
            publisher = EventPublisherClient(settings)
        }

        afterSpec {
            container.close()
        }

        "Publish two messages to Kafka - messages are received" {
            turbineScope {
                val firstPublishedEvent = randomEvent("Event 1")
                val lastPublishedEvent = randomEvent("Event 2")

                publisher.publishMessage(testTopic, firstPublishedEvent.toByteArray())
                publisher.publishMessage(testTopic, lastPublishedEvent.toByteArray())

                val receiver = KafkaReceiver(receiverSettings())
                val consumer = receiver.receive(testTopic)
                    .map { Pair(it.key(), it.value()) }

                consumer.test {
                    val (_, firstValue) = awaitItem()
                    compareEvents(firstPublishedEvent, firstValue)

                    val (_, secondValue) = awaitItem()
                    compareEvents(lastPublishedEvent, secondValue)
                }
            }
        }

        "Publish one message to Kafka, with conversationId - message is received" {
            turbineScope {
                val event = randomEvent("Event 3", Uuid.random().toString())
                publisher.publishMessage(testTopic, event.toByteArray())

                val receiver = KafkaReceiver(receiverSettings())
                val consumer = receiver.receive(testTopic)
                    .map { Pair(it.key(), it.value()) }

                consumer.test {
                    val (_, _) = awaitItem()
                    val (_, _) = awaitItem()

                    val (_, value) = awaitItem()
                    compareEvents(event, value)
                }
            }
        }
    }
)

private fun randomEvent(eventData: String, conversationId: String? = null): Event = Event(
    eventType = EventType.entries.random(),
    requestId = Uuid.random(),
    contentId = "contentId",
    messageId = "messageId",
    eventData = eventData,
    conversationId = conversationId
)

private fun compareEvents(event: Event, receivedValue: ByteArray) {
    val eventJson = receivedValue.decodeToString()
    val receivedEvent = Json.decodeFromString<Event>(eventJson)
    receivedEvent shouldBe event
}
