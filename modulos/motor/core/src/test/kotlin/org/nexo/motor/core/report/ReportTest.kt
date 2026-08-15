package org.nexo.motor.core.report

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.nexo.motor.core.session.SessionEvent

class ReportTest {

    private val configuracao = SessionConfiguration(
        eventNames = listOf("Evento A"),
        startingPosition = 1,
        idleThresholdMillis = 60_000L,
        events = listOf(EventConfiguration(eventName = "Evento A", skipEnabled = true, hintThreshold = 3, studyThreshold = 5)),
    )

    private val registro = listOf(
        SessionEvent.AttemptRejected(eventName = "Evento A", position = 1, timestamp = 1000L),
        SessionEvent.AttemptAccepted(eventName = "Evento A", position = 1, timestamp = 2000L),
    )

    @Test
    fun `sessionEventTypeName cobre as sete variantes de SessionEvent, EI-REG-01`() {
        assertEquals("tentativa aceita", sessionEventTypeName(SessionEvent.AttemptAccepted("E", 1, 0L)))
        assertEquals("tentativa rejeitada", sessionEventTypeName(SessionEvent.AttemptRejected("E", 1, 0L)))
        assertEquals("dica usada", sessionEventTypeName(SessionEvent.HintUsed("E", 1, 0L)))
        assertEquals("sugestão de estudo exibida", sessionEventTypeName(SessionEvent.StudySuggestionShown("E", 1, 0L)))
        assertEquals("posição pulada", sessionEventTypeName(SessionEvent.PositionSkipped("E", 1, 0L)))
        assertEquals("pausa", sessionEventTypeName(SessionEvent.Paused("E", 1, 0L)))
        assertEquals("ociosidade", sessionEventTypeName(SessionEvent.WentIdle("E", 1, 0L)))
    }

    @Test
    fun `buildReportCsv inclui a configuracao usada, EI-REG-01`() {
        val csv = buildReportCsv(configuracao, registro)

        assertTrue(csv.contains("Evento A"))
        assertTrue(csv.contains("ponto de início,1"))
        assertTrue(csv.contains("tempo de ociosidade (ms),60000"))
    }

    @Test
    fun `buildReportCsv inclui cada acontecimento com posicao, evento e horario, EI-REG-01`() {
        val csv = buildReportCsv(configuracao, registro)

        assertTrue(csv.contains("tentativa rejeitada,Evento A,1,1000"))
        assertTrue(csv.contains("tentativa aceita,Evento A,1,2000"))
    }

    @Test
    fun `buildReportCsv escapa campo com virgula, RFC 4180`() {
        val log = listOf(SessionEvent.AttemptAccepted(eventName = "Evento, com virgula", position = 1, timestamp = 0L))

        val csv = buildReportCsv(configuracao, log)

        assertTrue(csv.contains("\"Evento, com virgula\""))
    }

    @Test
    fun `buildReportCsv escapa campo com aspas duplicando-as, RFC 4180`() {
        val log = listOf(SessionEvent.AttemptAccepted(eventName = "Evento \"citado\"", position = 1, timestamp = 0L))

        val csv = buildReportCsv(configuracao, log)

        assertTrue(csv.contains("\"Evento \"\"citado\"\"\""))
    }

    @Test
    fun `buildReportCsv nao escapa campo comum, sem virgula nem aspas`() {
        val csv = buildReportCsv(configuracao, registro)

        assertTrue(csv.contains("tentativa aceita,Evento A,1,2000"))
    }

    @Test
    fun `buildReportCsv usa CRLF como terminador de linha, RFC 4180`() {
        val csv = buildReportCsv(configuracao, registro)

        assertTrue(csv.contains("\r\n"))
    }

    @Test
    fun `buildReportPdfLines inclui a configuracao e o historico, EI-REG-01`() {
        val linhas = buildReportPdfLines(configuracao, registro)

        assertTrue(linhas.any { it.contains("Evento A") && it.contains("pular: sim") })
        assertTrue(linhas.any { it.contains("tentativa aceita") && it.contains("posição 1") && it.contains("horário 2000") })
    }

    @Test
    fun `buildReportPdfLines com registro vazio ainda mostra a configuracao, sem quebrar`() {
        val linhas = buildReportPdfLines(configuracao, emptyList())

        assertTrue(linhas.any { it.contains("Ponto de início: posição 1") })
        assertTrue(linhas.last() == "Histórico:")
    }
}
