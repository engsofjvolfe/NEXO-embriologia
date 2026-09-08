package org.nexo.motor.app.ui

import android.content.Context
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.launch
import org.nexo.motor.core.session.deleteSessionState
import org.nexo.motor.core.session.loadSessionState
import java.io.File

/**
 * Ponto de entrada real das telas do motor -- decide, ao abrir, entre sessão pausada
 * (EI-NAV-01) e navegação normal (EI-NAV-02), e encadeia as nove telas de navegação com a tela
 * de jogo. Mecanismo decidido em decisions/0040. Encadeamento de pausar/sair confere com o
 * protótipo navegável já validado (decisions/0036): pausar leva à tela de Sessão pausada
 * (DA-RET-01), sair confirmado leva ao Resultado (DA-RET-14, EI-PAU-04), nunca direto à
 * Navegação. Gatilho de Consentimento e de Importar conteúdo: decisions/0045.
 *
 * Nenhum dado de conteúdo (nome de instância, tema, evento) é declarado aqui -- este arquivo
 * chama `conteudoInicialDe*()` sem saber de onde vem a resposta: em build de depuração, vem de
 * `src/debug/ConteudoInicial.kt` (conteúdo de exemplo, só pra teste visual); em build de
 * produção, vem de `src/release/ConteudoInicial.kt` (sempre vazio, porque o aplicativo ainda não
 * tem nenhum jeito real de carregar conteúdo importado) -- decisions/0042.
 */
private sealed interface AppScreen {
    data class Paused(val eventName: String) : AppScreen
    data object Navigation : AppScreen
    data object Configuration : AppScreen
    data object Consent : AppScreen
    data object ImportContent : AppScreen
    data object Game : AppScreen
    data object Result : AppScreen
}

// Visível (não privado) pra MotorAppTest.kt montar o mesmo caminho de arquivo sem duplicar o
// nome, separado da função abaixo porque o teste não tem um Context de produção pra passar por
// ela inteira.
internal const val PAUSED_SESSION_FILE_NAME = "sessao-pausada.json"

private fun pausedSessionFile(context: Context): File =
    File(context.filesDir, PAUSED_SESSION_FILE_NAME)

@Composable
fun MotorApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var current by remember {
        val paused = loadSessionState(pausedSessionFile(context))
        mutableStateOf<AppScreen>(
            if (paused != null) AppScreen.Paused(paused.expectedEventName) else AppScreen.Navigation,
        )
    }
    var gameUiState by remember { mutableStateOf(conteudoInicialDeResumoDeJogo()) }
    val configs = conteudoInicialDeConfiguracoesDeEvento()
    // Campo "Tempo de ociosidade" (EI-NAV-05) começa vazio -- EI-CFG-01 diz que esse número é
    // sempre escolhido por quem configura a sessão, nunca pelo motor.
    var idleThresholdText by remember { mutableStateOf("") }
    // decisions/0045: null enquanto ninguém marcou "lembrar minha escolha" ainda.
    val rememberedConsent by consentChoiceFlow(context).collectAsState(initial = null)

    when (val screen = current) {
        is AppScreen.Paused -> PausedSessionScreen(
            eventName = screen.eventName,
            onResumeRequested = {
                gameUiState = conteudoInicialDeEstadoDeJogo()
                current = AppScreen.Game
            },
            onExitRequested = {
                deleteSessionState(pausedSessionFile(context))
                current = AppScreen.Navigation
            },
        )
        AppScreen.Navigation -> NavigationScreen(
            searchText = "",
            onSearchTextChanged = {},
            entries = conteudoInicialDeNavegacao(),
            onEntryClicked = { current = AppScreen.Configuration },
            onImportContentRequested = { current = AppScreen.ImportContent },
        )
        AppScreen.ImportContent -> ImportContentScreen(
            result = null,
            onSelectFileRequested = {},
            onBackRequested = { current = AppScreen.Navigation },
        )
        AppScreen.Configuration -> SessionConfigurationScreen(
            isTabletLayout = currentWindowAdaptiveInfo().windowSizeClass
                .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND),
            startingPositionOptions = conteudoInicialDePosicoesDeInicio(),
            selectedStartingPosition = conteudoInicialDePosicoesDeInicio().firstOrNull() ?: 1,
            onStartingPositionSelected = {},
            eventConfigs = configs,
            selectedEventName = configs.firstOrNull()?.eventName,
            onEventSelected = {},
            onSkipAvailableChanged = { _, _ -> },
            onHintThresholdChanged = { _, _ -> },
            onStudyThresholdChanged = { _, _ -> },
            idleThresholdText = idleThresholdText,
            onIdleThresholdChanged = { idleThresholdText = it },
            onStartSessionRequested = {
                // decisions/0045: sem escolha lembrada, passa por Consentimento antes do jogo;
                // com escolha lembrada, vai direto -- EI-REG-03 (pedir no momento de registrar
                // dado de identificação) já foi atendido numa sessão anterior.
                if (rememberedConsent == null) {
                    current = AppScreen.Consent
                } else {
                    gameUiState = conteudoInicialDeResumoDeJogo()
                    current = AppScreen.Game
                }
            },
            rememberedConsentGiven = rememberedConsent?.given,
            onConsentReminderClicked = { current = AppScreen.Consent },
        )
        AppScreen.Consent -> ConsentScreen(
            consentText = conteudoInicialDeTextoDeConsentimento(),
            // EI-REG-03: "Continuar" nunca trava a sessão -- sem "Li e concordo" (given = false),
            // a sessão segue normalmente, só sem dado de identificação (findings.md, 2026-09-07).
            onContinueRequested = { given, rememberChoice ->
                coroutineScope.launch {
                    saveConsentChoice(
                        context,
                        if (rememberChoice) RememberedConsent(given = given) else null,
                    )
                }
                gameUiState = conteudoInicialDeResumoDeJogo()
                current = AppScreen.Game
            },
        )
        AppScreen.Game -> SessionGameScreen(
            uiState = gameUiState,
            onScreenAcknowledged = {},
            onSkipRequested = {},
            onContinueRequested = { current = AppScreen.Result },
            onExitRequested = { gameUiState = gameUiState.copy(exitConfirmationRequested = true) },
            onExitCancelled = { gameUiState = gameUiState.copy(exitConfirmationRequested = false) },
            onExitConfirmed = { current = AppScreen.Result },
            onPauseRequested = { current = AppScreen.Paused(configs.firstOrNull()?.eventName ?: "") },
        )
        AppScreen.Result -> ResultScreen(
            errorCount = 0,
            skipCount = 0,
            pauseCount = 0,
            onExportCsv = {},
            onExportPdf = {},
            onShare = {},
            onBackToNavigation = { current = AppScreen.Navigation },
        )
    }
}
