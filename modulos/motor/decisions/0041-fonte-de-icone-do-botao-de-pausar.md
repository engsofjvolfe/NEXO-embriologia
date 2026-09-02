# 0041 — Fonte de ícone do botão de pausar (revisão de decisions/0039)

Resumo em linguagem simples: [decisions/0039](0039-fonte-de-icone-dos-controles-de-tela.md) tinha
decidido usar só texto, sem ícone, em qualquer controle de tela que ainda não tivesse um. Esta ADR
revisa a decisão anterior: o botão de pausar passa a usar um ícone oficial (duas barras verticais),
com um rótulo de acessibilidade ("Pausar") no lugar do texto visível.

**Status:** aceito — substitui [decisions/0039](0039-fonte-de-icone-dos-controles-de-tela.md) para o
botão de pausar; a razão original de 0039 continua valendo para qualquer outro controle que, no
futuro, precise de ícone sem que o wireframe já exija isso hoje.

**Contexto:**

O rótulo de acessibilidade decidido no ponto 2, abaixo, atende à mesma exigência de acessibilidade
já citada em [decisions/0035](0035-sistema-visual-cor-tipografia-forma-contraste.md) (contraste no
nível WCAG AA, fonte GOOGLE, **Make apps more accessible**) — aquele ADR trata de contraste de cor;
este trata de rótulo pra leitor de tela, categoria diferente dentro do mesmo requisito geral de
acessibilidade, sem exigir fonte própria nova.

O wireframe do módulo ([`design/wireframe.md`](<../design/wireframe.md#tela-de-jogo--posição-dos-elementos-comuns>))
pede um ícone, sem texto, especificamente para o botão de pausar — os demais controles de tela
(Sair, Retomar, Iniciar sessão, etc.) usam texto por escolha comum do Material Design, não por
falta de ícone disponível, e continuam fora do escopo desta ADR.

Investigação completa (fonte encontrada, verificada e testada) em
[analysis.md](<../docs/analysis.md#2026-09-01-fonte-de-icone-oficial-via-repositorio-github-do-google>) —
não repetida aqui.

**Decisão:**

1. O ícone do botão de pausar (duas barras verticais, "Pause" no catálogo do Google) vem direto do
   repositório oficial `github.com/google/material-design-icons` (Apache License 2.0), arquivo
   `symbols/android/pause/materialsymbolsoutlined/pause_24px.xml`, guardado sem alteração em
   `app/src/main/res/drawable/ic_pause_24.xml`.
2. `SessionGameScreen.kt` troca o botão de texto ("Pausar") por um `IconButton` com esse ícone,
   `contentDescription = "Pausar"` — cobre a mesma informação que o texto visível cobria antes,
   agora pra ferramenta de acessibilidade (leitor de tela), não pra quem enxerga a tela.
3. Nenhuma biblioteca nova declarada em `gradle/libs.versions.toml` — o arquivo é um recurso
   (`drawable`), não uma dependência de código; Compose já sabe desenhar um vetor de recurso
   (`painterResource`), sem exigir nada além do que o módulo `app` já tem.
4. Continua valendo, sem mudança, a parte de [decisions/0039](0039-fonte-de-icone-dos-controles-de-tela.md)
   sobre os demais controles de tela: nenhum deles tem, hoje, uma exigência de ícone-sem-texto no
   wireframe, então continuam usando texto — essa ADR nova não força ícone em nenhum controle além
   do de pausar.

**Consequências:**

`SessionGameScreenTest.kt` passa a localizar o controle de pausar pela descrição de acessibilidade
(`onNodeWithContentDescription("Pausar")`), não mais pelo texto visível. Fecha a pendência
"Decidir a fonte de ícone dos controles de tela", registrada em `tasks.md`. Revisável de novo, sem
quebrar nada, se um controle novo precisar de ícone-sem-texto no futuro — o caminho (baixar do
mesmo repositório oficial, guardar como `drawable`) já fica registrado aqui, pronto pra reaproveitar.

## Referências

Fontes externas consultadas para embasar esta decisão, no formato definido pela norma ABNT NBR
6023 (Informação e documentação — Referências). Citadas no corpo do documento como (GOOGLE, ano).

GOOGLE. **material-design-icons — LICENSE**. GitHub, [s.d.]a. Disponível em:
https://github.com/google/material-design-icons/blob/master/LICENSE. Acesso em: 01 set. 2026.

GOOGLE. **material-design-icons — README**. GitHub, [s.d.]b. Disponível em:
https://github.com/google/material-design-icons/blob/master/README.md. Acesso em: 01 set. 2026.
