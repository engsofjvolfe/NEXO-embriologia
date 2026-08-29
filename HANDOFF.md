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
  por Bluetooth; testado por teste automático dos dois lados (`core`
  desde o início, `app` desde 17-08-2026) — ver
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
- `ViewModel` da sessão escrito (`app/ui/SessionUiState.kt`,
  `SessionViewModel.kt`) — liga leitura de peça, lógica de sessão e o
  que a tela mostra; correção registrada numa decisão anterior que
  tinha adiado o conteúdo do estado sem necessidade — ver
  [modulos/motor/docs/handoff.md](modulos/motor/docs/handoff.md).
- `CLAUDE.md` reorganizado — leitura obrigatória movida pro início do
  documento; dezesseis documentos (`TASKS.md`, `HANDOFF.md`,
  `README.md`, `modulos/README.md`, README da cascata VMODEL,
  `_template/` inteiro, `handoff.md`/`concept.md` do módulo motor)
  agora carregam automaticamente em toda sessão, confirmado por teste
  ao vivo; os cinco documentos numerados da cascata e o `prompt
  model.txt` continuam como leitura manual obrigatória — ver
  [TASKS.md, Em aberto](<TASKS.md#em-aberto>).
- Geração do relatório de saída antes de apagar a sessão pausada
  resolvida (EI-PAU-04) — ver
  [modulos/motor/docs/handoff.md](modulos/motor/docs/handoff.md).
- Gatilho de ociosidade resolvido (EI-PAU-06) — ver
  [modulos/motor/docs/handoff.md](modulos/motor/docs/handoff.md).
- Ferramenta de teste decidida para os cinco pontos do módulo `app`
  ainda sem comportamento checado (Bluetooth, NFC, escrita/
  compartilhamento de relatório, `ViewModel`, PDF) — escrita dos
  testes em si segue como pendência própria — ver
  [modulos/motor/decisions/0025](<modulos/motor/decisions/0025-ferramenta-de-teste-do-modulo-app.md>).
- Os quatro pontos do módulo `app` que não exigem aparelho ou emulador
  (Bluetooth, NFC, escrita/compartilhamento de relatório no caminho
  novo, `ViewModel`) já testados de verdade — só o desenho do PDF e o
  caminho antigo de escrita de relatório (Android 7 a 9) seguem como
  pendência, por exigirem aparelho. O teste do `ViewModel` (a peça que
  liga leitura de peça, sessão e tela) tinha ficado bloqueado por
  faltar decidir a forma exata de duas partes internas do código —
  resolvido em
  [modulos/motor/decisions/0026](<modulos/motor/decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md>);
  ao escrever o teste só a partir do que ficou decidido, sem abrir
  código, uma divergência real apareceu (o código já usava uma forma
  melhor que a decidida — sem duplicar dado que já existe em outro
  lugar) — corrigida via
  [modulos/motor/decisions/0027](<modulos/motor/decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md>),
  não revertendo o código pra bater com a decisão errada.
- Mecanismo que combina o recorte de temas com o recorte de eventos de
  cada tema, pra uma sessão que atravessa mais de um assunto, decidido
  e já escrito, com teste rodado de verdade — ver
  [modulos/motor/decisions/0028](<modulos/motor/decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md>).
- Responsabilidade pela aparência visual das telas do motor decidida —
  mora dentro do próprio módulo, numa casca única compartilhada por
  toda instância, nunca num módulo separado — ver
  [modulos/motor/decisions/0029](<modulos/motor/decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>).
  O desenho de fato dessa aparência (cor, fonte, layout de cada tela)
  segue como pendência própria.
- Padrão de navegação hierárquica das telas do motor (instância, tema,
  evento) decidido — expansão em acordeão, igual em celular e tablet,
  ver
  [modulos/motor/decisions/0030](<modulos/motor/decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>).
- Jetpack Compose decidido como ferramenta de desenho de tela do
  módulo `app`, destravando o desenho visual das telas — ver
  [modulos/motor/decisions/0031](<modulos/motor/decisions/0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md>).
- Sistema de conformidade do `CLAUDE.md` instalado: hooks do Claude
  Code (`.claude/hooks/`, `.claude/settings.json`) e do git nativo
  (`scripts/hooks/commit-msg`, `pre-push`, `pre-rebase`, ao lado do
  `pre-commit` já existente) transformam regras do `CLAUDE.md` em
  travas automáticas -- leitura obrigatória antes de editar, worktree
  própria, formato de commit, nunca reescrever `develop`/`main`, entre
  outras. Ver `MANUAL.md` da instalação (raiz do repositório) pra
  referência completa; regra que muda no `CLAUDE.md` sem hook
  correspondente agora recebe aviso automático no commit.
- Sistema de conformidade do `CLAUDE.md` formalizado como módulo
  próprio ([modulos/conformidade](modulos/conformidade/)), com
  `MANUAL.md` continuando como fonte normativa — quatro lacunas reais
  fechadas (leitura obrigatória cobrindo qualquer ferramenta, não só
  escrita; comparação de caminho de worktree corrigida pro Windows;
  checagem de esquema puro cobrindo esquema embutido em documento;
  revisor do fim da resposta conferindo instrução do usuário não
  atendida) — ver
  [modulos/conformidade/docs/handoff.md](modulos/conformidade/docs/handoff.md).
- Achada e corrigida a causa raiz de os ganchos revisados por IA
  (revisão de commit, revisão de preview, três checagens do fim da
  resposta) nunca bloquearem de verdade: o formato de resposta que
  pediam não era reconhecido pelo Claude Code como decisão de bloqueio
  — ver
  [modulos/conformidade/docs/handoff.md](modulos/conformidade/docs/handoff.md).
- Mais quatro regras do `CLAUDE.md` (nunca emoji, esquema é dado puro,
  linha de Licença em tabela, nunca "antes e depois" em documento nunca
  versionado) movidas do momento do commit pro momento da própria
  edição; achada e corrigida uma falha real do Claude Code (filtro
  `if` roda um gancho mesmo sem bater o padrão, em comando complexo
  demais pra ser interpretado) que fazia o revisor de commit disparar
  fora de contexto; sistema de ficha/síntese (resumo compacto do estado
  da sessão, substituindo releitura do diário inteiro a cada checagem)
  documentado retroativamente — ver
  [modulos/conformidade/docs/handoff.md](modulos/conformidade/docs/handoff.md).
- Bloqueio real dos ganchos de conformidade corrigido: `SessionStart`
  não apaga mais a ficha (registro de leitura/edição da sessão)
  durante o resumo automático de contexto; a ficha se recupera sozinha
  de um estado corrompido; `AUTORIZO-TRAVA` não dispara mais por um
  texto de exemplo citado sem motivo real; a leitura manual obrigatória
  dos seis documentos passa a expirar (frescor uniforme, em vez de
  permanente); lacuna de trava mecânica em `pitfalls.md`/`findings.md`/
  `decisions/` fechada — ver
  [modulos/conformidade/docs/handoff.md](modulos/conformidade/docs/handoff.md).
- Causa raiz encontrada, com fonte oficial, de por que os ganchos
  decididos por IA no sistema de conformidade não bloqueavam de
  verdade: formato de resposta errado em todos, e um deles ficando sem
  acesso a ferramenta enquanto a sessão está no modo automático. Dois
  ganchos removidos (revisão de commit, revisão de início do teste no
  preview), substituídos por checagem mecânica; os outros quatro
  mantidos, com o formato corrigido — ver
  [modulos/conformidade/docs/handoff.md](modulos/conformidade/docs/handoff.md).
- [TASKS.md, Resolvidas](TASKS.md#resolvidas) — pendências já corrigidas.

## Próximo passo

- [TASKS.md, Em aberto](TASKS.md#em-aberto) — pendência aberta atual.
