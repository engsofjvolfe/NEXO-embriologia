# Findings — Motor

<!-- module-doc-type: findings -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Findings |
| Versão | 0.2.0 |
| Data | 14-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Achados confirmados (por leitura de código, teste ao vivo, ou os dois)
> sobre este módulo — cada entrada datada.
>
> Checa se o código já existente bate com o requisito que `concept.md`
> já descreve -- nunca o contrário: um achado aqui não muda o que
> `concept.md` diz que deveria existir, só revela onde o código diverge
> disso (a divergência vira pendência em `tasks.md`). Pode acontecer a
> qualquer momento: antes de `architecture.md` existir, ou depois,
> quando a implementação revela algo não previsto no desenho.
>
> Um achado, uma vez escrito, não é apagado nem reescrito se deixar de
> valer depois (mudança real de código, por exemplo) — ganha uma entrada
> nova, datada, dizendo o que mudou. Igual ADR: acrescenta, não
> reescreve por cima.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, campo `Confirmado por` com valor fixo, resumo
> simples, depois detalhe técnico.

## Índice
- [Achados](#achados)
- [Controle de versão](#controle-de-versão)

## Achados

### <a id="2026-08-14-posicao-com-buraco-nao-detectada"></a>2026-08-14 — Posição de tema/evento com buraco ou duplicada não é detectada

**Confirmado por:** leitura de código

*Resumo simples:* o pacote `hierarchy` já existente checa nome
repetido dentro de um grupo, mas nunca checa se as posições dos itens
"com ordem" desse mesmo grupo fazem sentido — dois eventos podendo
declarar a mesma posição, ou um cadastro pulando um número (posições
1 e 3, sem o 2), passam sem nenhum aviso. Isso diverge do que o
próprio Documento de Conceito já exigia desde a aprovação da cascata.

*Detalhe técnico:*
- [`1 - documento-de-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
  seção 2: "Dentro de um tema, os eventos podem ter ordem entre si
  (**um evento é continuação lógica do anterior**) ou podem ser
  avulsos." Um item "com ordem" é definido como continuação do
  **anterior** — não existe "anterior" pra um item cuja posição
  imediatamente inferior não foi cadastrada por ninguém, o que torna
  buraco de posição incompatível com a própria definição de "ter
  ordem", não só "improvável".
- [`HierarchyValidation.kt`](<../core/src/main/kotlin/org/nexo/motor/core/hierarchy/HierarchyValidation.kt>)
  (antes desta correção) só implementava `duplicateNames` — nenhuma
  função olhava pro campo `position` de `Ordering.Ordered`.
- Descoberto durante o desenho do pacote `session`
  ([decisions/0009](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>)):
  o cálculo de recorte contíguo de uma sessão precisa presumir que a
  numeração de um grupo "com ordem" já chega correta — sem essa
  garantia vindo de `hierarchy`, `session` teria que reimplementar a
  mesma checagem por conta própria, ou (erro pior) simplesmente
  aceitar dado incorreto como se fosse uma sequência válida.
- Resolvido corrigindo `HierarchyValidation.kt`: para cada grupo "com
  ordem" (temas de uma instância, eventos de um tema), as posições
  declaradas precisam formar exatamente `1, 2, ..., N` (N = quantidade
  de itens "com ordem" do grupo) — sem pular número, sem repetir.
  Qualquer outro conjunto de posições vira uma violação nova
  (`NonContiguousThemePositions`, `NonContiguousEventPositions`),
  seguindo o mesmo formato de lista completa já usado desde
  [decisions/0007](<../decisions/0007-desenho-do-pacote-hierarchy.md>).

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. Entrada nova em "Achados" (append, sem
reescrever) também conta como mudança de conteúdo real. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial. | Criação inicial |
| 0.2.0 | 14-08-2026 | Achado "Posição de tema/evento com buraco ou duplicada não é detectada" acrescentado. | Desenho do pacote `session` revelou divergência no pacote `hierarchy` já existente |
