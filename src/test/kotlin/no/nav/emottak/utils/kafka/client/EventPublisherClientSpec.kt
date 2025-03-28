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
                groupId = "ebms-provider",
                topic = "test-topic",
                eventLoggingProducerActive = false
            )

            settings = kafkaSettings()
            publisher = EventPublisherClient(settings)
        }

        "Publish two messages to Kafka - messages are received" {
            turbineScope {
                val firstPublishedEvent = randomEvent("Event 1")
                val lastPublishedEvent = randomEvent("Event 2")

                publisher.publishMessage(firstPublishedEvent.toByteArray())
                publisher.publishMessage(lastPublishedEvent.toByteArray())

                val receiver = KafkaReceiver(receiverSettings())
                val consumer = receiver.receive(settings.topic)
                    .map { Pair(it.key(), it.value()) }

                consumer.test {
                    val (_, firstValue) = awaitItem()
                    val firstEventJson = firstValue.decodeToString()
                    val firstEvent = Json.decodeFromString<Event>(firstEventJson)

                    firstEvent shouldBe firstPublishedEvent

                    val (_, secondValue) = awaitItem()
                    val lastEventJson = secondValue.decodeToString()
                    val lastEvent = Json.decodeFromString<Event>(lastEventJson)

                    lastEvent shouldBe lastPublishedEvent
                }
            }
        }

        "Publish one message to Kafka - message is received" {
            turbineScope {
                val publishedEvent = randomEvent("Event 3")

                publisher.publishMessage(publishedEvent.toByteArray())

                val receiver = KafkaReceiver(receiverSettings())
                val consumer = receiver.receive(settings.topic)
                    .map { Pair(it.key(), it.value()) }

                consumer.test {
                    val (_, _) = awaitItem()
                    val (_, _) = awaitItem()

                    val (_, value) = awaitItem()
                    val eventJson = value.decodeToString()
                    val event = Json.decodeFromString<Event>(eventJson)

                    event shouldBe publishedEvent
                }
            }
        }
    }
)

private fun randomEvent(eventData: String): Event = Event(
    eventType = EventType.entries.random(),
    requestId = Uuid.random(),
    contentId = "contentId",
    messageId = "messageId",
    eventData = eventData
)
