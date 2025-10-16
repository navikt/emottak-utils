package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val error: String? = null,
    val errorCode: Int,
    val validationErrors: List<String>? = null,
    val stackTrace: String? = null,
    val requestId: String
)
