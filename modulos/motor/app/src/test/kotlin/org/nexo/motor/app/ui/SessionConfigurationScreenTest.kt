package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
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

    private val twoEvents = oneEvent + EventConfigUiState(
        eventName = "Nidação",
        hintEnabledInContent = true,
        skipAvailable = false,
        hintThresholdText = "2",
        studyThresholdText = "5",
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
    fun `celular mostra o nome de cada evento, um por bloco, quando ha mais de um`() {
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = twoEvents,
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

        composeTestRule.onNodeWithText("Fecundação").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nidação").assertIsDisplayed()
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

        assertEquals("Fecundação", changedEvent)
        assertEquals(true, changedValue)
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

    @Test
    fun `tablet clicar num evento da lista chama onEventSelected com o nome desse evento`() {
        var selected: String? = null
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = true,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = twoEvents,
                selectedEventName = null,
                onEventSelected = { selected = it },
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Nidação").performClick()

        assertEquals("Nidação", selected)
    }

    @Test
    fun `celular clicar na terceira posicao de inicio chama onStartingPositionSelected com essa posicao, nao com o indice`() {
        var selected: Int? = null
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(5, 8, 12),
                selectedStartingPosition = 5,
                onStartingPositionSelected = { selected = it },
                eventConfigs = listOf(
                    EventConfigUiState(
                        eventName = "Clivagem",
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

        composeTestRule.onNodeWithTag("posicao-inicio-12").performClick()

        assertEquals(12, selected)
    }

    @Test
    fun `celular digitar no limiar de dica do segundo evento chama onHintThresholdChanged com o nome desse evento`() {
        var changedEvent: String? = null
        var changedValue: String? = null
        val doisEventosComDica = listOf(
            EventConfigUiState(
                eventName = "Blastocisto",
                hintEnabledInContent = true,
                skipAvailable = false,
                hintThresholdText = "",
                studyThresholdText = "",
            ),
            EventConfigUiState(
                eventName = "Implantação",
                hintEnabledInContent = true,
                skipAvailable = false,
                hintThresholdText = "",
                studyThresholdText = "",
            ),
        )
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = doisEventosComDica,
                selectedEventName = null,
                onEventSelected = {},
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { name, value -> changedEvent = name; changedValue = value },
                onStudyThresholdChanged = { _, _ -> },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onAllNodesWithText("Limiar de erro — dica")[1].performTextInput("4")

        assertEquals("Implantação", changedEvent)
        assertEquals("4", changedValue)
    }

    @Test
    fun `celular digitar no limiar de sugestao de estudo do segundo evento chama onStudyThresholdChanged com o nome desse evento`() {
        var changedEvent: String? = null
        var changedValue: String? = null
        val doisEventosComDica = listOf(
            EventConfigUiState(
                eventName = "Mórula",
                hintEnabledInContent = true,
                skipAvailable = false,
                hintThresholdText = "",
                studyThresholdText = "",
            ),
            EventConfigUiState(
                eventName = "Eclosão",
                hintEnabledInContent = true,
                skipAvailable = false,
                hintThresholdText = "",
                studyThresholdText = "",
            ),
        )
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = false,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = doisEventosComDica,
                selectedEventName = null,
                onEventSelected = {},
                onSkipAvailableChanged = { _, _ -> },
                onHintThresholdChanged = { _, _ -> },
                onStudyThresholdChanged = { name, value -> changedEvent = name; changedValue = value },
                idleThresholdText = "60",
                onIdleThresholdChanged = {},
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onAllNodesWithText("Limiar de erro — sugestão de estudo")[1].performTextInput("9")

        assertEquals("Eclosão", changedEvent)
        assertEquals("9", changedValue)
    }

    @Test
    fun `celular digitar no tempo de ociosidade chama onIdleThresholdChanged`() {
        var changedValue: String? = null
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
                idleThresholdText = "",
                onIdleThresholdChanged = { changedValue = it },
                onStartSessionRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Tempo de ociosidade").performTextInput("45")

        assertEquals("45", changedValue)
    }

    @Test
    fun `tablet sem evento selecionado mostra a lista mas nao mostra o painel de configuracao`() {
        composeTestRule.setContent {
            SessionConfigurationScreen(
                isTabletLayout = true,
                startingPositionOptions = listOf(1),
                selectedStartingPosition = 1,
                onStartingPositionSelected = {},
                eventConfigs = twoEvents,
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

        composeTestRule.onNodeWithText("Fecundação").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nidação").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pular disponível").assertDoesNotExist()
    }
}
