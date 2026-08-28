# Concept — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Concept |
| Versão | 0.2.0 |
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

*Em detalhe técnico:* a fonte normativa deste módulo é o próprio
`CLAUDE.md` (raiz do repositório), direto -- sem um manual separado
descrevendo o sistema por cima dele: cada gancho, em
`.claude/hooks/*.sh` e `.claude/settings.json`, cita no próprio
comentário qual regra exata do `CLAUDE.md` ele aplica -- essa citação,
junto ao código que a aplica, é a documentação de verdade. Manter as
duas coisas juntas, no mesmo arquivo, evita o risco já confirmado uma
vez neste módulo: um documento que descreve o sistema pode ficar
desatualizado ou até errado sobre o que o código realmente faz (ver
[findings.md](findings.md)), enquanto o comentário dentro do próprio
gancho não tem como divergir de si mesmo. `MANUAL.md` (raiz) existe,
mas com outro papel: não descreve o sistema por cima do código, é um
checklist -- cada faixa de linha do `CLAUDE.md` marcada contra o
gancho real que a aplica, prova de cobertura, não narrativa.

Dentro do escopo: qualquer regra do `CLAUDE.md` que dê pra checar como
fato objetivo (existe o arquivo? o texto bate com o padrão proibido?
a ordem de edição está certa?) vira script que confere e barra sem
perguntar nada; qualquer regra que dependa de entendimento ou opinião
vira pergunta explícita, devolvida a quem pediu a tarefa, nunca
decidida sozinha; regra sobre o próprio histórico do git (branch
protegida, formato de commit) fica garantida em camada que nem
depende do Claude Code estar rodando.

Fora do escopo: o conteúdo das regras em si — isso é sempre decisão de
quem escreve o `CLAUDE.md`, nunca deste módulo; e os pontos sem
solução técnica possível, listados em
[Limites reconhecidos](#limites-reconhecidos) abaixo.

## Fluxo

*Em resumo:* pra saber o que cada gancho faz e por que, o lugar certo
é o próprio arquivo dele -- não um índice à parte.

*Em detalhe técnico:*

| Assunto | Onde está |
|---|---|
| Cada gancho do Claude Code (o que faz, qual regra do `CLAUDE.md` aplica) | `.claude/hooks/*.sh`, comentário no topo de cada arquivo |
| Funções compartilhadas (leitura de JSON, `AUTORIZO-TRAVA`, normalização de caminho) | `.claude/hooks/lib/common.sh` |
| Qual gancho liga a qual evento do Claude Code | `.claude/settings.json` |
| Hooks nativos do git (rodam fora do Claude Code, pra qualquer ferramenta) | `scripts/hooks/*` |
| Layout completo de arquivos | [`architecture.md`](architecture.md) |
| Achados confirmados por teste ao vivo | [`findings.md`](findings.md) |
| Armadilhas de ferramenta/ambiente já encontradas | [`pitfalls.md`](pitfalls.md) |

Qualquer mudança de comportamento deste módulo começa relendo o
`CLAUDE.md`, nunca um resumo dele -- `concept.md` só existe pra dar a
este módulo o ponto de entrada que todo módulo tem dentro de
`modulos/` (ver [`modulos/README.md`, Como
navegar](../../README.md#como-navegar)). Divergência entre o código
deste módulo e o que o `CLAUDE.md` decide vira achado em
`findings.md`, resolvida corrigindo o gancho, nunca reescrevendo o
`CLAUDE.md` pra bater com o que o gancho já faz.

## Limites reconhecidos

*Em resumo:* três pontos ficam sem garantia técnica plena, cada um por
um motivo específico — dois sem solução possível (provar entendimento
genuíno; confirmar que um teste manual, o preview isolado, funcionou
de verdade), um terceiro que é falta de informação disponível hoje, não
impossibilidade permanente: se um gancho decidido por IA (tipo `agent`
ou `prompt`) consegue mesmo impedir a resposta de terminar no evento
`Stop` — a documentação oficial marca esse mecanismo como
experimental e não confirma isso com um exemplo literal.

**Provar entendimento genuíno.** Dá pra forçar a ação -- reler o
arquivo antes de editar -- mas não dá pra garantir que quem leu
entendeu de verdade o que leu. Limite epistêmico, não algo que uma
ferramenta resolve.

**Confirmar que o teste no preview funcionou de verdade.** O
`CLAUDE.md`, seção "Como rodar o preview isolado", não define nenhum
sinal observável de sucesso (nem código de saída, nem arquivo de log,
nem suíte de teste) -- sem essa definição, não existe evidência pra
nenhum gancho capturar. `post_preview_track.sh` registra só que o
ambiente subiu e desceu (sinal fraco); o revisor de commit pergunta
explicitamente se foi testado de novo após mudança de código, em vez
de presumir. No dia em que essa seção do `CLAUDE.md` definir um sinal
concreto, essa checagem vira mecânica.

**Bloqueio real de gancho `agent`/`prompt` no evento `Stop`.**
Pesquisa direta na documentação oficial do Claude Code confirma o
formato de bloqueio pra `PreToolUse` com certeza (mesmo formato de um
gancho `command`), mas marca `agent`/`prompt` no evento `Stop` como
experimental, sem exemplo confirmado de bloqueio. Os ganchos de
julgamento do `Stop` usam o formato mais bem documentado disponível,
sem garantia -- a garantia real, nesse evento, é só
`stop_fact_check.sh` (gancho comum, sem IA, `exit 2` confirmado).
Pendência de confirmação ao vivo em [tasks.md](tasks.md).

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- módulo formalizado a partir do sistema de conformidade já existente em `.claude/hooks/`/`MANUAL.md`, sem repetir o conteúdo, só apontando pra ele como fonte normativa. | Criação inicial do módulo |
| 0.2.0 | 27-08-2026 | Limites reconhecidos: terceiro ponto acrescentado (bloqueio real do evento `Stop` por gancho `agent`/`prompt`, mecanismo experimental sem confirmação oficial). | Resolução de [decisions/0008](<../decisions/0008-formato-de-bloqueio-nos-ganchos-de-julgamento-do-stop.md>) |
