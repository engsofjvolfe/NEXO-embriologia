package org.nexo.motor.app.ui

/**
 * Fonte de conteúdo pra `MotorApp.kt` (`src/main/`) numa build de produção — decisions/0042.
 * Sempre vazia/neutra: o aplicativo, hoje, não tem nenhum jeito real de carregar conteúdo
 * importado (pendência própria, já registrada). Par exato de `src/debug/ConteudoInicial.kt`, que
 * preenche as mesmas cinco funções com conteúdo de exemplo pra teste visual.
 */

internal fun conteudoInicialDeNavegacao(): List<NavigationEntry> = emptyList()

internal fun conteudoInicialDeConfiguracoesDeEvento(): List<EventConfigUiState> = emptyList()

internal fun conteudoInicialDePosicoesDeInicio(): List<Int> = emptyList()

internal fun conteudoInicialDeEstadoDeJogo(): SessionUiState = SessionUiState(
    screen = SessionScreen.AwaitingAttempt(connectionState = null),
)

internal fun conteudoInicialDeResumoDeJogo(): SessionUiState = SessionUiState(
    screen = SessionScreen.EventSummary(synthesis = "", hasNextEvent = false),
)
