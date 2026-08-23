#!/bin/bash
# pre_commit_hygiene.sh -- evento: PreToolUse, filtro: Bash(git commit *)

source "$(dirname "$0")/lib/common.sh"
read_input

COMMAND=$(field '.tool_input.command')
CWD=$(field '.cwd')
TRANSCRIPT=$(field '.transcript_path')

# 1) Trailer proibido -- fato de texto, sem exceção possível
if echo "$COMMAND" | grep -qi "Co-Authored-By"; then
  block "Bloqueado: a mensagem de commit não pode ter a linha 'Co-Authored-By: Claude ...'."
fi

# 2) Emoji em qualquer arquivo do commit -- fato de texto, sem exceção
#
# "grep -P" com intervalo \x{...} acima de 0x7F exige locale UTF-8 --
# em locale "C"/"POSIX" (comum em ambiente Windows/Git Bash sem
# variável de locale definida), falha com "supports only unibyte and
# UTF-8 locales" e a checagem inteira nunca roda (silêncio, não
# segurança). Forçar LC_ALL=C.UTF-8 só nesta chamada -- confirmado
# por teste ao vivo que resolve, sem depender de nenhum locale
# instalado no sistema além do "C.UTF-8"/"C.utf8" que o glibc/musl já
# trazem por padrão.
EMOJI_PATTERN='[\x{1F300}-\x{1FAFF}\x{2600}-\x{27BF}\x{2190}-\x{21FF}\x{2B00}-\x{2BFF}]'
EMOJI_HIT=$(git -C "$CWD" diff --cached -U0 2>/dev/null | LC_ALL=C.UTF-8 grep -P "$EMOJI_PATTERN" | head -n 1)
if [[ -n "$EMOJI_HIT" ]]; then
  block "Bloqueado: encontrei um emoji num arquivo que entraria neste commit."
fi

# 2b) Emoji na MENSAGEM do commit em si -- achado na releitura linha
# por linha do CLAUDE.md: "Nunca usar emojis em nada escrito neste
# projeto (código, docs, commits, chat)" cobre a mensagem do commit,
# não só o conteúdo dos arquivos alterados. A checagem acima (2) só
# olha o diff dos arquivos -- esta olha o comando "git commit" em si,
# onde o texto da mensagem aparece de verdade (-m "..." ou heredoc).
if echo "$COMMAND" | LC_ALL=C.UTF-8 grep -qP "$EMOJI_PATTERN"; then
  block "Bloqueado: encontrei um emoji na própria mensagem do commit."
fi

# 3) Pureza de esquemas -- fato de texto, sem exceção
SCHEMA_FILES=$(git -C "$CWD" diff --cached --name-only -- '*/schemas/*.json' 'schemas/*.json' 2>/dev/null)
if [[ -n "$SCHEMA_FILES" ]]; then
  for f in $SCHEMA_FILES; do
    if git -C "$CWD" show ":$f" 2>/dev/null | grep -Eq '"description"|"example"'; then
      block "Bloqueado: $f tem campo 'description' ou 'example'. Esquema de dado carrega só dado puro."
    fi
  done
fi

# A partir daqui: checagens heurísticas -- podem ser puladas com
# AUTORIZO-TRAVA: <motivo> na sua mensagem (ver hook
# user_prompt_submit.sh, que já confirmou a autorização antes deste
# script rodar).
if is_authorized; then
  log_override "pre_commit_hygiene" "$(authorized_reason)"
  exit 0
fi

# 4) Leitura obrigatória (os 6 de leitura manual -- ver
# first_unread_mandatory_doc em lib/common.sh pro motivo de não checar
# os 16 auto-importados aqui). Reforço: pre_edit_safety.sh já barra
# isso antes da primeira edição (Portão pro Passo 2); esta é a
# segunda conferência, no commit.
UNREAD_DOC=$(first_unread_mandatory_doc)
if [[ -n "$UNREAD_DOC" ]]; then
  block "Bloqueado: não encontrei rastro de leitura (via Read) de '$UNREAD_DOC' nesta sessão. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
fi

# 5) Ordem completa: concept.md -> architecture.md -> schemas/ ->
# implementação (CLAUDE.md, Fluxo de escrita e revisão de
# documentação). Achado na releitura linha por linha: só
# "architecture.md antes do código" existia -- "concept.md antes de
# architecture.md" e "schemas/ antes do código" nunca foram checados.
if [[ -f "$EDIT_LOG" ]]; then
  MODULES=$(git -C "$CWD" diff --cached --name-only -- 'modulos/*' 2>/dev/null | sed -E 's#(modulos/[^/]+)/.*#\1#' | sort -u)
  for mod in $MODULES; do
    CONCEPT_TIME=$(grep "$mod/docs/concept.md" "$EDIT_LOG" | head -n 1 | awk '{print $1}')
    ARCH_TIME=$(grep "$mod/docs/architecture.md" "$EDIT_LOG" | head -n 1 | awk '{print $1}')
    SCHEMA_TIME=$(grep -E "^\S+ $mod/schemas/" "$EDIT_LOG" | head -n 1 | awk '{print $1}')
    CODE_TIME=$(grep -E "^\S+ $mod/" "$EDIT_LOG" | grep -Ev '/docs/|/schemas/|/decisions/' | head -n 1 | awk '{print $1}')
    if [[ -n "$CONCEPT_TIME" && -n "$ARCH_TIME" && "$ARCH_TIME" < "$CONCEPT_TIME" ]]; then
      block "Bloqueado: em $mod, architecture.md foi tocado antes de concept.md nesta sessão. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
    if [[ -n "$SCHEMA_TIME" && -n "$CODE_TIME" && "$CODE_TIME" < "$SCHEMA_TIME" ]]; then
      block "Bloqueado: em $mod, implementação foi tocada antes de schemas/ nesta sessão. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
    if [[ -n "$ARCH_TIME" && -n "$CODE_TIME" && "$CODE_TIME" < "$ARCH_TIME" ]]; then
      block "Bloqueado: em $mod, implementação foi tocada antes de architecture.md nesta sessão. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
  done
fi

# 6) Linha de Licença em tabela de cabeçalho
for f in $(git -C "$CWD" diff --cached --name-only -- '*.md' 2>/dev/null); do
  CONTENT=$(git -C "$CWD" show ":$f" 2>/dev/null)
  if echo "$CONTENT" | grep -qE '^\s*\|?\s*Campo\s*\|\s*Valor' && ! echo "$CONTENT" | grep -qi 'Licen'; then
    block "Bloqueado: $f tem tabela de cabeçalho mas nenhuma linha de Licença. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
  fi
done

# 7) "Antes e depois" em documento novo (nunca versionado)
NEW_DOCS=$(git -C "$CWD" diff --cached --name-only --diff-filter=A -- '*.md' 2>/dev/null)
if [[ -n "$NEW_DOCS" ]]; then
  for f in $NEW_DOCS; do
    if git -C "$CWD" show ":$f" 2>/dev/null | grep -Eiq 'era assim|ficou assim|antes:|anteriormente era|mudou de.*para'; then
      block "Bloqueado: $f é documento novo mas usa linguagem de 'antes e depois'. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
  done
fi

# 8) handoff.md deve ser o último arquivo de documentação tocado
if [[ -f "$EDIT_LOG" ]]; then
  MODULES=$(git -C "$CWD" diff --cached --name-only -- 'modulos/*/docs/*' 2>/dev/null | sed -E 's#(modulos/[^/]+)/docs/.*#\1#' | sort -u)
  for mod in $MODULES; do
    LAST=$(grep "$mod/docs/" "$EDIT_LOG" | tail -n 1 | awk '{print $2}')
    if [[ -n "$LAST" && "$(basename "$LAST")" != "handoff.md" ]]; then
      block "Bloqueado: o último arquivo tocado em $mod/docs/ foi $(basename "$LAST"), não handoff.md. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
  done
fi

# 9) Deriva entre CLAUDE.md e o mecanismo que o aplica -- checagem
# mecânica (lista de arquivo, não julgamento de conteúdo): se
# CLAUDE.md muda neste commit sem nenhum arquivo de hook mudar junto,
# a regra nova (ou alterada) pode ter ficado só no texto, sem trava
# correspondente -- sinal de alerta, não certeza (nem toda mudança de
# CLAUDE.md exige hook novo), por isso autorizável.
CLAUDE_MD_CHANGED=$(git -C "$CWD" diff --cached --name-only -- '.claude/CLAUDE.md' 2>/dev/null)
if [[ -n "$CLAUDE_MD_CHANGED" ]]; then
  HOOK_FILES_CHANGED=$(git -C "$CWD" diff --cached --name-only -- '.claude/hooks/*' '.claude/settings.json' 'scripts-hooks/*' 2>/dev/null)
  if [[ -z "$HOOK_FILES_CHANGED" ]]; then
    block "Aviso: CLAUDE.md mudou neste commit e nenhum arquivo de hook (.claude/hooks/, .claude/settings.json, scripts-hooks/) mudou junto. Confirme se a regra nova/alterada precisa de mecanismo correspondente, ou se é ajuste que não se mecaniza (prosa, contexto). Se já confirmou, use AUTORIZO-TRAVA: <motivo>."
  fi
fi

exit 0
