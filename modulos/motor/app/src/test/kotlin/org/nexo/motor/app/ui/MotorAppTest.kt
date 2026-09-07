package org.nexo.motor.app.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.nexo.motor.core.session.SessionState
import org.nexo.motor.core.session.saveSessionState
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class MotorAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // O arquivo do DataStore da escolha de consentimento lembrada sobrevive de um método de teste
    // pro outro dentro da mesma sandbox do Robolectric (mesma armadilha já documentada em
    // ConsentPreferenceTest.kt) -- sem isso, a ordem em que os testes rodam decide se a tela de
    // Consentimento aparece ou não, quebrando testes que não têm nada a ver com essa escolha.
    @Before
    fun limparEscolhaDeConsentimentoLembrada() = runBlocking {
        saveConsentChoice(RuntimeEnvironment.getApplication(), null)
    }

    private fun pausedSessionFile(): File =
        File(RuntimeEnvironment.getApplication().filesDir, PAUSED_SESSION_FILE_NAME)

    // decisions/0045: sem escolha de consentimento lembrada (caso padrão de todos os testes que só
    // querem chegar no jogo), "Iniciar sessão" passa por AppScreen.Consent antes de AppScreen.Game.
    private fun abrirConfiguracaoEIniciarSessao() {
        composeTestRule.onNodeWithText("Evento 1").performClick()
        composeTestRule.onNodeWithText("Iniciar sessão").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Li e concordo").performClick()
        composeTestRule.onNodeWithText("Continuar").performClick()
    }

    @Test
    fun `EI-NAV-02 - sem sessao pausada, abre na tela de navegacao`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }

        composeTestRule.onNodeWithText("Buscar").assertIsDisplayed()
    }

    @Test
    fun `EI-NAV-01 - com sessao pausada, abre na tela de sessao pausada com o evento certo`() {
        val file = pausedSessionFile()
        saveSessionState(
            SessionState(expectedEventName = "Evento salvo", expectedPosition = 2, paused = true, log = emptyList()),
            file,
        )

        composeTestRule.setContent { MotorApp() }

        composeTestRule.onNodeWithText("Evento salvo").assertIsDisplayed()

        file.delete()
    }

    @Test
    fun `Sair da sessao apaga o arquivo de sessao pausada e volta pra navegacao`() {
        val file = pausedSessionFile()
        saveSessionState(
            SessionState(expectedEventName = "Evento salvo", expectedPosition = 2, paused = true, log = emptyList()),
            file,
        )

        composeTestRule.setContent { MotorApp() }
        composeTestRule.onNodeWithText("Sair da sessão").performClick()

        composeTestRule.onNodeWithText("Buscar").assertIsDisplayed()
        assertFalse(file.exists())
    }

    @Test
    fun `Retomar nao apaga o arquivo de sessao pausada e volta pra tela de jogo`() {
        val file = pausedSessionFile()
        saveSessionState(
            SessionState(expectedEventName = "Evento salvo", expectedPosition = 2, paused = true, log = emptyList()),
            file,
        )

        composeTestRule.setContent { MotorApp() }
        composeTestRule.onNodeWithText("Retomar").performClick()

        composeTestRule.onNodeWithText("Sair").assertIsDisplayed()
        assertTrue(file.exists())

        file.delete()
    }

    @Test
    fun `encadeamento real - navegacao ate configuracao e inicio de sessao`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }

        abrirConfiguracaoEIniciarSessao()

        composeTestRule.onNodeWithText("Sair").assertIsDisplayed()
    }

    @Test
    fun `terminar a sessao leva ao resultado, que volta pra navegacao`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        abrirConfiguracaoEIniciarSessao()

        composeTestRule.onNodeWithText("Ver resultado").performClick()
        composeTestRule.onNodeWithText("Voltar à navegação").assertIsDisplayed()

        composeTestRule.onNodeWithText("Voltar à navegação").performClick()
        composeTestRule.onNodeWithText("Buscar").assertIsDisplayed()
    }

    @Test
    fun `sair da tela de jogo, confirmado, leva ao resultado`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        abrirConfiguracaoEIniciarSessao()

        composeTestRule.onNodeWithText("Sair").performClick()
        composeTestRule.onNodeWithTag(EXIT_CONFIRM_BUTTON_TAG).performClick()

        composeTestRule.onNodeWithText("Voltar à navegação").assertIsDisplayed()
    }

    @Test
    fun `pausar na tela de jogo leva a sessao pausada`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        abrirConfiguracaoEIniciarSessao()

        composeTestRule.onNodeWithContentDescription("Pausar").performClick()

        composeTestRule.onNodeWithText("Retomar").assertIsDisplayed()
    }

    @Test
    fun `EI-SES-02 - evento de exemplo com mais de uma posicao mostra a escolha Comecar em`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        composeTestRule.onNodeWithText("Evento 1").performClick()

        composeTestRule.onNodeWithText("Começar em:").assertIsDisplayed()
    }

    @Test
    fun `decisions0043 - largura compacta usa leiaute de celular na Configuracao`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        abrirConfiguracaoEIniciarSessaoSemIniciar()

        // CompactLayout mostra o bloco de configuração completo de cada evento do alcance da
        // sessão, sem esconder nenhum atrás de seleção -- diferente do leiaute de tablet, que só
        // mostra o bloco do evento selecionado (ver teste de tablet, abaixo). O conteúdo de
        // exemplo (ConteudoInicial.kt) tem dois eventos, então "Pular disponível" aparecendo duas
        // vezes -- uma por evento -- é o sinal de que estamos no leiaute celular, não no de
        // tablet, onde apareceria só uma vez.
        composeTestRule.onAllNodesWithText("Pular disponível").assertCountEquals(2)
    }

    @Config(qualifiers = "w840dp-h480dp")
    @Test
    fun `decisions0043 - largura media ou maior usa leiaute de tablet na Configuracao`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        abrirConfiguracaoEIniciarSessaoSemIniciar()

        // TabletLayout desenha o nome de cada evento como item de lista clicável, mas só desenha
        // o bloco de configuração completo (com "Pular disponível") do evento selecionado -- só
        // um evento pré-selecionado (o primeiro), então só um "Pular disponível" aparece, mesmo
        // com dois eventos no conteúdo de exemplo -- ver SessionConfigurationScreen.kt.
        composeTestRule.onNodeWithText("Evento 2").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Pular disponível").assertCountEquals(1)
    }

    private fun abrirConfiguracaoEIniciarSessaoSemIniciar() {
        composeTestRule.onNodeWithText("Evento 1").performClick()
    }

    @Test
    fun `decisions0045 - EI-REG-03 - iniciar sessao sem escolha lembrada mostra o Consentimento antes do jogo`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        composeTestRule.onNodeWithText("Evento 1").performClick()
        composeTestRule.onNodeWithText("Iniciar sessão").performScrollTo().performClick()

        composeTestRule.onNodeWithText("Li e concordo").assertIsDisplayed()
    }

    @Test
    fun `decisions0045 - tocar em Importar conteudo na navegacao leva a essa tela, e Voltar retorna`() {
        pausedSessionFile().delete()

        composeTestRule.setContent { MotorApp() }
        composeTestRule.onNodeWithText("Importar conteúdo").performClick()

        composeTestRule.onNodeWithText("Selecionar arquivo (.zip)").assertIsDisplayed()

        composeTestRule.onNodeWithText("Voltar").performClick()

        composeTestRule.onNodeWithText("Buscar").assertIsDisplayed()
    }

    // decisions/0045, item 3: com escolha já lembrada, "Iniciar sessão" pula direto pro jogo, sem
    // passar por AppScreen.Consent -- metade da regra que "decisions0045 - EI-REG-03..." acima não
    // cobre (aquele teste só prova o caso sem escolha lembrada).
    @Test
    fun `decisions0045 - com escolha lembrada, iniciar sessao pula direto pro jogo, sem Consentimento`() {
        pausedSessionFile().delete()
        runBlocking { saveConsentChoice(RuntimeEnvironment.getApplication(), RememberedConsent(given = true)) }

        composeTestRule.setContent { MotorApp() }
        composeTestRule.onNodeWithText("Evento 1").performClick()
        composeTestRule.onNodeWithText("Iniciar sessão").performScrollTo().performClick()

        composeTestRule.onNodeWithText("Sair").assertIsDisplayed()
    }

    // decisions/0045, item 3: o lembrete da tela de Configuração leva de volta pro Consentimento a
    // qualquer momento -- a outra metade da regra que o teste acima não cobre.
    @Test
    fun `decisions0045 - tocar no lembrete de consentimento na Configuracao volta pro Consentimento`() {
        pausedSessionFile().delete()
        runBlocking { saveConsentChoice(RuntimeEnvironment.getApplication(), RememberedConsent(given = true)) }

        composeTestRule.setContent { MotorApp() }
        composeTestRule.onNodeWithText("Evento 1").performClick()
        composeTestRule.onNodeWithText("Identificação: ativada — toque pra rever o consentimento")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithText("Li e concordo").assertIsDisplayed()
    }
}
