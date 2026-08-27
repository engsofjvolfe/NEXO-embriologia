# Concept — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Concept |
| Versão | 0.1.0 |
| Data | 27-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Descreve o desenho pretendido do módulo — o que ele deve ser e como
> deve se comportar. É sempre o primeiro passo do módulo, com código
> já existente ou não -- guia o resto (`architecture.md`, `schemas/`,
> implementação).
>
> Se o módulo já tem código, `analysis.md`/`findings.md` checam, a
> qualquer momento, se esse código bate com o requisito que este
> arquivo descreve -- nunca o contrário: descobrir o que o código já
> faz não muda o que este arquivo diz que deveria fazer. Divergência
> vira pendência em `tasks.md`, resolvida corrigindo o código, nunca
> reescrevendo este arquivo pra bater com ele.
>
> Não é um relato de investigação (isso é `analysis.md`) nem uma lista
> de achados (isso é `findings.md`). Edita-se por cima pra ajustes
> pequenos ao mesmo desenho; se o conceito inteiro for repensado, este
> arquivo é substituído por um novo (a troca em si vira um registro em
> `decisions/`), não silenciosamente reescrito por cima do que havia
> antes.
>
> Cada seção segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

## Índice
- [Escopo](#escopo)
- [Fluxo](#fluxo)
- [Limites reconhecidos](#limites-reconhecidos)
- [Controle de versão](#controle-de-versão)

## Escopo

*Em resumo:* este módulo é o mecanismo que transforma as regras do
`CLAUDE.md` do projeto em travas de verdade — coisas que impedem uma
ação errada de acontecer, em vez de instruções que dependem só de
serem lembradas numa sessão longa. Não decide quais são as regras
(isso é sempre o `CLAUDE.md`) — decide como cada regra já escrita lá é
aplicada de verdade.

*Em detalhe técnico:* o que este módulo deve ser e como deve se
comportar já está inteiramente decidido em
[`MANUAL.md`](../../../MANUAL.md), na raiz do repositório — a ideia
central (três categorias de regra: fato mecânico, julgamento, estado
do repositório em si), o modelo de quatro camadas (proteção de branch
do GitHub, hooks nativos do git, Vale, Claude Code), e a referência
completa, regra por regra do `CLAUDE.md`, contra o mecanismo que a
aplica. Este `concept.md` não repete esse conteúdo — aponta pra ele
como fonte normativa direta, mesmo padrão já usado por
[`modulos/motor/docs/concept.md`](<../../motor/docs/concept.md>) em
relação à cascata de documentos do motor.

Dentro do escopo: qualquer regra do `CLAUDE.md` que dê pra checar como
fato objetivo (existe o arquivo? o texto bate com o padrão proibido?
a ordem de edição está certa?) vira script que confere e barra sem
perguntar nada; qualquer regra que dependa de entendimento ou opinião
vira pergunta explícita, devolvida a quem pediu a tarefa, nunca
decidida sozinha; regra sobre o próprio histórico do git (branch
protegida, formato de commit) fica garantida em camada que nem
depende do Claude Code estar rodando.

Fora do escopo: o conteúdo das regras em si — isso é sempre decisão de
quem escreve o `CLAUDE.md`, nunca deste módulo; e os dois pontos sem
solução técnica possível, listados em
[Limites reconhecidos](#limites-reconhecidos) abaixo.

## Fluxo

*Em resumo:* cada peça deste sistema — o que cada camada cobre, como
os scripts e os revisores automáticos decidem, como usar no dia a dia,
como instalar — já está descrita por inteiro no `MANUAL.md`.

*Em detalhe técnico:* ponto de entrada pra cada assunto, dentro do
documento já existente:

| Assunto | Onde está decidido |
|---|---|
| Ideia central (três categorias de regra) | [`MANUAL.md`](../../../MANUAL.md), seção 1 |
| Modelo de quatro camadas | `MANUAL.md`, seção 2 |
| Referência completa -- cada regra do `CLAUDE.md`, sua camada, seu mecanismo | `MANUAL.md`, seção 3 |
| O que acontece quando algo é bloqueado (`AUTORIZO-TRAVA`) | `MANUAL.md`, seção 4 |
| Como os revisores semânticos decidem (processo de verificação em quatro etapas) | `MANUAL.md`, seção 5 |
| Uso no dia a dia (painel ao vivo, checagem sob demanda) | `MANUAL.md`, seção 6 |
| Instalação | `MANUAL.md`, seção 7 |
| Layout de arquivos | `MANUAL.md`, seção 8; ver também [`architecture.md`](architecture.md) |
| Achados confirmados por teste ao vivo | `MANUAL.md`, seção 9, e [`findings.md`](findings.md) deste módulo |

Qualquer mudança de comportamento deste módulo começa por `MANUAL.md`,
não por este arquivo — `concept.md` só existe pra apontar pra ele e
pra dar a este módulo o ponto de entrada que todo módulo tem dentro de
`modulos/` (ver [`modulos/README.md`, Como
navegar](../../README.md#como-navegar)). Divergência entre o código
deste módulo e o que `MANUAL.md` decidiu vira achado em `findings.md`,
nunca motivo pra reescrever o manual.

## Limites reconhecidos

*Em resumo:* dois pontos não têm solução técnica possível, cada um por
um motivo específico, não por serem difíceis — provar entendimento
genuíno, e confirmar que um teste manual (preview isolado) funcionou
de verdade.

*Em detalhe técnico:* ver `MANUAL.md`, seção 10, pro texto completo de
cada limite e o motivo exato.

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- módulo formalizado a partir do sistema de conformidade já existente em `.claude/hooks/`/`MANUAL.md`, sem repetir o conteúdo, só apontando pra ele como fonte normativa. | Criação inicial do módulo |
