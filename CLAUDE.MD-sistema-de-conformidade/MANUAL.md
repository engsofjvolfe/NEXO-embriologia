# Manual -- Sistema de conformidade do CLAUDE.md

Este manual descreve um sistema que transforma as regras do seu
`CLAUDE.md` em travas de verdade -- coisas que impedem uma ação
errada de acontecer, em vez de instruções que dependem só de serem
lembradas. Cobre o que o sistema é, como cada peça funciona, como
instalar, como usar no dia a dia, e onde ele para (com motivo).

## 1. Ideia central

Um documento de instruções (`CLAUDE.md`) compete por atenção com
tudo mais numa sessão longa -- pode ficar enterrado, pode ser
resumido, pode ser esquecido. Este sistema resolve isso separando
cada regra em uma de três categorias, e tratando cada uma do jeito
que ela exige:

- **Fato mecânico** (existe ou não existe, bate ou não bate) --
  vira um script que confere e barra, sem perguntar nada.
- **Julgamento** (depende de opinião, não só de olhar um arquivo) --
  nunca é decidido sozinho por um script ou por um revisor
  automático. Vira uma pergunta explícita, você responde, e a
  resposta fica registrada.
- **Estado do repositório em si** (histórico do git, branch
  protegida, formato de PR) -- fica garantido em camadas que nem
  dependem do Claude Code estar rodando: hooks nativos do git, e
  proteção de branch do próprio GitHub.

## 2. As quatro camadas, da mais forte pra mais específica

```
┌─────────────────────────────────────────────────────┐
│ 1. GitHub (proteção de branch)                       │  mais forte --
│    develop/main recusam force-push e exclusão        │  ninguém local
│    direto no servidor, não importa a ferramenta.     │  derruba sozinho
├─────────────────────────────────────────────────────┤
│ 2. Git nativo (.githooks/)                           │  vale pra
│    commit-msg, pre-rebase, pre-push -- rodam pra      │  qualquer
│    qualquer commit/rebase/push, de qualquer           │  ferramenta,
│    ferramenta, não só do Claude Code.                 │  local
├─────────────────────────────────────────────────────┤
│ 3. Vale (opcional, .vale.ini)                        │  estilo de
│    Confere frases de tom proibidas entendendo         │  prosa, sem
│    markdown de verdade (ignora blocos de código).     │  julgamento
├─────────────────────────────────────────────────────┤
│ 4. Claude Code (.claude/hooks/ + .claude/agents/)    │  comportamento
│    Tudo que é específico de como o agente trabalha:   │  do agente,
│    leitura obrigatória, worktree, fluxo de docs,      │  julgamento
│    tom, ADR, pausa antes de ação sensível.            │  quando precisa
└─────────────────────────────────────────────────────┘
```

Cada camada cobre o que ela sabe cobrir de verdade. Regra de
histórico do git não precisa de um agente de IA julgando -- precisa
de um hook de git. Regra de tom de um documento precisa de alguém
(ou algo) que entenda o texto -- isso cabe ao Claude Code.

## 3. Referência completa -- cada regra, sua camada, seu mecanismo

### Leitura e disciplina de sessão

| Regra | Mecanismo |
|---|---|
| Ler documentos na íntegra, sem resumo, sem corte | `post_read_track.sh` registra cada leitura real (caminho normalizado -- ver seção 9.3), separando leitura completa de parcial -- ver seção 9.9; `pre_edit_safety.sh` e `pre_commit_hygiene.sh` exigem leitura completa antes de agir |
| Não concluir a partir do que já carregou, tratar como hipótese | `pre_edit_safety.sh` -- bloqueia editar um arquivo sem reabri-lo (via Read) nesta sessão |
| Lista de leitura manual obrigatória (6 documentos -- os outros 16 chegam via `@caminho`, ver seção 9.8) | `pre_edit_safety.sh` (antes da primeira edição, Portão pro Passo 2) + `pre_commit_hygiene.sh` (segunda conferência, no commit) |
| Lembrete depois de compactação de contexto | `SessionStart` (matcher `compact`) reinjeta as regras mais críticas |

### Fluxo de escrita e revisão de documentação

| Regra | Mecanismo |
|---|---|
| `concept.md` primeiro, nunca reescrito pra bater com código | Revisor de commit -- processo de verificação em 4 etapas (ver seção 5) |
| `architecture.md`, `schemas/`, `analysis.md`, `findings.md`, `pitfalls.md` conforme o caso | Revisor de commit, mesmo processo |
| Ordem completa `concept.md` → `architecture.md` → `schemas/` → implementação | `pre_commit_hygiene.sh` -- confere as três etapas da ordem real de edição via `edit-order.log` (ver seção 9.11) |
| ADR quando há escolha real entre alternativas | Revisor de commit -- pergunta de julgamento, nunca decidida sozinha |
| `tasks.md` refletindo o estado atual | Revisor de commit |
| `handoff.md` sempre a última coisa tocada no módulo | `pre_commit_hygiene.sh`, usa `edit-order.log` |
| Documentos gerais da raiz (`modulos/README.md`, `TASKS.md`, `HANDOFF.md`) | Revisor do fim da resposta (evento `Stop`) |
| Tom impessoal, exceção `analysis.md` | Revisor de commit (semântico) + Vale opcional (frases fixas, mecânico) |
| Esquema de dado sem `description`/`example` | `pre_commit_hygiene.sh` |
| `CLAUDE.md` e o mecanismo que o aplica não derivam um do outro sem aviso | `pre_commit_hygiene.sh` -- aviso (autorizável) quando `CLAUDE.md` muda sem nenhum arquivo de hook mudar junto no mesmo commit |

### Commits, histórico, branches

| Regra | Mecanismo |
|---|---|
| Trailer `Co-Authored-By` proibido | `pre_commit_hygiene.sh` + `scripts-hooks/commit-msg` (vale mesmo fora do Claude Code -- destino real é a pasta de `core.hooksPath`, ver seção 7) |
| Formato do commit (título, bullet points) | `scripts-hooks/commit-msg` (mecânico) + revisor de commit (idioma, nuance) |
| Nunca reescrever `develop`/`main` | `pre_git_rules.sh` + `scripts-hooks/pre-rebase` + `scripts-hooks/pre-push` + proteção de branch no GitHub |
| Nunca commit novo direto em `develop`/`main` (só via merge) | `pre_git_rules.sh` -- bloqueia `git commit` nessas branches, sem exceção; `git merge --no-ff` continua liberado |
| Investigar antes de reescrever histórico | `pre_git_rules.sh` |
| Merge sempre com `--no-ff` | `pre_git_rules.sh` |
| Nunca merge por iniciativa própria | Pausa obrigatória (`permissions.ask`) |
| Worktree própria por tarefa, nunca checkout direto na pasta principal | `pre_git_rules.sh` (troca de branch) + `pre_edit_safety.sh` (qualquer escrita) |
| `gradlew --stop` antes de remover worktree | `pre_git_rules.sh` (caminho manual) + `worktree_remove_cleanup.sh` (evento nativo, reforço) |
| Nenhuma worktree esquecida (mesclada e não removida) | `stop_fact_check.sh` (fato mecânico: branch já contida em `develop`) + revisor do fim da resposta |
| Manter a pasta puxada (`git pull origin develop`) após merge | `post_merge_reminder.sh` -- lembrete depois de `git push`/`gh pr merge` |

### Pull requests

| Regra | Mecanismo |
|---|---|
| PR nunca direto pra `main` | `pre_git_rules.sh` (bloqueia `--base main`) + `stop_fact_check.sh` confere `baseRefName` como fato mecânico (sem IA) |
| PR realmente aberto, não só "pronto pra abrir" | Revisor do fim da resposta (`gh pr view`) |
| Descrição clara do PR | `.github/pull_request_template.md` (estrutura obrigatória) + revisor do fim da resposta pergunta se está vaga |
| Aprovação explícita antes do merge | Pausa obrigatória (`permissions.ask`) |

### Ferramentas e regras gerais

| Regra | Mecanismo |
|---|---|
| Nunca `AskUserQuestion` | Bloqueio direto, incondicional |
| Agent/Grep/Glob só quando pedido | Pausa obrigatória (`permissions.ask`, regras `"Agent"`/`"Grep"`/`"Glob"` -- nomes corrigidos, ver seção 9.7: `"Task(*)"` nunca funcionou, `Task` não existe como ferramenta) |
| Mesma proibição vale pra `ls`/`grep`/`find`/`sed` via Bash usados como busca ampla | `pre_bash_search_guard.sh` -- escala pra confirmação; `ls` de uma pasta específica continua liberado (mesmo recorte já usado em memória do projeto) |
| Nunca emoji (código, docs, commits, **chat**) | `pre_commit_hygiene.sh` (arquivo alterado + mensagem do commit, ver seção 9.10) + revisor de tom no fim da resposta (resposta de chat) |
| Linha de Licença em tabela de cabeçalho | `pre_commit_hygiene.sh` |
| Documento novo sem linguagem de "antes e depois" | `pre_commit_hygiene.sh` |
| Respostas sempre em português, termo técnico explicado | Revisor de tom no fim da resposta |

### Teste no preview isolado

| Regra | Mecanismo |
|---|---|
| Testar antes de abrir PR | `post_preview_track.sh` registra tentativas (sinal fraco); revisor do fim da resposta pergunta explicitamente se foi testado de novo após mudança de código -- **ver limite na seção 6** |

## 4. O que acontece quando algo é bloqueado

Toda vez que uma checagem mecânica barra algo, a mensagem de erro diz
exatamente o que falta. Quando é uma checagem heurística (com chance
real de falso positivo) ou uma pergunta de julgamento, a mensagem
também diz como liberar: escrevendo `AUTORIZO-TRAVA: <motivo>` na sua
próxima mensagem.

- A frase é detectada assim que você manda a mensagem (evento
  `UserPromptSubmit`), antes de qualquer outra coisa rodar.
- Vale só pra essa tentativa imediata -- na mensagem seguinte, se não
  repetir a frase, a autorização já não existe mais.
- Fica registrada, com data e motivo, em
  `.claude/hooks/state/overrides.log` -- nada passa em silêncio.
- **Não funciona** em develop/main nunca serem reescritas, nem no
  `--no-ff`, nem no bloqueio do `AskUserQuestion`. Essas são regras
  sem exceção no próprio CLAUDE.md, e a camada de GitHub/git nativo
  nem enxerga essa frase -- não tem como uma autorização de conversa
  chegar até o servidor.

## 5. Como os revisores semânticos decidem (processo de verificação)

Pedir pra um revisor "leia e diga se está tudo bem" numa passada só é
um padrão conhecido por falhar -- o modelo pode confirmar o próprio
palpite em vez de checar de verdade. Os revisores de commit, do fim
da resposta, e o auditor geral seguem um processo de quatro etapas
antes de fechar qualquer julgamento:

1. **Rascunho** -- lê a evidência (diff, arquivos do módulo) e forma
   um veredito inicial.
2. **Pergunta** -- pra cada veredito "parece certo", escreve uma
   pergunta específica que desmontaria esse veredito se estivesse
   errado.
3. **Verificação** -- responde essa pergunta olhando a evidência de
   novo, não a partir de impressão.
4. **Contra-argumento** -- antes de fechar, argumenta ativamente pelo
   lado oposto: que evidência apontaria pra isso estar errado?

Fatos puros (existe o arquivo? tem a palavra proibida?) não passam
por esse processo -- são conferidos direto. O processo é só pra onde
existe risco real de o revisor se convencer rápido demais.

Além disso, o auditor geral (segundo revisor, independente, no fim da
resposta) relê o `CLAUDE.md` do zero e confere tudo de novo, sem
reaproveitar a conclusão do primeiro revisor -- uma segunda opinião
independente pega o que uma checagem sozinha deixaria passar.

## 6. Uso no dia a dia

**Painel ao vivo.** Uma barra fixa embaixo da tela mostra, sempre:
em qual worktree você está (ou um aviso se não estiver em nenhuma),
o PR aberto e seu estado, quantas autorizações foram usadas na
sessão, e qual foi o último arquivo tocado.

**Checagem sob demanda.** A qualquer momento, peça "Use o
fiscal-claude-md pra conferir" pra um retrato de progresso, sem
esperar o commit ou o fim da resposta.

**Ajustar um critério.** Os textos dos revisores automáticos, dentro
de `.claude/settings.json`, são só instruções em português comum --
pode pedir pra mudar qualquer critério específico a qualquer momento.

## 7. Instalação

**Antes do passo 1** -- confira `git config --get core.hooksPath`. Se
já existir um valor (por exemplo `scripts/hooks`, com um `pre-commit`
próprio já funcionando ali, caso real do projeto NEXO), **nunca** rode
um comando que troque esse valor -- isso desliga, em silêncio, qualquer
gancho que já esteja ativo. Em vez de uma pasta `.githooks/` separada,
este pacote guarda os hooks nativos do git em `scripts-hooks/`
(nome de propósito, pra não confundir com um `core.hooksPath` novo) --
o destino real é sempre a pasta que `core.hooksPath` já aponta.

1. Copie `.claude/`, `.github/`, `.vale.ini` e `.vale/` pra raiz do
   projeto, mesclando com o que já existir (não sobrescreva sem
   checar). Copie o conteúdo de `scripts-hooks/` pra dentro da pasta
   que `core.hooksPath` já aponta (ver nota acima), ao lado do que já
   existir lá. Só copie `configurar-protecao-branch.sh` se for usar o
   passo 4.
2. Deixe tudo executável:
   ```
   chmod +x .claude/hooks/*.sh <pasta-do-core.hooksPath>/* configurar-protecao-branch.sh
   ```
3. Só se `core.hooksPath` **não** apontar pra lugar nenhum ainda
   (repositório sem nenhum gancho git prévio):
   ```
   git config core.hooksPath scripts/hooks
   ```
   Nunca rode este comando se o passo "Antes do passo 1" já encontrou
   um valor existente -- copie pra dentro dele em vez de substituí-lo.
4. (Opcional, exige `gh` autenticado com permissão de admin) Configure
   a proteção de branch no GitHub, uma vez só:
   ```
   ./configurar-protecao-branch.sh
   ```
5. (Opcional, exige instalar o binário do Vale -- https://vale.sh)
   Rode `vale modulos/` pra conferir tom nos documentos já existentes.
6. Confira que `jq` está instalado (`jq --version`). Todo hook em
   `.claude/hooks/*.sh` depende dele pra ler o JSON de entrada -- sem
   `jq`, os hooks falham em silêncio (não travam nada, mas também não
   checam nada de verdade). Ver seção 9.1.
7. Digite `/hooks` dentro do Claude Code e confira que os eventos
   aparecem: `UserPromptSubmit`, `SessionStart`, `PreToolUse`,
   `PostToolUse`, `WorktreeRemove`, `Stop`.
8. Digite `/agents` e confira que `fiscal-claude-md` aparece.

## 8. Estrutura de arquivos

```
.claude/
├── settings.json                    -- liga hooks, statusLine, permissões
├── hooks/
│   ├── lib/common.sh                -- funções compartilhadas (inclui require_jq, normalize_path)
│   ├── user_prompt_submit.sh        -- detecta AUTORIZO-TRAVA
│   ├── pre_commit_hygiene.sh        -- checagens antes do commit
│   ├── pre_git_rules.sh             -- develop/main, --no-ff, base do PR, worktree
│   ├── pre_edit_safety.sh           -- reler antes de editar, só escrever em worktree
│   ├── pre_bash_search_guard.sh     -- ls/grep/find/sed via Bash como busca ampla
│   ├── post_edit_track.sh           -- ordem real de edição
│   ├── post_read_track.sh           -- registro de leituras
│   ├── post_preview_track.sh        -- tentativas de teste no preview
│   ├── post_merge_reminder.sh       -- lembrete de git pull após merge/push
│   ├── stop_fact_check.sh           -- fatos do fim da resposta, sem IA (worktree, status, PR)
│   ├── worktree_remove_cleanup.sh   -- gradlew --stop (evento nativo)
│   └── statusline.sh                -- painel ao vivo
└── agents/fiscal-claude-md.md       -- checagem sob demanda

scripts-hooks/        -- hooks nativos do git (commit-msg, pre-rebase,
                         pre-push); destino real é a pasta que
                         core.hooksPath já aponta, nunca uma pasta
                         .githooks/ separada -- ver seção 7
.github/pull_request_template.md
.vale.ini + .vale/styles/Projeto/    -- estilo de prosa (opcional)
configurar-protecao-branch.sh        -- roda uma vez, configura o GitHub
```

## 9. Achados corrigidos numa revisão completa

Uma passada regra por regra do `CLAUDE.md`, comparando cada uma contra
o mecanismo que devia cobri-la, e testando ao vivo (não só lendo o
código), encontrou onze problemas reais nesta pasta -- os onze já
corrigidos e testados de ponta a ponta:

### 9.1 Dependência de `jq` falhava em silêncio

Todo hook lê o JSON de entrada com `jq` (função `field()` em
`lib/common.sh`). Sem `jq` instalado -- confirmado ao vivo, ausente
numa máquina real rodando este projeto -- `field()` volta string
vazia sem erro nenhum, porque redireciona a saída de erro do `jq` pra
não poluir o resultado com "chave ausente". Resultado prático: um
hook inteiro "roda", não trava nada, e não avisa que não checou nada
de verdade. `lib/common.sh` agora tem `require_jq()`, chamada no
início de `read_input()` -- sem `jq`, todo hook trava com mensagem
clara, em vez de passar em silêncio.

### 9.2 `grep -P` de emoji falhava em locale não-UTF-8

A checagem de emoji em `pre_commit_hygiene.sh` usa `grep -P` com
intervalo `\x{...}` acima de `0x7F` -- exige locale UTF-8. Em locale
`C`/`POSIX` (comum em ambiente Windows/Git Bash sem variável de
locale definida, confirmado ao vivo), falha com "supports only unibyte
and UTF-8 locales" e a checagem inteira nunca roda. Corrigido forçando
`LC_ALL=C.UTF-8` só nessa chamada -- testado ao vivo, resolve sem
exigir nenhum locale extra instalado no sistema.

### 9.3 Separador de caminho do Windows quebrava toda comparação

Todas as checagens que comparam caminho (dentro de worktree? já lido
antes?) usavam substring com `/` (padrão Unix). No Windows, `cwd` e
`file_path` vêm com `\` -- uma checagem como "`$CWD` contém
`/.claude/worktrees/`" nunca bate contra um caminho real do Windows,
mesmo estando de verdade dentro da worktree, bloqueando toda edição
por engano. `lib/common.sh` agora tem `normalize_path()` (troca `\`
por `/`), usada em `pre_edit_safety.sh`, `pre_git_rules.sh`,
`post_read_track.sh` e `post_edit_track.sh` antes de qualquer
comparação ou gravação em log.

### 9.4 Instalação sobrescrevia um `core.hooksPath` já existente

A instrução original de instalação (`git config core.hooksPath
.githooks`) apaga, em silêncio, qualquer valor já configurado --
inclusive um `pre-commit` real, já funcionando, que o projeto de
destino já tivesse. Corrigido: os hooks nativos do git agora moram em
`scripts-hooks/` (nome de propósito, não `.githooks/`), e a seção 7
instrui checar `core.hooksPath` antes e copiar pra dentro da pasta que
ele já aponta, nunca substituí-la.

### 9.5 Bash usado como busca ampla não era coberto por nenhum hook

A proibição de "Grep/Glob/Agent por iniciativa própria" só estava
mecanizada pras ferramentas nomeadas (`permissions.ask`). Rodar o
mesmo tipo de busca através do Bash (`ls`/`grep`/`find`/`sed`) escapava
de qualquer checagem -- achado concreto, não hipotético: aconteceu de
verdade numa sessão real, `ls`/`find` usados via Bash pra explorar uma
pasta, logo depois do `CLAUDE.md` ter sido relido. `pre_bash_search_guard.sh`
fecha essa lacuna: `grep`/`find`/`sed -n` sempre escalam pra
confirmação; `ls` de uma pasta específica (recorte já usado no
projeto) continua liberado, só `ls` recursivo/com curinga/de vários
caminhos escala.

### 9.6 Custo do hook de `Stop` (chamada de agente em toda resposta)

O evento `Stop` dispara em toda resposta, não só quando uma tarefa de
verdade termina -- e rodava dois hooks tipo `agent` (até 150s e 180s
de tempo limite cada) mais um `prompt` (30s), todo turno. Pesquisa na
documentação oficial confirmou que o campo `if` (que poderia pular um
hook inteiro por condição) só é avaliado em eventos de ferramenta
(`PreToolUse`/`PostToolUse`/`PostToolUseFailure`/`PermissionRequest`/
`PermissionDenied`) -- nunca em `Stop`. Não existe hoje um jeito de
pular a chamada de agente com base em "esta resposta foi trivial".
O que dava pra corrigir: três fatos que **não precisavam de IA
nenhuma** (worktree mesclada e esquecida, `git status` sujo quando a
resposta diz "pronto", base do PR) estavam dentro do hook `agent` caro,
contrariando o próprio princípio deste projeto (seção 1: fato mecânico
vira script, nunca pergunta pro modelo). `stop_fact_check.sh`, um hook
`command` novo, sem custo de modelo nenhum, assume esses três fatos; o
hook `agent` que continua rodando junto foi enxugado pra só as
perguntas de julgamento de verdade (significância pro `HANDOFF.md`,
produção, clareza do PR) -- mais rápido de responder, com menos coisa
pra reler. O segundo hook `agent` (auditor independente) e o `prompt`
de tom continuam como estavam, de propósito -- são a redundância
intencional descrita na seção 5, não custo redundante.

### 9.7 `Task(*)` não existe como ferramenta -- a proteção mais importante nunca funcionou

Pesquisa direta na documentação oficial de permissões (não só de
hooks) confirmou: a ferramenta de subagente neste Claude Code se
chama `Agent`, não `Task` (`Task` nem existe -- há `TaskOutput`/
`TaskStop`, de gerenciamento de tarefa em segundo plano, sem relação).
A doc confirma: "uma regra de permissão cujo nome de ferramenta não
bate com nenhuma ferramenta conhecida gera um aviso, sem efeito
nenhum". Ou seja, `"Task(*)"` em `permissions.ask` nunca gatilhou
confirmação nenhuma pra chamadas de subagente -- a proteção mais
citada desde o início desta revisão. Corrigido pra `"Agent"` (nome
sozinho, sem parênteses -- forma que a doc confirma como "casa toda
chamada da ferramenta", sem depender de `(*)` funcionar igual pra
ferramentas fora do Bash). `"Grep(*)"`/`"Glob(*)"` também trocados
pela forma confirmada, `"Grep"`/`"Glob"`.

### 9.8 Leitura obrigatória só era conferida no commit, tarde demais

O Portão pro Passo 2 do "Fluxo completo de uma tarefa" exige a leitura
obrigatória feita **antes** de qualquer código escrito -- só
`pre_commit_hygiene.sh` conferia isso, no commit, permitindo editar
dezenas de arquivos sem nunca ter lido nada. Movido pra
`pre_edit_safety.sh` (bloqueia a primeira escrita), com
`pre_commit_hygiene.sh` mantido como segunda conferência. A lista
usada nessa checagem também foi corrigida: só os 6 documentos de
"leitura manual obrigatória" entram -- os outros 16, importados
automaticamente via `@caminho`, já chegam garantidos pelo próprio
Claude Code assim que a sessão começa, e checá-los contra o log de
`Read` bloquearia toda sessão bem-comportada que nunca precisou
reler algo que já veio sozinho.

### 9.9 Leitura parcial (`limit`/`offset`) contava como leitura completa

`CLAUDE.md`: "é lido na íntegra... sem corte, sem exceção" -- um
`Read` com `limit`/`offset` é leitura parcial, de propósito, e não
contava como diferente de uma leitura completa no `read-log.txt`.
Corrigido: leitura parcial vai pra um log separado
(`partial-read-log.txt`), nunca no principal -- as checagens de
leitura obrigatória e de "reler antes de editar" só aceitam leitura
completa.

### 9.10 Emoji nunca checado na mensagem do commit nem na resposta de chat

`CLAUDE.md`: "nunca usar emojis em nada escrito neste projeto (código,
docs, commits, **chat**)". As duas checagens de emoji que existiam só
olhavam o conteúdo de arquivo alterado -- nenhuma olhava o texto da
própria mensagem do commit, nem a resposta de chat. Corrigido:
`pre_commit_hygiene.sh` agora também confere o comando `git commit`
em si (onde o texto da mensagem aparece); o hook de tom do `Stop`
(`type: prompt`, sem acesso a ferramentas, mas capaz de notar emoji
só de olhar o texto) agora também confere `last_assistant_message`.

### 9.11 Ordem completa do fluxo só checava metade

`CLAUDE.md` exige a ordem `concept.md` → `architecture.md` →
`schemas/` → implementação -- só "`architecture.md` antes do código"
existia. `pre_commit_hygiene.sh` agora também confere "`concept.md`
antes de `architecture.md`" e "`schemas/` antes do código".

## 10. Os dois limites reais

Depois de cobrir o documento inteiro, regra por regra, sobraram dois
pontos sem contorno técnico possível -- cada um por um motivo
específico, não por serem difíceis:

**Provar entendimento genuíno.** Dá pra forçar a ação -- reler o
arquivo antes de editar, é o que a seção 3 já cobre. O processo de
verificação da seção 5 torna o julgamento mais confiável, não
infalível: ele mesmo depende de o revisor entender o que está
olhando. Isso é um limite de natureza epistêmica, não algo que uma
ferramenta resolve.

**Confirmar que o teste no preview funcionou de verdade.** O limite
aqui não é do Claude Code, é do próprio `CLAUDE.md`: a seção "Como
rodar o preview isolado" não define nenhum sinal observável de
sucesso (nem um código de saída, nem um arquivo de log, nem uma
suíte de teste). Sem essa definição, não existe evidência pra
nenhuma ferramenta capturar. O que existe hoje -- `post_preview_track.sh`
registrando quando o ambiente sobe e desce, e o revisor do fim da
resposta perguntando explicitamente "isso foi testado de novo?" em
vez de presumir -- é o máximo possível até essa seção ser definida.
No dia em que ela definir um sinal concreto, essa checagem inteira
vira mecânica.
