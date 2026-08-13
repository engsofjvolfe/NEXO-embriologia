# Concept — <nome do módulo>

<!-- module-doc-type: concept -->

| Campo | Valor |
|---|---|
| Módulo | <nome do módulo> |
| Documento | Concept |
| Versão | 0.1.0 |
| Data | <AAAA-MM-DD> |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Descreve o desenho pretendido do módulo — o que ele deve ser e como
> deve se comportar. É sempre o primeiro passo do módulo, com código
> já existente ou não -- guia o resto (`architecture.md`, `schemas/`,
> implementação).
>
> Se o módulo já tem código, `analysis.md`/`findings.md` checam, a
> qualquer momento, se esse código bate com o requisito que este
> arquivo descreve -- nunca o contrário: descobrir o que o código já
> faz não muda o que este arquivo diz que deveria fazer. Divergência
> vira pendência em `tasks.md`, resolvida corrigindo o código, nunca
> reescrevendo este arquivo pra bater com ele.
>
> Quando o módulo tem contrato de dado, carrega um bloco YAML puro
> descrevendo esse contrato — fonte única da qual `schemas/*.json` é
> gerado (nunca escrito à mão em paralelo).
>
> Não é um relato de investigação (isso é `analysis.md`) nem uma lista
> de achados (isso é `findings.md`). Edita-se
> por cima pra ajustes pequenos ao mesmo desenho; se o conceito inteiro
> for repensado, este arquivo é substituído por um novo (a troca em si
> vira um registro em `decisions/`), não silenciosamente reescrito por
> cima do que havia antes.
>
> Cada seção segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

## Índice
- [Escopo](#escopo)
- [Fluxo](#fluxo)
- [Controle de versão](#controle-de-versão)

## Escopo

<!--
*Em resumo:* ...

*Em detalhe técnico:* o que este módulo cobre e o que fica fora dele.
-->

## Fluxo

<!--
*Em resumo:* ...

*Em detalhe técnico:* o desenho do comportamento, passo a passo.
-->

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | <AAAA-MM-DD> | Criação inicial. | Criação inicial |
