#!/bin/bash
# post_edit_track.sh -- evento: PostToolUse, filtro: Write|Edit
#
# Toda vez que um arquivo é escrito ou editado, guarda uma linha
# "hora + caminho" no log de ordem de edição. Um git diff sozinho só
# mostra o resultado final, nunca a ordem em que as coisas foram
# tocadas -- este log guarda essa ordem de verdade.

source "$(dirname "$0")/lib/common.sh"
read_input

FILE_PATH=$(normalize_path "$(field '.tool_input.file_path')")

if [[ -n "$FILE_PATH" ]]; then
  echo "$(date -u +%FT%TZ) $FILE_PATH" >> "$EDIT_LOG"
fi

exit 0
