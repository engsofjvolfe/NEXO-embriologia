# 0025 — Ferramenta de teste dos cinco pontos pendentes do módulo `app`

Resumo em linguagem simples: decide qual ferramenta testa cada um dos
cinco pontos do `app` que hoje só são "testados" por compilação real,
sem nenhum comportamento checado. A escolha pra quase todos é
Robolectric (ou `kotlin-test` puro, onde não existe nenhuma
dependência de Android) — o desenho do PDF (`ReportPdfRenderer.kt`) e
o caminho antigo de `ReportFileWriter.kt` (Android 7 a 9, ver nota de
acompanhamento) precisam de teste instrumentado, num aparelho ou
emulador de verdade, porque nenhuma ferramenta sem aparelho simula
esses dois comportamentos hoje.

**Status:** aceito

**Contexto:**

Cinco partes do módulo `app` — `BleAccessoryService.kt`,
`MainActivity.kt`, `ReportFileWriter.kt`/`ReportShareIntent.kt`,
`SessionViewModel.kt` e `ReportPdfRenderer.kt` — já foram escritas, mas
nenhuma delas tem
teste automatizado: cada uma só foi confirmada até hoje por
compilação real (`gradlew :app:assembleDebug`) ou, no caso do
esqueleto inicial, por instalação manual num emulador. Nenhuma dessas
confirmações prova comportamento — só que o código compila e o
aplicativo abre. Cada uma dessas cinco partes ficou registrada como
pendência em `tasks.md` no momento em que nasceu, sem ferramenta de
teste escolhida — a escolha, uma por categoria de dependência de
Android envolvida (Bluetooth, NFC, escrita de arquivo, PDF, ou
nenhuma), é o que esta ADR resolve. A pesquisa completa, fonte por
fonte, está em
[analysis.md](<../docs/analysis.md#2026-08-16-pesquisa-de-ferramenta-de-teste-pro-modulo-app>) —
nela também está a checagem de que a ferramenta escolhida pra
`connectivity` prova o contrato que já está documentado (decodificar e
repassar o identificador, `EI-VAL-02`), não só que ela consegue rodar
contra o código, mesma pergunta feita pras outras quatro partes.

Alternativa real considerada pra `connectivity` (`BleAccessoryService.kt`/`MainActivity.kt`):
isolar as duas atrás de uma interface trocável por implementação falsa
no teste (injeção de dependência). Não é necessária pra esta ADR —
raciocínio completo em
[analysis.md](<../docs/analysis.md#2026-08-16-pesquisa-de-ferramenta-de-teste-pro-modulo-app>)
— mas a ideia em si continua válida por outro motivo (reduzir
acoplamento com classe do Android, já recomendado em
[decisions/0015](<0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>)),
registrada como pendência própria em `tasks.md`, pra outro momento.

**Decisão:**

1. `BleAccessoryService.kt` (Bluetooth) e `MainActivity.kt` (NFC) —
   Robolectric 4.16.1, rodando sob JUnit 4 (`junit:junit:4.13.2`,
   única versão que o Robolectric documenta suporte oficial —
   `analysis.md`). O teste exige `testOptions { unitTests {
   isIncludeAndroidResources = true } }` em `app/build.gradle.kts`,
   conforme a própria documentação do Robolectric, pra carregar
   recurso do manifesto durante o teste.
2. `ReportFileWriter.kt`/`ReportShareIntent.kt` (escrita e
   compartilhamento de arquivo) — Robolectric também, mesma versão, só
   pro caminho novo que
   [decisions/0019](<0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>)
   já fixou (`MediaStore.Downloads` a partir do Android 10) — o
   `ShadowContentResolver` cobre esse caminho; o caminho antigo
   (`Environment.getExternalStoragePublicDirectory`, antes do Android
   10) não é coberto, ver nota de acompanhamento. O que o teste precisa
   provar, segundo DA-ARM-01/DA-ARM-02 (documento 4): que o conteúdo
   termina no armazenamento local simulado
   (`registerOutputStream`/`registerOutputStreamSupplier`) — nunca só
   que a função rodou sem erro.
3. `SessionViewModel.kt` inteiro (incluindo `onExitConfirmed` e o
   gatilho de ociosidade) — `kotlin-test-junit` (variante que roda sob
   JUnit 4; `app` não aplica o plugin Gradle do Kotlin de forma
   explícita — usa o suporte embutido do AGP 9,
   [pitfalls.md](<../docs/pitfalls.md#2026-08-14-agp-9-nao-aceita-mais-plugin-kotlin-android>)
   —, então a troca automática de variante que `kotlin("test")` dá de
   graça no `core` não vale aqui), sem Robolectric —
   [decisions/0020](<0020-ligacao-entre-leitura-de-peca-e-a-tela.md>)
   e
   [decisions/0023](<0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md>)
   já confirmam que nenhuma classe do Android está envolvida.
   `kotlinx-coroutines-test` (mesma versão já fixada no projeto,
   1.11.0) é necessário, porque o gatilho de ociosidade usa
   `viewModelScope`/`Dispatchers.Main`, que só existe em teste com um
   despachante de teste instalado (`Dispatchers.setMain`).
4. `ReportPdfRenderer.kt` (PDF) — teste instrumentado (`androidx.test`),
   único jeito de exercitar `PdfDocument`/`Canvas` de verdade, rodando
   dentro de um aparelho ou emulador Android.
   `androidx.test:core` 1.7.0, `androidx.test.ext:junit` 1.3.0,
   `androidx.test:runner` 1.7.0 (versões estáveis atuais,
   `analysis.md`). O teste exige
   `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"`
   em `defaultConfig`, dentro de `app/build.gradle.kts`.

**Consequências:**

Nenhum dos cinco pontos que `tasks.md` lista fica sem ferramenta
decidida. As dependências listadas acima (versão de Robolectric,
`androidx.test`, `kotlin-test-junit`, `kotlinx-coroutines-test`) ainda
não estão declaradas em `gradle/libs.versions.toml` nem em
`app/build.gradle.kts` — declarar essas dependências é parte da
implementação de cada teste, pendência própria em `tasks.md`, fora do
escopo desta ADR.

**Nota de acompanhamento (17-08-2026):**

*Resumo simples:* o ponto 2 desta decisão presumia que Robolectric
cobria `ReportFileWriter.kt`/`ReportShareIntent.kt` inteiros — não
cobre. Dos dois caminhos que `decisions/0019` já fixou pra escrever o
relatório, só o mais novo (Android 10 em diante) é testável assim; o
mais antigo (Android 7 a 9) depende de um retorno do Android que a
ferramenta nunca dispara.

*Detalhe técnico:* achado completo, com as três fontes que confirmam
isso, em
[findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric](<../docs/findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric>) —
não repetido aqui. O ponto 2 passa a valer só pro caminho novo; o
caminho antigo entra na mesma categoria do ponto 4 (`ReportPdfRenderer.kt`),
exigindo teste instrumentado — pendência registrada em `tasks.md`.

## Referências

Fontes externas consultadas nesta decisão, no formato definido pela
norma ABNT NBR 6023 (Informação e documentação — Referências). Citadas
no corpo do documento como (ENTIDADE, ano). Acesso em 16 ago. 2026,
salvo indicação contrária.

ROBOLECTRIC. **Getting started**. [S.l.], [s.d.]. Disponível em:
https://robolectric.org/getting-started/.

ROBOLECTRIC. **Compatibility table**. [S.l.], [s.d.]. Disponível em:
https://robolectric.org/compatibility_table/.

ROBOLECTRIC (ORGANIZATION). **robolectric/robolectric** [repositório de
código-fonte]. GitHub, [s.d.]. Disponível em:
https://github.com/robolectric/robolectric. Nota sobre uso: consultado
por leitura direta de arquivo-fonte (`ShadowNfcAdapter.java`,
`ShadowContentResolver.java`, `ShadowEnvironment.java`, ausência
confirmada de `ShadowPdfDocument.java`) e da API de lançamentos, não
por resultado de busca de código.

GOOGLE (ANDROID TEAM). **android/nowinandroid** [repositório de
código-fonte, aplicativo de referência oficial]. GitHub, [s.d.].
Disponível em: https://github.com/android/nowinandroid. Nota sobre uso:
consultado por leitura direta de `gradle/libs.versions.toml`, pra
confirmar versão real de Robolectric/AGP/Kotlin usada junto em produção
por esse aplicativo mantido pelo próprio time do Android.

GOOGLE. **Test — Jetpack releases**. Android Developers, [s.d.].
Disponível em: https://developer.android.com/jetpack/androidx/releases/test.
