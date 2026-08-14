# 0005 — Abordagem de teste do núcleo do motor

Resumo em linguagem simples: os pacotes dentro de `core` vão ser
testados com ferramentas comuns de teste do Kotlin, rodando direto na
máquina que compila o código — sem precisar de celular nem simulador
de celular. É a mesma combinação de ferramentas que a documentação
oficial do Kotlin recomenda hoje pra projeto novo.

Convenção do código citado abaixo:
- `PD-NAV` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.4.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado no [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
seção 6.3.3, e em [decisions/0004](0004-desenho-do-algoritmo-de-busca-aproximada.md).
Todo trecho abaixo marcado descreve a versão ou a recomendação atual
de uma ferramenta de terceiro (Kotlin, JUnit) — não uma decisão deste
projeto, e sujeito a mudar em qualquer lançamento novo dessas
ferramentas, inclusive depois desta ADR ser aceita. Quem ler este
documento depois deve tratar esse conteúdo como possivelmente
desatualizado e reconfirmar na fonte oficial (seção Referências) antes
de usar como base pra atualizar `build.gradle.kts` — uma nota de
acompanhamento datada, permitida pelo formato de ADR deste projeto
(ver [decisions/README.md](README.md)), é o jeito de registrar essa
reconfirmação sem reescrever a decisão original.

**Status:** aceito

**Contexto:**
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md) já
registrou que o módulo `core` não depende de nenhuma classe de
interface do Android, e que isso "torna esse módulo testável com
teste de unidade comum, sem precisar de aparelho ou emulador Android"
— mas não desceu ao nível de qual ferramenta de teste usar. Como o
pacote `search` (ver
[0004](0004-desenho-do-algoritmo-de-busca-aproximada.md)) é o primeiro
código do módulo `core` a ser escrito, e não existe ainda nenhum
arquivo de configuração de build (`settings.gradle.kts`,
`build.gradle.kts`) em nenhum lugar do repositório, essa escolha
precisa ser feita agora, pela primeira vez — ela vale pra todo pacote
futuro de `core`, não só pra `search`.

`[REVISAR-EXTERNO]` A documentação oficial do Kotlin registra a
configuração recomendada hoje pra um projeto novo:

- "Test Java code using Kotlin and JUnit — tutorial"
  (kotlinlang.org/docs/jvm-test-using-junit.html, revisada em 11 mai.
  2026): mostra a configuração completa de um módulo Kotlin/JVM
  testado com JUnit — plugin Gradle `kotlin("jvm")`, dependência
  `testImplementation(kotlin("test"))` mais
  `org.junit.jupiter:junit-jupiter-api`/`-params`, dependência
  `testRuntimeOnly` com `junit-jupiter-engine` e
  `junit-platform-launcher`, e `tasks.test { useJUnitPlatform() }` no
  `build.gradle.kts`. A versão de JUnit usada no exemplo é a 6 (JUnit
  Jupiter, artefatos com essa versão no catálogo de versões) — número
  de versão sujeito a mudar em qualquer lançamento novo do Kotlin ou
  do JUnit.
- Página de referência da API `kotlin-test`
  (kotlinlang.org/api/core/kotlin-test/): confirma que `kotlin-test`
  não substitui o JUnit — é uma camada de conveniência do Kotlin em
  cima dele (anotações e funções de asserção que funcionam
  independente do framework de teste por baixo).

**Decisão:** o módulo `core` usa:

- Plugin Gradle `kotlin("jvm")` (Kotlin puro, sem plugin Android —
  coerente com [decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)).
- `testImplementation(kotlin("test"))` +
  `org.junit.jupiter:junit-jupiter-api` e
  `org.junit.jupiter:junit-jupiter-params`; `testRuntimeOnly` com
  `org.junit.jupiter:junit-jupiter-engine` e
  `org.junit.platform:junit-platform-launcher`.
- `tasks.test { useJUnitPlatform() }` no `build.gradle.kts` do `core`.

Os testes do pacote `search` cobrem, no mínimo: os exemplos numéricos
que o próprio PD-NAV-02 já dá (termo de 4 a 5 letras tolera 1 erro,
termo de 10 a 14 tolera 2); e os três pontos fechados em
[0004](0004-desenho-do-algoritmo-de-busca-aproximada.md) (maiúscula e
acento não contam como diferença, empate preserva a ordem de entrada,
comparação inteira e por trecho com a inteira aparecendo primeiro).

**Consequências:** este é o primeiro arquivo de build de todo o
projeto — `settings.gradle.kts` na raiz e `core/build.gradle.kts`
nascem junto com o código do `search`, só com o módulo `core`
declarado (o módulo `app`, que depende de interface do Android, fica
pra quando alguém escrever a interface — mesma lógica já registrada em
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)). Testes
de unidade dos pacotes futuros de `core` (`hierarchy`, `session`,
`content`, `connectivity`, `report`) seguem essa mesma configuração de
build, sem precisar decidir de novo. Isso não resolve a pendência
maior "Escrever os testes de unidade, integração, sistema e
aceitação" registrada em
[tasks.md](<../docs/tasks.md#em-aberto>) — só estabelece a ferramenta
usada; a cobertura completa (integração entre pacotes, sistema,
aceitação) continua em aberto do jeito que já estava.

## Referências

JETBRAINS. **Test Java code using Kotlin and JUnit — tutorial**.
Kotlin Help, [s.d.]. Disponível em:
https://kotlinlang.org/docs/jvm-test-using-junit.html. Acesso em: 13
ago. 2026 (página com data de revisão de 11 mai. 2026).

JETBRAINS. **kotlin-test — Core API**. Kotlin Programming Language,
[s.d.]. Disponível em: https://kotlinlang.org/api/core/kotlin-test/.
Acesso em: 13 ago. 2026.
