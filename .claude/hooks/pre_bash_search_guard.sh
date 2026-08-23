#!/bin/bash
# pre_bash_search_guard.sh -- evento: PreToolUse, filtro: Bash
#
# CLAUDE.md, seção "Ferramentas": "Não usar [...] ferramentas de
# busca ampla (Grep, Glob, Explore) por iniciativa própria [...] Só
# usar essas ferramentas quando o usuário pedir busca/exploração [...]
# na mensagem atual." O settings.json já cobre as ferramentas
# nomeadas (Grep/Glob/Task via permissions.ask) -- mas rodar o mesmo
# tipo de busca através do Bash (ls/grep/find/sed) não escapa da
# regra só por trocar de ferramenta, e nenhum hook cobria esse
# caminho até agora. Achado concreto: aconteceu de verdade nesta
# mesma sessão (ls -la e find -type f rodados via Bash pra explorar
# uma pasta, logo depois de reler o CLAUDE.md).
#
# grep/find/sed usados como busca de conteúdo são quase sempre um
# substituto de Grep/Glob -- sempre pede confirmação. "ls" tem um
# recorte real permitido (CLAUDE.md, memória do projeto): confirmar
# que um arquivo/pasta específica existe é ação direta e pontual,
# continua liberado; "ls" recursivo, com curinga, ou de mais de um
# caminho, é a mesma varredura ampla que Glob cobriria.

source "$(dirname "$0")/lib/common.sh"
read_input

COMMAND=$(field '.tool_input.command')

if is_authorized; then
  log_override "pre_bash_search_guard" "$(authorized_reason)"
  exit 0
fi

ESCALATE_REASON=""

if echo "$COMMAND" | grep -Eq '(^|[;&|]|\bsudo\s)\s*grep\b'; then
  ESCALATE_REASON="'grep' via Bash usado como busca de conteúdo -- mesma proibição de Grep/Glob por iniciativa própria (CLAUDE.md, Ferramentas)."
elif echo "$COMMAND" | grep -Eq '(^|[;&|]|\bsudo\s)\s*find\b'; then
  ESCALATE_REASON="'find' via Bash usado como busca de arquivo -- mesma proibição de Grep/Glob por iniciativa própria (CLAUDE.md, Ferramentas)."
elif echo "$COMMAND" | grep -Eq '(^|[;&|]|\bsudo\s)\s*sed\s+-n\b'; then
  ESCALATE_REASON="'sed -n' via Bash usado como filtro de conteúdo -- mesma proibição de Grep/Glob por iniciativa própria (CLAUDE.md, Ferramentas); pra ler um arquivo já conhecido, usar a ferramenta Read."
elif echo "$COMMAND" | grep -Eq '(^|[;&|]|\bsudo\s)\s*ls\b'; then
  # "ls" simples (uma pasta, sem -R/-r, sem glob/wildcard, sem pipe
  # pra outro filtro) continua liberado -- só escala o caso amplo.
  #
  # Achado ao vivo: a primeira versão deste regex ("-\w*[Rr]\w*", sem
  # exigir limite de palavra) casava por engano dentro de um NOME DE
  # PASTA com hífen seguido de R -- ex.: "NEXO-EMBRIOLOGIA" tem
  # "-EMBRIOLOGIA", que contém "-...R...", disparando "ls amplo" pra
  # um "ls -la" comum de uma pasta só. Corrigido pra exigir que a flag
  # seja um token isolado (delimitado por espaço/início/fim), nunca
  # um trecho solto dentro de outra palavra.
  if echo "$COMMAND" | grep -Eq '(^|\s)-[a-zA-Z]*[Rr][a-zA-Z]*(\s|$)' \
     || echo "$COMMAND" | grep -Eq '\*|\?|\[' \
     || echo "$COMMAND" | grep -Eq '\bls\b[^|;&]*\|' \
     || [[ $(echo "$COMMAND" | grep -oE '\bls\b[^;&|]*' | wc -w) -gt 3 ]]; then
    ESCALATE_REASON="'ls' via Bash usado de forma ampla (recursivo, com curinga, ou vários caminhos) -- mesma proibição de Glob por iniciativa própria (CLAUDE.md, Ferramentas). 'ls' de uma pasta específica, pra confirmar que algo existe, continua liberado."
  fi
fi

if [[ -n "$ESCALATE_REASON" ]]; then
  jq -n --arg reason "$ESCALATE_REASON" '{
    hookSpecificOutput: {
      hookEventName: "PreToolUse",
      permissionDecision: "escalate",
      permissionDecisionReason: $reason
    }
  }'
fi

exit 0
