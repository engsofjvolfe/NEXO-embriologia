# 0020 — Ligação entre leitura de peça, lógica de sessão e tela

Resumo em linguagem simples: hoje, quando uma peça é lida — seja
encostando ela direto no celular, seja pelo acessório externo por
Bluetooth —, o aplicativo só sabe dizer "uma peça foi lida" — ninguém
está escutando esse aviso pra checar se é a peça certa, nem pra
atualizar a tela. Esta ADR decide o "fio" que liga esses três pontos,
nos dois casos de leitura por igual: a leitura (já existe), a lógica
que decide certo/errado (já existe, pronta, no pacote `session`) e a
tela (ainda não existe). A tela continua fora do escopo desta ADR —
só o mecanismo que vai alimentá-la é decidido aqui.

Convenção dos códigos citados abaixo:
- `DA-LEI` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.1.
- `EI-VAL` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.4.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Todo trecho abaixo marcado
descreve documentação oficial de terceiro (Google/Android) — não uma
decisão deste projeto, sujeita a mudar em qualquer revisão futura
dessa documentação. Quem ler este documento depois deve tratar esse
conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra mudar código.

**Status:** aceito

**Contexto:**

O pacote `connectivity` já entrega, dos dois jeitos de leitura
(NFC direto em `MainActivity`, ou Bluetooth via `BleAccessoryService`),
um aviso simples — "esta peça foi lida" — por meio de
`PieceReadListener` (ver
[decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md)).
O próprio `architecture.md` já registra que "nem `MainActivity` nem
`BleAccessoryService` decidem o que fazer com uma leitura além de
entregá-la" — falta o pedaço que recebe esse aviso, pergunta pra
`session` (DA-LEI-06, EI-VAL-02) se a peça bate com a posição
esperada, e entrega o resultado pra uma tela mostrar.

`[REVISAR-EXTERNO]` O guia oficial de arquitetura do Android recomenda
um padrão específico pra esse tipo de situação: um objeto chamado
`ViewModel` guarda o estado de uma tela e sobrevive a mudanças de
configuração (como girar o aparelho) — em tradução livre, "a classe
`ViewModel` é quem guarda a lógica de negócio ou o estado de uma tela;
ela expõe esse estado pra interface e reúne a lógica de negócio
relacionada" (GOOGLE, [s.d.]a). Esse mesmo guia também fixa um limite
importante — em tradução livre, "um `ViewModel` não deve guardar
nenhuma referência a API presa ao ciclo de vida, como `Context` ou
`Resources`, pra evitar vazamento de memória" (GOOGLE, [s.d.]a) — um
`ViewModel` pode viver mais tempo que a tela que o criou, então nunca
pode depender de nada que morra com ela.

Esse limite pesa direto na decisão: hoje, a ligação com o `Service` de
Bluetooth já é gerenciada pela própria tela (`MainActivity`) — conecta
quando a tela aparece, desconecta quando some, mesmo padrão que a
documentação oficial descreve pra esse tipo de serviço "local"
(rodando no mesmo processo do aplicativo) — em tradução livre, "quem
usa o serviço recebe o `Binder` e pode usá-lo pra acessar diretamente
os métodos públicos (...) recebendo o `Binder` pelo método
`onServiceConnected()`" (GOOGLE, [s.d.]b) — e recomenda ligar/desligar
acompanhando o tempo de vida da tela — em tradução livre, "se for
preciso interagir com o serviço só enquanto a tela estiver visível,
conectar durante `onStart()` e desconectar durante `onStop()`"
(GOOGLE, [s.d.]b). Como essa ligação é presa ao tempo de vida da tela,
ela não pode morar dentro do `ViewModel` — só pode continuar do jeito
que já está hoje, dentro da própria `MainActivity`.

Duas formas foram consideradas pra alimentar o `ViewModel`, então, sem
violar esse limite:

1. A tela repassa o aviso de peça lida pro `ViewModel` através de uma
   função comum, direta — a tela chama, por exemplo,
   `viewModel.onPieceRead(tagId)`, entregando só o texto do
   identificador, nunca a ligação com o `Service` nem a tela em si.
2. Um mecanismo mais elaborado, específico do Kotlin, que converte um
   aviso desse tipo (um "callback", jeito mais antigo de avisar
   evento) num fluxo contínuo de dados (`Flow`) que o `ViewModel`
   "assina" (`callbackFlow`, biblioteca de corrotinas do Kotlin,
   JETBRAINS, [s.d.]).

A opção 2 resolveria um problema que este caso não tem: ela existe pra
quando o próprio código que gera o aviso já mora dentro de algo que o
`ViewModel` pode "assinar" com segurança. Aqui, quem gera o aviso é
exatamente a ligação presa à tela (item acima) — então a conversão
não muda o limite: alguém ainda precisa estar dentro da tela pra
alimentar esse fluxo. A opção 1 é mais direta, resolve o mesmo
problema sem mecanismo extra, e é o padrão que os próprios exemplos
oficiais de `ViewModel` usam pra entrada de evento vindo de fora dele.

**Decisão:**

1. **A tela (`MainActivity`, ou a `Activity`/`Composable` que vier a
   substituí-la quando o desenho visual acontecer) continua
   gerenciando a ligação com o `BleAccessoryService` e o modo leitor
   de NFC exatamente como já está hoje** — nenhuma mudança nessa
   parte, já escrita (decisions/0015, decisions/0017, decisions/0018).

2. **Um novo `ViewModel` (`app/ui/SessionViewModel.kt`, dentro do
   pacote `ui/` já previsto em `architecture.md`) guarda o estado da
   sessão em curso, do jeito que a tela precisa mostrar**, exposto
   como `StateFlow` — os campos exatos desse estado ficam pra quando o
   desenho visual das telas acontecer (pendência já registrada em
   `tasks.md`); esta ADR fixa só o mecanismo, não o conteúdo.

3. **A tela repassa cada aviso (peça lida, mudança de estado de
   conexão) pro `ViewModel` por meio de uma função comum**
   (`viewModel.onPieceRead(tagId)`, `viewModel.onConnectionStateChanged(state)`)
   — implementando `PieceReadListener` e `ConnectionStateListener`
   (já existentes) só pra encaminhar o aviso, sem guardar nem decidir
   nada ali. Nenhum mecanismo de conversão de callback (`callbackFlow`
   ou equivalente) é usado nesta ligação.

4. **O `ViewModel` é quem chama `session` e `content`** — recebe o
   identificador da peça, busca em `content` qual posição ele
   corresponde (identificador esperado da posição atual), chama
   `session.recordAttempt` com esse dado, e atualiza o próprio estado
   a partir do novo `SessionState` devolvido. `session` continua sem
   conhecer `content` nem o `ViewModel` (RNF-MOD-01,
   [decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)) — é o
   `ViewModel`, na camada de `app`, quem já tem os dois em mãos e faz
   a ponte, mesmo padrão de responsabilidade já registrado em
   `architecture.md` pro pacote `session` ("quem chama essas funções é
   responsável por já ter em mãos o dado que vem de lá").

**Consequências:**

O núcleo do motor (`session`, `content`, `connectivity`) continua sem
nenhuma dependência de classe do Android — a ponte entre eles é
sempre responsabilidade de `app`, nunca deles entre si. O `ViewModel`
nunca guarda referência a `Context`, `Service` ou à própria tela —
só dados simples (texto do identificador, o próprio `SessionState`) —
consistente com o limite oficial confirmado nesta pesquisa. Quando o
desenho visual das telas acontecer (pendência já registrada em
`tasks.md`), o trabalho ali é só decidir o conteúdo exato do estado
exposto e como desenhá-lo — o mecanismo que leva a leitura até a tela
já está pronto, não muda.

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **ViewModel overview**. Android Developers, [s.d.]a. Disponível
em: https://developer.android.com/topic/libraries/architecture/viewmodel.
Acesso em: 15 ago. 2026.

GOOGLE. **Bound services overview**. Android Developers, [s.d.]b.
Disponível em: https://developer.android.com/develop/background-work/services/bound-services.
Acesso em: 15 ago. 2026.

JETBRAINS. **callbackFlow**. Kotlin — kotlinx.coroutines API reference,
[s.d.]. Disponível em:
https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html.
Acesso em: 15 ago. 2026.
