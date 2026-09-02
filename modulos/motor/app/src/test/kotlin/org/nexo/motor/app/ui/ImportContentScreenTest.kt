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
class ImportContentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `sem resultado ainda nao mostra Pacote aceito nem violacao`() {
        composeTestRule.setContent {
            ImportContentScreen(result = null, onSelectFileRequested = {})
        }

        composeTestRule.onNodeWithText("Pacote aceito").assertDoesNotExist()
    }

    @Test
    fun `botao Selecionar arquivo chama onSelectFileRequested`() {
        var requested = false
        composeTestRule.setContent {
            ImportContentScreen(result = null, onSelectFileRequested = { requested = true })
        }

        composeTestRule.onNodeWithText("Selecionar arquivo (.zip)").performClick()

        assertTrue(requested)
    }

    @Test
    fun `resultado aceito mostra Pacote aceito`() {
        composeTestRule.setContent {
            ImportContentScreen(result = ImportScreenResult.Accepted, onSelectFileRequested = {})
        }

        composeTestRule.onNodeWithText("Pacote aceito").assertIsDisplayed()
    }

    @Test
    fun `resultado recusado mostra a lista completa de violacoes`() {
        composeTestRule.setContent {
            ImportContentScreen(
                result = ImportScreenResult.Rejected(
                    violations = listOf(
                        "instance.themes[0]: nome repetido",
                        "instance.themes[0].events[1]: marco zero ausente",
                    ),
                ),
                onSelectFileRequested = {},
            )
        }

        composeTestRule.onNodeWithText("instance.themes[0]: nome repetido").assertIsDisplayed()
        composeTestRule.onNodeWithText("instance.themes[0].events[1]: marco zero ausente")
            .assertIsDisplayed()
    }
}
