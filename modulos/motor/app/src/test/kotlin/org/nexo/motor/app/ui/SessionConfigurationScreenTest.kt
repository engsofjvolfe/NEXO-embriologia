package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class SessionConfigurationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val oneEvent = listOf(
        EventConfigUiState(
            eventName = "Fecundação",
            hintEnabledInContent = true,
            skipAvailable = false,
            hintThresholdText = "3",
            studyThresholdText = "6",
        ),
    )

    @Test
    fun `celular esconde a escolha de posicao quando so ha uma posicao`() {
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = oneEvent,
                selectedEventName = null,
                onEventSelected = {},
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Começar em:").assertDoesNotExist()
    }

    @Test
    fun `celular mostra a escolha de posicao quando ha mais de uma`() {
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1, 2, 3),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = oneEvent,
                selectedEventName = null,
                onEventSelected = {},
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Começar em:").assertIsDisplayed()
    }

    @Test
    fun `celular mostra os limiares de dica so quando a dica esta habilitada no evento`() {
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = listOf(
                    EventConfigUiState(
                        eventName = "Sem dica",
                        hintEnabledInContent = false,
                        skipAvailable = false,
                        hintThresholdText = "",
                        studyThresholdText = "",
                    ),
                ),
                selectedEventName = null,
                onEventSelected = {},
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Limiar de erro — dica").assertDoesNotExist()
    }

    @Test
    fun `alternar Pular disponivel chama onSkipAvailableChanged com o nome do evento certo`() {
        var changedEvent: String? = null
        var changedValue: Boolean? = null
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = oneEvent,
                selectedEventName = null,
                onEventSelected = {},
                onSkipAvailableChanged = { name, value -> changedEvent = name; changedValue = value },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Pular disponível").performClick()

        assert(changedEvent == "Fecundação")
        assert(changedValue == true)
    }

    @Test
    fun `botao Iniciar sessao chama onStartSessionRequested`() {
        var started = false
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = oneEvent,
                selectedEventName = null,
                onEventSelected = {},
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = { started = true },
            )
        }

        composeTestRule.onNodeWithText("Iniciar sessão").performClick()

        assertTrue(started)
    }

    @Test
    fun `tablet mostra a lista de eventos e o painel do evento selecionado`() {
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = true,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = oneEvent,
                selectedEventName = "Fecundação",
                onEventSelected = {},
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Fecundação").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pular disponível").assertIsDisplayed()
    }
}
