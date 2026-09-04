package org.nexo.motor.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * DA-RET-01 (Sessão pausada). Única tela quando existe sessão pausada -- bloqueia qualquer
 * sessão nova até a pessoa escolher (EI-NAV-01). Nenhum outro elemento na tela (RF-PAU-05).
 */
@Composable
fun PausedSessionScreen(
    eventName: String,
    onResumeRequested: () -> Unit,
    onExitRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = eventName)
        Button(onClick = onResumeRequested, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
            Text("Retomar")
        }
        OutlinedButton(onClick = onExitRequested, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Sair da sessão")
        }
    }
}
