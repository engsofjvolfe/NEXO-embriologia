package org.nexo.motor.core.summary

import kotlin.test.Test
import kotlin.test.assertEquals

class SummaryTest {

    @Test
    fun `buildSkipMessage separa respondidas de nao respondidas, EI-PUL-05`() {
        val posicoes = listOf(
            PositionOutcome.Answered(position = 1, confirmationText = "Primeiro momento"),
            PositionOutcome.Skipped(position = 2),
            PositionOutcome.Answered(position = 3, confirmationText = "Terceiro momento"),
        )

        val mensagem = buildSkipMessage(posicoes)

        assertEquals(
            listOf(AnsweredPosition(1, "Primeiro momento"), AnsweredPosition(3, "Terceiro momento")),
            mensagem.answered,
        )
        assertEquals(listOf(2), mensagem.unansweredPositions)
    }

    @Test
    fun `buildSkipMessage nunca revela o conteudo da posicao pulada, so a posicao, EI-PUL-05`() {
        val posicoes = listOf(
            PositionOutcome.Answered(position = 1, confirmationText = "Texto visivel"),
            PositionOutcome.Skipped(position = 2),
        )

        val mensagem = buildSkipMessage(posicoes)

        assertEquals(listOf(2), mensagem.unansweredPositions)
        assertEquals(1, mensagem.answered.size)
    }

    @Test
    fun `buildSkipMessage aceita confirmation_text ausente, ele e opcional`() {
        val posicoes = listOf(PositionOutcome.Answered(position = 1, confirmationText = null))

        val mensagem = buildSkipMessage(posicoes)

        assertEquals(listOf(AnsweredPosition(1, null)), mensagem.answered)
    }

    @Test
    fun `buildSkipMessage com tudo respondido devolve lista de nao respondidas vazia`() {
        val posicoes = listOf(
            PositionOutcome.Answered(position = 1, confirmationText = "A"),
            PositionOutcome.Answered(position = 2, confirmationText = "B"),
        )

        val mensagem = buildSkipMessage(posicoes)

        assertEquals(emptyList(), mensagem.unansweredPositions)
    }

    @Test
    fun `buildChainSkipSynthesis so conta preenchidas e perdidas, sem texto, EI-ENC-03 com pulo`() {
        val desfechos = listOf(
            ChainOutcome.Filled,
            ChainOutcome.Filled,
            ChainOutcome.Lost,
            ChainOutcome.Filled,
            ChainOutcome.Lost,
        )

        val sintese = buildChainSkipSynthesis(desfechos)

        assertEquals(ChainSkipSynthesis(filledCount = 3, lostCount = 2), sintese)
    }

    @Test
    fun `buildChainSkipSynthesis com lista vazia devolve zero nos dois totais`() {
        assertEquals(ChainSkipSynthesis(filledCount = 0, lostCount = 0), buildChainSkipSynthesis(emptyList()))
    }

    @Test
    fun `buildContinuousSynthesis concatena os fragmentos na ordem recebida, EI-RET-04`() {
        val fragmentos = listOf("Como era.", "O que mudou.", "Como ficou.")

        assertEquals("Como era. O que mudou. Como ficou.", buildContinuousSynthesis(fragmentos))
    }

    @Test
    fun `buildContinuousSynthesis nunca usa confirmation_text, so summary_fragment, decisao 1 da ADR 0021`() {
        val fragmentos = listOf("fragmento unico")

        assertEquals("fragmento unico", buildContinuousSynthesis(fragmentos))
    }

    @Test
    fun `buildContinuousSynthesis com um unico fragmento nao acrescenta separador`() {
        assertEquals("unico", buildContinuousSynthesis(listOf("unico")))
    }
}
