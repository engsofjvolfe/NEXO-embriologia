package org.nexo.motor.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nexo.motor.app.connectivity.ConnectionStateListener
import org.nexo.motor.app.connectivity.PieceReadListener
import org.nexo.motor.app.connectivity.RadioStateListener
import org.nexo.motor.core.connectivity.ConnectionState
import org.nexo.motor.core.connectivity.Radio
import org.nexo.motor.core.content.ContentEvent
import org.nexo.motor.core.content.ContentInstance
import org.nexo.motor.core.report.EventConfiguration
import org.nexo.motor.core.report.SessionConfiguration
import org.nexo.motor.core.report.buildReportCsv
import org.nexo.motor.core.report.buildReportPdfLines
import org.nexo.motor.core.session.SessionEvent
import org.nexo.motor.core.session.SessionState
import org.nexo.motor.core.session.continueToNextEvent
import org.nexo.motor.core.session.deleteSessionState
import org.nexo.motor.core.session.eventComplete
import org.nexo.motor.core.session.goIdle
import org.nexo.motor.core.session.hintAvailable
import org.nexo.motor.core.session.pause
import org.nexo.motor.core.session.recordAttempt
import org.nexo.motor.core.session.referenceImage
import org.nexo.motor.core.session.saveSessionState
import org.nexo.motor.core.session.showStudySuggestion
import org.nexo.motor.core.session.skipPosition
import org.nexo.motor.core.session.studySuggestionAvailable
import org.nexo.motor.core.session.useHint
import org.nexo.motor.core.summary.ChainOutcome
import org.nexo.motor.core.summary.PositionOutcome
import org.nexo.motor.core.summary.buildChainSkipSynthesis
import org.nexo.motor.core.summary.buildContinuousSynthesis
import org.nexo.motor.core.summary.buildSkipMessage
import java.io.File

class SessionViewModel(
    private val instance: ContentInstance,
    private val configuration: SessionConfiguration,
    initialState: SessionState,
    private val pausedStateFile: File? = null,
    private val now: () -> Long = { System.currentTimeMillis() },
) : ViewModel(), PieceReadListener, ConnectionStateListener, RadioStateListener {

    private var sessionState: SessionState = initialState
    private var lastConnectionState: ConnectionState? = null
    private var nfcEnabled: Boolean = true
    private var bluetoothEnabled: Boolean = true
    private var idleJob: Job? = null

    private val _uiState = MutableStateFlow(SessionUiState(screen = referenceScreenFor(initialState)))
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        scheduleIdleTimeout()
    }

    override fun onPieceRead(tagId: String) {
        if (_uiState.value.screen !is SessionScreen.AwaitingAttempt) return
        val event = contentEvent(sessionState.expectedEventName)
        val expectedFrame = event.frames[sessionState.expectedPosition - 1]
        val accepted = tagId == expectedFrame.tagId
        sessionState = recordAttempt(sessionState, tagId, expectedFrame.tagId, now())
        scheduleIdleTimeout()
        setScreen(
            if (accepted) {
                SessionScreen.AttemptAccepted(confirmationText = expectedFrame.confirmationText)
            } else {
                resolveAfterRejection(event)
            }
        )
    }

    override fun onConnectionStateChanged(state: ConnectionState) {
        lastConnectionState = state
        if (_uiState.value.screen is SessionScreen.AwaitingAttempt) {
            setScreen(SessionScreen.AwaitingAttempt(connectionState = state, disabledRadio = currentDisabledRadio()))
        }
    }

    override fun onRadioStateChanged(radio: Radio, enabled: Boolean) {
        when (radio) {
            Radio.NFC -> nfcEnabled = enabled
            Radio.BLUETOOTH -> bluetoothEnabled = enabled
        }
        if (_uiState.value.screen is SessionScreen.AwaitingAttempt) {
            setScreen(SessionScreen.AwaitingAttempt(connectionState = lastConnectionState, disabledRadio = currentDisabledRadio()))
        }
    }

    // Decisions/0044: o aviso reflete só o rádio do caminho que a pessoa está de fato usando --
    // connectionState não nulo indica acessório Bluetooth em jogo (só o estado do Bluetooth
    // importa); connectionState nulo indica NFC direto (só o estado do NFC importa). O estado do
    // rádio que não é o caminho em uso nunca aparece -- avisar sobre um rádio que a pessoa nem
    // pretende usar seria instrução fora de contexto, não confirmação (Conceito, seção 8).
    private fun currentDisabledRadio(): Radio? = if (lastConnectionState != null) {
        if (!bluetoothEnabled) Radio.BLUETOOTH else null
    } else {
        if (!nfcEnabled) Radio.NFC else null
    }

    fun onSkipRequested() {
        val eventConfig = configuration.eventConfiguration(sessionState.expectedEventName) ?: return
        if (!eventConfig.skipEnabled) return
        sessionState = skipPosition(sessionState, now())
        setScreen(resolveAfterAdvance())
    }

    fun onScreenAcknowledged() {
        val next = when (_uiState.value.screen) {
            is SessionScreen.Reference -> awaitingAttemptScreen()
            is SessionScreen.AttemptAccepted -> resolveAfterAdvance()
            is SessionScreen.AttemptRejected,
            is SessionScreen.HintShown,
            is SessionScreen.StudySuggestionShown -> awaitingAttemptScreen()
            is SessionScreen.AwaitingAttempt, is SessionScreen.EventSummary, is SessionScreen.SkipMessageShown -> return
        }
        setScreen(next)
    }

    fun onContinueRequested() {
        val hasNext = when (val screen = _uiState.value.screen) {
            is SessionScreen.EventSummary -> screen.hasNextEvent
            is SessionScreen.SkipMessageShown -> screen.hasNextEvent
            else -> return
        }
        if (!hasNext) return
        val nextEventName = configuration.eventNames[configuration.eventNames.indexOf(sessionState.expectedEventName) + 1]
        sessionState = continueToNextEvent(sessionState, nextEventName)
        setScreen(referenceScreenFor(sessionState))
    }

    fun onExitRequested() {
        _uiState.update { it.copy(exitConfirmationRequested = true) }
    }

    fun onExitCancelled() {
        _uiState.update { it.copy(exitConfirmationRequested = false) }
    }

    fun onExitConfirmed(writeReport: (csv: String, pdfLines: List<String>) -> Unit) {
        idleJob?.cancel()
        writeReport(buildReportCsv(configuration, sessionState.log), buildReportPdfLines(configuration, sessionState.log))
        pausedStateFile?.let { deleteSessionState(it) }
        _uiState.update { it.copy(exitConfirmationRequested = false) }
    }

    fun onPauseRequested() {
        idleJob?.cancel()
        sessionState = pause(sessionState, now())
        pausedStateFile?.let { saveSessionState(sessionState, it) }
    }

    private fun scheduleIdleTimeout() {
        idleJob?.cancel()
        idleJob = viewModelScope.launch {
            delay(configuration.idleThresholdMillis)
            sessionState = goIdle(sessionState, now())
            pausedStateFile?.let { saveSessionState(sessionState, it) }
        }
    }

    private fun setScreen(screen: SessionScreen) {
        _uiState.update { it.copy(screen = screen) }
    }

    private fun resolveAfterRejection(event: ContentEvent): SessionScreen {
        val eventConfig = configuration.eventConfiguration(sessionState.expectedEventName)
        val studyThreshold = eventConfig?.studyThreshold
        val hintThreshold = eventConfig?.hintThreshold
        return when {
            event.hintEnabled && studyThreshold != null && hintUsedForCurrentPosition() &&
                studySuggestionAvailable(sessionState, studyThreshold) -> {
                if (!studySuggestionShownForCurrentPosition()) {
                    sessionState = showStudySuggestion(sessionState, now())
                }
                SessionScreen.StudySuggestionShown(skipAvailable = eventConfig?.skipEnabled ?: false)
            }
            event.hintEnabled && hintThreshold != null && hintAvailable(sessionState, hintThreshold) -> {
                if (!hintUsedForCurrentPosition()) {
                    sessionState = useHint(sessionState, now())
                }
                SessionScreen.HintShown(hintContent = event.hintContent ?: "")
            }
            else -> SessionScreen.AttemptRejected
        }
    }

    private fun resolveAfterAdvance(): SessionScreen {
        val event = contentEvent(sessionState.expectedEventName)
        return if (!eventComplete(sessionState, event.frames.size)) {
            awaitingAttemptScreen()
        } else {
            resolveEventCompletion()
        }
    }

    private fun awaitingAttemptScreen(): SessionScreen.AwaitingAttempt =
        SessionScreen.AwaitingAttempt(connectionState = lastConnectionState, disabledRadio = currentDisabledRadio())

    private fun resolveEventCompletion(): SessionScreen {
        val eventName = sessionState.expectedEventName
        val outcomes = positionsRange(eventName).map { position -> positionOutcome(eventName, position) }
        val hasLostPosition = outcomes.any { it is PositionOutcome.Skipped }
        val hasNextEvent = configuration.eventNames.indexOf(eventName) < configuration.eventNames.lastIndex
        val chainSynthesis = if (configuration.eventNames.size > 1 && !hasNextEvent) buildChainSynthesis() else null
        return if (hasLostPosition) {
            SessionScreen.SkipMessageShown(
                message = buildSkipMessage(outcomes),
                hasNextEvent = hasNextEvent,
                chainSynthesis = chainSynthesis,
            )
        } else {
            val fragments = positionsRange(eventName).map { position -> contentEvent(eventName).frames[position - 1].summaryFragment }
            SessionScreen.EventSummary(
                synthesis = buildContinuousSynthesis(fragments),
                hasNextEvent = hasNextEvent,
                chainSynthesis = chainSynthesis,
            )
        }
    }

    private fun buildChainSynthesis(): ChainSynthesisResult {
        val anyLost = configuration.eventNames.any { eventName ->
            positionsRange(eventName).any { position -> !wasFilled(eventName, position) }
        }
        return if (!anyLost) {
            val fragments = configuration.eventNames.flatMap { eventName ->
                positionsRange(eventName).map { position -> contentEvent(eventName).frames[position - 1].summaryFragment }
            }
            ChainSynthesisResult.Continuous(synthesis = buildContinuousSynthesis(fragments))
        } else {
            val outcomes = configuration.eventNames.flatMap { eventName ->
                positionsRange(eventName).map { position -> if (wasFilled(eventName, position)) ChainOutcome.Filled else ChainOutcome.Lost }
            }
            ChainSynthesisResult.Consolidated(totals = buildChainSkipSynthesis(outcomes))
        }
    }

    private fun referenceScreenFor(state: SessionState): SessionScreen.Reference {
        val event = contentEvent(state.expectedEventName)
        val isFirstEventOfSession = state.expectedEventName == configuration.eventNames.first()
        val previousFrameImage = if (state.expectedPosition > 1) event.frames[state.expectedPosition - 2].image else null
        val lastFilledImageOfPreviousEvent = if (!isFirstEventOfSession && state.expectedPosition == 1) {
            val previousEventName = configuration.eventNames[configuration.eventNames.indexOf(state.expectedEventName) - 1]
            lastFilledFrameImage(previousEventName)
        } else {
            null
        }
        val image = referenceImage(
            startingPosition = state.expectedPosition,
            isFirstEventOfSession = isFirstEventOfSession,
            previousFrameImage = previousFrameImage,
            zeroMarkImage = event.zeroMarkImage,
            lastFilledImageOfPreviousEvent = lastFilledImageOfPreviousEvent,
        )
        return SessionScreen.Reference(referenceImage = image)
    }

    private fun lastFilledFrameImage(eventName: String): String? {
        val position = sessionState.log.asReversed()
            .firstOrNull { it is SessionEvent.AttemptAccepted && it.eventName == eventName }
            ?.position
            ?: return null
        return contentEvent(eventName).frames[position - 1].image
    }

    private fun positionOutcome(eventName: String, position: Int): PositionOutcome =
        if (wasFilled(eventName, position)) {
            PositionOutcome.Answered(position = position, confirmationText = contentEvent(eventName).frames[position - 1].confirmationText)
        } else {
            PositionOutcome.Skipped(position = position)
        }

    private fun wasFilled(eventName: String, position: Int): Boolean =
        sessionState.log.any { it is SessionEvent.AttemptAccepted && it.eventName == eventName && it.position == position }

    private fun hintUsedForCurrentPosition(): Boolean = sessionState.log.any {
        it is SessionEvent.HintUsed && it.eventName == sessionState.expectedEventName && it.position == sessionState.expectedPosition
    }

    private fun studySuggestionShownForCurrentPosition(): Boolean = sessionState.log.any {
        it is SessionEvent.StudySuggestionShown && it.eventName == sessionState.expectedEventName && it.position == sessionState.expectedPosition
    }

    private fun positionsRange(eventName: String): IntRange {
        val start = if (eventName == configuration.eventNames.first()) configuration.startingPosition else 1
        return start..contentEvent(eventName).frames.size
    }

    private fun contentEvent(name: String): ContentEvent =
        instance.themes.flatMap { it.events }.first { it.name == name }

    private fun SessionConfiguration.eventConfiguration(eventName: String): EventConfiguration? =
        events.firstOrNull { it.eventName == eventName }
}
