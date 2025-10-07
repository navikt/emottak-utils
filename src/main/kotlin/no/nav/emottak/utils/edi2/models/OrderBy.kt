package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
enum class OrderBy(val value: Int, description: String) {
    ASC(1, "Ascending"),
    DESC(2, "Descending");

    companion object {
        fun fromValue(value: String?): OrderBy =
            entries.find { it.value == value?.toIntOrNull() } ?: ASC
    }
}
