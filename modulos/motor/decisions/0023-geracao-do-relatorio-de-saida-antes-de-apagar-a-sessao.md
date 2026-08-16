# 0023 — Geração do relatório de saída antes de apagar a sessão pausada

Resumo em linguagem simples: quando alguém confirma que quer sair de
uma sessão no meio do jogo, o sistema apaga o progresso salvo — mas
antes disso precisa existir um relatório do que aconteceu até aquele
ponto. Hoje isso não acontecia: a função que confirma a saída já
apagava o progresso sem nunca montar esse relatório. Esta ADR muda a
forma dessa função pra que seja impossível apagar sem passar primeiro
pela geração do relatório, mesmo a tela de resultado (que ainda não
existe) não estando pronta pra escrever o arquivo de verdade no
aparelho.

Convenção dos códigos citados abaixo:
- `EI-PAU` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.12.
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.

**Status:** aceito

**Contexto:**

`SessionViewModel.onExitConfirmed`, já escrito (decisions/0020,
decisions/0022), chama `deleteSessionState` (core/session,
decisions/0010) direto, sem nunca montar o relatório da sessão antes.
Isso viola EI-PAU-04 ("mesmo quando a sessão é encerrada por sair, o
motor gera o relatório... antes de apagar o estado retomável") — não é
uma lacuna teórica, é um comportamento incorreto já presente no código
hoje: chamando essa função agora, o progresso some e nenhum relatório
é criado.

O bloqueio até aqui: montar o conteúdo do relatório (texto do CSV,
linhas do PDF) não depende de nada do Android — já está pronto em
`core/report` (decisions/0019) —, mas escrever esse conteúdo de
verdade num arquivo do aparelho depende de um `Context`, que o
`ViewModel` nunca pode guardar (decisions/0020, seguindo a
recomendação oficial já citada ali). Quem teria o `Context` disponível
é a tela de resultado (`DA-RET-14`), que ainda não foi construída —
pendência grande e sem responsável, registrada em `tasks.md`. A nota
que registrou esta pendência assumiu que a correção só seria possível
quando essa tela existisse.

Duas formas de fechar essa lacuna sem esperar a tela foram
consideradas:

1. Mudar `onExitConfirmed` pra exigir, como parâmetro da própria
   chamada, uma função que recebe o conteúdo já montado do relatório
   (texto do CSV, linhas do PDF) e faz a escrita de verdade —
   `deleteSessionState` só é chamado depois dessa função ser
   executada, dentro do próprio corpo de `onExitConfirmed`. Por
   construção, não existe caminho de código que apague sem ter
   passado pela geração antes.
2. Acrescentar só uma função nova ao `ViewModel` que devolve o
   conteúdo do relatório já montado (`buildReportCsv`/
   `buildReportPdfLines`, aplicados aos dados que o próprio
   `ViewModel` já guarda), deixando `onExitConfirmed` do jeito que
   está — quem for escrever a tela de resultado, no futuro, teria que
   lembrar de chamar a função nova antes de confirmar a saída.

A opção 2 não corrige o comportamento incorreto que já existe hoje —
só cria uma peça nova, sem garantir que ela seja usada na ordem certa;
a garantia continuaria dependendo de alguém lembrar, no futuro, de
chamar as duas coisas na ordem certa. A opção 1 resolve o problema
agora, pela própria forma da função: quem chama `onExitConfirmed` é
obrigado a fornecer a função de escrita pra poder chamá-la, então a
ordem certa (gerar, depois apagar) é garantida independente de quem
escrever a tela e de quando isso acontecer. Não existe fonte externa
por trás dessa escolha — é uma decisão sobre a forma do código deste
projeto, mesma categoria de decisions/0008, decisions/0009 e
decisions/0022, nenhuma das quais cita fonte externa.

**Decisão:**

1. **`SessionViewModel.onExitConfirmed` passa a receber um parâmetro
   `writeReport: (csv: String, pdfLines: List<String>) -> Unit`.** O
   corpo da função chama `writeReport` com o conteúdo já montado —
   `buildReportCsv(configuration, sessionState.log)` e
   `buildReportPdfLines(configuration, sessionState.log)`, ambas de
   `core/report` (decisions/0019) — antes de chamar
   `deleteSessionState`.

2. **O `ViewModel` continua sem guardar `Context`** — quem fornece
   `writeReport`, na prática, é a tela que chama `onExitConfirmed`
   (ainda não escrita); essa função é só a ponte, `core/report`
   continua sem depender de Android, e a escrita de verdade
   (`app/report`, decisions/0019) continua vivendo em `app`, chamada
   de dentro do `writeReport` fornecido pela tela.

3. **Nenhuma tela precisa existir pra este código ser correto** — o
   parâmetro `writeReport` pode, hoje, ser um substituto de teste (uma
   função que só grava o que recebeu, sem tocar em arquivo real),
   suficiente pra confirmar que a ordem (gerar antes de apagar) é
   respeitada sem precisar de aparelho nem emulador.

**Consequências:**

A pendência "Gerar o relatório de saída antes de apagar a sessão
pausada (EI-PAU-04)" fica resolvida de fato, não apenas parcialmente —
o comportamento incorreto (apagar sem gerar relatório) deixa de ser
possível de acontecer, mesmo sem a tela de resultado (`DA-RET-14`)
existir ainda. Quando essa tela for desenhada e escrita (pendência que
continua em aberto, sem responsável, ver `tasks.md`), o trabalho ali
se resume a fornecer a função `writeReport` real — chamando
`app/report.writeReportCsv`/`writeReportPdf` (decisions/0019) com o
`Context` que só a tela tem. Nenhuma mudança de assinatura de
`onExitConfirmed` deve ser necessária quando isso acontecer.

`onExitRequested` e `onExitCancelled` não mudam — a exigência de
EI-PAU-04 é só sobre o caminho que de fato apaga o estado
(`onExitConfirmed`), não sobre pedir ou cancelar a saída.

Este código ainda não tem teste automatizado — mesma pendência já
registrada em `tasks.md` ("Decidir ferramenta de teste pro módulo
`app`"), porque `SessionViewModel` estende `androidx.lifecycle.ViewModel`
e o módulo `app` ainda não tem ferramenta de teste fixada. A função
`writeReport` em si, por não depender de nenhuma classe do Android,
não é o que bloqueia esse teste — é o restante do `ViewModel` que
continua preso à mesma pendência já registrada.
