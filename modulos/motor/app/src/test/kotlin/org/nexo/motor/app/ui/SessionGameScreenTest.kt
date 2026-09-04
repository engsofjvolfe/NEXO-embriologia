package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.nexo.motor.core.connectivity.ConnectionState
import org.nexo.motor.core.summary.AnsweredPosition
import org.nexo.motor.core.summary.ChainSkipSynthesis
import org.nexo.motor.core.summary.SkipMessage
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class SessionGameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        screen: SessionScreen,
        exitConfirmationRequested: Boolean = false,
        onScreenAcknowledged: () -> Unit = {},
        onSkipRequested: () -> Unit = {},
        onContinueRequested: () -> Unit = {},
        onExitRequested: () -> Unit = {},
        onExitCancelled: () -> Unit = {},
        onExitConfirmed: () -> Unit = {},
        onPauseRequested: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            SessionGameScreen(
                uiState = SessionUiState(
                    screen = screen,
                    exitConfirmationRequested = exitConfirmationRequested,
                ),
                onScreenAcknowledged = onScreenAcknowledged,
                onSkipRequested = onSkipRequested,
                onContinueRequested = onContinueRequested,
                onExitRequested = onExitRequested,
                onExitCancelled = onExitCancelled,
                onExitConfirmed = onExitConfirmed,
                onPauseRequested = onPauseRequested,
            )
        }
    }

    @Test
    fun `AttemptAccepted mostra o texto de confirmacao e avanca ao tocar`() {
        var acknowledged = false
        setContent(
            screen = SessionScreen.AttemptAccepted(confirmationText = "Foi assim que aconteceu."),
            onScreenAcknowledged = { acknowledged = true },
        )

        composeTestRule.onNodeWithText("Foi assim que aconteceu.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Foi assim que aconteceu.").performClick()

        assertTrue(acknowledged)
    }

    @Test
    fun `AttemptAccepted sem texto ainda avanca ao tocar em qualquer lugar`() {
        var acknowledged = false
        setContent(
            screen = SessionScreen.AttemptAccepted(confirmationText = null),
            onScreenAcknowledged = { acknowledged = true },
        )

        composeTestRule.onNodeWithTag(ACKNOWLEDGEABLE_CONTENT_TAG).performClick()

        assertTrue(acknowledged)
    }

    @Test
    fun `AttemptRejected mostra a mensagem fixa de negativa e avanca ao tocar`() {
        var acknowledged = false
        setContent(
            screen = SessionScreen.AttemptRejected,
            onScreenAcknowledged = { acknowledged = true },
        )

        composeTestRule.onNodeWithText("Essa peça não é a próxima da sequência.").performClick()

        assertTrue(acknowledged)
    }

    @Test
    fun `HintShown mostra o conteudo da dica e avanca ao tocar`() {
        var acknowledged = false
        setContent(
            screen = SessionScreen.HintShown(hintContent = "Observe o que mudou de posição."),
            onScreenAcknowledged = { acknowledged = true },
        )

        composeTestRule.onNodeWithText("Observe o que mudou de posição.").performClick()

        assertTrue(acknowledged)
    }

    @Test
    fun `AwaitingAttempt mostra o indicador de conexao conectado`() {
        setContent(screen = SessionScreen.AwaitingAttempt(connectionState = ConnectionState.CONNECTED))

        composeTestRule.onNodeWithText("● conectado").assertIsDisplayed()
    }

    @Test
    fun `AwaitingAttempt mostra o indicador de conexao procurando`() {
        setContent(screen = SessionScreen.AwaitingAttempt(connectionState = ConnectionState.SCANNING))

        composeTestRule.onNodeWithText("◐ procurando").assertIsDisplayed()
    }

    @Test
    fun `AwaitingAttempt mostra o indicador de conexao desconectado`() {
        setContent(screen = SessionScreen.AwaitingAttempt(connectionState = ConnectionState.DISCONNECTED))

        composeTestRule.onNodeWithText("○ desconectado").assertIsDisplayed()
    }

    @Test
    fun `AwaitingAttempt sem acessorio nao mostra nenhum indicador de conexao`() {
        setContent(screen = SessionScreen.AwaitingAttempt(connectionState = null))

        composeTestRule.onNodeWithText("● conectado").assertDoesNotExist()
        composeTestRule.onNodeWithText("◐ procurando").assertDoesNotExist()
        composeTestRule.onNodeWithText("○ desconectado").assertDoesNotExist()
    }

    @Test
    fun `AwaitingAttempt botao pular peca chama onSkipRequested`() {
        var skipped = false
        setContent(
            screen = SessionScreen.AwaitingAttempt(connectionState = null),
            onSkipRequested = { skipped = true },
        )

        composeTestRule.onNodeWithText("Pular peça").performClick()

        assertTrue(skipped)
    }

    @Test
    fun `StudySuggestionShown com pular disponivel separa toque livre do botao de pular`() {
        var acknowledged = false
        var skipped = false
        setContent(
            screen = SessionScreen.StudySuggestionShown(skipAvailable = true),
            onScreenAcknowledged = { acknowledged = true },
            onSkipRequested = { skipped = true },
        )

        composeTestRule.onNodeWithText("Pular peça").performClick()
        assertTrue(skipped)
        assertFalse(acknowledged)

        composeTestRule.onNodeWithText("Sugestão de estudo").performClick()
        assertTrue(acknowledged)
    }

    @Test
    fun `StudySuggestionShown sem pular disponivel nao mostra botao de pular`() {
        setContent(screen = SessionScreen.StudySuggestionShown(skipAvailable = false))

        composeTestRule.onNodeWithText("Pular peça").assertDoesNotExist()
    }

    @Test
    fun `EventSummary com proximo evento mostra Continuar e chama onContinueRequested`() {
        var continued = false
        setContent(
            screen = SessionScreen.EventSummary(
                synthesis = "Síntese do evento.",
                hasNextEvent = true,
                chainSynthesis = null,
            ),
            onContinueRequested = { continued = true },
        )

        composeTestRule.onNodeWithText("Continuar").performClick()

        assertTrue(continued)
    }

    @Test
    fun `EventSummary sem proximo evento mostra Ver resultado`() {
        setContent(
            screen = SessionScreen.EventSummary(
                synthesis = "Síntese do evento.",
                hasNextEvent = false,
                chainSynthesis = null,
            ),
        )

        composeTestRule.onNodeWithText("Ver resultado").assertIsDisplayed()
    }

    @Test
    fun `EventSummary com sintese de cadeia continua mostra a narrativa da cadeia`() {
        setContent(
            screen = SessionScreen.EventSummary(
                synthesis = "Síntese do evento.",
                hasNextEvent = false,
                chainSynthesis = ChainSynthesisResult.Continuous(synthesis = "Narrativa da cadeia inteira."),
            ),
        )

        composeTestRule.onNodeWithText("Narrativa da cadeia inteira.").assertIsDisplayed()
    }

    @Test
    fun `EventSummary com sintese de cadeia consolidada mostra o total preenchidas e perdidas`() {
        setContent(
            screen = SessionScreen.EventSummary(
                synthesis = "Síntese do evento.",
                hasNextEvent = false,
                chainSynthesis = ChainSynthesisResult.Consolidated(
                    totals = ChainSkipSynthesis(filledCount = 5, lostCount = 2),
                ),
            ),
        )

        composeTestRule.onNodeWithText("Preenchidas: 5 — Perdidas: 2").assertIsDisplayed()
    }

    @Test
    fun `SkipMessageShown mostra quantas posicoes foram respondidas e o intervalo sem resposta`() {
        setContent(
            screen = SessionScreen.SkipMessageShown(
                message = SkipMessage(
                    answered = listOf(
                        AnsweredPosition(position = 1, confirmationText = "Primeira peça."),
                        AnsweredPosition(position = 2, confirmationText = null),
                    ),
                    unansweredPositions = listOf(3, 4),
                ),
                hasNextEvent = true,
                chainSynthesis = null,
            ),
        )

        composeTestRule.onNodeWithText("Respondidas: 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sem resposta: 3-4").assertIsDisplayed()
    }

    @Test
    fun `SkipMessageShown com sintese de cadeia consolidada mostra o total junto com a mensagem de pulo`() {
        setContent(
            screen = SessionScreen.SkipMessageShown(
                message = SkipMessage(
                    answered = listOf(AnsweredPosition(position = 1, confirmationText = null)),
                    unansweredPositions = listOf(2, 3),
                ),
                hasNextEvent = false,
                chainSynthesis = ChainSynthesisResult.Consolidated(
                    totals = ChainSkipSynthesis(filledCount = 7, lostCount = 4),
                ),
            ),
        )

        composeTestRule.onNodeWithText("Respondidas: 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sem resposta: 2-3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preenchidas: 7 — Perdidas: 4").assertIsDisplayed()
    }

    @Test
    fun `SkipMessageShown com posicoes sem resposta nao contiguas nao finge que sao um intervalo unico`() {
        setContent(
            screen = SessionScreen.SkipMessageShown(
                message = SkipMessage(
                    answered = listOf(AnsweredPosition(position = 4, confirmationText = null)),
                    unansweredPositions = listOf(2, 3, 5),
                ),
                hasNextEvent = true,
                chainSynthesis = null,
            ),
        )

        composeTestRule.onNodeWithText("Sem resposta: 2-3, 5").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sem resposta: 2-5").assertDoesNotExist()
    }

    @Test
    fun `SkipMessageShown com proximo evento mostra Continuar`() {
        var continued = false
        setContent(
            screen = SessionScreen.SkipMessageShown(
                message = SkipMessage(answered = emptyList(), unansweredPositions = listOf(1)),
                hasNextEvent = true,
                chainSynthesis = null,
            ),
            onContinueRequested = { continued = true },
        )

        composeTestRule.onNodeWithText("Continuar").performClick()

        assertTrue(continued)
    }

    @Test
    fun `controle Sair chama onExitRequested`() {
        var requested = false
        setContent(
            screen = SessionScreen.AwaitingAttempt(connectionState = null),
            onExitRequested = { requested = true },
        )

        composeTestRule.onNodeWithText("Sair").performClick()

        assertTrue(requested)
    }

    @Test
    fun `controle Pausar chama onPauseRequested`() {
        var paused = false
        setContent(
            screen = SessionScreen.AwaitingAttempt(connectionState = null),
            onPauseRequested = { paused = true },
        )

        composeTestRule.onNodeWithContentDescription("Pausar").performClick()

        assertTrue(paused)
    }

    @Test
    fun `controle Pausar some durante a confirmacao de saida`() {
        setContent(
            screen = SessionScreen.AwaitingAttempt(connectionState = null),
            exitConfirmationRequested = true,
        )

        composeTestRule.onNodeWithContentDescription("Pausar").assertDoesNotExist()
    }

    @Test
    fun `confirmacao de saida chama onExitCancelled e onExitConfirmed pelos botoes certos`() {
        var confirmed = false
        var cancelled = false
        setContent(
            screen = SessionScreen.AwaitingAttempt(connectionState = null),
            exitConfirmationRequested = true,
            onExitConfirmed = { confirmed = true },
            onExitCancelled = { cancelled = true },
        )

        composeTestRule.onNodeWithText("Cancelar").performClick()
        assertTrue(cancelled)
        assertFalse(confirmed)

        composeTestRule.onNodeWithTag(EXIT_CONFIRM_BUTTON_TAG).performClick()
        assertTrue(confirmed)
    }
}
