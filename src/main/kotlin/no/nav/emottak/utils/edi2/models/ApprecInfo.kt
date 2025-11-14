package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class ApprecInfo(
    val receiverHerId: Int,
    val appRecStatus: AppRecStatus? = null,
    val appRecErrorList: List<AppRecError>? = null
)
