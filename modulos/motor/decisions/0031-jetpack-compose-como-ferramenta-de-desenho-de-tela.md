# 0031 — Jetpack Compose como ferramenta de desenho de tela do módulo `app`

Resumo em linguagem simples: as telas do aplicativo (o que a pessoa vê
e toca no aparelho) vão ser construídas com Jetpack Compose — a forma
mais nova de desenhar tela no Android, hoje recomendada pelo próprio
Google pra qualquer aplicativo novo — em vez do sistema mais antigo,
baseado em Views (arquivo XML de layout + `findViewById`).

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado em [decisions/0001](<0001-linguagem-do-aplicativo.md>) e no
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
seção 6.3.3. Todo trecho abaixo marcado descreve a posição atual de um
terceiro (Google) — não uma decisão deste projeto, e sujeita a mudar
sem aviso. Quem ler este documento depois deve tratar esse conteúdo
como possivelmente desatualizado e reconfirmar na fonte oficial (seção
Referências) antes de usar como base pra qualquer decisão nova.

**Status:** aceito

**Contexto:** nenhum documento da cascata
([`docs/docs-VMODEL-visao-geral/`](<../../../docs/docs-VMODEL-visao-geral/>))
decide qual mecanismo de desenho de tela o aplicativo usa — o Projeto
Arquitetônico e o Projeto Detalhado fixam só o *conteúdo funcional* de
cada tela (o que ela mostra, ver Projeto Arquitetônico, seção 6.6), de
propósito nunca a aparência nem a tecnologia usada pra desenhar essa
aparência (mesma exclusão de escopo que já vale pra cor, fonte e
layout — ver
[`architecture.md`, Interface](<../docs/architecture.md#interface>)).
Faltava, por isso, uma escolha do mesmo tipo que a linguagem de
programação já teve
([decisions/0001](<0001-linguagem-do-aplicativo.md>)): entre as duas
formas usuais de construir interface num aplicativo Android — Jetpack
Compose e o sistema de Views tradicional —, nenhuma estava decidida em
nenhum documento.

O módulo `app` hoje não tem nenhuma tela de verdade — só o esqueleto
mínimo (`MainActivity` sem `setContentView`, ver
[`architecture.md`, Esqueleto mínimo e versões de
build](<../docs/architecture.md#esqueleto-mínimo-e-versões-de-build>))
—, então esta escolha não herda nenhum custo de migração: não existe
código de tela já escrito em nenhuma das duas tecnologias pra
converter.

**Decisão:** Jetpack Compose.

Motivo, com fonte oficial (Google/Android Developers) pra cada ponto:

- `[REVISAR-EXTERNO]` Desde 19 de maio de 2026, o próprio Google
  declara publicamente que todo desenvolvimento de UI no Android
  deveria passar a ser feito com Compose — direção chamada de "Compose
  First" (nome próprio da iniciativa, mantido em inglês pela mesma
  razão que "Jetpack Compose" não é traduzido): toda API, biblioteca,
  ferramenta e orientação nova do Android passa a ser feita primeiro
  (ou só) pra Compose (BUTCHER, 2026).
- `[REVISAR-EXTERNO]` O mesmo anúncio classifica o sistema de Views
  (os componentes de `android.widget`, e bibliotecas como Fragments,
  RecyclerView, ViewPager) como em modo de manutenção: sem planos de
  remoção, mas só recebendo correção crítica de erro, nenhum recurso
  novo. As ferramentas novas do Android Studio também passam a ser
  construídas só pra Compose; as ferramentas baseadas em Views
  (Navigation Editor, Layout Editor) entram no mesmo modo de
  manutenção (BUTCHER, 2026).
- O `ViewModel` do módulo `app` já expõe o estado da tela como um
  único `StateFlow<SessionUiState>`
  ([decisions/0020](<0020-ligacao-entre-leitura-de-peca-e-a-tela.md>),
  [decisions/0022](<0022-conteudo-do-estado-exposto-pelo-viewmodel.md>)
  — decisões já tomadas antes desta, sem relação com ela). A
  documentação oficial do Android descreve `collectAsStateWithLifecycle()`
  como a forma recomendada de coletar `Flow` em aplicativos Android,
  justamente a partir de um composable que observa o `StateFlow` de um
  `ViewModel` — encaixe direto com o que já está construído, sem
  exigir retrabalho em `core` nem no `ViewModel` já escrito (GOOGLE,
  [s.d.]a).
- Em desempenho, a documentação oficial reporta paridade entre Compose
  e Views desde a versão 1.9.0 do Compose — mesma taxa de travamento
  (*jank*, o engasgo perceptível numa rolagem de tela) —, medida por
  *hero benchmarks* (cenários reais, como abrir o aplicativo a frio ou
  rolar uma lista) — não há, hoje, desvantagem de desempenho
  documentada que justifique preferir Views (GOOGLE, [s.d.]b).
- A exigência mínima de versão do Android pra usar Compose é API 21
  (GOOGLE, [s.d.]c) — dentro do `minSdk` 24 já decidido pro módulo
  `app`
  ([decisions/0012](<0012-versoes-de-plataforma-e-build-do-modulo-app.md>)),
  sem conflito.

**Consequências:** esta decisão fixa só a tecnologia de renderização —
não decide cor, fonte, layout, nem como as 17 entradas de tela do
Projeto Arquitetônico (seção 6.6) se agrupam em telas físicas; esse
desenho continua pendente, ver [`tasks.md`](<../docs/tasks.md>),
"Desenhar a aparência visual das telas do motor". Nenhum código já
escrito em `core` ou `app` precisa mudar — nenhum dos dois desenha
tela hoje. Existe interoperabilidade oficial entre Compose e Views
(`views-in-compose`/`compose-in-views`), mas ela não é exigida aqui,
já que não há tela em nenhuma das duas tecnologias pra interoperar.

## Referências

BUTCHER, Nick. **Android UI development is Compose First**. Android
Developers Blog, 19 maio 2026. Disponível em:
https://developer.android.com/blog/posts/android-ui-development-is-compose-first.
Acesso em: 22 ago. 2026.

GOOGLE. **State and Jetpack Compose**. Android Developers, [s.d.]a.
Disponível em: https://developer.android.com/develop/ui/compose/state.
Acesso em: 22 ago. 2026.

GOOGLE. **Jetpack Compose Performance**. Android Developers, [s.d.]b.
Disponível em: https://developer.android.com/develop/ui/compose/performance.
Acesso em: 22 ago. 2026.

GOOGLE. **Quick start with Jetpack Compose**. Android Developers,
[s.d.]c. Disponível em:
https://developer.android.com/develop/ui/compose/setup. Acesso em: 22
ago. 2026.
