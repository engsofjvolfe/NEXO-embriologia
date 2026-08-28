# Findings — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Findings |
| Versão | 0.5.0 |
| Data | 28-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Achados confirmados (por leitura de código, teste ao vivo, ou os dois)
> sobre este módulo — cada entrada datada.
>
> Checa se o código já existente bate com o requisito que `concept.md`
> já descreve -- nunca o contrário: um achado aqui não muda o que
> `concept.md` diz que deveria existir, só revela onde o código diverge
> disso (a divergência vira pendência em `tasks.md`). Pode acontecer a
> qualquer momento: antes de `architecture.md` existir, ou depois,
> quando a implementação revela algo não previsto no desenho.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, campo `Confirmado por` com valor fixo, resumo
> simples, depois detalhe técnico.

## Índice
- [Achados](#achados)
- [Controle de versão](#controle-de-versão)

## Achados

### 2026-08-27-funcoes-novas-testadas-isoladamente

**Confirmado por:** teste ao vivo.

*Em resumo:* as três funções/lógicas novas escritas para as ADRs 0001,
0002 e 0003 funcionam como desenhado, testadas fora do fluxo real de
um gancho (sem simular uma chamada de ferramenta), rodando o código
direto contra casos concretos.

*Em detalhe técnico:*
- `first_unread_mandatory_doc` (já existente, reaproveitada pela ADR
  0001): registro vazio devolve o primeiro documento da lista;
  registro com os seis documentos devolve vazio. Testado numa pasta de
  rascunho isolada, com `CLAUDE_PROJECT_DIR` apontando pra lá, sem
  tocar no estado real da sessão.
- `paths_equal` (ADR 0002): três casos -- mesma pasta com letra de
  unidade em caixa diferente (`H:/...` vs `h:/...`, considerados
  iguais); pastas realmente diferentes (consideradas diferentes);
  barra invertida do Windows de um lado só (considerado igual ao
  caminho com barra normal). Os três bateram o esperado.
- Heurística de esquema embutido (ADR 0003, bloco `awk` dentro de
  `pre_commit_hygiene.sh`): bloco com `description` dentro de um
  esquema válido (`type`+`required`+`properties`) detectado; o mesmo
  bloco sem `description` não gerou alarme falso.

### 2026-08-27-sintaxe-e-json-validos

**Confirmado por:** teste ao vivo.

*Em resumo:* os quatro scripts alterados/criados hoje (`lib/common.sh`,
`pre_mandatory_reading_guard.sh`, `pre_commit_hygiene.sh`,
`stop_fact_check.sh`) têm sintaxe de shell válida, e `settings.json`
continua um JSON válido depois da edição.

*Em detalhe técnico:* `bash -n <arquivo>` (checagem de sintaxe sem
executar) rodado nos quatro, um de cada vez -- os quatro devolveram
código de saída 0. `jq empty settings.json` confirmou JSON bem
formado.

### 2026-08-27-restricao-de-read-testada-isoladamente

**Confirmado por:** teste ao vivo.

*Em resumo:* a correção do loophole de `Read` em
`pre_mandatory_reading_guard.sh` ([decisions/0006](<../decisions/0006-read-so-libera-os-seis-documentos-obrigatorios-enquanto-faltar-algum.md>))
funciona como desenhado, testada fora do fluxo real de um gancho (JSON
de entrada fabricado, via stdin, contra uma cópia isolada dos
scripts).

*Em detalhe técnico:* três casos, numa pasta de rascunho isolada, com
`CLAUDE_PROJECT_DIR` apontando pra lá (sem nenhum dos seis documentos
obrigatórios "lidos" ainda, `read-log.txt` vazio): `Read` de um arquivo
fora da lista de seis bloqueou (código de saída 2, mensagem nomeando o
documento faltante); `Read` de um dos seis (comparado pelo nome do
arquivo) passou (código de saída 0); `Bash` continuou bloqueado, igual
ao comportamento já existente antes desta correção -- sem regressão.
Sintaxe de todos os scripts alterados (`pre_mandatory_reading_guard.sh`,
`pre_bash_search_guard.sh`, `stop_fact_check.sh`) conferida com
`bash -n`; `.claude/settings.json` conferido com `jq empty` -- os
quatro sem erro.

### 2026-08-27-ficha-sintese-testada-isoladamente

**Confirmado por:** teste ao vivo.

*Em resumo:* as seis funções da ficha/síntese
([decisions/0012](<../decisions/0012-ficha-sintese-substitui-releitura-do-diario-a-cada-checagem.md>))
e o gancho `session_start_reset.sh` funcionam como desenhado, testados
isoladamente numa pasta de rascunho.

*Em detalhe técnico:*
- `synthesis_age`/`synthesis_fresh`: fato recém-confirmado devolve
  idade 1 (não 0, porque outra ação já tinha avançado o contador);
  depois de 25 ações extras, a mesma checagem com janela de 20 recusa
  o fato (idade 26, fora da janela); fato nunca confirmado devolve
  idade vazia e `synthesis_fresh` recusa; `synthesis_reset` zera tudo.
- `session_start_reset.sh`: diários (`edit-order.log`, `read-log.txt`)
  com conteúdo de uma "sessão anterior" simulada, mais uma ficha com
  um fato -- depois de rodar o gancho, os diários originais não
  existem mais no lugar de sempre, o conteúdo deles aparece intacto,
  com carimbo de data/hora, dentro de `arquivo/`, e a ficha volta pro
  estado vazio inicial.

### 2026-08-28-padrao-de-emoji-incluia-bloco-de-setas-tipograficas

**Confirmado por:** teste ao vivo.

*Em resumo:* o padrão de detecção de emoji continha o bloco Unicode
"Arrows" (`\x{2190}-\x{21FF}`), usado por caracteres tipográficos
comuns como "→" e "↔" -- não emoji. Esse bloco bloqueava, por engano,
qualquer commit tocando texto com essas setas, presentes o tempo todo
na prosa deste projeto (`MANUAL.md`, vários `docs/` de módulo).

*Em detalhe técnico:* achado tentando commitar o trabalho desta
sessão -- confirmado comparando o intervalo contra `emoji-data.txt` do
Unicode Consortium: `\x{2190}-\x{21FF}` é o bloco base "Arrows",
diferente de `\x{2B00}-\x{2BFF}` ("Miscellaneous Symbols and Arrows",
que inclui setas com apresentação de emoji por padrão). Padrão
corrigido em `lib/common.sh` (`EMOJI_PATTERN`): bloco de setas simples
removido, blocos de bandeira (`\x{1F1E6}-\x{1F1FF}`) e técnico diverso
(`\x{2300}-\x{23FF}`, relógio/ampulheta) acrescentados -- cobertura
mais completa, sem incluir blocos que também são pontuação comum
("Geometric Shapes", `©`/`®`/`™`). Testado isoladamente: seta simples
não aciona mais o bloqueio; emoji real, bandeira de país e símbolo de
relógio continuam acionando.

### 2026-08-28-checagens-de-emoji-esquema-licenca-e-antes-depois-no-momento-da-edicao

**Confirmado por:** teste ao vivo.

*Em resumo:* as regras "nunca usar emoji", "esquema de dado é dado
puro", "tabela de cabeçalho carrega linha de Licença" e "documento
nunca versionado não usa linguagem de antes-e-depois" (CLAUDE.md,
Regras gerais) só tinham checagem mecânica no momento do commit
(`pre_commit_hygiene.sh`) -- movidas agora pro momento da própria
edição (`pre_edit_safety.sh`, itens 9 a 12), mesmo padrão já aplicado
antes a outras seis regras nesta sessão.

*Em detalhe técnico:* quatro casos positivos (deve bloquear) e três
casos negativos (não deve bloquear), todos com JSON de entrada
fabricado via stdin, fora do fluxo real de um gancho, numa pasta de
rascunho isolada (`CLAUDE_PROJECT_DIR` apontando pra lá, síntese
pré-populada com os seis documentos obrigatórios "lidos"):
- Emoji num texto novo: bloqueou (código de saída 2). Texto sem emoji:
  passou (código de saída 0).
- Bloco `\`\`\`yaml` embutido com `required`+`type`+`description`:
  bloqueou. Esquema puro (`schemas/*.json` sem `description`/
  `example`): passou.
- Tabela `Campo | Valor` sem linha de Licença: bloqueou. A mesma
  tabela com linha de Licença: passou.
- Texto com "era assim, ficou assim" num arquivo `.md` sem nenhum
  commit no histórico (`git log` vazio pro caminho): bloqueou, citando
  o caminho do arquivo e a regra do CLAUDE.md.

### 2026-08-28-autoportao-de-pre-commit-hygiene-testado-isoladamente

**Confirmado por:** teste ao vivo.

*Em resumo:* o auto-portão acrescentado em `pre_commit_hygiene.sh`
([decisions/0011](<../decisions/0011-auto-portao-contra-falha-aberta-do-filtro-if.md>))
sai (`exit 0`) de verdade quando o comando não contém `git commit`,
testado isoladamente (chamada direta do script via stdin, fora do
fluxo real de um gancho e fora de qualquer camada de segurança da
sessão).

*Em detalhe técnico:* comando fabricado sem `git commit` mas com um
emoji embutido (caso que, antes da correção, teria disparado a
checagem de emoji da mensagem de commit) -- devolveu código de saída 0
sem nenhuma mensagem, confirmando que o auto-portão intercepta antes
de qualquer checagem interna rodar. A instrução equivalente nos dois
ganchos `agent` (revisão de commit, revisão de preview) não pôde ser
testada da mesma forma -- ver
[pitfalls.md](<pitfalls.md#2026-08-27-configuracao-de-ganchos-nao-recarrega-na-mesma-sessao>)
e a pendência em [tasks.md](tasks.md).

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- dois achados registrados. | Criação inicial do módulo |
| 0.2.0 | 27-08-2026 | Achado novo registrado (teste isolado da restrição de Read). | Segunda rodada de correção do sistema de conformidade |
| 0.3.0 | 28-08-2026 | Dois achados novos registrados (checagens de emoji/esquema/licença/antes-depois no momento da edição; auto-portão de pre_commit_hygiene.sh). | Correção da falha aberta do filtro `if` |
| 0.4.0 | 28-08-2026 | Achado novo registrado (teste isolado da ficha/síntese e de session_start_reset.sh), retroativo a trabalho já feito nesta sessão sem registro. | Fechamento da lacuna de documentação da ficha/síntese (decisions/0012) |
| 0.5.0 | 28-08-2026 | Achado novo registrado (padrão de emoji incluía bloco de setas tipográficas, bloqueando commit legítimo). | Correção do padrão de emoji em lib/common.sh |
