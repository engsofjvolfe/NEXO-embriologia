#!/bin/bash
# user_prompt_submit.sh -- evento: UserPromptSubmit
#
# Roda antes de qualquer mensagem sua ser processada, em toda
# mensagem, sem exceção. Um único trabalho: descobrir se esta
# mensagem contém "AUTORIZO-TRAVA:" e, se sim, deixar um bilhete que
# os outros hooks desta mesma resposta vão consultar -- em vez de
# cada um vasculhar a conversa inteira à procura da frase por conta
# própria (impreciso, podia pegar uma autorização de três mensagens
# atrás por engano).
#
# O bilhete é apagado a cada mensagem nova, tenha ou não a frase --
# uma autorização vale só pra tentativa imediata, nunca fica
# "pendurada" indefinidamente.

source "$(dirname "$0")/lib/common.sh"
read_input

PROMPT=$(field '.prompt')

if echo "$PROMPT" | grep -q "AUTORIZO-TRAVA:"; then
  MOTIVO=$(echo "$PROMPT" | grep -o "AUTORIZO-TRAVA:.\{1,300\}" | head -n 1)
  echo "$MOTIVO" > "$AUTH_FILE"
  log_override "user_prompt_submit" "$MOTIVO"
  echo "Autorização registrada para esta mensagem: $MOTIVO"
else
  : > "$AUTH_FILE"
fi

exit 0
