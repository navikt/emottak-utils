package no.nav.emottak.utils.kafka.model

enum class EventDataType(val value: String) {
    ERROR_MESSAGE("error_message"),
    MESSAGE_ID("message_id"),
    CPA_ID("cpa_id"),
    QUEUE_NAME("queue_name"),
    JURIDISK_LOGG_ID("juridisk_logg_id"),
    ENCRYPTION_DETAILS("encryption_details"),
    REFERENCE_PARAMETER("reference_parameter"),
    SENDER_NAME("sender_name");

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromValue(value: String): EventDataType? {
            return entries.find { it.value == value }
        }
    }
}
