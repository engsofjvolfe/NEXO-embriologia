# 0015 — Fronteira entre `core` e `app` no pacote `connectivity`

Resumo em linguagem simples: o código que liga de verdade o Bluetooth
(fala com o acessório externo) e o que recebe a leitura de NFC (fala
com a antena do próprio aparelho) vai morar no módulo `app`, não no
`core` — mesmo o pacote `connectivity` sendo, por nome, parte do
"núcleo" do motor. O `core` guarda só a descrição de "o que é uma
peça lida" e qualquer lógica pura que exista em cima disso (ex.:
decodificar o dado bruto recebido); o `app` guarda a parte que precisa
falar de verdade com o rádio do aparelho. Essa divisão segue à risca a
própria documentação oficial do Android — não é invenção deste
projeto — checada linha a linha antes desta decisão, não só suposta
por analogia com decisões anteriores.

Convenção dos códigos citados abaixo:
- `DA-LEI` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.1.
- `PD-LEI` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.1.
- `PD-CON` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.2.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Todo trecho abaixo marcado
descreve documentação oficial de terceiro (Google/Android) — não uma
decisão deste projeto, sujeita a mudar em qualquer revisão futura
dessa documentação. Quem ler este documento depois deve tratar esse
conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra qualquer
decisão nova.

**Status:** aceito

**Contexto:**

[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md) já havia
decidido que o módulo `core` "não depende de nenhuma classe de
interface do Android (sem `Activity`, sem `Context` de tela, sem
`Composable`)", justamente pra continuar "testável com teste de
unidade comum da JVM, sem emulador" — e listou "o papel de cliente BLE
ao usar o acessório externo" como parte do que mora dentro de `core`,
sem descer ao nível de qual classe concreta faz essa conexão.
[architecture.md](<../docs/architecture.md#núcleo-do-motor>) repete
essa atribuição em texto (leitura de peça — DA-LEI-03 a DA-LEI-06 —,
"papel de cliente GATT... conectando ao Nordic UART Service", PD-CON-01
a PD-CON-04), mas, diferente dos pacotes `search`, `hierarchy`,
`session` e `content`, nunca chegou a ter uma seção própria de "desenho
interno" — a pendência em `tasks.md` ("Escrever o código-fonte do
pacote `connectivity`") aponta só pra essa descrição geral.

Bluetooth de baixo consumo (BLE) e NFC são, os dois, tecnologias que só
existem de verdade dentro do sistema Android rodando — não é possível
simular nenhuma delas num teste de unidade comum da JVM, do jeito que
`search`, `hierarchy`, `session` e `content` já são testados
([decisions/0005](0005-abordagem-de-teste-do-nucleo-do-motor.md)).
Guardar o código real de conexão dentro de `core`, como o texto atual
de `architecture.md` sugere sem detalhar, entraria em conflito direto
com o motivo que [decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)
já registrou pra manter `core` livre de classe de interface do Android.

Duas alternativas reais:

(a) Código real de Bluetooth (`BluetoothGatt`, `BluetoothAdapter`) e de
NFC (`NfcAdapter`, leitura de `Tag`) dentro de `core/connectivity`,
seguindo a árvore de pastas já esboçada em `architecture.md` e em
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md). Mais
simples — um pacote só, sem dividir a responsabilidade entre módulos —,
mas quebra a garantia de teste sem aparelho que o resto de `core` já
tem, e precisa de uma exceção não escrita em nenhum lugar à regra "sem
classe de interface do Android" já fixada.

(b) `core/connectivity` guarda só o contrato — o formato de "uma peça
foi lida" e qualquer lógica de decodificação pura que exista em cima
disso — e o código que liga de verdade o Bluetooth e recebe a leitura
NFC mora em `app`, chamando o `core` só depois de já ter o identificador
da peça em mãos. Mais alinhado com o motivo já registrado em
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md), mas exige
que `app` (ainda um esqueleto mínimo, ver
[decisions/0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md))
ganhe uma peça de código nova, cedo, antes do desenho visual das telas
estar pronto.

`[REVISAR-EXTERNO]` Antes de decidir entre as duas, a documentação
oficial do Android foi lida por completo (não só resumo de busca) em
quatro páginas — as duas primeiras sobre arquitetura geral, as duas
seguintes sobre as tecnologias em si:

- O guia geral de arquitetura de aplicativo Android registra, como
  prática recomendada: "Reduce dependencies on Android classes. Make
  your app components the only classes that rely on Android framework
  SDK APIs such as `Context` or `Toast`" — tradução livre: só as
  quatro peças que o próprio Android reconhece oficialmente como
  "componente de app" (`Activity`, `Service`, `BroadcastReceiver`,
  `ContentProvider`) deveriam depender direto de `Context` ou de
  qualquer API que exija o sistema Android rodando; todo o resto do
  código deveria ficar de fora disso, "pra melhorar testabilidade e
  reduzir acoplamento" (GOOGLE, [s.d.]a).
- O guia oficial de como conectar a um servidor GATT (o papel de
  cliente Bluetooth que o acessório externo espera do aplicativo,
  PD-CON-03) mostra, como implementação de referência do próprio
  Google, o código de `BluetoothGatt`/`BluetoothAdapter` inteiro dentro
  de um `Service` vinculado (`BluetoothLeService`), nunca numa classe
  solta — a `Activity` só conversa com esse `Service`, nunca liga o
  Bluetooth diretamente (GOOGLE, [s.d.]c).
- O Projeto Arquitetônico já aprovado, em DA-LEI-04, já tinha decidido
  que o caminho de leitura direta usa "o modo de leitura em primeiro
  plano do próprio sistema Android" — checando a referência oficial
  atual da classe `NfcAdapter`, esse modo é o `enableReaderMode(Activity
  activity, NfcAdapter.ReaderCallback callback, int flags, Bundle
  extras)`, sem nenhuma marca de descontinuado, com `activity` como
  parâmetro obrigatório (GOOGLE, [s.d.]d; GOOGLE, [s.d.]e). Diferente
  do sistema mais antigo de despacho de intent
  (`ACTION_NDEF_DISCOVERED` e afins, entregues em `onNewIntent`), o
  modo leitor restringe toda a leitura de NFC só ao que a `Activity`
  em primeiro plano está interessada em ler — mas continua, do mesmo
  jeito, só existindo associado a uma `Activity`.

Ou seja: a fonte oficial não deixa em aberto onde o código de hardware
deveria morar — ela mesma já resolve isso, atribuindo essa
responsabilidade só às peças reconhecidas como "componente de app"
(`Service` pro Bluetooth, `Activity` pro NFC), nunca a uma classe
comum. Esse achado bate, sem contradição, com o motivo que
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md) já tinha
registrado pra manter `core` sem classe de interface do Android.

**Decisão:** alternativa (b).

`core/connectivity` guarda só o que é possível descrever e testar sem
tocar o sistema Android rodando: um tipo representando a leitura de uma
peça (o identificador físico já lido, sem importar se veio da antena
própria ou do acessório — DA-LEI-06, "a validação trata os dois do
mesmo jeito") e, se houver, a lógica pura de decodificar o dado bruto
recebido do acessório nesse identificador. Nenhum tipo de
`android.bluetooth` nem de `android.nfc` é referenciado dentro de
`core`.

O código que de fato liga o Bluetooth — gerencia `BluetoothGatt`,
conecta ao Nordic UART Service que o acessório anuncia, entrega a
notificação recebida — mora em `app`, dentro de um `Service` vinculado,
no mesmo desenho de referência que a própria documentação oficial usa
(GOOGLE, [s.d.]c). O código que recebe a leitura NFC direta mora em
`app`, dentro da `Activity` de entrada (a mesma `MainActivity` já
escrita em
[decisions/0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md)
— nada aqui espera pelo desenho visual das telas, ainda pendente:
DA-LEI-04 já pede leitura ativa "continuamente enquanto está em
primeiro plano", não só durante uma tela específica de jogo), usando o
modo leitor de NFC já decidido em DA-LEI-04 (GOOGLE, [s.d.]d; GOOGLE,
[s.d.]e). Os dois só repassam pro `core` o
identificador já lido — o `core` nunca sabe, ele mesmo, que existe
Bluetooth ou NFC por trás disso, mesma separação de responsabilidade já
usada entre `SessionState.kt` (lógica pura) e
`SessionStatePersistence.kt` (E/S em arquivo,
[architecture.md](<../docs/architecture.md#pacote-session--desenho-interno>)),
e entre `ContentImport.kt` (validação pura) e
`ContentPackageArchive.kt` (leitura do ZIP,
[decisions/0013](0013-desenho-do-pacote-content.md)).

O papel de "cliente GATT" que PD-CON-03 já atribui ao aplicativo
continua correto e não é reescrito por esta decisão — é um papel do
protocolo Bluetooth (quem pede dado, quem responde), não uma afirmação
de qual classe Kotlin especificamente implementa esse papel; esta ADR
só resolve a segunda pergunta, que a cascata de documentação nunca
desceu a esse nível de detalhe.

**Consequências:**

O desenho interno completo do pacote `connectivity` — os tipos exatos
de `core/connectivity`, e os nomes das classes de `app` que hospedam a
conexão Bluetooth e a leitura NFC — fica pra uma seção própria de
`architecture.md` ("desenho interno" do pacote, mesmo formato já usado
para `search`, `hierarchy`, `session` e `content`), escrita como
próximo passo depois desta ADR, nunca antes dela.

`app`, hoje um esqueleto mínimo sem nenhuma lógica
([decisions/0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md)),
ganha código de verdade antes do desenho visual das telas estar
pronto — aceito porque a fronteira entre núcleo e interface continua
intacta: o `Service` de Bluetooth e o código de leitura NFC na
`Activity` não decidem nada de tela, só repassam o identificador lido
pro `core` decidir o resto, exatamente como `architecture.md` já exige
("o núcleo nunca decide aparência — só estado").

Fica de fora desta decisão, sem resposta ainda: o desenho exato de como
`app` recebe de volta, do `core`, o resultado da validação de uma
tentativa (Flow, callback, ou outro mecanismo) — pertence ao desenho
interno ainda pendente, não a esta fronteira.

## Referências

Fontes externas citadas no Contexto e na Decisão, no formato definido
pela norma ABNT NBR 6023 (Informação e documentação — Referências).
Citadas no corpo do documento como (ENTIDADE, ano).

GOOGLE. **Guide to app architecture**. Android Developers, [s.d.]a.
Disponível em: https://developer.android.com/topic/architecture.
Acesso em: 14 ago. 2026.

GOOGLE. **Bluetooth Low Energy**. Android Developers, [s.d.]b.
Disponível em:
https://developer.android.com/develop/connectivity/bluetooth/ble/ble-overview.
Acesso em: 14 ago. 2026.

GOOGLE. **Connect to a GATT server**. Android Developers, [s.d.]c.
Disponível em:
https://developer.android.com/develop/connectivity/bluetooth/ble/connect-gatt-server.
Acesso em: 14 ago. 2026.

GOOGLE. **NFC basics**. Android Developers, [s.d.]d. Disponível em:
https://developer.android.com/develop/connectivity/nfc/nfc. Acesso em:
14 ago. 2026.

GOOGLE. **NfcAdapter**. Android Developers, [s.d.]e. Disponível em:
https://developer.android.com/reference/android/nfc/NfcAdapter. Acesso
em: 14 ago. 2026. Referência atual da classe — confirma a assinatura
completa de `enableReaderMode` (`Activity`, `NfcAdapter.ReaderCallback`,
`int flags`, `Bundle extras`; método adicionado na API nível 19, sem
nenhuma marca de descontinuado na versão consultada). Mesmo assunto já
citado, indiretamente, em DA-LEI-04 do Projeto Arquitetônico (lá via
GOOGLE [s.d.]a/[s.d.]b daquele documento).
