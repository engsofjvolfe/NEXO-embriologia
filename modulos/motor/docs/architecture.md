# Architecture — Motor

<!-- module-doc-type: architecture -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Architecture |
| Versão | 0.22.0 |
| Data | 15-08-2026 |
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
    - [Pacote `content` — desenho interno](#pacote-content--desenho-interno)
    - [Pacote `connectivity` — desenho interno](#pacote-connectivity--desenho-interno)
    - [Pacote `report` — desenho interno](#pacote-report--desenho-interno)
    - [Pacote `summary` — desenho interno](#pacote-summary--desenho-interno)
    - [Interface](#interface)
      - [Ligação com o núcleo do motor](#ligação-com-o-núcleo-do-motor)
    - [Esqueleto mínimo e versões de build](#esqueleto-mínimo-e-versões-de-build)
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
    summary/        texto de resumo/síntese de fim de evento e de cadeia
app/
  src/main/kotlin/org/nexo/motor/app/
    ui/             telas e o ViewModel que liga sessão à interface — ver
                     "Interface" e "Ligação com o núcleo do motor", abaixo
    connectivity/   Service de Bluetooth e leitura NFC — ver "Pacote
                     `connectivity` — desenho interno", abaixo
    report/         escrita de verdade do relatório no aparelho — ver
                     "Pacote `report` — desenho interno", abaixo
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

- Leitura de peça (categoria LEI): dois caminhos existem ao mesmo
  tempo, sempre — direto pela antena própria do aparelho (DA-LEI-04),
  ou repassada pelo acessório externo via Bluetooth (ver
  [Fronteira de dado entre aplicativo e acessório](#fronteira-de-dado-entre-aplicativo-e-acessório))
  — nos dois casos, a lógica de validação (categoria VAL, EI-VAL-*)
  trata a leitura do mesmo jeito (DA-LEI-06). Qual caminho "funciona"
  a qualquer momento depende só de qual rádio a pessoa ligou no
  aparelho dela, nunca de uma escolha automática do aplicativo — ponto
  em que este módulo diverge, de propósito, da leitura literal de
  DA-LEI-03; motivo completo em
  [decisions/0017](<../decisions/0017-quem-decide-a-tecnologia-de-leitura.md>).
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
DA-NAV-01), nunca decidido pelo próprio pacote. Um quarto ponto, no
mesmo nível, fica registrado em
[decisions/0014](<../decisions/0014-busca-aproximada-com-termo-vazio.md>):
termo vazio (ou só espaço) devolve a lista de entrada sem filtro nem
reordenação, nunca lista vazia.

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
  SessionState.kt              SessionState (retrato imutável), SessionEvent (registro interno,
                                sete variantes: AttemptAccepted, AttemptRejected, HintUsed,
                                StudySuggestionShown, PositionSkipped, Paused, WentIdle — cada
                                uma com timestamp próprio), errorCount(event), consecutiveAttempts(position)
                                — derivados do registro
  SessionScope.kt               sessionScope<T>(siblings: List<T>, ordering: (T) -> Ordering, from: T, until: T): List<T>
  SessionTransitions.kt         recordAttempt, skipPosition, hintAvailable, useHint,
                                 studySuggestionAvailable, showStudySuggestion, eventComplete,
                                 continueToNextEvent, pause, goIdle, resume — cada uma devolve um
                                 novo SessionState, nunca altera o estado recebido em lugar
  SessionStatePersistence.kt    saveSessionState(state: SessionState, file: File),
                                 loadSessionState(file: File): SessionState?,
                                 deleteSessionState(file: File)
  SessionReference.kt            referenceImage(startingPosition: Int, isFirstEventOfSession: Boolean,
                                 previousFrameImage: String?, zeroMarkImage: String,
                                 lastFilledImageOfPreviousEvent: String?): String — EI-SES-04 pelas
                                 próprias palavras (ver decisions/0022, Consequências)
```

Todo evento do registro carrega o próprio horário (`timestamp: Long`,
milissegundos), recebido como parâmetro explícito por quem chama cada
função de transição — mesma lógica de `expectedTagId` abaixo: `session`
não lê relógio nenhum sozinho, só guarda o valor que recebe.
`showStudySuggestion` registra o momento em que a sugestão de estudo
aparece na tela, mesmo padrão já usado entre `hintAvailable`/`useHint`.
Horário, `WentIdle` e `StudySuggestionShown` fecham uma lacuna entre o
registro e EI-REG-01 — motivo completo em
[findings.md](<findings.md#2026-08-15-registro-de-sessao-incompleto-frente-a-ei-reg-01>).

`recordAttempt` e `continueToNextEvent` recebem o identificador
esperado (`expectedTagId`) e o nome do próximo evento como parâmetro,
em vez de descobrir isso sozinhos — `session` não conhece o pacote
`content`, então quem chama essas funções é
responsável por já ter em mãos o dado que vem de lá, mesma lógica de
desacoplamento por assunto já usada em todo o núcleo
(RNF-MOD-01, [decisions/0003](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>)).
Pela mesma razão, a montagem de texto da mensagem de três partes
(EI-PUL-05) e das sínteses de resumo/cadeia (EI-RET-04, EI-ENC-03) não
entra neste pacote — é responsabilidade do pacote `summary` (ver
[pacote `summary`](#pacote-summary--desenho-interno),
[decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>)),
que combina o registro daqui com os textos que só `content` tem;
`session` só registra os fatos (o quê, quando, em que posição) que
esse outro pacote vai precisar.

O registro interno do estado é também a fonte que o pacote `report`
(ver [pacote `report`](#pacote-report--desenho-interno),
[decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>))
usa para montar o relatório final (EI-REG-01/02) — `session` só expõe
o registro; `report` decide formato e exportação, sem que `session`
precise saber nada sobre isso.

Testado com `kotlin-test` + JUnit Jupiter, mesma ferramenta já fixada
em [decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)
para todo pacote de `core`.

##### Pacote `content` — desenho interno

*Em resumo:* dentro do núcleo, o pacote `content` é quem lê o arquivo
que reúne todo o conteúdo de uma área (temas, eventos, fotogramas,
textos) e confere se ele bate exatamente com o formato já combinado.
Nunca corrige nem altera o arquivo — só decide, sem meio-termo: ou o
pacote inteiro está certo e vira uma instância pronta pra jogo, ou não
está, e nada dele fica disponível, com a lista completa do que precisa
ser corrigido.

*Em detalhe técnico:* implementa PD-IMP-01 (esquema do pacote de
conteúdo) e PD-IMP-03 (leitura do arquivo ZIP com
`java.util.zip.ZipFile`), além de DA-CFG-01/02 (validação de conteúdo
incompleto).

**Regra de aceitação — tudo ou nada, em qualquer nível da
hierarquia.** Havendo qualquer violação — falta um tema numa
instância, falta um evento num tema, falta uma sequência de
fotogramas num evento, ou é só um campo isolado — o pacote inteiro é
recusado; a instância só é devolvida quando a lista de violações vem
completamente vazia. Diverge, de propósito, da leitura literal de
DA-CFG-03 e PD-IMP-02 ("item recusado sozinho, sem travar o restante
do pacote") — ver
[decisions/0013](<../decisions/0013-desenho-do-pacote-content.md>),
decisão 1, que usa o mecanismo de revisão que o próprio Projeto
Arquitetônico já previu pra essa escolha específica, sem reescrever
nenhum documento da cascata.

Três detalhes técnicos que essa regra exige, e que a cascata de
documentação não desce a esse nível, ficam registrados em
[decisions/0013](<../decisions/0013-desenho-do-pacote-content.md>):

- **Nome do manifesto:** o arquivo JSON dentro do ZIP tem nome fixo,
  `content.json`, na raiz do pacote.
- **Varredura completa, não fail-fast:** o manifesto é parseado como
  árvore genérica (`JsonElement`, kotlinx.serialization), nunca
  decodificado direto pra um tipo Kotlin tipado — cada tema, evento e
  fotograma é validado individualmente, e um problema numa parte não
  impede de continuar checando o resto, juntando a lista completa de
  violações numa só passada (cada fotograma identificado pelo índice
  dele dentro do evento, já que não tem `position` própria). A árvore
  inteira (mesmo vindo de partes com erro) ainda passa pela checagem
  de `hierarchy.validate()` (ver
  [pacote `hierarchy`](#pacote-hierarchy--desenho-interno)) e pela
  checagem de `tag_id` único em todo o pacote — essa unicidade só dá
  pra confirmar depois que tudo já foi lido, nunca olhando um
  fotograma sozinho.

API pública:

```
core/content/
  Content.kt                 Frame, ContentEvent, ContentTheme, ContentInstance,
                              ContentViolation (sealed: InvalidManifest, InvalidTheme,
                              InvalidEvent, InvalidFrame, DuplicateTagId, Hierarchy),
                              ContentImportResult
  ContentImport.kt            importContentPackage(manifestJson: String): ContentImportResult
  ContentPackageArchive.kt    ContentPackageArchive (Closeable, abre um ZipFile),
                              readManifest(): String, readImage(path: String): ByteArray
```

A leitura do arquivo ZIP fica isolada da validação do JSON —
`importContentPackage` recebe o texto do manifesto já lido, sem saber
que ele veio de um arquivo compactado. Mesma separação de assunto já
usada entre `SessionState.kt` (lógica pura) e
`SessionStatePersistence.kt` (E/S em arquivo). Cada chamada processa um
único arquivo e devolve uma única instância (completa) ou nenhuma —
nunca mescla conteúdo de mais de uma importação; decidir o que fazer
quando um nome de instância já existente é reimportado fica fora deste
pacote, numa camada de armazenamento ainda não desenhada.

Testado com `kotlin-test` + JUnit Jupiter, mesma ferramenta já fixada
em [decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)
para todo pacote de `core`.

#### Pacote `connectivity` — desenho interno

*Em resumo:* dentro do núcleo, o pacote `connectivity` guarda só a
descrição de "uma peça foi lida" — sem saber, ele mesmo, se essa
leitura veio da antena do próprio aparelho ou foi repassada pelo
acessório externo por Bluetooth (DA-LEI-06: "os dois casos tratados do
mesmo jeito"). O código que liga de verdade o rádio — Bluetooth ou NFC
— não mora aqui: mora no módulo `app`, decisão registrada em
[decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>).

*Em detalhe técnico:* implementa a parte de `core` de DA-LEI-04
(caminho direto) e DA-LEI-06 (tratamento igual dos dois caminhos) e o
contrato de protocolo de PD-CON-01 a PD-CON-04 (Nordic UART Service) —
nunca a conexão em si. Não implementa a escolha automática entre os
dois caminhos que a leitura literal de DA-LEI-03 sugere — ver
[decisions/0017](<../decisions/0017-quem-decide-a-tecnologia-de-leitura.md>).

Um ponto que a cascata de documentação não desce a esse nível de
detalhe — e que o texto de "Núcleo do motor" acima, escrito antes
desta seção existir, só descreve em palavras ("papel de cliente
GATT... conectando ao Nordic UART Service") — fica registrado em
[decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>):
esse "papel de cliente GATT" é um conceito de protocolo (quem pede
dado, quem responde — PD-CON-03), não uma afirmação de que a classe
que gerencia `BluetoothGatt` mora dentro de `core` — onde essa classe
mora de fato é o assunto dos dois parágrafos abaixo.

API pública:

```
core/connectivity/
  NordicUartService.kt   SERVICE_UUID, RX_CHARACTERISTIC_UUID, TX_CHARACTERISTIC_UUID —
                          os três UUIDs fixados em PD-CON-02, como java.util.UUID puro
  TagId.kt                tagIdFromBytes(bytes: ByteArray): String — decodifica um
                          identificador físico bruto (bytes) pro mesmo formato de texto
                          hexadecimal maiúsculo usado no campo tag_id do pacote de
                          conteúdo (PD-IMP-01)
  ConnectionState.kt      enum DISCONNECTED/SCANNING/CONNECTED — dado puro, sem nenhuma
                          opinião sobre como isso aparece pra pessoa (isso é aparência,
                          fora de escopo aqui); usado só pelo lado app do Bluetooth, já
                          que a leitura NFC não tem estado de conexão (é por peça, não
                          contínua)
```

Nenhum tipo do pacote depende de classe do Android — testável por
teste de unidade comum, com `kotlin-test` + JUnit Jupiter
([decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)),
mesma ferramenta já fixada pra todo pacote de `core`. `tagIdFromBytes`
serve os dois caminhos de leitura igual (DA-LEI-06): tanto o
identificador bruto lido diretamente de uma etiqueta NFC pela antena
do próprio aparelho quanto o que chega pela notificação da
característica TX do acessório passam pela mesma função antes de virar
o `tag_id` que `session.recordAttempt` espera receber.

O código que liga de verdade o Bluetooth — gerencia `BluetoothGatt`,
conecta ao Nordic UART Service usando os UUIDs acima, recebe a
notificação — mora em `app`, dentro de um `Service` vinculado
(`connectivity/BleAccessoryService.kt`, pacote novo dentro de `app`,
irmão de `ui/`). O código que recebe a leitura NFC direta mora na
`Activity` de entrada que já existe
([decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>),
`MainActivity`), usando o modo leitor de NFC já decidido em DA-LEI-04
(`enableReaderMode()` com um `NfcAdapter.ReaderCallback`) — motivo
completo e fonte oficial em
[decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>).

O acessório manda o identificador físico bruto na notificação da
característica TX, sem nenhuma conversão prévia — o mesmo formato de
bytes que o módulo leitor PN532 devolve (PD-LEI-01) — pra que
`tagIdFromBytes` seja a única conversão que existe, usada pelos dois
caminhos por igual; motivo completo em
[decisions/0016](<../decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md>).

O aplicativo dá suporte aos dois caminhos de leitura ao mesmo tempo,
sempre — qual "funciona de verdade" a qualquer momento depende só de
qual rádio a pessoa ligou no aparelho dela, nunca de uma escolha
automática do aplicativo; motivo completo em
[decisions/0017](<../decisions/0017-quem-decide-a-tecnologia-de-leitura.md>).

O `AndroidManifest.xml` do módulo `app` declara a permissão de NFC e
as cinco permissões de Bluetooth exigidas pelas versões de Android
entre `minSdk` 24 e `targetSdk` 36 (duas faixas de versão diferentes,
ver [decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>)),
mais as duas declarações de hardware opcional (NFC e Bluetooth de
baixo consumo — nenhum dos dois é obrigatório pro aplicativo instalar).
O aplicativo pede a permissão de Bluetooth pra pessoa no momento em que
uma sessão de jogo está prestes a começar — sempre, independente do
aparelho ter ou não NFC — nunca de antemão. Lista exata de permissões,
motivo de cada uma, e por que pedir só nesse momento (com fonte oficial
do Android): [decisions/0018](<../decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md>).

API do lado `app`:

```
app/connectivity/
  PieceReadListener.kt         fun interface PieceReadListener { onPieceRead(tagId: String) } —
                                formato único de aviso, usado tanto pelo Service de Bluetooth
                                quanto pela leitura NFC de MainActivity, em vez de cada caminho
                                inventar o próprio formato
  ConnectionStateListener.kt   fun interface ConnectionStateListener {
                                onConnectionStateChanged(state: ConnectionState) } — só o
                                Service de Bluetooth usa (ver acima, por que NFC não tem isso)
  BluetoothPermissions.kt      requiredBluetoothPermissions(), hasBluetoothPermissions(context) —
                                lista as permissões exigidas pela versão do Android em uso
                                (decisions/0018), num lugar só
  BleAccessoryService.kt       Service vinculado (LocalBinder); startScanAndConnect() procura
                                por perto um aparelho anunciando o Nordic UART Service e conecta
                                no primeiro encontrado (só costuma existir um por ambiente — não
                                há tela de escolha entre vários, pendência ainda em aberto se
                                algum dia isso deixar de valer); avisa o ConnectionStateListener
                                a cada mudança de estado (procurando, conectado, desconectado) e
                                o PieceReadListener a cada leitura — o único lugar que muda os
                                dois estados é updateConnectionState, em vez de repetir a lógica
                                de avisar em cada ponto que a conexão muda; onCharacteristicChanged
                                decodifica a notificação da característica TX com tagIdFromBytes
```

`MainActivity` (esqueleto já existente, ver
[decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>))
implementa `NfcAdapter.ReaderCallback` e liga/desliga o modo leitor em
`onResume`/`onPause`; `onTagDiscovered` decodifica o identificador bruto
da etiqueta com a mesma `tagIdFromBytes` e entrega pro
`PieceReadListener` exposto por ela. Nem `MainActivity` nem
`BleAccessoryService` decidem o que fazer com uma leitura além de
entregá-la — quem consome esse aviso é o `ViewModel` descrito em
["Interface", abaixo](#interface), ver
[decisions/0020](<../decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md>).

Testado só por compilação real (`gradlew :app:assembleDebug`) — sem
teste automatizado, porque o módulo `app` não tem ferramenta de teste
configurada ainda pra código que toca API do Android (diferente do
`core`); ver pendência em [tasks.md](tasks.md).

#### Pacote `report` — desenho interno

*Em resumo:* dentro do núcleo, o pacote `report` monta o conteúdo dos
dois formatos de relatório (CSV e PDF) a partir do registro que
`session` já mantém — nunca decide onde o arquivo é salvo no
aparelho, nem desenha nada. Dividido entre `core` e `app` pelo mesmo
motivo que `connectivity` já é: a ferramenta de desenho do PDF só
existe dentro do Android.

*Em detalhe técnico:* implementa DA-REG-01/02 (dois formatos, mesmo
registro) e EI-REG-01/02 (o que cada relatório precisa conter).
Mecanismo de geração de cada formato, onde o arquivo fica guardado no
aparelho, e o atalho de compartilhar na tela de resultado (DA-RET-14):
[decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>),
nota de acompanhamento incluída (motivo completo em
[findings.md](<findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core>)).

API pública do lado `core` — monta só dado, nunca desenha nem escreve
arquivo de verdade, recebendo o registro de `session` (`SessionEvent`)
e a configuração da sessão como parâmetro (esta última ainda sem
produtor real, porque depende da tela de configuração de sessão,
ainda pendente — ver [`tasks.md`](tasks.md)):

```
core/report/
  Report.kt   EventConfiguration(eventName, skipEnabled, hintThreshold, studyThreshold),
              SessionConfiguration(eventNames, startingPosition, idleThresholdMillis, events),
              sessionEventTypeName(event: SessionEvent): String — nome em português de cada
              uma das sete variantes de SessionEvent, usado nas duas linhas abaixo
              buildReportCsv(configuration, log: List<SessionEvent>): String — texto CSV,
              escapado conforme RFC 4180 (DA-REG-01), terminador de linha CRLF
              buildReportPdfLines(configuration, log: List<SessionEvent>): List<String> —
              uma linha de texto por item; app/report desenha cada linha, nunca decide o texto
```

API do lado `app` — só aqui existe classe do Android:

```
app/report/
  ReportPdfRenderer.kt   renderReportPdf(lines: List<String>): PdfDocument — desenha cada
                          linha com Canvas, paginando quando a página enche
  ReportFileWriter.kt     writeReportCsv/writeReportPdf(context, fileName, conteúdo,
                          onWritten: (Uri) -> Unit) — os dois caminhos da decisão 3 de
                          decisions/0019 (MediaStore.Downloads a partir do Android 10;
                          getExternalStoragePublicDirectory + MediaScannerConnection.scanFile
                          antes disso), sempre entregando um content:// Uri pro callback
  ReportShareIntent.kt    buildReportShareIntent(csvUri, pdfUri): Intent — ACTION_SEND_MULTIPLE,
                          tipo genérico "*/*", decisão 4 de decisions/0019
```

`AndroidManifest.xml` ganha `WRITE_EXTERNAL_STORAGE` com
`android:maxSdkVersion="28"`, ao lado das já existentes de Bluetooth e
NFC. Nenhuma tela ainda chama esse código — a chamada real (a partir
da tela de resultado, DA-RET-14) fica pra quando o desenho visual das
telas acontecer (ver [`tasks.md`](tasks.md)), mesma situação que
`app/connectivity` já tinha antes do `ViewModel` existir.

Lado `core` testável com `kotlin-test` + JUnit Jupiter
([decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)),
mesma ferramenta de todo pacote de `core`. Lado `app` testado só por
compilação real (`gradlew :app:assembleDebug`), mesmo padrão já usado
em `app/connectivity`, porque `app` ainda não tem ferramenta de teste
configurada pra código que toca API do Android (ver [`tasks.md`](tasks.md)).

#### Pacote `summary` — desenho interno

*Em resumo:* dentro do núcleo, o pacote `summary` monta o texto (ou a
lista de dados) de fim de evento e de fim de cadeia — mensagem de
pulo, síntese sem pulo — a partir do que `session` e `content` já
sabem, sem conhecer nenhum dos dois por dentro.

*Em detalhe técnico:* implementa EI-PUL-05, EI-RET-04 e EI-ENC-03.
Mecanismo completo, incluindo o campo novo no contrato de dado
(`summary_fragment`, ver [concept.md](<concept.md#contrato-de-dado>)):
[decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>).

API pública, recebendo sempre dado já extraído de `session`/`content`
pelo `ViewModel` — o pacote nunca conhece esses dois por dentro, mesmo
espírito de `search`:

```
core/summary/
  Summary.kt   PositionOutcome (sealed: Answered(position, confirmationText), Skipped(position)),
               AnsweredPosition, SkipMessage(answered, unansweredPositions),
               buildSkipMessage(positions: List<PositionOutcome>): SkipMessage — EI-PUL-05
               ChainOutcome (Filled/Lost), ChainSkipSynthesis(filledCount, lostCount),
               buildChainSkipSynthesis(outcomes: List<ChainOutcome>): ChainSkipSynthesis — EI-ENC-03, caso com pulo
               buildContinuousSynthesis(summaryFragmentsInOrder: List<String>): String — EI-RET-04, EI-ENC-03 sem pulo
```

`buildSkipMessage` nunca recebe nem devolve o conteúdo de uma posição
pulada — só a posição em si, preservando a proibição de revelar
conteúdo pulado (EI-PUL-05, Documento de Conceito, seções 1 e 13).
`buildContinuousSynthesis` concatena os fragmentos na ordem recebida,
com um espaço simples entre eles — quem monta o conteúdo já escreve
cada `summary_fragment` pensando em encaixar com o vizinho (ver
[decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>)),
então a função em si não precisa de nenhuma pontuação especial, só
juntar.

Testável como os demais pacotes de `core`, com `kotlin-test` + JUnit
Jupiter
([decisions/0005](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>)),
já que não depende de nenhuma classe do Android.

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

Ponto específico a não esquecer quando essa aparência for desenhada,
com detalhe completo em [tasks.md](tasks.md): "Aguardando tentativa"
(DA-RET-06) precisa mostrar o estado de conexão do acessório
Bluetooth.

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

##### Ligação com o núcleo do motor

*Em resumo:* separado da aparência (ainda pendente), o mecanismo que
liga a leitura de uma peça à lógica de sessão e ao que a tela mostra
já está decidido e escrito — inclusive o conteúdo exato de cada
situação que a tela precisa mostrar.

*Em detalhe técnico:*
[decisions/0020](<../decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md>)
e
[decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>):
`app/ui/SessionViewModel.kt` implementa `PieceReadListener` e
`ConnectionStateListener` — `MainActivity` (ou o que vier a
substituí-la) continua gerenciando a leitura de NFC e a ligação com
`BleAccessoryService`, só repassando cada aviso pro `ViewModel` por
função direta, nunca por referência à tela ou ao `Service` — e expõe
`uiState: StateFlow<SessionUiState>` (`app/ui/SessionUiState.kt`,
formato fechado em decisions/0022). Internamente chama `session`,
`content` e `summary` pra transformar cada aviso, pulo, dica, sugestão
de estudo ou continuação de evento num novo `SessionScreen`. Ações que
a tela ainda vai disparar já têm método próprio no `ViewModel`
(`onSkipRequested`, `onScreenAcknowledged`, `onContinueRequested`,
`onExitRequested`, `onExitCancelled`, `onExitConfirmed`) — o efeito de
cada uma (que `SessionScreen` resulta) está fechado; o gatilho exato
na tela (toque, temporizador) continua parte do desenho visual
pendente (ver [tasks.md](tasks.md)). `onExitConfirmed` já apaga o
estado retomável da sessão (`deleteSessionState`, decisions/0010).

Um ponto fica de fora do que já está escrito: gerar o relatório de
saída (EI-PAU-04) antes de apagar o estado — mecanismo já fechado em
decisions/0019, só falta a chamada em si, que é papel da tela de
resultado (DA-RET-14), ainda não construída. Outro: o gatilho de
ociosidade (EI-PAU-06) — nenhum temporizador foi decidido ainda,
`goIdle` (`core/session`) continua sem quem o chame.

Testado por compilação real (`:app:assembleDebug`), mesmo padrão já
usado pro resto do lado `app` do módulo.

#### Esqueleto mínimo e versões de build

*Em resumo:* antes de qualquer tela de verdade existir, o módulo `app`
precisa só existir como projeto Android capaz de abrir num aparelho ou
emulador — sem aparência, sem lógica, só o mínimo que o próprio Android
exige pra reconhecer o que está instalado.

*Em detalhe técnico:* três arquivos, sem nenhuma dependência da
Interface descrita acima (que continua pendente):

```
app/
  src/main/AndroidManifest.xml   declaração do pacote, da Application
                                  e da Activity de entrada
  src/main/kotlin/org/nexo/motor/app/
    NexoMotorApplication.kt      subclasse mínima de Application
    MainActivity.kt              subclasse mínima de Activity, sem
                                  tela (nenhum setContentView) —
                                  aparência fica pra quando a pendência
                                  de desenho visual (ver tasks.md) for
                                  resolvida
```

Versão mínima de Android aceita, versão usada como alvo/compilação, e
versão do Android Gradle Plugin (a ferramenta que ensina o Gradle a
montar um projeto Android, além do Kotlin puro que `core` já usa):
decididas em
[decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>).

Testado só por build real (`gradlew :app:assembleDebug`) e instalação
num emulador — sem teste automatizado ainda, porque não há nenhum
comportamento pra testar além de "o pacote instala e abre".

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
| 0.8.0 | 14-08-2026 | Acrescentado o esqueleto mínimo do módulo `app` (manifesto, `Application`, `Activity` sem tela) e as versões de plataforma/build que ele usa. | Resolução de [decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>) |
| 0.9.0 | 14-08-2026 | Acrescentado o desenho interno do pacote `content` (nome fixo do manifesto, validação por árvore JSON genérica item a item, regra de fotograma malformado, unicidade de `tag_id` em todo o pacote); removida a menção a `content` como "ainda não desenhado" na seção do pacote `session`. | Resolução de [decisions/0013-desenho-do-pacote-content.md](<../decisions/0013-desenho-do-pacote-content.md>) |
| 0.10.0 | 14-08-2026 | Seção do pacote `content` revisada: regra de aceitação passa a ser tudo ou nada (qualquer violação recusa o pacote inteiro), substituindo a exclusão item a item descrita na versão anterior. | Revisão de [decisions/0013-desenho-do-pacote-content.md](<../decisions/0013-desenho-do-pacote-content.md>), em conversa direta antes de dar a tarefa como concluída |
| 0.11.0 | 14-08-2026 | Acrescentado o quarto ponto do desenho de `search` (comportamento com termo vazio). | Resolução de [decisions/0014-busca-aproximada-com-termo-vazio.md](<../decisions/0014-busca-aproximada-com-termo-vazio.md>) |
| 0.12.0 | 14-08-2026 | Acrescentado o desenho interno do pacote `connectivity` (contrato puro em `core`, UUIDs do Nordic UART Service, decodificação de identificador físico, formato do dado transmitido pelo acessório) e a fronteira com o `Service`/`Activity` de `app` que hospedam o código real de Bluetooth e NFC; acrescentado pacote `connectivity` na árvore de `app`. | Resolução de [decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>) e [decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md](<../decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md>) |
| 0.13.0 | 14-08-2026 | Registrado que a escolha entre os dois caminhos de leitura (NFC direto ou acessório por Bluetooth) é da pessoa, nunca automática do aplicativo — ajustadas as duas citações de DA-LEI-03 em "Núcleo do motor" e na seção `connectivity` que sugeriam o contrário; acrescentada a estratégia de permissão de Bluetooth e NFC (declarações de manifesto, momento do pedido). | Resolução de [decisions/0017-quem-decide-a-tecnologia-de-leitura.md](<../decisions/0017-quem-decide-a-tecnologia-de-leitura.md>) e [decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md](<../decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md>) |
| 0.14.0 | 15-08-2026 | Acrescentada a API do lado `app` do pacote `connectivity` (`PieceReadListener.kt`, `BluetoothPermissions.kt`, `BleAccessoryService.kt`) e a leitura NFC em `MainActivity`; testado por compilação real. | Escrita do lado `app` do pacote `connectivity` |
| 0.15.0 | 15-08-2026 | Acrescentado `ConnectionState` (`core`) e `ConnectionStateListener` (`app`) — o `Service` de Bluetooth passa a avisar quando muda de estado (procurando/conectado/desconectado), não só quando lê uma peça. | Pendência "como a pessoa sabe se está conectado", parte de dado (sem aparência) |
| 0.16.0 | 15-08-2026 | Acrescentado o desenho interno do pacote `report` (geração de CSV e PDF sem biblioteca externa, guarda na pasta pública "Downloads" do aparelho por dois caminhos conforme a versão do Android, atalho de compartilhar na tela de resultado); acrescentado pacote `report` na árvore de `app`; ajustado o ponteiro na seção do pacote `session` que citava `report` como "ainda não desenhado". | Resolução de [decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>) |
| 0.17.0 | 15-08-2026 | Acrescentada a seção "Ligação com o núcleo do motor" (ViewModel que guarda o estado da sessão, alimentado por função direta a partir de `MainActivity`/`BleAccessoryService`, nunca por referência à tela ou ao `Service`); ajustado o parágrafo do pacote `connectivity` que apontava esse consumo como pendência. | Resolução de [decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md](<../decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md>) |
| 0.18.0 | 15-08-2026 | Acrescentado o desenho interno do pacote `summary` (mensagem de pulo como dado organizado, síntese sem pulo concatenando `summary_fragment`) e o pacote `summary` na árvore de `core`; ajustado o parágrafo do pacote `session` que citava a montagem de texto como responsabilidade indefinida de `content`. | Resolução de [decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>) |
| 0.19.0 | 15-08-2026 | Seção do pacote `session` revisada: `SessionEvent` ganha `timestamp` em toda variante e dois tipos novos (`StudySuggestionShown`, `WentIdle`, ao lado do `Paused` já existente); API pública atualizada com `showStudySuggestion` e `goIdle`. | Nota de acompanhamento em [decisions/0008](<../decisions/0008-representacao-do-estado-da-sessao.md>), achado [findings.md#2026-08-15-registro-de-sessao-incompleto-frente-a-ei-reg-01](<findings.md#2026-08-15-registro-de-sessao-incompleto-frente-a-ei-reg-01>) |
| 0.20.0 | 15-08-2026 | Seção do pacote `summary` ganha a API pública (`buildSkipMessage`, `buildChainSkipSynthesis`, `buildContinuousSynthesis`), que faltava. | Escrita do código-fonte do pacote `summary` |
| 0.21.0 | 15-08-2026 | Seção do pacote `report` reescrita: dividido entre `core/report/` (dado puro) e `app/report/` (desenho do PDF, escrita no aparelho, atalho de compartilhar), corrigindo a afirmação de que o pacote inteiro ficaria sem depender de Android. | Nota de acompanhamento em [decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>), achado [findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core](<findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core>) |
| 0.22.0 | 15-08-2026 | Acrescentada `referenceImage` na API do pacote `session` (EI-SES-04); seção "Ligação com o núcleo do motor" reescrita com a API real de `app/ui/SessionViewModel.kt` e `SessionUiState.kt`, substituindo a descrição abstrata do mecanismo; `onExitConfirmed` já apaga o estado retomável da sessão; registrados os dois pontos que ainda faltam escrever (chamar a geração do relatório de saída a partir da tela de resultado, gatilho de ociosidade). | Resolução de [decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>); testado por compilação real (`:app:assembleDebug`, `:core:test`) |
