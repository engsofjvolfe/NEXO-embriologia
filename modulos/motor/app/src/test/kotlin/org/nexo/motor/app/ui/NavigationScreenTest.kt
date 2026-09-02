package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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
class NavigationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `mostra o rotulo de cada entrada visivel`() {
        composeTestRule.setContent {
            NavigationScreen(
                searchText = "",
                onSearchTextChanged = {},
                entries = listOf(
                    NavigationEntry(id = "instancia-1", label = "Embriologia", depth = 0, expandable = true, expanded = false),
                ),
                onEntryClicked = {},
            )
        }

        composeTestRule.onNodeWithText("Embriologia").assertIsDisplayed()
    }

    @Test
    fun `tocar numa entrada chama onEntryClicked com ela`() {
        var clicked: NavigationEntry? = null
        val entry = NavigationEntry(id = "instancia-1", label = "Embriologia", depth = 0, expandable = true, expanded = false)
        composeTestRule.setContent {
            NavigationScreen(
                searchText = "",
                onSearchTextChanged = {},
                entries = listOf(entry),
                onEntryClicked = { clicked = it },
            )
        }

        composeTestRule.onNodeWithText("Embriologia").performClick()

        assertEquals(entry, clicked)
    }

    @Test
    fun `digitar no campo de busca chama onSearchTextChanged`() {
        var typed = ""
        composeTestRule.setContent {
            NavigationScreen(
                searchText = "",
                onSearchTextChanged = { typed = it },
                entries = emptyList(),
                onEntryClicked = {},
            )
        }

        composeTestRule.onNodeWithText("Buscar").performTextInput("emb")

        assertTrue(typed.endsWith("emb"))
    }
}
