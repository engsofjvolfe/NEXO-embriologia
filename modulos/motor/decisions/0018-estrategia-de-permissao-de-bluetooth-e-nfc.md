# 0018 — Estratégia de permissão de Bluetooth e NFC no aplicativo

Resumo em linguagem simples: o aplicativo pede a permissão de
Bluetooth pra pessoa do jeito comum que qualquer aplicativo pede — na
hora que ela começa a jogar (não assim que o aplicativo abre, sem
motivo). A permissão de NFC nem precisa desse pedido: não é do tipo
que aparece como pop-up, só uma declaração fixa dentro do aplicativo.
Esta ADR depende diretamente de
[decisions/0017](0017-quem-decide-a-tecnologia-de-leitura.md): como a
pessoa é quem decide, ligando o rádio que ela quer usar, o aplicativo
não precisa mais adivinhar nada antes de pedir a permissão de
Bluetooth — só espera o momento certo.

Convenção dos códigos citados abaixo:
- `DA-LEI` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.1.
- `PD-CON` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.2.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Todo trecho abaixo marcado
descreve documentação oficial de terceiro (Google/Android) — não uma
decisão deste projeto, sujeita a mudar em qualquer revisão futura
dessa documentação, principalmente o modelo de permissão de Bluetooth,
que já mudou uma vez (Android 12) e pode mudar de novo. Quem ler este
documento depois deve tratar esse conteúdo como possivelmente
desatualizado e reconfirmar na fonte oficial (seção Referências) antes
de usar como base pra qualquer decisão nova.

**Status:** aceito

**Contexto:**

[decisions/0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md)
já tinha registrado, como trabalho pendente do pacote `connectivity`
(então ainda não escrito), que `minSdk` 24 obriga tratar dois modelos
de permissão de Bluetooth diferentes: antes do Android 12 (nível de
API 31), ler aparelhos próximos por Bluetooth exige permissão de
localização (`ACCESS_FINE_LOCATION`); a partir dali, passa a exigir as
permissões dedicadas `BLUETOOTH_SCAN` e `BLUETOOTH_CONNECT`.

[decisions/0017](0017-quem-decide-a-tecnologia-de-leitura.md) já
decidiu que a pessoa, não o aplicativo, escolhe qual tecnologia usar —
o aplicativo dá suporte às duas ao mesmo tempo. Falta só decidir a
declaração exata no `AndroidManifest.xml` e o momento de pedir cada
permissão pra pessoa.

`[REVISAR-EXTERNO]` Checando a fonte já citada em decisions/0012
(GOOGLE, [s.d.]d — mesma referência reaproveitada aqui como GOOGLE,
[s.d.]a), a declaração completa, pra funcionar do Android 7.0
(`minSdk` 24) até o mais recente, precisa dos dois modelos ao mesmo
tempo, cada um restrito à faixa de versão certa:

```xml
<uses-permission android:name="android.permission.BLUETOOTH"
    android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"
    android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"
    android:maxSdkVersion="30" />

<uses-permission android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

O atributo `maxSdkVersion="30"` faz o Android ignorar essas três
permissões antigas em aparelhos com Android 12 ou mais novo, onde elas
já foram substituídas; `usesPermissionFlags="neverForLocation"` declara
que o aplicativo não usa a leitura de aparelhos por perto pra descobrir
onde a pessoa está — permitido aqui porque é a verdade, e evita ter que
pedir `ACCESS_FINE_LOCATION` também na faixa nova. Nenhuma dessas
permissões é concedida automaticamente — são permissões de risco
(*dangerous*), que exigem pedido explícito em tempo de execução, com a
pessoa podendo aceitar ou recusar.

A permissão de NFC é diferente: `android.permission.NFC` não é de
risco — só precisa constar no manifesto, sem pedido nenhum em tempo de
execução (GOOGLE, [s.d.]b, já citada em
[decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md)).

A documentação oficial do Android sobre quando pedir permissão de risco
é direta: "Ask for a permission in context, when the user starts to
interact with the feature that requires it" — pedir a permissão no
contexto, no momento em que a pessoa começa a interagir com a
funcionalidade que precisa dela, nunca antes (GOOGLE, [s.d.]c).

**Decisão:**

O aplicativo pede a permissão de Bluetooth no momento em que a pessoa
está prestes a começar uma sessão de jogo — o mesmo momento em que
qualquer um dos dois caminhos de leitura passa a importar. Não existe
mais checagem de hardware decidindo se o pedido acontece ou não
([decisions/0017](0017-quem-decide-a-tecnologia-de-leitura.md)): o
pedido acontece sempre, nesse momento, independente do aparelho ter ou
não NFC — a pessoa que decide, ao aceitar ou recusar, e ao ligar ou não
o Bluetooth por fora do aplicativo, qual caminho ela de fato vai usar.

A declaração de `uses-feature` do Bluetooth de baixo consumo e do NFC
seguem a mesma lógica de não exigir hardware que pode não existir:
`android:required="false"` nos dois (`android.hardware.bluetooth_le` e
`android.hardware.nfc`) — nenhum dos dois é obrigatório pro aplicativo
instalar.

**Consequências:**

O `AndroidManifest.xml` do módulo `app` ganha as cinco declarações de
permissão de Bluetooth acima (três antigas com `maxSdkVersion`, duas
novas), mais a permissão de NFC e as duas declarações de `uses-feature`
opcionais. O pedido de permissão de Bluetooth em tempo de execução mora
junto do código que inicia uma sessão de jogo — ainda não escrito, ver
[tasks.md](<../docs/tasks.md#em-aberto>) — não dentro do `Service` de
Bluetooth em si (`connectivity/BleAccessoryService.kt`), que só deve
ser iniciado depois da permissão já concedida.

Fica de fora desta decisão: o texto exato mostrado pra pessoa
explicando por que o aplicativo quer aquela permissão (o "rationale"
que a documentação oficial recomenda mostrar antes do pedido, em
alguns casos), e como a pessoa fica sabendo se o Bluetooth ou o NFC
estão desligados no aparelho dela — as duas pertencem ao desenho visual
das telas, ainda pendente (ver [tasks.md](<../docs/tasks.md#em-aberto>)).

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **Bluetooth permissions**. Android Developers, [s.d.]a.
Disponível em:
https://developer.android.com/develop/connectivity/bluetooth/bt-permissions.
Acesso em: 14 ago. 2026. Mesma fonte já citada em
[decisions/0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md).

GOOGLE. **NFC basics**. Android Developers, [s.d.]b. Disponível em:
https://developer.android.com/develop/connectivity/nfc/nfc. Acesso em:
14 ago. 2026. Mesma fonte já citada em
[decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md).

GOOGLE. **Request runtime permissions**. Android Developers, [s.d.]c.
Disponível em: https://developer.android.com/training/permissions/requesting.
Acesso em: 14 ago. 2026.
