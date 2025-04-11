package no.nav.emottak.utils.kafka.model

enum class EventDataType(val value: String) {
    ERROR_MESSAGE("error_message"),
    MESSAGE_ID("message_id"),
    CPA_ID("cpa_id"),
    QUEUE_NAME("queue_name"),
    JURIDISK_LOGG_ID("juridisk_logg_id"),
    ENCRYPTION_DETAILS("encryption_details"),
    REFERENCE("reference");

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromValue(value: String): EventDataType? {
            return values().find { it.value == value }
        }
    }
}
