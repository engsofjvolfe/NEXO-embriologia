package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ConsentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `mostra o texto de consentimento e comeca com Continuar desabilitado`() {
        composeTestRule.setContent {
            ConsentScreen(consentText = "Texto legal de exemplo.", onContinueRequested = {})
        }

        composeTestRule.onNodeWithText("Texto legal de exemplo.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continuar").assertIsNotEnabled()
    }

    @Test
    fun `marcar Li e concordo habilita Continuar, que chama onContinueRequested`() {
        var continued = false
        composeTestRule.setContent {
            ConsentScreen(
                consentText = "Texto legal de exemplo.",
                onContinueRequested = { continued = true },
            )
        }

        composeTestRule.onNodeWithText("Li e concordo").performClick()
        composeTestRule.onNodeWithText("Continuar").assertIsEnabled()
        composeTestRule.onNodeWithText("Continuar").performClick()

        assertTrue(continued)
    }
}
