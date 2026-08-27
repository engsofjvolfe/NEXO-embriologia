#!/bin/bash
# lib/common.sh
#
# Funções compartilhadas por todos os scripts de hook deste projeto.
# Todo script começa com:
#   source "$(dirname "$0")/lib/common.sh"
#   read_input
#
# Isso existe pra três coisas não ficarem repetidas oito vezes: ler o
# JSON de entrada, checar se há autorização ativa (AUTORIZO-TRAVA),
# e registrar em log sempre que algo relevante acontece.

STATE_DIR="${CLAUDE_PROJECT_DIR}/.claude/hooks/state"
AUTH_FILE="${STATE_DIR}/current-authorization"
OVERRIDES_LOG="${STATE_DIR}/overrides.log"
EDIT_LOG="${STATE_DIR}/edit-order.log"
PREVIEW_LOG="${STATE_DIR}/preview-sessions.log"

mkdir -p "$STATE_DIR"

# Todo hook deste projeto lê o JSON de entrada via jq (função field()
# abaixo). Sem jq instalado, "field" falha e devolve string vazia --
# em silêncio, porque field() redireciona o erro do jq pra /dev/null
# de propósito (evita que uma chave ausente no JSON vire ruído). Sem
# esta checagem, um hook inteiro "roda" sem checar nada de verdade e
# sem nenhum aviso -- falha travando (fail closed) em vez de falhar
# em silêncio (fail open). Achado ao vivo nesta máquina (jq ausente),
# confirmado por teste real.
require_jq() {
  if ! command -v jq >/dev/null 2>&1; then
    block "Bloqueado: jq não está instalado -- todo hook deste projeto depende dele pra ler o JSON de entrada, e sem ele as checagens não travam nada de verdade (silêncio, não segurança). Instale jq (ver MANUAL.md, seção Instalação) antes de continuar."
  fi
}

# Lê todo o stdin uma vez (só dá pra ler uma vez por processo) e
# guarda em $HOOK_INPUT pro resto do script usar.
read_input() {
  require_jq
  HOOK_INPUT=$(cat)
}

# Atalho pra extrair um campo do JSON de entrada com jq.
# Uso: field '.tool_input.command'
field() {
  echo "$HOOK_INPUT" | jq -r "$1 // empty" 2>/dev/null
}

# Existe autorização ativa pra ESTA mensagem? (escrita pelo hook
# user_prompt_submit.sh, que roda antes de qualquer outro hook nesta
# resposta)
is_authorized() {
  [[ -s "$AUTH_FILE" ]]
}

authorized_reason() {
  [[ -f "$AUTH_FILE" ]] && cat "$AUTH_FILE"
}

# Windows usa "\" como separador de caminho; as checagens deste projeto
# comparam substring com "/" (padrão Unix, usado nas regras do
# CLAUDE.md). Sem normalizar, uma checagem como '"$CWD" contém
# "/.claude/worktrees/"' nunca bate contra um caminho do Windows tipo
# "H:\...\.claude\worktrees\...", mesmo estando de verdade dentro da
# worktree -- bloqueando toda edição por engano. Usar em todo caminho
# (cwd, file_path) antes de comparar ou gravar em log.
normalize_path() {
  echo "${1//\\//}"
}

# Compara dois caminhos ignorando maiúscula/minúscula, depois de
# normalizar a barra de cada um. Necessário porque, no Windows, a letra
# de unidade do mesmo diretório pode chegar em caixas diferentes
# dependendo de quem informou o caminho -- o "cwd" que o Claude Code
# manda pro hook e o caminho que "git worktree list" devolve para essa
# mesma pasta nem sempre usam a mesma caixa, mesmo apontando pro mesmo
# lugar em disco (sistema de arquivo do Windows não diferencia
# maiúscula de minúscula). Comparação de texto, sem consultar o disco
# nem resolver link simbólico.
paths_equal() {
  local a b
  a=$(normalize_path "$1")
  b=$(normalize_path "$2")
  [[ "${a,,}" == "${b,,}" ]]
}

# Lista de leitura obrigatória (CLAUDE.md, seção "Leitura obrigatória,
# fonte da verdade"). Só os 6 documentos de "LEITURA MANUAL
# OBRIGATÓRIA" entram nesta checagem -- os outros 16, importados
# automaticamente via "@caminho" no próprio CLAUDE.md, já chegam
# garantidos pelo mecanismo do Claude Code assim que a sessão começa,
# sem chamada de Read nenhuma. Checar os 16 contra o read-log.txt
# bloquearia toda sessão bem-comportada (nenhuma precisa reler algo
# que já veio automaticamente) -- só os 6 manuais existem justamente
# porque o auto-import falha pra eles (espaço no nome do arquivo, ver
# TASKS.md), então só eles exigem um Read explícito de verdade.
#
# Função compartilhada porque o Portão pro Passo 2 do fluxo completo
# ("nenhuma linha de código escrita antes disso, e a leitura
# obrigatória já feita de verdade") exige essa checagem ANTES da
# primeira edição, não só no commit -- antes, só pre_commit_hygiene.sh
# conferia isso, tarde demais (dava pra editar dezenas de arquivos sem
# nunca ter lido nada).
MANUAL_MANDATORY_DOCS=(
  "1 - documento-de-conceito-geral.md"
  "2 - requisitos-conceito-geral.md"
  "3 - especificacao-conceito-geral.md"
  "4 - projeto-arquitetonico.md"
  "5 - projeto-detalhado.md"
  "prompt model.txt"
)

# Devolve, por stdout, o primeiro documento de leitura manual
# obrigatória ainda sem rastro de leitura completa (não parcial -- ver
# post_read_track.sh) no read-log.txt desta sessão. Vazio se todos já
# foram lidos.
first_unread_mandatory_doc() {
  local READ_LOG="${STATE_DIR}/read-log.txt"
  for doc in "${MANUAL_MANDATORY_DOCS[@]}"; do
    if [[ ! -f "$READ_LOG" ]] || ! grep -qF "$doc" "$READ_LOG"; then
      echo "$doc"
      return 0
    fi
  done
}

# Registra o uso de uma autorização -- nunca some em silêncio.
log_override() {
  echo "$(date -u +%FT%TZ) [$1] $2" >> "$OVERRIDES_LOG"
}

# Bloqueia a ação atual com uma mensagem explicando o porquê.
block() {
  echo "$1" >&2
  exit 2
}
