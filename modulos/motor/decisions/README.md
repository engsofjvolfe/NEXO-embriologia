# Decisions — Motor

<!-- module-doc-type: decisions-index -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Decisions — Índice |
| Versão | 0.1.0 |
| Data | 12-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Decisões (ADR) específicas deste módulo — um arquivo por decisão,
> numerado (`0001-titulo-curto.md`, `0002-...`).
>
> Não é uma etapa fixa do fluxo do módulo — nasce em qualquer ponto (ao
> decidir o conceito entre alternativas, ao desenhar a arquitetura, ao
> formalizar o schema, ao implementar), sempre que existe uma escolha
> real entre alternativas que precisa ficar registrada com o contexto
> que a motivou.
>
> Cada arquivo começa com um resumo em linguagem simples (porta de
> entrada, não conta como um dos quatro campos fixos), seguido dos
> quatro campos fixos:
> - **Status** — só o estado (`proposto` / `aceito` / `substituído pelo
>   ADR-NNNN`).
> - **Contexto** — o que motivou a decisão, o diagnóstico.
> - **Decisão** — o que foi decidido.
> - **Consequências** — o que foi verificado, o que mudou de fato, o que
>   ficou de fora.
>
> Nunca editado depois de aceito pra revisar a decisão em si — decisão
> que muda gera um ADR novo, que substitui o antigo (o antigo marcado
> como substituído no campo Status, não apagado). Edição depois de
> aceito é permitida só como manutenção que não muda a decisão em si:
> corrigir erro factual, marcar transição de Status, acrescentar nota de
> acompanhamento datada com verificação posterior -- essa nota também
> começa com um resumo simples antes do detalhe técnico.

## Índice

| ADR | Título |
|---|---|
| [0001](0001-linguagem-do-aplicativo.md) | Linguagem do aplicativo |
| [0002](0002-framework-do-firmware-do-acessorio.md) | Framework do firmware do acessório leitor |

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
deste documento (ADR nova acrescentada à lista, por exemplo) sobe a
versão (SemVer) e ganha uma linha nova aqui, junto com o campo Versão
da tabela de cabeçalho, que sempre reflete a última linha desta
tabela. Esta versão é do próprio índice -- cada ADR individual não
leva versão própria, o histórico dela é o campo Status (substituído
pelo ADR-NNNN). -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial: ADRs 0001 e 0002. | Criação inicial |
