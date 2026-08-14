# 0006 — Localização do projeto Gradle no repositório

Resumo em linguagem simples: o projeto de código do aplicativo
(arquivos do Gradle, o módulo `core`, e futuramente o módulo `app`)
mora dentro de `modulos/motor/`, junto com a documentação do módulo —
não na raiz do repositório, separado dela.

**Status:** aceito

**Contexto:** [decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)
já tinha fixado a estrutura interna do projeto Android (dois módulos
Gradle, `core` e `app`, com pacotes organizados por assunto), mas
nunca decidiu em que pasta do repositório esse projeto Gradle deveria
existir de fato — a árvore de pastas mostrada ali (`core/`, `app/`)
nunca vinha com um caminho completo na frente. Como o pacote `search`
é o primeiro código do módulo `core` a ser escrito, e não existia
ainda nenhum arquivo de projeto (`settings.gradle.kts`) em lugar
nenhum do repositório, essa escolha precisava ser feita agora.

Duas alternativas reais foram consideradas:

(a) Projeto Gradle na raiz do repositório (`settings.gradle.kts`,
`core/`, `gradle/` direto em `NEXO-EMBRIOLOGIA/`) — o padrão mais
comum de projeto Android isolado, e o que ferramentas como o Android
Studio esperam abrir sem indicar uma subpasta específica.

(b) Projeto Gradle dentro de `modulos/motor/` (`modulos/motor/settings.gradle.kts`,
`modulos/motor/core/`, `modulos/motor/gradle/`) — mantém o código
junto com o resto do que já existe sobre o módulo motor
(`concept.md`, `architecture.md`, `decisions/`, `schemas/`), em vez de
separado dele.

Nenhum problema técnico real foi encontrado contra a alternativa (b):
Android Studio abre normalmente uma subpasta como raiz de projeto (só
exige apontar pra ela, em vez de pra raiz do repositório); um sistema
de integração contínua só precisa rodar o comando de dentro dela;
nada na estrutura de `git worktree` já usada neste projeto depende de
onde, dentro do repositório, um projeto Gradle específico mora — a
worktree sempre traz o repositório inteiro, não importa a
profundidade. Também existe precedente de mercado para projeto Android
vários níveis abaixo da raiz de um repositório que reúne mais coisas
além dele (documentação, outros componentes).

Uma tentativa inicial de justificar a alternativa (a) citando o
precedente de `docs/docs-VMODEL-visao-geral/` (que mora na raiz do
repositório, fora de `modulos/`, apesar de ser inteiramente sobre o
motor) não se sustentou: essa cascata parece existir fora de
`modulos/` porque é anterior à própria estrutura de módulos deste
projeto (o `CLAUDE.md` manda lê-la antes até de `modulos/README.md`),
não porque exista uma regra geral de "todo produto final mora fora de
`modulos/`, só documentação de processo mora dentro". Essa analogia foi
descartada como argumento.

**Decisão:** alternativa (b). O projeto Gradle mora dentro de
`modulos/motor/`:

```
modulos/motor/
  settings.gradle.kts
  gradle/                (wrapper e catálogo de versões)
  gradlew, gradlew.bat
  core/
    build.gradle.kts
    src/main/kotlin/org/nexo/motor/core/...
    src/test/kotlin/org/nexo/motor/core/...
```

Critério de desempate: consistência com o resto do projeto, que já
organiza tudo relativo a um módulo (`concept.md`, `architecture.md`,
`decisions/`, `schemas/`) dentro de `modulos/<nome>/` — o código passa
a seguir a mesma lógica, em vez de abrir uma exceção só pra ele. Quem
quiser entender ou mexer no módulo motor inteiro, documentação e
código, olha um lugar só.

**Consequências:** ao abrir o projeto numa ferramenta como o Android
Studio, é preciso apontar pra `modulos/motor/`, não pra raiz do
repositório — custo aceito, uma navegação a mais, sem efeito colateral
técnico. O firmware do acessório leitor (C++/Arduino/PlatformIO, ver
[decisions/0002](0002-framework-do-firmware-do-acessorio.md)), quando
escrito, segue a mesma lógica — mora dentro de `modulos/motor/`
também, não na raiz. O `.gitignore` da raiz do repositório ganhou
entradas pra artefatos de build do Gradle (`.gradle/`, `build/`,
`local.properties`, `.idea/`, `*.iml`), únicas até agora porque este é
o primeiro código real do projeto inteiro.
