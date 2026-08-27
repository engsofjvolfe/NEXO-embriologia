# Analysis — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Analysis |
| Versão | 0.1.0 |
| Data | 27-08-2026 |
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

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- uma investigação registrada. | Criação inicial do módulo |
