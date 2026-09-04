package org.nexo.motor.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.nexo.motor.app.R
import org.nexo.motor.core.connectivity.ConnectionState

// Mensagem única e padronizada de não correspondência (EI-RET-02) -- a redação exata não é
// exigida por nenhum documento, só a exigência de ser sempre a mesma, sem identificar a peça
// correta nem o motivo da rejeição.
private const val NEGATIVE_MESSAGE = "Essa peça não é a próxima da sequência."
const val EXIT_CONFIRM_BUTTON_TAG = "exit-confirm-button"
const val ACKNOWLEDGEABLE_CONTENT_TAG = "acknowledgeable-content"

@Composable
fun SessionGameScreen(
    uiState: SessionUiState,
    onScreenAcknowledged: () -> Unit,
    onSkipRequested: () -> Unit,
    onContinueRequested: () -> Unit,
    onExitRequested: () -> Unit,
    onExitCancelled: () -> Unit,
    onExitConfirmed: () -> Unit,
    onPauseRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        SessionGameScreenContent(
            screen = uiState.screen,
            onScreenAcknowledged = onScreenAcknowledged,
            onSkipRequested = onSkipRequested,
            onContinueRequested = onContinueRequested,
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onExitRequested) { Text("Sair") }
            if (!uiState.exitConfirmationRequested) {
                IconButton(onClick = onPauseRequested) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pause_24),
                        contentDescription = "Pausar",
                    )
                }
            }
        }
        if (uiState.exitConfirmationRequested) {
            ExitConfirmationDialog(
                onExitCancelled = onExitCancelled,
                onExitConfirmed = onExitConfirmed,
            )
        }
    }
}

@Composable
private fun SessionGameScreenContent(
    screen: SessionScreen,
    onScreenAcknowledged: () -> Unit,
    onSkipRequested: () -> Unit,
    onContinueRequested: () -> Unit,
) {
    when (screen) {
        is SessionScreen.Reference -> ReferenceContent(screen)
        is SessionScreen.AwaitingAttempt -> AwaitingAttemptContent(screen, onSkipRequested)
        is SessionScreen.AttemptAccepted -> AcknowledgeableContent(
            text = screen.confirmationText,
            onAcknowledge = onScreenAcknowledged,
        )
        is SessionScreen.AttemptRejected -> AcknowledgeableContent(
            text = NEGATIVE_MESSAGE,
            onAcknowledge = onScreenAcknowledged,
        )
        is SessionScreen.HintShown -> AcknowledgeableContent(
            text = screen.hintContent,
            onAcknowledge = onScreenAcknowledged,
        )
        is SessionScreen.StudySuggestionShown -> StudySuggestionContent(
            screen = screen,
            onScreenAcknowledged = onScreenAcknowledged,
            onSkipRequested = onSkipRequested,
        )
        is SessionScreen.EventSummary -> EventSummaryContent(screen, onContinueRequested)
        is SessionScreen.SkipMessageShown -> SkipMessageContent(screen, onContinueRequested)
    }
}

@Composable
private fun ReferenceContent(screen: SessionScreen.Reference) {
    // screen.referenceImage é só o caminho do fotograma dentro do pacote de conteúdo (PD-IMP-01)
    // -- a estratégia de carregamento já está decidida (decisions/0038); falta só a parte de
    // acesso ao pacote de conteúdo já importado (ContentPackageArchive) pra aplicá-la aqui -- ver
    // tasks.md. Texto provisório até essa pendência fechar.
    Text(text = "Referência: ${screen.referenceImage}", style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun AwaitingAttemptContent(
    screen: SessionScreen.AwaitingAttempt,
    onSkipRequested: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        screen.connectionState?.let { state ->
            Text(
                text = connectionIndicatorText(state),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 56.dp, end = 16.dp),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        TextButton(
            onClick = onSkipRequested,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Text("Pular peça")
        }
    }
}

private fun connectionIndicatorText(state: ConnectionState): String = when (state) {
    ConnectionState.CONNECTED -> "● conectado"
    ConnectionState.SCANNING -> "◐ procurando"
    ConnectionState.DISCONNECTED -> "○ desconectado"
}

@Composable
private fun AcknowledgeableContent(text: String?, onAcknowledge: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(ACKNOWLEDGEABLE_CONTENT_TAG)
            .clickable(onClick = onAcknowledge),
        contentAlignment = Alignment.Center,
    ) {
        if (text != null) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun StudySuggestionContent(
    screen: SessionScreen.StudySuggestionShown,
    onScreenAcknowledged: () -> Unit,
    onSkipRequested: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onScreenAcknowledged),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Sugestão de estudo", style = MaterialTheme.typography.bodyLarge)
        }
        if (screen.skipAvailable) {
            Button(
                onClick = onSkipRequested,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            ) {
                Text("Pular peça")
            }
        }
    }
}

@Composable
private fun EventSummaryContent(
    screen: SessionScreen.EventSummary,
    onContinueRequested: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = screen.synthesis, style = MaterialTheme.typography.bodyLarge)
            when (val chain = screen.chainSynthesis) {
                is ChainSynthesisResult.Continuous ->
                    Text(text = chain.synthesis, style = MaterialTheme.typography.bodyLarge)
                is ChainSynthesisResult.Consolidated ->
                    Text(
                        text = "Preenchidas: ${chain.totals.filledCount} " +
                            "— Perdidas: ${chain.totals.lostCount}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                null -> Unit
            }
        }
        ContinueFooter(hasNextEvent = screen.hasNextEvent, onContinueRequested = onContinueRequested)
    }
}

@Composable
private fun SkipMessageContent(
    screen: SessionScreen.SkipMessageShown,
    onContinueRequested: () -> Unit,
) {
    // EI-PUL-05: nunca revela o conteúdo de uma posição pulada -- só a contagem do que foi
    // respondido e o intervalo (posição a posição) que ficou sem resposta.
    val message = screen.message
    val unanswered = message.unansweredPositions
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "Respondidas: ${message.answered.size}",
                style = MaterialTheme.typography.bodyLarge,
            )
            if (unanswered.isNotEmpty()) {
                Text(
                    text = "Sem resposta: ${formatUnansweredPositions(unanswered)}",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            when (val chain = screen.chainSynthesis) {
                is ChainSynthesisResult.Continuous ->
                    Text(text = chain.synthesis, style = MaterialTheme.typography.bodyLarge)
                is ChainSynthesisResult.Consolidated ->
                    Text(
                        text = "Preenchidas: ${chain.totals.filledCount} " +
                            "— Perdidas: ${chain.totals.lostCount}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                null -> Unit
            }
        }
        ContinueFooter(hasNextEvent = screen.hasNextEvent, onContinueRequested = onContinueRequested)
    }
}

// Agrupa posições em blocos vizinhos (ex.: [2, 3, 5] -> "2-3, 5") em vez de presumir que a
// primeira e a última posição da lista formam sempre um intervalo fechado -- EI-PUL-05 exige
// "a indicação do intervalo sem resposta", mas EI-VAL-01/EI-PUL-04 permitem pular uma posição,
// responder a seguinte, e pular outra depois, então as posições sem resposta nem sempre são
// vizinhas entre si.
private fun formatUnansweredPositions(positions: List<Int>): String {
    val sorted = positions.sorted()
    val groups = mutableListOf<IntRange>()
    var start = sorted.first()
    var end = start
    for (position in sorted.drop(1)) {
        if (position == end + 1) {
            end = position
        } else {
            groups += start..end
            start = position
            end = position
        }
    }
    groups += start..end
    return groups.joinToString(", ") { range ->
        if (range.first == range.last) "${range.first}" else "${range.first}-${range.last}"
    }
}

@Composable
private fun ContinueFooter(hasNextEvent: Boolean, onContinueRequested: () -> Unit) {
    Button(
        onClick = onContinueRequested,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(if (hasNextEvent) "Continuar" else "Ver resultado")
    }
}

@Composable
private fun ExitConfirmationDialog(
    onExitCancelled: () -> Unit,
    onExitConfirmed: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onExitCancelled,
        properties = DialogProperties(dismissOnClickOutside = false),
        confirmButton = {
            TextButton(
                onClick = onExitConfirmed,
                modifier = Modifier.testTag(EXIT_CONFIRM_BUTTON_TAG),
            ) { Text("Sair") }
        },
        dismissButton = {
            TextButton(onClick = onExitCancelled) { Text("Cancelar") }
        },
        text = { Text("Sair da sessão? O progresso não poderá ser retomado.") },
    )
}
