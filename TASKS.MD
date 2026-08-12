# Tasks — NEXO (raiz)

> Lista mutável de pendências gerais do projeto — não de um módulo
> específico (essas ficam em `modulos/<nome>/docs/tasks.md`). Mesma
> regra dos módulos: pendência resolvida não é apagada, vira item
> riscado em "Resolvidas".

## Índice
- [Em aberto](#em-aberto)
- [Resolvidas](#resolvidas)

## Em aberto

- [ ] **Avançar para a Implementação da cascata do motor.**

      *Resumo simples:* os cinco primeiros documentos do módulo
      "Conceito Geral (Motor)" — conceito, requisitos, especificação,
      projeto arquitetônico e projeto detalhado — já estão prontos e
      aprovados (versão 1.0.0 cada, com referências de apoio). Falta
      continuar a cascata a partir da próxima etapa.

      *Detalhe técnico:* próxima etapa é a Implementação, seguindo a
      ordem descrita em `docs/prompt model.txt` (Projeto Detalhado →
      Implementação → Testes). Ver
      `docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md`, seção 9
      ("Itens deferidos à Implementação"), pro que essa próxima etapa
      precisa cobrir.

- [ ] **Desenhar a aparência visual das telas do motor.**

      *Resumo simples:* a cascata de documentação do motor (docs 1 a
      5) especifica o fluxo funcional de cada tela — quais existem, o
      que cada uma mostra (ver
      `docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md`,
      seção 6.6, DA-RET-01 a DA-RET-17) —, mas nunca decide a
      aparência visual (cor, fonte, layout) de nenhuma delas: isso foi
      excluído de propósito, repetidamente, em todos os cinco
      documentos. Diferente de outras exclusões da cascata (como o
      texto legal do termo de consentimento, que virou pendência
      explícita com "rodada própria"), essa nunca virou um item
      rastreado em nenhum documento — por isso mora aqui, na raiz, não
      pertencendo a nenhuma etapa específica do V-Model do motor.

      *Detalhe técnico:* das 17 entradas de tela, 7 são páginas de
      navegação de fato (sessão pausada, navegação, ponto de início,
      configuração da sessão, resultado/relatório, importar conteúdo,
      consentimento) e 10 são estados/variações de conteúdo dentro da
      tela principal de jogo (referência, aguardando tentativa,
      confirmação de acerto, negativa, dica, sugestão de estudo,
      resumo de evento, mensagem de pulo, síntese de cadeia,
      confirmação de saída) — como agrupar esses estados em telas
      físicas é parte do próprio desenho visual pendente. Sem
      responsável definido ainda (designer, ou o próprio usuário).

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
