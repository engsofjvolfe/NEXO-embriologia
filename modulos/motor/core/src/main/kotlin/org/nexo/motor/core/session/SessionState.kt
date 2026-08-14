package org.nexo.motor.core.session

import kotlinx.serialization.Serializable

@Serializable
sealed interface SessionEvent {
    val eventName: String
    val position: Int

    @Serializable
    data class AttemptAccepted(override val eventName: String, override val position: Int) : SessionEvent

    @Serializable
    data class AttemptRejected(override val eventName: String, override val position: Int) : SessionEvent

    @Serializable
    data class HintUsed(override val eventName: String, override val position: Int) : SessionEvent

    @Serializable
    data class PositionSkipped(override val eventName: String, override val position: Int) : SessionEvent

    @Serializable
    data class Paused(override val eventName: String, override val position: Int) : SessionEvent
}

@Serializable
data class SessionState(
    val expectedEventName: String,
    val expectedPosition: Int,
    val paused: Boolean,
    val log: List<SessionEvent>,
)

fun errorCount(log: List<SessionEvent>, eventName: String): Int =
    log.count { it is SessionEvent.AttemptRejected && it.eventName == eventName }

fun consecutiveAttempts(log: List<SessionEvent>, eventName: String, position: Int): Int =
    log.asReversed()
        .filter { it.eventName == eventName && it.position == position }
        .filter { it is SessionEvent.AttemptRejected || it is SessionEvent.AttemptAccepted || it is SessionEvent.PositionSkipped }
        .takeWhile { it is SessionEvent.AttemptRejected }
        .size
