# Pitfalls — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Pitfalls |
| Versão | 0.1.0 |
| Data | 27-08-2026 |
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

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- uma armadilha registrada. | Criação inicial do módulo |
