package org.nexo.motor.core.summary

sealed interface PositionOutcome {
    val position: Int

    data class Answered(override val position: Int, val confirmationText: String?) : PositionOutcome
    data class Skipped(override val position: Int) : PositionOutcome
}

data class AnsweredPosition(val position: Int, val confirmationText: String?)

data class SkipMessage(val answered: List<AnsweredPosition>, val unansweredPositions: List<Int>)

fun buildSkipMessage(positions: List<PositionOutcome>): SkipMessage {
    val answered = positions.filterIsInstance<PositionOutcome.Answered>()
        .map { AnsweredPosition(position = it.position, confirmationText = it.confirmationText) }
    val unansweredPositions = positions.filterIsInstance<PositionOutcome.Skipped>().map { it.position }
    return SkipMessage(answered = answered, unansweredPositions = unansweredPositions)
}

enum class ChainOutcome { Filled, Lost }

data class ChainSkipSynthesis(val filledCount: Int, val lostCount: Int)

fun buildChainSkipSynthesis(outcomes: List<ChainOutcome>): ChainSkipSynthesis =
    ChainSkipSynthesis(
        filledCount = outcomes.count { it == ChainOutcome.Filled },
        lostCount = outcomes.count { it == ChainOutcome.Lost },
    )

fun buildContinuousSynthesis(summaryFragmentsInOrder: List<String>): String =
    summaryFragmentsInOrder.joinToString(separator = " ")
