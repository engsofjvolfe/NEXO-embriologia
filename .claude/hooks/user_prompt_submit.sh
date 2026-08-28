#!/bin/bash
# user_prompt_submit.sh -- evento: UserPromptSubmit
#
# Roda antes de qualquer mensagem sua ser processada, em toda
# mensagem, sem exceção. Três bilhetes possíveis, cada um consultado
# pelos outros hooks desta mesma resposta -- em vez de cada um
# vasculhar a conversa inteira à procura da frase por conta própria
# (impreciso, podia pegar uma autorização de mensagens atrás por
# engano): "AUTORIZO-TRAVA:" (bypass geral, qualquer gancho), "nada a
# registrar, confirmado" (item 13 de pre_edit_safety.sh, só esse
# item), "sem alternativas reais, confirmado" (itens 14/15 do mesmo
# script, só esses). Autorização geral rejeita o caso em que o texto
# depois de "AUTORIZO-TRAVA:" é só o placeholder do exemplo
# ("<motivo>", sem nada real escrito) -- ver comentário mais abaixo.
#
# Todo bilhete é apagado a cada mensagem nova, tenha ou não a frase
# correspondente -- uma autorização vale só pra tentativa imediata,
# nunca fica "pendurada" indefinidamente.

source "$(dirname "$0")/lib/common.sh"
read_input

PROMPT=$(field '.prompt')

# Achado ao vivo nesta rodada: uma mensagem que só CITA a frase
# "AUTORIZO-TRAVA: <motivo>." -- por exemplo, colando o texto de um
# gancho de bloqueio, que usa esse padrão como exemplo de como usar a
# autorização -- batia neste grep e liberava a sessão inteira sem
# nenhuma decisão real da pessoa. Antes de aceitar, exige que o texto
# depois de "AUTORIZO-TRAVA:" NÃO COMECE com o placeholder literal
# "<motivo>" -- placeholder no início nunca é autorização de verdade,
# é o padrão do exemplo reaparecendo, mesmo com mais texto depois (ex.:
# ". valeu", ou o resto do gancho colado). Uma autorização legítima
# nunca começa dessa forma. Achado a mais, na mesma rodada: a primeira
# versão desta correção exigia que o texto inteiro fosse só
# "<motivo>" (com `$` no fim do padrão) -- não bastava, porque o texto
# capturado quase sempre tem algo depois (o resto da frase onde o
# exemplo apareceu colado). Corrigido pra checar só o início.
if echo "$PROMPT" | grep -q "AUTORIZO-TRAVA:"; then
  MOTIVO=$(echo "$PROMPT" | grep -o "AUTORIZO-TRAVA:.\{1,300\}" | head -n 1)
  RAZAO_LIMPA=$(echo "$MOTIVO" | sed -E 's/^AUTORIZO-TRAVA:[[:space:]]*//')
  if echo "$RAZAO_LIMPA" | grep -qiE '^<[[:space:]]*motivo[[:space:]]*>'; then
    : > "$AUTH_FILE"
    log_override "user_prompt_submit" "IGNORADO -- placeholder sem motivo real: $MOTIVO"
  else
    echo "$MOTIVO" > "$AUTH_FILE"
    log_override "user_prompt_submit" "$MOTIVO"
    echo "Autorização registrada para esta mensagem: $MOTIVO"
  fi
else
  : > "$AUTH_FILE"
fi

# Confirmação pontual pros itens 13 e 14/15 de pre_edit_safety.sh --
# mais estreita que AUTORIZO-TRAVA (resolve só aquele item específico,
# nunca libera o resto do gancho). Mesma regra de não ficar
# "pendurada": limpa a cada mensagem nova, escreve só quando a frase
# exata aparece nesta mensagem.
if echo "$PROMPT" | grep -qi "nada a registrar, confirmado"; then
  echo "confirmado" > "$NO_FINDING_FILE"
  log_override "user_prompt_submit" "nada a registrar, confirmado"
else
  : > "$NO_FINDING_FILE"
fi

if echo "$PROMPT" | grep -qi "sem alternativas reais, confirmado"; then
  echo "confirmado" > "$NO_ADR_FILE"
  log_override "user_prompt_submit" "sem alternativas reais, confirmado"
else
  : > "$NO_ADR_FILE"
fi

exit 0
