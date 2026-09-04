package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertIsDisplayed
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
class PausedSessionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `mostra o nome do evento em que a sessao parou`() {
        composeTestRule.setContent {
            PausedSessionScreen(
                eventName = "Divisão celular",
                onResumeRequested = {},
                onExitRequested = {},
            )
        }

        composeTestRule.onNodeWithText("Divisão celular").assertIsDisplayed()
    }

    @Test
    fun `Retomar chama onResumeRequested`() {
        var resumed = false
        var exited = false
        composeTestRule.setContent {
            PausedSessionScreen(
                eventName = "Divisão celular",
                onResumeRequested = { resumed = true },
                onExitRequested = { exited = true },
            )
        }

        composeTestRule.onNodeWithText("Retomar").performClick()

        assertTrue(resumed)
        assertFalse(exited)
    }

    @Test
    fun `Sair da sessao chama onExitRequested`() {
        var exited = false
        composeTestRule.setContent {
            PausedSessionScreen(
                eventName = "Divisão celular",
                onResumeRequested = {},
                onExitRequested = { exited = true },
            )
        }

        composeTestRule.onNodeWithText("Sair da sessão").performClick()

        assertTrue(exited)
    }
}
