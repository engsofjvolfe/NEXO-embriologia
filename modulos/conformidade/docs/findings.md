# Findings — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Findings |
| Versão | 0.6.0 |
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
> Um achado, uma vez escrito, não é apagado nem reescrito se deixar de
> valer depois (mudança real de código, por exemplo) — ganha uma entrada
> nova, datada, dizendo o que mudou. Igual ADR: acrescenta, não
> reescreve por cima.
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

### 2026-08-28-sessionstart-sem-matcher-reseta-a-ficha-na-compactacao

**Confirmado por:** teste ao vivo.

*Em resumo:* o bloco de `SessionStart` que roda `session_start_reset.sh`
não tinha `matcher`, então disparava em todo sub-evento -- inclusive
`compact` (resumo automático do histórico numa conversa longa), não só
no início/retomada/limpeza de verdade da sessão. Como
`session_start_reset.sh` apaga o registro inteiro de leitura/edição da
sessão (a ficha, `synthesis.json`), isso apagava o progresso no meio
da conversa.

*Em detalhe técnico:* reproduzido ao vivo, na mesma sessão que
descobriu o defeito: a leitura manual obrigatória (os seis documentos
da cascata V-Model) reapareceu como bloqueio, repetidas vezes, depois
de já ter sido satisfeita e confirmada mais cedo na mesma conversa --
sem nenhuma ação do lado de quem estava conduzindo a sessão que
justificasse isso. Corrigido acrescentando `"matcher":
"startup|resume|clear"` ao bloco em `.claude/settings.json` -- o
segundo bloco do array `SessionStart`, que roda `session_start_reset.sh`
e `session_start_import_check.sh`. `jq empty .claude/settings.json`
confirmou JSON bem formado depois da mudança.

### 2026-08-28-corrupcao-e-perda-de-fato-na-ficha-por-escrita-concorrente

**Confirmado por:** teste ao vivo.

*Em resumo:* independente do achado acima, um lote de leituras em
paralelo (vários `Read` na mesma resposta) truncou a ficha
(`synthesis.json`) pra 0 bytes -- e, uma vez vazia, ela nunca se
recuperava sozinha, travando a leitura manual obrigatória de forma
permanente, mesmo depois de reler os seis documentos várias vezes.

*Em detalhe técnico:* `synthesis_bump`/`synthesis_set`
(`lib/common.sh`) escreviam sempre no mesmo nome de arquivo
intermediário fixo (`"${SYNTHESIS_FILE}.tmp"`) antes de mover por cima
do arquivo real -- duas chamadas concorrentes (cada `Read` em paralelo
dispara seu próprio `post_read_track.sh`) competem pelo mesmo arquivo
intermediário, podendo truncá-lo. Uma vez truncado, `synthesis_init`
(`[[ -f "$SYNTHESIS_FILE" ]]`) só checava existência, não conteúdo --
um arquivo vazio "existe", então nunca era reconstruído. E `jq`, por
padrão, trata entrada vazia como sucesso silencioso (zero valores de
saída, sem erro nenhum) -- toda leitura seguinte lia o vazio, escrevia
o vazio de novo, num ciclo que nunca se corrigia sozinho. Confirmado
ao vivo: `.claude/hooks/state/synthesis.json` com 0 bytes, enquanto o
diário bruto (`read-log.txt`) mostrava as leituras acontecendo
normalmente -- prova de que o defeito era na ficha, não na leitura em
si.

Correção completa, com duas partes: `synthesis_init` passa a validar
conteúdo (`jq empty`), não só existência, reconstruindo do zero se o
arquivo estiver vazio ou inválido; e cada leitura+escrita da ficha
(`synthesis_bump`, `synthesis_set`) passa a rodar dentro de uma trava
baseada em `mkdir` (criar uma pasta é atômico entre processos,
inclusive no Windows), com quebra automática depois de ~5 segundos se
a trava ficar presa. A trava é necessária porque nome de arquivo
temporário único, sozinho, evita a corrupção mas não evita perda de
fato: cada processo concorrente lê o estado atual antes de escrever, e
quem termina por último apaga o que os outros escreveram nesse
meio-tempo. Teste isolado (dez chamadas concorrentes de verdade,
`&`/`wait`, numa pasta de rascunho): antes da trava, só quatro dos dez
fatos sobreviviam, mesmo sem corrupção; com a trava, os dez
sobreviveram, JSON continuou válido. `bash -n lib/common.sh` sem erro.

### 2026-08-28-autorizo-trava-disparado-por-texto-de-exemplo-citado

**Confirmado por:** teste ao vivo.

*Em resumo:* colar, na conversa, o conteúdo de um arquivo deste
projeto que continha o texto de exemplo "AUTORIZO-TRAVA: <motivo>."
(mostrando como a autorização deveria ser usada) foi suficiente pra
`user_prompt_submit.sh` registrar uma autorização de verdade -- sem
nenhuma decisão real de quem estava conduzindo a sessão.

*Em detalhe técnico:* o gancho comparava só se a mensagem continha a
palavra "AUTORIZO-TRAVA:", sem checar se havia um motivo de fato
escrito depois dela. Corrigido: o texto imediatamente depois de
"AUTORIZO-TRAVA:" é comparado contra o início do padrão do placeholder
literal ("<motivo>") -- se o texto começar assim, a autorização não é
registrada, mesmo havendo mais alguma coisa escrita depois (o restante
da frase onde o exemplo apareceu citado). Checar só o início, não o
texto inteiro, importa aqui: o texto capturado depois de
"AUTORIZO-TRAVA:" quase sempre tem algo mais depois do placeholder (o
resto da frase citada) -- exigir que o texto inteiro fosse exatamente
"<motivo>" não bloqueava esse caso real. Testado isoladamente: mensagem
citando "AUTORIZO-TRAVA: <motivo>. valeu" não autoriza; mensagem com
motivo de fato escrito autoriza normalmente. `bash -n
user_prompt_submit.sh` sem erro.

### 2026-08-28-lacuna-de-trava-mecanica-em-pitfalls-findings-decisions

**Confirmado por:** teste ao vivo.

*Em resumo:* `pitfalls.md`, `findings.md` (este arquivo) e
`decisions/` só eram checados por julgamento de IA no momento do
commit (`pre_commit_hygiene.sh`), sem nenhuma trava mecânica no
momento da própria edição -- diferente do resto do fluxo de escrita
de documentação, já movido pra esse momento em rodadas anteriores
deste módulo.

*Em detalhe técnico:* `pre_edit_safety.sh` ganha os itens 13 (achado
sem registro) e 14/15 (escolha sem ADR), que sinalizam -- nunca
travam sozinhos, porque decidir se algo revelou um achado ou envolveu
escolha real entre alternativas é julgamento de quem desenvolve, não
fato mecânico -- e destravam por uma frase de confirmação específica
(`"nada a registrar, confirmado"` ou `"sem alternativas reais,
confirmado"`, mais estreitas que `AUTORIZO-TRAVA`, resolvem só aquele
item) ou por `AUTORIZO-TRAVA`. `post_edit_track.sh` passa a registrar
na ficha quando `findings.md`, `pitfalls.md` e `decisions/*` são
tocados, pros dois itens novos consultarem.

Testado isoladamente, JSON de entrada fabricado via stdin, fora do
fluxo real de um gancho: escrever código num módulo sem `pitfalls.md`
nem `findings.md` tocados bloqueou (item 13); com a frase "nada a
registrar, confirmado" registrada, a mesma edição passou. Escrever em
`schemas/` sem nenhuma ADR nova em `decisions/` bloqueou (item 14/15);
com a frase "sem alternativas reais, confirmado" registrada, a mesma
edição passou. `bash -n pre_edit_safety.sh` e `bash -n
post_edit_track.sh` sem erro. Ver
[decisions/0013](<../decisions/0013-frescor-uniforme-de-leitura-substitui-permanencia.md>)
pra decisão de desenho relacionada (frescor uniforme de leitura).

### 2026-08-28-cascata-do-item-5-perdia-alternativa-de-schemas

**Confirmado por:** teste ao vivo.

*Em resumo:* a checagem de código do item 5 (última etapa da cascata
`concept.md` → `architecture.md` → `schemas/` → implementação) só
aceitava `architecture.md` como leitura fresca válida -- perdendo a
alternativa de `schemas/` que a checagem antiga (por edição) já tinha.
Um módulo sem pasta `schemas/` (ex.: este módulo, `conformidade`)
ficaria bloqueado sem necessidade real.

*Em detalhe técnico:* corrigido com `synthesis_any_fresh_with_prefix`
(`lib/common.sh`) -- aceita qualquer fato de leitura cuja chave comece
com o caminho da pasta `schemas/` do módulo, já que arquivo de esquema
não tem nome fixo (`synthesis_fresh` sozinha só cobre chave exata).
Escrevendo o teste isolado dessa função, uma segunda causa apareceu:
`jq` neste ambiente devolve cada linha terminada em `\r\n`, e o `\r`
sobrava no fim da chave lida num laço `while read`, fazendo a
comparação falhar sempre -- ver
[pitfalls.md](<pitfalls.md#2026-08-28-jq-devolve-linha-com-retorno-de-carro-neste-ambiente>).

Bateria de teste isolado, casos positivo e negativo pra cada ponto,
fora do fluxo real de um gancho: limite exato da janela de frescor
(idade 20 conta como fresco, idade 21 não); implementação liberada com
`architecture.md` não fresco mas um arquivo de `schemas/` fresco;
implementação bloqueada com nem um nem outro fresco; fato de
`schemas/` de um módulo (`testmod20`) não satisfaz a checagem de outro
módulo com nome parecido (`testmod2`); item 6 -- `Write` sobrescrevendo
`handoff.md` já existente sem leitura fresca bloqueou, mesmo sem
passar pelo item 1 (que só cobre `Edit`); item 7 -- mesmo teste pra
`modulos/README.md`; item 8 -- mesmo teste pra `TASKS.md` (raiz); e um
caso de falso positivo da correção do placeholder de `AUTORIZO-TRAVA`
(mensagem "AUTORIZO-TRAVA: <verificação manual: já testei isso a
mão>" -- começa com `<` mas não é o texto de exemplo -- autorizou
normalmente, sem bloquear por engano). `bash -n lib/common.sh` e
`bash -n pre_edit_safety.sh` sem erro depois da correção.

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- dois achados registrados. | Criação inicial do módulo |
| 0.2.0 | 27-08-2026 | Achado novo registrado (teste isolado da restrição de Read). | Segunda rodada de correção do sistema de conformidade |
| 0.3.0 | 28-08-2026 | Dois achados novos registrados (checagens de emoji/esquema/licença/antes-depois no momento da edição; auto-portão de pre_commit_hygiene.sh). | Correção da falha aberta do filtro `if` |
| 0.4.0 | 28-08-2026 | Achado novo registrado (teste isolado da ficha/síntese e de session_start_reset.sh), retroativo a trabalho já feito nesta sessão sem registro. | Fechamento da lacuna de documentação da ficha/síntese (decisions/0012) |
| 0.5.0 | 28-08-2026 | Achado novo registrado (padrão de emoji incluía bloco de setas tipográficas, bloqueando commit legítimo). | Correção do padrão de emoji em lib/common.sh |
| 0.6.0 | 28-08-2026 | Quatro achados novos registrados, todos testados isoladamente antes do commit: reset indevido da ficha na compactação, corrupção e perda de fato na ficha por escrita concorrente, AUTORIZO-TRAVA disparado por texto de exemplo citado, e a lacuna de trava mecânica em pitfalls/findings/decisions. | Correção do bloqueio real dos ganchos de conformidade |
| 0.7.0 | 28-08-2026 | Achado novo registrado (item 5 perdia a alternativa de `schemas/`, revelado numa segunda rodada de teste mais rigorosa, cobrindo limite exato da janela, isolamento entre módulos, itens 6/7/8 e falso positivo do placeholder). | Correção do bloqueio real dos ganchos de conformidade |
