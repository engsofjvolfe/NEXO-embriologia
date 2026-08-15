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
        val novoEstado = recordAttempt(estadoInicial, tagId = "TAG-1", expectedTagId = "TAG-1", timestamp = 1000L)

        assertEquals(2, novoEstado.expectedPosition)
        assertEquals(
            listOf(SessionEvent.AttemptAccepted(eventName = "Evento A", position = 1, timestamp = 1000L)),
            novoEstado.log,
        )
    }

    @Test
    fun `recordAttempt nao muda a posicao quando a peca nao corresponde, EI-VAL-02`() {
        val novoEstado = recordAttempt(estadoInicial, tagId = "TAG-ERRADA", expectedTagId = "TAG-1", timestamp = 1000L)

        assertEquals(1, novoEstado.expectedPosition)
        assertEquals(
            listOf(SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = 1000L)),
            novoEstado.log,
        )
    }

    @Test
    fun `recordAttempt rejeita qualquer peca diferente da esperada do mesmo jeito, EI-VAL-03`() {
        val primeiraPecaErrada = recordAttempt(estadoInicial, tagId = "TAG-X", expectedTagId = "TAG-1", timestamp = 1000L)
        val segundaPecaErrada = recordAttempt(estadoInicial, tagId = "TAG-Y", expectedTagId = "TAG-1", timestamp = 1000L)

        assertEquals(primeiraPecaErrada.log, segundaPecaErrada.log)
        assertEquals(primeiraPecaErrada.expectedPosition, segundaPecaErrada.expectedPosition)
    }

    @Test
    fun `recordAttempt registra o horario recebido, EI-REG-01`() {
        val novoEstado = recordAttempt(estadoInicial, tagId = "TAG-1", expectedTagId = "TAG-1", timestamp = 12345L)

        assertEquals(12345L, novoEstado.log.single().timestamp)
    }

    @Test
    fun `skipPosition marca a posicao como perdida e avanca, EI-PUL-04`() {
        val novoEstado = skipPosition(estadoInicial, timestamp = 1000L)

        assertEquals(2, novoEstado.expectedPosition)
        assertEquals(
            listOf(SessionEvent.PositionSkipped(eventName = "Evento A", position = 1, timestamp = 1000L)),
            novoEstado.log,
        )
    }

    @Test
    fun `hintAvailable libera a dica ao atingir o limiar configurado, EI-DIC-01`() {
        val comDoisErros = estadoInicial.copy(
            log = listOf(
                SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = 1000L),
                SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = 1001L),
            ),
        )

        assertFalse(hintAvailable(comDoisErros, hintThreshold = 3))
        assertTrue(hintAvailable(comDoisErros, hintThreshold = 2))
    }

    @Test
    fun `consecutiveAttempts ignora dica usada no meio da sequencia de erros`() {
        val log = listOf(
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = 1000L),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = 1001L),
            SessionEvent.HintUsed(eventName = "Evento A", position = 1, timestamp = 1002L),
            SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = 1003L),
        )

        assertEquals(3, consecutiveAttempts(log, eventName = "Evento A", position = 1))
    }

    @Test
    fun `useHint registra a dica usada sem mudar a posicao esperada, EI-DIC-02`() {
        val novoEstado = useHint(estadoInicial, timestamp = 1000L)

        assertEquals(1, novoEstado.expectedPosition)
        assertEquals(
            listOf(SessionEvent.HintUsed(eventName = "Evento A", position = 1, timestamp = 1000L)),
            novoEstado.log,
        )
    }

    @Test
    fun `studySuggestionAvailable so libera depois da dica ja ter sido usada, EI-DIC-03`() {
        val semDicaUsada = estadoInicial.copy(
            log = List(5) { i -> SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = i.toLong()) },
        )
        val comDicaUsada = estadoInicial.copy(
            log = listOf(SessionEvent.HintUsed(eventName = "Evento A", position = 1, timestamp = 0L)) +
                List(5) { i -> SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = i.toLong()) },
        )

        assertFalse(studySuggestionAvailable(semDicaUsada, studyThreshold = 5))
        assertTrue(studySuggestionAvailable(comDicaUsada, studyThreshold = 5))
    }

    @Test
    fun `showStudySuggestion registra a sugestao exibida sem mudar a posicao esperada, EI-REG-01`() {
        val novoEstado = showStudySuggestion(estadoInicial, timestamp = 1000L)

        assertEquals(1, novoEstado.expectedPosition)
        assertEquals(
            listOf(SessionEvent.StudySuggestionShown(eventName = "Evento A", position = 1, timestamp = 1000L)),
            novoEstado.log,
        )
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
    fun `pause marca a sessao como pausada e registra o gatilho explicito, EI-PAU-01, RF-REG-01`() {
        val novoEstado = pause(estadoInicial, timestamp = 1000L)

        assertTrue(novoEstado.paused)
        assertEquals(
            listOf(SessionEvent.Paused(eventName = "Evento A", position = 1, timestamp = 1000L)),
            novoEstado.log,
        )
    }

    @Test
    fun `goIdle marca a sessao como pausada e registra o gatilho de ociosidade, distinto de pause, EI-PAU-01, RF-REG-01`() {
        val novoEstado = goIdle(estadoInicial, timestamp = 1000L)

        assertTrue(novoEstado.paused)
        assertEquals(
            listOf(SessionEvent.WentIdle(eventName = "Evento A", position = 1, timestamp = 1000L)),
            novoEstado.log,
        )
    }

    @Test
    fun `pause e goIdle produzem o mesmo efeito de interrupcao, mas registros distintos, Conceito secao 12`() {
        val porPausa = pause(estadoInicial, timestamp = 1000L)
        val porOciosidade = goIdle(estadoInicial, timestamp = 1000L)

        assertEquals(porPausa.paused, porOciosidade.paused)
        assertEquals(porPausa.expectedPosition, porOciosidade.expectedPosition)
        assertTrue(porPausa.log.single() is SessionEvent.Paused)
        assertTrue(porOciosidade.log.single() is SessionEvent.WentIdle)
    }

    @Test
    fun `resume tira a marca de pausada sem mexer no resto do estado`() {
        val pausado = pause(estadoInicial, timestamp = 1000L)

        val retomado = resume(pausado)

        assertFalse(retomado.paused)
        assertEquals(pausado.log, retomado.log)
        assertEquals(pausado.expectedPosition, retomado.expectedPosition)
    }
}
