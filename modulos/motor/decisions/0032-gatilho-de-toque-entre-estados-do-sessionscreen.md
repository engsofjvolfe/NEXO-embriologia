# 0032 — Gatilho de toque entre estados do `SessionScreen`

Resumo em linguagem simples: quando a pessoa acerta, erra, vê uma
dica, ou termina um evento, a tela precisa de algum gesto pra avançar
pra próxima situação. Esta ADR decide, situação por situação, se esse
gesto é tocar em qualquer lugar da tela, ou se exige um botão com
nome específico — nunca por preferência solta, sempre a partir de duas
fontes reais e independentes: o padrão visual que o Google já publica
pra esse tipo de decisão, e um critério de usabilidade já usado neste
projeto.

Convenção dos códigos citados abaixo:
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.
- `RF-PUL`, `RF-DIC` — [`2 - requisitos-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/2 - requisitos-conceito-geral.md>), seções 6.6 e 6.8.
- `EI-RET`, `EI-DIC`, `EI-PUL`, `EI-ENC`, `EI-PAU` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seções 6.7, 6.8, 6.6, 6.11 e 6.12.

**Status:** proposto

**Contexto:**

[decisions/0022](0022-conteudo-do-estado-exposto-pelo-viewmodel.md) já
fixou o conteúdo exato de cada uma das oito situações da tela de jogo
(`SessionScreen`, tipo fechado) mais o pedido de saída (sinalizador à
parte, nunca uma situação própria) — mas, de propósito, sem decidir
gesto nenhum, só dado. O `ViewModel` já expõe um método por ação
prevista (`onSkipRequested`, `onScreenAcknowledged`,
`onContinueRequested`, `onExitRequested`, `onExitCancelled`,
`onExitConfirmed`, `onPauseRequested` —
[architecture.md, "Ligação com o núcleo do motor"](<../docs/architecture.md#ligação-com-o-núcleo-do-motor>)),
mas o gatilho visual exato de cada um — toque livre ou botão — segue
registrado como pendência em
[tasks.md](<../docs/tasks.md#em-aberto>) e citado do mesmo jeito em
[architecture.md, "Interface"](<../docs/architecture.md#interface>).

O Documento de Conceito, seção 8, já fixa um limite pra qualquer
desenho aqui: "a tela cumpre uma função estritamente reativa: ela
confirma, não anuncia" — o gesto escolhido não pode virar uma
explicação ou aviso que compita com esse silêncio.

Duas alternativas reais foram consideradas antes de decidir por
situação:

1. **Toque em qualquer lugar, sempre, pra toda situação.** Simples de
   implementar, mas trata do mesmo jeito um aviso sem consequência
   (por exemplo, ver que acertou) e uma ação que muda o rumo da sessão
   (por exemplo, pular uma peça, ou sair) — risco real de disparar por
   engano uma ação que não devia ser tão fácil de acionar.
2. **Botão nomeado, sempre, pra toda situação.** Nunca ambíguo, mas
   acrescenta um toque extra em situações que são só um aviso de
   passagem, sem decisão nenhuma envolvida — argumento oficial contra
   isso citado abaixo.
3. **Depende da situação: toque livre só quando não existe decisão
   real; botão nomeado quando existe escolha entre caminhos, ou uma
   ação com consequência.** (alternativa escolhida, ver Decisão)

A alternativa 3 vem de duas fontes reais, consultadas de propósito
antes de decidir, não de preferência:

- O padrão visual oficial do Google (Material Design 3) distingue dois
  componentes só por esse critério — importância e se a ação é
  obrigatória. Sobre o componente de aviso curto (*snackbar*): "Low
  priority... Optional: Snackbars disappear automatically" — prioridade
  baixa, o botão é opcional, o aviso desaparece sozinho (GOOGLE,
  [s.d.]a). Sobre o componente de caixa de decisão (*dialog*): "High
  priority... Required: Dialogs block app usage until the user takes a
  dialog action" — prioridade alta, obrigatório, trava o resto do
  aplicativo até a pessoa agir (GOOGLE, [s.d.]a). A própria página de
  diálogos reforça o mesmo limite: "Don't use dialogs for low- or
  medium-priority information. Instead use a snackbar, which can be
  dismissed or disappear automatically" — não usar caixa de decisão
  pra informação de prioridade baixa ou média; usar aviso curto, que
  pode ser dispensado ou desaparecer sozinho (GOOGLE, [s.d.]b).
- O grupo de pesquisa em usabilidade Nielsen Norman Group, já citado
  como fonte neste projeto
  ([architecture.md, Referências](<../docs/architecture.md#referências>)),
  reforça o mesmo critério do lado oposto — quando existe consequência
  real: "This solution is ideal for destructive cancel actions that
  would lose the user's work" — confirmação explícita é indicada pra
  ações que descartam algo que a pessoa já fez (NIELSEN NORMAN GROUP,
  2020). E: "eliminate the ambiguous X icon in favor of explicit
  text-labeled buttons" — texto claro num botão, nunca um gesto
  ambíguo, quando a ação tem consequência (NIELSEN NORMAN GROUP, 2020).

As duas fontes apontam na mesma direção, de lados diferentes: aviso
sem consequência real não precisa de botão; ação com consequência real
(perder algo, mudar de caminho) precisa de botão nomeado, nunca de
gesto livre.

**Decisão:**

Aplicando esse critério a cada uma das oito situações de
[decisions/0022](<0022-conteudo-do-estado-exposto-pelo-viewmodel.md>)
e ao pedido de saída:

1. **`AttemptAccepted` (confirmação de acerto), `AttemptRejected`
   (negativa) e `HintShown` (dica) — toque em qualquer lugar da tela,
   chamando `onScreenAcknowledged`.** Nenhuma das três envolve decisão
   real: são só um retrato do que aconteceu (`EI-RET-02`, `EI-RET-03`)
   ou uma dica que não obriga nada (`RF-DIC-01`) — mesmo perfil do
   componente de aviso curto que a fonte do Google descreve como não
   exigindo interação da pessoa.
2. **`StudySuggestionShown` (sugestão de estudo) — toque em qualquer
   lugar fora do botão de pular dispensa a sugestão
   (`onScreenAcknowledged`); "Pular peça" continua um botão nomeado à
   parte (`onSkipRequested`).** A sugestão em si não obriga nada
   (`RF-DIC-03`: "sem obrigar"); pular é que é a ação com consequência
   real (a peça fica perdida na sessão, `EI-PUL-04`) — por isso só ele
   precisa de botão. Não é uma tela nova: pular já é uma ação
   disponível "a qualquer momento" enquanto uma posição está aberta
   (`RF-PUL-04`), então o mesmo botão, já presente durante a espera
   normal por uma peça, continua visível aqui, sem precisar de
   controle duplicado.
3. **`EventSummary` (resumo de evento) e `SkipMessageShown` (mensagem
   de pulo) — botão nomeado, chamando `onContinueRequested`.**
   Diferente das situações do item 1, aqui a pessoa está saindo de um
   marco real da sessão — o fim de um evento, com ou sem próximo
   evento na cadeia (o campo `hasNextEvent`, já parte do estado desde
   [decisions/0022](<0022-conteudo-do-estado-exposto-pelo-viewmodel.md>),
   ponto 2, decide o que o botão faz, nunca se ele aparece). Mesmo
   critério do Google citado acima: ação com consequência real, que
   muda o rumo da sessão, exige confirmação explícita, nunca gesto
   livre.
4. **Pedido de saída (abre a confirmação) — controle nomeado, nunca
   gesto livre em cima do conteúdo da tela.** A posição exata desse
   controle na tela (canto, ícone, texto) é leiaute — a posição e o
   tamanho de cada elemento na tela —, fora do escopo desta ADR,
   decidida quando o restante das telas for desenhado (ver
   [tasks.md](<../docs/tasks.md#em-aberto>)).
5. **Confirmação de saída (`DA-RET-15`) — dois botões nomeados,
   "Cancelar" e "Sair", nunca toque fora da caixa.** Corresponde
   exatamente ao padrão de duas ações que a fonte do Google descreve
   pra caixa de decisão: "If two actions are provided, one must be a
   confirming action, and the other a dismissing action" — uma
   confirma, a outra dispensa (GOOGLE, [s.d.]b). Já é exigência da
   Especificação (`EI-PAU-03`: confirmação explícita antes de apagar o
   progresso).
6. **`Reference` (referência) e `AwaitingAttempt` (aguardando
   tentativa) ficam fora desta decisão.** As duas não avançam por
   toque — avançam quando uma peça física é lida, mecanismo já fixado
   em `EI-SES-04`/`EI-VAL-02`. O botão "Pular peça", já presente em
   `AwaitingAttempt`, segue a mesma regra do item 2 (ação com
   consequência, sempre botão nomeado).

**Consequências:**

Nenhuma mudança na API do `ViewModel` — os sete métodos já existentes
(`architecture.md, "Ligação com o núcleo do motor"`) já cobrem todas as
ações decididas acima; esta ADR só liga cada um a um gesto visual
específico, sem exigir método novo.

A posição exata de cada botão, o texto exato de cada rótulo, e a
posição do controle de saída na tela continuam fora desta decisão —
isso é leiaute (passo 2 do método já fixado em
[architecture.md, "Interface"](<../docs/architecture.md#interface>)),
resolvido quando o restante das 16 entradas de tela for desenhado, ver
[tasks.md](<../docs/tasks.md#em-aberto>).

## Referências

Fontes externas citadas no Contexto e na Decisão, no formato definido
pela norma ABNT NBR 6023 (Informação e documentação — Referências).
Citações traduzidas livremente no corpo do documento; texto original
preservado entre aspas antes da tradução. Citadas no corpo do
documento como (ENTIDADE, ano).

GOOGLE. **Snackbar**. Material Design 3, [s.d.]a. Disponível em:
https://m3.material.io/components/snackbar/guidelines. Acesso em: 29
ago. 2026.

GOOGLE. **Dialogs**. Material Design 3, [s.d.]b. Disponível em:
https://m3.material.io/components/dialogs/guidelines. Acesso em: 29
ago. 2026.

NIELSEN NORMAN GROUP. **Cancel vs Close: Design to Distinguish the
Difference**. 2020. Disponível em:
https://www.nngroup.com/articles/cancel-vs-close/. Acesso em: 29 ago.
2026.
