# 0014 — Comportamento da busca aproximada com termo vazio

Resumo em linguagem simples: quando a caixa de busca está vazia (ou só
tem espaço), a função de busca aproximada não filtra nem reordena nada
— devolve a lista de itens exatamente como recebeu. O código já fazia
isso, e já existia um teste confirmando, mas nenhum documento tinha
registrado essa escolha como decisão de verdade, nem por que ela foi
tomada assim e não de outro jeito.

Convenção dos códigos citados abaixo:
- `PD-NAV` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.4.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Todo trecho abaixo marcado
descreve comportamento documentado de ferramenta de mercado — não uma
decisão deste projeto, e sujeito a mudar em qualquer revisão futura
dessa documentação. Quem ler este documento depois deve tratar esse
conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra qualquer
decisão nova.

**Status:** aceito

**Contexto:** [decisions/0004](0004-desenho-do-algoritmo-de-busca-aproximada.md)
já resolveu três pontos que a cascata de documentação não descia a
esse nível de detalhe para o pacote `search` (maiúscula/acento, ordem
de desempate, comparação inteira e por trecho) — mas não cobriu um
quarto ponto, no mesmo nível: o que a função devolve quando o termo
digitado, depois de removidos os espaços das pontas, fica vazio.

O algoritmo de distância de Levenshtein (PD-NAV-01) não tem resposta
natural pra essa situação: a distância de qualquer nome até uma string
vazia é sempre igual ao próprio tamanho do nome, o que, aplicado sem
ajuste ao limiar já fixado (PD-NAV-02), descartaria quase todo item da
lista — o oposto do que faz sentido quando não há nenhum critério de
busca em uso ainda.

O teste já existente (`ApproximateSearchTest.kt`) já confirmava esse
comportamento — termo vazio devolve a lista de entrada sem filtrar nem
reordenar —, sem nunca ler o código de implementação em si: essa
investigação, do início ao fim, partiu só da documentação e dos testes
já escritos. Mas essa escolha nunca tinha passado pelo registro formal
que o restante do pacote `search` já segue, ficando como comportamento
de fato, comprovado por teste, mas nunca elevado a decisão.

Duas alternativas reais:

(a) Termo vazio devolve a lista de entrada, sem filtro nem
reordenação — trata "nada digitado" como "nenhum critério aplicado
ainda".

(b) Termo vazio devolve lista vazia — trata "nada digitado" como
"nenhum resultado correspondente".

`[REVISAR-EXTERNO]` A documentação oficial do Algolia — serviço usado
especialmente pra filtrar uma lista em tempo real conforme a pessoa
digita, o mesmo tipo de tela que a categoria NAV do motor vai precisar
(EI-NAV-02 a EI-NAV-05) — registra como padrão a alternativa (a): por
padrão, o InstantSearch sempre mostra resultados, mesmo com a busca
vazia (tradução livre; ALGOLIA, [s.d.]). Vale como precedente de
mercado pro mesmo tipo de problema que este pacote resolve, mas com
peso técnico limitado: é a escolha de produto de uma empresa, não um
argumento de necessidade algorítmica — a documentação oficial confirma
o comportamento, mas não explica o motivo por trás dele.

**Decisão:** alternativa (a). Mesma lógica já registrada na decisão 2
da [ADR 0004](0004-desenho-do-algoritmo-de-busca-aproximada.md) ("o
pacote `search` nunca decide um critério de desempate por conta
própria; herda a ordem de quem chamou"), estendida aqui: sem termo pra
comparar, não existe distância nenhuma a calcular, então o pacote não
impõe filtro nenhum — devolve a entrada como veio, na ordem como veio.
O precedente do Algolia acima reforça que essa escolha é viável na
prática, sem ser o motivo principal da decisão.

Alternativa (b) foi descartada, além do motivo acima, porque trataria
"a pessoa ainda não digitou nada" do mesmo jeito que "a pessoa digitou
algo e nada bateu" — dois estados com significado diferente pra quem
for desenhar a tela de navegação (pendência ainda em aberto, ver
[tasks.md](<../docs/tasks.md#em-aberto>)). Devolver a lista intocada
preserva essa distinção em vez de apagá-la de dentro do núcleo, antes
mesmo de a interface decidir o que fazer com ela.

**Consequências:** `approximateSearch` passa a ter contrato definido e
registrado pra qualquer termo digitado, incluindo vazio ou só espaço —
nenhuma mudança de código foi necessária, porque a implementação já
seguia esta alternativa; o teste que já existia ganhou referência
explícita a esta ADR, deixando de ser um comportamento implícito sem
registro por trás. Fica de fora desta decisão, sem resposta ainda: se
a tela de navegação (quando for desenhada) chega a chamar
`approximateSearch` com termo vazio de verdade, ou se resolve esse
estado antes, sem passar pela função — isso pertence ao desenho da
Interface, não a este pacote.

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

ALGOLIA. **Conditional display — Building a search UI**. Algolia
Documentation, [s.d.]. Disponível em:
https://www.algolia.com/doc/guides/building-search-ui/going-further/conditional-display/react.
Acesso em: 14 ago. 2026.
