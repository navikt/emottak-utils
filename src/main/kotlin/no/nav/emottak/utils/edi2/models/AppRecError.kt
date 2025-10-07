package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class AppRecError(
    val errorCode: String?,
    val details: String?,
    val description: String?,
    val oid: String?
)
