package org.nexo.motor.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * DA-RET-14 (Resultado / relatório). Mostrada ao final de toda sessão, e continua acessível
 * depois, fora de sessão, sem login (EI-REG-06). Os três números vêm do registro já acumulado;
 * as três ações de exportar/compartilhar chamam código já escrito e testado em core/report e
 * app/report -- este Composable só encaminha o toque, nunca decide o conteúdo do relatório.
 */
@Composable
fun ResultScreen(
    errorCount: Int,
    skipCount: Int,
    pauseCount: Int,
    onExportCsv: () -> Unit,
    onExportPdf: () -> Unit,
    onShare: () -> Unit,
    onBackToNavigation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text("Erros: $errorCount")
                Text("Pulos: $skipCount")
                Text("Pausas: $pauseCount")
            }
            OutlinedButton(onClick = onExportCsv, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("Exportar CSV")
            }
            OutlinedButton(onClick = onExportPdf, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("Exportar PDF")
            }
            OutlinedButton(onClick = onShare, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("Compartilhar")
            }
        }
        Button(onClick = onBackToNavigation, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar à navegação")
        }
    }
}
