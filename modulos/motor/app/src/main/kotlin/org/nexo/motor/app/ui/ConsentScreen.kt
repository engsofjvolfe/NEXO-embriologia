package org.nexo.motor.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * DA-RET-17 (Consentimento). Só aparece antes de registrar dado que identifique a pessoa
 * (EI-REG-03) -- o texto legal exato fica fora do escopo da cascata do motor. O botão
 * "Continuar" fica desabilitado até a caixa "Li e concordo" ser marcada.
 */
@Composable
fun ConsentScreen(
    consentText: String,
    onContinueRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var agreed by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(text = consentText)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.toggleable(value = agreed, onValueChange = { agreed = it }),
        ) {
            Checkbox(checked = agreed, onCheckedChange = null)
            Text("Li e concordo")
        }
        Button(
            onClick = onContinueRequested,
            enabled = agreed,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continuar")
        }
    }
}
