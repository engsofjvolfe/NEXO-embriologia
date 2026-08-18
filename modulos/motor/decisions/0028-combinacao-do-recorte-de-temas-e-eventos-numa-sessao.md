# 0028 — Combinação do recorte de temas e de eventos numa sessão que atravessa mais de um tema

Resumo em linguagem simples: uma sessão pode atravessar mais de um tema, não só mais de um evento
dentro do mesmo tema — isso já é regra aprovada desde o primeiro documento da cascata. O que
faltava era o mecanismo em código que transforma essa regra numa lista única e plana de nomes de
evento, do início ao fim da sessão, prevendo os dois níveis (tema e evento) juntos. Esta ADR decide
esse mecanismo: a mesma regra de "nunca pular item no meio de um grupo" já usada em
`decisions/0009` pra um nível só, aplicada agora duas vezes, uma dentro da outra.

Convenção dos códigos citados abaixo:
- `EI-HIE` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.1.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.10.
- `EI-NAV` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.15.

**Status:** aceito

**Contexto:**

Achado durante [decisions/0026](0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md):
`SessionConfiguration.eventNames` (`core/report`), que `SessionState`/`SessionViewModel` já
presumem pronta e plana (ver [decisions/0027](0027-sessionstate-referencia-o-evento-atual-pelo-nome.md)),
não tinha, em nenhum documento, a regra de como é montada quando a sessão atravessa mais de um
tema. [decisions/0009](0009-calculo-do-recorte-continuo-de-sessao.md) já resolve o recorte
contíguo dentro de **um** grupo só — temas de uma instância, ou eventos de um tema —, mas nunca os
dois níveis combinados na mesma sessão.

O texto já aprovado, lido de novo com atenção antes de decidir qualquer coisa, já resolve o
comportamento — falta só o mecanismo:

- Documento de Conceito, seção 10: "um recorte contíguo de eventos dentro do mesmo tema — nunca
  alternado: de um conjunto de eventos 1, 2 e 3, é possível jogar 1-2, 1-2-3 ou 2-3, **mas nunca 1
  e 3 sem o 2**; um recorte contíguo de temas encadeáveis dentro da mesma instância, seguindo **a
  mesma regra de contiguidade um nível acima** — é possível **atravessar um tema inteiro** e
  continuar no próximo, ou parar num subconjunto contíguo de temas".
- EI-SES-07 repete a mesma frase ("podendo atravessar um tema inteiro e seguir para o próximo do
  grupo"), em documento diferente — não é acaso, é a mesma regra confirmada duas vezes.

A frase "nunca alternado... mas nunca 1 e 3 sem o 2" e a frase "atravessar um tema inteiro" não são
duas regras — são a mesma regra recursiva, aplicada duas vezes: uma sessão é sempre um caminho
contínuo, de um ponto de início (um evento, dentro de um tema) até um ponto de fim (outro evento,
dentro de outro tema, ou do mesmo), andando peça por peça na ordem que `hierarchy` já garante. Um
tema que a sessão apenas atravessa — sem terminar nele — precisa ter ido até o fim antes de seguir
pro próximo, porque não existe, em lugar nenhum do texto aprovado, uma forma de deixar parte de um
tema pra trás e continuar adiante sem tê-lo esgotado; isso seria exatamente a mesma violação de "1
e 3 sem o 2", só que um nível acima. "Atravessar um tema inteiro" não é uma regra própria — é
consequência direta de nunca poder atravessar sem terminar.

**Caso fora do alcance desta ADR, já resolvido em outro lugar:** um tema ou evento declarado
avulso nunca participa de recorte nenhum, sozinho ou combinado — `decisions/0009`, decisão 2, já
fixa isso ("escolher um item avulso como ponto de entrada limita o recorte a esse item sozinho, sem
cálculo"). Um tema "atravessado por inteiro" nesta ADR quer dizer todos os seus eventos **com
ordem** — um evento avulso dentro de um tema atravessado nunca entra na sessão, mesma regra de
`decisions/0009` aplicada dentro de cada tema, sem exceção nova.

**Decisão:**

1. **Uma nova função, no mesmo arquivo de `sessionScope` (`SessionScope.kt`), calcula a lista
   plana de nomes de evento combinando os dois níveis — sem depender do pacote `content`,
   mantendo a mesma regra de desacoplamento já usada em todo `core/session` (RNF-MOD-01,
   `decisions/0003`; `session` não conhece `content`):**

   ```kotlin
   fun <Theme, Event> sessionEventNames(
       themes: List<Theme>,
       themeOrdering: (Theme) -> Ordering,
       eventsOf: (Theme) -> List<Event>,
       eventOrdering: (Event) -> Ordering,
       eventName: (Event) -> String,
       fromTheme: Theme,
       fromEvent: Event,
       untilTheme: Theme,
       untilEvent: Event,
   ): List<String>
   ```

   Quem chama (o `ViewModel`, que já conhece `content`) entrega `ContentTheme`/`ContentEvent` como
   `Theme`/`Event`, com `ContentTheme::ordering`, `ContentTheme::events`, `ContentEvent::ordering`
   e `ContentEvent::name` como as funções de acesso — mesmo padrão genérico já usado em
   `sessionScope<T>` e no pacote `search` (RNF-MOD-01).

2. **A função reaproveita `sessionScope` duas vezes, nunca reimplementa a lógica de recorte.**
   Alternativa real considerada e descartada: escrever um algoritmo novo, único, que percorra
   tema e evento juntos numa passada só, sem chamar `sessionScope`. Descartada porque duplicaria,
   num segundo lugar, a mesma lógica de "separar só os itens com ordem, andar da posição de início
   até a de fim, sem pular nada" que `sessionScope` já implementa e já tem teste próprio — exigiria
   manter a mesma regra em dois lugares, com risco real de as duas cópias divergirem depois de uma
   correção futura, sem nenhum ganho em troca (nenhum requisito pede um algoritmo numa passada só).
   Primeiro, `sessionScope` é chamado em `themes`, com `themeOrdering`, de `fromTheme` até
   `untilTheme`, obtendo a lista de temas envolvidos, na ordem. Depois, para cada tema dessa lista,
   `sessionScope` é chamado de novo sobre `eventsOf(tema)`, com `eventOrdering`, decidindo o
   `from`/`until` conforme a posição do tema na lista:
   - Se o tema é ao mesmo tempo o primeiro e o último da lista (sessão dentro de um tema só):
     `from = fromEvent`, `until = untilEvent`.
   - Se o tema é só o primeiro: `from = fromEvent`, `until` = o último evento com ordem daquele
     tema.
   - Se o tema é só o último: `from` = o primeiro evento com ordem daquele tema, `until = untilEvent`.
   - Qualquer outro tema da lista (nem primeiro, nem último): `from`/`until` cobrem todos os
     eventos com ordem daquele tema — nunca uma regra própria, só o resultado de nenhum dos dois
     pontos escolhidos (início ou fim da sessão) cair dentro dele.

   O resultado de cada chamada de tema é mapeado por `eventName` e concatenado, na ordem, numa
   única `List<String>`.

**Consequências:**

`core/session/SessionScope.kt` ganha `sessionEventNames`, além do `sessionScope` já existente —
nenhuma mudança em `sessionScope` em si. A pendência "Decidir, via ADR, como combinar o recorte de
temas com o recorte de eventos..." fica resolvida; a implementação e o teste dessa função ainda não
existem, seguem como próximo passo desta mesma tarefa, escritos depois deste documento e do
`architecture.md` estarem prontos, nunca antes.

Fica de fora, porque já está fora do alcance desta pendência: onde e quando essa função é chamada
de verdade (a tela de configuração de sessão, EI-NAV-05, ainda não desenhada — ver pendência
"Desenhar a aparência visual das telas do motor", `tasks.md`) — esta ADR só decide o mecanismo de
cálculo, não a interface que o aciona.
