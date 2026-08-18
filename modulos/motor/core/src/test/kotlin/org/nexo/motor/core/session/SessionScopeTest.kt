package org.nexo.motor.core.session

import org.nexo.motor.core.hierarchy.Event
import org.nexo.motor.core.hierarchy.Ordering
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Fixture só de teste, pra sessionEventNames -- nunca reaproveita tipo de content nem presume nada
 * sobre a forma real de hierarchy.Theme, mantendo a função sob teste genérica de verdade (decisions/0028).
 */
private data class TestTheme(val name: String, val ordering: Ordering, val events: List<Event>)

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

    private val a1 = Event(name = "A1", ordering = Ordering.Ordered(1))
    private val a2 = Event(name = "A2", ordering = Ordering.Ordered(2))
    private val a3 = Event(name = "A3", ordering = Ordering.Ordered(3))
    private val temaA = TestTheme(name = "Tema A", ordering = Ordering.Ordered(1), events = listOf(a1, a2, a3))

    private val b1 = Event(name = "B1", ordering = Ordering.Ordered(1))
    private val b2 = Event(name = "B2", ordering = Ordering.Ordered(2))
    private val b3 = Event(name = "B3", ordering = Ordering.Ordered(3))
    private val bAvulso = Event(name = "B avulso", ordering = Ordering.Standalone)
    private val temaB = TestTheme(name = "Tema B", ordering = Ordering.Ordered(2), events = listOf(b1, b2, bAvulso, b3))

    private val c1 = Event(name = "C1", ordering = Ordering.Ordered(1))
    private val c2 = Event(name = "C2", ordering = Ordering.Ordered(2))
    private val c3 = Event(name = "C3", ordering = Ordering.Ordered(3))
    private val temaC = TestTheme(name = "Tema C", ordering = Ordering.Ordered(3), events = listOf(c1, c2, c3))

    private val temas = listOf(temaA, temaB, temaC)

    private fun sessionEventNames(fromTheme: TestTheme, fromEvent: Event, untilTheme: TestTheme, untilEvent: Event) =
        sessionEventNames(
            themes = temas,
            themeOrdering = { it.ordering },
            eventsOf = { it.events },
            eventOrdering = { it.ordering },
            eventName = { it.name },
            fromTheme = fromTheme,
            fromEvent = fromEvent,
            untilTheme = untilTheme,
            untilEvent = untilEvent,
        )

    @Test
    fun `sessionEventNames dentro de um unico tema se comporta como sessionScope, EI-SES-06`() {
        assertEquals(
            listOf("A2", "A3"),
            sessionEventNames(fromTheme = temaA, fromEvent = a2, untilTheme = temaA, untilEvent = a3),
        )
    }

    @Test
    fun `sessionEventNames atravessando dois temas, sem tema no meio -- o ultimo fica parcial, EI-SES-07`() {
        assertEquals(
            listOf("A2", "A3", "B1", "B2"),
            sessionEventNames(fromTheme = temaA, fromEvent = a2, untilTheme = temaB, untilEvent = b2),
        )
    }

    @Test
    fun `sessionEventNames atravessando tres temas -- o do meio entra inteiro, avulso dele fica de fora, EI-SES-07`() {
        assertEquals(
            listOf("A2", "A3", "B1", "B2", "B3", "C1", "C2"),
            sessionEventNames(fromTheme = temaA, fromEvent = a2, untilTheme = temaC, untilEvent = c2),
        )
    }

    @Test
    fun `sessionEventNames cobrindo a sessao inteira, do primeiro evento do primeiro tema ao ultimo do ultimo`() {
        assertEquals(
            listOf("A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"),
            sessionEventNames(fromTheme = temaA, fromEvent = a1, untilTheme = temaC, untilEvent = c3),
        )
    }
}
