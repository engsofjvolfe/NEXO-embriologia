# 0043 — Mecanismo de classificação de tamanho de janela (`isTabletLayout`)

Resumo em linguagem simples: `decisions/0033` já fixou os limiares oficiais do Android (compacta
<600dp, média 600-839dp, expandida >=840dp) e que `SessionConfigurationScreen` ganha leiaute de
duas colunas em tela média ou expandida — mas nunca decidiu por qual mecanismo o código descobre a
largura real da janela pra aplicar esses limiares. Esta ADR decide: a API oficial de classificação
de tamanho de janela do próprio Android (`WindowSizeClass`), que compara a largura contra os
limiares certos sozinha, sem o código precisar guardar o número `600` em lugar nenhum.

**Status:** aceito

**Contexto:**

`MotorApp.kt` passava `isTabletLayout = false` fixo pra `SessionConfigurationScreen`, nunca
calculado — achado na revisão de PR (revisor-valores-fixos), registrado como pendência em
`tasks.md` e como limitação explícita em `architecture.md`. `decisions/0033` já decide os limiares
exatos (item 2) e que Configuração da sessão precisa desse tratamento (item 3), mas descreve isso
como escolha de desenho visual, sem descer ao nível de qual API do código lê a largura real.

Pesquisa direta na documentação oficial (Android Developers, acesso em 02 set. 2026):

- A forma recomendada de obter a classificação dentro de um Composable, segundo a página "Use
  window size classes" (mesma já citada em `decisions/0033`), é a função de nível superior
  `currentWindowAdaptiveInfo()`, da biblioteca `androidx.compose.material3.adaptive`: "Compute the
  current `WindowSizeClass` using the `currentWindowAdaptiveInfo()` top-level function... The
  function returns an instance of `WindowAdaptiveInfo`, which contains `windowSizeClass`" — em
  tradução livre, calcula a `WindowSizeClass` atual usando essa função, que devolve um objeto com
  o campo `windowSizeClass` já pronto.
- Essa função funciona dentro de qualquer Composable, sem exigir uma referência a `Activity` — só
  o contexto de composição já em uso em `MotorApp()`.
- Checar se a largura já está na faixa média ou maior usa
  `windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)` — a
  constante `WIDTH_DP_MEDIUM_LOWER_BOUND` já é o limiar oficial (600dp) definido pela própria
  biblioteca; o código nunca escreve o número `600` diretamente.
- A biblioteca (`androidx.compose.material3.adaptive:adaptive`) está na versão estável `1.3.0`
  (lançada 12 ago. 2026), sem marcação de experimental/alpha/beta na tabela de versões oficial —
  consultada em https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive.
- A mesma página de notas de versão registra, na versão `1.3.0-alpha10` (08 abr. 2026): "Deprecate
  `currentWindowAdaptiveInfo` and introduce V2 of it" — em tradução livre, `currentWindowAdaptiveInfo()`
  foi marcada como descontinuada, com uma "V2" introduzida no lugar. Três tentativas de achar o
  nome exato da função V2 substituta (a mesma página de notas de versão, a referência de API do
  pacote em developer.android.com, e o rastreador de problemas do Google linkado na nota) não
  confirmaram esse nome — a página de referência de API não trouxe o conteúdo detalhado da
  assinatura substituta na consulta feita, e o rastreador de problemas exige login pra ver o
  conteúdo completo. `currentWindowAdaptiveInfo()` (a função usada nesta decisão) continua
  funcional e faz parte da mesma versão estável `1.3.0`, não removida, só marcada como
  descontinuada — mesma categoria de aviso já aceita conscientemente neste módulo em
  [pitfalls.md#2026-08-15-suppress-nao-vale-em-instrucao-solta](<../docs/pitfalls.md#2026-08-15-suppress-nao-vale-em-instrucao-solta>)
  pras duas APIs de Bluetooth descontinuadas ainda necessárias (mesma categoria de aviso, contexto
  diferente: lá a API é descontinuada por faixa de versão de SDK, sem substituto de mesmo nível;
  aqui a API é descontinuada por já existir sucessora, mas com o nome dessa sucessora não
  confirmável agora). Sem `@Suppress` aqui: o aviso do compilador (`is deprecated`) fica visível de
  propósito, marcando o ponto exato a revisar quando o nome da V2 for confirmado numa fonte
  acessível — não silenciado.

Duas alternativas reais pro mecanismo em si:

1. **API oficial `WindowSizeClass`, via `currentWindowAdaptiveInfo()`** (alternativa escolhida, ver
   Decisão) — dependência nova, mas estável, e é a própria fonte que `decisions/0033` já cita como
   autoridade pros limiares; nenhum limiar precisa ser copiado à mão pro código do motor.
2. **`LocalConfiguration.current.screenWidthDp`**, comparado à mão contra um limiar escrito direto
   no código do motor — sem dependência nova, mas reintroduz exatamente o problema que motivou
   esta ADR (um número mágico, `600`, sem fonte visível no próprio código) só que um nível abaixo;
   descartada por isso.

**Decisão:**

1. Dependência nova em `gradle/libs.versions.toml` e `app/build.gradle.kts`:
   `androidx.compose.material3.adaptive:adaptive:1.3.0`.
2. `MotorApp()` chama `currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
   WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)` pra calcular `isTabletLayout` — nenhum valor fixo,
   nenhum limiar copiado à mão.
3. Revisável, sem quebrar nenhum código já escrito, se uma necessidade real de distinguir também
   "large"/"extra-large" (parâmetro `supportLargeAndXLargeWidth`) aparecer em outra tela no futuro
   — nenhuma das 15 demais entradas de tela tem essa necessidade hoje (`decisions/0033`, item 5).
4. Revisável também quando o nome da função V2 (ver Contexto) for confirmado numa fonte acessível —
   troca direta, sem mudança de comportamento esperada.

**Consequências:**

Fecha a pendência "`isTabletLayout` fixo em `MotorApp.kt`" em `tasks.md`; remove a limitação
correspondente de `architecture.md`, seção "Ponto de entrada real (MotorApp)". Teste escrito antes
do código (`MotorAppTest.kt`, dois casos: largura compacta padrão do Robolectric mantém leiaute de
celular; `@Config(qualifiers = "w840dp-h480dp")` força leiaute de tablet), a partir só do contrato
já existente de `SessionConfigurationScreen.kt` (decisions/0033) — nunca do valor fixo que estava
em `MotorApp.kt`. Testado ao vivo: `gradlew :app:testDebugUnitTest :core:test`, `BUILD SUCCESSFUL`,
suíte completa sem quebra.

## Referências

Fontes externas consultadas para embasar esta decisão, no formato definido pela norma ABNT NBR
6023 (Informação e documentação — Referências). Citações traduzidas livremente no corpo do
documento; texto original preservado entre aspas antes da tradução. Citadas no corpo do documento
como (GOOGLE, ano).

GOOGLE. **Use window size classes**. Android Developers, [s.d.]. Disponível em:
https://developer.android.com/develop/adaptive-apps/guides/use-window-size-classes. Acesso em: 02
set. 2026.

GOOGLE. **Compose Material 3 Adaptive**. Android Developers — Jetpack releases, [s.d.]. Disponível
em: https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive. Acesso em:
02 set. 2026.
