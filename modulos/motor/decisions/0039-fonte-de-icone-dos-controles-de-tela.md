# 0039 — Fonte de ícone dos controles de tela

Resumo em linguagem simples: o wireframe pede ícone, sem texto, pra alguns controles (o botão de
pausar, por exemplo). Faltava decidir de onde esse ícone vem. Esta ADR decide: rótulo de texto no
lugar de ícone, em todo controle que hoje pediria um — sem pipeline de recurso visual novo, sem
mudar o que cada controle faz, só a forma de mostrar.

**Status:** substituído pelo [ADR-0041](0041-fonte-de-icone-do-botao-de-pausar.md) para o botão de
pausar — continua valendo, sem mudança, pra qualquer outro controle sem exigência de ícone no
wireframe hoje (ver nota de acompanhamento abaixo).

**Contexto:**

Achado durante a escrita da tela de jogo (`SessionGameScreen.kt`, decisions/0037), com a
investigação completa (fonte oficial consultada, trecho literal) em
[analysis.md](<../docs/analysis.md#2026-09-01-escrita-da-sessiongamescreen-revela-tres-lacunas>):
[`design/wireframe.md`](<../design/wireframe.md#tela-de-jogo--posição-dos-elementos-comuns>) pede
"ícone (sem texto)" pro controle de pausar, mas a biblioteca clássica de ícones do Compose
(`androidx.compose.material:material-icons-core`/`-extended`) não é mais recomendada pelo próprio
Google pra aplicativo novo — o caminho recomendado no lugar dela (baixar ícone específico do
Google Fonts Icons como recurso `.xml`) exige um processo de importação de recurso que nenhum
documento deste módulo descreve ainda.

Três alternativas reais:

1. **Biblioteca clássica de ícones do Compose** (`material-icons-core`/`-extended`) — descartada
   direto pela própria fonte oficial, que desaconselha o uso em aplicativo novo.
2. **Ícone avulso baixado do Google Fonts Icons, importado como recurso `.xml`** — resolveria o
   pedido literal do wireframe, mas exige decidir, pra cada ícone necessário, qual variante exata
   baixar, e criar um processo de importação de recurso — trabalho novo, sem necessidade real
   comprovada até agora (só um controle, "pausar", pede ícone hoje).
3. **Rótulo de texto, no lugar do ícone** (alternativa escolhida, ver Decisão).

**Decisão:**

1. Todo controle de tela que hoje pediria um ícone sem texto (o botão de pausar, ver wireframe)
   usa rótulo de texto curto em vez disso — mesmo componente (`TextButton`, já usado pro controle
   de "Sair", ao lado) e mesmo comportamento já fechado noutro documento (`onPauseRequested`, sem
   confirmação — `EI-PAU-03`), só a forma visual muda.
2. Indicadores que já usam caractere de texto simples, sem depender de nenhuma biblioteca de
   ícone (o indicador de conexão do acessório — "● conectado"/"◐ procurando"/"○ desconectado",
   já decidido em `wireframe.md`) continuam como estão — não são o assunto desta ADR, que trata
   só de controle tocável sem rótulo hoje.
3. Revisável, sem quebrar nenhum código já escrito, se uma validação com pessoas reais mostrar
   que a falta de ícone atrapalha o reconhecimento do controle — a troca, se vier a acontecer,
   é só de aparência (texto → ícone importado), nunca de comportamento.

**Consequências:**

Fecha a pendência "Decidir a fonte de ícone dos controles de tela" em `tasks.md`. Nenhum código
precisa mudar — o botão de pausar em `SessionGameScreen.kt` já usava rótulo de texto antes desta
ADR existir; esta decisão só formaliza, com fonte e alternativas comparadas, uma escolha que já
estava em uso. Nenhuma dependência nova de ícone entra no projeto.

## Referências

Fonte externa consultada para embasar esta decisão, no formato definido pela norma ABNT NBR 6023
(Informação e documentação — Referências). Citada no corpo do documento como (GOOGLE, ano).

GOOGLE. **Vector graphics**. Android Developers — Jetpack Compose, [s.d.]. Disponível em:
https://developer.android.com/develop/ui/compose/graphics/images/material. Acesso em: 01 set.
2026.
