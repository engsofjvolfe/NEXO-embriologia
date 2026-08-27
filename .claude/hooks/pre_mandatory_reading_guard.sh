#!/bin/bash
# pre_mandatory_reading_guard.sh -- evento: PreToolUse, matcher: "*" (toda ferramenta)
#
# CLAUDE.md, "Leitura obrigatória, fonte da verdade": a lista de seis
# documentos de leitura manual vale "antes de qualquer outra coisa" --
# não só antes de escrever código. Este gancho é a única checagem
# deste projeto que cobre esse "qualquer outra coisa" por inteiro:
# roda antes de toda ferramenta, e só deixa passar sem checar a
# própria Read (o único jeito de cumprir a exigência) e a
# TodoWrite (não afeta nada fora da própria lista de tarefas -- sem
# efeito no repositório, no sistema de arquivos ou em qualquer serviço
# externo). Qualquer outra ferramenta -- Bash, Grep, Glob, Write,
# Edit, Agent, WebFetch, WebSearch, EnterWorktree, e por aí em diante
# -- fica bloqueada até os seis documentos terem passado por Read por
# inteiro nesta sessão.
#
# `pre_edit_safety.sh` e `pre_commit_hygiene.sh` continuam com a
# mesma checagem, cada um no próprio ponto (antes da primeira escrita,
# antes do commit) -- redundância deliberada, mesmo padrão já usado no
# resto deste projeto (ver MANUAL.md, seção 5): esta checagem aqui
# cobre o caminho que nenhuma das outras duas alcança, que é qualquer
# ação anterior à primeira escrita.

source "$(dirname "$0")/lib/common.sh"
read_input

TOOL_NAME=$(field '.tool_name')

if [[ "$TOOL_NAME" == "Read" || "$TOOL_NAME" == "TodoWrite" ]]; then
  exit 0
fi

if is_authorized; then
  log_override "pre_mandatory_reading_guard" "$(authorized_reason)"
  exit 0
fi

UNREAD_DOC=$(first_unread_mandatory_doc)
if [[ -n "$UNREAD_DOC" ]]; then
  block "Bloqueado: leitura manual obrigatória ainda não feita ('$UNREAD_DOC') -- CLAUDE.md exige isso antes de qualquer outra coisa, não só antes de código. Leia (via Read) os seis documentos da lista antes de usar '$TOOL_NAME'. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
fi

exit 0
