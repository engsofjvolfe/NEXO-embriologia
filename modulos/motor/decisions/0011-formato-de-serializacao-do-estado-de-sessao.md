# 0011 — Formato de serialização do estado de sessão persistido

Resumo em linguagem simples: pra gravar o estado de uma sessão pausada
num arquivo de verdade (decisions/0010), era preciso escolher um
formato — a ADR anterior deixou esse ponto em aberto, de propósito,
pra decidir na hora de implementar. A escolha é JSON, usando a
biblioteca oficial de serialização do Kotlin (`kotlinx.serialization`)
— mesmo formato que o projeto já usa no único outro lugar onde grava
dado estruturado em arquivo (o pacote de conteúdo), e mesma lógica já
usada antes pra escolher ferramenta de teste: preferir a solução
oficial da linguagem, não uma de terceiro.

Convenção dos códigos citados abaixo:
- `DA-IMP` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.4.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. O trecho abaixo marcado
descreve documentação oficial de um terceiro (Kotlin/JetBrains) — não
uma decisão deste projeto, e sujeito a mudar em qualquer lançamento
novo dessa biblioteca. Quem ler este documento depois deve tratar
esse conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra atualizar
`build.gradle.kts`.

**Status:** aceito

**Contexto:** [decisions/0010](0010-persistencia-do-estado-de-sessao-pausada.md)
já decidiu que `session` grava e lê o estado da sessão pausada usando
`java.io.File`, mas deixou o formato exato do conteúdo desse arquivo
em aberto, de propósito, pra quando a implementação acontecesse — o
mesmo já valia pro formato do registro interno da sessão
([decisions/0008](0008-representacao-do-estado-da-sessao.md)). Chegado
esse momento, duas alternativas reais foram consideradas:

1. Um formato de texto próprio, inventado pra este projeto, sem
   biblioteca nenhuma — evita adicionar dependência nova ao módulo
   `core`.
2. JSON, usando `kotlinx.serialization` — biblioteca de serialização
   mantida pela própria JetBrains (dona do Kotlin), não um pacote de
   terceiro.

DA-IMP-06 já fixa, na cascata aprovada, que "o motor prefere, em cada
camada onde há escolha, tecnologia e formato aberto e sem custo de
licença **(JSON**, ZIP, Bluetooth, NFC padrão)" — JSON já está citado,
nominalmente, como exemplo do que o projeto prefere. É também o
formato já escolhido, em PD-IMP-01, pro único outro lugar onde este
projeto grava dado estruturado em arquivo (o pacote de conteúdo).

Um formato inventado na hora, apesar de evitar uma dependência nova,
vai contra o espírito de DA-IMP-06: só o próprio código deste projeto
saberia ler esse arquivo — nenhuma ferramenta externa, nenhuma outra
pessoa, conseguiria inspecionar ou depurar o conteúdo sem reimplementar
o formato à mão. "Aberto" não é só "sem custo de licença" — é também
"documentado e lido por mais de uma ferramenta", e um formato
inventado na hora não atende isso.

`[REVISAR-EXTERNO]` `kotlinx.serialization` é confirmada, na própria
documentação oficial do Kotlin, como biblioteca mantida pela JetBrains
(todos os artefatos sob o grupo `org.jetbrains.kotlinx`), recomendada
como ponto de partida pra quem quer serializar dado em Kotlin
(JETBRAINS, [s.d.]a). O artefato JSON (`kotlinx-serialization-json`)
está em versão estável, não experimental (JETBRAINS, [s.d.]b). Versão
usada: 1.11.0, a mais recente estável na data de acesso — o plugin
Gradle que habilita a anotação `@Serializable`
(`org.jetbrains.kotlin.plugin.serialization`) precisa da mesma versão
já usada pelo Kotlin do projeto (2.4.10, ver
`gradle/libs.versions.toml`), não uma versão própria (JETBRAINS,
[s.d.]c).

**Decisão:** o estado de sessão pausada
([decisions/0008](0008-representacao-do-estado-da-sessao.md)) é
serializado em JSON, usando `kotlinx.serialization`
(`kotlinx-serialization-json` 1.11.0, plugin `plugin.serialization` na
mesma versão do Kotlin do projeto). Os tipos `SessionState` e
`SessionEvent` (`core/session/SessionState.kt`) recebem a anotação
`@Serializable`.

**Consequências:** o módulo `core` ganha sua primeira dependência de
biblioteca externa além das de teste — aceito, porque é biblioteca
oficial da linguagem, não de terceiro, mesma categoria de fonte já
usada em
[decisions/0005](0005-abordagem-de-teste-do-nucleo-do-motor.md) pra
justificar `kotlin-test`/JUnit. Nenhuma classe do Android entra em
`core` por causa disso — `kotlinx.serialization` roda em qualquer JVM,
sem depender de `Context` nem de nenhuma API do Android, mesma
consistência já mantida desde
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md). O
arquivo gravado em disco fica legível por qualquer ferramenta que leia
JSON — inclusive um editor de texto comum, útil pra depuração manual
durante o desenvolvimento.

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

JETBRAINS. **Serialization**. Kotlin Programming Language, [s.d.]a.
Disponível em: https://kotlinlang.org/docs/serialization.html. Acesso
em: 14 ago. 2026.

JETBRAINS. **Kotlin/kotlinx.serialization — Releases**. GitHub,
[s.d.]b. Disponível em:
https://github.com/Kotlin/kotlinx.serialization/releases. Acesso em:
14 ago. 2026.

JETBRAINS. **Get started with Kotlin serialization**. Kotlin
Programming Language, [s.d.]c. Disponível em:
https://kotlinlang.org/docs/serialization-get-started.html. Acesso em:
14 ago. 2026.
