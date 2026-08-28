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
# Consulta a ficha (síntese, lib/common.sh) em vez de reler o diário
# inteiro -- o fato "leitura.<caminho>" é gravado por post_read_track.sh
# toda vez que o Read acontece de verdade, sem expiração dentro da
# sessão (mesmo raciocínio do item 3 abaixo: "foi lido" não é um fato
# que perde validade com o tempo, só entre sessões -- e isso já é
# coberto pelo reset em session_start_reset.sh).
if [[ "$TOOL_NAME" == "Edit" && -n "$FILE_PATH" ]]; then
  if [[ -z "$(synthesis_age "leitura.${FILE_PATH}")" ]]; then
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

# 4) Não concluir a partir do que já chegou carregado -- CLAUDE.md,
# topo do documento: "confirmar antes de escrever a correção", não
# depois. Antes, essa checagem só existia no revisor de commit
# (julgamento, tarde -- roda só quando o commit já vai acontecer,
# depois de várias edições). Movida pra cá: no momento exato da
# escrita, mecânica (sem IA) -- se o texto novo cita outro documento
# .md (por nome, não a própria edição em si), esse documento precisa
# ter sido lido por completo (via Read) nesta sessão antes.
#
# Não basta ter sido lido "em algum momento da sessão" -- pesquisa
# direta na documentação oficial confirmou que não existe forma de
# forçar uma ferramenta (Read) rodar antes de outra (Edit/Write); o
# máximo real é bloquear até existir o registro. Pra chegar mais perto
# de "lido no momento em que precisa dele" (não uma leitura antiga,
# esquecida, usada de memória várias ações depois), só aceita leitura
# dentro das últimas RECENCY_WINDOW ações registradas na ficha (síntese,
# lib/common.sh) -- não em qualquer ponto do histórico da sessão
# inteira, e sem reler o diário pra descobrir isso (synthesis_fresh
# consulta só o fato já resumido). Heurística, com falso positivo
# possível (citar um nome não é sempre "depender" da convenção dele) --
# por isso autorizável.
RECENCY_WINDOW=20
NEW_CONTENT=""
if [[ "$TOOL_NAME" == "Write" ]]; then
  NEW_CONTENT=$(field '.tool_input.content')
elif [[ "$TOOL_NAME" == "Edit" ]]; then
  NEW_CONTENT=$(field '.tool_input.new_string')
fi
if [[ -n "$NEW_CONTENT" ]]; then
  CITED_FILES=$(echo "$NEW_CONTENT" | grep -oE '[A-Za-z0-9_./-]+\.md' | sort -u)
  SELF_BASENAME=$(basename "$FILE_PATH" 2>/dev/null)
  for cited in $CITED_FILES; do
    cited_base=$(basename "$cited")
    [[ "$cited_base" == "$SELF_BASENAME" ]] && continue
    if ! synthesis_fresh "leitura.${cited_base}" "$RECENCY_WINDOW"; then
      if [[ -n "$(synthesis_age "leitura.${cited_base}")" ]]; then
        block "Bloqueado: este texto cita '$cited', que já foi lido nesta sessão, mas não entre as últimas $RECENCY_WINDOW ações -- pode estar desatualizado na memória, não confirmado agora. CLAUDE.md, topo: 'não concluir... a partir do que já chegou carregado, confirmar antes de escrever a correção'. Releia '$cited' de novo antes, ou use AUTORIZO-TRAVA: <motivo>."
      else
        block "Bloqueado: este texto cita '$cited' mas não encontrei rastro de leitura completa (via Read) dele nesta sessão -- CLAUDE.md, topo: 'não concluir... a partir do que já chegou carregado, confirmar antes de escrever a correção'. Releia '$cited' antes, ou, se já leu de outro jeito (ou a citação não depende do conteúdo dele), use AUTORIZO-TRAVA: <motivo>."
      fi
    fi
  done
fi

# 5) Ordem completa concept.md -> architecture.md -> schemas/ ->
# implementação, no momento da própria edição -- não só no commit.
# `pre_commit_hygiene.sh` #5 já confere isso, mas só quando o commit
# já vai acontecer -- dá pra escrever a sessão inteira fora de ordem e
# só descobrir no fim, depois de já ter perdido o trabalho. Consulta a
# ficha (síntese, lib/common.sh) em vez do diário: como
# `post_edit_track.sh` marca "edicao.<mod>.<etapa>" só DEPOIS da edição
# acontecer, o que já está na ficha agora reflete só as edições
# anteriores a esta -- comparação segura, sem reler edit-order.log.
if [[ -n "$FILE_PATH" && "$FILE_PATH" == *"/modulos/"* ]]; then
  MOD=$(echo "$FILE_PATH" | sed -E 's#.*/modulos/([^/]+)/.*#\1#')
  if [[ "$MOD" != "_template" ]]; then
    IS_ARCH=false; [[ "$FILE_PATH" == *"modulos/$MOD/docs/architecture.md" ]] && IS_ARCH=true
    IS_SCHEMA=false; [[ "$FILE_PATH" == *"modulos/$MOD/schemas/"* ]] && IS_SCHEMA=true
    IS_CODE=false; [[ "$FILE_PATH" == *"modulos/$MOD/"* && "$FILE_PATH" != *"/docs/"* && "$FILE_PATH" != *"/schemas/"* && "$FILE_PATH" != *"/decisions/"* ]] && IS_CODE=true
    if $IS_ARCH && [[ -z "$(synthesis_age "edicao.${MOD}.concept")" ]]; then
      block "Bloqueado: tentando editar architecture.md em $MOD antes de concept.md ter sido tocado nesta sessão -- CLAUDE.md, ordem completa (concept.md -> architecture.md -> schemas/ -> implementação). Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
    if $IS_SCHEMA && [[ -z "$(synthesis_age "edicao.${MOD}.architecture")" ]]; then
      block "Bloqueado: tentando editar schemas/ em $MOD antes de architecture.md ter sido tocado nesta sessão. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
    if $IS_CODE && [[ -z "$(synthesis_age "edicao.${MOD}.schemas")" && -z "$(synthesis_age "edicao.${MOD}.architecture")" ]]; then
      block "Bloqueado: tentando editar implementação em $MOD antes de architecture.md/schemas/ terem sido tocados nesta sessão. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
    fi
  fi
fi

# 6) handoff.md sempre a última coisa tocada no módulo -- mesmo motivo
# do item 5: `pre_commit_hygiene.sh` #8 já confere isso, só que tarde
# demais (no commit). Aqui, no momento da edição, consultando a ficha:
# se handoff.md deste módulo já foi tocado nesta sessão, qualquer outra
# edição em modulos/<mod>/docs/ depois disso bloqueia.
if [[ -n "$FILE_PATH" && "$FILE_PATH" == *"/modulos/"*"/docs/"* && "$FILE_PATH" != *"/docs/handoff.md" ]]; then
  MOD2=$(echo "$FILE_PATH" | sed -E 's#.*/modulos/([^/]+)/docs/.*#\1#')
  if [[ "$MOD2" != "_template" && -n "$(synthesis_age "edicao.${MOD2}.handoff")" ]]; then
    block "Bloqueado: handoff.md de $MOD2 já foi tocado nesta sessão -- CLAUDE.md: 'handoff.md é sempre a última coisa tocada dentro do módulo'. Se precisar editar mais algo em docs/, isso deveria vir antes de handoff.md. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
  fi
fi

# 7) Módulo novo sem linha em modulos/README.md -- mesmo motivo dos
# itens 5 e 6: `pre_commit_hygiene.sh` #10 já confere isso, só no
# commit. Aqui, via ficha: assim que concept.md de um módulo novo é
# criado (Write, primeira vez -- ainda sem fato "edicao.<mod>.concept"
# registrado), qualquer edição seguinte que não seja o próprio
# modulos/README.md bloqueia, até essa linha existir.
if [[ -n "$FILE_PATH" && "$FILE_PATH" == *"/modulos/"* && "$FILE_PATH" != *"/modulos/README.md" ]]; then
  MOD3=$(echo "$FILE_PATH" | sed -E 's#.*/modulos/([^/]+)/.*#\1#')
  if [[ "$MOD3" != "_template" && -n "$MOD3" ]]; then
    CONCEPT_EXISTS_BEFORE=false
    [[ -n "$(synthesis_age "edicao.${MOD3}.concept")" ]] && CONCEPT_EXISTS_BEFORE=true
    IS_NEW_CONCEPT_WRITE=false
    [[ "$TOOL_NAME" == "Write" && "$FILE_PATH" == *"modulos/$MOD3/docs/concept.md" ]] && IS_NEW_CONCEPT_WRITE=true
    README_TOUCHED=false
    [[ -n "$(synthesis_age "edicao.modulos-readme")" ]] && README_TOUCHED=true
    if ( $CONCEPT_EXISTS_BEFORE || $IS_NEW_CONCEPT_WRITE ) && ! $README_TOUCHED; then
      # Só é "módulo novo" de verdade se a pasta não existia antes desta sessão
      if [[ ! -d "${CWD%.claude/worktrees/*}modulos/$MOD3" ]] || $IS_NEW_CONCEPT_WRITE; then
        block "Bloqueado: módulo '$MOD3' parece novo (concept.md criado nesta sessão) mas modulos/README.md ainda não foi tocado -- CLAUDE.md: módulo novo precisa de linha na tabela antes de continuar. Se isso for engano (módulo já existia antes), use AUTORIZO-TRAVA: <motivo>."
      fi
    fi
  fi
fi

# 8) tasks.md de módulo mudando de "Em aberto" vazia pra não-vazia (ou
# o contrário) sem TASKS.md (raiz) acompanhar -- mesmo motivo dos
# itens 5 a 7. Sem depender de nada além de bash/awk/grep (nenhuma
# dependência nova): pra Write, compara o arquivo inteiro; pra Edit,
# compara só o fragmento trocado (old_string/new_string) -- heurística
# um pouco menos precisa que reconstruir o arquivo inteiro, mas
# suficiente pro caso real (a edição que faz essa transição sempre
# toca a própria linha "- [ ]" dentro do fragmento).
if [[ -n "$FILE_PATH" && "$FILE_PATH" == *"/modulos/"*"/docs/tasks.md" ]]; then
  OLD_HAS_ITEM=false
  NEW_HAS_ITEM=false
  if [[ "$TOOL_NAME" == "Write" ]]; then
    [[ -f "$FILE_PATH" ]] && OLD_SECTION=$(awk '/^## Em aberto/{flag=1;next}/^## /{flag=0}flag' "$FILE_PATH" 2>/dev/null)
    echo "$OLD_SECTION" | grep -q '^- \[ \]' && OLD_HAS_ITEM=true
    NEW_CONTENT_W=$(field '.tool_input.content')
    NEW_SECTION=$(echo "$NEW_CONTENT_W" | awk '/^## Em aberto/{flag=1;next}/^## /{flag=0}flag')
    echo "$NEW_SECTION" | grep -q '^- \[ \]' && NEW_HAS_ITEM=true
  elif [[ "$TOOL_NAME" == "Edit" ]]; then
    OLD_STRING=$(field '.tool_input.old_string')
    NEW_STRING=$(field '.tool_input.new_string')
    echo "$OLD_STRING" | grep -q '^- \[ \]' && OLD_HAS_ITEM=true
    echo "$NEW_STRING" | grep -q '^- \[ \]' && NEW_HAS_ITEM=true
  fi
  if [[ "$OLD_HAS_ITEM" != "$NEW_HAS_ITEM" ]] && [[ -z "$(synthesis_age "edicao.tasks-raiz")" ]]; then
    block "Bloqueado: esta edição parece mudar se $FILE_PATH tem pendência aberta (item '- [ ]' aparecendo ou sumindo), mas TASKS.md (raiz) ainda não foi tocado nesta sessão. Se isso for engano (o item '- [ ]' na edição não é sobre a seção 'Em aberto'), use AUTORIZO-TRAVA: <motivo>."
  fi
fi

# Fragmento de texto novo desta edição -- reaproveita o mesmo cálculo
# do item 4 acima (Write -> content inteiro; Edit -> só new_string,
# porque no momento do PreToolUse o arquivo em disco ainda tem o
# conteúdo ANTIGO -- não dá pra reconstruir o arquivo novo inteiro
# aqui, mesma limitação já aceita no item 4).
NEW_FRAGMENT="$NEW_CONTENT"

# 9) Nunca usar emoji em nada escrito no projeto -- achado na
# releitura desta rodada: essa regra (CLAUDE.md, Regras gerais) só
# tinha checagem mecânica no commit (pre_commit_hygiene.sh #2), tarde
# demais -- editava-se o arquivo inteiro com emoji sem trava nenhuma
# até o momento de commitar. Aqui, no momento da própria edição, fato
# de texto sem exceção (mesma função compartilhada de lib/common.sh).
if [[ -n "$NEW_FRAGMENT" ]] && echo "$NEW_FRAGMENT" | has_emoji; then
  block "Bloqueado: encontrei um emoji neste texto -- CLAUDE.md, Regras gerais: 'nunca usar emojis em nada escrito neste projeto (código, docs, commits, chat)'. Sem exceção, não é autorizável."
fi

# 10) Pureza de esquema de dado (sem campo description/example) --
# mesmo achado do item 9: só existia checagem no commit
# (pre_commit_hygiene.sh #3/#3b). Aqui: se o arquivo é um
# schemas/*.json, o fragmento novo não pode ter "description"/
# "example"; se é qualquer .md, um bloco ```yaml/```json embutido no
# fragmento não pode ter esses campos junto com required/properties+type.
if [[ -n "$NEW_FRAGMENT" ]]; then
  if [[ "$FILE_PATH" == *"/schemas/"*".json" ]]; then
    if echo "$NEW_FRAGMENT" | grep -Eq '"description"|"example"'; then
      block "Bloqueado: este esquema tem campo 'description' ou 'example' -- CLAUDE.md, Regras gerais: esquema de dado carrega só dado puro. Sem exceção, não é autorizável."
    fi
  elif [[ "$FILE_PATH" == *.md ]]; then
    if echo "$NEW_FRAGMENT" | schema_block_impure; then
      block "Bloqueado: este texto tem um bloco de esquema embutido (\`\`\`yaml ou \`\`\`json) com campo 'description' ou 'example' -- CLAUDE.md, Regras gerais: esquema de dado carrega só dado puro, mesmo embutido num documento. Sem exceção, não é autorizável."
    fi
  fi
fi

# 11) Linha de Licença em tabela de cabeçalho ("Campo | Valor") -- só
# existia checagem no commit (pre_commit_hygiene.sh #6). Aqui: só dá
# pra checar com confiança quando o fragmento novo contém a própria
# linha de cabeçalho da tabela (criação do arquivo via Write, ou uma
# Edit que reescreve essa tabela) -- reconstruir o arquivo inteiro pra
# achar uma tabela de cabeçalho que já existia antes desta edição não
# é possível aqui (mesma limitação do item 9/10). Fora desse caso, o
# commit continua sendo o backstop de certeza.
if [[ -n "$NEW_FRAGMENT" && "$FILE_PATH" == *.md ]]; then
  if echo "$NEW_FRAGMENT" | grep -qE '^\s*\|?\s*Campo\s*\|\s*Valor' && ! echo "$NEW_FRAGMENT" | grep -qi 'Licen'; then
    block "Bloqueado: este texto cria/reescreve uma tabela de cabeçalho ('Campo | Valor') sem linha de Licença -- CLAUDE.md, Regras gerais. Se não for cabível pra este documento, use AUTORIZO-TRAVA: <motivo>."
  fi
fi

# 12) "Antes e depois" em documento nunca versionado -- só existia
# checagem no commit (pre_commit_hygiene.sh #7). "Nunca versionado" =
# sem nenhum commit no histórico do git pra este caminho; se o git em
# si não responder (fora de repositório, comando falhando), pula a
# checagem em vez de bloquear tudo por engano (fail-open só aqui,
# porque o oposto -- tratar toda falha de git como "nunca versionado"
# -- bloquearia edição de arquivo antigo também).
if [[ -n "$NEW_FRAGMENT" && "$FILE_PATH" == *.md ]] && git -C "$CWD" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  HAS_HISTORY=$(git -C "$CWD" log --oneline -- "$FILE_PATH" 2>/dev/null | head -n 1)
  if [[ -z "$HAS_HISTORY" ]] && echo "$NEW_FRAGMENT" | grep -Eiq 'era assim|ficou assim|antes:|anteriormente era|mudou de.*para'; then
    block "Bloqueado: $FILE_PATH nunca foi versionado (sem commit no histórico) e este texto usa linguagem de 'antes e depois' -- CLAUDE.md, Regras gerais: o que está sendo implementado e nunca foi versionado não entra como correção. Se isso for engano, use AUTORIZO-TRAVA: <motivo>."
  fi
fi

exit 0
