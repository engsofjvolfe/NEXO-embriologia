# Handoff — NEXO (raiz)

> Resumo curto de "onde o projeto está" e "o que fazer a seguir" —
> só ponteiro, nunca detalhe. Detalhe de verdade mora em `TASKS.md`
> (raiz) e nos documentos do módulo em questão. Modelo pro `handoff.md`
> de cada módulo dentro de `modulos/<nome>/docs/`.

## Índice
- [Estado atual](#estado-atual)
- [Próximo passo](#proximo-passo)

## Estado atual

- Cascata completa do modelo em V do motor aprovada (versão 1.0.0):
  [conceito](<docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
  [requisitos](<docs/docs-VMODEL-visao-geral/2 - requisitos-conceito-geral.md>),
  [especificação](<docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>),
  [projeto arquitetônico](<docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>)
  e [projeto detalhado](<docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>).
- [`modulos/_template/`](modulos/_template/) — molde de módulo, agora
  com controle de versão em todo arquivo.
- [`scripts/`](scripts/) — gancho de git que avisa quando falta subir
  a versão de um documento.
- Módulo [motor](modulos/motor/) criado — desenho de código, contrato
  de dado e decisões de linguagem/framework já registrados.
- Código do módulo motor em andamento, dentro de
  [`modulos/motor/core/`](modulos/motor/core/): pacotes `search` (busca
  aproximada), `hierarchy` (cadastro e navegação hierárquica) e
  `session` (lógica de uma partida — validação, erro, pular, dica,
  encadeamento, pausa/retomada) já escritos — ver
  [modulos/motor/docs/handoff.md](modulos/motor/docs/handoff.md).
- [TASKS.md, Resolvidas](TASKS.md#resolvidas) — pendências já corrigidas.

## Próximo passo

- [TASKS.md, Em aberto](TASKS.md#em-aberto) — pendência aberta atual.
