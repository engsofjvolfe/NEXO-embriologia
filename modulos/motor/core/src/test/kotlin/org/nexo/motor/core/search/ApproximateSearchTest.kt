package org.nexo.motor.core.search

import kotlin.test.Test
import kotlin.test.assertEquals

class ApproximateSearchTest {

    @Test
    fun `levenshteinDistance entre strings identicas e zero`() {
        assertEquals(0, levenshteinDistance("evento", "evento"))
    }

    @Test
    fun `levenshteinDistance kitten sitting e tres, exemplo do Google Cloud`() {
        assertEquals(3, levenshteinDistance("kitten", "sitting"))
    }

    @Test
    fun `levenshteinDistance survey surgery e dois, exemplo do Navarro 2001`() {
        assertEquals(2, levenshteinDistance("survey", "surgery"))
    }

    @Test
    fun `approximateSearchThreshold segue os exemplos numericos do PD-NAV-02`() {
        assertEquals(1, approximateSearchThreshold(4))
        assertEquals(1, approximateSearchThreshold(5))
        assertEquals(2, approximateSearchThreshold(10))
        assertEquals(2, approximateSearchThreshold(14))
    }

    @Test
    fun `approximateSearchThreshold nunca fica abaixo de um`() {
        assertEquals(1, approximateSearchThreshold(1))
        assertEquals(1, approximateSearchThreshold(0))
    }

    @Test
    fun `normalize ignora maiuscula e acento, decisao 1 da ADR 0004`() {
        assertEquals(normalize("evento"), normalize("EvEnto"))
        assertEquals(normalize("evento"), normalize("Évento"))
    }

    @Test
    fun `substringLevenshteinDistance acha trecho exato dentro de nome maior`() {
        assertEquals(0, substringLevenshteinDistance("vent", normalize("Evento X")))
    }

    @Test
    fun `approximateSearch ignora maiuscula e acento ao buscar`() {
        val itens = listOf("Évento")
        assertEquals(itens, approximateSearch(itens, "EVENTO", nameOf = { it }))
    }

    @Test
    fun `approximateSearch preserva a ordem de entrada em caso de empate, decisao 2 da ADR 0004`() {
        val itens = listOf("abx", "axc")
        assertEquals(itens, approximateSearch(itens, "abc", nameOf = { it }))

        val itensInvertidos = listOf("axc", "abx")
        assertEquals(itensInvertidos, approximateSearch(itensInvertidos, "abc", nameOf = { it }))
    }

    @Test
    fun `approximateSearch traz resultado de nome inteiro antes do resultado por trecho, decisao 3 da ADR 0004`() {
        val nomeSoTrecho = "abcextra"
        val nomeInteiro = "abd"
        val itens = listOf(nomeSoTrecho, nomeInteiro)

        assertEquals(listOf(nomeInteiro, nomeSoTrecho), approximateSearch(itens, "abc", nameOf = { it }))
    }

    @Test
    fun `approximateSearch com termo vazio devolve a lista sem reordenar, decisao da ADR 0014`() {
        val itens = listOf("Zebra", "Abacate", "Manga")
        assertEquals(itens, approximateSearch(itens, "   ", nameOf = { it }))
    }

    @Test
    fun `approximateSearch exclui item cuja distancia, inteira e por trecho, excede o limiar do PD-NAV-02`() {
        val itens = listOf("gato", "elefante")
        assertEquals(listOf("gato"), approximateSearch(itens, "gato", nameOf = { it }))
    }
}
