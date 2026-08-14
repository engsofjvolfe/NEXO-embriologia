package org.nexo.motor.core.hierarchy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HierarchyValidationTest {

    private fun evento(nome: String) = Event(name = nome, ordering = Ordering.Standalone)

    @Test
    fun `validate devolve lista vazia quando nao ha nome repetido`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(name = "Tema A", ordering = Ordering.Standalone, events = listOf(evento("Evento 1"))),
                Theme(name = "Tema B", ordering = Ordering.Standalone, events = listOf(evento("Evento 1"))),
            ),
        )

        assertTrue(validate(instance).isEmpty())
    }

    @Test
    fun `validate acha tema com nome repetido na mesma instancia`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(name = "Tema A", ordering = Ordering.Standalone, events = listOf(evento("Evento 1"))),
                Theme(name = "Tema A", ordering = Ordering.Standalone, events = listOf(evento("Evento 2"))),
            ),
        )

        assertEquals(
            listOf(HierarchyViolation.DuplicateThemeName(instanceName = "Instancia", name = "Tema A")),
            validate(instance),
        )
    }

    @Test
    fun `validate acha evento com nome repetido dentro do mesmo tema`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(
                    name = "Tema A",
                    ordering = Ordering.Standalone,
                    events = listOf(evento("Evento 1"), evento("Evento 1")),
                ),
            ),
        )

        assertEquals(
            listOf(HierarchyViolation.DuplicateEventName(instanceName = "Instancia", themeName = "Tema A", name = "Evento 1")),
            validate(instance),
        )
    }

    @Test
    fun `validate nao acusa evento com mesmo nome em temas diferentes, EI-HIE-01 e por tema`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(name = "Tema A", ordering = Ordering.Standalone, events = listOf(evento("Evento 1"))),
                Theme(name = "Tema B", ordering = Ordering.Standalone, events = listOf(evento("Evento 1"))),
            ),
        )

        assertTrue(validate(instance).isEmpty())
    }

    @Test
    fun `validate junta todas as violacoes numa lista so, decisao 2 da ADR 0007`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(
                    name = "Tema A",
                    ordering = Ordering.Standalone,
                    events = listOf(evento("Evento 1"), evento("Evento 1")),
                ),
                Theme(name = "Tema A", ordering = Ordering.Standalone, events = listOf(evento("Evento 2"))),
            ),
        )

        val violacoes = validate(instance)

        assertEquals(2, violacoes.size)
        assertTrue(HierarchyViolation.DuplicateThemeName(instanceName = "Instancia", name = "Tema A") in violacoes)
        assertTrue(
            HierarchyViolation.DuplicateEventName(instanceName = "Instancia", themeName = "Tema A", name = "Evento 1") in violacoes,
        )
    }

    @Test
    fun `validate devolve lista vazia quando as posicoes dos eventos com ordem sao contiguas`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(
                    name = "Tema A",
                    ordering = Ordering.Standalone,
                    events = listOf(
                        Event(name = "Evento 1", ordering = Ordering.Ordered(position = 1)),
                        Event(name = "Evento 2", ordering = Ordering.Ordered(position = 2)),
                        Event(name = "Evento 3", ordering = Ordering.Ordered(position = 3)),
                    ),
                ),
            ),
        )

        assertTrue(validate(instance).isEmpty())
    }

    @Test
    fun `validate acha buraco na posicao dos eventos com ordem, achado 2026-08-14`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(
                    name = "Tema A",
                    ordering = Ordering.Standalone,
                    events = listOf(
                        Event(name = "Evento 1", ordering = Ordering.Ordered(position = 1)),
                        Event(name = "Evento 3", ordering = Ordering.Ordered(position = 3)),
                    ),
                ),
            ),
        )

        assertEquals(
            listOf(
                HierarchyViolation.NonContiguousEventPositions(
                    instanceName = "Instancia",
                    themeName = "Tema A",
                    positions = listOf(1, 3),
                ),
            ),
            validate(instance),
        )
    }

    @Test
    fun `validate acha posicao duplicada entre eventos com ordem, achado 2026-08-14`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(
                    name = "Tema A",
                    ordering = Ordering.Standalone,
                    events = listOf(
                        Event(name = "Evento 1", ordering = Ordering.Ordered(position = 1)),
                        Event(name = "Evento 2", ordering = Ordering.Ordered(position = 1)),
                    ),
                ),
            ),
        )

        assertEquals(
            listOf(
                HierarchyViolation.NonContiguousEventPositions(
                    instanceName = "Instancia",
                    themeName = "Tema A",
                    positions = listOf(1, 1),
                ),
            ),
            validate(instance),
        )
    }

    @Test
    fun `validate acha buraco na posicao dos temas com ordem, achado 2026-08-14`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(name = "Tema A", ordering = Ordering.Ordered(position = 1), events = listOf(evento("Evento 1"))),
                Theme(name = "Tema B", ordering = Ordering.Ordered(position = 3), events = listOf(evento("Evento 1"))),
            ),
        )

        assertEquals(
            listOf(
                HierarchyViolation.NonContiguousThemePositions(instanceName = "Instancia", positions = listOf(1, 3)),
            ),
            validate(instance),
        )
    }

    @Test
    fun `validate nao acusa buraco quando os itens sao avulsos, sem posicao`() {
        val instance = Instance(
            name = "Instancia",
            themes = listOf(
                Theme(name = "Tema A", ordering = Ordering.Standalone, events = listOf(evento("Evento 1"))),
                Theme(name = "Tema B", ordering = Ordering.Standalone, events = listOf(evento("Evento 1"))),
            ),
        )

        assertTrue(validate(instance).isEmpty())
    }
}
