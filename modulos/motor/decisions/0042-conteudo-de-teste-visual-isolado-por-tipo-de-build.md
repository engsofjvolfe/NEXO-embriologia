# 0042 — Conteúdo de teste visual isolado por tipo de build (`src/debug`)

Resumo em linguagem simples: pra testar visualmente o encadeamento real das telas
(`app/ui/MotorApp.kt`, decisions/0040) antes de existir um jeito real de carregar conteúdo
importado, era preciso mostrar algum tema/evento de exemplo nas telas. A primeira tentativa
colocou esse exemplo direto dentro do código principal do aplicativo, atrás de uma checagem que
liga só em modo de depuração — mas o texto de exemplo continuava morando dentro do "motor" de
verdade, o que o motor nunca deveria carregar (RNF-MOD-01: o motor não conhece nenhum assunto
específico). Esta ADR decide um jeito estrutural, não só uma checagem: o conteúdo de exemplo mora
numa pasta própria do projeto Android (`src/debug/`), que o próprio sistema de montagem do Android
nunca inclui numa versão de verdade do aplicativo — não é uma checagem que liga ou desliga, é uma
pasta inteira que simplesmente não entra na build de produção.

**Status:** aceito

**Contexto:**

Mesmo princípio já formalizado, pra outro assunto, em
[decisions/0029](0029-aparencia-visual-das-telas-mora-no-motor.md) (RNF-MOD-01: o motor não conhece
nenhum assunto específico) — lá, o precedente decide de quem é a responsabilidade pela aparência
visual; aqui, o mesmo requisito volta a valer pra decidir onde conteúdo de exemplo pode morar
fisicamente dentro do projeto.

`MotorApp.kt` (decisions/0040) precisa de dado pra preencher a tela de Navegação, a de
Configuração da sessão e a inicial da tela de jogo — sem esse dado, essas telas não têm nada pra
mostrar, porque o aplicativo ainda não tem nenhum jeito real de carregar o conteúdo que uma pessoa
importou (pendência própria, já registrada, "Decidir onde o conteúdo importado fica guardado no
aparelho..."). Testar visualmente o encadeamento das telas, hoje, exige algum dado de exemplo —
mas esse dado nunca pode ser confundido com conteúdo real, nem pode aparecer numa instalação de
verdade do aplicativo.

Três alternativas reais, comparadas:

1. **Checagem em tempo de execução dentro do código principal** (`if (BuildConfig.DEBUG) ... else
   emptyList()`, direto em `app/ui/`) — primeira tentativa, revertida por esta ADR. Funciona (uma
   instalação de produção nunca mostra o dado de exemplo, porque `BuildConfig.DEBUG` é sempre
   falso nela), mas o texto de exemplo ("Tema A", "Evento 1"...) continua fisicamente dentro do
   código-fonte principal do aplicativo (`src/main/`) — a mesma pasta que carrega a lógica de
   verdade do motor. Guardar aparência ("o dado nunca aparece na tela") sem guardar
   substância ("o dado não deveria nem existir ali") não resolve o problema de fundo.
2. **Pasta própria por tipo de build** (`src/debug/`, escolhida — ver Decisão) — mecanismo do
   próprio sistema de montagem do Android (Gradle/AGP): cada tipo de build (`debug`, `release`)
   pode ter sua própria versão de um arquivo, e o `src/main/` nunca declara essa peça, só chama
   ela pelo nome — cada build real (debug ou release) enxerga só a versão que pertence a ela,
   nunca as duas ao mesmo tempo (GOOGLE, [s.d.]). Único cuidado, confirmado na mesma fonte: o
   arquivo não pode existir em `src/main/` *e* em `src/debug/` ao mesmo tempo — duplicidade de
   declaração é erro de build, não escolha de qual delas "vence".
3. **Variante de build por sabor de produto** (*product flavor* — ex.: um sabor "demo" e um sabor
   "real", cada um com sua pasta própria) — mesmo mecanismo estrutural da opção 2, mas usando a
   dimensão certa pra "conteúdo diferente" em vez de reaproveitar a dimensão de tipo de build
   (`debug`/`release`, pensada pra empacotamento e depuração, não pra conteúdo) (GOOGLE, [s.d.]).
   Tecnicamente mais correta, mas introduz uma dimensão nova de variante pra todo o módulo `app`
   (nomes de tarefa do Gradle mudam — `:app:testDemoDebugUnitTest` em vez de
   `:app:testDebugUnitTest`, por exemplo), efeito colateral maior do que o necessário pra um
   conteúdo que é temporário por natureza (apaga-se quando o carregamento real de conteúdo
   existir). Descartada por esse motivo — revisável se, no futuro, o projeto já tiver outra razão
   real pra separar variantes por sabor de produto.

**Decisão:**

1. O conteúdo de exemplo mora em `app/src/debug/kotlin/org/nexo/motor/app/ui/ConteudoInicial.kt`
   — nunca em `src/main/`. Cinco funções, cada uma devolvendo o dado que uma parte de `MotorApp.kt`
   precisa: lista de entradas de navegação, lista de configuração por evento, lista de posições de
   início disponíveis, estado inicial da tela de jogo, estado de resumo da tela de jogo.
2. `app/src/release/kotlin/org/nexo/motor/app/ui/ConteudoInicial.kt` declara as mesmas cinco
   funções, cada uma devolvendo o valor vazio/neutro — build de produção sempre honesta: sem
   conteúdo real carregado, a tela de Navegação vem vazia de verdade, não com um exemplo escondido.
3. `app/ui/MotorApp.kt` (em `src/main/`) chama essas cinco funções pelo nome, sem declarar nenhuma
   delas — não sabe, e não precisa saber, se está rodando a versão de depuração ou de produção;
   não importa nada de conteúdo específico, preservando RNF-MOD-01 também dentro do próprio
   código, não só no comportamento observável.
4. Conteúdo de exemplo, em si, generoso o bastante pra exercitar a tela de verdade: duas
   instâncias de tema ("Tema A" com dois eventos de tamanho diferente, "Tema B" com um evento),
   um evento com dica habilitada e outro sem, disponibilidade de pular variando entre eventos, e
   um evento com mais de uma posição de início disponível (pra exercitar a escolha "Começar em",
   `EI-SES-02`, que um evento de posição única não mostra). Nomes genéricos (nunca um assunto
   real), no mesmo padrão já usado no protótipo navegável (`design/prototipo-navegavel.js`).

**Consequências:**

`src/main/` fica livre de qualquer literal de conteúdo de exemplo. Testes de unidade
(`MotorAppTest.kt`, rodando contra a variante de depuração) continuam vendo o conteúdo rico de
`src/debug/`, sem precisar importar nada dele diretamente — só verificam o texto que já sabem que
vai aparecer. Uma build de produção (`:app:assembleRelease`, hoje nunca executada neste projeto)
passa a exigir a versão de `src/release/` pra compilar — sem ela, a build falharia por não achar
as cinco funções; por isso essa pasta nasce junto com a de depuração, nesta mesma ADR, mesmo sem
uso imediato. Revisável por completo quando a pendência de carregar conteúdo real for resolvida:
nesse momento, a versão de produção passa a ler o conteúdo de verdade (deixa de ser vazia), e a
pasta `src/debug/` pode ser simplificada ou apagada, conforme fizer sentido então.

## Referências

Fonte externa consultada para embasar esta decisão, no formato definido pela norma ABNT NBR 6023
(Informação e documentação — Referências). Citada no corpo do documento como (GOOGLE, ano).

GOOGLE. **Configure build variants**. Android Developers, [s.d.]. Disponível em:
https://developer.android.com/build/build-variants. Acesso em: 02 set. 2026.
