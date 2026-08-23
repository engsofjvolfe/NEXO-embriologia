#!/bin/bash
# pre_git_rules.sh -- evento: PreToolUse, filtro: Bash(git *) e Bash(gh pr create *)

source "$(dirname "$0")/lib/common.sh"
read_input

COMMAND=$(field '.tool_input.command')
CWD=$(normalize_path "$(field '.cwd')")
TRANSCRIPT=$(field '.transcript_path')

BRANCH=$(git -C "$CWD" branch --show-current 2>/dev/null)

IS_REWRITE=false
if echo "$COMMAND" | grep -Eq '\brebase\b|\bcommit[[:space:]]+--amend\b|\breset[[:space:]]+--hard\b|\bpush[[:space:]]+--force\b|[[:space:]]-f([[:space:]]|$)'; then
  IS_REWRITE=true
fi

# 1) Reescrever histórico em develop/main -- NUNCA tem chave, sem exceção
if [[ "$BRANCH" == "develop" || "$BRANCH" == "main" ]] && $IS_REWRITE; then
  block "Bloqueado: reescrever histórico não é permitido em develop/main, sem exceção -- não há AUTORIZO-TRAVA que libere isso."
fi

# 1b) Commit novo direto em develop/main -- NUNCA tem chave, sem exceção.
# Todo trabalho novo nasce numa branch de tarefa, numa worktree própria;
# develop/main só recebem conteúdo por "git merge --no-ff" (Passo 7 do
# fluxo completo), nunca por "git commit" direto. Cuidado: o merge em
# si roda com develop como branch atual e produz um commit de merge --
# isso não pode ser bloqueado, então a checagem é só sobre "git commit"
# escrito por extenso no comando, nunca sobre "git merge".
if [[ "$BRANCH" == "develop" || "$BRANCH" == "main" ]] && echo "$COMMAND" | grep -Eq '\bgit[[:space:]]+commit\b' && ! echo "$COMMAND" | grep -Eq '\bgit[[:space:]]+merge\b'; then
  block "Bloqueado: commit novo direto em $BRANCH não é permitido, sem exceção -- todo trabalho nasce numa branch de tarefa, numa worktree própria (ver CLAUDE.md, Trabalho em múltiplas frentes). $BRANCH só recebe conteúdo por 'git merge --no-ff', nunca por 'git commit' direto."
fi

# 2) Merge sem --no-ff -- sem chave, é trivial de corrigir direto
if [[ "$BRANCH" == "develop" ]] && echo "$COMMAND" | grep -Eq '\bgit[[:space:]]+merge\b'; then
  if ! echo "$COMMAND" | grep -q -- '--no-ff'; then
    block "Bloqueado: merge em develop sempre usa --no-ff. Só adicionar a flag, não precisa de autorização."
  fi
fi

# 3) Base do PR tem que ser develop, nunca main -- fato objetivo, não
# julgamento: gh pr view devolve isso com certeza depois de criado.
if echo "$COMMAND" | grep -Eq '\bgh[[:space:]]+pr[[:space:]]+create\b'; then
  if echo "$COMMAND" | grep -Eq '\-\-base[[:space:]]+main\b'; then
    block "Bloqueado: PR nunca vai direto pra main, sempre pra develop."
  fi
fi

# gradlew --stop automático antes de remover worktree via comando
# manual (defesa em profundidade -- o hook nativo WorktreeRemove
# cobre o caminho --worktree, este cobre o "git worktree remove"
# digitado à mão, que é como o CLAUDE.md deste projeto instrui)
if echo "$COMMAND" | grep -Eq '\bgit[[:space:]]+worktree[[:space:]]+remove\b'; then
  WT_PATH=$(echo "$COMMAND" | sed -E 's#.*worktree[[:space:]]+remove[[:space:]]+##' | awk '{print $1}')
  FULL_WT_PATH="${CWD}/${WT_PATH}"
  if [[ -f "${FULL_WT_PATH}/gradlew" ]]; then
    (cd "${FULL_WT_PATH}" && ./gradlew --stop) >/dev/null 2>&1
  fi
fi

# A partir daqui: checagens heurísticas, autorizáveis com AUTORIZO-TRAVA
if is_authorized; then
  log_override "pre_git_rules" "$(authorized_reason)"
  exit 0
fi

# 4) Investigar antes de reescrever (qualquer branch)
if $IS_REWRITE && [[ -f "$TRANSCRIPT" ]]; then
  if ! grep -Eq 'git log|git reflog|git ls-remote|git fetch' "$TRANSCRIPT"; then
    block "Bloqueado: antes de reescrever histórico, confira o estado real primeiro (git log, git reflog, git ls-remote ou git fetch). Se já conferiu de outro jeito, use AUTORIZO-TRAVA: <motivo>."
  fi
fi

# 5) Trocar de branch fora de worktree
if echo "$COMMAND" | grep -Eq '\bgit[[:space:]]+(checkout|switch)\b' && [[ "$CWD" != *"/.claude/worktrees/"* ]]; then
  if ! echo "$COMMAND" | grep -Eq '\bcheckout\b.*--[[:space:]]'; then
    block "Bloqueado: trocar de branch fora de uma worktree. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
  fi
fi

exit 0
