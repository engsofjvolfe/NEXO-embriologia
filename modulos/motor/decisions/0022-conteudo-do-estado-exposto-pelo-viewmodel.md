# 0022 — Conteúdo do estado exposto pelo `ViewModel`

Resumo em linguagem simples: a decisão anterior (0020) já tinha fixado
*como* a leitura de uma peça chega até a tela — só não tinha decidido
*o que*, exatamente, a tela recebe pra mostrar em cada momento do
jogo. Esta ADR decide isso: cada situação (esperando uma peça, acerto,
erro, dica, fim de evento, fim de cadeia, pedido de sair) ganha um
formato de dado próprio, tirado direto da tabela que já lista o que
cada tela precisa mostrar — nenhuma cor, fonte ou layout entra aqui,
só o conteúdo.

Convenção dos códigos citados abaixo:
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seções 6.9 e 6.10.
- `EI-RET` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.7.
- `EI-DIC` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.8.
- `EI-PUL` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.6.
- `EI-ENC` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.11.
- `EI-PAU` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.12.
- `EI-CFG` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.14.

**Status:** aceito

**Contexto:**

[decisions/0020](<0020-ligacao-entre-leitura-de-peca-e-a-tela.md>)
decidiu o mecanismo (`ViewModel`, `StateFlow`, função direta pra
receber aviso de peça lida) mas deixou o conteúdo do estado em aberto,
com uma justificativa errada — que precisava esperar o desenho visual.
Não precisa: a tabela DA-RET (Projeto Arquitetônico, seção 6.6) já
lista, linha a linha, "o que cada tela mostra — sem decidir aparência
(cor, fonte, layout)", e isso já é o bastante pra desenhar o formato
do dado.

Das 17 entradas da tabela,
[tasks.md](<../docs/tasks.md#em-aberto>) já separa (nota acrescentada
durante a revisão da pendência "Desenhar a aparência visual das
telas") 7 como páginas de navegação — fora do que esta ADR cobre, por
não fazerem parte de uma sessão já em curso, escopo que
[decisions/0020](<0020-ligacao-entre-leitura-de-peca-e-a-tela.md>) já
fixou — e 10 como variações de conteúdo dentro da sessão em jogo:
DA-RET-05, 06, 07, 08, 09, 10, 11, 12, 13 e 15. Esta ADR cobre essas
dez.

Pra cada uma dessas dez, o item de Especificação que a própria tabela
já cita dá o conteúdo exato — a Decisão, abaixo, só lista qual campo
cada uma exige, sem repetir a explicação de cada item de Especificação
já citada na Convenção acima. Quatro pontos, porém, precisam de
explicação à parte, porque não são só "copiar um campo da tabela":
mudam a forma do dado, ou puxam algo de fora dela.

- **DA-RET-05 (Referência), EI-SES-04** não tinha função própria em
  `session` ainda — achado durante esta ADR, não uma escolha de
  desenho (a regra em si já está fechada, três casos, sem alternativa;
  [architecture.md](<../docs/architecture.md#núcleo-do-motor>) já
  registra que ela "vira código direto, sem exigir ADR"). A função
  nova está nas Consequências.
- **DA-RET-06 (Aguardando tentativa), EI-RET-01** diz "não antecipa
  nem anuncia nada" — sem dado nenhum sobre a posição esperada. O
  único dado que entra vem de outro lugar, não da tabela: o estado de
  conexão do acessório (`ConnectionState`, já existe em
  [architecture.md, pacote `connectivity`](<../docs/architecture.md#pacote-connectivity--desenho-interno>)),
  pendência já registrada em
  [tasks.md](<../docs/tasks.md#em-aberto>) sobre mostrar isso de forma
  discreta.
- **DA-RET-11/12/13 (Resumo, mensagem de pulo, síntese de cadeia)**
  formam um grupo só, porque EI-ENC-03 exige que a síntese de cadeia
  (DA-RET-13) e o resumo (ou mensagem de pulo) do último evento
  "apareçam juntos, sem que uma substitua a outra" — por isso
  DA-RET-13 nunca vira campo/variante própria (ver Decisão, ponto 3).
- **DA-RET-15 (Confirmação de saída), EI-PAU-03** é um pedido de
  confirmação, não uma tela própria — pode acontecer em cima de
  qualquer uma das outras nove situações, sem substituir o que estava
  sendo mostrado (ver Decisão, ponto 1).

**Decisão:**

1. **O estado do `ViewModel` (`SessionUiState`) é um par: a situação
   atual do jogo (`screen`), mais um sinalizador independente de
   pedido de saída (`exitConfirmationRequested`, booleano)** — porque
   DA-RET-15 (EI-PAU-03) pode acontecer em cima de qualquer uma das
   outras nove situações, sem substituir o que estava sendo mostrado.

2. **A situação atual do jogo (`SessionScreen`) é um tipo fechado
   (`sealed interface`), uma variante por entrada da tabela DA-RET**
   (exceto DA-RET-13, ver ponto 3), cada uma carregando só o dado que
   o item de Especificação correspondente exige — nunca mais, nunca
   menos:

   | Variante | Campo(s) | DA-RET |
   |---|---|---|
   | `Reference` | `referenceImage: String` | 05 |
   | `AwaitingAttempt` | `connectionState: ConnectionState?` | 06 |
   | `AttemptAccepted` | `confirmationText: String?` | 07 |
   | `AttemptRejected` | (nenhum) | 08 |
   | `HintShown` | `hintContent: String` | 09 |
   | `StudySuggestionShown` | `skipAvailable: Boolean` | 10 |
   | `EventSummary` | `synthesis: String`, `hasNextEvent: Boolean`, `chainSynthesis: ChainSynthesisResult?` | 11 (+13) |
   | `SkipMessageShown` | `message: SkipMessage`, `hasNextEvent: Boolean`, `chainSynthesis: ChainSynthesisResult?` | 12 (+13) |

3. **DA-RET-13 nunca vira uma variante própria** — é o campo opcional
   `chainSynthesis` (`ChainSynthesisResult`, com as variantes
   `Continuous(synthesis: String)` e `Consolidated(totals:
   ChainSkipSynthesis)`), presente só quando o evento que terminou
   também é o último de uma cadeia com mais de um evento, direto da
   exigência de EI-ENC-03 de que as duas sínteses "aparecem juntas".

4. **`AttemptAccepted` e `AttemptRejected` não recebem `errorCount` nem
   `consecutiveAttempts`** — a Especificação nunca pede pra mostrar
   esses números na tela (EI-RET-02/03 só falam da mensagem/texto);
   eles só existem hoje pra decidir *quando* a dica ou a sugestão de
   estudo ficam disponíveis (EI-DIC-01/03), cálculo que continua
   dentro do `ViewModel`, nunca exposto como dado de tela.

**Consequências:**

`core/session/` ganha uma função nova, mecânica, sem alternativa de
desenho (já autorizada por
[architecture.md](<../docs/architecture.md#núcleo-do-motor>), "vira
código direto, sem exigir ADR"): dado o ponto de início da sessão, se
o evento é o primeiro dela, e as três imagens candidatas (fotograma
anterior, marco zero, última peça preenchida do evento anterior),
devolve qual delas é a referência, seguindo EI-SES-04 pelas suas
próprias palavras — nunca decide isso sozinha, quem chama já entrega
as três imagens prontas (mesma lógica de parâmetro explícito já usada
em `recordAttempt`).

O `ViewModel` (`app/ui/SessionViewModel.kt`) passa a poder ser escrito
de verdade — a pendência em [tasks.md](<../docs/tasks.md#em-aberto>)
deixa de estar bloqueada. Cada transição de `session`/`summary` mapeia
pra um novo `SessionScreen`: `recordAttempt` aceito →
`AttemptAccepted`; rejeitado → `AttemptRejected`; `useHint` →
`HintShown`; `showStudySuggestion` → `StudySuggestionShown`; fim de
evento sem pulo → `EventSummary`; fim de evento com pulo →
`SkipMessageShown`; início de tentativa → `Reference` seguido de
`AwaitingAttempt`.

Nenhum campo aqui depende de cor, fonte ou layout — quando o desenho
visual das telas acontecer (pendência que continua em aberto, ver
[tasks.md](<../docs/tasks.md#em-aberto>)), o trabalho é só decidir
como cada campo já definido aqui aparece na
tela, nunca inventar campo novo por causa da aparência.
