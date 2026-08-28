# Tasks — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Tasks |
| Versão | 0.5.0 |
| Data | 28-08-2026 |
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

- [ ] **Confirmar de ponta a ponta, numa sessão nova, que os cinco
      mecanismos corrigidos na segunda rodada de hoje bloqueiam de
      verdade.**

      *Resumo simples:* uma queixa direta -- "o sistema ainda deixa
      escolher se segue a trava ou não" -- revelou que boa parte do
      sistema nunca bloqueava de verdade: o formato de resposta que os
      ganchos revisados por IA usavam não é reconhecido pelo Claude
      Code como decisão de bloqueio, então eles só "avisavam". Cinco
      pontos corrigidos, mesma limitação da pendência acima (sessão
      que corrige não consegue confirmar o bloqueio ao vivo).

      *Detalhe técnico:* cinco pontos a confirmar: (1) o gancho `agent`
      de revisão de commit (`if: Bash(git commit *)`) bloqueia um
      commit de verdade quando encontra fato faltando, usando o formato
      `hookSpecificOutput.permissionDecision` (achado raiz, ver
      MANUAL.md 9.12); (2) o mesmo formato bloqueia o gancho de revisão
      de preview; (3) `pre_mandatory_reading_guard.sh` bloqueia `Read`
      de um arquivo fora da lista de seis, enquanto sobrar algum deles
      por ler (decisions/0006); (4) `stop_fact_check.sh` bloqueia a
      resposta de terminar (`exit 2`) quando encontra um dos três fatos
      mecânicos, e libera com `AUTORIZO-TRAVA` (decisions/0007); (5) os
      três ganchos de julgamento do evento `Stop` -- este quinto ponto
      é o de confiança mais baixa dos cinco: mesmo se `decision:
      "continue"` não bloquear (documentação marca como experimental,
      sem exemplo confirmado -- ver decisions/0008), os outros quatro
      continuam valendo.

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

      *Detalhe técnico:* pista nova, ainda não certeza: investigação
      registrada em
      [analysis.md](<analysis.md#2026-08-28-falha-aberta-do-filtro-if-e-ganchos-que-nunca-parecem-rodar>)
      encontrou evidência de que mensagens de bloqueio recebidas ao
      vivo (formato "Agent hook condition was not met") podem vir de
      uma camada separada (classificador do "Auto Mode"), não dos
      scripts reais deste módulo -- mesma família de sintoma desta
      pendência, ainda sem confirmação definitiva. Sem desenho, sem
      responsável -- registrado só pra não perder o achado. Pode ser
      configuração da sessão em si (fora do controle deste módulo) ou
      algo que vale a pena entender melhor antes de repetir.

- [x] **Confirmar ao vivo, numa sessão nova, a ficha/síntese
      ([decisions/0012](<../decisions/0012-ficha-sintese-substitui-releitura-do-diario-a-cada-checagem.md>)).**
      Resolvido, embora não do jeito esperado -- ver
      [Resolvidas](#resolvidas).

- [ ] **Confirmar ao vivo, numa sessão nova, o auto-portão contra
      falha aberta do filtro `if` nos dois ganchos `agent` (revisão de
      commit, revisão de preview).**

      *Resumo simples:* [decisions/0011](<../decisions/0011-auto-portao-contra-falha-aberta-do-filtro-if.md>)
      corrige um comportamento real e documentado oficialmente (o
      filtro `if` roda o gancho mesmo sem bater o padrão, quando o
      comando não é parseável) -- a versão em script
      (`pre_commit_hygiene.sh`) já foi testada isoladamente, mas a
      instrução equivalente nos dois ganchos `agent` não tem como ser
      testada fora do fluxo real (não são script, são prompt de IA).

      *Detalhe técnico:* confirmar, numa sessão nova (depois do
      merge), que um comando Bash qualquer -- sem `git commit` real,
      mas complexo o bastante pra disparar a falha aberta do `if` --
      não aciona mais o julgamento completo do gancho de revisão de
      commit, respondendo `allow` direto. Mesmo teste, adaptado, pro
      gancho de revisão de preview.

- [ ] **Confirmar de ponta a ponta, numa sessão nova, os mecanismos
      corrigidos nesta rodada (bloqueio real dos ganchos de
      conformidade).**

      *Resumo simples:* mesma limitação de toda esta lista -- a
      sessão que corrige não consegue ver a correção valendo de
      verdade, porque os ganchos rodam a partir da pasta principal do
      repositório, não da worktree onde a correção foi escrita. Cinco
      pontos corrigidos nesta rodada, ainda sem confirmação numa
      sessão limpa, que já carregue os arquivos corrigidos desde o
      início.

      *Detalhe técnico:* cinco pontos a confirmar: (1) `SessionStart`
      não apaga mais a ficha na compactação (só em
      início/retomada/limpeza de verdade); (2) a ficha se recupera
      sozinha de um estado corrompido (arquivo vazio ou JSON inválido),
      sem precisar de intervenção manual; (3) `AUTORIZO-TRAVA` não
      dispara mais por um texto de exemplo citado, só por um motivo de
      fato escrito; (4) a leitura manual obrigatória dos seis
      documentos expira depois de 20 ações, exigindo releitura; (5) os
      itens 13 (achado sem registro) e 14/15 (escolha sem ADR) de
      `pre_edit_safety.sh` sinalizam nos casos certos e destravam pelas
      frases de confirmação específicas. Ver
      [findings.md](<findings.md#2026-08-28-sessionstart-sem-matcher-reseta-a-ficha-na-compactacao>)
      em diante, e
      [decisions/0013](<../decisions/0013-frescor-uniforme-de-leitura-substitui-permanencia.md>).

## Resolvidas

- [x] **Confirmar ao vivo, numa sessão nova, a ficha/síntese
      ([decisions/0012](<../decisions/0012-ficha-sintese-substitui-releitura-do-diario-a-cada-checagem.md>)).**
      Resolvido, embora não do jeito planejado (nenhum teste isolado
      de propósito) -- a própria sessão que corrigiu o resto deste
      módulo ficou repetidamente bloqueada pela ficha travando de
      verdade (leitura manual obrigatória reaparecendo, mesmo depois
      de já satisfeita), confirmando ao vivo que o bloqueio funciona.
      Essa mesma experiência revelou dois defeitos novos na própria
      ficha, corrigidos na mesma rodada -- ver
      [findings.md](<findings.md#2026-08-28-sessionstart-sem-matcher-reseta-a-ficha-na-compactacao>)
      e
      [findings.md](<findings.md#2026-08-28-corrupcao-e-perda-de-fato-na-ficha-por-escrita-concorrente>).

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- quatro pendências registradas. | Criação inicial do módulo |
| 0.2.0 | 27-08-2026 | Pendência nova acrescentada: confirmação de ponta a ponta dos cinco mecanismos corrigidos na segunda rodada (decisions/0006 a 0008). | Correção do formato de bloqueio que nunca era reconhecido pelo Claude Code |
| 0.3.0 | 28-08-2026 | Pendência de investigação da causa raiz do "modo sem perguntar" atualizada com pista nova; pendência nova acrescentada (confirmação do auto-portão de decisions/0011 nos dois ganchos agent). | Correção da falha aberta do filtro `if` |
| 0.4.0 | 28-08-2026 | Pendência nova acrescentada (confirmação da ficha/síntese, decisions/0012, numa sessão nova). | Fechamento da lacuna de documentação da ficha/síntese |
| 0.5.0 | 28-08-2026 | Pendência de confirmação da ficha/síntese resolvida (confirmada ao vivo por acidente, revelando dois defeitos novos, corrigidos na mesma rodada); pendência nova acrescentada (confirmação de ponta a ponta dos mecanismos desta rodada). | Correção do bloqueio real dos ganchos de conformidade |
