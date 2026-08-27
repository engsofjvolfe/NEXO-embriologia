# Tasks — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Tasks |
| Versão | 0.1.0 |
| Data | 27-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Lista mutável de pendências só deste módulo. Lida depois de
> `concept.md`/`architecture.md`, antes de mexer em qualquer coisa.
> Atualizada direto conforme resolve. Assim que uma pendência vira
> decisão de verdade, o item aqui vira só um ponteiro pra ADR em
> `decisions/` — nunca um resumo paralelo do que a decisão já diz.
> Pendência resolvida (com ou sem ADR) não é apagada — vira item
> riscado na seção `Resolvidas`.
>
> Cada item segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

## Índice
- [Em aberto](#em-aberto)
- [Resolvidas](#resolvidas)
- [Controle de versão](#controle-de-versão)

## Em aberto

- [ ] **Confirmar de ponta a ponta, numa sessão nova, que os quatro
      mecanismos criados hoje bloqueiam de verdade.**

      *Resumo simples:* tudo foi testado isolado (funções chamadas
      direto, fora do fluxo real de um gancho) — ver
      [findings.md](findings.md) — porque a mesma sessão que escreveu
      o código não consegue ver a correção valendo de verdade (ver
      [pitfalls.md](<pitfalls.md#2026-08-27-configuracao-de-ganchos-nao-recarrega-na-mesma-sessao>)).
      Falta confirmar ao vivo, numa sessão que já carregue os arquivos
      corrigidos desde o início.

      *Detalhe técnico:* quatro pontos a confirmar: (1)
      `pre_mandatory_reading_guard.sh` bloqueia de verdade uma
      ferramenta como `Bash` antes da leitura obrigatória, e libera
      `Read`/`TodoWrite` (decisions/0001); (2) `stop_fact_check.sh` não
      lista mais a pasta principal como "worktree esquecida" por
      engano (decisions/0002); (3) um commit tocando um `.md` com
      esquema embutido contendo `description`/`example` é bloqueado de
      verdade (decisions/0003); (4) o gancho de `Stop` pergunta sobre
      instrução do usuário esquecida quando existir uma de verdade
      (decisions/0004).

- [ ] **Fazer a auditoria linha a linha do `CLAUDE.md`, com checklist
      visível, que ficou combinada pra depois.**

      *Resumo simples:* a varredura feita hoje comparou o `CLAUDE.md`
      contra a tabela de referência do `MANUAL.md` e achou quatro
      lacunas reais — mas não foi, ela mesma, uma releitura linha a
      linha com registro visível de cada regra conferida. Combinado
      explicitamente adiar essa auditoria mais rigorosa.

      *Detalhe técnico:* produzir um checklist -- cada linha do
      `CLAUDE.md` (ou cada regra identificável) contra o mecanismo que
      a cobre (ou a ausência de um), visível de verdade, não só a
      palavra do revisor. Sem desenho ainda.

- [ ] **Formalizar retroativamente, em `decisions/`, as escolhas
      estruturais já tomadas antes deste módulo existir como módulo.**

      *Resumo simples:* os quatro ADRs de hoje
      (decisions/0001 a 0004) cobrem só o trabalho desta sessão -- as
      decisões estruturais mais antigas (por que os hooks nativos do
      git moram em `scripts-hooks/`, não `.githooks/`; o desenho de
      `pre_bash_search_guard.sh`; a divisão de custo do evento `Stop`
      entre fato mecânico e pergunta de julgamento; entre outras) só
      existem narradas em `MANUAL.md`, seção 9 -- nunca como ADR
      própria deste módulo.

      *Detalhe técnico:* candidatas identificadas em `MANUAL.md`,
      seção 9: 9.4 (local dos hooks nativos do git), 9.5 (desenho do
      `pre_bash_search_guard.sh`), 9.6 (arquitetura de custo do
      `Stop`), 9.8 (onde a checagem de leitura obrigatória entra no
      fluxo). As demais entradas da seção 9 (9.1, 9.2, 9.3, 9.7, 9.9,
      9.10, 9.11) são conserto de bug ou erro factual, não escolha
      real entre alternativas -- não precisam de ADR.

- [ ] **Investigar a causa raiz do "modo sem perguntar" que bloqueou o
      gancho de `Stop` durante parte desta sessão.**

      *Resumo simples:* durante boa parte do trabalho de hoje, a
      sessão entrou num modo de permissão que nega qualquer pedido de
      leitura/execução automaticamente, sem perguntar -- isso impediu
      o próprio gancho de `Stop` de terminar a checagem dele várias
      vezes seguidas. Nunca foi investigado por quê, nem se é algo que
      este projeto deveria tratar de alguma forma.

      *Detalhe técnico:* sem desenho, sem responsável -- registrado só
      pra não perder o achado. Pode ser configuração da sessão em si
      (fora do controle deste módulo) ou algo que vale a pena entender
      melhor antes de repetir.

## Resolvidas

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- quatro pendências registradas. | Criação inicial do módulo |
