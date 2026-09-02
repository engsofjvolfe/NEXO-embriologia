# Pitfalls — Motor

<!-- module-doc-type: pitfalls -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Pitfalls |
| Versão | 0.9.0 |
| Data | 01-09-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Comportamento não óbvio de ferramenta/mecanismo usado só neste módulo
> — pra não redescobrir o mesmo problema depois. Registrado conforme
> aparece, tipicamente durante a implementação (quando o código encontra
> o comportamento real de ferramenta/ambiente). Raramente muda; se mudar
> (versão nova de dependência, por exemplo), mesma regra de
> `findings.md`: entrada nova, não reescrita.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, resumo simples, depois detalhe técnico.

## Índice
- [Armadilhas](#armadilhas)
- [Controle de versão](#controle-de-versão)

## Armadilhas

### <a id="2026-08-14-agp-9-nao-aceita-mais-plugin-kotlin-android"></a>2026-08-14 — AGP 9 não aceita mais o plugin `org.jetbrains.kotlin.android`

*Resumo simples:* a partir da versão 9.0 do Android Gradle Plugin
(AGP), o suporte a Kotlin já vem embutido nele — aplicar o plugin
separado que todo tutorial mais antigo ensina (`org.jetbrains.kotlin.android`)
quebra a build, em vez de só ser redundante.

*Detalhe técnico:* build do módulo `app` (AGP 9.3.0, ver
[decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>))
falhou com "The 'org.jetbrains.kotlin.android' plugin is no longer
required for Kotlin support since AGP 9.0" ao aplicar esse plugin em
paralelo ao `com.android.application`. Correção: não aplicar o plugin
Kotlin no módulo `app` — a compilação Kotlin roda embutida no próprio
AGP, com `jvmTarget` continuando configurável via `kotlinOptions {}`
dentro do bloco `android {}`, sem mudança de sintaxe além de remover a
linha do plugin. Módulo `core`, que não usa o Android Gradle Plugin,
não é afetado — continua aplicando `org.jetbrains.kotlin.jvm`
normalmente. Possível reverter esse comportamento com
`android.builtInKotlin=false` em `gradle.properties`, mas essa saída de
emergência é removida na versão 10.0 do AGP (GOOGLE, 2026), então não
foi usada aqui.

### <a id="2026-08-14-kotlin-embutido-do-agp-exige-versao-igual-no-core"></a>2026-08-14 — Kotlin embutido do AGP exige a mesma versão de Kotlin do módulo `core`

*Resumo simples:* o módulo `app` não escolhe mais sua própria versão
de Kotlin (armadilha anterior) — ele usa a que vem dentro do AGP
9.3.0, que é mais antiga que a que o `core` já usava. Se as duas
ficarem diferentes, o `app` não consegue ler o código já compilado do
`core`.

*Detalhe técnico:* com `core` em Kotlin 2.4.10 (versão em uso antes
desta tarefa) e o Kotlin embutido do AGP 9.3.0 na faixa 2.2.x, a build
falhou com "Module was compiled with an incompatible version of
Kotlin. The binary version of its metadata is 2.4.0, expected version
is 2.2.0" ao `app` depender de `:core`. Tentativa de forçar uma versão
mais nova de Kotlin dentro do próprio AGP (`buildscript { dependencies
{ classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10") } }`,
sugerida pela documentação oficial pra "usar uma versão de KGP mais
alta") quebrou de um jeito diferente e pior: "Could not create an
instance of type ...KotlinAndroidTarget" — o AGP 9.3.0 foi compilado
contra uma versão específica das classes internas do Kotlin Gradle
Plugin, incompatível com uma versão forçada por fora. Correção efetiva:
descer a versão de Kotlin do `core` pra 2.2.10 (mesma faixa que o AGP
9.3.0 já traz embutida), em vez de tentar subir a do `app` — ver
[decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>).
Suíte de teste do `core` (`gradlew :core:test`) rodada de novo depois
da mudança de versão, sem quebra.

Consequência colateral, ainda presente mas sem quebrar a build: o
Gradle avisa "The Kotlin Gradle plugin was loaded multiple times in
different subprojects" — porque `core` aplica o plugin
`org.jetbrains.kotlin.jvm` explicitamente enquanto `app` carrega o
Kotlin embutido do AGP por dentro, e os dois convivem no mesmo build.
Registrado aqui como aviso conhecido, não como problema em aberto — se
uma atualização futura de Gradle ou AGP transformar esse aviso em erro
de verdade, este é o motivo.

### <a id="2026-08-14-jsonprimitive-booleanornull-intornull-nao-checam-isstring"></a>2026-08-14 — `booleanOrNull`/`intOrNull` do kotlinx.serialization aceitam texto entre aspas como se fosse booleano/número

*Resumo simples:* ao ler um campo de um JSON de estrutura desconhecida
(`JsonElement`), as funções prontas `booleanOrNull` e `intOrNull` da
biblioteca aceitam um valor que veio entre aspas no JSON original
(por exemplo, `"hint_enabled": "true"`, uma string) do mesmo jeito que
aceitam o literal sem aspas (`"hint_enabled": true`) — não bastava
usar essas funções prontas pra validar que um campo é *de fato* do
tipo esperado pelo esquema.

*Detalhe técnico:* implementação de `JsonPrimitive.booleanOrNull`
(kotlinx.serialization, `formats/json/commonMain/.../JsonElement.kt`):
`get() = content.toBooleanStrictOrNull()` — opera só sobre `content`
(o texto, sempre uma string internamente), sem checar `isString`
antes. `intOrNull` segue o mesmo padrão, convertendo `content` direto.
`isString` é a propriedade que de fato diferencia as duas origens —
"indicates whether the primitive was explicitly constructed from
String... E.g. `JsonPrimitive("42")` is represented by a string, while
`JsonPrimitive(42)` is not" (JETBRAINS, [s.d.]). Sem checar `isString`
manualmente, um pacote de conteúdo com `"hint_enabled": "true"` (tipo
errado, deveria ser rejeitado por PD-IMP-01) passaria despercebido.
Correção usada no pacote `content`
([decisions/0013](<../decisions/0013-desenho-do-pacote-content.md>)):
três funções próprias (`asStringOrNull`, `asBooleanOrNull`,
`asIntOrNull`, em `ContentImport.kt`) checam `isString` explicitamente
antes de converter, coerentes com o "type" exato de cada campo do
esquema.

### <a id="2026-08-15-suppress-nao-vale-em-instrucao-solta"></a>2026-08-15 — `@Suppress` não é válido em cima de uma instrução solta, só de uma declaração

*Resumo simples:* pra silenciar um aviso de "descontinuado"
(`@Suppress("DEPRECATION")`), escrever a anotação bem em cima da linha
que usa a API descontinuada só funciona se essa linha for uma
declaração (função, variável, classe) — em cima de uma instrução
solta, como uma atribuição de propriedade (`objeto.propriedade =
valor`), essa posição não é válida em Kotlin.

*Detalhe técnico:* ao escrever `BleAccessoryService.kt` (pacote
`connectivity` do módulo `app`), duas chamadas a APIs de Bluetooth já
descontinuadas, mas ainda necessárias pra cobrir toda a faixa de
`minSdk` 24 a `targetSdk` 36
([decisions/0018](<../decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md>)) —
`BluetoothGattDescriptor.value =` e `BluetoothGatt.writeDescriptor(descriptor)`
de um argumento só — foram inicialmente marcadas com
`@Suppress("DEPRECATION")` direto em cima de cada instrução solta.
Corrigido antes de qualquer tentativa de compilação (revisão manual,
já que o SDK do Android ainda não estava configurado neste ambiente
naquele momento): a anotação precisa ir na declaração que contém essas
instruções (a função `onServicesDiscovered`, no caso), cobrindo o
corpo inteiro — nunca em cima de uma instrução isolada dentro dela.
Compilação real (`gradlew :app:assembleDebug`), feita depois da
correção, confirmou que o código corrigido compila sem erro.

### <a id="2026-08-15-local-properties-precisa-de-barra-dupla"></a>2026-08-15 — `sdk.dir` em `local.properties` precisa de barra invertida duplicada no Windows

*Resumo simples:* `local.properties` (o arquivo, por worktree, que
aponta pro SDK do Android instalado na máquina) segue o formato de
arquivo `.properties` do Java, que usa barra invertida como caractere
de escape — um caminho comum do Windows, com uma barra só entre cada
pasta, não é lido como o caminho pretendido.

*Detalhe técnico:* `sdk.dir=C\:\Android\sdk` (uma barra antes de cada
pasta, jeito mais natural de escrever um caminho do Windows) faz o
Gradle falhar com `java.io.IOException: A sintaxe do nome do arquivo,
do nome do diretório ou do rótulo do volume está incorreta` ao rodar
qualquer tarefa que dependa do SDK (`:app:compileDebugKotlin`,
`:app:assembleDebug`) — o parser de `.properties` engole a barra como
escape do caractere seguinte, entregando um caminho quebrado. Forma
correta: `sdk.dir=C\:\\Android\\sdk` — barra dupla antes de cada pasta,
pra cada uma virar uma barra literal depois do parser. Como
`local.properties` é criado do zero em toda worktree nova (arquivo
ignorado pelo git, não existe cópia compartilhada), esse erro tende a
se repetir a cada worktree nova, não é algo resolvido uma vez só.

### <a id="2026-08-16-nfcadapter-getdefaultadapter-nulo-sem-feature-nfc"></a>2026-08-16 — `NfcAdapter.getDefaultAdapter()` volta nulo no Robolectric sem declarar a característica de hardware NFC

*Resumo simples:* num teste Robolectric, pedir o adaptador NFC padrão
devolve `null` — mesmo o `AndroidManifest.xml` já declarando NFC como
hardware opcional — até o teste avisar, explicitamente, o gerenciador
de pacotes simulado de que o aparelho "tem" NFC.

*Detalhe técnico:* `ShadowNfcAdapter.getDefaultAdapter(Context)`
delega pro método real (`@Direct`) de `android.nfc.NfcAdapter`, que
consulta `PackageManager.hasSystemFeature(FEATURE_NFC)` — o
`PackageManager` simulado do Robolectric não ativa uma característica
de hardware sozinho só por ela estar declarada `android:required="false"`
no manifesto. Sem isso, `NfcAdapter.getDefaultAdapter(activity)` no
teste devolve `null`, e qualquer uso dele (`Shadows.shadowOf(null)`)
lança `NullPointerException`. Correção: no teste, antes de tudo,
`Shadows.shadowOf(application.packageManager).setSystemFeature(PackageManager.FEATURE_NFC, true)`.

### <a id="2026-08-16-shadownfcadapter-createmocktag-nao-aceita-id"></a>2026-08-16 — `ShadowNfcAdapter.createMockTag()` não aceita o identificador da etiqueta como parâmetro

*Resumo simples:* o método pronto do Robolectric pra criar uma
etiqueta NFC de teste não deixa escolher qual identificador ela carrega
— sempre cria com identificador vazio — então não serve sozinho pra
testar que um identificador específico chega decodificado do outro
lado.

*Detalhe técnico:* lendo o código-fonte oficial
(`shadows/framework/.../ShadowNfcAdapter.java`, branch `master`),
`createMockTag()` chama, por reflexão, o método estático oculto
`android.nfc.Tag.createMockTag(byte[] id, int[] techList, Bundle[]
techListExtras[, long cookie])` sempre com `id = new byte[0]` — sem
jeito de passar um `id` próprio por fora. O quarto parâmetro (`cookie`)
só existe a partir da versão de Android seguinte ao Tiramisu (API 33);
em API igual ou anterior a essa, o método tem só os três primeiros
parâmetros. Solução: chamar o mesmo método oculto diretamente, pela
mesma técnica de reflexão que o Robolectric usa por dentro
(`org.robolectric.util.ReflectionHelpers.callStaticMethod`), passando o
`id` desejado — ramificando pelos dois formatos de assinatura conforme
`RuntimeEnvironment.getApiLevel()`, igual o Robolectric já faz.

### <a id="2026-08-17-scanrecord-parsefrombytes-e-metodo-oculto"></a>2026-08-17 — `ScanRecord.parseFromBytes` é método oculto do SDK público do Android

*Resumo simples:* montar um resultado de busca Bluetooth (`ScanResult`)
de teste, anunciando um serviço específico, exige montar o `ScanRecord`
a partir dos bytes brutos de propaganda BLE — mas o método que faz
isso não está disponível pra chamar direto, escondido do SDK público
do próprio Android.

*Detalhe técnico:* `android.bluetooth.le.ScanRecord.parseFromBytes(byte[])`
é um método estático oculto (não faz parte do SDK público que o
compilador enxerga, mesmo existindo de verdade na classe) — tentar
chamar direto falha com `Unresolved reference: parseFromBytes`.
Solução, mesma técnica já registrada pro `Tag.createMockTag` do NFC,
também um método oculto do SDK público: chamar por reflexão
(`org.robolectric.util.ReflectionHelpers.callStaticMethod`), passando
os bytes já montados manualmente no formato de estrutura de
propaganda BLE (tamanho, tipo `0x07` — lista completa de UUID de 128
bits —, UUID em ordem de byte invertida).

### <a id="2026-09-01-sem-tema-xml-noactionbar-a-barra-nativa-cobre-o-compose"></a>2026-09-01 — Sem um tema XML `NoActionBar`, a barra de título nativa do Android aparece por cima das telas em Compose

*Resumo simples:* o conteúdo desenhado com Jetpack Compose (a ferramenta usada pra desenhar a
tela, ver [decisions/0031](<../decisions/0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md>))
só controla o que aparece dentro da tela — a moldura da janela em volta dele (a barra escura no
topo, com o título do aplicativo) é decidida por um arquivo de configuração separado, que nunca
tinha sido criado neste módulo. Sem esse arquivo, o Android desenha sua própria barra de título
padrão por cima de tudo, sem nenhuma das cores/fontes escolhidas em
[decisions/0035](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>).

*Detalhe técnico:* confirmado ao vivo — captura de tela real do aplicativo rodando no emulador
mostrou uma barra preta, com o texto "NEXO Motor" (vindo de `android:label` no
`AndroidManifest.xml`), acima do conteúdo Compose (o campo "Buscar" e a lista de navegação),
mesmo com `NexoMotorTheme` (Compose) já aplicado em `MainActivity.kt`
(`setContent { NexoMotorTheme { MotorApp() } }`). Causa: `app/src/main/res/` não existia até este
ponto — nenhum arquivo `values/themes.xml`, e o `<application>` do `AndroidManifest.xml` não
declarava `android:theme` nenhum. Sem um tema XML de base, o Android usa um tema padrão da
plataforma, que inclui uma barra de ação (*ActionBar*) nativa — mecanismo completamente separado
de `MaterialTheme`/`NexoMotorTheme` do Compose, que só afeta o que é desenhado dentro de
`setContent`, nunca a moldura da janela em volta dele. Corrigido criando
`app/src/main/res/values/themes.xml`, com um tema que estende
`Theme.Material3.DayNight.NoActionBar` (suprime a barra nativa), referenciado via
`android:theme="@style/Theme.NexoMotor"` no `<application>` do manifesto. Esse estilo, apesar do
nome "Material3", não vem do Compose (`androidx.compose.material3`, já usado neste módulo) — é
parte da biblioteca de Views/XML `com.google.android.material:material` (Material Components for
Android), nunca antes declarada aqui; sem ela, a build falha com "AAPT: error: resource
style/Theme.Material3.DayNight.NoActionBar not found". Dependência acrescentada na versão estável
mais recente confirmada direto no repositório oficial (`dl.google.com/android/maven2/.../
maven-metadata.xml`, tag `<release>`): `1.14.0`. Build resolve a referência ao tema sem erro
(`BUILD SUCCESSFUL`); confirmação visual da ausência da barra numa segunda captura de tela segue
em aberto — ver [analysis.md](<analysis.md#2026-09-01-confirmacao-visual-do-ponto-de-entrada-real-e-limite-de-ambiente-do-emulador>).

## Referências

Fontes citadas nas armadilhas acima, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **Android Gradle plugin 9.0.1 (January 2026) — Android Gradle
plugin built-in Kotlin**. Android Developers, 2026. Disponível em:
https://developer.android.com/build/releases/agp-9-0-0-release-notes#android-gradle-plugin-built-in-kotlin.
Acesso em: 14 ago. 2026.

JETBRAINS. **JsonPrimitive.isString**. Kotlin API reference,
kotlinx.serialization, [s.d.]. Disponível em:
https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-primitive/is-string.html.
Acesso em: 14 ago. 2026.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. Entrada nova em "Armadilhas" (append, sem
reescrever) também conta como mudança de conteúdo real. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial. | Criação inicial |
| 0.2.0 | 14-08-2026 | Acrescentada a armadilha do suporte embutido a Kotlin no AGP 9, que quebra a build se o plugin `org.jetbrains.kotlin.android` continuar aplicado. | Achado durante a compilação do esqueleto mínimo do módulo `app` |
| 0.3.0 | 14-08-2026 | Acrescentada a armadilha da versão de Kotlin do `core` precisar bater com o Kotlin embutido no AGP. | Achado durante a compilação do esqueleto mínimo do módulo `app` |
| 0.4.0 | 14-08-2026 | Acrescentada a armadilha de `booleanOrNull`/`intOrNull` do kotlinx.serialization não checarem `isString`. | Achado durante a escrita do pacote `content` |
| 0.5.0 | 15-08-2026 | Acrescentada a armadilha de `@Suppress` não valer em cima de uma instrução solta, só de uma declaração. | Achado durante a escrita do lado `app` do pacote `connectivity` |
| 0.6.0 | 15-08-2026 | Acrescentada a armadilha de `sdk.dir` em `local.properties` precisar de barra invertida duplicada no Windows. | Achado ao configurar `local.properties` numa worktree nova, pela segunda vez nesta tarefa |
| 0.7.0 | 16-08-2026 | Acrescentadas duas armadilhas do Robolectric: `NfcAdapter.getDefaultAdapter()` volta nulo sem declarar a característica de hardware NFC no `PackageManager` simulado; `ShadowNfcAdapter.createMockTag()` não aceita o identificador da etiqueta como parâmetro. | Achado ao escrever e rodar de verdade o teste de `MainActivity.kt` (leitura NFC) |
| 0.8.0 | 17-08-2026 | Acrescentada a armadilha de `ScanRecord.parseFromBytes` ser método oculto do SDK público do Android. | Achado ao escrever e rodar de verdade o teste de `BleAccessoryService.kt` (Bluetooth) |
| 0.9.0 | 01-09-2026 | Acrescentada a armadilha da barra de título nativa do Android aparecer por cima das telas em Compose sem um tema XML `NoActionBar`. | Achado por captura de tela ao vivo, ao confirmar visualmente o encadeamento real das telas |
