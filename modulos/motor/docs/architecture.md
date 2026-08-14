# Architecture — Motor

<!-- module-doc-type: architecture -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Architecture |
| Versão | 0.7.0 |
| Data | 14-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Descreve como o módulo é construído por dentro — layout de arquivos,
> pacote, fronteiras, fluxo de dados técnico. É o "como" que corresponde
> ao "o quê" do `concept.md`; lido logo em seguida, quando existir.
>
> Implementação de código deriva sempre daqui e do contrato em
> `schemas/` — nunca o contrário: nunca escrever código primeiro e
> desenhar arquitetura/schema depois só pra bater com o que já foi
> escrito.
>
> Só cobre a parte do módulo cujo "como construir" já foi desenhado a
> partir do `concept.md` — nunca escrito a partir do código já
> existente. Se `concept.md` já cobre uma parte sem esse desenho
> ainda, isso é dito explicitamente aqui, com ponteiro pra pendência
> em `tasks.md` — nunca um documento incompleto sem explicação.
>
> Vale igual quando o módulo já tem código: o "como" aqui é sempre o
> correto, desenhado a partir do requisito em `concept.md`, nunca um
> espelho do código já existente. Se o código atual não bate com o
> "como" desenhado aqui, quem muda é o código, não a arquitetura -- a
> correção fica pendência em `tasks.md` até acontecer.
>
> Cada seção segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

Convenção dos códigos citados neste documento:
- `DA-LEI` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.1.
- `DA-ARM` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.3.
- `DA-IMP` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.4.
- `DA-REG` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.7.
- `DA-CFG` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.5.
- `EI-HIE` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.1.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.10.
- `EI-VAL` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.4.
- `PD-LEI` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.1.
- `PD-CON` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.2.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.
- `PD-NAV` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.4.

## Índice
- [Layout](#layout)
  - [Aparelho de jogo (aplicativo)](#aparelho-de-jogo-aplicativo)
    - [Núcleo do motor](#núcleo-do-motor)
      - [Pacote `search` — desenho interno](#pacote-search--desenho-interno)
      - [Pacote `hierarchy` — desenho interno](#pacote-hierarchy--desenho-interno)
      - [Pacote `session` — desenho interno](#pacote-session--desenho-interno)
    - [Interface](#interface)
  - [Acessório leitor (firmware)](#acessório-leitor-firmware)
  - [Fronteira de dado entre aplicativo e acessório](#fronteira-de-dado-entre-aplicativo-e-acessório)
  - [Fronteira de dado do pacote de conteúdo](#fronteira-de-dado-do-pacote-de-conteúdo)
- [Referências](#referências)
- [Controle de versão](#controle-de-versão)

## Layout

*Em resumo:* o módulo motor tem dois pedaços de código bem separados —
o aplicativo, que roda no aparelho de quem joga, e o firmware de um
acessório físico opcional, usado só quando o aparelho não tem antena
própria pra ler as peças. Os dois trocam só um dado entre si.

*Em detalhe técnico:* essa divisão em dois componentes já vem
decidida — não é um desenho novo, é a mesma distinção que
[`concept.md`](concept.md), seção Fluxo, e o
[Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
seção 4, já registram entre "aparelho de jogo" e "acessório leitor".
O que este documento faz é descer um nível: dizer o que cada
componente é responsável por fazer e como os dois se conectam. A
linguagem de programação de cada componente, e a estrutura de módulos e
pacotes do aplicativo, já estão decididas (ver `decisions/`, ADRs 0001,
0002 e 0003, referenciadas ao final de cada subseção abaixo).

### Aparelho de jogo (aplicativo)

*Em resumo:* é onde a lógica do motor roda de verdade — leitura de
peça, validação de tentativa, telas, guarda de sessão e relatório.
Todo comportamento já decidido na cascata (documentos 1 a 5) vira
código aqui. Por dentro, esse componente se divide em duas partes —
núcleo e interface — do mesmo jeito que um motor de jogo se separa de
quem desenha a tela: uma decide, a outra mostra.

*Em detalhe técnico:* linguagem de programação: Kotlin, ver
[decisions/0001-linguagem-do-aplicativo.md](<../decisions/0001-linguagem-do-aplicativo.md>).
Estrutura do projeto Android: dois módulos Gradle — `core` (núcleo do
motor) e `app` (interface), com `app` dependendo de `core` e nunca o
contrário — com pacotes organizados por assunto funcional dentro de cada
um. Alternativas consideradas, decisão completa e motivo em
[decisions/0003-estrutura-de-modulos-do-aplicativo.md](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>).
Esse projeto Gradle mora dentro de `modulos/motor/` (não na raiz do
repositório) — motivo em
[decisions/0006-localizacao-do-projeto-gradle-no-repositorio.md](<../decisions/0006-localizacao-do-projeto-gradle-no-repositorio.md>).
A árvore abaixo é relativa a `modulos/motor/`:

```
core/
  src/main/kotlin/org/nexo/motor/core/
    hierarchy/      instância, tema, evento — cadastro e navegação hierárquica
    session/        sessão em curso: validação, erro, pular, dica, encadeamento, pausa/ocioso/saída
    search/         busca aproximada (Levenshtein)
    content/        importação e validação do pacote de conteúdo
    connectivity/   cliente BLE, leitura de peça (NFC direta ou via acessório)
    report/         registro de sessão, relatório, exportação CSV/PDF
app/
  src/main/kotlin/org/nexo/motor/app/
    ui/             telas (Activities/Composables) — fluxo funcional definido
                     na seção Interface, abaixo
```

O módulo `app` ainda não é desmembrado em módulos de funcionalidade
menores — como as 17 entradas de tela do
[Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
seção 6.6, se agrupam em telas físicas é parte do desenho visual ainda
pendente (ver [`tasks.md`](tasks.md)).

#### Núcleo do motor

*Em resumo:* tudo que decide alguma coisa — nunca desenha nada na
tela. Já está inteiramente descrito pela cascata, sem nenhuma
pendência de desenho.

*Em detalhe técnico:* responsabilidades, cada uma já decidida em algum
ponto da cascata:

- Leitura de peça (categoria LEI) pelos dois caminhos já fixados
  (DA-LEI-03): direto pela antena própria do aparelho (DA-LEI-04), ou
  repassada pelo acessório externo via Bluetooth (ver
  [Fronteira de dado entre aplicativo e acessório](#fronteira-de-dado-entre-aplicativo-e-acessório))
  — nos dois casos, a lógica de validação (categoria VAL, EI-VAL-*)
  trata a leitura do mesmo jeito (DA-LEI-06).
- Toda a lógica de sessão — hierarquia, validação, erro, pular, dica,
  pausa, registro — como especificado em
  [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>).
- Importação e validação (categoria IMP), item a item, do pacote de
  conteúdo (PD-IMP-01, PD-IMP-02), lendo o arquivo compactado com
  `java.util.zip.ZipFile`, parte do próprio SDK do Android/Java, sem
  biblioteca externa (PD-IMP-03).
- Navegação (categoria NAV) com busca aproximada por distância de
  Levenshtein (PD-NAV-01, PD-NAV-02).
- Guarda local de configuração, registro e relatório (categorias ARM
  e REG), sem servidor central (DA-ARM-01), com o relatório exportado
  em CSV e PDF (DA-REG-01).
- Papel de cliente GATT (papel *central*), quando usa o acessório
  externo, conectando ao Nordic UART Service que o acessório anuncia
  (categoria CON, PD-CON-01 a PD-CON-04). Definição de GATT, serviço e
  característica, e do papel de cliente/central:
  [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
  seção 4 e PD-CON-03.

O núcleo nunca decide aparência — só estado. Pra cada tela, o que o
núcleo entrega pra interface mostrar é exatamente o "conteúdo
funcional" já listado no
[Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
seção 6.6 — essa tabela já é, na prática, o contrato entre as duas
partes.

##### Pacote `search` — desenho interno

*Em resumo:* dentro do núcleo, o pacote `search` é quem sabe achar um
item de navegação (instância, tema ou evento) mesmo com erro de
digitação. Não decide nada de tela — só recebe uma lista de itens e o
termo que a pessoa digitou, e devolve a lista já filtrada e ordenada.

*Em detalhe técnico:* implementa PD-NAV-01 e PD-NAV-02 (distância de
edição de Levenshtein, limiar de 20% do tamanho do termo digitado,
arredondado pra baixo, mínimo 1). Três pontos que a cascata de
documentação não desce a esse nível de detalhe ficam registrados em
[decisions/0004](<../decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md>):
a comparação ignora maiúscula/minúscula e acento (normaliza os dois
lados antes de comparar, mesmo padrão usado por Google Cloud e
Elasticsearch); a busca compara o termo tanto contra o nome inteiro de
cada item quanto contra qualquer trecho contíguo dele, com o resultado
de nome inteiro aparecendo sempre primeiro; e o empate de distância
preserva a ordem que a lista já tinha (por padrão, alfabética —
DA-NAV-01), nunca decidido pelo próprio pacote.

API pública, genérica o bastante pra servir tanto a lista de
instâncias quanto a de temas ou de eventos — nenhuma das três sabe o
que é "instância" ou "evento", só recebem uma lista de itens e uma
função que extrai o nome de cada um (RNF-MOD-01, o motor não conhece o
assunto do conteúdo):

```
core/search/
  ApproximateSearch.kt   levenshteinDistance, substringLevenshteinDistance,
                          approximateSearchThreshold, approximateSearch<T>
```

Testado com `kotlin-test` + JUnit Jupiter, conforme
[decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)
— primeiro pacote do módulo `core` a existir, junto com o esqueleto
mínimo de build do projeto inteiro (`settings.gradle.kts` na raiz,
`core/build.gradle.kts`).

##### Pacote `hierarchy` — desenho interno

*Em resumo:* dentro do núcleo, o pacote `hierarchy` é quem representa
e organiza os três primeiros níveis do conteúdo — instância, tema e
evento — garantindo que a regra "cada tema ou evento é ou tem ordem,
ou é avulso" nunca vire uma combinação impossível dentro do código.
Não decide nada sobre a sessão de jogo em si, nem sobre o conteúdo de
um fotograma — só sobre a estrutura e o nome de cada item.

*Em detalhe técnico:* implementa EI-HIE-01 a EI-HIE-04 (nome único
dentro do grupo imediatamente acima; declaração individual, por item,
de ordem ou avulso; posição obrigatória quando "com ordem", proibida
quando avulso) — incluindo a exigência, do
[Documento de Conceito](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
seção 2, de que um item "com ordem" seja sempre "continuação lógica do
anterior": as posições de um mesmo grupo "com ordem" precisam formar
uma sequência sem buraco nem repetição (1, 2, 3...), não só ser únicas
entre si — ponto que faltava na implementação original, corrigido
depois de um achado (ver
[findings.md](<findings.md#2026-08-14-posicao-com-buraco-nao-detectada>)).
EI-HIE-05 (fotograma sempre ordenado, sem exceção)
fica fora deste pacote — sequência e fotograma são o nível onde "a
mecânica de peça e tentativa... realmente acontece"
([`1 - documento-de-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
§2), território do pacote `session`, não de navegação hierárquica.
Pelo mesmo motivo, os campos do contrato de dado ligados ao conteúdo
de um evento em si (`zero_mark`, `hint_enabled`, `hint_content`,
`frames`, ver [concept.md, Contrato de dado](<concept.md#contrato-de-dado>))
também ficam fora — `hierarchy` só carrega o que é necessário pra
organizar e navegar: nome, e a relação de ordem/avulso com os vizinhos
do mesmo grupo.

Dois pontos que a cascata de documentação não desce a esse nível de
detalhe ficam registrados em
[decisions/0007](<../decisions/0007-desenho-do-pacote-hierarchy.md>):
a relação "com ordem" ou "avulso" é representada por um tipo próprio
(`Ordering`), com duas formas que o próprio compilador Kotlin já
diferencia — torna impossível, em tempo de compilação, um item avulso
carregar uma posição por engano, ou um item com ordem não carregar
nenhuma; e a checagem de nome repetido dentro do mesmo grupo
(EI-HIE-01) devolve a lista completa do que foi encontrado de errado,
em vez de parar no primeiro problema — mesma postura já usada em
DA-CFG-03/PD-IMP-02 para item de conteúdo incompleto ("recusado
sozinho, sem impedir a importação do restante"). Um terceiro ponto,
descoberto só depois, durante o desenho do pacote `session` — checar
posição não bastava conferir só unicidade; buraco na numeração (ex.:
posições 1 e 3, sem o 2) também precisa ser recusado, pelo mesmo
motivo acima ("continuação lógica do anterior") — está registrado em
[findings.md](<findings.md#2026-08-14-posicao-com-buraco-nao-detectada>),
não numa ADR nova, porque não envolveu escolher entre alternativas
reais, só corrigir a implementação pra bater com o que o Conceito já
exigia.

API pública:

```
core/hierarchy/
  Hierarchy.kt             Ordering (Ordered/Standalone), Event, Theme, Instance
  HierarchyValidation.kt   HierarchyViolation (DuplicateThemeName, DuplicateEventName,
                           NonContiguousThemePositions, NonContiguousEventPositions),
                           validate(instance: Instance): List<HierarchyViolation>
```

O cálculo de qual trecho, dentro de um grupo já validado, entra numa
sessão que atravesse mais de um evento ou tema (escolher um ponto de
início e até onde a sessão vai) não entra neste pacote: `hierarchy`
garante que a numeração de um grupo "com ordem" já chega sem buraco
nem repetição (ver acima); decidir e montar esse recorte é
responsabilidade do pacote `session` (ver
[decisions/0009](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>)).

Testado com `kotlin-test` + JUnit Jupiter, mesma ferramenta já fixada
em [decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)
para todo pacote de `core`.

##### Pacote `session` — desenho interno

*Em resumo:* dentro do núcleo, o pacote `session` é quem sabe o que
está acontecendo numa partida agora — qual peça se espera, quantos
erros já aconteceram, se a dica já pode aparecer, se a sessão está
pausada. É o pacote mais complexo do núcleo: reúne quase toda a
mecânica de jogo descrita na Especificação, em vez de uma regra
isolada como `search` ou `hierarchy`.

*Em detalhe técnico:* implementa EI-VAL-01 a EI-VAL-03 (validação de
tentativa), EI-ERR-01/02 (contagem de erro do evento), EI-PUL-01 a
EI-PUL-05 (pular), EI-DIC-01 a EI-DIC-04 (dica e sugestão de estudo),
EI-SES-05 a EI-SES-08 (composição de sessão), EI-ENC-01 a EI-ENC-03
(encadeamento e resumos), EI-PAU-01 a EI-PAU-06 (pausa, ocioso,
saída). A regra de qual referência mostrar antes de cada tentativa
(EI-SES-01 a EI-SES-04) já está inteiramente fechada pela própria
Especificação, sem alternativa de desenho — vira código direto, sem
exigir ADR.

Três pontos que a cascata de documentação não desce a esse nível de
detalhe ficam registrados em
[decisions/0008](<../decisions/0008-representacao-do-estado-da-sessao.md>),
[decisions/0009](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>)
e
[decisions/0010](<../decisions/0010-persistencia-do-estado-de-sessao-pausada.md>):

- **Representação do estado:** o estado de uma sessão em curso é um
  único objeto imutável, substituído a cada transição (tentativa,
  pulo, dica usada, pausa) — nunca alterado em lugar —, carregando um
  registro interno de tudo que já aconteceu. Os números que a
  Especificação exige nunca guardar soltos (EI-ERR-02: contagem de
  erro do evento; EI-DIC-01, mesma lógica: contagem de tentativas
  seguidas por posição) são sempre calculados a partir desse registro,
  nunca de um contador à parte.
- **Cálculo do recorte contíguo:** montar uma sessão que atravesse
  mais de um evento ou tema (EI-SES-06 a EI-SES-08) nunca aceita um
  conjunto livre de itens escolhidos e checado depois — é sempre um
  trecho calculado a partir de um ponto de início, percorrendo a lista
  já ordenada que `hierarchy` garante sem buraco nem repetição (ver
  [findings.md](<findings.md#2026-08-14-posicao-com-buraco-nao-detectada>)).
- **Persistência da sessão pausada:** gravar e ler o estado de uma
  sessão pausada usa só `java.io.File`, nunca uma API de armazenamento
  do Android — o caminho do arquivo é recebido como parâmetro; decidir
  esse caminho de verdade (`context.filesDir`) é responsabilidade do
  módulo `app`, não de `core`.

API pública:

```
core/session/
  SessionState.kt              SessionState (retrato imutável), SessionEvent (registro interno),
                                errorCount(event), consecutiveAttempts(position) — derivados do registro
  SessionScope.kt               sessionScope<T>(siblings: List<T>, ordering: (T) -> Ordering, from: T, until: T): List<T>
  SessionTransitions.kt         recordAttempt, skipPosition, hintAvailable, useHint,
                                 studySuggestionAvailable, eventComplete, continueToNextEvent,
                                 pause, resume — cada uma devolve um novo SessionState, nunca altera
                                 o estado recebido em lugar
  SessionStatePersistence.kt    saveSessionState(state: SessionState, file: File),
                                 loadSessionState(file: File): SessionState?,
                                 deleteSessionState(file: File)
```

`recordAttempt` e `continueToNextEvent` recebem o identificador
esperado (`expectedTagId`) e o nome do próximo evento como parâmetro,
em vez de descobrir isso sozinhos — `session` não conhece o pacote
`content` (ainda não desenhado), então quem chama essas funções é
responsável por já ter em mãos o dado que vem de lá, mesma lógica de
desacoplamento por assunto já usada em todo o núcleo
(RNF-MOD-01, [decisions/0003](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>)).
Pela mesma razão, a montagem de texto da mensagem de três partes
(EI-PUL-05) e das sínteses de resumo/cadeia (EI-RET-04, EI-ENC-03) não
entra neste pacote — depende dos textos e fotogramas que só `content`
vai ter; `session` só registra os fatos (o quê, quando, em que
posição) que essas telas vão precisar.

O registro interno do estado é também a fonte que o pacote `report`,
ainda não desenhado, vai usar para montar o relatório final
(EI-REG-01/02) — `session` só expõe o registro; `report` decide
formato e exportação, sem que `session` precise saber nada sobre
isso.

Testado com `kotlin-test` + JUnit Jupiter, mesma ferramenta já fixada
em [decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)
para todo pacote de `core`.

#### Interface

*Em resumo:* as telas — o que mostra o estado que o núcleo decide,
nunca decide nada por conta própria. Continua dentro do módulo motor,
não um módulo separado (ver decisão registrada em `tasks.md`).

*Em detalhe técnico:* o fluxo funcional de cada tela (quais existem, o
que cada uma mostra) já está fixado no
[Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
seção 6.6. A aparência visual (cor, fonte, layout) não está decidida —
pendência registrada em [`tasks.md`](tasks.md). Direção provável,
ainda não pesquisada nem decidida de verdade: uma casca única,
neutra, no padrão Material Design do Google — reaproveitada por toda
instância, já que a única coisa que varia de fato entre instâncias é
o conteúdo (fotogramas, textos), não a aparência das telas em si.

Material Design decide só a aparência (cor, componente, espaçamento)
— não decide onde cada elemento vai em cada tela. Essa segunda
decisão (layout, composição) segue um processo com método próprio,
reconhecido fora deste projeto, chamado design centrado no usuário
(NORMAN, 2013; INTERNATIONAL ORGANIZATION FOR STANDARDIZATION, 2019):

1. Arquitetura de informação — o que precisa existir em cada tela. Já
   resolvido: é a coluna "Conteúdo funcional" da tabela do
   [Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
   seção 6.6.
2. Wireframe — layout de cada tela (onde cada elemento vai), sem cor
   nem fonte ainda.
3. Aplicação do sistema visual (Material Design — GOOGLE, [s.d.]) em
   cima do wireframe já pronto.
4. Protótipo navegável e avaliação contra as heurísticas de
   usabilidade (NIELSEN, 1994) antes de virar código de verdade.

Nenhuma dessas quatro etapas foi executada ainda — só o método a
seguir está registrado aqui.

### Acessório leitor (firmware)

*Em resumo:* só existe pra aparelhos sem antena NFC própria. Lê a
etiqueta e informa o identificador ao aplicativo — não guarda
conteúdo nem roda lógica de jogo, como já registrado no
[Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
seção 4.

*Em detalhe técnico:*

- Hardware já fixado: módulo leitor NXP PN532/C1 (PD-LEI-01) e
  microcontrolador Espressif ESP32-D0WD-V3 (PD-LEI-02), ligados entre
  si por I2C (PD-LEI-03).
- Papel de servidor GATT (papel *peripheral*, oposto ao de
  cliente/central do aplicativo — ver
  [Aparelho de jogo](#aparelho-de-jogo-aplicativo), ambos definidos
  no [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
  PD-CON-03), anunciando o Nordic UART Service (PD-CON-01, PD-CON-03).
- A cada leitura de etiqueta bem-sucedida pelo PN532, notifica o
  identificador da etiqueta na característica TX do serviço
  (PD-CON-02, PD-CON-03).

Linguagem e framework de firmware: C++ com framework Arduino, via
PlatformIO — ver
[decisions/0002-framework-do-firmware-do-acessorio.md](<../decisions/0002-framework-do-firmware-do-acessorio.md>).

### Fronteira de dado entre aplicativo e acessório

*Em resumo:* os dois trocam só um dado — o identificador da etiqueta
lida, mandado do acessório pro aplicativo. Nada mais atravessa essa
fronteira hoje.

*Em detalhe técnico:* o acessório notifica o identificador na
característica TX; a característica RX (escrita, do aplicativo pro
acessório) existe, reservada, seguindo o par completo do serviço
padrão, mas nenhuma comunicação nesse sentido é exigida pelos
requisitos atuais — TX e RX definidas no
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
PD-CON-02 e PD-CON-03. Tamanho de mensagem nunca chega perto do
limite de MTU da conexão — *Maximum Transmission Unit*, o tamanho
máximo de dado que cabe numa única mensagem trocada pela conexão —,
então não há fragmentação a tratar (mesmo documento, PD-CON-04; o
termo "MTU" em si não tem definição em nenhum documento da cascata,
só a citação de PD-CON-04).

### Fronteira de dado do pacote de conteúdo

*Em resumo:* quem monta o conteúdo de uma instância entrega um único
arquivo pro aplicativo; o aplicativo só aceita esse arquivo se ele
bater exatamente com o contrato já fixado.

*Em detalhe técnico:* o contrato está no bloco YAML de
[`concept.md`](concept.md), seção Contrato de dado, gerado em
[`schemas/`](../schemas/) — essa é a única porta de entrada de
conteúdo novo no motor (DA-IMP-01, DA-IMP-02). O aplicativo nunca
escreve esse arquivo, só lê (PD-IMP-03); a ferramenta que um dia vier
a gerar esse arquivo pra quem monta conteúdo fica fora do escopo deste
módulo (mesma premissa já registrada no
[Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>)
e no
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
ambos seção 8).

## Referências

Fontes externas citadas na seção [Interface](#interface), no formato
definido pela norma ABNT NBR 6023 (Informação e documentação —
Referências). Citadas no corpo do documento como (AUTOR, ano).

GOOGLE. **Material Design 3**. [S.l.], [s.d.]. Disponível em:
https://m3.material.io/. Acesso em: 12 ago. 2026. Nota sobre acesso:
o site carrega o conteúdo por JavaScript — a ferramenta de leitura
automatizada usada na elaboração deste documento não conseguiu trazer
o texto completo da página, só confirmar que ela existe e pertence ao
domínio oficial do Google para o Material Design.

INTERNATIONAL ORGANIZATION FOR STANDARDIZATION. **ISO 9241-210:2019 —
Ergonomics of human-system interaction — Part 210: Human-centred
design for interactive systems**. Geneva: ISO, 2019. Disponível em:
https://www.iso.org/standard/77520.html. Acesso em: 12 ago. 2026.
Nota sobre acesso: o acesso automatizado ao domínio iso.org não foi
possível durante a elaboração deste documento (erro do servidor) —
mesma situação já registrada no
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>)
para o site da NXP.

NIELSEN, Jakob. **10 Usability Heuristics for User Interface
Design**. Nielsen Norman Group, 24 abr. 1994, revisado em 30 jan.
2024. Disponível em: https://www.nngroup.com/articles/ten-usability-heuristics/.
Acesso em: 12 ago. 2026.

NORMAN, Donald A. **The Design of Everyday Things**: revised and
expanded edition. New York: Basic Books, 2013.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial: layout do aparelho de jogo (núcleo e interface), do acessório leitor, e das duas fronteiras de dado. | Criação inicial |
| 0.2.0 | 13-08-2026 | Estrutura de módulos e pacotes do projeto Android resolvida (dois módulos Gradle, `core` e `app`), substituindo a pendência registrada na seção "Aparelho de jogo (aplicativo)". | Resolução de [decisions/0003-estrutura-de-modulos-do-aplicativo.md](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>) |
| 0.3.0 | 14-08-2026 | Acrescentado o desenho interno do pacote `search` (API pública, normalização de texto, comparação inteira e por trecho, abordagem de teste), e a localização do projeto Gradle dentro de `modulos/motor/`. | Resolução de [decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md](<../decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md>), [decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>) e [decisions/0006-localizacao-do-projeto-gradle-no-repositorio.md](<../decisions/0006-localizacao-do-projeto-gradle-no-repositorio.md>) |
| 0.4.0 | 14-08-2026 | Acrescentado o desenho interno do pacote `hierarchy` (tipos que representam instância, tema e evento, representação de ordem/avulso que o compilador já diferencia, validação de nome único devolvendo lista de violações). | Resolução de [decisions/0007-desenho-do-pacote-hierarchy.md](<../decisions/0007-desenho-do-pacote-hierarchy.md>) |
| 0.5.0 | 14-08-2026 | Validação de `hierarchy` passa a exigir posição sem buraco nem repetição dentro de um grupo "com ordem", não só posição única; ajustado o ponteiro do cálculo de recorte contíguo, agora de responsabilidade de `session`. | Achado [findings.md#2026-08-14-posicao-com-buraco-nao-detectada](<findings.md#2026-08-14-posicao-com-buraco-nao-detectada>), revelado pelo desenho de [decisions/0009-calculo-do-recorte-continuo-de-sessao.md](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>) |
| 0.6.0 | 14-08-2026 | Acrescentado o desenho interno do pacote `session` (representação do estado, cálculo de recorte contíguo, persistência da sessão pausada). | Resolução de [decisions/0008-representacao-do-estado-da-sessao.md](<../decisions/0008-representacao-do-estado-da-sessao.md>), [decisions/0009-calculo-do-recorte-continuo-de-sessao.md](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>) e [decisions/0010-persistencia-do-estado-de-sessao-pausada.md](<../decisions/0010-persistencia-do-estado-de-sessao-pausada.md>) |
| 0.7.0 | 14-08-2026 | Acrescentadas as transições de estado do pacote `session` (validar tentativa, pular, dica, sugestão de estudo, encadeamento, pausa/retomada) e o formato de serialização (JSON). | Resolução de [decisions/0011-formato-de-serializacao-do-estado-de-sessao.md](<../decisions/0011-formato-de-serializacao-do-estado-de-sessao.md>) |
