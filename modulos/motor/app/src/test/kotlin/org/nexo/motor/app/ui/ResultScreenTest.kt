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
class ResultScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        onExportCsv: () -> Unit = {},
        onExportPdf: () -> Unit = {},
        onShare: () -> Unit = {},
        onBackToNavigation: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            ResultScreen(
                errorCount = 4,
                skipCount = 1,
                pauseCount = 2,
                onExportCsv = onExportCsv,
                onExportPdf = onExportPdf,
                onShare = onShare,
                onBackToNavigation = onBackToNavigation,
            )
        }
    }

    @Test
    fun `mostra os tres numeros do registro`() {
        setContent()

        composeTestRule.onNodeWithText("Erros: 4").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pulos: 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pausas: 2").assertIsDisplayed()
    }

    @Test
    fun `Exportar CSV chama onExportCsv`() {
        var called = false
        setContent(onExportCsv = { called = true })

        composeTestRule.onNodeWithText("Exportar CSV").performClick()

        assertTrue(called)
    }

    @Test
    fun `Exportar PDF chama onExportPdf`() {
        var called = false
        setContent(onExportPdf = { called = true })

        composeTestRule.onNodeWithText("Exportar PDF").performClick()

        assertTrue(called)
    }

    @Test
    fun `Compartilhar chama onShare`() {
        var called = false
        setContent(onShare = { called = true })

        composeTestRule.onNodeWithText("Compartilhar").performClick()

        assertTrue(called)
    }

    @Test
    fun `Voltar a navegacao chama onBackToNavigation`() {
        var called = false
        setContent(onBackToNavigation = { called = true })

        composeTestRule.onNodeWithText("Voltar à navegação").performClick()

        assertTrue(called)
    }
}
