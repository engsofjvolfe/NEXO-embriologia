# 0037 — Ferramenta de teste das telas em Jetpack Compose do módulo `app`

Resumo em linguagem simples: a pendência de escrever o código de verdade das 17 telas do motor,
em Jetpack Compose, precisa de teste antes do código (regra já seguida em todo o módulo). Faltava
decidir com que ferramenta testar uma tela — a mesma pergunta que
[decisions/0025](0025-ferramenta-de-teste-do-modulo-app.md) já respondeu pra outras cinco partes do
`app` (Bluetooth, NFC, escrita de relatório, `ViewModel`, PDF), mas que nunca cobriu a tela em si.
Esta ADR decide: Robolectric, rodando como teste local (sem aparelho nem emulador) — mesma
ferramenta e mesma configuração já usada nas outras partes do `app`.

Convenção dos códigos citados abaixo:
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.

**Status:** aceito

**Contexto:**

[decisions/0031](0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md) já fixou o Jetpack
Compose como tecnologia de desenho das telas do `app`; [decisions/0025](0025-ferramenta-de-teste-do-modulo-app.md)
já decidiu a ferramenta de teste de cinco partes específicas do `app` (`BleAccessoryService.kt`,
`MainActivity.kt`, `ReportFileWriter.kt`/`ReportShareIntent.kt`, `SessionViewModel.kt`,
`ReportPdfRenderer.kt`) — nenhuma delas é a tela em si. A pendência "Escrever o código real das
telas do módulo `app` (Jetpack Compose) e ligar tudo" ([tasks.md](<../docs/tasks.md#em-aberto>))
segue a mesma regra de todo o resto do módulo: teste antes do código. Faltava, por isso, a mesma
escolha que 0025 já fez, agora pra Composable de tela.

Duas alternativas reais, confirmadas em fonte oficial (Android Developers), antes de decidir:

1. **Teste instrumentado** (`androidTest`, exige aparelho ou emulador ligado) — caminho descrito na
   página oficial de teste de Compose (GOOGLE, [s.d.]a): `createComposeRule()` ou
   `createAndroidComposeRule<Activity>()`, dependências em `androidTestImplementation`. Sempre
   funciona, mas paga o custo de emulador ligado a cada rodada de teste — mesmo custo que já levou
   o projeto, em 0025, a reservar teste instrumentado só pro que o Robolectric comprovadamente não
   cobre (`ReportPdfRenderer.kt`, caminho antigo de `ReportFileWriter.kt`).
2. **Teste local via Robolectric** (alternativa escolhida, ver Decisão) — a mesma API de teste
   (`ComposeTestRule`/`createComposeRule()`), rodando dentro da JVM, sem aparelho.

A escolha se apoia em fonte oficial distinta da usada em 0025, mas do mesmo tipo (documentação
oficial do Android Developers, GOOGLE, [s.d.]b):

- "Robolectric is an open-source framework maintained by Google that lets you run tests in a
  simulated Android environment inside a JVM, without the overhead and flakiness of an emulator" —
  em tradução livre: o Robolectric é um framework de código aberto, mantido pelo Google, que roda
  teste dentro de um ambiente Android simulado, dentro de uma JVM (a máquina virtual que executa
  código Java/Kotlin), sem o custo nem a instabilidade de um emulador de verdade.
- "most Google apps... rely heavily on Robolectric" — em tradução livre: a maioria dos próprios
  aplicativos do Google depende fortemente do Robolectric.
- "Robolectric can also run UI tests such as Espresso or Compose tests. You can convert an
  instrumented test to Robolectric by moving it to the `test` source set and setting up the
  Robolectric dependencies" — em tradução livre: o Robolectric também roda teste de interface, como
  teste de Espresso ou de Compose; um teste instrumentado vira teste Robolectric só de mudar ele de
  pasta (de `androidTest` pra `test`) e configurar as dependências do Robolectric.
- "Most UI tests don't interact with the framework and you can run them on Robolectric... For
  example, when a Compose test verifies that the UI has changed after clicking a button" — em
  tradução livre: a maioria dos testes de interface não depende de verdade do sistema operacional, e
  pode rodar no Robolectric — por exemplo, quando um teste de Compose confirma que a tela mudou
  depois de um clique num botão. Esse é exatamente o tipo de teste que as telas do motor exigem:
  confirmar que um texto aparece pro estado certo (`SessionScreen`, decisions/0022), e que tocar um
  botão chama o método certo do `ViewModel` (decisions/0032) — nunca comportamento de sistema
  (borda a borda da tela, `WebView`, câmera) que a mesma fonte lista como fora do alcance do
  Robolectric.
- A configuração de build exigida é a mesma já usada em 0025 pra `BleAccessoryService.kt`/
  `MainActivity.kt`: `testOptions { unitTests { isIncludeAndroidResources = true } }` (GOOGLE,
  [s.d.]b) — nenhuma configuração nova, só dependência nova de teste.

**Decisão:**

1. Teste de tela em Compose usa Robolectric, mesma versão já fixada em
   [decisions/0025](0025-ferramenta-de-teste-do-modulo-app.md) (4.16.1, sob JUnit 4 —
   `junit:junit:4.13.2`) — nunca uma versão à parte só pra tela.
2. O teste mora no mesmo lugar dos demais testes de `app` (pasta `test`, teste local) — nunca em
   `androidTest` —, usando `createComposeRule()` (nunca `createAndroidComposeRule<Activity>()`: uma
   tela do motor é testada isolada, recebendo um `SessionUiState`/`SessionScreen` já pronto como
   parâmetro, mesma lógica de estado já injetado que o teste de `SessionViewModel.kt` usa — nenhuma
   tela depende de uma `Activity` específica pra ser testada).
3. Dependências novas de teste: `androidx.compose.ui:ui-test-junit4` (`testImplementation`) e
   `androidx.compose.ui:ui-test-manifest` (`debugImplementation`, exigida só por
   `createComposeRule()`, conforme a própria página oficial de teste de Compose — GOOGLE, [s.d.]a).
   Declaradas já no momento de escrever o primeiro teste de tela, não como pendência separada —
   diferente de 0025 (que decidiu a ferramenta antes de qualquer teste em si existir), aqui a
   escrita do primeiro teste começa logo em seguida a esta ADR, então não faz sentido separar
   "decidir a dependência" de "declarar a dependência" em dois momentos.
4. Teste instrumentado (emulador/aparelho) fica reservado pra comportamento que o Robolectric
   comprovadamente não cobre — mesma régua já usada em 0025. Nenhuma tela do motor exige isso hoje:
   nenhuma usa `WebView`, câmera, ou comportamento de sistema (borda a borda da tela, picture-in-
   picture) — os únicos exemplos que a própria fonte oficial cita como fora do alcance do
   Robolectric. Se algum dia isso mudar, a decisão é revista então, não antecipada aqui.
5. Fidelidade visual real (pixel, cor, espaçamento) não é o que este teste prova — isso já foi
   validado no protótipo navegável e na avaliação heurística
   ([decisions/0036](0036-ferramenta-e-fidelidade-do-prototipo-navegavel.md)). O teste de Compose
   aqui prova comportamento (texto certo pro estado certo, botão certo chamando o método certo do
   `ViewModel`), nunca aparência.

**Consequências:**

Desbloqueia escrever teste antes de qualquer tela, cumprindo a mesma regra já seguida em todo o
resto do módulo. As duas dependências do ponto 3 entram em `gradle/libs.versions.toml` e
`app/build.gradle.kts` no mesmo passo em que o primeiro teste de tela é escrito, logo depois desta
ADR — não como pendência separada em `tasks.md`.

## Referências

Fontes externas consultadas para embasar esta decisão, no formato definido pela norma ABNT NBR 6023
(Informação e documentação — Referências). Citações traduzidas livremente no corpo do documento;
texto original preservado entre aspas antes da tradução. Citadas no corpo do documento como
(GOOGLE, ano).

GOOGLE. **Test your Compose layout**. Android Developers, [s.d.]a. Disponível em:
https://developer.android.com/develop/ui/compose/testing. Acesso em: 01 set. 2026.

GOOGLE. **Robolectric strategies**. Android Developers — Test your app on Android, [s.d.]b.
Disponível em: https://developer.android.com/training/testing/local-tests/robolectric. Acesso em:
01 set. 2026.
