package org.nexo.motor.core.session

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionTransitionsTest {

    private val estadoInicial = SessionState(
        expectedEventName = "Evento A",
        expectedPosition = 1,
        paused = false,
        log = emptyList(),
    )

    @Test
    fun `recordAttempt avanca a posicao e marca acerto quando a peca corresponde, EI-VAL-02`() {
        val novoEstado = recordAttempt(estadoInicial, tagId = "TAG-1", expectedTagId = "TAG-1")

        assertEquals(2, novoEstado.expectedPosition)
        assertEquals(listOf(SessionEvent.AttemptAccepted(eventName = "Evento A", position = 1)), novoEstado.log)
    }

    @Test
    fun `recordAttempt nao muda a posicao quando a peca nao corresponde, EI-VAL-02`() {
        val novoEstado = recordAttempt(estadoInicial, tagId = "TAG-ERRADA", expectedTagId = "TAG-1")

        assertEquals(1, novoEstado.expectedPosition)
        assertEquals(listOf(SessionEvent.AttemptRejected(eventName = "Evento A", position = 1)), novoEstado.log)
    }

    @Test
    fun `recordAttempt trata qualquer peca errada do mesmo jeito, nao importa a origem, EI-VAL-03`() {
        val deOutroEvento = recordAttempt(estadoInicial, tagId = "TAG-DE-OUTRO-EVENTO", expectedTagId = "TAG-1")
        val jaUsadaAntes = recordAttempt(estadoInicial, tagId = "TAG-JA-USADA", expectedTagId = "TAG-1")

        assertEquals(deOutroEvento.log, jaUsadaAntes.log.map { SessionEvent.AttemptRejected(eventName = "Evento A", position = 1) })
    }

    @Test
    fun `skipPosition marca a posicao como perdida e avanca, EI-PUL-04`() {
        val novoEstado = skipPosition(estadoInicial)

        assertEquals(2, novoEstado.expectedPosition)
        assertEquals(listOf(SessionEvent.PositionSkipped(eventName = "Evento A", position = 1)), novoEstado.log)
    }

    @Test
    fun `hintAvailable libera a dica ao atingir o limiar configurado, EI-DIC-01`() {
        val comDoisErros = estadoInicial.copy(
            log = listOf(
                SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
                SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            ),
        )

        assertFalse(hintAvailable(comDoisErros, hintThreshold = 3))
        assertTrue(hintAvailable(comDoisErros, hintThreshold = 2))
    }

    @Test
    fun `consecutiveAttempts ignora dica usada no meio da sequencia de erros`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
            SessionEvent.HintUsed(eventName = "Evento A", position = 1),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1),
        )

        assertEquals(3, consecutiveAttempts(log, eventName = "Evento A", position = 1))
    }

    @Test
    fun `studySuggestionAvailable so libera depois da dica ja ter sido usada, EI-DIC-03`() {
        val semDicaUsada = estadoInicial.copy(
            log = List(5) { SessionEvent.AttemptRejected(eventName = "Evento A", position = 1) },
        )
        val comDicaUsada = estadoInicial.copy(
            log = listOf(SessionEvent.HintUsed(eventName = "Evento A", position = 1)) +
                List(5) { SessionEvent.AttemptRejected(eventName = "Evento A", position = 1) },
        )

        assertFalse(studySuggestionAvailable(semDicaUsada, studyThreshold = 5))
        assertTrue(studySuggestionAvailable(comDicaUsada, studyThreshold = 5))
    }

    @Test
    fun `eventComplete verifica se a ultima posicao ja foi preenchida`() {
        val emAndamento = estadoInicial.copy(expectedPosition = 3)
        val completo = estadoInicial.copy(expectedPosition = 4)

        assertFalse(eventComplete(emAndamento, totalPositions = 3))
        assertTrue(eventComplete(completo, totalPositions = 3))
    }

    @Test
    fun `continueToNextEvent troca so o nome do evento e reinicia a posicao, EI-ENC-02`() {
        val emAndamento = estadoInicial.copy(expectedEventName = "Evento A", expectedPosition = 4)

        val novoEstado = continueToNextEvent(emAndamento, nextEventName = "Evento B")

        assertEquals("Evento B", novoEstado.expectedEventName)
        assertEquals(1, novoEstado.expectedPosition)
    }

    @Test
    fun `pause marca a sessao como pausada e registra o momento, EI-PAU-01`() {
        val novoEstado = pause(estadoInicial)

        assertTrue(novoEstado.paused)
        assertEquals(listOf(SessionEvent.Paused(eventName = "Evento A", position = 1)), novoEstado.log)
    }

    @Test
    fun `resume tira a marca de pausada sem mexer no resto do estado`() {
        val pausado = pause(estadoInicial)

        val retomado = resume(pausado)

        assertFalse(retomado.paused)
        assertEquals(pausado.log, retomado.log)
        assertEquals(pausado.expectedPosition, retomado.expectedPosition)
    }
}
