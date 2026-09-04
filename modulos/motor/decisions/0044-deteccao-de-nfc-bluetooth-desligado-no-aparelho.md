# 0044 — Detecção de NFC/Bluetooth desligado no aparelho, pra tela de jogo

Resumo em linguagem simples: hoje, se a pessoa desligar o NFC ou o
Bluetooth do aparelho dela no meio de uma sessão, a tela de jogo não
avisa nada — ela só mostra "desconectado" (ou nada, se estiver usando
NFC direto), como se fosse falha momentânea de leitura, não rádio
desligado de propósito. Esta ADR decide como o aplicativo descobre que
um rádio está desligado, e como esse dado chega até a tela.

Convenção dos códigos citados abaixo:
- `EI-RET` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.7.

**Status:** aceito

**Contexto:**

[design/wireframe.md, Tela de jogo — posição dos elementos
comuns](<../design/wireframe.md#tela-de-jogo--posição-dos-elementos-comuns>)
já fixou onde esse aviso aparece e o texto que ele mostra ("NFC
desligado" / "Bluetooth desligado", substituindo o indicador de conexão
na mesma posição, nunca os dois ao mesmo tempo) — essa parte não é
assunto desta ADR. O que faltava era só o mecanismo técnico: como o
aplicativo sabe, de verdade, que um rádio está desligado, distinto de
"aparelho sem esse hardware" (fora do alcance desta pendência, já que
não é algo que a pessoa possa "religar") e distinto de "acessório
Bluetooth não encontrado por perto" (que já é o estado `DISCONNECTED`
existente, [decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md)).

Essa pergunta já tinha sido levantada, sem resposta, em duas ADRs
anteriores do mesmo pacote `connectivity`:
[decisions/0017](0017-quem-decide-a-tecnologia-de-leitura.md)
("como ela é avisada se o rádio que ela quer usar está desligado no
aparelho... continua em aberto") e
[decisions/0018](0018-estrategia-de-permissao-de-bluetooth-e-nfc.md)
("como a pessoa fica sabendo se o Bluetooth ou o NFC estão desligados
no aparelho dela... pertence ao desenho visual das telas, ainda
pendente"). Esta ADR resolve essa mesma pergunta, agora que o desenho
visual (`wireframe.md`) já existe.

Hoje o aplicativo já checa dois pontos parecidos, mas nenhum cobre
"rádio desligado":
- `MainActivity.onResume()` ativa o modo leitor de NFC
  (`enableReaderMode`) sem checar antes se o NFC está ligado —
  [decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md).
- `BleAccessoryService.startScanAndConnect()` pega o adaptador
  Bluetooth do sistema, mas só verifica se ele existe (`adapter !=
  null`), nunca se está ligado (`adapter.isEnabled`).

Duas fontes oficiais, lidas na íntegra, traduzidas livremente abaixo:

Pro lado NFC (`android.nfc.NfcAdapter`, GOOGLE, [s.d.]a): `isEnabled()`
(existe desde a API 9) devolve verdadeiro se o adaptador tem algum
recurso ligado; se devolver falso, o hardware de NFC tem garantia de
não gerar nem responder a nenhuma comunicação pelo próprio rádio.
`ACTION_ADAPTER_STATE_CHANGED` (existe desde a API 18) é a notificação
de sistema disparada quando o estado do adaptador NFC muda — por
exemplo, ligado ou desligado.

Pro lado Bluetooth (`developer.android.com/develop/connectivity/bluetooth/setup`,
GOOGLE, [s.d.]b): chamar `isEnabled()` diz se o Bluetooth está ligado
no momento; se o método devolver falso, o Bluetooth está desligado.
Opcionalmente, o aplicativo também pode escutar a notificação de
sistema `ACTION_STATE_CHANGED`, disparada sempre que o estado do
Bluetooth muda, com os campos extras `EXTRA_STATE` e
`EXTRA_PREVIOUS_STATE` (estado novo e estado anterior) — estados
possíveis: `STATE_TURNING_ON`, `STATE_ON`, `STATE_TURNING_OFF`,
`STATE_OFF`.

Os dois pares de mecanismo (`isEnabled()` + notificação de mudança de
estado) existem desde uma versão de API bem anterior ao `minSdk` 24 já
fixado pelo módulo (`NfcAdapter.ACTION_ADAPTER_STATE_CHANGED`, o mais
recente dos quatro, é de API 18) — não há incompatibilidade com nenhum
aparelho já suportado por este aplicativo
([decisions/0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md)).

Registrar um receptor desses dois avisos em tempo de execução, porém,
exige mais um cuidado a partir do Android 13 (API 33) — dentro da faixa
de `targetSdk` já fixada em decisions/0012. O guia oficial de
broadcasts (GOOGLE, [s.d.]c), traduzido livremente: alguns avisos de
sistema vêm de aplicativos altamente privilegiados, como Bluetooth e
telefonia, que fazem parte do framework do Android mas não rodam sob o
identificador de processo único do sistema; pra receber todo aviso de
sistema, incluindo os desses aplicativos privilegiados, é preciso
marcar o receptor com a flag `RECEIVER_EXPORTED`.

Bluetooth está citado nominalmente como um desses "apps altamente
privilegiados" — sem a flag `RECEIVER_EXPORTED`, o receptor de
`BluetoothAdapter.ACTION_STATE_CHANGED` simplesmente não recebe o
aviso em aparelhos com Android 13 ou mais novo, sem erro nenhum
aparecer, só silêncio. NFC não é citado nominalmente na mesma frase,
mas nada na documentação garante o contrário pra ele — por segurança,
a mesma flag é usada nos dois receptores.

**Decisão:**

Um tipo novo, puro, dentro de `core/connectivity` — `Radio` (`NFC`,
`BLUETOOTH`) — identifica qual rádio físico está em jogo, do mesmo jeito
que `ConnectionState` já representa o estado da conexão Bluetooth: sem
nenhuma opinião sobre como isso aparece na tela.

O aplicativo (`app`) é quem sabe ligar de verdade com o rádio do
aparelho (mesma fronteira já fixada em
[decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md)
pro resto do pacote `connectivity`). Um `RadioStateListener` novo
(`fun interface`, mesmo padrão de `PieceReadListener`/
`ConnectionStateListener`) avisa `onRadioStateChanged(radio: Radio,
enabled: Boolean)` — um rádio por vez, nunca os dois combinados num
único aviso, porque `MainActivity` (NFC) e `BleAccessoryService`
(Bluetooth) são componentes independentes, cada um só sabe do próprio
rádio.

Cada lado usa os dois mecanismos confirmados acima, de forma simétrica:
- `MainActivity`: `onResume()` passa a checar `nfcAdapter?.isEnabled`
  antes de decidir o aviso (chamada a `enableReaderMode` continua
  incondicional, preservando o comportamento já existente — sem
  hardware ou com o rádio desligado, `enableReaderMode` já não tem
  efeito nenhum por conta própria); também registra dinamicamente um
  receptor de `NfcAdapter.ACTION_ADAPTER_STATE_CHANGED`, desregistrado
  em `onPause()` — mesmo ciclo de vida do modo leitor, já que ler NFC
  só faz sentido com a `Activity` em primeiro plano.
- `BleAccessoryService`: `startScanAndConnect()` passa a checar
  `adapter.isEnabled` antes de iniciar a busca — se desligado, avisa o
  `RadioStateListener` e não tenta buscar (hoje isso falhava em
  silêncio, sem avisar nada, só nunca encontrando nenhum acessório);
  registra dinamicamente um receptor de `BluetoothAdapter.ACTION_STATE_CHANGED`
  em `onCreate()`, desregistrado em `onDestroy()` — cobre o caso de a
  pessoa desligar o Bluetooth com uma sessão já em andamento (o
  `Service` continua vivo entre uma tentativa e outra, diferente da
  NFC, que só faz sentido junto da `Activity`).

Os dois registros dinâmicos usam a flag `RECEIVER_EXPORTED` a partir do
Android 13 (`Build.VERSION_CODES.TIRAMISU`), ramificando por versão do
sistema do mesmo jeito que `decisions/0018` já faz pra permissão de
Bluetooth — em versões anteriores, o registro de dois argumentos
(sem flag) continua funcionando normalmente.

Do lado do núcleo (`core`), `SessionViewModel` passa a implementar
`RadioStateListener`, guardando o último estado dos dois rádios
(`nfcEnabled`, `bluetoothEnabled`, os dois começando `true` — otimista,
mesmo espírito de `ConnectionState?` começar `null`/ausente até o
primeiro aviso real chegar). Toda vez que monta a tela
`SessionScreen.AwaitingAttempt`, calcula qual rádio (se algum) mostrar
como desligado:

```
connectionState != null -> !bluetoothEnabled ? BLUETOOTH : nenhum
connectionState == null -> !nfcEnabled ? NFC : nenhum
```

A regra usa `connectionState` (nulo quando não existe acessório em
jogo, [decisions/0022](0022-conteudo-do-estado-exposto-pelo-viewmodel.md))
como pista de qual caminho a pessoa está usando de fato, e olha só o
estado do rádio desse caminho — o estado do outro rádio nunca entra na
conta, mesmo que ele também esteja desligado: quando há acessório em
jogo, só o Bluetooth importa; quando não há acessório nenhum em jogo,
só o NFC importa (é o caminho provável, preenchendo a lacuna que hoje
deixa a pessoa sem nenhum sinal — Documento de Conceito, seção 8: a
tela precisa, no mínimo, confirmar). Avisar sobre um rádio que a
pessoa nem pretende usar seria instrução fora de contexto, não
confirmação — por isso a regra final não tem "caso os dois estejam
desligados": o caminho em uso já decide sozinho qual rádio checar,
mesmo quando o outro também está desligado.

Na tela (`SessionGameScreen.kt`), `SessionScreen.AwaitingAttempt` ganha
o campo `disabledRadio: Radio?`; o texto mostrado dá prioridade a esse
campo sobre o indicador de conexão normal, textos "NFC desligado" /
"Bluetooth desligado" — cumprindo `EI-RET-02`/Documento de Conceito
§8 (aviso, nunca instrução: não diz "vá em Configurações e ligue").

**Consequências:**

Alternativa descartada: representar "rádio desligado" como um quarto
valor dentro do próprio `ConnectionState` (`RADIO_OFF`). Rejeitada
porque `ConnectionState` é especificamente o estado da conexão *com o
acessório Bluetooth* — usar o mesmo tipo pra também representar "NFC
desligado" misturaria dois rádios diferentes dentro de um enum que,
por desenho, já tem `null` reservado pra "sem acessório em jogo"
([design/wireframe.md](<../design/wireframe.md#tela-de-jogo--posição-dos-elementos-comuns>):
"não é um quarto estado, é ausência de estado").

Alternativa descartada: só checar `isEnabled()` uma vez, sem os
receptores de notificação em tempo real. Rejeitada depois de confirmar
que as duas notificações (`ACTION_ADAPTER_STATE_CHANGED` pro NFC,
`ACTION_STATE_CHANGED` pro Bluetooth) já existem desde uma API bem
anterior ao mínimo já exigido pelo módulo — usar só checagem pontual
deixaria a tela sem atualizar se a pessoa desligasse o rádio no meio de
uma tentativa em andamento, contrariando a mesma exigência de "a tela
confirma" que motiva a pendência em primeiro lugar.

`SessionGameScreenTest.kt`, `SessionViewModelTest.kt`,
`MainActivityTest.kt` e `BleAccessoryServiceTest.kt` ganham teste novo
cobrindo, respectivamente: o texto exibido por `disabledRadio`; a regra
de precedência do `SessionViewModel`; a notificação real de
`MainActivity` (checagem inicial e receptor de broadcast); a notificação
real de `BleAccessoryService` (checagem antes de buscar e receptor de
broadcast).

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **NfcAdapter**. Android Developers, [s.d.]a. Disponível em:
https://developer.android.com/reference/android/nfc/NfcAdapter.
Acesso em: 03 set. 2026.

GOOGLE. **Set up Bluetooth**. Android Developers, [s.d.]b. Disponível
em: https://developer.android.com/develop/connectivity/bluetooth/setup.
Acesso em: 03 set. 2026.

GOOGLE. **Broadcasts overview**. Android Developers, [s.d.]c.
Disponível em:
https://developer.android.com/develop/background-work/background-tasks/broadcasts.
Acesso em: 03 set. 2026.
