package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class PostAppRecRequest(
    val appRecStatus: AppRecStatus,
    val appRecErrorList: List<AppRecError>? = null,
    val ebXmlOverrides: EbXmlInfo? = null
)
