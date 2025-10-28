package no.nav.emottak.utils.edi2.models

import kotlinx.serialization.Serializable

@Serializable
data class StatusInfo(
    val receiverHerId: Int,
    val transportDeliveryState: DeliveryState,
    val appRecStatus: AppRecStatus? = null
)
