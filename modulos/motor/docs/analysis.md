# Analysis — Motor

<!-- module-doc-type: analysis -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Analysis |
| Versão | 0.24.0 |
| Data | 31-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Registro datado de como uma investigação foi feita neste módulo — o
> que foi lido, o que foi checado, o raciocínio seguido. Acontece junto
> com `findings.md`, não numa etapa depois — é o relato de como se
> chegou a cada achado. É o único lugar onde narrativa de processo é
> esperada; por existir aqui, `concept.md` nunca precisa carregar esse
> tipo de conteúdo. Cada entrada é datada e não é reescrita depois — uma
> investigação nova ganha entrada nova.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, campo `Levou a` com link pro achado gerado (ou
> `ainda sem conclusão`), resumo simples, depois detalhe técnico.

## Índice
- [Investigações](#investigacoes)
- [Controle de versão](#controle-de-versão)

## Investigações

### <a id="2026-08-14-esqueleto-minimo-do-modulo-app"></a>2026-08-14 — Esqueleto mínimo do módulo `app`

**Levou a:** [decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>),
[pitfalls.md#2026-08-14-agp-9-nao-aceita-mais-plugin-kotlin-android](<pitfalls.md#2026-08-14-agp-9-nao-aceita-mais-plugin-kotlin-android>),
[pitfalls.md#2026-08-14-kotlin-embutido-do-agp-exige-versao-igual-no-core](<pitfalls.md#2026-08-14-kotlin-embutido-do-agp-exige-versao-igual-no-core>)

*Resumo simples:* o esqueleto do módulo `app` (manifesto,
`Application`, `Activity` sem tela) foi escrito, compilado e aberto de
verdade num emulador Android — não só "deveria funcionar". Duas
versões de ferramenta de build precisaram ser ajustadas no caminho
(ver `pitfalls.md`).

*Detalhe técnico:*
- SDK de linha de comando do Android instalado (`cmdline-tools`,
  `platform-tools`, plataforma 36, `build-tools` 36.0.0, `emulator`,
  imagem de sistema `system-images;android-34;google_apis;x86_64`),
  licenças aceitas, AVD `nexo_motor_test` criado (perfil Pixel 6) —
  pré-requisito de ambiente pra qualquer um dos passos abaixo.
- Pesquisa direta em fontes oficiais (developer.android.com, acesso em
  14 ago. 2026) pra decidir `minSdk`, `compileSdk`/`targetSdk` e a
  versão do Android Gradle Plugin — detalhe completo e alternativas
  descartadas em
  [decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>).
- Duas tentativas de build falharam antes de compilar de verdade,
  ambas por comportamento de ferramenta não documentado em nenhum
  lugar do projeto até agora: o plugin `org.jetbrains.kotlin.android`
  não é mais aceito a partir do AGP 9 (suporte a Kotlin embutido), e o
  Kotlin embutido no AGP 9.3.0 não lê código compilado com uma versão
  de Kotlin mais nova que a que ele mesmo traz — obrigou a descer a
  versão de Kotlin do `core` de 2.4.10 pra 2.2.10. As duas ficaram
  registradas em `pitfalls.md`, não só corrigidas em silêncio.
- Depois da correção: `gradlew :core:test` (suíte já existente do
  `core`, três pacotes) rodada de novo, sem quebra — confirma que
  baixar a versão do Kotlin não mudou nenhum comportamento.
  `gradlew :app:assembleDebug` compilou com sucesso.
- Teste ao vivo: `gradlew :app:installDebug` instalou o APK de debug
  no emulador; `adb shell am start -n org.nexo.motor.app/.MainActivity`
  abriu a tela; `adb shell dumpsys activity activities` confirmou
  `topResumedActivity=...org.nexo.motor.app/.MainActivity` (em
  primeiro plano) e o processo rodando (`adb shell pidof
  org.nexo.motor.app`); `adb logcat` do processo sem nenhum erro ou
  encerramento inesperado, só avisos de desempenho normais de emulador
  com renderização por software (SwiftShader).

### <a id="2026-08-14-revisao-de-cobertura-de-teste-dos-pacotes-core"></a>2026-08-14 — Revisão de cobertura de teste dos pacotes `search`, `hierarchy`, `session` e `content`

**Levou a:** ainda sem conclusão

*Resumo simples:* checagem de cada teste automatizado já existente
nesses quatro pacotes contra a cascata de documentação (não contra o
código de implementação, nunca aberto durante esta investigação), pra
responder se o teste prova mesmo a regra documentada ou só espelha o
que o código já fazia. A maioria dos testes já cita, no próprio nome,
o requisito que prova, e a asserção bate com o texto do documento —
nenhum sinal de teste ajustado ao resultado do código. Cinco pontos
específicos, documentados mas sem nenhum teste provando-os, foram
encontrados e fechados com teste novo.

*Detalhe técnico:*
- Leitura completa, antes de abrir qualquer teste: os cinco documentos
  da cascata aprovada (1 a 5), as ADRs 0004, 0005, 0007 a 0011 e 0013,
  `findings.md`, `pitfalls.md` e `architecture.md` — pra ter, de forma
  independente do código, o que cada pacote deveria fazer antes de ler
  o que cada teste afirma.
- Método: comparar só o nome e o corpo de cada teste com a regra
  documentada correspondente — nunca abrir `core/src/main/` durante a
  checagem, pra não correr o risco de confirmar que o teste bate com o
  que o código já faz, em vez de checar se bate com o que o código
  deveria fazer.
- Cinco lacunas encontradas, cada uma com requisito ou decisão já
  documentada mas nenhum teste cobrindo:
  1. `search`: nenhum teste provava que um item com distância acima do
     limiar (PD-NAV-02) fica de fora do resultado — só a função do
     limiar em si era testada isoladamente.
  2. `session`: `useHint`, listada em
     [architecture.md](<architecture.md#pacote-session--desenho-interno>)
     como parte da API pública de `SessionTransitions.kt`, nunca era
     chamada por nenhum teste.
  3. `session`: `deleteSessionState`, também listada em
     `architecture.md`, ligada a "sair apaga o progresso retomável"
     (EI-PAU-03), nunca era chamada por nenhum teste.
  4. `content`: a "varredura completa, não fail-fast" (decisão 3 da
     [ADR 0013](<../decisions/0013-desenho-do-pacote-content.md>)) nunca
     era comprovada com duas violações independentes ocorrendo juntas
     na mesma importação — todo teste existente provocava só um
     problema de cada vez.
  5. `session`: um teste já existente, citando EI-VAL-03 ("origem da
     peça não é considerada"), na verdade não podia provar isso —
     `recordAttempt` nunca recebe "origem" como parâmetro, só a peça
     apresentada e a esperada. O teste foi reescrito pra afirmar só o
     que de fato checa (duas peças erradas quaisquer recebem o mesmo
     tratamento), sem trocar o comportamento provado.
- Teste novo escrito pra cada um dos cinco pontos, sempre a partir da
  regra documentada, nunca do resultado observado do código.
  `gradlew :core:test` rodado de verdade depois: 81 testes (77 antes),
  0 falhas — nenhuma divergência de comportamento encontrada; a
  implementação já atendia todos os cinco pontos, só faltava o teste
  escrito provando isso.

### <a id="2026-08-14-comportamento-de-busca-aproximada-com-termo-vazio"></a>2026-08-14 — Comportamento da busca aproximada com termo vazio

**Levou a:** [decisions/0014-busca-aproximada-com-termo-vazio.md](<../decisions/0014-busca-aproximada-com-termo-vazio.md>)

*Resumo simples:* um dos seis pontos levantados na investigação
anterior (comportamento de `approximateSearch` com termo vazio, ver
entrada acima) tinha ficado de fora, sem decisão, por uma dúvida real:
isso depende do desenho de uma tela que ainda não existe, ou é uma
decisão do próprio pacote `search`, independente de qualquer tela? A
checagem mostrou que é a segunda — a função já roda hoje, já tem teste
próprio, e o pacote é desenhado pra nunca depender de interface
(RNF-MOD-01), então o contrato dela precisa estar decidido de qualquer
jeito, com ou sem tela.

*Detalhe técnico:*
- Releitura de [decisions/0004](<../decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md>)
  confirmou o precedente direto: os três pontos que essa ADR resolve
  também não vêm de nenhuma tela — vêm da necessidade do próprio
  algoritmo de ter uma resposta pra rodar. Termo vazio é um quarto
  ponto do mesmo tipo, não um ponto de categoria diferente.
- Duas alternativas reais comparadas: devolver a lista de entrada sem
  filtro, ou devolver lista vazia.
- Cinco fontes externas pesquisadas, cobrindo os dois lados da escolha
  e o cenário mais parecido tecnicamente com este pacote (busca
  aproximada sobre lista em memória, sem servidor). Duas trouxeram
  conteúdo oficial verificável, com citação exata confirmada direto na
  fonte: documentação do Elasticsearch (parâmetro `zero_terms_query`,
  padrão devolve nenhum resultado) e documentação do Algolia
  (InstantSearch, padrão devolve todos os resultados). A do
  Elasticsearch resolve um problema diferente do deste pacote (consulta
  de texto contra servidor remoto, "zero termos" geralmente por filtro
  de palavra irrelevante, não filtro de lista em memória) e não entra
  na ADR. As outras três fontes checadas (Fuse.js — biblioteca de busca
  aproximada em memória, o caso tecnicamente mais parecido com
  `search`, mas só confirmada por discussão de comunidade, não por
  página oficial, e no sentido contrário à escolha feita aqui;
  documentação do componente de busca do Material Design, sem texto
  extraível pela ferramenta de leitura automatizada, mesma limitação já
  registrada em `architecture.md`; `SearchView` do Android, que
  documenta um padrão de busca por confirmação, não o filtro em tempo
  real que a categoria NAV do motor vai precisar) não trouxeram
  conteúdo oficial verificável o bastante pra citar.
- Prática de mercado não aponta um lado único pro caso específico deste
  pacote — a decisão em [decisions/0014](<../decisions/0014-busca-aproximada-com-termo-vazio.md>)
  se apoia principalmente na mesma lógica já registrada na decisão 2 da
  ADR 0004, com o precedente do Algolia como reforço (peso técnico
  limitado: escolha de produto, sem justificativa declarada), não como
  base única.
- Nenhuma mudança de código foi necessária — a implementação já
  existente, e o teste já existente antes desta tarefa, já seguiam a
  alternativa escolhida; o teste só ganhou a referência explícita à
  ADR nova.
- Checagem mecânica ao abrir `decisions/README.md` do módulo pra
  acrescentar a ADR 0014: as ADRs 0012 e 0013 já existiam como arquivo
  mas nunca tinham entrado no índice — corrigido junto, retroativamente.

### <a id="2026-08-15-verificacao-de-exigencia-de-homologacao-anatel"></a>2026-08-15 — Verificação de exigência de homologação ANATEL pro acessório leitor

**Levou a:** [tasks.md, Em aberto](<tasks.md#em-aberto>) (pendência
nova, sem decisão ainda)

*Resumo simples:* investigação nascida durante a pesquisa sobre a
descontinuação do chip leitor NFC (PD-LEI-01) — ao comparar peça
genuína contra cópia/clone, pra um projeto que não nasce só como
piloto descartável, apareceu uma exigência legal nunca antes
considerada por nenhum documento da cascata: qualquer equipamento com
Bluetooth ou NFC ativo, pra ser usado ou vendido no Brasil, depende de
homologação da ANATEL, a agência reguladora de telecomunicação do
país.

*Detalhe técnico:*
- Ponto de partida: pesquisa de mercado sobre disponibilidade do NXP
  PN532/C1 e de substitutos (PN7150/PN7160, MFRC522) — cobrindo status
  de ciclo de vida do fabricante, loja brasileira e internacional, e a
  diferença entre chip genuíno e clone. A pergunta "qual o problema
  real de usar um chip clone" levou direto a esta checagem: não é só
  questão de confiabilidade de peça, é exigência legal formal.
- Fontes oficiais lidas por completo, sem ferramenta de resumo,
  seguindo pedido direto de checar fonte primária, nunca resultado de
  busca: Lei nº 9.472, de 16 de julho de 1997, art. 162, §2º;
  Resolução ANATEL nº 715/2019, art. 1º e art. 55; Resolução ANATEL nº
  680/2017, texto integral — as três conferidas como cópia genuína
  (a última, pela estrutura padrão de norma: artigos, parágrafos,
  marcações "Redação dada pela Resolução nº..." de cada atualização
  histórica).
- Enquadramento do achado dentro de engenharia de requisitos: a norma
  ISO/IEC/IEEE 29148:2018 (*Systems and software engineering — Life
  cycle processes — Requirements engineering*) trata restrição legal e
  regulatória como parte do processo de análise de negócio/missão — a
  etapa mais próxima do que os documentos 1 e 2 da cascata (Conceito e
  Requisitos) deste projeto cobrem. Reforça que o lugar "natural" pra
  esse tipo de exigência seria bem no início da cascata, não no
  Projeto Detalhado; mas, como a cascata já está aprovada e é imutável
  (ver
  [docs/docs-VMODEL-visao-geral/README.md](<../../../docs/docs-VMODEL-visao-geral/README.md>)),
  o achado fica registrado aqui e em `tasks.md`, nunca inserido
  retroativamente nos documentos 1 ou 2. Título, número e escopo geral
  confirmados via página oficial do IEEE (`standards.ieee.org`).
- Resolução 680/2017 não cita "Bluetooth" nem "NFC" pelo nome — a
  ligação entre essas tecnologias e as faixas de frequência listadas
  no Anexo I dela (2.400-2.483,5 MHz pro Bluetooth, 13,41-14,01 MHz
  cobrindo os 13,56 MHz do NFC já fixados em PD-LEI-01) é inferência
  técnica desta investigação, não citação literal da norma.

### <a id="2026-08-15-revisao-do-registro-de-sessao-contra-ei-reg-01"></a>2026-08-15 — Revisão do registro interno de `session` contra EI-REG-01

**Levou a:** [findings.md#2026-08-15-registro-de-sessao-incompleto-frente-a-ei-reg-01](<findings.md#2026-08-15-registro-de-sessao-incompleto-frente-a-ei-reg-01>)

*Resumo simples:* antes de escrever o pacote `report` (que monta o
relatório final a partir do registro que `session` mantém), foi
checado se esse registro já carrega tudo que o relatório precisa
mostrar. Não carregava — faltavam três coisas, cada uma confirmada
lendo o texto normativo já aprovado, não por suposição.

*Detalhe técnico:*
- Leitura completa, nesta ordem, antes de qualquer conclusão: Conceito
  (seções 6, 12 e 14), Requisitos (RF-PAU-01, RF-REG-01, RF-CFG-01),
  Especificação (EI-REG-01, EI-PAU-01, EI-DIC-03), Projeto
  Arquitetônico (DA-REG-01 a 03) e
  [decisions/0008](<../decisions/0008-representacao-do-estado-da-sessao.md>)
  — a ADR que desenhou o registro interno de `session` e afirma cobrir
  "exatamente os fatos que EI-REG-01 já exige".
- Comparação, item a item, entre o que EI-REG-01 lista como exigido no
  registro ("tentativa aceita, tentativa rejeitada, dica usada,
  sugestão de estudo exibida, posição pulada, pausa ou ociosidade
  acionada... com a posição, o evento, e o momento em que cada um
  ocorreu") e o que `SessionState.kt`/`SessionTransitions.kt` (antes
  desta correção) de fato registravam. Três lacunas confirmadas:
  1. Nenhum campo de horário existia em `SessionEvent`, embora
     EI-REG-01 e RF-REG-01 exijam "o momento em que cada [fato]
     ocorreu".
  2. `studySuggestionAvailable` calculava só se a sugestão de estudo
     *podia* aparecer (um booleano) — nenhuma função gravava, no
     registro, o momento em que ela *de fato* apareceu na tela, embora
     EI-REG-01 liste "sugestão de estudo exibida" como um dos seis
     fatos a registrar.
  3. Existia um único tipo de evento (`Paused`) para as duas situações
     de interrupção (pausa explícita e ociosidade). Uma primeira
     leitura, só da Especificação, concluiu que isso bastava — conclusão
     corrigida depois de ler também o Conceito (seção 12: "a única
     diferença entre as duas é o que dispara cada uma") e os Requisitos
     (RF-PAU-01, mesma frase; RF-REG-01, que lista "pausa" e
     "ociosidade" como duas categorias separadas na mesma enumeração em
     que "erro" e "pulo" também são duas categorias separadas) — as três
     fontes, lidas juntas, deixam claro que o gatilho é o dado que
     precisa ficar registrado, não só o efeito comum às duas.
- Nenhuma das três lacunas envolveu escolher entre alternativas de
  desenho reais — as três são casos de completar, no código, uma
  exigência que já estava no texto normativo antes deste código
  existir. Por isso a correção virou achado (`findings.md`) e nota de
  acompanhamento em
  [decisions/0008](<../decisions/0008-representacao-do-estado-da-sessao.md>),
  nunca uma ADR nova.

### <a id="2026-08-15-pdfdocument-incompativel-com-modulo-core-kotlin-puro"></a>2026-08-15 — `PdfDocument` incompatível com o módulo `core` (Kotlin puro)

**Levou a:** [findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core](<findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core>)

*Resumo simples:* antes de escrever o pacote `report`, foi conferido
se a ferramenta de PDF já escolhida em
[decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>)
(`android.graphics.pdf.PdfDocument`) realmente compila dentro do
módulo `core`, do jeito que a própria ADR afirma. Não compila — é uma
classe exclusiva do Android, e `core` é montado sem nada de Android.

*Detalhe técnico:*
- Leitura de `modulos/motor/core/build.gradle.kts`: só os plugins
  `kotlinJvm` e `kotlinSerialization` — nenhum plugin Android
  (`com.android.library`), nenhuma dependência do SDK do Android.
  Mesma estrutura já fixada em
  [decisions/0003](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>):
  `core` é Kotlin puro, `app` depende de `core`, nunca o contrário.
- `android.graphics.pdf.PdfDocument` e `android.graphics.Canvas`
  pertencem ao pacote `android.*`, disponível só quando o módulo Gradle
  usa o plugin Android (`compileSdk` configurado) — um módulo `kotlinJvm`
  puro não enxerga essas classes em tempo de compilação, diferente de
  `java.util.zip.ZipFile` (usado em `content`) ou `java.io.File` (usado
  em `session`), que são parte do Java SE comum, disponíveis em
  qualquer JVM.
- [decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>)
  (Decisão, item 1) manda usar `PdfDocument` dentro do que a mesma ADR
  (Consequências) descreve como a parte de `report` "sem depender de
  classe do Android, testável como os demais pacotes de `core`" — as
  duas afirmações não podem ser verdadeiras ao mesmo tempo.
- Precedente direto já existente no próprio módulo, pro mesmo tipo de
  problema: [decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>)
  já dividiu `connectivity` entre um contrato puro em `core` e a classe
  que gerencia `BluetoothGatt` (Android) em `app`, exatamente porque
  essa classe também não compila em `core`.
- Confirmado ao vivo: escrever o pacote `report` dividido do mesmo jeito
  (`core/report/` só monta dado — texto do CSV, lista de linhas do PDF
  — sem nenhuma classe do Android; `app/report/` desenha o PDF de
  verdade com `PdfDocument`/`Canvas`, escreve os dois arquivos e monta o
  atalho de compartilhar) e rodar `gradlew :app:assembleDebug` — `BUILD
  SUCCESSFUL`, confirmando que a divisão resolve o problema.
- Não envolveu escolher entre alternativas reais de ferramenta —
  `PdfDocument` continua sendo a ferramenta certa, só corrige onde o
  código que a usa mora. Por isso virou achado (`findings.md`) e nota
  de acompanhamento em
  [decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>),
  nunca uma ADR nova.

### <a id="2026-08-16-pesquisa-de-ferramenta-de-teste-pro-modulo-app"></a>2026-08-16 — Pesquisa de ferramenta de teste pros cinco pontos pendentes do módulo `app`

**Levou a:** [decisions/0025-ferramenta-de-teste-do-modulo-app.md](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>)

*Resumo simples:* investigação sobre como testar de verdade os cinco
pontos que `tasks.md` já lista como pendentes (`BleAccessoryService.kt`,
`MainActivity.kt`, `ReportPdfRenderer.kt`,
`ReportFileWriter.kt`/`ReportShareIntent.kt`, `SessionViewModel.kt`).
Três dos cinco têm simulação pronta no Robolectric, confirmada lendo o
código-fonte oficial dele, arquivo por arquivo. `SessionViewModel.kt`
não precisa de nenhuma simulação de Android — não toca em nenhuma
classe do Android, então roda com `kotlin-test-junit` puro. Só o
desenho do PDF (`ReportPdfRenderer.kt`) continua sem nenhuma simulação,
exigindo teste instrumentado.

*Detalhe técnico:*
- JUnit e versão de build, checados de novo, sem presumir a resposta:
  o guia oficial de início rápido do Robolectric
  (`robolectric.org/getting-started/`) usa `junit:junit:4.13.2` como
  exemplo de dependência; a tabela de compatibilidade oficial
  (`robolectric.org/compatibility_table/`) lista a versão estável mais
  recente (4.16) testada contra AGP 8.12.0 e Gradle 8.14.3 — mais
  antigos que os 9.3.0/9.7.0 já usados neste projeto
  ([decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>)).
  Lançamentos mais novos checados direto na API do GitHub
  (`gh api repos/robolectric/robolectric/releases`): a estável mais
  recente é 4.16.1 (21 jan. 2026), com 4.17-beta-1/2 já em teste (jul.
  2026) — nenhum dos dois traz nota de lançamento sobre AGP 9.x.
  Mitigado, não eliminado, confirmando ao vivo, hoje, o catálogo de
  versão real do "Now in Android" (`gradle/libs.versions.toml`,
  repositório oficial do time do Android,
  `github.com/android/nowinandroid`): usa Robolectric 4.16 junto com
  AGP 9.0.0 e Kotlin 2.3.0, em produção, agora.
- `BleAccessoryService.kt` (Bluetooth): `ShadowBluetoothGatt.java` e
  `ShadowBluetoothLeScanner.java` existem na pasta de shadows oficial
  do Robolectric.
- `MainActivity.kt` (NFC): `ShadowNfcAdapter.java` existe, com dois
  métodos que resolvem exatamente o problema que faltava — o de como
  montar uma etiqueta de teste, nunca resolvido antes: `public static
  Tag createMockTag()` e `public void dispatchTagDiscovered(Tag tag)`.
- Conferido o contrato real de `BleAccessoryService.kt`/`MainActivity.kt`
  contra
  [decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>)/[decisions/0016](<../decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md>)
  e `architecture.md`, em vez de presumir que "o Robolectric roda contra
  essas classes" já bastava: o contrato das duas não é validar a
  tentativa (`EI-VAL-02`, já testada dentro de `session`) — é decodificar
  o aviso bruto recebido e repassar pro `PieceReadListener`. Conferido
  também que a decodificação em si (`tagIdFromBytes`) já está isolada
  numa função pura, em `core/connectivity`, com teste próprio. Conclusão:
  falta provar só que `onCharacteristicChanged`/`onTagDiscovered` chamam
  `tagIdFromBytes` com o dado certo e repassam o resultado certo pro
  `PieceReadListener`, simulando uma notificação/etiqueta de verdade
  chegando. Considerada a alternativa de isolar as duas classes atrás de
  uma interface (injeção de dependência) — não necessária pra esta ADR,
  porque não mudaria esse alvo (a parte que decodifica já está separada
  e testada, só mudaria onde o teste chama a simulação do Android);
  continua válida por outro motivo (reduzir acoplamento com classe do
  Android, já recomendado em decisions/0015), registrada como pendência
  própria em `tasks.md`, pra outro momento.
- `ReportFileWriter.kt` (escrita/compartilhamento de arquivo, nunca
  pesquisado antes): `ShadowContentResolver.java` tem
  `registerOutputStream(Uri, OutputStream)` e
  `registerOutputStreamSupplier(Uri, Supplier<OutputStream>)` — cobre o
  caminho novo (`MediaStore.Downloads`, Android 10+). `ShadowEnvironment.java`
  tem `setExternalStoragePublicDirectory(Path)` — cobre o caminho
  antigo (Android 7 a 9,
  [decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>)).
  `ReportShareIntent.kt` só monta um `Intent`/`Uri` comum — classes já
  simuladas por padrão em qualquer teste Robolectric, sem exigir
  pesquisa própria. Conferido, mesma lógica do bullet acima, o que os
  dois arquivos precisam provar segundo documento 4 (DA-ARM-01,
  DA-ARM-02, checados direto na fonte): que o conteúdo termina só no
  armazenamento local do aparelho (DA-ARM-01), e que
  `buildReportShareIntent` nunca dispara nada sozinho, só monta e
  devolve o `Intent` — quem decide se e quando ele é usado é sempre a
  pessoa (DA-ARM-02). Confirmado que
  `registerOutputStream`/`registerOutputStreamSupplier` (Robolectric)
  permitem checar exatamente o conteúdo que chegou no destino local
  simulado, sem precisar de nada externo, cobrindo a primeira exigência;
  a segunda já é garantida pela própria forma da função (devolve
  `Intent`, nunca chama `startActivity`), sem depender de nenhuma
  simulação pra ser verdade.
- `ReportPdfRenderer.kt` (PDF): `ShadowPdfDocument.java` não existe —
  confirmado direto, pedindo o arquivo pela API do GitHub e recebendo
  `404 Not Found`. Continua precisando de teste instrumentado (aparelho
  ou emulador real) — versão estável atual das três bibliotecas que
  isso exige, checada na página oficial de lançamentos do Android
  Jetpack (`developer.android.com/jetpack/androidx/releases/test`):
  `androidx.test:core` 1.7.0, `androidx.test.ext:junit` 1.3.0,
  `androidx.test:runner` 1.7.0.
- `SessionViewModel.kt` (`onExitConfirmed`, gatilho de ociosidade): sem
  pesquisa nova — [decisions/0020](<../decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md>)
  e [decisions/0023](<../decisions/0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md>),
  já aceitas, já resolvem sozinhas que nenhuma classe do Android está
  envolvida.

### <a id="2026-08-16-implementacao-do-teste-de-mainactivity-nfc"></a>2026-08-16 — Implementação do teste de `MainActivity.kt` (NFC)

**Levou a:** [pitfalls.md#2026-08-16-nfcadapter-getdefaultadapter-nulo-sem-feature-nfc](<pitfalls.md#2026-08-16-nfcadapter-getdefaultadapter-nulo-sem-feature-nfc>),
[pitfalls.md#2026-08-16-shadownfcadapter-createmocktag-nao-aceita-id](<pitfalls.md#2026-08-16-shadownfcadapter-createmocktag-nao-aceita-id>)

*Resumo simples:* primeiro teste escrito de verdade, a partir da
ferramenta já decidida em [decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>):
prova que a leitura NFC de `MainActivity.kt` decodifica o identificador
bruto da etiqueta e repassa pro `PieceReadListener`, conforme já
documentado. A assinatura de cada função chamada veio só de
`architecture.md` (nunca do `.kt` de implementação); a montagem em si
esbarrou duas vezes em comportamento não óbvio do Robolectric, as duas
resolvidas conferindo o código-fonte oficial da ferramenta, nunca o do
NEXO — registradas em `pitfalls.md`, não aqui, por serem comportamento
de ferramenta, não achado sobre o código do NEXO.

*Detalhe técnico:*
- Alvo do teste fundamentado direto no requisito: EI-VAL-02 (documento
  3) já mora e já é testada dentro de `session`; o que faltava provar
  aqui, especificamente, é que `onTagDiscovered` chama `tagIdFromBytes`
  com o dado certo da etiqueta e repassa o resultado certo pro
  `PieceReadListener` — mesmo raciocínio já registrado na investigação
  de 16/08 sobre a ferramenta de teste, agora aplicado na escrita real.
- `testOptions { unitTests { isIncludeAndroidResources = true } }`
  acrescentado a `app/build.gradle.kts`, e `junit:junit`/`org.robolectric:robolectric`
  acrescentados a `gradle/libs.versions.toml` e como `testImplementation`
  — nenhum dos dois existia antes desta tarefa (decisions/0025 só
  decidiu a ferramenta, não configurou o build).
- Primeira tentativa de compilação falhou em três pontos: import de
  `kotlin.test` (não existe no classpath de teste do `app`, só em
  `core` — corrigido pra `org.junit.Assert`); `ShadowNfcAdapter.createMockTag()`
  chamado com um argumento que ele não aceita (assinatura real
  confirmada lendo `ShadowNfcAdapter.java` direto do repositório oficial
  do Robolectric, `gh api`, branch `master` — não aceita identificador
  nenhum, sempre cria com `id` vazio).
- Corrigido chamando o método estático oculto por trás dele
  (`android.nfc.Tag.createMockTag`) diretamente, pela mesma técnica de
  reflexão que o próprio Robolectric usa por dentro
  (`ReflectionHelpers.callStaticMethod`), com o `id` desejado — a
  assinatura desse método muda a partir da versão de Android seguinte
  ao Tiramisu (parâmetro `cookie` novo), tratado com a mesma
  ramificação por `RuntimeEnvironment.getApiLevel()` que o Robolectric
  usa.
- Segunda falha, em tempo de execução, não de compilação:
  `NfcAdapter.getDefaultAdapter(activity)` devolvendo `null`. Rastreado
  até o `PackageManager` simulado do Robolectric não considerar a
  característica de hardware NFC presente só por ela estar declarada
  `android:required="false"` no manifesto — precisa ser ligada
  explicitamente no teste (`Shadows.shadowOf(packageManager).setSystemFeature(...)`).
- Depois das duas correções, `gradlew :app:testDebugUnitTest --tests
  MainActivityTest` rodado de verdade: `BUILD SUCCESSFUL`, teste
  passando.
- Achado à parte, fora do escopo de teste em si: o nome exato da
  propriedade que expõe o `PieceReadListener` em `MainActivity`
  (`pieceReadListener: PieceReadListener?`) nunca tinha sido registrado
  em `architecture.md` com a mesma precisão que o resto do documento
  usa pra API pública — só prosa solta ("exposto por ela"). Corrigido
  em `architecture.md` (0.27.0), confirmado por este teste.

### <a id="2026-08-17-implementacao-do-teste-de-bleaccessoryservice-bluetooth"></a>2026-08-17 — Implementação do teste de `BleAccessoryService.kt` (Bluetooth)

**Levou a:** [pitfalls.md#2026-08-17-scanrecord-parsefrombytes-e-metodo-oculto](<pitfalls.md#2026-08-17-scanrecord-parsefrombytes-e-metodo-oculto>)

*Resumo simples:* segundo teste escrito de verdade da mesma ferramenta
já decidida em [decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>):
prova o mesmo alvo já fundamentado na investigação de 16/08 (EI-VAL-02
— `onCharacteristicChanged` decodifica o identificador bruto recebido
por Bluetooth e repassa pro `PieceReadListener`), agora implementado de
verdade. Diferente do teste de `MainActivity.kt`, a assinatura de duas
partes específicas — como plugar o escutador, e qual das duas formas
de `onCharacteristicChanged` a classe implementa — não estava
disponível em nenhum documento, exigindo abrir o `.kt` só pra confirmar
essas duas assinaturas (não pra copiar comportamento pro teste).

*Detalhe técnico:*
- Tentativa inicial, só com o que `architecture.md` já documentava:
  `service.pieceReadListener = ...` (mesmo padrão de `MainActivity`)
  falhou na compilação, propriedade privada. Confirmado abrindo
  `BleAccessoryService.kt`: os dois escutadores têm, cada um, um método
  público próprio já pronto (`setPieceReadListener`,
  `setConnectionStateListener`), e o `onCharacteristicChanged`
  implementado é a versão antiga (dois parâmetros, sem `value`,
  lendo `characteristic.value`), a mesma que `pitfalls.md` já registra
  como escolha deliberada deste arquivo (dar suporte a partir do
  Android 7, antes da versão nova existir).
- Checado se esse padrão (método público direto no `Service`, `Binder`
  só devolvendo a instância) é documentado ou improvisado: é o padrão
  oficial — a mesma fonte já pesquisada em 16/08 ("Bound services
  overview") diz "clients call public methods in the service", sem
  distinguir propriedade de método; o exemplo de referência do Google
  pra "conectar a um servidor GATT" (já citado em `decisions/0015`)
  usa a mesma estrutura de `Service` vinculado carregando toda a lógica
  de Bluetooth que este arquivo usa.
- Montado um `ScanResult` de teste anunciando de verdade o UUID do
  Nordic UART Service (bytes de propaganda BLE reais, não um resultado
  qualquer), pra funcionar independente de `startScanAndConnect()`
  filtrar sozinho ou deixar o sistema filtrar — registrado em
  `ShadowBluetoothLeScanner` antes de chamar `startScanAndConnect()`,
  que entrega o resultado assim que `startScan` é chamado.
- Capturado o `BluetoothGattCallback` real que o `Service` registra
  (`Shadows.shadowOf(gatt).gattCallback`, depois que
  `startScanAndConnect()` já disparou a conexão de verdade) — chamado
  `onCharacteristicChanged` diretamente nele, com a característica TX
  carregando o identificador bruto.
- `gradlew :app:testDebugUnitTest --tests
  "org.nexo.motor.app.connectivity.BleAccessoryServiceTest"` rodado de
  verdade: `BUILD SUCCESSFUL`, teste passando.
- Achado à parte, mesma categoria do de `MainActivity`: os dois métodos
  públicos (`setPieceReadListener`, `setConnectionStateListener`)
  nunca tinham sido registrados em `architecture.md` com a mesma
  precisão usada pro resto da API pública — só prosa solta ("avisa o
  PieceReadListener"). Corrigido em `architecture.md`, confirmado por
  este teste.

### <a id="2026-08-17-investigacao-de-teste-de-reportfilewriter-e-reportshareintent"></a>2026-08-17 — Investigação de teste de `ReportFileWriter.kt`/`ReportShareIntent.kt`

**Levou a:** [findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric](<findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric>)

*Resumo simples:* dos dois caminhos técnicos que `decisions/0019`
já decidiu pra escrever o relatório (um pra Android 10 em diante,
outro pra Android 7 a 9), só o mais novo dá pra testar com a
ferramenta já escolhida em `decisions/0025` — o mais antigo depende
de um retorno do sistema Android que a ferramenta nunca dispara.
Confirmado por três fontes independentes, não por suposição.

*Detalhe técnico:*
- Guia oficial do Android sobre escrever no `MediaStore` (já citado em
  `decisions/0019`, lido por completo agora): confirma que inserir e
  escrever um item novo é sempre direto — devolve o endereço na hora,
  sem retorno assíncrono no meio; em tradução livre, "dá pra testar com
  chamadas diretas, sem espera".
- Código-fonte oficial do `ShadowContentResolver` (Robolectric):
  `insert()` sem nenhum provedor de conteúdo registrado não falha —
  devolve um endereço próprio, previsível; `openOutputStream()`
  entrega o fluxo já registrado por `registerOutputStream()` pra esse
  mesmo endereço. O caminho novo (Android 10+) é testável assim, sem
  precisar simular nenhum provedor completo.
- Código-fonte oficial do `ShadowMediaScannerConnection` (Robolectric),
  lido por completo: `scanFile()` só grava os caminhos recebidos, nunca
  chama o retorno (`OnScanCompletedListener`) que entrega o endereço
  final. O caminho antigo (Android 7 a 9) depende desse retorno pra
  saber o endereço do arquivo — sem ele, não dá pra confirmar esse
  passo.
- Considerada a possibilidade de outra ferramenta resolver isso: o
  próprio Robolectric tem um "modo de gráficos nativo", com mais de 60
  arquivos de teste próprios cobrindo `Canvas`, `Bitmap`, `Paint`,
  tipografia — nenhum cobrindo `PdfDocument` (achado relacionado,
  reforçando que o ponto já decidido em `decisions/0025` sobre o
  desenho do PDF precisar de aparelho continua correto, com evidência
  mais forte que antes).
- Busca nos problemas já relatados por outras pessoas no repositório
  oficial do Robolectric (`gh api search/issues`), pelos termos
  `PdfDocument` e `MediaScannerConnection`: nenhum resultado nos dois —
  confirmado que a busca em si funciona, testando antes com um termo
  garantido (`Bitmap`, 154 resultados).
- Nenhuma alternativa de ferramenta encontrada que resolva sem
  aparelho — `Paparazzi` (outra ferramenta de teste sem aparelho,
  focada em tela) não tem confirmação de cobrir `PdfDocument`, e usar
  um substituto simulado (mock) só provaria o próprio substituto, não
  o desenho real do PDF.
- Escrito o teste do caminho novo: `writeReportCsv` (prova DA-ARM-01)
  e `buildReportShareIntent` (prova DA-ARM-02). `writeReportPdf` fica
  de fora deste teste — recebe um `PdfDocument` já pronto como
  parâmetro, e só existe um `PdfDocument` de verdade através do teste
  instrumentado de `ReportPdfRenderer.kt`, ainda não escrito; o
  mecanismo de escrita em si (mesmo usado pelas duas funções) já fica
  provado através de `writeReportCsv`.
- `ShadowContentResolver.insert()` sem provedor registrado devolve um
  endereço próprio, previsível (endereço base + contador, começando em
  1 num teste novo) — usado pra registrar, com antecedência, o fluxo
  de saída que o teste depois confere.
- `gradlew :app:testDebugUnitTest --tests
  "org.nexo.motor.app.report.ReportFileWriterTest"` rodado de verdade:
  `BUILD SUCCESSFUL`, os dois testes passando. Suíte completa do
  módulo `app` rodada de novo depois, sem quebra.

### <a id="2026-08-17-lacuna-na-forma-de-sessionstate-e-dos-tipos-de-content"></a>2026-08-17 — Lacuna na forma exata de `SessionState`, dos tipos de `content` e do construtor de `SessionViewModel`

**Levou a:** [tasks.md, Em aberto](<tasks.md#em-aberto>) (pendência
nova, ainda sem decisão)

*Resumo simples:* pra montar, dentro de um teste automático, um
exemplo de "sessão de jogo em andamento" e de "conteúdo já carregado",
seria preciso saber exatamente como essas duas coisas são guardadas
por dentro do programa. Isso nunca foi escrito em nenhum documento de
decisão do projeto, só decidido direto no código, faz tempo. Essa
descrição que falta precisa virar uma tarefa própria, decidida com
calma, antes do teste da peça que liga a leitura de uma peça física à
tela do jogo poder ser escrito.

*Detalhe técnico:*
- Primeira tentativa: abrir `SessionViewModel.kt` inteiro pra resolver
  a dúvida. Errada por método — a regra do módulo não é "quanto código
  foi lido", é "de onde a decisão nasceu"; mesmo um fato pequeno, se
  vem do código em vez do documento, já inverte a ordem que
  `modulos/README.md` (Como navegar) fixa. Revertida — nenhum campo,
  assinatura ou trecho de lógica visto ali entrou em qualquer decisão
  posterior.
- Segunda tentativa: escrever a forma de `SessionState`/`content`/
  `SessionViewModel` direto em `architecture.md`, sem ADR, citando as
  ADRs já existentes (decisions/0008, 0013, 0020, 0022, 0023, 0024)
  como se fosse desenho derivado delas. Também errada, por um motivo
  mais sutil: parte dessas ADRs decide o *conceito* (retrato imutável,
  campos diretos, registro interno), mas nenhuma delas fixa nome de
  campo — a "derivação" era, na prática, um palpite com citação de ADR
  em volta. Prova concreta: ao compilar contra esse palpite, o mesmo
  erro (`ordering` faltando em `ContentEvent`/`ContentTheme`) apareceu
  de novo, idêntico ao da primeira tentativa — mostrando que o segundo
  palpite não era mais fundamentado que o primeiro, só mais bem
  escrito. Revertida por completo (`git reset --hard` até o commit
  anterior, arquivo de teste novo apagado).
- Reconhecido, só depois dessas duas tentativas, que o problema não é
  de método de pesquisa — é que não existe alternativa real a
  comparar pra "qual o nome de um campo de uma classe Kotlin já
  escrita": não é o tipo de decisão que tem fonte externa ou
  precedente de mercado (diferente de decisions/0008, que comparou
  duas fontes reais — Android "state holder" e Microsoft "event
  sourcing" — antes de decidir). A decisão sobre esses nomes já foi
  tomada por quem escreveu o código, e nunca passou por ADR nenhuma —
  ou seja, pelas próprias regras deste projeto, nunca foi decidida de
  verdade, mesmo o código já existindo. "Código que já existe, mas
  nunca passou pela documentação" não vira motivo pra pular a
  documentação — vira pendência pra fazer a documentação nascer agora,
  com o código corrigido depois se divergir do que for decidido, nunca
  o contrário (mesma regra já fixada em `modulos/README.md`).
- Caminho identificado, sem ainda decidir nada, pra quando essa tarefa
  acontecer: pelo menos a parte de `content` tem uma saída sem
  depender de conhecer o construtor de `ContentInstance` — a função já
  documentada `importContentPackage(manifestJson: String)` aceita
  texto no formato já 100% fixado em
  [concept.md, Contrato de dado](<concept.md#contrato-de-dado>) e
  devolve a instância pronta; não resolve `SessionState`, que não tem
  nenhuma função equivalente documentada pra montar o primeiro retrato
  de uma sessão.
- Nenhum arquivo de código ou de `architecture.md` ficou modificado
  depois desta investigação — os dois `git reset --hard` devolveram a
  worktree exatamente ao estado do commit anterior (terceiro teste,
  `ReportFileWriter`/`ReportShareIntent`) antes de qualquer commit
  novo.

### <a id="2026-08-18-formalizacao-da-forma-de-sessionstate-content-e-construtor-do-viewmodel"></a>2026-08-18 — Formalização da forma de `SessionState`, `content` e do construtor de `SessionViewModel`

**Levou a:** [decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md](<../decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md>)

*Resumo simples:* releitura completa da cascata e das ADRs já citadas pela investigação de 17/08
(decisions/0007, 0008, 0009, 0013, 0020, 0022), sem abrir nenhum arquivo `.kt` em nenhum momento, mais
uma busca externa nova, pra derivar a forma exata que faltava. Achado principal: a pergunta "que nome
dar a um campo" não tem fonte externa (confirmado de novo, mesma conclusão de 17/08), mas a pergunta
"os dados relacionados de uma tela/sessão devem ficar num objeto só, ou espalhados" é uma pergunta
diferente, maior, e essa sim tem fonte oficial — nunca consultada nas duas tentativas anteriores.

*Detalhe técnico:*
- Leitura completa, nesta ordem, numa worktree própria (`adr-sessionstate-content-viewmodel`, criada
  a partir de `develop`, corrigida depois de nascer da branch padrão errada — mesmo comportamento já
  registrado em memória de sessão sobre `EnterWorktree`): `tasks.md` (pendência exata),
  `decisions/0008`, `0013`, `0020`, `0022` (na íntegra, não só o resumo), `architecture.md` (na
  íntegra, incluindo o trecho que tinha ficado de fora numa leitura anterior cortada pela ferramenta),
  `findings.md`, `decisions/README.md`.
- Pesquisa externa, em duas buscas e uma leitura de página oficial: confirmado que não existe fonte
  de mercado ou documentação oficial pra decidir o nome de um campo específico (mesma conclusão da
  investigação de 17/08); encontrada, sim, fonte oficial pra uma pergunta diferente — se dados
  relacionados de uma tela/sessão devem ficar bundlados num objeto só ou espalhados — no guia oficial
  de arquitetura do Android (`developer.android.com/topic/architecture/ui-layer`), que reforça, de
  fora, a mesma direção que `decisions/0008` já tinha tomado.
- Ponto de desenho real encontrado e resolvido sem abrir código: onde a configuração de sessão
  (limiar de dica, disponibilidade de pular) deveria morar — campo do próprio `SessionState`, ou
  parâmetro explícito passado pelo `ViewModel` a cada função de transição. A segunda opção foi
  escolhida por evitar que `session` passe a depender de `report` (que já depende de `session`, pelo
  registro `SessionEvent`) — uma dependência nos dois sentidos entre os mesmos dois pacotes, o que
  `architecture.md` já registra implicitamente ao mostrar que o `ViewModel` já lê `SessionConfiguration`
  (`core/report`) direto pro gatilho de ociosidade.
- Um ponto ficou fora de propósito, sem resposta forçada: como combinar o recorte de temas com o
  recorte de eventos de cada tema, pra uma sessão que atravessa mais de um tema — `sessionScope`
  (decisions/0009) resolve só um grupo por vez. Virou pendência nova em `tasks.md`, não resposta
  chutada dentro desta ADR.

### <a id="2026-08-18-escrita-do-teste-de-sessionviewmodel-e-correcao-da-forma-de-sessionstate"></a>2026-08-18 — Escrita do teste de `SessionViewModel.kt` e correção da forma de `SessionState`

**Levou a:** [decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md](<../decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md>)

*Resumo simples:* o teste foi escrito só a partir do que `architecture.md`/`decisions/0026` já
documentavam — nenhum arquivo de `src/main` foi aberto antes de rodar o teste pela primeira vez.
A primeira rodada não compilou: o código real usa `SessionState.expectedEventName: String`, não
`sessionEvents`/`currentEventIndex` como `decisions/0026` tinha decidido, e o construtor de
`SessionViewModel` tem três nomes de parâmetro diferentes. Comparando os dois desenhos de
`SessionState`, o do código já existente não duplica a lista de eventos da sessão (que já mora em
`SessionConfiguration.eventNames`) — por isso a correção seguiu pro código, e não o contrário: uma
ADR nova (`decisions/0027`) formaliza a forma já existente, com fonte externa própria.

*Detalhe técnico:*
- Teste escrito com dez casos, cobrindo a tela inicial (referência, EI-SES-04), a passagem pra
  aguardar tentativa, aceitar/rejeitar uma peça (EI-VAL-01/02), mudança de estado de conexão sem
  interromper a tela de jogo, pedido/cancelamento/confirmação de saída (EI-PAU-03/04,
  `decisions/0023` — relatório escrito antes do estado ser apagado) e o gatilho de ociosidade, com
  e sem reinício por uma tentativa recente (EI-PAU-01/06, `decisions/0024`).
- Primeira rodada (`gradlew :app:testDebugUnitTest --tests SessionViewModelTest`): falha de
  compilação, não de asserção — `No parameter with name 'sessionEvents' found`,
  `No value passed for parameter 'expectedEventName'`, `No parameter with name 'content' found`,
  `No value passed for parameter 'instance'`, `No parameter with name 'stateFile' found`.
- Comparação dos dois desenhos de `SessionState`, sem adotar o do código só porque já existe:
  `expectedEventName: String` (código) não duplica dado nenhum — `continueToNextEvent` já recebe
  o próximo nome como parâmetro explícito, buscado em `SessionConfiguration.eventNames`, que já
  existe. `sessionEvents: List<String>` + `currentEventIndex: Int` (`decisions/0026`) duplicava
  essa mesma lista dentro de `SessionState`. Fonte externa nova, específica pra este ponto (não a
  mesma citação reaproveitada de `decisions/0026`): o guia oficial de arquitetura do Android define
  o princípio de "fonte única de verdade" (*Single Source of Truth*) — em tradução livre, "quando
  um tipo de dado novo é definido no seu aplicativo, atribua uma única fonte de verdade a ele...
  centraliza toda mudança de um tipo de dado num lugar só" (GOOGLE, [s.d.]). `decisions/0026` já
  tinha citado a mesma família de fonte (guia de arquitetura do Android), mas por outro princípio
  dela (juntar campos relacionados num objeto só) — usado, sem perceber, pra justificar o oposto do
  que este princípio mais específico recomenda. Registrado como correção de raciocínio, não como
  leitura nova por acaso.
- As outras três diferenças (`instance`/`content`, `pausedStateFile` opcional/`stateFile`
  obrigatório, parâmetro `now: () -> Long` a mais) não competiam com nenhuma alternativa de desenho
  considerada em `decisions/0026` — corrigidas como nota de acompanhamento factual na própria ADR,
  sem ADR nova.
- Teste corrigido pra usar os nomes reais (`expectedEventName`, `instance`, `pausedStateFile`);
  segunda rodada: nove de dez testes passaram, um falhou por engano de fixture do próprio teste,
  não do código — `File.createTempFile` cria um arquivo vazio, mas `loadSessionState` só devolve
  `null` quando o arquivo *não existe*; um arquivo vazio-mas-existente não representa nenhuma
  sessão real (o caminho de `app`, `context.filesDir`, nunca existe antes da primeira gravação).
  Corrigido apagando o arquivo temporário logo depois de reservar o nome.
- Terceira rodada: `gradlew :app:testDebugUnitTest --tests
  "org.nexo.motor.app.ui.SessionViewModelTest"`, `BUILD SUCCESSFUL`, dez testes. Suíte completa
  (`gradlew :app:testDebugUnitTest :core:test`) rodada de novo, sem quebra.

### <a id="2026-08-18-combinacao-do-recorte-de-temas-e-eventos-numa-sessao-multitema"></a>2026-08-18 — Combinação do recorte de temas e de eventos numa sessão multi-tema

**Levou a:** [decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md](<../decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md>)

*Resumo simples:* releitura completa do Documento de Conceito (seção 10) e da Especificação
(EI-SES-06 a EI-SES-08), sem abrir nenhum arquivo de código, pra decidir como uma sessão que
atravessa mais de um tema vira uma lista única e plana de nomes de evento. A conversa passou por
duas correções antes de fechar no desenho certo — as duas registradas aqui, não escondidas,
porque o raciocínio errado também é parte de como se chegou à resposta certa.

*Detalhe técnico:*
- Primeira leitura, isolada: "atravessar um tema inteiro" (Conceito §10, repetida em EI-SES-07)
  interpretada como uma categoria própria — "tema do meio é sempre inteiro, tema do primeiro e do
  último podem ser parciais". Essa framing (com uma distinção explícita "primeiro/meio/último")
  gerou confusão ao ser explicada — não porque estivesse tecnicamente errada no resultado, mas
  porque tratava como regra separada algo que é consequência de uma regra só.
- Segunda leitura, corrigindo a primeira longe demais: ao simplificar pra "um único ponto de
  início e um único ponto de fim, andando em linha reta", cheguei a dizer que não haveria mais
  nenhuma distinção de tema "sempre inteiro" — o que também não estava certo, porque descartava a
  proibição de "1 e 3 sem o 2" (EI-SES-06) aplicada um nível acima.
- Terceira leitura, a que fechou: as duas primeiras eram, na prática, a mesma coisa, só explicadas
  de dois jeitos que pareciam contraditórios — as duas descreviam o mesmo mecanismo por ângulos
  diferentes, sem que nenhuma das duas estivesse de fato errada no resultado. Conclusão final, com
  o raciocínio completo, registrada só em
  [decisions/0028, Contexto](<../decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md>) —
  não repetida aqui.
- Checado, antes de fechar a decisão, se o caso de tema/evento avulso (declarado individualmente
  por quem monta o conteúdo, na criação — Conceito §2) já estava coberto: estava, por
  [decisions/0009](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>), decisão 2 — um
  item avulso nunca participa de recorte combinado, sozinho ou não. Não precisou de decisão nova.
- Desenho final: uma função nova, genérica sobre tema/evento (mesmo padrão de `sessionScope`),
  que reaproveita `sessionScope` duas vezes — uma pra lista de temas, outra pra lista de eventos de
  cada tema — sem nunca fazer `core/session` depender de `core/content` (RNF-MOD-01). Nenhum
  arquivo de código aberto em nenhum momento desta investigação; código e teste seguem como
  pendência própria em `tasks.md`.
- Pesquisa externa tentada pra uma pergunta diferente da regra em si (essa já vinha do texto
  aprovado, sem precisar de fonte de fora): a escolha de implementação de reaproveitar
  `sessionScope` duas vezes, em vez de escrever um algoritmo novo que percorra os dois níveis numa
  passada só. Três buscas — duas sobre "achatar" (flatten) intervalos aninhados, sem trazer fonte
  oficial diretamente aplicável, e uma sobre o princípio *Don't Repeat Yourself* (DRY) — não
  chegaram a uma fonte que passasse no mesmo padrão já usado nas outras citações deste módulo
  (site oficial ou artigo academicamente validável): o resultado da terceira busca misturava
  Wikipedia, um artigo do LinkedIn e um post de blog não-oficial na mesma resposta, sem
  confirmação direta na fonte primária (o livro em si). Descartada — o argumento fica só na lógica
  interna já registrada em `decisions/0028` (evitar duplicar a mesma regra em dois lugares, com
  risco real de as cópias divergirem depois), sem precisar de nome de princípio nem citação
  externa pra se sustentar — mesmo caso de `decisions/0009`, que também não usou fonte externa.

### <a id="2026-08-18-arquitetura-de-informacao-das-telas-do-motor"></a>2026-08-18 — Arquitetura de informação das telas do motor

**Levou a:** [findings.md#2026-08-18-sessionviewmodel-sem-acao-de-pausar-manual](<findings.md#2026-08-18-sessionviewmodel-sem-acao-de-pausar-manual>)

*Resumo simples:* pra montar o passo 1 do método de desenho visual já registrado em
`architecture.md` (arquitetura de informação — o que precisa existir em cada uma das 17 telas), a
primeira lista foi montada a partir da tabela de telas do Projeto Arquitetônico (seção 6.6) e das
variantes de `SessionScreen` já fechadas em `decisions/0022` — documentos derivados, não a fonte
raiz. Reler o Documento de Conceito inteiro, do início ao fim, revelou duas coisas que os
documentos derivados não cobriam.

*Detalhe técnico:*
- Primeira montagem: 17 entradas da tabela DA-RET, mais os campos exatos de cada situação de jogo
  já decididos em `decisions/0022` — sem abrir nenhum arquivo de código.
- Releitura completa do Documento de Conceito (não só as seções já citadas pelos documentos
  derivados) encontrou duas lacunas:
  1. Seção 12 exige "uma ação explícita de pausar", distinta do gatilho automático de ociosidade —
     sem entrada correspondente na tabela DA-RET nem nas ações do `ViewModel` já documentadas em
     `architecture.md`.
  2. Seção 10 ("composição de uma sessão") — a escolha de até onde uma sessão vai, que a primeira
     versão da lista só citava como justificativa de fundo, sem registrar como um elemento que a
     pessoa realmente vê e escolhe na tela de navegação.
- Ponto 2 não precisou de pendência nova: o mecanismo que calcula até onde a sessão pode ir já
  estava decidido — `decisions/0009` (recorte dentro de um tema) e `decisions/0028` (recorte
  atravessando temas) — e já virou código testado (`sessionScope`/`sessionEventNames`,
  `core/session/SessionScope.kt`). Bastou corrigir a entrada da tela de navegação pra citar esse
  mecanismo em vez da regra em linguagem solta.
- Ponto 1 foi conferido contra o código real antes de virar achado — não bastava a suspeita: abri
  `app/ui/SessionViewModel.kt` (único arquivo lido nesta investigação) e confirmei que ele não
  expõe nenhum método de pausa manual, só o relógio automático. Essa checagem por leitura de código
  é exigida pela própria regra deste documento: um achado em `findings.md` só existe confirmado por
  leitura de código ou teste ao vivo, nunca por dedução a partir de outro documento.

### <a id="2026-08-22-padrao-de-navegacao-hierarquica-e-revisao-das-pendencias-de-tela"></a>2026-08-22 — Padrão de navegação hierárquica e revisão das pendências de tela

**Levou a:** [decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>)

*Resumo simples:* decisão de como a navegação entre instância, tema e evento se comporta
(expansão em acordeão, não troca de tela inteira) — resolvida só com a lógica interna da
Especificação, sem fonte externa. No processo de revisar as pendências de desenho visual
relacionadas, três delas ("Ponto de início"/"Configuração da sessão" serem a mesma tela; quantas
telas físicas o Grupo B vira; se o botão de pausar precisa de confirmação) se revelaram já
resolvidas por documentos existentes, nunca conectadas antes — nenhuma delas exigiu decisão nova.

*Detalhe técnico:*
- Duas alternativas reais comparadas pro padrão de navegação: troca de tela inteira a cada nível,
  ou acordeão (expandir em vez de trocar). Decisão pelo acordeão apoiada em duas exigências já
  fixadas na Especificação — `EI-NAV-03` (a escolha de alcance da sessão precisa ficar presa
  visualmente ao item que a originou) e `DA-NAV-02`/`DA-NAV-03` (busca única, que o pacote
  `search` já implementa de forma genérica sobre qualquer lista) — sem precisar de fonte externa.
- Tentativa de embasar com fonte externa (Nielsen Norman Group, citando dois estudos acadêmicos)
  pra um ponto secundário (padrão de navegação hierárquica em geral) — descartada depois de
  checar o peso de cada fonte: um dos dois estudos citados pelo NN/g era, na verdade, um
  relatório técnico da Universidade de Maryland (1999), nunca publicado em revista revisada por
  pares, apesar de citado como se fosse; o outro (Puerta Melguizo et al., 2012, Behaviour &
  Information Technology) tinha dado bibliográfico sólido, mas o texto literal do resumo nunca foi
  confirmado (cinco tentativas de acesso, todas bloqueadas). Decisão final não usa nenhuma citação
  externa — só a lógica interna já registrada acima.
- Um achado revelou uma pendência maior, fora do escopo desta ADR: o acordeão precisa renderizar
  de forma preguiçosa (sem desenhar tudo de uma vez conforme mais níveis abrem), mas o nome exato
  da peça que faz isso depende de qual ferramenta de tela o módulo `app` usa (Jetpack Compose ou
  Views tradicionais) — decisão que nenhum documento do projeto tinha tomado ainda
  (`decisions/0003` registra as duas opções, sem escolher). Virou pendência nova em `tasks.md`,
  fora desta ADR e desta worktree (mesmo porte de `decisions/0001`, linguagem do aplicativo).
- Releitura direta da Especificação (`EI-NAV-05`) revelou que "Ponto de início" e "Configuração
  da sessão" já são a mesma tela — "todos são decididos nessa mesma tela, no mesmo momento" — sem
  nenhuma ambiguidade. Essa pergunta já vinha registrada como pendência aberta desde antes desta
  sessão (rascunho de trabalho, nunca commitado), sem que ninguém tivesse conectado a resposta já
  existente.
- Releitura de `decisions/0022` (já aceita, de 15-08-2026) confirmou que "quantas telas físicas o
  Grupo B vira" também já tinha resposta: `SessionScreen` é um tipo fechado, uma variante por
  entrada da tabela DA-RET — uma tela só, mudando de conteúdo por dentro. Essa mesma frase
  desatualizada tinha sobrevivido em `tasks.md` desde 15-08-2026 (parte do commit que decidiu
  `decisions/0029`), nunca corrigida até esta revisão — achado registrado com commit próprio,
  separado do resto desta tarefa, por se tratar de conteúdo real anterior, não rascunho do dia.
- Releitura do Documento de Conceito (seção 12) e de `EI-PAU-03` confirmou que o botão de pausar
  também já tinha resposta: só "sair" exige confirmação explícita, porque apaga algo irreversível;
  pausar não apaga nada, então age direto, sem confirmação. Único ponto genuíno que sobrou sem
  resposta em documento nenhum, entre as pendências de tela revisadas: o indicador de conexão
  Bluetooth/NFC (`DA-RET-06`).
- Padrão repetido três vezes na mesma tarefa (muitas entradas do acordeão, mesma tela de
  início/configuração, telas físicas do Grupo B) — em nenhum dos três havia de fato uma escolha
  entre alternativas reais a fazer; a documentação já respondia, só não tinha sido lida com
  atenção antes de listar como pendência.
- O achado sobre o Grupo B (`decisions/0022`, 15-08-2026, já fecha o agrupamento de 10 das 17
  entradas de tela) não estava isolado em `tasks.md` — a mesma frase, tratando o agrupamento das
  17 entradas como inteiramente em aberto, sobrevivia em mais quatro lugares. O critério pra
  decidir entre corrigir direto ou acrescentar nota de acompanhamento numa ADR não é "já foi
  mesclada"/"já foi revisada" — é se a informação que falta já existia no momento em que aquele
  texto foi escrito. `decisions/0003` (13-08-2026) e a seção "Layout" de `architecture.md` (mesma
  data) são de antes de `decisions/0022` existir — a afirmação era razoável quando escrita, só
  ficou desatualizada depois; nota de acompanhamento datada é o tratamento certo, sem tocar no
  texto original. Já `decisions/0029` (18-08-2026) e `decisions/0031` (22-08-2026) — e o trecho
  correspondente de `concept.md` — foram escritos depois de `decisions/0022` já existir: a
  informação certa já estava disponível, só não foi checada antes de escrever. Isso é erro de
  nascença, não fato novo surgido depois — corrigidos direto, sem nota de acompanhamento nem
  palavra "corrigido" no changelog, mesmo tratamento já dado a "Ponto de início"/botão de pausar
  mais cedo nesta sessão. `decisions/0031` tinha ganhado nota de acompanhamento numa primeira
  tentativa, corrigida depois que a data de `decisions/0022` foi checada de verdade contra a data
  da própria ADR. Mesmo padrão já visto três vezes antes nesta mesma tarefa (busca aproximada,
  "Ponto de início", Grupo B): tratar como pendência ou como decisão nova algo que já tinha
  resposta, por não conferir contra o que já existia. Confirmado por leitura direta de cada
  arquivo (`grep` pela frase exata) e checagem de data por commit (`git log`), não por suposição
  de que toda ocorrência pedia o mesmo tratamento.

### <a id="2026-08-30-pesquisa-do-sistema-visual-material-design-e-cor-semente"></a>2026-08-30 — Pesquisa do sistema visual (Material Design), acessibilidade e cor semente

**Levou a:** ainda sem conclusão

*Resumo simples:* pesquisa em fonte oficial direta (nunca resumo de
terceiro) pros pontos do passo 3 do método já registrado em
`architecture.md` (aplicar o sistema visual sobre o esqueleto de tela
já pronto — `wireframe.md`): versão do sistema, cor, tipografia,
forma, tema claro/escuro, contraste e área de toque. Confirmado que o
guarda-chuva de processo pra essa etapa já existe — a norma ISO
9241-210, já citada em `architecture.md` — sem precisar de metodologia
nova. Cinco dos sete pontos já foram decididos direto em conversa (cor
dinâmica; tema claro e escuro, com escolha manual pendente de um menu
de configurações ainda não desenhado; forma no padrão do Material;
contraste no nível comum, 4,5 para 1; fonte Roboto, a padrão do
sistema; área de toque de 48dp por 48dp, que é regra técnica do
Android, não escolha). Só a cor semente (a cor fixa de reserva, usada
quando o aparelho não suporta cor dinâmica) segue em aberto, entre uma
combinação vibrante de laranja e azul e uma combinação mais fechada —
sem decisão ainda.

*Detalhe técnico:*
- `m3.material.io` (site oficial do Material Design 3) carrega o
  conteúdo por JavaScript e não deixa a ferramenta de leitura
  automatizada extrair o texto — mesma limitação já registrada em
  `architecture.md, Referências` pra essa mesma fonte. Conteúdo
  técnico confirmado, com trecho literal, pela documentação irmã do
  Android Developers (mesmo dono, texto estático):
  `developer.android.com/develop/ui/compose/designsystems/material3` —
  cor dinâmica (`dynamicLightColorScheme`/`dynamicDarkColorScheme`, só
  Android 12+) com fallback pra esquema fixo (`LightColorScheme`/
  `DarkColorScheme`) baseado numa única cor semente; tipografia em
  cinco categorias (display, headline, title, body, label), cada uma
  em três tamanhos; forma em cinco tamanhos de arredondamento (4dp a
  24dp); tema escuro/claro resolvido por `isSystemInDarkTheme()`.
- Contraste checado em duas fontes oficiais, concordantes: W3C, dono
  do padrão WCAG (`w3.org/WAI/WCAG21/Understanding/contrast-minimum.html`,
  lido por completo, não só resumo de busca) — nível comum (AA) exige
  4,5 para 1 em texto normal, 3 para 1 em texto grande; e Android
  Developers (`developer.android.com/guide/topics/ui/accessibility/apps`)
  — mesma faixa, mais a área de toque mínima de 48dp por 48dp.
- Gestalt (organização visual) confirmado por fonte acadêmica de
  acesso aberto, não blog: capítulo de livro-texto universitário
  (OpenStax Psychology 2e, Rice University,
  `openstax.org/books/psychology-2e/pages/5-6-gestalt-principles-of-perception`,
  lido por completo) — cinco princípios (figura-fundo, proximidade,
  similaridade, continuidade, fechamento), fundadores Max Wertheimer,
  Wolfgang Köhler e Kurt Koffka, início do século XX.
- Psicologia da cor: artigo trazido pelo usuário (Elliot e Maier,
  2014, *Color Psychology: Effects of Perceiving Color on
  Psychological Functioning in Humans*, *Annual Review of Psychology*,
  v. 65, revisado por pares) — três tentativas anteriores de acesso
  direto (Annual Reviews, SSRN, ResearchGate, Semantic Scholar)
  bloqueadas por erro 403. Lido por completo, do resumo à conclusão,
  sem inferir além do texto. O próprio artigo desaconselha usar seus
  achados pra decisão prática: o resumo já avisa que a área está "em
  estágio inicial de desenvolvimento" e falta trabalho antes de
  "recomendações de aplicação" serem justificadas; a seção de
  Direções Futuras reforça isso, em tradução livre, "os efeitos da
  cor são chamativos e atraem atenção da mídia, o que pode empurrar a
  pressa de uma descoberta de laboratório pra uma conclusão sobre
  aplicação no mundo real — achamos melhor resistir a esse impulso".
  A maioria dos achados também é de contexto distante de interface de
  aplicativo (vermelho em competição esportiva, vermelho em atração,
  cor de alimento); o ponto mais próximo (azul associado a "mais
  relaxante, mais confiável" em site e logotipo) vem de comportamento
  de consumidor, não de interface. Decisão tomada: não usar este
  artigo como base pra escolher uma cor — ele mesmo pede pra não ser
  usado assim ainda.
- Seis combinações de laranja e azul montadas numa página comparativa
  (Artifact, gerado nesta sessão, sem citar nenhum rascunho externo ao
  repositório): https://claude.ai/code/artifact/f66529ec-d07a-4bdb-9923-16ca6e538170 —
  do mais vibrante (laranja `#FF6D1F` + azul royal `#2D5FE0`) ao mais
  discreto (pêssego `#FFB37B` + azul claro `#7EB6E8`), cada uma com o
  código hexadecimal exato. Escolha entre a primeira (laranja vibrante
  `#FF6D1F` + azul royal `#2D5FE0`) e a sexta (mostarda `#D98E04` +
  índigo `#38419D`) segue em aberto; contraste calculado à mão pela
  fórmula oficial do WCAG (luminância relativa, mesma fórmula da fonte
  já citada acima) mostra que o índigo da sexta opção tem contraste
  bem mais alto contra fundo claro (8,71 para 1, contra 5,49 para 1 do
  azul royal) mas quase desaparece contra um fundo escuro típico do
  Material (1,96 para 1, contra 3,10 para 1 do azul royal) — como o
  projeto já decidiu suportar os dois temas, um par mais equilibrado
  nos dois modos tem uma vantagem objetiva mensurável, mas o Material
  já gera automaticamente um tom acessível de qualquer cor semente
  (mesma fonte acima: "on-primary... para fornecer contraste acessível
  ao usuário"), então essa vantagem é real mas não decisiva sozinha —
  o restante da escolha (vibrante ou discreto) é preferência, não
  critério técnico.

### <a id="2026-08-30-fechamento-da-decisao-de-cor-semente"></a>2026-08-30 — Fechamento da decisão de cor semente

**Levou a:** [decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>)

*Resumo simples:* fecha a investigação anterior, no mesmo dia. Duas
coisas novas: a página comparativa das seis combinações de cor passou
a morar dentro do repositório (antes era só um link externo), e uma
fonte oficial nova confirmou que o Google recomenda partir de uma cor
de marca já existente ou preferida, em vez de uma fórmula. Escolha
final: a primeira das seis combinações, laranja `#FF6D1F` e azul royal
`#2D5FE0`.

*Detalhe técnico:*
- Página movida pra
  [cor-semente-candidatas.html](<../design/cor-semente-candidatas.html>),
  dentro de `design/` (pasta nova do módulo, ver nota de acompanhamento
  abaixo), com nota de origem (material gerado por IA, preferência de
  laranja e azul indicada por N. Denominado — autoria do projeto,
  `README.md` da raiz).
- Duas fontes novas do Google (Google Design; Codelab de
  personalização de cor) confirmam que a orientação oficial é manter
  uma cor de marca já existente/preferida — resolve a pergunta se
  existe fonte oficial pra esse tipo de escolha.
- Material Theme Builder (ferramenta correlata) considerado, mas não
  operado nesta investigação — código-fonte arquivado desde 23 jul.
  2026.
- Buscas sobre cor em interação humano-computador e em tecnologia
  educacional, interrompidas antes de trazer resultado, seguem em
  aberto.

### <a id="2026-08-30-nota-pagina-comparativa-e-escolha-desatualizadas"></a>2026-08-30 — Nota de acompanhamento: entrada anterior desatualizada

*Resumo simples:* a entrada
[2026-08-30-pesquisa-do-sistema-visual-material-design-e-cor-semente](<#2026-08-30-pesquisa-do-sistema-visual-material-design-e-cor-semente>),
escrita mais cedo no mesmo dia, cita a página comparativa por um link
externo e descreve a escolha entre a primeira e a sexta combinação
como "ainda em aberto". As duas informações mudaram — página movida
pra dentro do repositório, escolha fechada — registradas na entrada
anterior, sem alterar o texto já escrito daquela.

### <a id="2026-08-30-reorganizacao-de-pasta-do-modulo"></a>2026-08-30 — Reorganização de pasta do módulo: `design/` separado de `docs/`

**Levou a:** ainda sem conclusão (reorganização de arquivo, não decisão de conteúdo)

*Resumo simples:* `docs/` deste módulo, como de qualquer módulo, tem
um conjunto fixo de sete documentos definido pelo molde
(`_template/`). `wireframe.md` e `cor-semente-candidatas.html` tinham
sido criados direto ali, sem checar se cabiam — não cabiam. Os dois
foram movidos pra `design/`, pasta nova, irmã de `docs/`, reservada
pra material visual (esqueleto de tela, aplicação do sistema visual,
protótipo).

*Detalhe técnico:*
- Confirmado por listagem direta de `docs/` que só esses dois arquivos
  não pertenciam ao conjunto fixo do molde.
- Links corrigidos em todo lugar que apontava pro caminho antigo:
  `architecture.md`, `tasks.md`, `handoff.md` (módulo) e `HANDOFF.md`
  (raiz) — os quatro já commitados antes desta correção.
- Pendência em aberto: se essa mesma pasta (`design/`) deveria virar
  parte formal do molde (`_template/`), valendo pra qualquer módulo
  futuro, não só o motor — decisão ainda não tomada.

### <a id="2026-08-30-auditoria-de-desenho-visual-antes-do-prototipo"></a>2026-08-30 — Auditoria de leitura completa antes da tarefa do protótipo navegável

**Levou a:** correções diretas em `concept.md` e `architecture.md`, e notas de acompanhamento em
[decisions/0029](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>),
[decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>) e
[decisions/0031](<../decisions/0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md>)

*Resumo simples:* antes de montar o protótipo navegável (passo 4 do método de desenho visual),
leitura obrigatória completa de todo documento do módulo ligado à aparência das telas revelou frase
desatualizada em cinco lugares — cada uma escrita antes de uma decisão posterior
([decisions/0035](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>),
`design/wireframe.md`) resolver o que elas ainda descreviam como pendente. Nenhuma correção
envolveu escolher entre alternativas novas — só reconectar texto antigo a uma decisão já tomada,
mesmo padrão já registrado na investigação de
[22-08-2026](<#2026-08-22-padrao-de-navegacao-hierarquica-e-revisao-das-pendencias-de-tela>).

*Detalhe técnico:*
- `concept.md`: duas frases corrigidas (seção Escopo e tabela da seção Fluxo) — apontam agora pra
  `decisions/0035` e `design/wireframe.md`, em vez de tratar a aparência como sem decisão.
- `architecture.md`, seção Interface: três trechos corrigidos — a mesma frase de aparência sem
  decisão; o lembrete de que a posição do indicador de conexão Bluetooth ainda precisava ser
  desenhada (já fechada em `design/wireframe.md`); e "nenhuma das quatro etapas foi concluída" (só a
  quarta, protótipo navegável, seguia pendente).
- `decisions/0029`, `0030` e `0031` já estavam aceitos — a correção entrou como nota de
  acompanhamento datada, nunca reescrevendo a Decisão nem o texto original de Consequências, mesma
  regra já usada em `decisions/0019`/`0025`.
- `decisions/0034` conferida e deixada como está: a frase ali ("fica para a implementação, quando a
  pendência... chegar") descreve escrita de código, que de fato continua pendente — não é o mesmo
  tipo de desatualização das demais.
- Pasta `Design/` (rascunho de outro ambiente de trabalho, já citado em `decisions/0033`) lida por
  completo nesta sessão, sem usar nenhum conteúdo dela nesta auditoria — nenhuma correção acima se
  apoia nela.

### <a id="2026-08-31-verificacao-do-prototipo-navegavel-apos-revisao-de-pr"></a>2026-08-31 — Verificação do protótipo navegável depois da revisão de PR

**Levou a:** correções diretas em `design/prototipo-navegavel.js` (sem ADR — nenhuma das três
decisões envolveu escolher entre alternativas reais, só completar comportamento já exigido pelos
documentos)

*Resumo simples:* a revisão de PR (`/revisar-pr`) apontou três pontos sem teste de verdade no
protótipo navegável: o ramo "sessão continua pro próximo evento do mesmo tema" nunca tinha sido
implementado nem testado; o leiaute de tablet nunca foi conferido fora da tela de Configuração; e o
botão "Pular peça", em `StudySuggestionShown`, nunca foi clicado de verdade sob a camada de
toque-livre que cobre o resto da tela. Os três pontos foram implementados (o primeiro não existia
ainda) e testados com asserções específicas contra o que EI-ENC-01/02 e `decisions/0033` já exigem
— não só "rodou sem erro".

*Detalhe técnico:*
- Ramo `hasNextEvent`: `eventoAtualTemProximo()` e `continuarProximoEvento()` escritas;
  `rodapeContinuarOuVerResultado()` passa a mostrar "Continuar" (leva pro próximo evento do mesmo
  tema, direto em `AwaitingAttempt`, sem tela de Referência própria — EI-ENC-02) quando existe
  próximo evento, e "Ver resultado" quando não existe. Teste: terminar o Evento 1 (4 fotogramas) do
  Tema A (que tem Evento 2 na sequência) e confirmar o texto do botão, o `sessionSubEstado` seguinte
  (`AwaitingAttempt`, não uma tela de referência separada) e o `totalPosicoes`/`posicaoAtual` do
  evento novo; depois terminar o Evento 2 (último do tema) e confirmar que o botão muda pra "Ver
  resultado".
- Limiares de dica/sugestão de estudo e tempo de ociosidade, antes fixos no texto da tela
  (`3`, `6`, `60s`), viram campos `<input type="number">` de verdade, editáveis, ligados a
  `estado.limiarDica`/`estado.limiarSugestaoEstudo`/`estado.tempoOciosidadeSegundos` — RF-CFG-01/
  RF-DIC-04/RF-PAU-06 (documento 2) já proíbem o motor de impor um valor padrão; o protótipo tinha
  esse número gravado como texto estático, contradizendo a própria regra que ele deveria ilustrar.
  Teste: alterar `estado.limiarDica`/`estado.limiarSugestaoEstudo` pra valores diferentes do exemplo
  original e confirmar que `simularErro()` respeita o valor novo (não o `3`/`6` fixo de antes) pra
  decidir entre `AttemptRejected`, `HintShown` e `StudySuggestionShown`.
- Isolamento do leiaute de tablet (`decisions/0033`): teste navegando, com o controle "tablet"
  marcado, por Configuração, Navegação, tela de jogo (`SessionScreen`) e Resultado, conferindo a
  classe do elemento da moldura (`elMoldura.className`) em cada uma — só Configuração deveria trocar
  pra `"tablet"`, as outras três continuam `"celular"`.
- Botão "Pular peça" sob o toque-livre: teste inicial (sem rolar a página) apontou o elemento no
  centro do botão como "nenhum" — investigado antes de concluir que era bug: o retângulo do botão
  (`getBoundingClientRect`) media `top: 797` numa janela de `805px` de altura, ou seja, o botão
  simplesmente estava abaixo da área visível da janela de teste (rolagem de página comum, sem
  relação com sobreposição de camada). Confirmado chamando `scrollIntoView()` antes de medir de
  novo: com o botão dentro da área visível, `document.elementFromPoint()` no centro dele aponta pro
  próprio botão, não pro `.toque-livre` (`position:absolute;inset:0`) que cobre o resto da tela —
  a hipótese de sobreposição real não se confirmou. Complementado com um clique de verdade
  (`botao.click()`, não a função isolada) confirmando a transição pra `SkipMessageShown`.
- Ferramenta: Microsoft Edge em modo sem interface (`--headless=new --disable-gpu --no-sandbox
  --window-size=1200,900 --dump-dom`), carregando o `.css`/`.js` reais do repositório por caminho
  `file:///` absoluto; resultado lido pelo `<title>` da página (cada falha vira uma frase específica
  ali, não um booleano solto). Roteiro do lado de fora do repositório, no diretório de rascunho da
  sessão — não faz parte da entrega.
- Nenhuma das três correções envolveu escolher entre alternativas reais (não houve segunda opção
  cogitada pra "o que o botão deveria dizer" ou "que classe a moldura deveria ter") — por isso nenhum
  ADR novo, mesma regra já aplicada nas correções anteriores desta sessão.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. Entrada nova em "Investigações" (append,
sem reescrever) também conta como mudança de conteúdo real. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial. | Criação inicial |
| 0.2.0 | 14-08-2026 | Acrescentada a investigação do esqueleto mínimo do módulo `app` (instalação do SDK, pesquisa de versões, duas armadilhas de ferramenta, teste ao vivo no emulador). | Resolução da pendência "Escrever o esqueleto mínimo do módulo `app`" |
| 0.3.0 | 14-08-2026 | Acrescentada a investigação de cobertura de teste dos pacotes `search`, `hierarchy`, `session` e `content` (cinco lacunas encontradas e fechadas com teste novo, sem divergência de comportamento). | Revisão de cobertura de teste, pedido direto |
| 0.4.0 | 14-08-2026 | Acrescentada a investigação do comportamento da busca aproximada com termo vazio, sexto ponto deixado em aberto na investigação anterior. | Resolução de [decisions/0014-busca-aproximada-com-termo-vazio.md](<../decisions/0014-busca-aproximada-com-termo-vazio.md>) |
| 0.5.0 | 15-08-2026 | Acrescentada a investigação da exigência de homologação ANATEL pro acessório leitor (Bluetooth + NFC), com três fontes legais e a norma ISO/IEC/IEEE 29148:2018 como enquadramento metodológico. | Achado revelado durante a pesquisa sobre substituição do chip PN532; pendência nova em `tasks.md` |
| 0.6.0 | 15-08-2026 | Acrescentada a investigação do registro interno de `session` contra EI-REG-01, revelando três lacunas (horário ausente, sugestão de estudo exibida nunca registrada, pausa e ociosidade não distinguíveis). | Preparação pro pacote `report`, que depende desse registro estar completo |
| 0.7.0 | 15-08-2026 | Reordenada a investigação do registro de `session` pra depois da investigação ANATEL (ordem cronológica correta — a de `session` aconteceu depois, não antes, na sequência real de trabalho desta rodada). Acrescentada a investigação de incompatibilidade entre `PdfDocument` e o módulo `core` (Kotlin puro), confirmada ao vivo com `gradlew :app:assembleDebug`. | Preparação pro pacote `report`, correção de ordenação notada durante a revisão |
| 0.8.0 | 16-08-2026 | Acrescentada a investigação de ferramenta de teste pros cinco pontos pendentes do módulo `app` (Bluetooth, NFC, escrita/compartilhamento de arquivo, PDF, `SessionViewModel`). | Resolução da pendência "Decidir ferramenta de teste pro módulo `app`" |
| 0.9.0 | 16-08-2026 | Acrescentada a investigação da implementação do teste de `MainActivity.kt` (NFC) — duas armadilhas de Robolectric encontradas e resolvidas, achado de precisão em `architecture.md` corrigido. | Escrita do primeiro teste real de `BleAccessoryService.kt`/`MainActivity.kt`/`ReportFileWriter.kt`/`ReportShareIntent.kt`/`SessionViewModel.kt` |
| 0.10.0 | 17-08-2026 | Acrescentada a investigação da implementação do teste de `BleAccessoryService.kt` (Bluetooth) — armadilha de Robolectric encontrada e resolvida, achado de precisão em `architecture.md` corrigido. | Escrita do segundo teste real do módulo `app` |
| 0.11.0 | 17-08-2026 | Acrescentada a investigação de teste de `ReportFileWriter.kt`/`ReportShareIntent.kt` — caminho antigo (Android 7 a 9) confirmado não testável com a ferramenta já escolhida, achado registrado; caminho novo testado de verdade (`writeReportCsv`, `buildReportShareIntent`). | Terceiro teste real do módulo `app` escrito e rodado |
| 0.12.0 | 17-08-2026 | Acrescentada a investigação da lacuna na forma exata de `SessionState`, dos tipos de `content` e do construtor de `SessionViewModel` — duas tentativas de contornar sem ADR própria, feitas e revertidas nesta mesma investigação. | Tentativa de escrever o teste de `SessionViewModel.kt`; pendência nova em `tasks.md` |
| 0.13.0 | 18-08-2026 | Acrescentada a investigação que formaliza a forma de `SessionState`, `content` e do construtor de `SessionViewModel` — releitura completa sem código, mais pesquisa externa nova (guia oficial de arquitetura do Android). | Resolução de [decisions/0026](<../decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md>) |
| 0.14.0 | 18-08-2026 | Acrescentada a investigação da escrita do teste de `SessionViewModel.kt` — divergência real encontrada ao rodar contra o código, comparação dos dois desenhos de `SessionState`, correção via `decisions/0027`. | Quarto e último teste real do módulo `app` escrito e rodado |
| 0.15.0 | 18-08-2026 | Acrescentada a investigação da combinação do recorte de temas e de eventos numa sessão multi-tema, incluindo as duas leituras corrigidas antes de fechar no desenho certo (conclusão final só apontada, não repetida — está em `decisions/0028`, Contexto) e a tentativa de pesquisa externa pra decisão de reaproveitar `sessionScope`, descartada por não ter fonte no mesmo padrão das demais citações do módulo. | Resolução de [decisions/0028](<../decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md>) |
| 0.16.0 | 18-08-2026 | Acrescentada a investigação da arquitetura de informação das telas do motor — duas lacunas encontradas ao reler o Documento de Conceito contra os documentos derivados, uma resolvida por citação (mecanismo já existente) e outra confirmada como achado de verdade por leitura de código. | Montagem do passo 1 do método de desenho visual já registrado em `architecture.md`; achado novo em `findings.md` |
| 0.17.0 | 22-08-2026 | Acrescentada a investigação do padrão de navegação hierárquica — decisão apoiada só em lógica interna, tentativa de fonte externa descartada por peso insuficiente, e três pendências de tela que já estavam resolvidas em outros documentos, nunca conectadas antes. | Resolução de [decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>) |
| 0.20.0 | 22-08-2026 | Estendida a mesma investigação: o achado sobre o Grupo B não estava isolado em `tasks.md` — a mesma frase imprecisa se repetia em mais quatro lugares. Critério corrigido: o que separa nota de acompanhamento de correção direta é a data (a informação certa já existia antes daquele texto ser escrito?), não se a worktree já foi mesclada — `decisions/0031` tinha ganhado nota de acompanhamento por engano, corrigido depois de checar a data de verdade. | Auditoria completa por `grep` da frase exata e checagem de data por commit, ao reconciliar esta worktree com `develop` |
| 0.21.0 | 30-08-2026 | Acrescentada a investigação do sistema visual (Material Design), acessibilidade e cor semente — cinco dos sete pontos do passo 3 já decididos em conversa; artigo de psicologia da cor lido por completo e descartado como base de decisão, por pedido explícito do próprio artigo; seis combinações de cor semente comparadas numa página própria, escolha entre duas delas ainda em aberto. | Pesquisa pro passo 3 do método de desenho visual já registrado em `architecture.md` |
| 0.22.0 | 30-08-2026 | Acrescentadas três entradas: fechamento da escolha da cor semente (com fonte oficial nova sobre orientação de marca do Google), nota de acompanhamento sobre a entrada anterior ter ficado desatualizada, e a reorganização de `wireframe.md`/`cor-semente-candidatas.html` pra uma pasta nova (`design/`). | Resolução de [decisions/0035](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>); correção estrutural de pasta do módulo |
| 0.23.0 | 30-08-2026 | Acrescentada a auditoria de leitura completa que precedeu a tarefa do protótipo navegável — cinco frases desatualizadas sobre aparência visual encontradas e corrigidas em `concept.md`, `architecture.md` e três ADRs (nota de acompanhamento). | Preparação pra "Montar o protótipo navegável", em `tasks.md` |
| 0.24.0 | 31-08-2026 | Acrescentada a verificação do protótipo navegável depois da revisão de PR — três pontos sem teste de verdade implementados (ramo `hasNextEvent`) ou testados (isolamento do leiaute tablet, clique real no botão "Pular peça" sob o toque-livre), com asserções específicas, não genéricas. | Resposta às decisões do usuário sobre os achados de `/revisar-pr` |
