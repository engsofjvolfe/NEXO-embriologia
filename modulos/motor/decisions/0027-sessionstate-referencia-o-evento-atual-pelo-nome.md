# 0027 — `SessionState` referencia o evento atual pelo nome, não guarda a lista da sessão duplicada

Resumo em linguagem simples: [decisions/0026](0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md)
tinha decidido que `SessionState` guardaria a lista inteira de eventos da sessão
(`sessionEvents`) mais um índice (`currentEventIndex`). Testando de verdade contra o código já
escrito, ficou claro que isso duplica um dado que já mora em outro lugar — a lista de eventos já
configurados pra sessão, dentro de `SessionConfiguration`. Esta ADR substitui só esse pedaço da
decisão anterior: `SessionState` guarda só o nome do evento em curso agora
(`expectedEventName`), e quem avança pro próximo evento já entrega o próximo nome, tirado da
lista que já existe — sem duplicar informação em dois lugares.

Convenção dos códigos citados abaixo:
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seções 6.9 e 6.10.
- `EI-NAV` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.15.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já usado nas ADRs anteriores
deste módulo. O trecho abaixo marcado descreve documentação oficial do Android — não uma decisão
deste projeto, sujeita a mudar em qualquer revisão futura dela.

**Status:** aceito

**Contexto:**

[decisions/0026](0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md),
decisão 2, definia `SessionState` com `sessionEvents: List<String>` e `currentEventIndex: Int`,
justificado pela necessidade de a sessão saber "dentro de qual evento, de uma lista de eventos já
decidida no início" (EI-NAV-05).

Ao escrever o teste de `SessionViewModel.kt` a partir só dessa forma documentada — sem abrir
nenhum arquivo de código em nenhum momento, mesma disciplina já registrada em `analysis.md` — o
teste não compilou contra o código real: o compilador apontou que `SessionState` já usa um campo
`expectedEventName: String`, sem `sessionEvents` nem `currentEventIndex`.

Comparando os dois desenhos: a forma já escrita no código não duplica a lista de eventos da
sessão — essa lista já existe, completa e ordenada, em `SessionConfiguration.eventNames`
(`core/report`, já usada pelo `ViewModel` pro `idleThresholdMillis`, `decisions/0024`).
`continueToNextEvent(state, nextEventName)` já recebe o próximo nome como parâmetro explícito,
mesmo padrão já usado em `recordAttempt`/`expectedTagId` — e o mesmo padrão que
`decisions/0026`, decisão 4, já tinha escolhido pros limiares de dica e de pular (nunca
duplicados dentro de `SessionState`, exatamente pra evitar duas fontes divergentes do mesmo
dado). A forma anterior contrariava esse próprio princípio, aplicado de forma inconsistente.

`[REVISAR-EXTERNO]` Fonte externa, consultada de novo pra este ponto específico (não a mesma
citação genérica reaproveitada da ADR anterior): o guia oficial de arquitetura do Android define
o princípio de "fonte única de verdade" (*Single Source of Truth*, SSOT). Em tradução livre:
"quando um tipo de dado novo é definido no seu aplicativo, atribua uma única fonte de verdade a
ele. A fonte única é a dona daquele dado, e só ela pode modificá-lo" — com os benefícios
"centraliza toda mudança de um tipo de dado num lugar só" e "deixa as mudanças no dado mais
rastreáveis, o que facilita achar erro" (GOOGLE, [s.d.]). `decisions/0026` já citava o guia
oficial de arquitetura do Android, mas por outro princípio dele (juntar campos relacionados num
objeto só) — usado ali pra justificar, sem perceber, o oposto do que este princípio mais
específico (fonte única, sem duplicar o mesmo dado em dois lugares) recomenda aqui. É uma
correção de raciocínio sobre a mesma família de fonte, não uma leitura nova por acaso.

**Decisão:**

1. **`SessionState` não guarda mais `sessionEvents` nem `currentEventIndex` — guarda
   `expectedEventName: String`, o nome do evento em curso agora:**

   ```kotlin
   data class SessionState(
       val expectedEventName: String,
       val expectedPosition: Int,
       val paused: Boolean,
       val log: List<SessionEvent>,
   )
   ```

2. **Avançar pro próximo evento (`continueToNextEvent`) recebe o próximo nome como parâmetro
   explícito** — quem chama (o `ViewModel`) já busca esse nome em `configuration.eventNames`, a
   mesma lista que `SessionConfiguration` já expõe, nunca duplicada dentro de `session`.

3. **O restante de `decisions/0026` continua valendo, sem mudança** — os tipos de `content`
   (decisão 1), as sete variantes de `SessionEvent` com `eventName`/`position`/`timestamp`
   (decisão 3), e a escolha de passar configuração como parâmetro explícito em vez de campo do
   retrato (decisão 4, parte da justificativa) — só o campo que representava "dentro de qual
   evento" estava errado.

**Consequências:**

`decisions/0026` recebe nota de acompanhamento marcando a decisão 2 como substituída por esta
ADR — sem apagar o restante dela, que continua correto. `architecture.md`, seção do pacote
`session`, corrigida.

Nenhuma mudança de código é necessária neste ponto específico — o código já estava certo; só o
teste (escrito a partir da forma anterior, incorreta, de `decisions/0026`) precisa ser corrigido
pra usar `expectedEventName`.

## Referências

Fonte externa citada no Contexto, no formato definido pela norma ABNT NBR 6023 (Informação e
documentação — Referências). Citada no corpo do documento como (GOOGLE, ano).

GOOGLE. **App architecture — Guide to app architecture**. Android Developers, [s.d.]. Disponível
em: https://developer.android.com/topic/architecture. Acesso em: 18 ago. 2026.
