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
}
