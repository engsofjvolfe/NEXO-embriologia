# 0004 — Desenho do algoritmo de busca aproximada

Resumo em linguagem simples: a distância de Levenshtein (quantas letras
precisam mudar pra transformar um texto no outro) e o limiar de
tolerância já estavam decididos noutro documento, aprovado e impossível
de editar. O que faltava decidir era mais específico: se maiúscula,
minúscula e acento contam como diferença (não contam — a busca ignora
os dois, mesmo padrão usado por buscadores de mercado); o que acontece
quando dois resultados empatam (mantém a ordem que a lista já tinha na
tela); e se a busca compara só a palavra inteira ou também pedaços
dela (as duas, com a palavra inteira aparecendo primeiro nos
resultados).

Convenção dos códigos citados abaixo:
- `PD-NAV` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.4.
- `DA-NAV` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.8.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado no [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
seção 6.3.3. Todo trecho abaixo marcado descreve um fato mantido por
terceiro (classificação de revista acadêmica, comportamento
documentado de uma ferramenta de mercado) — nunca uma decisão deste
projeto, e sujeito a mudar sem aviso a qualquer momento, inclusive
depois desta ADR ser aceita. Quem ler este documento depois deve
tratar esse conteúdo como possivelmente desatualizado e reconfirmar na
fonte oficial (seção Referências) antes de usar o dado como base pra
qualquer decisão nova — uma nota de acompanhamento datada, permitida
pelo formato de ADR deste projeto (ver
[decisions/README.md](README.md)), é o jeito de registrar essa
reconfirmação sem reescrever a decisão original.

**Status:** aceito

**Contexto:** PD-NAV-01 e PD-NAV-02 já fixam o algoritmo (distância de
edição de Levenshtein) e o limiar de tolerância (20% do tamanho do
termo digitado, arredondado pra baixo, mínimo 1) — documento aprovado,
imutável (ver
[`docs/docs-VMODEL-visao-geral/README.md`](<../../../docs/docs-VMODEL-visao-geral/README.md>)),
não reaberto aqui. O próprio PD-NAV-02 já registra que esse número de
20% não vem de nenhuma norma ou fonte externa — é uma escolha de
engenharia do documento, proposital mas provisória, com pendência de
validação já aberta em
[tasks.md, "Validar em campo o limiar de busca aproximada"](<../docs/tasks.md#em-aberto>).
Mas nenhum documento da cascata desceu ao nível de três pontos que
qualquer implementação real do algoritmo precisa resolver:

1. A comparação trata maiúscula/minúscula e acento como diferença, ou
   ignora os dois antes de comparar?
2. Quando dois nomes empatam na mesma distância, em que ordem aparecem
   no resultado?
3. A busca compara o termo digitado só contra o nome inteiro de cada
   item, ou também contra pedaços (trechos) desse nome — por exemplo,
   digitar "vent" encontrando "Evento X" mesmo sem bater o nome
   inteiro?

Pra resolver o primeiro ponto, em vez de uma escolha arbitrária, a
prática de dois sistemas de busca de mercado foi comparada:

- `[REVISAR-EXTERNO]` Google Cloud, **"What is fuzzy search?"**
  (cloud.google.com, atualizado em 14 jan. 2026): lista, como primeiro
  passo de qualquer implementação de busca aproximada, "converting
  text to lowercase" antes de calcular a similaridade.
- `[REVISAR-EXTERNO]` Elastic, documentação oficial da **fuzzy query**
  do Elasticsearch (elastic.co) e da análise de texto (analyzers): os
  três analisadores padrão (`standard`, `simple`, `whitespace`) já
  convertem tudo pra minúscula antes de comparar, sem precisar
  configurar nada — e a mesma documentação mostra o filtro
  `asciifolding`, usado junto com `lowercase`, especificamente pra
  também ignorar acento (o exemplo oficial converte "Is this déja vu?"
  em "is", "this", "deja", "vu").

Os dois já tratam a distância de edição como a base do cálculo (mesmo
algoritmo de PD-NAV-01) — a única diferença está numa etapa anterior à
comparação em si (normalizar o texto), não no algoritmo.

Pro terceiro ponto, com uma fonte acadêmica: NAVARRO, Gonzalo. **A
guided tour to approximate string matching**. ACM Computing Surveys,
v. 33, 2001 — revista de revisão da ACM (Association for Computing
Machinery), publicada sem interrupção desde 1969. `[REVISAR-EXTERNO]`
Classificada como Q1 (a faixa mais alta) pelo SCImago Journal &
Country Rank, com índice-h de 329 e média de citação de ~22,6 por
artigo nos últimos dois anos, ambos reavaliados anualmente pelas
respectivas fontes (SCIMAGO JOURNAL & COUNTRY RANK, [s.d.]; OPENALEX,
[s.d.]). A seção 3.1 formaliza
exatamente o problema de achar um termo *dentro* de um nome maior
tolerando erros (o caso "por trecho" deste documento): dado um texto
T, um termo P e um número de erros k, achar toda posição de T onde
existe um trecho cuja distância até P é ≤ k. A seção 5.1.1 mostra o
algoritmo pra comparar duas strings inteiras (o caso "palavra
inteira"); a seção 5.1.2 mostra a mesma tabela de programação
dinâmica com uma única mudança na condição de contorno (a primeira
linha da tabela vira zero em vez de crescente, liberando o trecho pra
começar em qualquer posição do nome) — atribuída por Navarro (2001) a
SELLERS, Peter H. **The theory and computation of evolutionary
distances: pattern recognition**. Journal of Algorithms, v. 1, n. 4,
1980 (citado indiretamente via Navarro, 2001, seção 5.1.2).

**Decisão:**

1. **Maiúscula/minúscula e acento não contam como diferença.** Antes
   de calcular a distância, tanto o termo digitado quanto o nome
   cadastrado passam por normalização: converter pra minúscula e
   substituir cada letra acentuada pela equivalente sem acento (mesmo
   efeito do filtro `asciifolding` do Elasticsearch — "é" e "e" viram
   o mesmo caractere antes da comparação). "EvEnto", "evento" e
   "évento" têm distância 0 entre si.

2. **Empate de distância preserva a ordem de exibição já em uso.** A
   ordenação por distância usa um algoritmo de ordenação estável (o
   `sortedBy`/`sortedWith` do Kotlin, que documentadamente preserva a
   ordem relativa de elementos iguais) sobre a lista já ordenada
   segundo o critério ativo na tela — por padrão, alfabético
   (DA-NAV-01). O pacote `search` nunca decide um critério de
   desempate por conta própria; herda a ordem de quem chamou.

3. **A busca compara o termo inteiro E também por trecho, com o
   resultado de palavra inteira sempre aparecendo primeiro.** Duas
   distâncias são calculadas pra cada item, ambas sobre a mesma tabela
   de programação dinâmica (Navarro, 2001, seções 5.1.1 e 5.1.2):
   - distância de string inteira: entre o termo digitado (normalizado)
     e o nome completo do item (normalizado);
   - distância por trecho: a menor distância entre o termo digitado e
     qualquer trecho contíguo do nome do item — permite achar "vent"
     dentro de "Evento X" mesmo a palavra inteira "Evento X" estando
     muito longe de "vent".

   Um item entra no resultado se qualquer uma das duas distâncias for
   ≤ ao limiar de PD-NAV-02 (o limiar depende só do tamanho do termo
   digitado, o mesmo valor vale pras duas comparações). Dentro do
   resultado, todo item que bateu por palavra inteira aparece antes de
   qualquer item que só bateu por trecho; dentro de cada um desses dois
   grupos, a ordenação por distância e o desempate seguem a decisão 2.

**Consequências:** o pacote `search` (`core/search/`) implementa duas
funções de distância sobre a mesma base matemática (Navarro, 2001),
mais uma função de normalização (minúsculas + sem acento) aplicada
antes de qualquer comparação, mais uma função de composição que aplica
limiar, filtra e ordena (palavra inteira primeiro, depois por trecho,
com empate preservando a ordem de entrada). Nenhuma das funções
depende de nada específico do Android — consistente com
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md). Custo
aceito: a comparação por trecho é O(tamanho do termo × tamanho do
nome) por item, mais cara que a comparação de string inteira — aceitável
dado o tamanho esperado das listas de navegação (instâncias, temas e
eventos de uma instância, não um catálogo de milhões de itens; a
própria arquitetura do motor não usa servidor nem índice central, ver
DA-ARM-01).

Fica registrado como ponto de atenção pra quando o limiar de PD-NAV-02
for validado em campo (pendência já existente em
[tasks.md](<../docs/tasks.md#em-aberto>)): a validação deve cobrir os
dois casos (palavra inteira e por trecho) separadamente, já que a
busca por trecho tende a gerar mais resultados — qualquer nome
comprido pode conter um trecho parecido com um termo curto.

## Referências

ELASTIC. **Fuzzy query — Elasticsearch Reference**. [S.l.], [s.d.].
Disponível em:
https://www.elastic.co/docs/reference/query-languages/query-dsl/query-dsl-fuzzy-query.
Acesso em: 13 ago. 2026.

GOOGLE. **What is fuzzy search?**. Google Cloud, 14 jan. 2026.
Disponível em: https://cloud.google.com/discover/what-is-fuzzy-search.
Acesso em: 13 ago. 2026.

NAVARRO, Gonzalo. **A guided tour to approximate string matching**.
ACM Computing Surveys, v. 33, n. 1, p. 31-88, 2001. Disponível em:
http://www.dcc.uchile.cl/~gnavarro/ps/acmcs01.1.pdf. Acesso em: 13
ago. 2026.

OPENALEX. **ACM Computing Surveys — source metadata**. [S.l.], [s.d.].
Disponível em: https://api.openalex.org/sources?search=ACM%20Computing%20Surveys.
Acesso em: 13 ago. 2026.

SCIMAGO JOURNAL & COUNTRY RANK. **ACM Computing Surveys**. [S.l.],
[s.d.]. Disponível em:
https://www.scimagojr.com/journalsearch.php?q=23038&tip=sid. Acesso
em: 13 ago. 2026.

SELLERS, Peter H. **The theory and computation of evolutionary
distances: pattern recognition**. Journal of Algorithms, v. 1, n. 4,
p. 359-373, 1980. Citado indiretamente via NAVARRO (2001), seção
5.1.2.
