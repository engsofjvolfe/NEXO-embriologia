package org.nexo.motor.core.session

import org.nexo.motor.core.hierarchy.Event
import org.nexo.motor.core.hierarchy.Ordering
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionScopeTest {

    private val evento1 = Event(name = "Evento 1", ordering = Ordering.Ordered(position = 1))
    private val evento2 = Event(name = "Evento 2", ordering = Ordering.Ordered(position = 2))
    private val evento3 = Event(name = "Evento 3", ordering = Ordering.Ordered(position = 3))
    private val eventos = listOf(evento1, evento2, evento3)

    @Test
    fun `sessionScope cobre a sequencia inteira quando from e until sao os extremos, EI-SES-05`() {
        assertEquals(
            listOf(evento1, evento2, evento3),
            sessionScope(eventos, { it.ordering }, from = evento1, until = evento3),
        )
    }

    @Test
    fun `sessionScope cobre so um trecho quando until para antes do fim, exemplo 1-2 do doc de conceito, secao 10`() {
        assertEquals(
            listOf(evento1, evento2),
            sessionScope(eventos, { it.ordering }, from = evento1, until = evento2),
        )
    }

    @Test
    fun `sessionScope comeca no meio do grupo, exemplo 2-3 do doc de conceito, secao 10`() {
        assertEquals(
            listOf(evento2, evento3),
            sessionScope(eventos, { it.ordering }, from = evento2, until = evento3),
        )
    }

    @Test
    fun `sessionScope de um unico evento sozinho`() {
        assertEquals(
            listOf(evento2),
            sessionScope(eventos, { it.ordering }, from = evento2, until = evento2),
        )
    }

    @Test
    fun `sessionScope de item avulso fica limitado a ele mesmo, EI-SES-08`() {
        val avulso = Event(name = "Evento avulso", ordering = Ordering.Standalone)
        val grupo = eventos + avulso

        assertEquals(
            listOf(avulso),
            sessionScope(grupo, { it.ordering }, from = avulso, until = evento3),
        )
    }

    @Test
    fun `sessionScope nunca pula um item do meio, por construcao, EI-SES-06`() {
        val recorte = sessionScope(eventos, { it.ordering }, from = evento1, until = evento3)

        assertEquals(listOf(1, 2, 3), recorte.map { (it.ordering as Ordering.Ordered).position })
    }
}
