# 0033 — Formato de aparelho (leiaute responsivo) para as telas do motor

Resumo em linguagem simples: antes de desenhar onde cada botão, texto e
campo vai em cada tela do motor, faltava decidir se esse desenho parte
só do celular, ou também leva o tablet em conta — e, se levar, quando
uma tela precisa de fato de um leiaute diferente no tablet, em vez de
só esticar o mesmo desenho. Esta ADR fixa: celular primeiro, sempre
como a versão completa de referência; tablet só ganha leiaute
realmente diferente onde existe necessidade concreta de mostrar duas
informações relacionadas ao mesmo tempo — hoje, isso acontece em uma
única tela.

Convenção dos códigos citados abaixo:
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.
- `EI-NAV` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.15.

**Status:** aceito

**Contexto:**

Nenhum documento da cascata do motor decide se o desenho das telas
considera só o celular, ou também o tablet — o Projeto Arquitetônico
fixa apenas o conteúdo funcional de cada tela (`DA-RET`), nunca
aparência ou leiaute (ver
[architecture.md, Interface](<../docs/architecture.md#interface>)).
Existe um rascunho de design fora deste repositório, numa pasta de
trabalho separada e não versionada (`Design/`), que já assumiu uma
resposta própria (celular retrato como referência; tablet paisagem com
duas colunas reais só em duas telas específicas) — mas essa resposta
nunca passou pelo processo de decisão deste projeto (pesquisa em fonte
oficial, alternativas reais, registro em ADR).

Duas fontes oficiais foram consultadas antes de decidir:

1. A documentação oficial do Android sobre classes de tamanho de
   janela (GOOGLE, [s.d.]a) confirma faixas de largura de tela
   reconhecidas oficialmente: compacta (largura menor que 600dp — `dp`
   é a unidade de medida de tela do Android, que não muda com a
   densidade de pixel do aparelho), média (600 a 839dp) e expandida
   (840dp ou mais); 99,96% dos celulares em retrato caem na faixa
   compacta. A mesma fonte diz: "a largura disponível costuma importar
   mais que a altura disponível... a classe de tamanho de largura é
   provavelmente mais relevante para a interface do seu aplicativo" e
   "a maioria dos aplicativos consegue construir uma interface
   adaptável considerando só a classe de tamanho de largura".
2. A documentação oficial de leiautes prontos e já testados pelo
   próprio Android (GOOGLE, [s.d.]b) descreve três padrões
   reconhecidos: **lista-detalhe** (duas áreas lado a lado — lista de
   itens e detalhe do item escolhido —, usadas juntas só em tela
   expandida; em tela compacta ou média, mostra uma ou outra, nunca as
   duas ao mesmo tempo; o conteúdo do detalhe "é significativo mesmo
   sem o conteúdo principal"), **feed** (grade que se ajusta de uma
   coluna a várias) e **painel de apoio** (conteúdo principal ocupando
   a maior parte da tela, mais um painel secundário de apoio — "o
   conteúdo do painel secundário só é significativo em relação ao
   conteúdo principal" —, dividido meio a meio em tela média, 70/30 em
   tela expandida).

A mesma fonte (GOOGLE, [s.d.]a) também orienta que, ao adaptar um
aplicativo que já tem leiaute pronto pro celular, o próximo leiaute a
desenhar é o da tela expandida, não o da tela média ("se você tem um
leiaute existente pra telas compactas, primeiro otimize seu leiaute
para a classe de tamanho expandida... depois decida qual leiaute faz
sentido para a classe de tamanho média"). Essa orientação parte do
princípio de que a base do celular já existe — não é uma recomendação
sobre qual aparelho vem primeiro na estratégia de desenho, só da ordem
de desenhar as versões extras depois que a base já existe. Não
conflita com "celular primeiro" como estratégia geral, e não se aplica
da mesma forma aqui: o motor não tem nenhuma tela pronta ainda, é
desenho do zero, não adaptação de algo existente.

Ponto que já está fechado por decisão anterior e não pode ser reaberto
aqui: a tela de Navegação usa expansão em acordeão, "igual em celular
e em tablet — a decisão não depende do tamanho da tela"
([decisions/0030](0030-padrao-de-navegacao-hierarquica-de-conteudo.md),
decisão 2). Um leiaute de duas colunas de verdade pra essa tela
específica, se vier a existir, precisa conviver com o acordeão, nunca
substituí-lo — fica fora do escopo desta ADR, a resolver (se for o
caso) na etapa de esqueleto de tela (`tasks.md`, "Desenhar o esqueleto
das 16 entradas de tela restantes").

**Decisão:**

1. **Celular primeiro.** O esqueleto de cada uma das 17 entradas de
   tela do motor é desenhado primeiro para a tela compacta (celular,
   retrato) — essa é sempre a versão completa e de referência. O
   tablet é tratado como uma extensão desse desenho, nunca o ponto de
   partida.
2. As faixas de largura usadas para decidir quando uma tela muda de
   verdade no tablet são as oficiais do Android: compacta (<600dp),
   média (600-839dp) e expandida (≥840dp).
3. Uma tela só ganha leiaute diferente no tablet quando existe
   necessidade real de mostrar duas informações relacionadas ao mesmo
   tempo — nunca por padrão, nunca só porque sobra espaço. Das telas
   do motor, hoje existe necessidade real reconhecida em uma:
   **Configuração da sessão** (lista de eventos dentro do alcance da
   sessão + formulário de configuração do evento escolhido) — encaixa
   nos padrões oficiais "lista-detalhe" ou "painel de apoio" (ambos
   prevêem lista + detalhe lado a lado em tela média/expandida), com o
   formulário do evento selecionado como painel secundário. A
   proporção exata de colunas, e qual dos dois padrões se aplica
   melhor, fica para a etapa de esqueleto de tela — aqui só se decide
   que essa tela tem necessidade real reconhecida.
4. A tela de Navegação **não** ganha esse tratamento nesta ADR —
   `decisions/0030` já fixa que ela é igual em qualquer tamanho de
   tela (acordeão sempre, ver Contexto). Um leiaute de duas colunas
   para Navegação, se algum dia existir, precisa ser compatível com o
   acordeão, não substituí-lo; decisão fora do escopo daqui.
5. As outras 15 entradas de tela (as 10 variações da tela de jogo, mais
   Sessão pausada, Ponto de início — já a mesma tela que Configuração
   da sessão, `EI-NAV-05` —, Resultado/relatório, Importar conteúdo,
   Consentimento) não recebem leiaute dedicado de tablet nesta rodada:
   no tablet, mostram o mesmo conteúdo do celular, centralizado numa
   coluna de leitura confortável, com margens — sem informação nova,
   sem leiaute diferente. Revisável depois, sem quebrar nada, se
   aparecer necessidade real (mesmo critério do item 3).
6. O rascunho não canônico da pasta `Design/` propôs duas colunas reais
   em Navegação e em Configuração da sessão — só a segunda parte é
   reaproveitável; a de Navegação contraria o item 4.

**Consequências:**

Desbloqueia a pendência "Desenhar o esqueleto das 16 entradas de tela
restantes" em `tasks.md` — agora existe um critério objetivo (as
faixas oficiais de largura, mais o critério de necessidade real do
item 3) para decidir, tela por tela, se ela precisa de leiaute de
tablet dedicado.

Nenhuma mudança em código já escrito — nenhuma tela existe ainda.

Fica para a etapa de esqueleto de tela, não desta ADR: a proporção
exata de colunas em Configuração da sessão, e se ela usa o padrão
"lista-detalhe" ou "painel de apoio". Segundo a distinção da própria
fonte oficial, o formulário de configuração de um evento só faz
sentido junto da lista de eventos que ele está configurando — perfil
mais próximo de "painel de apoio" que de "lista-detalhe" — mas a
escolha final fica para quando essa tela for desenhada de verdade, não
presumida aqui.

**Nota de acompanhamento (02-09-2026):**

*Resumo simples:* a pasta `Design/` citada no Contexto e no item 6, acima, nunca foi versionada
neste repositório e continua sem existir nele — quem ler esta ADR agora não consegue conferir o
que ela continha. A citação nunca serviu de prova da decisão em si (que se apoia só nas duas
fontes oficiais da seção Referências); serve só de registro de onde a ideia original das duas
colunas veio. Ainda assim, por este projeto exigir que toda fonte citada seja conferível, essa
referência específica fica, a partir de agora, marcada como não conferível — não uma correção da
decisão, só um esclarecimento sobre os limites dessa citação.

## Referências

Fontes externas consultadas para embasar esta decisão, no formato
definido pela norma ABNT NBR 6023 (Informação e documentação —
Referências). Citações traduzidas livremente no corpo do documento;
texto original preservado entre aspas antes da tradução quando citado
diretamente. Citadas no corpo do documento como (GOOGLE, ano).

GOOGLE. **Use window size classes**. Android Developers, [s.d.]a.
Disponível em: https://developer.android.com/develop/adaptive-apps/guides/use-window-size-classes.
Acesso em: 30 ago. 2026.

GOOGLE. **Canonical layouts**. Android Developers, [s.d.]b. Disponível
em: https://developer.android.com/develop/adaptive-apps/guides/canonical-layouts.
Acesso em: 30 ago. 2026.
