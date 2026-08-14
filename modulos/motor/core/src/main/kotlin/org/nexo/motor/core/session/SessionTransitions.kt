package org.nexo.motor.core.session

fun recordAttempt(state: SessionState, tagId: String, expectedTagId: String): SessionState {
    val matches = tagId == expectedTagId
    val event = if (matches) {
        SessionEvent.AttemptAccepted(eventName = state.expectedEventName, position = state.expectedPosition)
    } else {
        SessionEvent.AttemptRejected(eventName = state.expectedEventName, position = state.expectedPosition)
    }
    return state.copy(
        expectedPosition = if (matches) state.expectedPosition + 1 else state.expectedPosition,
        log = state.log + event,
    )
}

fun skipPosition(state: SessionState): SessionState {
    val event = SessionEvent.PositionSkipped(eventName = state.expectedEventName, position = state.expectedPosition)
    return state.copy(expectedPosition = state.expectedPosition + 1, log = state.log + event)
}

fun hintAvailable(state: SessionState, hintThreshold: Int): Boolean =
    consecutiveAttempts(state.log, state.expectedEventName, state.expectedPosition) >= hintThreshold

fun useHint(state: SessionState): SessionState {
    val event = SessionEvent.HintUsed(eventName = state.expectedEventName, position = state.expectedPosition)
    return state.copy(log = state.log + event)
}

fun studySuggestionAvailable(state: SessionState, studyThreshold: Int): Boolean {
    val hintUsedForCurrentPosition = state.log.asReversed()
        .takeWhile { it.eventName == state.expectedEventName && it.position == state.expectedPosition }
        .any { it is SessionEvent.HintUsed }
    return hintUsedForCurrentPosition &&
        consecutiveAttempts(state.log, state.expectedEventName, state.expectedPosition) >= studyThreshold
}

fun eventComplete(state: SessionState, totalPositions: Int): Boolean =
    state.expectedPosition > totalPositions

fun continueToNextEvent(state: SessionState, nextEventName: String): SessionState =
    state.copy(expectedEventName = nextEventName, expectedPosition = 1)

fun pause(state: SessionState): SessionState {
    val event = SessionEvent.Paused(eventName = state.expectedEventName, position = state.expectedPosition)
    return state.copy(paused = true, log = state.log + event)
}

fun resume(state: SessionState): SessionState = state.copy(paused = false)
