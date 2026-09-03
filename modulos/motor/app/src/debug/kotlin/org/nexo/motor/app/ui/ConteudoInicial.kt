package org.nexo.motor.app.ui

/**
 * Fonte de conteúdo pra `MotorApp.kt` (`src/main/`) numa build de depuração — decisions/0042.
 * Existe só pra permitir testar visualmente o encadeamento real das telas antes de existir um
 * jeito real de carregar conteúdo importado (pendência própria, já registrada). Nomes genéricos
 * (Tema A/B, Evento 1/2), no mesmo padrão já usado no protótipo navegável
 * (`design/prototipo-navegavel.js`) — nunca um assunto real, porque o motor não conhece nenhum
 * assunto específico (RNF-MOD-01). `src/main/` nunca declara estas cinco funções — só chama,
 * cada tipo de build fornecendo a própria versão (ver `src/release/` pro par vazio).
 *
 * Conteúdo rico o bastante pra exercitar de verdade o encadeamento: dois temas, um evento com
 * dica habilitada e outro sem, disponibilidade de pular variando, e um evento com mais de uma
 * posição de início cadastrada (pra mostrar a escolha "Começar em", EI-SES-02).
 */

internal fun conteudoInicialDeNavegacao(): List<NavigationEntry> = listOf(
    NavigationEntry(id = "instancia", label = "Instância de exemplo", depth = 0, expandable = true, expanded = true),
    NavigationEntry(id = "tema-a", label = "Tema A", depth = 1, expandable = true, expanded = true),
    NavigationEntry(id = "tema-a-evento-1", label = "Evento 1", depth = 2, expandable = false, expanded = false),
    NavigationEntry(id = "tema-a-evento-2", label = "Evento 2", depth = 2, expandable = false, expanded = false),
    NavigationEntry(id = "tema-b", label = "Tema B", depth = 1, expandable = true, expanded = false),
)

internal fun conteudoInicialDeConfiguracoesDeEvento(): List<EventConfigUiState> = listOf(
    EventConfigUiState(
        eventName = "Evento 1",
        hintEnabledInContent = true,
        skipAvailable = true,
        hintThresholdText = "3",
        studyThresholdText = "6",
    ),
    EventConfigUiState(
        eventName = "Evento 2",
        hintEnabledInContent = false,
        skipAvailable = false,
        hintThresholdText = "3",
        studyThresholdText = "6",
    ),
)

internal fun conteudoInicialDePosicoesDeInicio(): List<Int> = listOf(1, 2, 3, 4)

internal fun conteudoInicialDeEstadoDeJogo(): SessionUiState = SessionUiState(
    screen = SessionScreen.AwaitingAttempt(connectionState = null),
)

internal fun conteudoInicialDeResumoDeJogo(): SessionUiState = SessionUiState(
    screen = SessionScreen.EventSummary(synthesis = "Síntese de exemplo, só pra teste visual.", hasNextEvent = false),
)
