package org.nexo.motor.app.ui

import org.nexo.motor.core.connectivity.ConnectionState
import org.nexo.motor.core.connectivity.Radio
import org.nexo.motor.core.summary.ChainSkipSynthesis
import org.nexo.motor.core.summary.SkipMessage

data class SessionUiState(
    val screen: SessionScreen,
    val exitConfirmationRequested: Boolean = false,
)

sealed interface SessionScreen {
    data class Reference(val referenceImage: String) : SessionScreen
    data class AwaitingAttempt(
        val connectionState: ConnectionState?,
        val disabledRadio: Radio? = null,
    ) : SessionScreen
    data class AttemptAccepted(val confirmationText: String?) : SessionScreen
    data object AttemptRejected : SessionScreen
    data class HintShown(val hintContent: String) : SessionScreen
    data class StudySuggestionShown(val skipAvailable: Boolean) : SessionScreen
    data class EventSummary(
        val synthesis: String,
        val hasNextEvent: Boolean,
        val chainSynthesis: ChainSynthesisResult? = null,
    ) : SessionScreen
    data class SkipMessageShown(
        val message: SkipMessage,
        val hasNextEvent: Boolean,
        val chainSynthesis: ChainSynthesisResult? = null,
    ) : SessionScreen
}

sealed interface ChainSynthesisResult {
    data class Continuous(val synthesis: String) : ChainSynthesisResult
    data class Consolidated(val totals: ChainSkipSynthesis) : ChainSynthesisResult
}
