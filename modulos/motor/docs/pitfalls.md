# Pitfalls — Motor

<!-- module-doc-type: pitfalls -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Pitfalls |
| Versão | 0.4.0 |
| Data | 14-08-2026 |
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
