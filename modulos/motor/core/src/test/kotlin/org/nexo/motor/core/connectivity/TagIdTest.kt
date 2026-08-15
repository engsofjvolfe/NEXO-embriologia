package org.nexo.motor.core.connectivity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TagIdTest {

    private val tagIdPattern = Regex("^[0-9A-F]+$")

    @Test
    fun `tagIdFromBytes decodifica cada byte como dois digitos hexadecimais maiusculos`() {
        assertEquals("04A1B2C3", tagIdFromBytes(byteArrayOf(0x04, 0xA1.toByte(), 0xB2.toByte(), 0xC3.toByte())))
    }

    @Test
    fun `tagIdFromBytes preenche com zero a esquerda quando o byte e menor que 0x10`() {
        assertEquals("000A", tagIdFromBytes(byteArrayOf(0x00, 0x0A)))
    }

    @Test
    fun `tagIdFromBytes devolve string que bate com o padrao tag_id do PD-IMP-01`() {
        val resultado = tagIdFromBytes(byteArrayOf(0x7A, 0xFF.toByte(), 0x01))
        assertTrue(tagIdPattern.matches(resultado))
    }

    @Test
    fun `tagIdFromBytes de um unico byte`() {
        assertEquals("FF", tagIdFromBytes(byteArrayOf(0xFF.toByte())))
    }
}
