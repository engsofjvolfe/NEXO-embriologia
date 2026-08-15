# Analysis — Motor

<!-- module-doc-type: analysis -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Analysis |
| Versão | 0.7.0 |
| Data | 15-08-2026 |
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
