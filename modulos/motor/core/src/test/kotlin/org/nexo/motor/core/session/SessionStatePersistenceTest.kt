package org.nexo.motor.core.session

import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionStatePersistenceTest {

    private val arquivoTemporario = createTempFile(prefix = "sessao-", suffix = ".json")

    @AfterTest
    fun limpar() {
        arquivoTemporario.deleteIfExists()
    }

    @Test
    fun `saveSessionState seguido de loadSessionState devolve um estado igual, decisao 1 da ADR 0010`() {
        val estado = SessionState(
            expectedEventName = "Evento A",
            expectedPosition = 3,
            paused = false,
            log = listOf(
                SessionEvent.AttemptAccepted(eventName = "Evento A", position = 1),
                SessionEvent.AttemptRejected(eventName = "Evento A", position = 2),
                SessionEvent.HintUsed(eventName = "Evento A", position = 2),
                SessionEvent.AttemptAccepted(eventName = "Evento A", position = 2),
            ),
        )

        saveSessionState(estado, arquivoTemporario.toFile())
        val estadoLido = loadSessionState(arquivoTemporario.toFile())

        assertEquals(estado, estadoLido)
    }

    @Test
    fun `loadSessionState devolve nulo quando o arquivo nao existe`() {
        arquivoTemporario.deleteIfExists()

        assertNull(loadSessionState(arquivoTemporario.toFile()))
    }

    @Test
    fun `saveSessionState grava um arquivo legivel como JSON puro, DA-IMP-06`() {
        val estado = SessionState(
            expectedEventName = "Evento A",
            expectedPosition = 1,
            paused = true,
            log = emptyList(),
        )

        saveSessionState(estado, arquivoTemporario.toFile())
        val conteudo = arquivoTemporario.toFile().readText()

        assertEquals(true, conteudo.contains("\"expectedEventName\""))
        assertEquals(true, conteudo.contains("\"paused\": true"))
    }

    @Test
    fun `deleteSessionState apaga o estado retomavel, EI-PAU-03`() {
        val estado = SessionState(
            expectedEventName = "Evento A",
            expectedPosition = 1,
            paused = true,
            log = emptyList(),
        )
        saveSessionState(estado, arquivoTemporario.toFile())

        deleteSessionState(arquivoTemporario.toFile())

        assertNull(loadSessionState(arquivoTemporario.toFile()))
    }
}
