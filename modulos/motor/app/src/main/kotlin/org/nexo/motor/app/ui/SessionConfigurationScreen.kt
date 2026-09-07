package org.nexo.motor.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Configuração de um evento dentro do alcance da sessão, tal como a tela precisa mostrar
 * (EI-NAV-05). `hintEnabledInContent` vem do conteúdo já importado (`hint_enabled` do evento,
 * PD-IMP-01) -- decide só se os dois campos de limiar aparecem, nunca é editável nesta tela.
 */
data class EventConfigUiState(
    val eventName: String,
    val hintEnabledInContent: Boolean,
    val skipAvailable: Boolean,
    val hintThresholdText: String,
    val studyThresholdText: String,
)

/**
 * DA-RET-03/04 (Ponto de início / Configuração da sessão) -- já a mesma tela, EI-NAV-05. Leiaute
 * de celular (lista vertical única) e leiaute de tablet (duas colunas), decisions/0033; qual dos
 * dois usar é decidido por quem chama (a partir da classe de tamanho de janela), não por este
 * Composable.
 */
@Composable
fun SessionConfigurationScreen(
    isTabletLayout: Boolean,
    startingPositionOptions: List<Int>,
    selectedStartingPosition: Int,
    onStartingPositionSelected: (Int) -> Unit,
    eventConfigs: List<EventConfigUiState>,
    selectedEventName: String?,
    onEventSelected: (String) -> Unit,
    onSkipAvailableChanged: (eventName: String, value: Boolean) -> Unit,
    onHintThresholdChanged: (eventName: String, value: String) -> Unit,
    onStudyThresholdChanged: (eventName: String, value: String) -> Unit,
    idleThresholdText: String,
    onIdleThresholdChanged: (String) -> Unit,
    onStartSessionRequested: () -> Unit,
    rememberedConsentGiven: Boolean? = null,
    onConsentReminderClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (isTabletLayout) {
        TabletLayout(
            startingPositionOptions = startingPositionOptions,
            selectedStartingPosition = selectedStartingPosition,
            onStartingPositionSelected = onStartingPositionSelected,
            eventConfigs = eventConfigs,
            selectedEventName = selectedEventName,
            onEventSelected = onEventSelected,
            onSkipAvailableChanged = onSkipAvailableChanged,
            onHintThresholdChanged = onHintThresholdChanged,
            onStudyThresholdChanged = onStudyThresholdChanged,
            idleThresholdText = idleThresholdText,
            onIdleThresholdChanged = onIdleThresholdChanged,
            onStartSessionRequested = onStartSessionRequested,
            rememberedConsentGiven = rememberedConsentGiven,
            onConsentReminderClicked = onConsentReminderClicked,
            modifier = modifier,
        )
    } else {
        CompactLayout(
            startingPositionOptions = startingPositionOptions,
            selectedStartingPosition = selectedStartingPosition,
            onStartingPositionSelected = onStartingPositionSelected,
            eventConfigs = eventConfigs,
            onSkipAvailableChanged = onSkipAvailableChanged,
            onHintThresholdChanged = onHintThresholdChanged,
            onStudyThresholdChanged = onStudyThresholdChanged,
            idleThresholdText = idleThresholdText,
            onIdleThresholdChanged = onIdleThresholdChanged,
            onStartSessionRequested = onStartSessionRequested,
            rememberedConsentGiven = rememberedConsentGiven,
            onConsentReminderClicked = onConsentReminderClicked,
            modifier = modifier,
        )
    }
}

/** wireframe.md, "Ponto de início / Configuração da sessão", item 4 -- decisions/0045. */
@Composable
private fun ConsentReminder(consentGiven: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val status = if (consentGiven) "ativada" else "desativada"
    Text(
        text = "Identificação: $status — toque pra rever o consentimento",
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
    )
}

@Composable
private fun CompactLayout(
    startingPositionOptions: List<Int>,
    selectedStartingPosition: Int,
    onStartingPositionSelected: (Int) -> Unit,
    eventConfigs: List<EventConfigUiState>,
    onSkipAvailableChanged: (eventName: String, value: Boolean) -> Unit,
    onHintThresholdChanged: (eventName: String, value: String) -> Unit,
    onStudyThresholdChanged: (eventName: String, value: String) -> Unit,
    idleThresholdText: String,
    onIdleThresholdChanged: (String) -> Unit,
    onStartSessionRequested: () -> Unit,
    rememberedConsentGiven: Boolean? = null,
    onConsentReminderClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        if (startingPositionOptions.size > 1) {
            Text("Começar em:")
            startingPositionOptions.forEach { position ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = position == selectedStartingPosition,
                        onClick = { onStartingPositionSelected(position) },
                        modifier = Modifier.testTag("posicao-inicio-$position"),
                    )
                    Text(position.toString())
                }
            }
        }
        eventConfigs.forEach { event ->
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            // wireframe.md, "Ponto de início / Configuração da sessão (DA-RET-03/04)", leiaute
            // celular: cada bloco leva o nome do evento -- sem isso, uma sessão com mais de um
            // evento no alcance escolhido (EI-SES-08) não deixa claro qual bloco é de qual evento.
            Text(text = event.eventName, style = MaterialTheme.typography.titleMedium)
            EventConfigBlock(
                event = event,
                onSkipAvailableChanged = { onSkipAvailableChanged(event.eventName, it) },
                onHintThresholdChanged = { onHintThresholdChanged(event.eventName, it) },
                onStudyThresholdChanged = { onStudyThresholdChanged(event.eventName, it) },
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        OutlinedTextField(
            value = idleThresholdText,
            onValueChange = onIdleThresholdChanged,
            label = { Text("Tempo de ociosidade") },
            modifier = Modifier.fillMaxWidth(),
        )
        if (rememberedConsentGiven != null) {
            ConsentReminder(
                consentGiven = rememberedConsentGiven,
                onClick = onConsentReminderClicked,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        Button(
            onClick = onStartSessionRequested,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Iniciar sessão")
        }
    }
}

@Composable
private fun TabletLayout(
    startingPositionOptions: List<Int>,
    selectedStartingPosition: Int,
    onStartingPositionSelected: (Int) -> Unit,
    eventConfigs: List<EventConfigUiState>,
    selectedEventName: String?,
    onEventSelected: (String) -> Unit,
    onSkipAvailableChanged: (eventName: String, value: Boolean) -> Unit,
    onHintThresholdChanged: (eventName: String, value: String) -> Unit,
    onStudyThresholdChanged: (eventName: String, value: String) -> Unit,
    idleThresholdText: String,
    onIdleThresholdChanged: (String) -> Unit,
    onStartSessionRequested: () -> Unit,
    rememberedConsentGiven: Boolean? = null,
    onConsentReminderClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (startingPositionOptions.size > 1) {
            Text("Começar em:")
            startingPositionOptions.forEach { position ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = position == selectedStartingPosition,
                        onClick = { onStartingPositionSelected(position) },
                        modifier = Modifier.testTag("posicao-inicio-$position"),
                    )
                    Text(position.toString())
                }
            }
        }
        OutlinedTextField(
            value = idleThresholdText,
            onValueChange = onIdleThresholdChanged,
            label = { Text("Tempo de ociosidade") },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            LazyColumn(modifier = Modifier.weight(0.3f)) {
                items(eventConfigs, key = { it.eventName }) { event ->
                    Text(
                        text = event.eventName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEventSelected(event.eventName) }
                            .padding(12.dp),
                    )
                }
            }
            val selected = eventConfigs.firstOrNull { it.eventName == selectedEventName }
            if (selected != null) {
                Column(modifier = Modifier.weight(0.7f).padding(start = 16.dp)) {
                    EventConfigBlock(
                        event = selected,
                        onSkipAvailableChanged = { onSkipAvailableChanged(selected.eventName, it) },
                        onHintThresholdChanged = { onHintThresholdChanged(selected.eventName, it) },
                        onStudyThresholdChanged = { onStudyThresholdChanged(selected.eventName, it) },
                    )
                }
            }
        }
        if (rememberedConsentGiven != null) {
            ConsentReminder(consentGiven = rememberedConsentGiven, onClick = onConsentReminderClicked)
        }
        Button(onClick = onStartSessionRequested, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar sessão")
        }
    }
}

@Composable
private fun EventConfigBlock(
    event: EventConfigUiState,
    onSkipAvailableChanged: (Boolean) -> Unit,
    onHintThresholdChanged: (String) -> Unit,
    onStudyThresholdChanged: (String) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(value = event.skipAvailable, onValueChange = onSkipAvailableChanged),
        ) {
            Text("Pular disponível")
            Switch(checked = event.skipAvailable, onCheckedChange = null)
        }
        if (event.hintEnabledInContent) {
            OutlinedTextField(
                value = event.hintThresholdText,
                onValueChange = onHintThresholdChanged,
                label = { Text("Limiar de erro — dica") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = event.studyThresholdText,
                onValueChange = onStudyThresholdChanged,
                label = { Text("Limiar de erro — sugestão de estudo") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
