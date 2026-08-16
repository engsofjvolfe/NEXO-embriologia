package org.nexo.motor.core.session

import kotlin.test.Test
import kotlin.test.assertEquals

class SessionReferenceTest {

    @Test
    fun `referenceImage usa o fotograma anterior quando o ponto de inicio nao e a posicao 1, EI-SES-04 caso 1`() {
        val referencia = referenceImage(
            startingPosition = 3,
            isFirstEventOfSession = true,
            previousFrameImage = "posicao-2.png",
            zeroMarkImage = "marco-zero.png",
            lastFilledImageOfPreviousEvent = null,
        )

        assertEquals("posicao-2.png", referencia)
    }

    @Test
    fun `referenceImage usa o marco zero quando o evento e o primeiro da sessao e comeca na posicao 1, EI-SES-04 caso 2`() {
        val referencia = referenceImage(
            startingPosition = 1,
            isFirstEventOfSession = true,
            previousFrameImage = null,
            zeroMarkImage = "marco-zero.png",
            lastFilledImageOfPreviousEvent = null,
        )

        assertEquals("marco-zero.png", referencia)
    }

    @Test
    fun `referenceImage usa a ultima peca preenchida do evento anterior quando o evento e encadeado, EI-SES-04 caso 3`() {
        val referencia = referenceImage(
            startingPosition = 1,
            isFirstEventOfSession = false,
            previousFrameImage = null,
            zeroMarkImage = "marco-zero-evento-atual.png",
            lastFilledImageOfPreviousEvent = "ultima-peca-evento-anterior.png",
        )

        assertEquals("ultima-peca-evento-anterior.png", referencia)
    }

    @Test
    fun `referenceImage volta pro marco zero do evento atual quando nenhuma posicao do evento anterior foi preenchida, EI-SES-04 caso 3`() {
        val referencia = referenceImage(
            startingPosition = 1,
            isFirstEventOfSession = false,
            previousFrameImage = null,
            zeroMarkImage = "marco-zero-evento-atual.png",
            lastFilledImageOfPreviousEvent = null,
        )

        assertEquals("marco-zero-evento-atual.png", referencia)
    }
}
