# Pitfalls — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Pitfalls |
| Versão | 0.2.0 |
| Data | 28-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Comportamento não óbvio de ferramenta/mecanismo usado só neste módulo
> — pra não redescobrir o mesmo problema depois. Registrado conforme
> aparece, tipicamente durante a implementação (quando o código encontra
> o comportamento real de ferramenta/ambiente). Raramente muda; se mudar
> (versão nova de dependência, por exemplo), mesma regra de
> `findings.md`: entrada nova, não reescrita.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, resumo simples, depois detalhe técnico.

## Índice
- [Armadilhas](#armadilhas)
- [Controle de versão](#controle-de-versão)

## Armadilhas

### 2026-08-27-configuracao-de-ganchos-nao-recarrega-na-mesma-sessao

*Em resumo:* uma correção salva em disco, dentro de um script de
gancho, não passa a valer imediatamente pra sessão que está com esse
arquivo aberto -- a configuração de ganchos (`settings.json` e o
conteúdo dos scripts que ela liga) parece ser carregada uma vez, no
início da sessão, não recarregada a cada chamada de ferramenta.

*Em detalhe técnico:* depois de corrigir `stop_fact_check.sh` (ver
[decisions/0002](<../decisions/0002-comparacao-de-caminho-ignora-maiuscula-e-minuscula.md>))
numa worktree criada no meio da sessão, o aviso de "worktree já
mesclada" continuou aparecendo pra pasta principal no formato antigo
(sem a correção), em respostas seguintes da mesma sessão -- mesmo com
o arquivo corrigido já salvo em disco, no caminho que
`${CLAUDE_PROJECT_DIR}` deveria apontar. Consequência prática:
qualquer mudança em `.claude/hooks/`/`settings.json` só é confirmável
de ponta a ponta numa sessão nova, começada depois da mudança já estar
no lugar onde essa sessão nova vai rodar (por exemplo, depois do merge
em `develop`) -- nunca na mesma sessão que fez a mudança. Nenhuma fonte
oficial consultada confirma esse comportamento por escrito -- registrado
aqui só pela observação direta, ao vivo, nesta sessão.

### 2026-08-28-filtro-if-falha-aberto-em-comando-nao-parseavel

*Em resumo:* o campo `if` de um gancho `PreToolUse`/`PostToolUse`
sobre o matcher `Bash` (ex.: `"if": "Bash(git commit *)"`) não
restringe com garantia quando o comando é composto, com aspas
aninhadas ou heredoc -- nesses casos, documentação oficial confirma
que o filtro roda o gancho mesmo sem o padrão bater ("fails open").
Um teste envolvendo JSON fabricado com aspas aninhadas (padrão comum
ao testar os próprios ganchos deste módulo) é exatamente o tipo de
comando que dispara essa falha.

*Em detalhe técnico:* fonte oficial,
`code.claude.com/docs/en/hooks`: "the filter also fails open, running
your hook regardless of pattern, when the Bash command can't be
parsed". Mitigação adotada:
[decisions/0011](<../decisions/0011-auto-portao-contra-falha-aberta-do-filtro-if.md>)
-- cada gancho que depende de `if` sobre `Bash` confirma o próprio
padrão de novo, no início do script/prompt, sem depender só do `if`.

### 2026-08-28-mensagem-de-bloqueio-ao-vivo-nao-prova-execucao-real-do-gancho

*Em resumo:* uma mensagem de bloqueio recebida ao vivo, durante uma
chamada de ferramenta (formato "PreToolUse:Bash hook error: [...]" ou
"Agent hook condition was not met: ..."), mesmo citando o caminho de
um script real e um texto de `block()` que só existe nesse script, não
é prova, sozinha, de que aquele script rodou de verdade -- pode vir de
uma camada de segurança separada (o classificador do "Auto Mode") que
aparenta ler o conteúdo do script sem executá-lo. A forma de checar
com confiança: um marcador de gravação (log/arquivo de estado) que só
existe se o script realmente rodou até aquele ponto -- não a palavra
da mensagem em si.

*Em detalhe técnico:* achado ao investigar por que uma correção salva
em `pre_commit_hygiene.sh` não parecia valer, mesmo depois de
confirmada em disco -- ver
[analysis.md](<analysis.md#2026-08-28-falha-aberta-do-filtro-if-e-ganchos-que-nunca-parecem-rodar>).
Sem fonte oficial que confirme isso por escrito -- registrado só pela
observação direta, ao vivo, nesta sessão, com a incerteza sobre a
causa exata explícita na entrada de `analysis.md` correspondente.

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- uma armadilha registrada. | Criação inicial do módulo |
| 0.2.0 | 28-08-2026 | Duas armadilhas novas registradas (falha aberta do filtro `if`; mensagem de bloqueio ao vivo não prova execução real do gancho). | Investigação da checagem de emoji disparando fora de contexto |
