#!/bin/bash
# stop_fact_check.sh -- evento: Stop
#
# Antes deste script existir, três fatos puros (lista de worktree sem
# sobra, git status limpo, base do PR igual a "develop") eram
# checados por um hook do tipo "agent" -- uma chamada de modelo cara
# (custo + latência), a cada resposta, pra confirmar coisa que um
# script consegue confirmar sozinho, sem IA nenhuma. Isso contradiz o
# próprio princípio deste projeto (MANUAL.md, seção 1: "fato mecânico
# vira script que confere e barra, sem perguntar nada"). Este hook
# assume essa parte; o hook "agent" que continua rodando junto (ver
# settings.json) fica só com as perguntas de julgamento de verdade
# (significância pro HANDOFF.md, produção, clareza da descrição do
# PR), que exigem entendimento, não só olhar um valor.
#
# Limite honesto: isso reduz custo, mas não elimina a chamada de
# agente do Stop -- o campo "if" (que poderia pular um hook inteiro
# condicionalmente) só é avaliado em eventos de ferramenta
# (PreToolUse/PostToolUse/PostToolUseFailure/PermissionRequest/
# PermissionDenied), nunca em Stop, confirmado na documentação
# oficial de hooks. Não existe hoje um jeito de pular o hook "agent"
# do Stop com base em "esta resposta foi trivial".

source "$(dirname "$0")/lib/common.sh"
read_input

CWD=$(normalize_path "$(field '.cwd')")
CONTEXT=""

# 1) Worktree já mesclada em develop e esquecida (CLAUDE.md: "rodar
# git worktree list de vez em quando pra conferir que não sobrou
# nenhuma"). Mesclada = a branch da worktree já está contida em
# develop (git branch --merged develop já a lista).
MERGED_BRANCHES=$(git -C "$CWD" branch --merged develop --format='%(refname:short)' 2>/dev/null)
STALE_WORKTREES=""
while IFS= read -r line; do
  WT_PATH=$(echo "$line" | awk '{print $1}')
  WT_BRANCH=$(echo "$line" | grep -oE '\[[^]]+\]' | tr -d '[]')
  [[ -z "$WT_BRANCH" || "$WT_PATH" == "$CWD" ]] && continue
  if echo "$MERGED_BRANCHES" | grep -qxF "$WT_BRANCH"; then
    STALE_WORKTREES+="  - $WT_PATH (branch '$WT_BRANCH', já mesclada em develop)"$'\n'
  fi
done < <(git -C "$CWD" worktree list 2>/dev/null)
if [[ -n "$STALE_WORKTREES" ]]; then
  CONTEXT+="FATO: existe worktree já mesclada em develop e não removida:"$'\n'"$STALE_WORKTREES"
fi

# 2) git status --porcelain limpo -- só relevante se a resposta
# sinaliza entrega ("pronto", "concluíd", "finalizad").
LAST_MSG=$(field '.last_assistant_message')
if echo "$LAST_MSG" | grep -Eiq 'pronto|conclu[ií]d|finalizad'; then
  DIRTY=$(git -C "$CWD" status --porcelain 2>/dev/null)
  if [[ -n "$DIRTY" ]]; then
    CONTEXT+="FATO: a resposta sinaliza entrega, mas git status não está limpo:"$'\n'"$DIRTY"$'\n'
  fi
fi

# 3) Base do PR -- fato objetivo (nunca pergunta), só quando a
# resposta menciona PR.
if echo "$LAST_MSG" | grep -Eiq '\bPR\b|pull request'; then
  PR_JSON=$(gh pr view --json baseRefName,url 2>/dev/null)
  if [[ -n "$PR_JSON" ]]; then
    BASE=$(echo "$PR_JSON" | jq -r '.baseRefName // empty')
    if [[ "$BASE" == "main" ]]; then
      CONTEXT+="FATO: PR aberto com base em 'main' -- nunca permitido, sempre 'develop'."$'\n'
    fi
  fi
fi

if [[ -n "$CONTEXT" ]]; then
  jq -n --arg ctx "$CONTEXT" '{hookSpecificOutput: {hookEventName: "Stop", additionalContext: $ctx}}'
fi

exit 0
