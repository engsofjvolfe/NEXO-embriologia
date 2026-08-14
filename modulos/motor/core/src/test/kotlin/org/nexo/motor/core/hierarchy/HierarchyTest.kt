package org.nexo.motor.core.hierarchy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HierarchyTest {

    @Test
    fun `Ordering Ordered carrega a posicao`() {
        val ordering = Ordering.Ordered(position = 2)
        assertEquals(2, ordering.position)
    }

    @Test
    fun `Ordering Standalone nao carrega posicao, decisao 1 da ADR 0007`() {
        val ordering: Ordering = Ordering.Standalone
        assertIs<Ordering.Standalone>(ordering)
    }

    @Test
    fun `when sobre Ordering trata os dois casos sem else, decisao 1 da ADR 0007`() {
        fun positionOf(ordering: Ordering): Int? = when (ordering) {
            is Ordering.Ordered -> ordering.position
            Ordering.Standalone -> null
        }

        assertEquals(3, positionOf(Ordering.Ordered(position = 3)))
        assertEquals(null, positionOf(Ordering.Standalone))
    }

    @Test
    fun `Event carrega nome e ordenacao`() {
        val event = Event(name = "Fecundacao", ordering = Ordering.Ordered(position = 1))
        assertEquals("Fecundacao", event.name)
        assertEquals(Ordering.Ordered(position = 1), event.ordering)
    }

    @Test
    fun `Theme carrega os eventos na ordem em que foram passados`() {
        val eventos = listOf(
            Event(name = "Fecundacao", ordering = Ordering.Ordered(position = 1)),
            Event(name = "Clivagem", ordering = Ordering.Ordered(position = 2)),
        )
        val theme = Theme(name = "Desenvolvimento embrionario", ordering = Ordering.Standalone, events = eventos)

        assertEquals(eventos, theme.events)
    }

    @Test
    fun `Instance carrega os temas na ordem em que foram passados`() {
        val temas = listOf(
            Theme(
                name = "Tema A",
                ordering = Ordering.Standalone,
                events = listOf(Event(name = "Evento A1", ordering = Ordering.Standalone)),
            ),
        )
        val instance = Instance(name = "Instancia de teste", themes = temas)

        assertEquals(temas, instance.themes)
    }
}
