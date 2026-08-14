package org.nexo.motor.core.session

import kotlin.test.Test
import kotlin.test.assertEquals

class SessionStateTest {

    @Test
    fun `errorCount conta so tentativas rejeitadas do evento, decisao 3 da ADR 0008`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptAccepted(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento B", position = 1),
        )

        assertEquals(2, errorCount(log, eventName = "Evento A"))
        assertEquals(1, errorCount(log, eventName = "Evento B"))
    }

    @Test
    fun `errorCount nunca zera, mesmo apos a posicao avancar, EI-ERR-02`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptAccepted(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 2),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 2),
            SessionEvent.AttemptAccepted(eventName = "Evento A", position = 2),
        )

        assertEquals(3, errorCount(log, eventName = "Evento A"))
    }

    @Test
    fun `errorCount devolve zero quando nao ha nenhuma tentativa rejeitada`() {
        assertEquals(0, errorCount(emptyList(), eventName = "Evento A"))
    }

    @Test
    fun `consecutiveAttempts conta so as rejeicoes seguidas mais recentes daquela posicao, EI-DIC-01`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
        )

        assertEquals(3, consecutiveAttempts(log, eventName = "Evento A", position = 1))
    }

    @Test
    fun `consecutiveAttempts zera quando a posicao e preenchida por acerto`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptAccepted(eventName = "Evento A", position = 1),
        )

        assertEquals(0, consecutiveAttempts(log, eventName = "Evento A", position = 1))
    }

    @Test
    fun `consecutiveAttempts zera quando a posicao e preenchida por pulo, EI-DIC-01`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.PositionSkipped(eventName = "Evento A", position = 1),
        )

        assertEquals(0, consecutiveAttempts(log, eventName = "Evento A", position = 1))
    }

    @Test
    fun `consecutiveAttempts nao mistura posicoes ou eventos diferentes`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 2),
            SessionEvent.AttemptRejected(eventName = "Evento B", position = 1),
        )

        assertEquals(1, consecutiveAttempts(log, eventName = "Evento A", position = 2))
        assertEquals(1, consecutiveAttempts(log, eventName = "Evento B", position = 1))
    }
}
