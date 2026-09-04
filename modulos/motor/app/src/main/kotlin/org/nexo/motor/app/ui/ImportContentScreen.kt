package org.nexo.motor.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Resultado de uma tentativa de importação, tal como a tela de Importar conteúdo (DA-RET-16)
 * precisa mostrar -- espelha, do lado da interface, a regra tudo ou nada já decidida em
 * decisions/0013 (core/content): ou o pacote inteiro é aceito, ou vem a lista completa do que
 * foi recusado (DA-CFG-03), nunca os dois ao mesmo tempo.
 */
sealed interface ImportScreenResult {
    data object Accepted : ImportScreenResult
    data class Rejected(val violations: List<String>) : ImportScreenResult
}

/**
 * DA-RET-16 (Importar conteúdo). O seletor de arquivo em si é o padrão do sistema Android
 * (DA-IMP-04) -- fora do controle deste Composable; quem chama decide como abri-lo e como chegar
 * a um [ImportScreenResult] (usando `importContentPackage`, já escrito em core/content).
 */
@Composable
fun ImportContentScreen(
    result: ImportScreenResult?,
    onSelectFileRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Button(onClick = onSelectFileRequested, modifier = Modifier.fillMaxWidth()) {
            Text("Selecionar arquivo (.zip)")
        }
        when (result) {
            null -> Unit
            is ImportScreenResult.Accepted ->
                Text(text = "Pacote aceito", modifier = Modifier.padding(top = 16.dp))
            is ImportScreenResult.Rejected -> LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(result.violations) { violation ->
                    Text(text = violation, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
