# 0034 — Mecanismo de carregamento aos poucos (lazy) do acordeão de navegação

Resumo em linguagem simples: a tela de navegação (instância, tema,
evento) pode ter dezenas de itens abertos ao mesmo tempo, conforme a
pessoa vai expandindo o menu em sanfona já decidido em `decisions/0030`
— sem cuidado, isso desenharia tudo de uma vez, mesmo o que está fora
da tela, travando o aplicativo conforme a lista cresce. Esta ADR decide
o mecanismo exato que evita isso: uma única lista "preguiçosa"
(`LazyColumn`, componente pronto do Jetpack Compose), nunca uma lista
preguiçosa dentro de outra.

Convenção dos códigos citados abaixo:
- `DA-RET`, `DA-NAV` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seções 6.6 e 6.8.

**Status:** aceito

**Contexto:**

[decisions/0030](0030-padrao-de-navegacao-hierarquica-de-conteudo.md),
seção Decisão, item 6, já fixa que o acordeão de navegação "renderiza
de forma preguiçosa (lazy) — nunca desenha de uma vez a estrutura
inteira já expandida na tela", mas deixa o mecanismo exato em aberto,
registrando isso como pendência nova: "o nome exato da peça que faz
essa renderização preguiçosa depende de uma escolha que nenhum
documento do projeto fez ainda — `decisions/0003` registra 'telas
(Activities/Composables)' sem decidir entre as duas, e `decisions/0012`
não toca nisso". Essa pendência (movida pra
[tasks.md](<../docs/tasks.md#em-aberto>) depois) ficou sem resposta até
[decisions/0031](0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md)
fixar o Jetpack Compose como ferramenta de desenho de tela do módulo
`app` — decisão anterior, sem relação direta com esta, mas que fecha a
plataforma dentro da qual o mecanismo de carregamento aos poucos
precisa existir.

A documentação oficial do Jetpack Compose (GOOGLE, [s.d.]) descreve
`LazyColumn` (e `LazyRow`, a versão horizontal) exatamente para este
problema: "se você precisa mostrar uma quantidade grande de itens (ou
uma lista de tamanho desconhecido), usar um leiaute como `Column` pode
causar problema de desempenho, já que todos os itens serão desenhados
e posicionados, estejam ou não visíveis" — o Compose "fornece um
conjunto de componentes que só desenham e posicionam os itens visíveis
na área da tela do próprio componente. Esses componentes incluem
`LazyColumn` e `LazyRow`."

A mesma fonte trata diretamente do caso deste projeto — uma estrutura
com níveis dentro de níveis (instância, tema, evento) que se expandem:
"evite aninhar componentes que rolam na mesma direção" — colocar uma
lista preguiçosa dentro de outra lista rolável na mesma direção
("vertical dentro de vertical") não é só desaconselhado, lança erro em
tempo de execução (`IllegalStateException`) segundo a própria
documentação. A alternativa oficial pra estrutura hierárquica, como a
sanfona de navegação já decidida, é achatar tudo numa única
`LazyColumn`, usando a linguagem própria dela (`item { }` pra um item
isolado, `items(lista) { }` pra uma lista) pra descrever, em sequência
plana, o que está visível a cada momento: "o mesmo resultado pode ser
alcançado envolvendo todos os seus itens dentro de uma única
`LazyColumn` pai, usando a linguagem dela pra passar diferentes tipos
de conteúdo".

**Decisão:**

1. O acordeão de navegação (instância, tema, evento —
   `decisions/0030`) é desenhado como uma única `LazyColumn`, nunca
   como uma lista preguiçosa aninhada dentro de outra.
2. Cada linha visível — uma instância, um tema já expandido, um evento
   já expandido — é um item plano dentro dessa mesma `LazyColumn`,
   usando a linguagem oficial dela (`item`/`items`) pra descrever o que
   está visível a cada momento; expandir ou recolher um nível muda
   quais itens entram nessa lista achatada, nunca abre uma segunda
   lista rolável dentro da primeira.
3. A busca que já roda sobre a estrutura inteira
   (`decisions/0030`, item 4) opera sobre a mesma lista de dados que
   alimenta essa `LazyColumn` achatada — a forma de apresentação
   (achatada, preguiçosa) não muda o que já foi decidido sobre o
   alcance da busca.
4. Fora desta decisão: espaçamento, ícone de "aberto"/"fechado",
   animação de expandir — isso é aplicação do sistema visual (passo 3
   do método, `architecture.md#interface`), não deste documento.

**Consequências:**

Fecha a pendência "Decidir o mecanismo de carregamento aos poucos
(lazy) da lista em acordeão de navegação" registrada em
[tasks.md](<../docs/tasks.md#em-aberto>).

Nenhuma mudança em código já escrito — nenhuma tela existe ainda.

A escrita de verdade dessa tela (como o estado de "aberto/fechado" de
cada nível é guardado, como a lista achatada é montada a partir da
hierarquia e do estado de expansão) fica para a implementação, quando
a pendência "Desenhar o esqueleto das 16 entradas de tela restantes"
chegar até essa tela específica — esta ADR fixa só o mecanismo de
renderização, não a estrutura de dado que alimenta ele.

## Referências

Fonte externa consultada para embasar esta decisão, no formato
definido pela norma ABNT NBR 6023 (Informação e documentação —
Referências). Citações traduzidas livremente no corpo do documento;
texto original preservado entre aspas antes da tradução quando citado
diretamente. Citada no corpo do documento como (GOOGLE, ano).

GOOGLE. **Lazy lists and lazy grids**. Android Developers — Jetpack
Compose, [s.d.]. Disponível em:
https://developer.android.com/develop/ui/compose/lists. Acesso em: 30
ago. 2026.
