# 0024 — Mecanismo do gatilho de ociosidade

Resumo em linguagem simples: a regra de "a sessão ficou parada tempo
demais" já existe pronta dentro do núcleo do motor (`goIdle`), mas
nada, até agora, mede esse tempo de verdade dentro do aplicativo — o
relógio em si nunca foi ligado. Esta ADR decide como esse relógio
funciona: o que o liga, o que o reinicia, e o que ele faz quando chega
ao fim sem nenhuma tentativa nova.

Convenção dos códigos citados abaixo:
- `EI-PAU` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.12.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. O trecho abaixo marcado
descreve documentação oficial de terceiro (Google/Android/JetBrains) —
não uma decisão deste projeto, sujeita a mudar em qualquer revisão
futura dessa documentação. Quem ler este documento depois deve tratar
esse conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra mudar código.

**Status:** aceito

**Contexto:**

EI-PAU-06 já fixa a regra em si, sem margem de escolha: "o motor conta
o tempo sem nenhuma tentativa dentro da sessão em curso. Ao atingir o
limiar escolhido na configuração daquela sessão, aciona a ociosidade."
`core/session.goIdle(state, timestamp)` já marca o estado como pausado
e registra o evento `WentIdle` — pronto e testado desde a resolução do
achado registrado em
[findings.md](<../docs/findings.md#2026-08-15-registro-de-sessao-incompleto-frente-a-ei-reg-01>).
O que falta é só o "relógio" do lado `app`: algo que conte o tempo
decorrido desde a última tentativa (`SessionViewModel.onPieceRead`,
[decisions/0020](0020-ligacao-entre-leitura-de-peca-e-a-tela.md)),
reinicie a cada tentativa nova, e chame `goIdle` quando o limiar
configurado (`SessionConfiguration.idleThresholdMillis`, `core/report`)
passar sem nenhuma tentativa.

Três formas de implementar esse relógio dentro de `SessionViewModel`
foram consideradas:

1. `Handler(Looper.getMainLooper()).postDelayed(...)`, cancelando e
   reagendando a cada tentativa (`removeCallbacks`). Mecanismo clássico
   do Android, não exige nenhuma dependência nova — mas introduz, pela
   primeira vez neste `ViewModel`, uma classe de framework bruto
   (`Handler`, `Looper`), quando hoje ele só toca classes de
   arquitetura (`ViewModel`) e Kotlin puro (`StateFlow`, já usado pro
   estado exposto à tela).
2. Uma corrotina Kotlin, lançada em `viewModelScope` (a mesma corrotina
   cancelada e relançada a cada tentativa), com `delay(idleThresholdMillis)`
   contando o tempo. `[REVISAR-EXTERNO]` A documentação oficial do
   Android descreve exatamente esse escopo — em tradução livre: "um
   escopo de corrotina (`ViewModelScope`) é definido pra cada
   `ViewModel` do aplicativo (...) qualquer corrotina lançada nesse
   escopo é cancelada automaticamente quando o `ViewModel` é encerrado"
   (GOOGLE, [s.d.]a). Cancelamento automático ao encerrar o `ViewModel`
   já vem de graça, sem código extra pra isso.
3. Um `CoroutineScope` próprio, criado à mão dentro do `ViewModel`
   (sem usar `viewModelScope`), com um `Dispatcher` escolhido
   manualmente. Evitaria depender de `Dispatchers.Main` (ver abaixo),
   mas exige reescrever, à mão, o cancelamento automático que
   `viewModelScope` já garante (sobrescrever `onCleared()` e cancelar
   o escopo manualmente) — refazendo algo que a opção 2 já resolve sem
   custo.

A opção 2 é a única que reaproveita, sem inventar mecanismo novo, o
mesmo estilo já usado neste `ViewModel` (`StateFlow`, corrotinas do
Kotlin) e o padrão que a própria documentação oficial recomenda pra
esse tipo de trabalho preso ao tempo de vida de um `ViewModel`. Ela
precisa de uma peça a mais pra funcionar: `viewModelScope` despacha
por padrão em `Dispatchers.Main`, que só existe em tempo de execução
se o módulo declarar a dependência
`org.jetbrains.kotlinx:kotlinx-coroutines-android` — hoje ausente (só
`kotlinx-coroutines-core`, usado até agora por `StateFlow`, que não
depende de nenhum dispatcher específico). `[REVISAR-EXTERNO]` A
documentação oficial do Kotlin/Android é explícita sobre esse
pré-requisito — em tradução livre: "pra usar corrotinas num projeto
Android, acrescente a seguinte dependência no arquivo `build.gradle`
do aplicativo: `org.jetbrains.kotlinx:kotlinx-coroutines-android`"
(GOOGLE, [s.d.]b).

Essa peça a mais não é tão nova quanto parece: confirmado direto no
código-fonte oficial da biblioteca, `Dispatchers.Main`, no Android, é
montado por dentro a partir do mesmo `Handler` preso ao `Looper`
principal que a opção 1 usaria diretamente — a classe responsável por
criar esse despachante busca o `Looper` principal e o transforma num
`Handler` (JETBRAINS, [s.d.]). A opção 2, então, não é uma peça extra
e arriscada: é a mesma peça da opção 1, só embrulhada numa API de
corrotina. E essa embalagem tem um motivo além de estilo: o campo que
guarda o andamento da sessão dentro do `ViewModel` (`sessionState`)
não tem nenhuma trava contra escrita simultânea — rodar o relógio
sempre na mesma linha de execução principal que já processa cada peça
lida evita que as duas coisas escrevam nesse campo ao mesmo tempo. Um
escopo de corrotina próprio (opção 3) despachando numa linha de
execução diferente da principal reabriria exatamente esse risco, a
menos que reescrevesse, por conta própria, o mesmo cuidado que a
opção 2 já traz de graça.

**Decisão:**

1. **`SessionViewModel` usa uma corrotina lançada em `viewModelScope`
   pra contar o tempo de ociosidade** (opção 2 acima). Um único `Job`
   guardado como propriedade privada (`idleJob`) é cancelado e
   relançado a cada nova tentativa — nunca mais de um relógio rodando
   ao mesmo tempo.
2. **O relógio começa a contar assim que o `ViewModel` é criado**
   (início da sessão, `init`), e reinicia em toda chamada de
   `onPieceRead` que chega a registrar uma tentativa de verdade
   (depois do `recordAttempt` já existente) — nunca em pulo, dica,
   sugestão de estudo, ou pedido de saída, seguindo a letra exata de
   EI-PAU-06 ("tempo sem nenhuma tentativa"), não a linguagem mais
   solta do Documento de Conceito, §12 ("sem nenhuma interação") — a
   Especificação é quem desce ao nível de regra concreta, e é ela que
   este código implementa.
3. **Ao vencer o prazo sem cancelamento, a corrotina chama
   `core/session.goIdle` e grava o estado resultante em disco**, com
   `saveSessionState` (já existente,
   [decisions/0010](0010-persistencia-do-estado-de-sessao-pausada.md)),
   no mesmo arquivo (`pausedStateFile`) que `onExitConfirmed` já usa
   pra apagar o estado retomável. Sem essa gravação, marcar o estado
   como pausado só na memória do `ViewModel` não cumpriria EI-PAU-01
   ("grava a posição esperada, o evento e a sessão em curso") — o
   ponto inteiro de existir um estado pausado é sobreviver ao
   aplicativo fechar.
4. **`onExitConfirmed` cancela o relógio antes de apagar o estado
   retomável.** Sem isso, uma corrotina de ociosidade já em andamento
   poderia vencer o prazo depois da saída confirmada e recriar, no
   disco, um arquivo de sessão pausada que acabou de ser apagado de
   propósito.
5. **Duas dependências novas em `app/build.gradle.kts`:**
   `org.jetbrains.kotlinx:kotlinx-coroutines-android` (fornece
   `Dispatchers.Main`, exigido por `viewModelScope`) e
   `androidx.lifecycle:lifecycle-viewmodel-ktx` (garante
   `viewModelScope` disponível, sem depender de precisar confirmar se
   a versão de `lifecycle-viewmodel` já fixada no projeto o inclui
   sozinha).

**Consequências:**

`SessionViewModel` passa a tocar `Dispatchers.Main` pela primeira vez,
mas continua sem guardar nenhuma referência a `Context`, `Service` ou
à própria tela — o limite fixado
em [decisions/0020](0020-ligacao-entre-leitura-de-peca-e-a-tela.md)
continua de pé. Testado só por compilação real
(`gradlew :app:assembleDebug`, `gradlew :core:test`), mesma situação de
todo o resto do lado `app` do `ViewModel` — sem teste automatizado,
mesma pendência "Decidir ferramenta de teste pro módulo `app`" já
registrada em `tasks.md`.

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **Use Kotlin coroutines with lifecycle-aware components**.
Android Developers, [s.d.]a. Disponível em:
https://developer.android.com/topic/libraries/architecture/coroutines.
Acesso em: 16 ago. 2026.

GOOGLE. **Improve app performance with Kotlin coroutines**. Android
Developers, [s.d.]b. Disponível em:
https://developer.android.com/kotlin/coroutines. Acesso em: 16 ago.
2026.

JETBRAINS. **HandlerDispatcher.kt** [código-fonte]. In: kotlinx.coroutines,
branch master. [S.l.], [s.d.]. Disponível em:
https://github.com/Kotlin/kotlinx.coroutines/blob/master/ui/kotlinx-coroutines-android/src/HandlerDispatcher.kt.
Acesso em: 16 ago. 2026.
