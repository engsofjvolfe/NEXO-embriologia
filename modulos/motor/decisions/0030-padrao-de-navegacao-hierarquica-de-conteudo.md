# 0030 — Padrão de navegação hierárquica de conteúdo

Resumo em linguagem simples: a tela de escolher instância, tema e evento
(`DA-RET-02`) precisa decidir como a pessoa passa de um nível pro outro —
cada nível troca a tela inteira, ou a lista de baixo abre encaixada no
item escolhido, sem esconder o nível de cima? Esta ADR decide: abre
encaixada (acordeão), nunca troca de tela inteira, em celular ou tablet.

Convenção dos códigos citados abaixo:
- `EI-NAV`, `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seções 6.15 e 6.10.
- `DA-RET`, `DA-NAV` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seções 6.6 e 6.8.
- `PD-NAV` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.4.

**Status:** proposto

**Contexto:**

`DA-RET-02` já fixa o conteúdo da tela de navegação ("lista de instâncias,
depois temas, depois eventos, respeitando a hierarquia; com busca e
ordenação"), mas não decide a forma espacial dessa transição entre
níveis — isso é layout (passo 2 do método já registrado em
`architecture.md#interface`), não conteúdo funcional, e por isso fica de
fora do Projeto Arquitetônico de propósito. `tasks.md` já registra essa
lacuna como parte da pendência "Desenhar a aparência visual das telas do
motor", ainda sem resposta.

Duas alternativas reais consideradas:

1. **Troca de tela inteira a cada nível** (a lista de instâncias some,
   dá lugar à lista de temas daquela instância; escolher um tema faz o
   mesmo com a lista de eventos). Simples de implementar, mas esconde o
   nível anterior assim que a pessoa desce — pra reconsiderar outro
   tema da mesma instância, precisa voltar antes de poder escolher de
   novo.
2. **Acordeão — expandir em vez de trocar de tela.** Tocar numa
   instância abre a lista de temas dela logo abaixo, sem esconder as
   outras instâncias da lista; tocar num tema abre a lista de eventos
   dele do mesmo jeito. Fechar um item recolhe de volta.

A escolha pela alternativa 2 vem de duas exigências que a Especificação
já fixa:

- `EI-NAV-03` liga uma escolha adicional (até onde a sessão vai) ao
  item específico que a pessoa acabou de tocar, dentro de um grupo "com
  ordem". Essa escolha faz mais sentido presa visualmente ao item que a
  originou — o que só acontece se aquele item continuar na tela depois
  de tocado. Trocando a tela inteira, o item que gerou a pergunta some
  do lugar onde apareceu, e a escolha de alcance perde a referência
  visual direta a ele.
- `DA-NAV-02`/`DA-NAV-03` descrevem uma busca única, por texto livre,
  sobre "essa lista" (singular), sem dizer se ela alcança um nome ainda
  fechado (não expandido) ou só o que já está visível na tela.
  Restringir a busca ao que já está expandido esvaziaria o motivo dela
  existir: quem já sabe abrir a instância e o tema certos pra achar um
  evento não precisava de busca nenhuma pra isso — o caso que a busca
  aproximada resolve (`PD-NAV-01`, `PD-NAV-02`) é justamente achar algo
  sem já saber o caminho até ele. Por isso a busca precisa alcançar
  todo nome cadastrado — instância, tema, evento — esteja ou não
  expandido no momento. Com troca de tela inteira por nível, a busca
  precisaria ser reinventada por nível (buscar só dentro da tela atual)
  ou virar um mecanismo à parte que pula de tela em tela sozinho — os
  dois complicam algo que hoje é simples. Com acordeão, a estrutura
  inteira (expandida ou não) é filtrável de uma vez, do jeito que o
  pacote `search` já espera (`approximateSearch<T>` opera sobre
  qualquer lista de itens, sem se importar se cada um está visível na
  tela ou não).

**Decisão:**

1. A navegação entre instância, tema e evento usa expansão em
   acordeão — nunca troca de tela inteira. Tocar num item expande,
   embaixo dele, a lista do nível seguinte; tocar de novo recolhe.
2. Vale igual em celular e em tablet — a decisão não depende do
   tamanho da tela, só de como a hierarquia é apresentada.
3. A escolha de alcance da sessão (`EI-NAV-03`, `EI-SES-06` a
   `EI-SES-08`) aparece encaixada junto do próprio tema ou evento
   tocado como ponto de entrada da sessão — nunca numa tela ou painel
   separado.
4. A busca (`DA-NAV-02`, `DA-NAV-03`) roda sobre a hierarquia inteira —
   todo nome de instância, tema e evento cadastrado, esteja ou não
   expandido no momento — nunca só sobre o que já está visível na
   tela. Ao digitar um termo, a estrutura se rearranja: cada item que
   bate (ou é ancestral de um item que bate) abre sozinho, revelando o
   caminho completo até o resultado; o resto recolhe ou some. A pessoa
   nunca precisa abrir manualmente um nível antes de poder buscar
   dentro dele.
5. Fora desta decisão: cor, espaçamento, ícone de "aberto"/"fechado" de
   cada item — isso é aplicação do sistema visual (passo 3 do método),
   não deste documento.
6. O acordeão renderiza de forma preguiçosa (lazy) — nunca desenha de
   uma vez a estrutura inteira já expandida na tela. É consequência
   direta de escolher acordeão em vez de troca de tela inteira: a
   troca de tela nunca acumula mais de uma lista visível ao mesmo
   tempo, o acordeão sim, e sem renderização preguiçosa isso cresceria
   sem limite conforme mais níveis fossem abertos. O mecanismo exato
   (qual API faz isso) não é decidido aqui — ver Consequências.

**Consequências:**

Nenhuma mudança no que já está implementado — `core/search`
(`approximateSearch`) e `core/session` (`sessionScope`,
`sessionEventNames`) continuam exatamente como estão; esta ADR decide
só a apresentação da hierarquia, não o cálculo por trás dela, que já
existe e já tem teste.

Um nível do acordeão com muitas entradas (dezenas de temas ou eventos)
tem duas partes separadas. Achar um item específico já está resolvido:
a busca aproximada (`DA-NAV-02`, `DA-NAV-03`), já implementada e
testada no pacote `search`, mais o item 4 acima (busca cobrindo a
estrutura inteira de uma vez), é o mecanismo que o próprio projeto já
escolheu pra isso — a pessoa filtra digitando, em vez de rolar a lista
inteira. Mostrar a estrutura sem travar ou consumir memória demais
enquanto ela cresce (item 6 acima) é diferente — decorre só de
escolher acordeão, não da busca. O nome exato da peça que faz essa
renderização preguiçosa depende de uma escolha que nenhum documento do
projeto fez ainda — `decisions/0003` registra "telas
(Activities/Composables)" sem decidir entre as duas, e `decisions/0012`
não toca nisso. Essa escolha é maior que esta ADR (do
mesmo porte de `decisions/0001`, linguagem do aplicativo) e não
pertence a ela — vira pendência nova em `tasks.md`.

As demais pendências de desenho visual já registradas em `tasks.md`
(indicador de Bluetooth/NFC desligado; o botão de pausar, que também
exige código novo em `SessionViewModel.kt`) continuam em aberto — esta
ADR resolve só o padrão de navegação hierárquica, nenhuma delas.
"Ponto de início" e "Configuração da sessão" não entram nessa lista:
já são a mesma tela, por `EI-NAV-05`, sem pendência nenhuma.
