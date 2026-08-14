# 0009 — Cálculo do recorte contíguo de uma sessão

Resumo em linguagem simples: uma sessão pode cobrir mais de um evento
ou mais de um tema, desde que sejam vizinhos na ordem já declarada —
nunca pulando um item que existe no meio (regra já fixada na
Especificação, faltava só decidir como o código garante isso). O
pacote `hierarchy` já garante que a numeração de um grupo "com ordem"
chega sem buraco nem repetição (ver
[findings.md](<../docs/findings.md#2026-08-14-posicao-com-buraco-nao-detectada>)),
então o cálculo aqui parte de uma lista já confiável — o trabalho de
`session` é só percorrer essa lista a partir de um ponto de início,
nunca deixar montar um conjunto solto de itens e checar depois: assim,
escolher errado (pular um item do meio) fica impossível de acontecer,
em vez de só proibido depois de acontecer.

Convenção dos códigos citados abaixo:
- `EI-HIE` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.1.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.10.
- `EI-NAV` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.15.

**Status:** aceito

**Contexto:** [decisions/0007](0007-desenho-do-pacote-hierarchy.md) já
registrava esta pendência explicitamente, deferida pra quando `session`
fosse desenhado: "o cálculo de que itens formam um recorte contíguo
dentro de um grupo ordenado (EI-SES-06 a EI-SES-08) [...] pertence ao
pacote `session`". Nenhum outro documento decide algo além disso —
conferido pela mesma leitura completa da cascata e das ADRs já feita
para [decisions/0008](0008-representacao-do-estado-da-sessao.md).

O ponto de partida deste desenho é a garantia que `hierarchy` já dá,
depois de corrigida (ver
[findings.md](<../docs/findings.md#2026-08-14-posicao-com-buraco-nao-detectada>)):
dentro de um grupo "com ordem" (temas de uma instância, eventos de um
tema), as posições declaradas sempre formam uma sequência 1, 2, 3...,
sem pular número e sem repetir — nunca chegam em `session` com buraco
nem duplicata. Isso simplifica o que falta decidir aqui: `session` não
precisa checar de novo nada sobre a validade da numeração, só decidir
como percorrer essa lista já correta pra montar o recorte escolhido
por quem inicia a sessão.

Duas formas de montar o recorte foram consideradas:

1. Deixar quem monta a sessão escolher livremente um conjunto de itens
   (ex.: uma lista de eventos marcados), e só depois rodar uma checagem
   que confirma se esse conjunto é mesmo contíguo — rejeitando se não
   for.
2. Nunca permitir montar um conjunto livre em primeiro lugar: o recorte
   é sempre o resultado de percorrer a lista já ordenada a partir de um
   ponto de início escolhido, extraindo um trecho dela (do início até
   onde quem monta a sessão decidir parar, nunca voltando pra trás nem
   pulando itens da lista).

A primeira exige escrever e manter uma função de checagem que pode
ficar sem uso em algum ponto de chamada (código nunca é obrigado a
chamá-la antes de montar a sessão). A segunda torna a violação de
EI-SES-06/07 irrepresentável por construção — mesma lógica já usada em
[decisions/0007](0007-desenho-do-pacote-hierarchy.md) pra `Ordering`
(um item avulso não consegue carregar uma posição por engano, porque o
tipo não permite escrever esse código): aqui, um recorte que pula um
item do meio não consegue ser montado, porque a própria função que
constrói o recorte nunca produz esse resultado.

**Decisão:**

1. **O recorte de uma sessão é sempre calculado, nunca escolhido item a
   item e validado depois.** Uma função recebe a lista de irmãos do
   mesmo grupo (temas de uma instância, ou eventos de um tema), separa
   só os "com ordem" (item avulso nunca participa desse cálculo), e
   ordena pela posição — confiando que `hierarchy` já garantiu que essa
   posição não tem buraco nem repetição. A partir de um item de início
   escolhido, o recorte é sempre um trecho contínuo dessa lista já
   ordenada — do ponto de início até onde quem configura a sessão
   decidir parar (ver EI-NAV-05, tela de configuração), nunca voltando
   a um item anterior ao ponto de início, nunca pulando um item do
   meio.

2. **Escolher um item avulso como ponto de entrada limita o recorte a
   esse item sozinho, sem cálculo** — aplicação direta de EI-SES-08,
   sem alternativa a decidir.

API pública, genérica o bastante pra servir tanto à lista de temas de
uma instância quanto à lista de eventos de um tema — mesma lógica de
generalização já usada no pacote `search`
([decisions/0004](0004-desenho-do-algoritmo-de-busca-aproximada.md)),
atendendo RNF-MOD-01:

```
core/session/
  SessionScope.kt   sessionScope<T>(siblings: List<T>, ordering: (T) -> Ordering, from: T, until: T): List<T>
```

Sem tipo de violação próprio neste pacote — a única fonte de violação
possível (posição com buraco ou repetida) já é responsabilidade de
`hierarchy.validate()`; `sessionScope` presume uma lista já validada
por ele.

**Consequências:** o pacote `session` (`core/session/`) passa a
depender do que `hierarchy` já expõe (`Ordering`, a lista de irmãos de
um grupo, e a garantia de posição sem buraco depois de
[findings.md](<../docs/findings.md#2026-08-14-posicao-com-buraco-nao-detectada>)),
sem precisar reimplementar nenhuma checagem de validade — só a lógica
de recorte em si. Nenhuma função ou tipo deste pacote depende de
classe de interface do Android, mesma consistência já mantida em
`search` e `hierarchy`
([decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)) —
testável por teste de unidade comum, com `kotlin-test` + JUnit
Jupiter, mesma ferramenta já fixada em
[decisions/0005](0005-abordagem-de-teste-do-nucleo-do-motor.md).

Fica de fora desta decisão, registrado só como pendência futura: onde
o recorte de uma sessão em curso — e o restante do estado descrito em
[decisions/0008](0008-representacao-do-estado-da-sessao.md) — fica
salvo em disco entre uma abertura do aplicativo e outra, pra atender
EI-PAU-01/02. Fica para uma ADR própria.
