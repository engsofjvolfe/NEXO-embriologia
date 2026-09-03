# Tasks — NEXO (raiz)

> Lista mutável de pendências gerais do projeto — não de um módulo
> específico (essas ficam em `modulos/<nome>/docs/tasks.md`). Mesma
> regra dos módulos: pendência resolvida não é apagada, vira item
> riscado em "Resolvidas".

## Índice
- [Em aberto](#em-aberto)
- [Resolvidas](#resolvidas)

## Em aberto

- [ ] **Continuar a implementação do módulo motor.**

      *Resumo simples:* o módulo já tem desenho de código, contrato de
      dado, decisões de linguagem e o desenho visual das telas
      (protótipo navegável já avaliado) — falta o código real das
      telas e os testes.

      *Detalhe técnico:* ver
      [modulos/motor/docs/tasks.md, Em aberto](modulos/motor/docs/tasks.md#em-aberto)
      pra lista completa.

- [ ] **Criar uma ferramenta de autoria de conteúdo amigável, sem
      exigir programação.**

      *Resumo simples:* o [README.md](README.md) da raiz já promete
      que "quem monta [...] conteúdo (um professor, por exemplo)"
      nunca precisa "escrever qualquer linha de código" — hoje isso
      não é verdade: montar um pacote de conteúdo válido exige
      escrever o JSON à mão ou com um editor de texto simples.

      *Detalhe técnico:* o
      [Projeto Detalhado](<docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>)
      já classifica essa ferramenta como fora do escopo da cascata do
      motor, de propósito (§2.2, Premissas seção 8) — não é módulo
      `motor`, é ferramenta separada, sobre o contrato de dado já
      fixado em PD-IMP-01. Sem desenho ainda, nem worktree própria.
      Nota acrescentada durante a escrita do pacote `content`
      ([modulos/motor/decisions/0013](<modulos/motor/decisions/0013-desenho-do-pacote-content.md>)):
      essa ferramenta deveria impedir, no momento da criação, o tipo de
      erro que hoje só é percebido depois, na importação (nome
      repetido, posição de tema/evento pulando número) — sugerindo o
      próximo número automaticamente, por exemplo, em vez de deixar a
      pessoa digitar à mão. Ideia levantada, ainda não decidida: em vez
      de (ou além de) um aplicativo de autoria completo, um site
      simples, sem servidor próprio (hospedado num serviço gratuito de
      arquivo estático, ex.: Netlify), que rode a mesma validação já
      escrita em `content` e deixe a pessoa importar um pacote ali só
      pra checar e corrigir erros, antes de instalar de verdade no
      aplicativo — cada opção com seu próprio custo de construção,
      nenhuma escolhida ainda. Sem relação com o desenho desta
      ferramenta em si (assunto diferente: aqui é sobre montar e
      validar um pacote de conteúdo, não sobre desenhar tela), mas
      [modulos/motor/decisions/0036](<modulos/motor/decisions/0036-ferramenta-e-fidelidade-do-prototipo-navegavel.md>)
      já comparou, com fonte oficial, o mesmo tipo de escolha de
      ferramenta cogitada aqui pro "site simples" (página autocontida
      — HTML/CSS/JavaScript, sem servidor, sem conta externa, sem
      custo) — precedente a conferir quando esta pendência for
      decidida de verdade, nunca uma resposta já pronta pra ela.

- [ ] **Encontrar forma de importar automaticamente os seis documentos
      com espaço no nome (cascata VMODEL numerada + `prompt
      model.txt`) pro `CLAUDE.md`.**

      *Resumo simples:* hoje esses seis arquivos não entram na
      importação automática do `CLAUDE.md` — precisam ser lidos
      manualmente, toda sessão, por instrução explícita no topo do
      documento.

      *Detalhe técnico:* confirmado por teste ao vivo (sessão nova,
      pergunta direta sobre o que carregou no contexto) que espaço no
      nome do arquivo quebra o mecanismo de importação `@caminho` do
      Claude Code — testado em três formatos diferentes (linha solta,
      parágrafo separado, lista markdown) e com codificação `%20`,
      todos falharam. Comportamento não coberto pela documentação
      oficial (confirmado por consulta direta a ela). Renomear os
      arquivos pra tirar o espaço foi avaliado e descartado: os cinco
      documentos da cascata se citam entre si dentro do próprio texto
      já aprovado, que é imutável (ver
      [docs/docs-VMODEL-visao-geral/README.md](<docs/docs-VMODEL-visao-geral/README.md>)).

- [ ] **Fechar as lacunas restantes do sistema de conformidade do
      `CLAUDE.md`.**

      *Resumo simples:* o sistema de hooks que trava as regras do
      `CLAUDE.md` automaticamente foi formalizado como módulo próprio
      — várias lacunas reais já fechadas em múltiplas rodadas (entre
      elas: o formato de resposta que os ganchos revisados por IA
      usavam nunca era reconhecido pelo Claude Code como bloqueio de
      verdade; uma auditoria linha a linha do `CLAUDE.md` inteiro já
      foi feita, virou o `MANUAL.md` na raiz; dez regras adicionais
      movidas do momento do commit pro momento da própria edição; uma
      falha real do Claude Code, no filtro `if`, corrigida com
      auto-portão nos ganchos afetados; a causa raiz de os ganchos
      revisados por IA nunca bloquearem de verdade confirmada com fonte
      oficial -- formato de resposta errado, mais um deles ficando sem
      acesso a ferramenta enquanto a sessão está no modo automático --
      dois desses ganchos removidos, os outros quatro mantidos com o
      formato corrigido), mas ainda falta confirmar tudo ao vivo numa
      sessão nova -- nenhuma correção feita na mesma sessão que a
      escreveu pôde ser vista bloqueando de verdade.

      *Detalhe técnico:* ver
      [modulos/conformidade/docs/tasks.md, Em aberto](modulos/conformidade/docs/tasks.md#em-aberto)
      pra lista completa.

- [ ] **Desenhar e construir o preview isolado do projeto.**

      *Resumo simples:* `modulos/preview/`, citado no `CLAUDE.md`
      como se já existisse, está vazio — nenhum arquivo, nenhuma
      instrução.

      *Detalhe técnico:* o pré-requisito direto desta pendência (existir
      algo real pra testar num emulador) já foi resolvido — ver
      [modulos/motor/docs/tasks.md, Resolvidas](modulos/motor/docs/tasks.md#resolvidas),
      "Escrever o esqueleto mínimo do módulo `app`". Falta agora
      desenhar e construir o preview isolado em si — ainda sem
      trabalho iniciado.

## Resolvidas

- [x] **Terminar a cascata do modelo em V no visão geral do motor
      (conceito, requisitos, especificação, projeto arquitetônico).**
      Resolvido — ver
      [1 - documento-de-conceito-geral.md](<docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
      [2 - requisitos-conceito-geral.md](<docs/docs-VMODEL-visao-geral/2 - requisitos-conceito-geral.md>),
      [3 - especificacao-conceito-geral.md](<docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>)
      e [4 - projeto-arquitetonico.md](<docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>).
- [x] **Avançar para o Projeto Detalhado da cascata do motor.**
      Resolvido — ver
      [5 - projeto-detalhado.md](<docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>).
- [x] **Aprovar e versionar (1.0.0) a cascata completa do modelo em V
      do motor.** Resolvido — os cinco documentos (conceito,
      requisitos, especificação, projeto arquitetônico e projeto
      detalhado) passaram de "criação inicial" (0.1.0) para
      "Aprovado" (1.0.0).
- [x] **Formalizar controle de versão no molde de módulo.** Resolvido
      — [`modulos/_template/`](modulos/_template/) agora leva, em todo
      arquivo, o campo Versão (na tabela de cabeçalho) e a tabela
      Controle de versão (no rodapé), no mesmo formato já usado nos
      cinco documentos da cascata do motor.
- [x] **Automatizar o aviso de versão desatualizada.** Resolvido — ver
      [`scripts/hooks/pre-commit`](scripts/hooks/pre-commit) e
      [`scripts/README.md`](scripts/README.md): gancho de git que
      bloqueia o commit quando um documento com campo de versão
      reconhecido muda de conteúdo sem a versão acompanhar.
- [x] **Criar o módulo "motor" em `modulos/`.** Resolvido — desenho
      de código, contrato de dado e decisões de linguagem/framework
      já registrados, ver [modulos/motor/](modulos/motor/).
