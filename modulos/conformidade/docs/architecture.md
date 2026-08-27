# Architecture — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Architecture |
| Versão | 0.1.0 |
| Data | 27-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Descreve como o módulo é construído por dentro — layout de arquivos,
> pacote, fronteiras, fluxo de dados técnico. É o "como" que corresponde
> ao "o quê" do `concept.md`; lido logo em seguida, quando existir.
>
> Implementação de código deriva sempre daqui e do contrato em
> `schemas/` — nunca o contrário. Este módulo não tem `schemas/`: a
> fronteira de dado real que ele consome (o JSON que o Claude Code
> entrega a cada gancho -- `tool_name`, `tool_input`, `cwd`, etc.) é
> definida pela documentação oficial do Claude Code, não por este
> projeto -- não há contrato pra gerar aqui.
>
> Cada seção segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

## Índice
- [Layout](#layout)
- [Controle de versão](#controle-de-versão)

## Layout

*Em resumo:* o layout de arquivos completo (o que existe, onde, e o
papel de cada arquivo) já está descrito no `MANUAL.md`, seções 2 e 8.
Este módulo não repete essa lista — aponta pra ela, e acrescenta só o
que a estrutura de módulo em si exige (`decisions/`, `findings.md`,
`tasks.md`, `handoff.md`, que `MANUAL.md` não tinha antes deste módulo
existir).

*Em detalhe técnico:*

- [`MANUAL.md`, seção 2](<../../../MANUAL.md#2-as-quatro-camadas-da-mais-forte-pra-mais-específica>) —
  as quatro camadas (GitHub, git nativo, Vale, Claude Code) e como uma
  cobre o que a outra não alcança.
- [`MANUAL.md`, seção 8](<../../../MANUAL.md#8-estrutura-de-arquivos>) —
  árvore completa de arquivos: `.claude/hooks/*.sh`,
  `.claude/hooks/lib/common.sh` (funções compartilhadas),
  `.claude/settings.json` (liga cada script a um evento do Claude
  Code), `scripts/hooks/` (hooks nativos do git, fora do Claude Code),
  `.github/pull_request_template.md`, `.vale.ini`/`.vale/`.
- `modulos/conformidade/decisions/` — uma ADR por escolha real entre
  alternativas feita neste módulo (matcher de gancho, forma de
  comparar caminho, heurística de detecção, onde uma checagem nova
  entra). Pendência de formalizar, aqui, decisões estruturais já
  tomadas antes deste módulo existir como módulo — ver
  [`tasks.md`](tasks.md).

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- aponta pro layout já descrito em `MANUAL.md`, acrescenta só a pasta `decisions/` própria do módulo. | Criação inicial do módulo |
