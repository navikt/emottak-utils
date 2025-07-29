package no.nav.emottak.utils.common.model

import kotlinx.serialization.Serializable

/* Domenemodell brukt mellom:
    - ebxml-processor.ebms-provider
    - emottak-event-manager
 */

@Serializable
data class DuplicateCheckRequest(
    val requestId: String,
    val messageId: String,
    val conversationId: String,
    val cpaId: String
)

@Serializable
data class DuplicateCheckResponse(
    val requestId: String,
    val isDuplicate: Boolean
)
