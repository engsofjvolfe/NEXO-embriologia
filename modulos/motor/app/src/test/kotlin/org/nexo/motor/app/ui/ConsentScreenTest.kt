package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ConsentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `mostra o texto de consentimento`() {
        composeTestRule.setContent {
            ConsentScreen(consentText = "Texto legal de exemplo.", onContinueRequested = { _, _ -> })
        }

        composeTestRule.onNodeWithText("Texto legal de exemplo.").assertIsDisplayed()
    }

    @Test
    fun `mostra a caixa Lembrar minha escolha, separada de Li e concordo`() {
        composeTestRule.setContent {
            ConsentScreen(consentText = "Texto legal de exemplo.", onContinueRequested = { _, _ -> })
        }

        composeTestRule.onNodeWithText("Li e concordo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lembrar minha escolha nas próximas sessões").assertIsDisplayed()
    }

    // findings.md, 2026-09-07: EI-REG-03 exige consentimento só pra registrar dado de
    // identificação, nunca pra seguir jogando -- "Continuar" nunca pode ficar travado.
    @Test
    fun `Continuar sempre habilitado, mesmo sem marcar nenhuma caixa`() {
        composeTestRule.setContent {
            ConsentScreen(consentText = "Texto legal de exemplo.", onContinueRequested = { _, _ -> })
        }

        composeTestRule.onNodeWithText("Continuar").assertIsEnabled()
    }

    @Test
    fun `Continuar sem marcar nenhuma caixa chama onContinueRequested com given false e remember false`() {
        var given: Boolean? = null
        var remembered: Boolean? = null
        composeTestRule.setContent {
            ConsentScreen(
                consentText = "Texto legal de exemplo.",
                onContinueRequested = { g, r -> given = g; remembered = r },
            )
        }

        composeTestRule.onNodeWithText("Continuar").performClick()

        assertFalse(given!!)
        assertFalse(remembered!!)
    }

    @Test
    fun `marcar Li e concordo e Lembrar minha escolha, Continuar chama onContinueRequested com given true e remember true`() {
        var given: Boolean? = null
        var remembered: Boolean? = null
        composeTestRule.setContent {
            ConsentScreen(
                consentText = "Texto legal de exemplo.",
                onContinueRequested = { g, r -> given = g; remembered = r },
            )
        }

        composeTestRule.onNodeWithText("Li e concordo").performClick()
        composeTestRule.onNodeWithText("Lembrar minha escolha nas próximas sessões").performClick()
        composeTestRule.onNodeWithText("Continuar").performClick()

        assertTrue(given!!)
        assertTrue(remembered!!)
    }
}
