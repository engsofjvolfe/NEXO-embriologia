# 0026 — Forma de `SessionState`, tipos de `content` usados pelo `ViewModel`, e construtor de `SessionViewModel`

Resumo em linguagem simples: faltava anotar, em algum documento, exatamente que informação cada
"peça interna" do programa carrega — a ficha da sessão em jogo (`SessionState`), o conteúdo já lido
de um pacote (`Frame`, `ContentEvent`, `ContentTheme`, `ContentInstance`) e o que o `SessionViewModel`
recebe pra começar a funcionar. Essa falta nunca foi um espaço em branco: era uma decisão real, só
que tomada direto no código, sem nunca passar por um documento — o que este projeto não aceita. Esta
ADR preenche essa lacuna, derivando cada campo do que a Especificação e as ADRs já aceitas exigem,
nunca do código já existente.

Convenção dos códigos citados abaixo:
- `EI-VAL` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.4.
- `EI-ERR` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.5.
- `EI-PUL` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.6.
- `EI-DIC` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.8.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seções 6.9 e 6.10.
- `EI-ENC` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.11.
- `EI-PAU` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.12.
- `EI-REG` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.13.
- `EI-NAV` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.15.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já usado nas ADRs anteriores
deste módulo. O trecho abaixo marcado descreve documentação oficial do Android — não uma decisão
deste projeto, sujeita a mudar em qualquer revisão futura dela. Quem ler este documento depois deve
tratar esse conteúdo como possivelmente desatualizado e reconfirmar na fonte oficial (seção
Referências) antes de usar como base pra mudar código.

**Status:** aceito

**Contexto:**

Nenhum documento já existente decide o nível de campo que falta aqui — conferido por leitura
completa antes de escrever esta ADR, nunca abrindo o código já escrito:

- [decisions/0008](0008-representacao-do-estado-da-sessao.md) já decide o *conceito* de
  `SessionState` (retrato imutável, substituído a cada transição; registro interno de tudo que já
  aconteceu; todo número que a Especificação proíbe guardar solto é sempre derivado desse registro)
  — mas não desce a nome nem tipo de campo.
- [decisions/0013](0013-desenho-do-pacote-content.md) já fixa os *nomes* dos tipos do pacote
  `content` (`Frame`, `ContentEvent`, `ContentTheme`, `ContentInstance`) e que eles reaproveitam
  `Ordering`, `Instance`, `Theme`, `Event` de `hierarchy` — mas não lista os campos completos de cada
  um.
- [decisions/0020](0020-ligacao-entre-leitura-de-peca-e-a-tela.md) e
  [decisions/0022](0022-conteudo-do-estado-exposto-pelo-viewmodel.md) já fixam o mecanismo do
  `ViewModel` e o conteúdo do estado de *tela* (`SessionUiState`/`SessionScreen`) — nenhuma das duas
  decide o estado de *sessão* em si (`SessionState`) nem os parâmetros do construtor do
  `SessionViewModel`.

Duas tentativas de preencher essa lacuna sem uma ADR própria — uma abrindo o código já existente pra
adivinhar a resposta, outra escrevendo direto em `architecture.md` citando as ADRs acima como se
fosse desenho derivado delas — foram feitas e revertidas antes desta tarefa; a segunda tentativa
produziu, ao compilar, o mesmo erro da primeira, confirmando que era um palpite, não uma dedução.
Investigação completa em
[analysis.md#2026-08-17-lacuna-na-forma-de-sessionstate-e-dos-tipos-de-content](<../docs/analysis.md#2026-08-17-lacuna-na-forma-de-sessionstate-e-dos-tipos-de-content>).

**Os tipos de `content` são transcrição direta, sem escolha envolvida.** O contrato de dado já
aprovado, imutável ([`concept.md`, Contrato de dado](<../docs/concept.md#contrato-de-dado>)), já fixa
campo a campo o que `frame`, `event`, `theme` e `instance` carregam; só falta o nome equivalente em
Kotlin (convenção `camelCase` do idioma, já usada em toda parte deste código, nunca precisou de
decisão própria porque nunca antes um tipo Kotlin espelhou, campo a campo, um contrato com nome de
campo em `snake_case`).

**A forma de `SessionState` exige uma escolha real, sem fonte externa possível pro nome de cada
campo — mas com uma pergunta maior por trás dela, que tem.** Uma sessão pode cobrir mais de um
evento encadeado (EI-SES-06/07), então o retrato da sessão precisa saber não só "qual posição", mas
"dentro de qual evento, de uma lista de eventos já decidida no início" (EI-NAV-05: essa configuração
é feita uma única vez, antes da primeira tentativa, e não muda depois). Nenhum documento já aprovado
decide como representar isso.

Como o achado de 17/08 já confirmou (ver `analysis.md`, citado acima), não existe fonte externa pra
decidir *o nome* de um campo de uma classe Kotlin já pensada por este projeto — mas existe fonte
externa pra decidir *a forma geral*: reunir tudo num objeto só, ou espalhar em vários pedaços. Pra
essa pergunta, o guia oficial de arquitetura do Android sobre a camada de interface, consultado agora
pela primeira vez nesta tarefa (não citado em nenhuma tentativa anterior), tem posição declarada.
`[REVISAR-EXTERNO]` Em tradução livre: "use um único objeto de estado de interface pra lidar com
estados relacionados entre si — isso leva a menos inconsistências e deixa o código mais fácil de
entender" (GOOGLE, [s.d.]). Essa fonte não decide nome de campo nenhum — só reforça, de fora, a
mesma direção que `decisions/0008` já tinha tomado (um retrato único, não vários), agora estendida
pro aspecto novo que `decisions/0008` não cobria (mais de um evento na mesma sessão).

**Decisão:**

1. **Tipos do pacote `content` usados pelo `ViewModel`** — transcrição campo a campo do contrato de
   dado, com `ordering`+`position` do JSON reunidos no único campo `ordering: Ordering`, reaproveitando
   o tipo que [decisions/0007](0007-desenho-do-pacote-hierarchy.md) já criou (a mesma razão de lá
   vale aqui: torna a combinação inválida, ordem sem posição ou avulso com posição, irrepresentável
   em código, em vez de checada em tempo de execução):

   ```kotlin
   data class Frame(
       val tagId: String,
       val image: String,
       val confirmationText: String?,
       val summaryFragment: String,
   )

   data class ContentEvent(
       val name: String,
       val ordering: Ordering,
       val zeroMarkImage: String,
       val hintEnabled: Boolean,
       val hintContent: String?,
       val frames: List<Frame>,
   )

   data class ContentTheme(
       val name: String,
       val ordering: Ordering,
       val events: List<ContentEvent>,
   )

   data class ContentInstance(
       val name: String,
       val retentionPeriod: String,
       val themes: List<ContentTheme>,
   )
   ```

2. **`SessionState` carrega, direto, a posição da sessão dentro de uma lista de eventos já resolvida
   no início — nunca recalculada durante o jogo:**

   ```kotlin
   data class SessionState(
       val sessionEvents: List<String>,
       val currentEventIndex: Int,
       val expectedPosition: Int,
       val paused: Boolean,
       val log: List<SessionEvent>,
   )
   ```

   - `sessionEvents`: nome de cada evento que faz parte desta sessão, na ordem em que a sessão os
     percorre — calculado uma única vez, no momento em que a sessão é configurada (EI-NAV-05), a
     partir do que [decisions/0009](0009-calculo-do-recorte-continuo-de-sessao.md) já resolve
     (`sessionScope`). Depois de calculada, essa lista nunca muda — segue a mesma regra que já vale
     pra disponibilidade de pular e pros limiares de dica (decidida uma vez, fixa durante toda a
     sessão).
   - `currentEventIndex`: posição, dentro de `sessionEvents`, do evento em curso agora. Avança só
     por `continueToNextEvent` (já listada na API pública de `SessionTransitions.kt`,
     `architecture.md`).
   - `expectedPosition`: a "posição esperada" que EI-VAL-01 exige existir sempre, dentro do evento em
     curso — nome do campo é o próprio termo que a Especificação já usa, não um termo inventado.
     Avança tanto num acerto (`recordAttempt`) quanto num pulo (`skipPosition`) — os dois preenchem a
     posição, na definição de EI-VAL-01.
   - `paused: Boolean`: cobre pausa e ociosidade juntas, como o Conceito já define ("a única diferença
     entre as duas é o que dispara cada uma", seção 12) — qual das duas ocorreu fica só no registro
     (`SessionEvent.Paused` ou `SessionEvent.WentIdle`), nunca duplicado como um segundo campo direto.
   - `log`: o registro interno já decidido em `decisions/0008`, ponto 2 — lista de `SessionEvent`.
     `errorCount(event)` e `consecutiveAttempts(position)` continuam funções derivadas dele, nunca
     campos (já fixado em `decisions/0008`, ponto 3, e já refletido na API pública listada em
     `architecture.md`).

3. **Cada uma das sete variantes de `SessionEvent` (já nomeadas em `architecture.md`) carrega o mesmo
   trio de campos — evento, posição, horário —, exigido por EI-REG-01 pra todo fato registrado, sem
   exceção, inclusive tentativa rejeitada:**

   ```kotlin
   sealed interface SessionEvent {
       val eventName: String
       val position: Int
       val timestamp: Long

       data class AttemptAccepted(override val eventName: String, override val position: Int, override val timestamp: Long) : SessionEvent
       data class AttemptRejected(override val eventName: String, override val position: Int, override val timestamp: Long) : SessionEvent
       data class HintUsed(override val eventName: String, override val position: Int, override val timestamp: Long) : SessionEvent
       data class StudySuggestionShown(override val eventName: String, override val position: Int, override val timestamp: Long) : SessionEvent
       data class PositionSkipped(override val eventName: String, override val position: Int, override val timestamp: Long) : SessionEvent
       data class Paused(override val eventName: String, override val position: Int, override val timestamp: Long) : SessionEvent
       data class WentIdle(override val eventName: String, override val position: Int, override val timestamp: Long) : SessionEvent
   }
   ```

4. **Construtor de `SessionViewModel`:** recebe só dado simples e referências que não morrem com a
   tela — nunca `Context`, `Service` ou a tela em si, limite já fixado em `decisions/0020`.

   ```kotlin
   class SessionViewModel(
       initialState: SessionState,
       private val content: ContentInstance,
       private val configuration: SessionConfiguration,
       private val stateFile: File,
   )
   ```

   - `initialState`: primeiro retrato da sessão, já montado por quem inicia (tela de configuração de
     sessão, EI-NAV-05) — construção direta do `data class SessionState`, sem função própria (mesma
     categoria de "vira código direto, sem exigir ADR" já usada em `decisions/0022` pra
     `referenceImage`).
   - `content`: a instância já importada e validada por `content` (ver `decisions/0013`) — é aqui que
     o `ViewModel` busca qual posição um `tag_id` lido corresponde, exatamente como `decisions/0020`,
     ponto 4, já descreve.
   - `configuration`: `SessionConfiguration` (já existente em `core/report`, ver `architecture.md`,
     pacote `report`) — o mesmo tipo que o `ViewModel` já usa hoje pro `idleThresholdMillis` do
     gatilho de ociosidade (`decisions/0024`); os limiares de dica/sugestão de estudo e a
     disponibilidade de pular, por evento, entram como parâmetro direto (primitivo) em cada função de
     transição que precisa deles (`hintAvailable(state, position, hintThreshold)`, por exemplo) —
     nunca guardados como campo do próprio `SessionState`, mesma lógica de parâmetro explícito já
     usada em `recordAttempt`/`expectedTagId`. Isso evita que `session` passe a depender de `report`
     (que já depende de `session`, pelo registro `SessionEvent`) — uma dependência nos dois sentidos
     entre os mesmos dois pacotes.
   - `stateFile`: o arquivo onde `SessionStatePersistence` (`decisions/0010`) grava e lê o estado
     pausado — caminho de verdade (`context.filesDir`) decidido por `app`, nunca por `core`, mesma
     fronteira que `decisions/0008` já registrava como pendência do `core`.

**Consequências:**

`core/session/SessionState.kt` e `core/content/Content.kt` passam a ter forma fechada, documentada
antes do código — qualquer divergência entre o código já existente e o que esta ADR decide é corrigida
no código, nunca nesta ADR (mesma regra geral de `modulos/README.md`, Como navegar). A pendência
"Escrever o teste de `SessionViewModel.kt`" deixa de estar bloqueada.

Fica de fora desta decisão, registrado só como pendência nova: o algoritmo exato que resolve
`sessionEvents` quando uma sessão atravessa mais de um tema — `sessionScope` (decisions/0009) calcula
o recorte contíguo de um grupo só (temas de uma instância, ou eventos de um tema), mas nenhum
documento ainda decide como combinar um recorte de temas com o recorte de eventos de cada tema dentro
dele numa lista única e plana. Este ponto não precisa de resposta pra fechar a forma de `SessionState`
em si (que só exige a lista já pronta, qualquer que seja o jeito de montá-la) — fica pra ADR própria,
apontada em `tasks.md`.

**Nota de acompanhamento (18-08-2026):**

*Resumo simples:* ao escrever o teste de `SessionViewModel.kt` só a partir do que esta ADR
documenta, sem abrir código, duas partes da decisão 2 e 4 não bateram com o código real. A
decisão 2 (forma de `SessionState`) estava errada de verdade — corrigida por
[decisions/0027](0027-sessionstate-referencia-o-evento-atual-pelo-nome.md), que substitui só essa
parte. A decisão 4 (construtor do `ViewModel`) tinha três diferenças de nome sem princípio de
desenho concorrente por trás — corrigidas aqui, como correção factual, sem precisar de ADR
própria.

*Detalhe técnico:*
- **Decisão 2 (`SessionState`): substituída por
  [decisions/0027](0027-sessionstate-referencia-o-evento-atual-pelo-nome.md).** O campo
  `sessionEvents: List<String>` + `currentEventIndex: Int` duplicava a lista de eventos que já
  mora em `SessionConfiguration.eventNames` — o código já existente usa só
  `expectedEventName: String`, forma que `decisions/0027` explica e adota.
- **Decisão 4 (construtor de `SessionViewModel`): três nomes corrigidos, mesmo formato geral.**
  O parâmetro `content: ContentInstance` chama-se, no código, `instance`; `stateFile: File`
  (obrigatório) é, no código, `pausedStateFile: File? = null` (opcional, permite montar o
  `ViewModel` sem persistência, por exemplo pra teste ou pré-visualização); existe um quinto
  parâmetro, `now: () -> Long = { System.currentTimeMillis() }`, que fornece o horário de cada
  evento do registro — mesma lógica de injeção explícita já usada em toda parte de `session`
  (o `ViewModel` nunca lê relógio de sistema escondido dentro de uma função sem receber essa
  possibilidade como parâmetro trocável). Nenhuma das três é uma escolha de desenho concorrente
  com alguma alternativa considerada nesta ADR — por isso entram como correção factual, não como
  ADR nova.

## Referências

Fonte externa citada no Contexto, no formato definido pela norma ABNT NBR 6023 (Informação e
documentação — Referências). Citada no corpo do documento como (GOOGLE, ano).

GOOGLE. **UI layer**. Android Developers, [s.d.]. Disponível em:
https://developer.android.com/topic/architecture/ui-layer. Acesso em: 18 ago. 2026.
