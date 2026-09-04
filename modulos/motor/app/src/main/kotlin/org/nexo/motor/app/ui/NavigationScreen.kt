package org.nexo.motor.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Recuo horizontal somado por nível de profundidade dentro do acordeão de navegação. */
private val NavigationEntryIndentStep = 16.dp

/**
 * Uma linha visível do acordeão de navegação (instância, tema ou evento) — dado já achatado,
 * pronto pra desenhar; a lógica que converte a hierarquia real (core/hierarchy) e o estado de
 * expandido/recolhido de cada nível nessa lista fica fora deste Composable (decisions/0034,
 * Consequências: "a escrita de verdade... fica para a implementação").
 */
data class NavigationEntry(
    val id: String,
    val label: String,
    val depth: Int,
    val expandable: Boolean,
    val expanded: Boolean,
)

/**
 * DA-RET-02 (Navegação). Acordeão (decisions/0030) numa única LazyColumn achatada
 * (decisions/0034), com campo de busca fixo no topo (DA-NAV-02). Tocar numa entrada expande ou
 * recolhe o nível seguinte, ou abre a escolha de alcance da sessão -- decidido por quem chama,
 * não por este Composable.
 */
@Composable
fun NavigationScreen(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    entries: List<NavigationEntry>,
    onEntryClicked: (NavigationEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            label = { Text("Buscar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        LazyColumn {
            items(entries, key = { it.id }) { entry ->
                Text(
                    text = entry.label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEntryClicked(entry) }
                        .padding(
                            start = NavigationEntryIndentStep + NavigationEntryIndentStep * entry.depth,
                            top = 12.dp,
                            bottom = 12.dp,
                            end = 16.dp,
                        ),
                )
            }
        }
    }
}
