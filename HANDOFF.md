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
  aproximada), `hierarchy` (cadastro e navegação hierárquica),
  `session` (lógica de uma partida — validação, erro, pular, dica,
  encadeamento, pausa/retomada) e `content` (leitura e validação do
  pacote de conteúdo) já escritos. Projeto Android mínimo do
  módulo [`modulos/motor/app/`](modulos/motor/app/) também já existe,
  sem tela de verdade ainda — ver
  [modulos/motor/docs/handoff.md](modulos/motor/docs/handoff.md).
- Cobertura de teste dos pacotes `search`, `hierarchy`, `session` e
  `content` revisada contra a documentação; cinco lacunas fechadas com
  teste novo, sem divergência de comportamento encontrada — ver
  [modulos/motor/docs/analysis.md](<modulos/motor/docs/analysis.md#2026-08-14-revisao-de-cobertura-de-teste-dos-pacotes-core>).
  Um sexto ponto (comportamento da busca aproximada com termo vazio)
  formalizado como decisão — ver
  [modulos/motor/decisions/0014](<modulos/motor/decisions/0014-busca-aproximada-com-termo-vazio.md>).
- Pacote `connectivity` completo (lado `core` e lado `app`) — lê a
  peça física direto pela antena do celular ou pelo acessório externo
  por Bluetooth; testado por teste automático (`core`) e por
  compilação real (`app`, que ainda não tem ferramenta de teste
  configurada) — ver
  [modulos/motor/decisions/0015](<modulos/motor/decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>)
  a
  [modulos/motor/decisions/0018](<modulos/motor/decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md>).
  Registrado que é a pessoa quem decide usar NFC ou Bluetooth, nunca
  uma escolha automática do aplicativo (decisions/0017).
- Achado: o acessório leitor (Bluetooth + NFC) provavelmente depende
  de homologação da ANATEL antes de poder ser usado ou vendido no
  Brasil — nenhum documento da cascata trata disso; pendência nova,
  ainda sem decisão — ver
  [modulos/motor/docs/tasks.md, Em aberto](<modulos/motor/docs/tasks.md#em-aberto>).
- Registro interno de sessão completado (horário de cada acontecimento,
  sugestão de estudo exibida, pausa distinta de ociosidade — os três já
  exigidos pela Especificação, faltavam no código); pacote `content`
  atualizado pro contrato de dado `2.0.0`; pacotes `summary` e `report`
  escritos, este último dividido entre `core` (dado puro) e `app`
  (desenho do PDF, escrita no aparelho) — ver
  [modulos/motor/docs/handoff.md](modulos/motor/docs/handoff.md).
- [TASKS.md, Resolvidas](TASKS.md#resolvidas) — pendências já corrigidas.

## Próximo passo

- [TASKS.md, Em aberto](TASKS.md#em-aberto) — pendência aberta atual.
