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
      dado e decisões de linguagem — falta o código em si, os testes,
      e o desenho visual das telas.

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

- [ ] **Desenhar e construir o preview isolado do projeto.**

      *Resumo simples:* `modulos/preview/`, citado no `CLAUDE.md`
      como se já existisse, está vazio — nenhum arquivo, nenhuma
      instrução.

      *Detalhe técnico:* não faz sentido desenhar antes de existir
      algo real pra testar num emulador — ver
      [modulos/motor/docs/tasks.md, "Escrever o esqueleto mínimo do
      módulo `app`"](modulos/motor/docs/tasks.md#em-aberto), que é
      pré-requisito direto desta pendência.

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
