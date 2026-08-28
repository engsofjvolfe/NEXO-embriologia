# Analysis — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Analysis |
| Versão | 0.2.0 |
| Data | 28-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Registro datado de como uma investigação foi feita neste módulo — o
> que foi lido, o que foi checado, o raciocínio seguido. Acontece junto
> com `findings.md`, não numa etapa depois — é o relato de como se
> chegou a cada achado. É o único lugar onde narrativa de processo é
> esperada.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, campo `Levou a` com link pro achado gerado (ou
> `ainda sem conclusão`), resumo simples, depois detalhe técnico.

## Índice
- [Investigações](#investigacoes)
- [Controle de versão](#controle-de-versão)

## Investigações

### 2026-08-27-cobertura-do-sistema-de-ganchos-contra-o-claude-md

**Levou a:**
[decisions/0001](<../decisions/0001-cobertura-do-gancho-de-leitura-obrigatoria.md>),
[decisions/0002](<../decisions/0002-comparacao-de-caminho-ignora-maiuscula-e-minuscula.md>),
[decisions/0003](<../decisions/0003-deteccao-de-esquema-embutido-em-documento.md>),
[decisions/0004](<../decisions/0004-checagem-de-instrucoes-do-usuario-no-gancho-de-stop-existente.md>),
[findings.md](findings.md),
[pitfalls.md](pitfalls.md).

*Em resumo:* uma sessão que começou investigando o estado de uma
worktree acabou revelando que a leitura manual obrigatória do
`CLAUDE.md` tinha sido pulada antes de qualquer outra ação -- gatilho
pra checar se o próprio sistema de ganchos deveria ter impedido isso.
Não deveria: o gancho existente só cobre "antes de código", não "antes
de qualquer outra coisa" (texto literal do `CLAUDE.md`). Investigação
ampliada, a pedido direto, pra cobrir o sistema inteiro: reler cada
gancho já existente (pra não duplicar nada), pesquisar a documentação
oficial do Claude Code sobre a sintaxe de `matcher`, e fechar as
lacunas encontradas.

*Em detalhe técnico:*

1. Sessão pedia "investigar o estado da worktree aberta" -- antes de
   qualquer investigação, os seis documentos de leitura manual
   obrigatória (cascata VMODEL + `prompt model.txt`) deveriam ter sido
   lidos; não foram. Apontado diretamente.
2. Os seis documentos lidos por completo. Pergunta seguinte, direta:
   por que nenhum gancho bloqueou a investigação antes dessa leitura?
3. Leitura completa de `MANUAL.md` e de todo script em
   `.claude/hooks/` (nove scripts + `lib/common.sh`), confirmando que
   nenhum duplicava o que seria escrito -- só depois disso, qualquer
   edição.
4. Achado: `first_unread_mandatory_doc` (a função que checa os seis
   documentos) só era chamada dentro de `pre_edit_safety.sh` (antes de
   escrever/editar) e `pre_commit_hygiene.sh` (antes do commit) --
   nunca antes de `Bash`, `Read` avulso, `Grep`, `Glob`, ou qualquer
   outra ferramenta de investigação.
5. Pesquisa direta na documentação oficial do Claude Code (não por
   suposição) sobre o campo `matcher` de um gancho `PreToolUse`:
   confirmado que `"*"`/vazio/omitido cobre toda ferramenta, que
   `tool_name` sempre chega no JSON de entrada independente do
   matcher, que múltiplos ganchos coexistem no mesmo evento, e que não
   existe sintaxe de negação (`"todas menos X"`) -- a forma correta é
   matcher amplo + filtro dentro do próprio script, o mesmo padrão que
   os ganchos já existentes deste projeto já usavam.
6. Gancho novo (`pre_mandatory_reading_guard.sh`) desenhado e escrito
   com essa base -- ver decisions/0001.
7. Durante essa varredura, três lacunas a mais encontradas por leitura
   direta: bug de comparação de caminho maiúscula/minúscula em
   `stop_fact_check.sh` (achado ao ver, na própria resposta do gancho,
   a pasta principal listada por engano como "worktree esquecida");
   checagem de esquema puro nunca cobrindo esquema embutido em
   documento fora de `schemas/*.json` (achado relendo a regra geral do
   `CLAUDE.md` contra o código real da checagem); e nenhum mecanismo
   conferindo se instrução do usuário, dada numa tarefa detalhada,
   ficou sem atender (pedido direto). As três viraram decisions/0002,
   0003 e 0004.
8. Tentativa de testar os ganchos simulando uma chamada de ferramenta
   (JSON fabricado, imitando `PreToolUse`) foi barrada por uma camada
   de supervisão própria desta sessão, tratando esse tipo de simulação
   como caso que merece mais cautela. Ajuste de método: testar a
   lógica interna direto (funções de `lib/common.sh`, chamadas de
   dentro de uma pasta de rascunho isolada, sem simular entrada de
   ferramenta) -- resultado em `findings.md`. Confirmação de ponta a
   ponta, com o gancho de verdade bloqueando uma chamada real, não foi
   possível dentro da mesma sessão -- ver pitfalls.md.
9. Pergunta levantada, no meio do trabalho: onde esse sistema deveria
   morar na documentação do projeto? Não havia módulo formal em
   `modulos/` pra ele -- só `MANUAL.md`, solto na raiz, sem
   `decisions/`, `tasks.md` ou `findings.md` próprios. Três caminhos
   apresentados; escolhido formalizar como módulo (`modulos/conformidade/`),
   com `MANUAL.md` continuando como fonte normativa (mesmo papel que a
   cascata VMODEL cumpre pro módulo `motor`), e as quatro decisões
   acima registradas retroativamente como as primeiras ADRs do módulo
   novo.

### 2026-08-28-falha-aberta-do-filtro-if-e-ganchos-que-nunca-parecem-rodar

**Levou a:**
[decisions/0011](<../decisions/0011-auto-portao-contra-falha-aberta-do-filtro-if.md>),
[findings.md](findings.md),
[pitfalls.md](pitfalls.md).

*Em resumo:* testando a checagem de emoji recém-movida pro momento da
edição, um comando de teste (sem nenhum "git commit") disparou o
bloqueio de emoji de `pre_commit_hygiene.sh` -- gancho que só deveria
rodar em `git commit`. Investigação disso revelou um mecanismo real,
oficial, e corrigível (a falha aberta do filtro `if`), e separou dele
uma segunda observação, mais incerta, sobre ganchos que parecem nunca
executar de verdade nesta sessão.

*Em detalhe técnico:*

1. Reprodução isolada, fora do fluxo real de um gancho: um comando
   Bash trivial, de uma linha, sem emoji e sem `git commit`, não
   disparou nada -- comportamento correto. O mesmo comando com um
   emoji, ainda sem `git commit`, também não disparou nada quando
   testado como comando trivial de uma linha. Só um comando composto,
   de múltiplas linhas, com aspas aninhadas (construindo um JSON de
   teste), disparou o bloqueio de `pre_commit_hygiene.sh` de forma
   repetível.
2. Consulta direta à documentação oficial
   (`code.claude.com/docs/en/hooks`) sobre o campo `if`: confirma que
   ele usa a mesma sintaxe de regra de permissão, e que "the filter
   also fails open, running your hook regardless of pattern, when the
   Bash command can't be parsed" -- exatamente o padrão reproduzido no
   passo 1 (comando composto, aspas aninhadas, difícil de parsear).
3. Corrigido via [decisions/0011](<../decisions/0011-auto-portao-contra-falha-aberta-do-filtro-if.md>):
   auto-portão dentro do próprio script/prompt de cada gancho que usa
   `if` sobre o matcher `Bash`, checando o padrão de novo, sem
   depender só do `if`.
4. Tentativa de confirmar a correção ao vivo (mesmo comando de teste,
   depois da correção salva em disco) reproduziu o mesmo bloqueio,
   com a mesma mensagem exata de antes da correção -- o que não bate
   com "o `if` falhou aberto e o script rodou até o fim", porque o
   script corrigido deveria ter saído mais cedo. Teste decisivo: um
   marcador de diagnóstico (uma linha `echo` gravando num arquivo de
   log, sem bloquear nada) foi acrescentado no topo de
   `pre_commit_hygiene.sh`, e removido depois. Nenhuma chamada de
   ferramenta durante essa checagem -- nem as que supostamente
   dispararam o bloqueio -- deixou esse marcador no arquivo de log.
5. Checagem adicional: `edit-order.log`, escrito por
   `post_edit_track.sh` (gancho `PostToolUse`, matcher `Write|Edit`,
   sem `if` nenhum -- não sujeito à falha do passo 2) a cada edição
   real, não existe em disco, apesar de dezenas de edições reais nesta
   mesma sessão.
6. Conclusão possível, mas não certeza: os dois achados acima (nenhum
   marcador de diagnóstico gravado, `edit-order.log` nunca criado)
   sugerem que os ganchos configurados em `settings.json` podem não
   ter rodado de verdade em nenhum momento desta sessão -- reforçando,
   com uma evidência mais direta, a armadilha já registrada em
   [pitfalls.md](<pitfalls.md#2026-08-27-configuracao-de-ganchos-nao-recarrega-na-mesma-sessao>)
   (configuração de gancho não recarrega na mesma sessão que a edita).
   As mensagens de bloqueio recebidas ao vivo durante esta sessão
   (formato "PreToolUse:Bash hook error: [...]", e também "Agent hook
   condition was not met: ...") não puderam, portanto, ser atribuídas
   com certeza aos scripts reais deste módulo -- podem vir de uma
   camada de segurança separada (o classificador do "Auto Mode",
   mencionado em erro à parte nesta mesma sessão: "claude-sonnet-5 is
   temporarily unavailable, so auto mode cannot determine the
   safety..."), que aparenta ler o conteúdo dos scripts de gancho (as
   mensagens citam texto real de `block()`) sem necessariamente
   executá-los. Sem fonte oficial que confirme isso por escrito --
   registrado só pela observação direta, ao vivo, nesta sessão, com a
   incerteza explícita. Não muda a correção do item 3 (o auto-portão é
   correto e vale independente da causa exata do sintoma que motivou
   procurá-lo), só a certeza sobre O QUE, exatamente, bloqueou os
   comandos de teste vistos ao vivo nesta sessão.

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- uma investigação registrada. | Criação inicial do módulo |
| 0.2.0 | 28-08-2026 | Investigação nova registrada (falha aberta do filtro `if`, e observação separada sobre ganchos que parecem nunca rodar nesta sessão). | Teste isolado das checagens de emoji/esquema/licença/antes-e-depois movidas pro momento da edição |
