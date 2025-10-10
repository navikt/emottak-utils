package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val error: String?,
    val errorCode: Int,
    val validationErrors: List<String>?,
    val stackTrace: String?,
    val requestId: String
)
