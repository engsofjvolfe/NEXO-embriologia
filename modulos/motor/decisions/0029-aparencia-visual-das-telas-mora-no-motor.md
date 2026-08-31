# 0029 — Aparência visual das telas mora dentro do módulo motor

Resumo em linguagem simples: toda tela do motor precisa de uma aparência (cor, fonte, layout).
Faltava decidir de quem é essa responsabilidade — de quem monta cada assunto (cada instância), ou
do próprio motor, numa única casca reaproveitada por todo mundo. Esta ADR fixa a segunda opção, e
explica por que a primeira não se sustenta.

Convenção dos códigos citados abaixo:
- `RNF-MOD` — [`2 - requisitos-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/2 - requisitos-conceito-geral.md>), seção 7.
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.

**Status:** aceito

**Contexto:**

O Projeto Arquitetônico ([`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
§2.2, aprovado, imutável) deixa a aparência visual de qualquer tela fora do escopo da cascata,
atribuindo essa responsabilidade a "quem constrói a interface de cada instância, de acordo com o
próprio conceito (§15)". Conferindo a seção 15 citada como base
([`1 - documento-de-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>)),
ela trata de outro assunto — os dois momentos de configuração manual do sistema (montagem de
conteúdo, configuração de sessão) — sem nunca falar em quem desenha uma tela; a citação não tem,
de fato, um fundamento direto no texto que ela aponta.

Outros pontos, dentro da mesma cascata de documentos e do restante do projeto, pesam contra a
leitura literal dessa frase:

- O motor não muda quando um assunto novo é criado — só o conteúdo muda. Documento de Conceito,
  seção 15: "uma nova instância... é adicionada... sem qualquer alteração na lógica central do
  sistema. O motor permanece o mesmo; muda apenas o conteúdo que roda dentro dele" — reforçado por
  RNF-MOD-01 e RNF-MOD-02.
- Quem monta um assunto novo nunca escreve código — promessa central do projeto, registrada no
  [`README.md`](../../../README.md) da raiz: "sem que quem monta esse conteúdo (um professor, por
  exemplo) precise escrever qualquer linha de código".
- A tela só confirma, nunca anuncia nem explica por conta própria (Documento de Conceito, seção 8)
  — o espaço de escolha real de qualquer tela já é bem estreito, decidido pelo comportamento, não
  pela aparência.
- A lista de telas em si (quantas existem, o que cada uma mostra) já é fixa, decidida uma vez só
  pro motor inteiro, nunca por instância (DA-RET-01 a DA-RET-17).
- Nenhum documento da cascata, em nenhum ponto, prevê ferramenta ou processo para alguém sem
  conhecimento técnico desenhar uma tela — a única ferramenta de autoria prevista é um editor de
  texto simples pra escrever o arquivo de conteúdo (Projeto Arquitetônico, §8, Premissas).

Antes desta ADR, o módulo motor já vinha tratando a aparência como responsabilidade própria, sem
que essa escolha tivesse passado por uma análise de alternativas registrada: `tasks.md` continha a
frase solta "já decidido: essa camada mora dentro do módulo motor", e `architecture.md` já continha
uma seção inteira ("Interface") construída sobre essa premissa. Esta ADR formaliza, com a análise
acima, o que já vinha sendo praticado nos dois documentos.

**Decisão:**

A aparência visual das telas do motor — cor, fonte, layout, e como as 17 entradas de tela do
Projeto Arquitetônico se agrupam em telas físicas — é responsabilidade deste módulo, nunca de quem
monta cada instância. Uma única casca visual, compartilhada e reaproveitada sem alteração por toda
instância. Essa camada mora dentro do módulo motor, como uma seção própria em `architecture.md`
("Interface") — não vira módulo separado, porque não existe fronteira real (nenhuma variação de
fato entre instâncias) que justifique separar.

Esta decisão extrapola a leitura literal do Projeto Arquitetônico §2.2 (aprovado, imutável) —
legítimo porque é este módulo, através dos próprios documentos (`concept.md`, `architecture.md`),
quem evolui esse ponto, sem reescrever o documento aprovado. Mesmo mecanismo já usado por este
módulo antes, num assunto totalmente diferente: `concept.md` já extrapolou o Projeto Detalhado
(PD-IMP-01, aprovado, imutável) ao subir o contrato de dado de `1.0.0` pra `2.0.0`, com o mesmo
raciocínio ("legítimo porque este `concept.md`, não aquele documento, é quem evolui o contrato de
dado") — ver `concept.md`, seção Contrato de dado, e Controle de versão, linha `0.3.0`. Aquela
mudança específica (campo `summary_fragment` obrigatório) foi motivada por
[decisions/0021](0021-quem-monta-o-texto-de-resumo-e-sintese.md), que decide quem escreve o texto
de resumo e síntese — o princípio de extrapolar um documento aprovado, em si, é só do `concept.md`,
não daquela ADR.

Alternativa real considerada e descartada: cada instância desenha e mantém a própria interface —
descartada porque contradiz todos os pontos listados em Contexto (exigiria código de quem monta
conteúdo; não existe ferramenta prevista pra isso; a lista de telas já é fixa pro motor inteiro,
não por instância).

Alternativa real considerada e adiada, não descartada: permitir um ajuste visual pontual por
instância (por exemplo, uma cor principal, escolhida como campo opcional no próprio arquivo de
conteúdo, sem exigir código) — honraria em parte a leitura original do Projeto Arquitetônico. Não
decidida agora porque exigiria mudar o contrato de dado do pacote de conteúdo, e nenhuma
necessidade real disso apareceu ainda; fica registrada aqui como possibilidade, não como pendência
ativa.

**Consequências:**

`concept.md` passa a listar a aparência visual como dentro do escopo deste módulo, apontando pra
esta ADR em vez de carregar a justificativa solta. `architecture.md` (seção "Interface") e
`tasks.md` (pendência "Desenhar a aparência visual das telas do motor") passam a apontar pra esta
ADR, no lugar da frase "já decidido" que carregavam antes, sem base registrada.

O valor exato da aparência (cor, fonte, layout de cada tela) continua sem decisão — esta ADR
resolve só de quem é a responsabilidade, não o desenho em si. Esse desenho segue como pendência
própria em `tasks.md`, pelo método já registrado em `architecture.md` (arquitetura de informação,
wireframe, aplicação do sistema visual, protótipo com avaliação de usabilidade) — nenhuma das
quatro etapas foi executada ainda. Dentro disso, o agrupamento das 17 entradas de tela em telas
físicas já está parcialmente resolvido, à parte desta ADR: as 10 que são variações de conteúdo
dentro da sessão em jogo formam uma tela só
([decisions/0022](0022-conteudo-do-estado-exposto-pelo-viewmodel.md), `SessionScreen`, tipo
fechado); só as outras 7 (páginas de navegação de fato) seguem sem agrupamento físico decidido.

Nenhuma fonte externa foi necessária pra esta decisão — resolvida inteiramente com o que a cascata
de documentos e o `README.md` já estabelecem.

**Nota de acompanhamento (30-08-2026):**

*Resumo simples:* o valor exato da aparência (cor, fonte, layout), que este ADR registrava acima
como ainda sem decisão, já foi decidido — as quatro etapas do método citadas acima estão prontas.

*Detalhe técnico:* etapa 2 (wireframe) resolvida em `design/wireframe.md`; etapa 3 (sistema visual)
resolvida em [decisions/0035](0035-sistema-visual-cor-tipografia-forma-contraste.md); etapa 4
(protótipo navegável e avaliação) resolvida em
[decisions/0036](0036-ferramenta-e-fidelidade-do-prototipo-navegavel.md) — cada uma carrega o
detalhe exato, nunca repetido aqui.
