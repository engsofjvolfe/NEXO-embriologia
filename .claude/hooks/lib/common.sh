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
SYNTHESIS_FILE="${STATE_DIR}/synthesis.json"

mkdir -p "$STATE_DIR"

# --- Síntese (estado atual, não o diário) ---------------------------
#
# Cada log deste projeto (edit-order.log, read-log.txt, etc.) é um
# diário: cresce pra sempre, nunca apaga nada, é a fonte bruta -- ótimo
# pra investigar depois, ruim pra checar rápido (checar "isso já foi
# feito?" reler o diário inteiro toda vez, ficando mais lento conforme
# a sessão cresce). A síntese é o oposto: um arquivo pequeno, JSON, que
# guarda só o estado ATUAL de cada coisa (foi tocado? há quanto tempo,
# em número de ações, não em relógio?) -- suficiente pra responder
# "isso já foi feito, e ainda vale?" sem reler nada.
#
# "Ainda vale" é a parte importante -- a síntese não é "marcar como
# feito pra sempre": cada entrada carrega o número da ação (não da
# hora do relógio) em que foi confirmada, e cada checagem decide, na
# hora, se essa distância (ação atual menos ação registrada) ainda é
# aceitável pra aquela regra específica -- mesmo princípio já usado
# antes só pra citação de documento (janela das 20 leituras mais
# recentes), generalizado agora pra qualquer fato guardado aqui.
#
# O diário nunca é substituído -- continua existindo, cresce do mesmo
# jeito, serve de prova bruta pra quem quiser investigar ou fazer uma
# segunda conferência independente, sem confiar na síntese de ninguém.
# A síntese só existe *a mais*, como atalho rápido.
#
# Reinicia (arquivo novo, contador em zero) uma vez por sessão --
# ver session_start_reset.sh -- pra nunca deixar um fato de uma sessão
# anterior contar como "confirmado nesta sessão".

synthesis_init() {
  [[ -f "$SYNTHESIS_FILE" ]] || echo '{"acao_atual": 0, "fatos": {}}' > "$SYNTHESIS_FILE"
}

# Anda o "relógio" da síntese uma ação -- chamado pelos ganchos
# post_*_track.sh, sempre que algo relevante acontece (leitura
# completa, edição). Devolve o novo valor por stdout.
synthesis_bump() {
  synthesis_init
  local novo
  novo=$(jq '.acao_atual += 1 | .acao_atual' "$SYNTHESIS_FILE" 2>/dev/null)
  [[ -z "$novo" ]] && novo=1
  jq --argjson n "$novo" '.acao_atual = $n' "$SYNTHESIS_FILE" > "${SYNTHESIS_FILE}.tmp" 2>/dev/null \
    && mv "${SYNTHESIS_FILE}.tmp" "$SYNTHESIS_FILE"
  echo "$novo"
}

# Marca um fato como confirmado agora (na ação atual). Uso:
#   synthesis_set "leitura.<nome-do-documento>"
#   synthesis_set "edicao.<modulo>.concept"
synthesis_set() {
  local chave="$1"
  synthesis_init
  local atual
  atual=$(jq -r '.acao_atual' "$SYNTHESIS_FILE" 2>/dev/null)
  jq --arg k "$chave" --argjson a "${atual:-0}" '.fatos[$k] = $a' "$SYNTHESIS_FILE" > "${SYNTHESIS_FILE}.tmp" 2>/dev/null \
    && mv "${SYNTHESIS_FILE}.tmp" "$SYNTHESIS_FILE"
}

# Devolve, por stdout, há quantas ações um fato foi confirmado pela
# última vez (0 = agora mesmo; vazio = nunca confirmado nesta sessão).
synthesis_age() {
  local chave="$1"
  synthesis_init
  local confirmado_em atual
  confirmado_em=$(jq -r --arg k "$chave" '.fatos[$k] // empty' "$SYNTHESIS_FILE" 2>/dev/null)
  [[ -z "$confirmado_em" ]] && return 0
  atual=$(jq -r '.acao_atual' "$SYNTHESIS_FILE" 2>/dev/null)
  echo $(( ${atual:-0} - confirmado_em ))
}

# Um fato foi confirmado, e a distância (em ações) até agora está
# dentro do limite aceitável pra essa regra? Uso:
#   synthesis_fresh "leitura.modulos/README.md" 20
synthesis_fresh() {
  local chave="$1" limite="$2"
  local idade
  idade=$(synthesis_age "$chave")
  [[ -z "$idade" ]] && return 1
  [[ "$idade" -le "$limite" ]]
}

# Reinicia a síntese pro estado vazio -- chamado uma vez por sessão
# nova (session_start_reset.sh), nunca no meio de uma sessão.
synthesis_reset() {
  echo '{"acao_atual": 0, "fatos": {}}' > "$SYNTHESIS_FILE"
}

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
# post_read_track.sh) nesta sessão. Consulta a ficha (síntese, sem
# expiração pra este fato específico -- leitura obrigatória é sobre
# ORDEM, "antes de qualquer outra coisa", não sobre um fato que
# precisa ficar sendo reconfirmado; diferente da citação de documento
# num texto novo, que É sobre confiar em algo lido há pouco -- ver
# pre_edit_safety.sh #4), nunca relendo o diário inteiro. Vazio se
# todos já foram lidos.
first_unread_mandatory_doc() {
  for doc in "${MANUAL_MANDATORY_DOCS[@]}"; do
    if [[ -z "$(synthesis_age "leitura.${doc}")" ]]; then
      echo "$doc"
      return 0
    fi
  done
}

# Padrão de emoji, compartilhado entre pre_edit_safety.sh (no momento
# da edição) e pre_commit_hygiene.sh (segunda conferência, no commit),
# e função de esquema impuro, também compartilhada -- um lugar só,
# nunca cópia duplicada que possa divergir entre os dois arquivos.
#
# Cobertura, por bloco Unicode (faixas de emoji de verdade, conforme
# `emoji-data.txt` do Unicode Consortium):
# - \x{1F1E6}-\x{1F1FF}: indicadores regionais -- bandeira de país é
#   sempre um par desses dois caracteres.
# - \x{1F300}-\x{1FAFF}: pictogramas, emoticons, transporte, símbolos
#   suplementares -- o grosso dos emojis "modernos".
# - \x{2600}-\x{27BF}: símbolos diversos e dingbats (ex.: sol, coração,
#   tesoura, avião).
# - \x{2B00}-\x{2BFF}: símbolos diversos e setas (ex.: estrela, seta
#   grossa colorida -- diferente do bloco "Arrows" abaixo).
# - \x{2300}-\x{23FF}: técnico diverso (ex.: relógio, ampulheta).
#
# Deliberadamente fora do padrão, mesmo aparecendo em alguma lista de
# emoji: o bloco Unicode "Arrows" (\x{2190}-\x{21FF}, setas
# tipográficas simples como "→"/"↔") e "Geometric Shapes"
# (\x{25A0}-\x{25FF}, quadrados/círculos simples) -- os dois usados o
# tempo todo como pontuação comum na prosa deste projeto (ex.:
# "concept.md → architecture.md"), e "©"/"®"/"™" -- comuns em texto
# legal/técnico comum. Incluir esses blocos bloquearia texto legítimo
# sem nenhum emoji de verdade envolvido.
EMOJI_PATTERN='[\x{1F1E6}-\x{1F1FF}\x{1F300}-\x{1FAFF}\x{2300}-\x{23FF}\x{2600}-\x{27BF}\x{2B00}-\x{2BFF}]'

# "grep -P" com \x{...} acima de 0x7F exige locale UTF-8 -- em locale
# "C"/"POSIX" (comum em Windows/Git Bash sem variável de locale
# definida), falha em silêncio (nunca acha nada, nunca bloqueia).
# Forçar LC_ALL=C.UTF-8 aqui, confirmado por teste ao vivo.
has_emoji() {
  LC_ALL=C.UTF-8 grep -qP "$EMOJI_PATTERN"
}

# Bloco cercado por ```yaml ou ```json que também tem required ou
# properties, junto com type (forma de todo bloco de contrato de dado
# deste projeto), e além disso tem description ou example -- esquema
# que devia ser dado puro mas não é.
schema_block_impure() {
  awk '
    /^```(yaml|json)[[:space:]]*$/ { infence=1; buf=""; next }
    /^```[[:space:]]*$/ {
      if (infence) {
        if ((buf ~ /required/ || buf ~ /properties/) && buf ~ /type/) {
          if (buf ~ /description/ || buf ~ /example/) { print "HIT"; exit }
        }
      }
      infence=0; next
    }
    infence { buf = buf $0 "\n" }
  ' | grep -q HIT
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
