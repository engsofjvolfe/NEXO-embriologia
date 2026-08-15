package org.nexo.motor.core.content

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.nexo.motor.core.hierarchy.HierarchyViolation
import org.nexo.motor.core.hierarchy.Ordering

class ContentImportTest {

    private fun frame(tagId: String, image: String = "img.png") =
        """{"tag_id": "$tagId", "image": "$image"}"""

    private fun event(
        name: String,
        ordering: String = "standalone",
        position: Int? = null,
        hintEnabled: Boolean = false,
        hintContent: String? = null,
        frames: List<String> = listOf(frame("01")),
    ): String {
        val positionField = position?.let { "\"position\": $it," } ?: ""
        val hintContentField = hintContent?.let { "\"hint_content\": \"$it\"," } ?: ""
        return """
            {
              "name": "$name",
              "ordering": "$ordering",
              $positionField
              "zero_mark": { "image": "zero.png" },
              "hint_enabled": $hintEnabled,
              $hintContentField
              "frames": [${frames.joinToString(",")}]
            }
        """.trimIndent()
    }

    private fun theme(
        name: String,
        ordering: String = "standalone",
        position: Int? = null,
        events: List<String> = listOf(event("Evento 1")),
    ): String {
        val positionField = position?.let { "\"position\": $it," } ?: ""
        return """
            {
              "name": "$name",
              "ordering": "$ordering",
              $positionField
              "events": [${events.joinToString(",")}]
            }
        """.trimIndent()
    }

    private fun manifest(themes: List<String>, schemaVersion: String = "1.0.0"): String =
        """
            {
              "schema_version": "$schemaVersion",
              "instance": {
                "name": "Instancia",
                "retention_period": "P6M",
                "themes": [${themes.joinToString(",")}]
              }
            }
        """.trimIndent()

    @Test
    fun `importContentPackage devolve instancia valida quando o manifesto bate com o contrato`() {
        val result = importContentPackage(manifest(listOf(theme("Tema A"))))

        assertTrue(result.violations.isEmpty())
        val instance = result.instance!!
        assertEquals("Instancia", instance.name)
        assertEquals("P6M", instance.retentionPeriod)
        assertEquals(1, instance.themes.size)
        assertEquals("Tema A", instance.themes.first().name)
        assertEquals(Ordering.Standalone, instance.themes.first().ordering)
        assertEquals("01", instance.themes.first().events.first().frames.first().tagId)
    }

    @Test
    fun `importContentPackage aceita eventos e temas com ordering ordered e posicao contigua`() {
        val eventos = listOf(
            event("Evento 1", ordering = "ordered", position = 1),
            event("Evento 2", ordering = "ordered", position = 2, frames = listOf(frame("02"))),
        )
        val result = importContentPackage(manifest(listOf(theme("Tema A", ordering = "ordered", position = 1, events = eventos))))

        assertTrue(result.violations.isEmpty())
        assertEquals(Ordering.Ordered(1), result.instance!!.themes.first().ordering)
        assertEquals(
            listOf(Ordering.Ordered(1), Ordering.Ordered(2)),
            result.instance!!.themes.first().events.map { it.ordering },
        )
    }

    @Test
    fun `importContentPackage aceita eventos com posicao completa mesmo escritos fora de ordem no arquivo`() {
        val eventos = listOf(
            event("Evento 1", ordering = "ordered", position = 1, frames = listOf(frame("01"))),
            event("Evento 3", ordering = "ordered", position = 3, frames = listOf(frame("03"))),
            event("Evento 2", ordering = "ordered", position = 2, frames = listOf(frame("02"))),
        )

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = eventos))))

        assertTrue(result.violations.isEmpty())
        assertEquals(3, result.instance!!.themes.first().events.size)
    }

    @Test
    fun `importContentPackage rejeita JSON invalido`() {
        val result = importContentPackage("{ nao e um json valido")

        assertNull(result.instance)
        assertTrue(result.violations.single() is ContentViolation.InvalidManifest)
    }

    @Test
    fun `importContentPackage rejeita schema_version incompativel`() {
        val result = importContentPackage(manifest(listOf(theme("Tema A")), schemaVersion = "0.1.0"))

        assertNull(result.instance)
        val violation = result.violations.single() as ContentViolation.InvalidManifest
        assertTrue(violation.reason.contains("schema_version"))
    }

    @Test
    fun `importContentPackage rejeita instance ausente`() {
        val result = importContentPackage("""{"schema_version": "1.0.0"}""")

        assertNull(result.instance)
        val violation = result.violations.single() as ContentViolation.InvalidManifest
        assertTrue(violation.reason.contains("instance"))
    }

    @Test
    fun `importContentPackage rejeita retention_period fora do padrao ISO 8601`() {
        val json = """
            {
              "schema_version": "1.0.0",
              "instance": {
                "name": "Instancia",
                "retention_period": "6 meses",
                "themes": [${theme("Tema A")}]
              }
            }
        """.trimIndent()

        val result = importContentPackage(json)

        assertNull(result.instance)
        val violation = result.violations.single() as ContentViolation.InvalidManifest
        assertTrue(violation.reason.contains("retention_period"))
    }

    @Test
    fun `importContentPackage rejeita name que nao e string, mesmo com o mesmo texto`() {
        val json = """
            {
              "schema_version": "1.0.0",
              "instance": {
                "name": 123,
                "retention_period": "P6M",
                "themes": [${theme("Tema A")}]
              }
            }
        """.trimIndent()

        val result = importContentPackage(json)

        assertNull(result.instance)
        val violation = result.violations.single() as ContentViolation.InvalidManifest
        assertTrue(violation.reason.contains("name"))
    }

    @Test
    fun `importContentPackage recusa o pacote inteiro quando um tema vem invalido, mesmo com outro tema correto`() {
        val temaSemOrdering = """{"name": "Tema invalido", "events": [${event("Evento 1")}]}"""

        val result = importContentPackage(manifest(listOf(temaSemOrdering, theme("Tema valido"))))

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.InvalidTheme && it.reason.contains("ordering") })
    }

    @Test
    fun `importContentPackage recusa o pacote inteiro quando um evento vem invalido, mesmo com outro evento correto no mesmo tema`() {
        val eventoSemZeroMark = """
            {"name": "Evento invalido", "ordering": "standalone", "hint_enabled": false, "frames": [${frame("01")}]}
        """.trimIndent()

        val result = importContentPackage(
            manifest(listOf(theme("Tema A", events = listOf(eventoSemZeroMark, event("Evento valido", frames = listOf(frame("02"))))))),
        )

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.InvalidEvent && it.reason.contains("zero_mark") })
    }

    @Test
    fun `importContentPackage recusa o pacote inteiro quando um fotograma vem malformado, mesmo com outro fotograma correto no mesmo evento`() {
        val fotogramaSemImage = """{"tag_id": "01"}"""
        val eventoComFotogramaRuim = event("Evento com fotograma ruim", frames = listOf(fotogramaSemImage, frame("02")))

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = listOf(eventoComFotogramaRuim)))))

        assertNull(result.instance)
        assertTrue(
            result.violations.any { it is ContentViolation.InvalidFrame && it.reason.contains("image") },
        )
    }

    @Test
    fun `importContentPackage exige position quando ordering e ordered`() {
        val eventoSemPosition = event("Evento 1", ordering = "ordered")

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = listOf(eventoSemPosition)))))

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.InvalidEvent && it.reason.contains("position") })
    }

    @Test
    fun `importContentPackage proibe position quando ordering e standalone`() {
        val eventoComPositionIndevida = event("Evento 1", ordering = "standalone", position = 1)

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = listOf(eventoComPositionIndevida)))))

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.InvalidEvent && it.reason.contains("position") })
    }

    @Test
    fun `importContentPackage exige hint_content quando hint_enabled e true`() {
        val eventoSemHintContent = event("Evento 1", hintEnabled = true)

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = listOf(eventoSemHintContent)))))

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.InvalidEvent && it.reason.contains("hint_content") })
    }

    @Test
    fun `importContentPackage proibe hint_content quando hint_enabled e false`() {
        val eventoComHintContentIndevido = event("Evento 1", hintEnabled = false, hintContent = "dica")

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = listOf(eventoComHintContentIndevido)))))

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.InvalidEvent && it.reason.contains("hint_content") })
    }

    @Test
    fun `importContentPackage rejeita tag_id fora do padrao hexadecimal`() {
        val eventoComTagIdInvalido = event("Evento 1", frames = listOf(frame("nao-hex")))

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = listOf(eventoComTagIdInvalido)))))

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.InvalidFrame && it.reason.contains("tag_id") })
    }

    @Test
    fun `importContentPackage recusa o pacote inteiro quando ha tag_id duplicado em todo o pacote`() {
        val eventoA = event("Evento A", frames = listOf(frame("01")))
        val eventoB = event("Evento B", frames = listOf(frame("01")))

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = listOf(eventoA, eventoB)))))

        assertNull(result.instance)
        assertTrue(result.violations.any { it is ContentViolation.DuplicateTagId && it.tagId == "01" })
    }

    @Test
    fun `importContentPackage recusa o pacote inteiro quando dois temas tem nome repetido, mesmo com conteudo correto em cada um`() {
        val temaA = theme("Tema repetido", events = listOf(event("Evento 1", frames = listOf(frame("01")))))
        val temaB = theme("Tema repetido", events = listOf(event("Evento 1", frames = listOf(frame("02")))))

        val result = importContentPackage(manifest(listOf(temaA, temaB)))

        assertNull(result.instance)
        assertTrue(
            result.violations.any {
                it is ContentViolation.Hierarchy && it.violation is HierarchyViolation.DuplicateThemeName
            },
        )
    }

    @Test
    fun `importContentPackage recusa o pacote inteiro quando a posicao de eventos com ordem pula um numero, mesmo com todo o resto correto`() {
        val eventos = listOf(
            event("Evento 1", ordering = "ordered", position = 1, frames = listOf(frame("01"))),
            event("Evento 3", ordering = "ordered", position = 3, frames = listOf(frame("02"))),
            event("Evento avulso", ordering = "standalone", frames = listOf(frame("03"))),
        )

        val result = importContentPackage(manifest(listOf(theme("Tema A", events = eventos))))

        assertNull(result.instance)
        assertTrue(
            result.violations.any {
                it is ContentViolation.Hierarchy && it.violation is HierarchyViolation.NonContiguousEventPositions
            },
        )
    }

    @Test
    fun `importContentPackage acumula mais de uma violacao independente na mesma passada, varredura completa da decisao 3 da ADR 0013`() {
        val temaA = theme("Tema repetido", events = listOf(event("Evento 1", frames = listOf(frame("01")))))
        val temaB = theme("Tema repetido", events = listOf(event("Evento 2", frames = listOf(frame("01")))))

        val result = importContentPackage(manifest(listOf(temaA, temaB)))

        assertNull(result.instance)
        assertTrue(
            result.violations.any {
                it is ContentViolation.Hierarchy && it.violation is HierarchyViolation.DuplicateThemeName
            },
        )
        assertTrue(result.violations.any { it is ContentViolation.DuplicateTagId && it.tagId == "01" })
    }
}
