#!/bin/bash
# pre_edit_safety.sh -- evento: PreToolUse, filtro: Write|Edit

source "$(dirname "$0")/lib/common.sh"
read_input

TOOL_NAME=$(field '.tool_name')
FILE_PATH=$(normalize_path "$(field '.tool_input.file_path')")
CWD=$(normalize_path "$(field '.cwd')")
TRANSCRIPT=$(field '.transcript_path')

if is_authorized; then
  log_override "pre_edit_safety" "$(authorized_reason)"
  exit 0
fi

# 1) Reler antes de editar (não se aplica a Write de arquivo novo).
# Consulta o registro limpo de leituras (post_read_track.sh), não o
# transcript bruto. read-log.txt já é gravado normalizado (ver
# post_read_track.sh), por isso comparar $FILE_PATH (já normalizado
# acima) direto contra ele funciona em qualquer sistema operacional.
READ_LOG="${STATE_DIR}/read-log.txt"
if [[ "$TOOL_NAME" == "Edit" && -n "$FILE_PATH" ]]; then
  if [[ ! -f "$READ_LOG" ]] || ! grep -qF "$FILE_PATH" "$READ_LOG"; then
    block "Bloqueado: não encontrei rastro de que $FILE_PATH foi lido (via Read) nesta sessão antes desta edição. Se já leu de outro jeito, use AUTORIZO-TRAVA: <motivo>."
  fi
fi

# 2) Escrever fora de uma worktree
if [[ -n "$FILE_PATH" && "$CWD" != *"/.claude/worktrees/"* && "$FILE_PATH" != *"/.claude/"* ]]; then
  block "Bloqueado: escrita fora de uma worktree própria. Se isso for intencional, use AUTORIZO-TRAVA: <motivo>."
fi

# 3) Leitura manual obrigatória antes de qualquer código -- Portão pro
# Passo 2 do "Fluxo completo de uma tarefa": "nenhuma linha de código
# escrita antes disso, e a leitura obrigatória já feita de verdade".
# Achado na releitura linha por linha: antes, só pre_commit_hygiene.sh
# conferia isso, no commit -- tarde demais, dava pra editar dezenas de
# arquivos sem nunca ter lido nada. Só os 6 documentos de leitura
# manual entram aqui (ver lib/common.sh, first_unread_mandatory_doc).
UNREAD_DOC=$(first_unread_mandatory_doc)
if [[ -n "$UNREAD_DOC" ]]; then
  block "Bloqueado: leitura manual obrigatória ainda não feita ('$UNREAD_DOC') -- nenhum código antes disso (CLAUDE.md, Portão pro Passo 2). Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
fi

exit 0
