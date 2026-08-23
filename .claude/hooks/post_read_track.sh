#!/bin/bash
# post_read_track.sh -- evento: PostToolUse, matcher: Read
#
# Melhoria em cima da primeira versão: em vez de vasculhar o arquivo
# de transcript inteiro (formato menos previsível) atrás de "Read"
# perto de um caminho, este hook grava um registro limpo, próprio,
# toda vez que a ferramenta Read é usada de verdade. As checagens de
# leitura obrigatória e de "reler antes de editar" passam a consultar
# este arquivo, não o transcript bruto.

source "$(dirname "$0")/lib/common.sh"
read_input

FILE_PATH=$(normalize_path "$(field '.tool_input.file_path')")
OFFSET=$(field '.tool_input.offset')
LIMIT=$(field '.tool_input.limit')

# CLAUDE.md: "é lido na íntegra: sem ferramenta de resumo, sem corte,
# sem exceção" -- vale pra qualquer documento do projeto, não só a
# lista de 22 obrigatórios. Um Read com offset/limit é leitura
# parcial, de propósito -- não conta como "lido" pras checagens de
# leitura obrigatória nem de "reler antes de editar". Vai pro log
# separado (rastro, não apaga o pedido), nunca no read-log.txt
# principal, que as checagens conferem.
if [[ -n "$FILE_PATH" ]]; then
  if [[ -n "$OFFSET" || -n "$LIMIT" ]]; then
    echo "$(date -u +%FT%TZ) $FILE_PATH (parcial: offset=${OFFSET:-0} limit=${LIMIT:-?})" >> "${STATE_DIR}/partial-read-log.txt"
  else
    echo "$(date -u +%FT%TZ) $FILE_PATH" >> "${STATE_DIR}/read-log.txt"
  fi
fi

exit 0
