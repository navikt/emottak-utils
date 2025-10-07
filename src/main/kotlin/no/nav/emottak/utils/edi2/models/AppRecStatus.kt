package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
enum class AppRecStatus(val value: String, val description: String) {
    OK("Ok", "Ok"),
    REJECTED("Rejected", "Avvist"),
    OK_ERROR_IN_MESSAGE_PART("OkErrorInMessagePart", "Ok, feil i delmelding"),
    UNKNOWN("Unknown", "The value is not supported");

    companion object {
        fun fromValue(value: String?): AppRecStatus =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: UNKNOWN
    }
}
