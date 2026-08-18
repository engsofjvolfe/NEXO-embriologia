# Findings — Motor

<!-- module-doc-type: findings -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Findings |
| Versão | 0.6.0 |
| Data | 17-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Achados confirmados (por leitura de código, teste ao vivo, ou os dois)
> sobre este módulo — cada entrada datada.
>
> Checa se o código já existente bate com o requisito que `concept.md`
> já descreve -- nunca o contrário: um achado aqui não muda o que
> `concept.md` diz que deveria existir, só revela onde o código diverge
> disso (a divergência vira pendência em `tasks.md`). Pode acontecer a
> qualquer momento: antes de `architecture.md` existir, ou depois,
> quando a implementação revela algo não previsto no desenho.
>
> Um achado, uma vez escrito, não é apagado nem reescrito se deixar de
> valer depois (mudança real de código, por exemplo) — ganha uma entrada
> nova, datada, dizendo o que mudou. Igual ADR: acrescenta, não
> reescreve por cima.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, campo `Confirmado por` com valor fixo, resumo
> simples, depois detalhe técnico.

## Índice
- [Achados](#achados)
- [Controle de versão](#controle-de-versão)

## Achados

### <a id="2026-08-14-posicao-com-buraco-nao-detectada"></a>2026-08-14 — Posição de tema/evento com buraco ou duplicada não é detectada

**Confirmado por:** leitura de código

*Resumo simples:* o pacote `hierarchy` já existente checa nome
repetido dentro de um grupo, mas nunca checa se as posições dos itens
"com ordem" desse mesmo grupo fazem sentido — dois eventos podendo
declarar a mesma posição, ou um cadastro pulando um número (posições
1 e 3, sem o 2), passam sem nenhum aviso. Isso diverge do que o
próprio Documento de Conceito já exigia desde a aprovação da cascata.

*Detalhe técnico:*
- [`1 - documento-de-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
  seção 2: "Dentro de um tema, os eventos podem ter ordem entre si
  (**um evento é continuação lógica do anterior**) ou podem ser
  avulsos." Um item "com ordem" é definido como continuação do
  **anterior** — não existe "anterior" pra um item cuja posição
  imediatamente inferior não foi cadastrada por ninguém, o que torna
  buraco de posição incompatível com a própria definição de "ter
  ordem", não só "improvável".
- [`HierarchyValidation.kt`](<../core/src/main/kotlin/org/nexo/motor/core/hierarchy/HierarchyValidation.kt>)
  (antes desta correção) só implementava `duplicateNames` — nenhuma
  função olhava pro campo `position` de `Ordering.Ordered`.
- Descoberto durante o desenho do pacote `session`
  ([decisions/0009](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>)):
  o cálculo de recorte contíguo de uma sessão precisa presumir que a
  numeração de um grupo "com ordem" já chega correta — sem essa
  garantia vindo de `hierarchy`, `session` teria que reimplementar a
  mesma checagem por conta própria, ou (erro pior) simplesmente
  aceitar dado incorreto como se fosse uma sequência válida.
- Resolvido corrigindo `HierarchyValidation.kt`: para cada grupo "com
  ordem" (temas de uma instância, eventos de um tema), as posições
  declaradas precisam formar exatamente `1, 2, ..., N` (N = quantidade
  de itens "com ordem" do grupo) — sem pular número, sem repetir.
  Qualquer outro conjunto de posições vira uma violação nova
  (`NonContiguousThemePositions`, `NonContiguousEventPositions`),
  seguindo o mesmo formato de lista completa já usado desde
  [decisions/0007](<../decisions/0007-desenho-do-pacote-hierarchy.md>).

### <a id="2026-08-14-violacao-de-hierarquia-nao-impedia-pacote-de-ficar-disponivel"></a>2026-08-14 — Violação de hierarquia era relatada, mas o pacote inteiro ainda ficava disponível pra jogo

**Confirmado por:** leitura de código

*Resumo simples:* a primeira versão do pacote `content` chamava a
checagem de hierarquia (nome repetido, posição com buraco) sobre os
temas/eventos de um pacote de conteúdo, mas só acrescentava o problema
numa lista de avisos, sem efeito nenhum sobre o resultado — o
programa nunca chegava a apagar nem alterar nada do arquivo original
(isso nunca foi o problema), mas também não recusava o pacote: a
instância inteira, incluindo o pedaço com a violação, era devolvida
como se estivesse pronta pra jogo. Isso divergia de DA-CFG-02 ("não
existe estado de conteúdo 'incompleto, mas disponível para jogo', em
nenhum nível"), já decidido antes desta tarefa.

*Detalhe técnico:*
- `ContentImport.kt` (antes desta correção): depois de montar
  `cleanedInstance` e chamar `validateHierarchy(...)`, o resultado da
  validação só virava `ContentViolation.Hierarchy` acrescentado à
  lista — a função sempre devolvia `cleanedInstance` inteiro como
  `ContentImportResult.instance`, mesmo quando essa lista não estava
  vazia.
- Exemplo concreto que expôs o problema: um tema com eventos
  declarados nas posições 1 e 3 (sem o 2, "com ordem") era aceito e
  devolvido como pronto pra jogo, com a violação presente só como nota
  informativa, sem efeito nenhum sobre o resultado.
- Descoberto em conversa direta durante a revisão da tarefa, ao
  perguntar sobre esse cenário específico (posições 1, 3) antes de dar
  a tarefa como concluída — não em teste automatizado (nenhum teste
  cobria esse caminho até então).
- Resolvido não com uma exclusão seletiva do pedaço problemático, mas
  com uma reformulação maior do desenho de validação do pacote
  `content`: qualquer violação, em qualquer lugar, agora recusa o
  pacote inteiro — ver
  [decisions/0013](<../decisions/0013-desenho-do-pacote-content.md>),
  decisão 1.

### <a id="2026-08-15-registro-de-sessao-incompleto-frente-a-ei-reg-01"></a>2026-08-15 — Registro interno de `session` incompleto frente a EI-REG-01

**Confirmado por:** leitura de código

*Resumo simples:* o registro que `session` mantém de tudo que acontece
numa partida (usado depois pelo relatório final) não guardava três
coisas que a Especificação já exige desde antes deste código existir:
o horário de cada acontecimento, o momento em que a sugestão de
estudo aparece na tela, e a distinção entre pausa explícita e
ociosidade.

*Detalhe técnico:*
- Raciocínio completo — o texto normativo (EI-REG-01, Conceito §12,
  RF-PAU-01, RF-REG-01) comparado item a item contra o código, incluindo
  a correção de uma conclusão errada no meio do caminho — em
  [analysis.md#2026-08-15-revisao-do-registro-de-sessao-contra-ei-reg-01](<analysis.md#2026-08-15-revisao-do-registro-de-sessao-contra-ei-reg-01>);
  não repetido aqui.
- Em código: `SessionEvent` (`SessionState.kt`, antes desta correção)
  não tinha campo de horário em nenhuma variante; `studySuggestionAvailable`
  (`SessionTransitions.kt`) calculava só um booleano, sem gravar no
  registro que a sugestão *de fato* apareceu; existia um único tipo de
  evento (`Paused`) pras duas situações de interrupção, sem guardar
  qual gatilho ocorreu.
- Resolvido acrescentando: campo `timestamp: Long` em toda variante de
  `SessionEvent`, recebido como parâmetro explícito por cada função de
  transição (mesmo padrão já usado pra `tagId`/`expectedTagId`); tipo
  novo `SessionEvent.StudySuggestionShown` e função
  `showStudySuggestion`, espelhando `HintUsed`/`useHint`; tipo novo
  `SessionEvent.WentIdle` e função `goIdle`, ao lado do `Paused`/`pause`
  já existente. Nota de acompanhamento acrescentada em
  [decisions/0008](<../decisions/0008-representacao-do-estado-da-sessao.md>),
  que afirmava cobrir "exatamente os fatos que EI-REG-01 já exige" —
  afirmação incompleta até esta correção.

### <a id="2026-08-15-mecanismo-de-pdf-incompativel-com-core"></a>2026-08-15 — Mecanismo de PDF de `decisions/0019` incompatível com o módulo `core`

**Confirmado por:** leitura de código e teste ao vivo

*Resumo simples:* a decisão que escolheu `android.graphics.pdf.PdfDocument`
pra gerar o PDF do relatório também afirma que o pacote `report` fica
inteiro "sem depender de nenhuma classe do Android" — as duas coisas
não podem ser verdade ao mesmo tempo, porque `PdfDocument` é uma
classe exclusiva do Android, e o pacote `report` mora dentro de `core`,
que é montado sem nada de Android.

*Detalhe técnico:*
- Raciocínio completo — leitura de `core/build.gradle.kts`, por que
  `android.graphics.pdf.PdfDocument`/`Canvas` não compilam num módulo
  Kotlin puro, e o precedente já existente no módulo pro mesmo tipo de
  problema (`connectivity`, decisions/0015) — em
  [analysis.md#2026-08-15-pdfdocument-incompativel-com-modulo-core-kotlin-puro](<analysis.md#2026-08-15-pdfdocument-incompativel-com-modulo-core-kotlin-puro>);
  não repetido aqui.
- Resolvido dividindo `report` do mesmo jeito que `connectivity` já
  divide: `core/report/` monta só dado (texto do CSV, lista de linhas
  do conteúdo do PDF), sem nenhuma classe do Android; `app/report/`
  desenha o PDF de verdade (`PdfDocument`/`Canvas`), escreve os dois
  arquivos no aparelho e monta o atalho de compartilhar. Testado por
  compilação real: `gradlew :app:assembleDebug`, `BUILD SUCCESSFUL`.
  Nota de acompanhamento acrescentada em
  [decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>) —
  a ferramenta escolhida (`PdfDocument`) não muda, só onde o código que
  a usa mora.

### <a id="2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric"></a>2026-08-17 — Caminho antigo de `ReportFileWriter.kt` não testável com Robolectric

**Confirmado por:** leitura de código

*Resumo simples:* `decisions/0025` escolheu Robolectric pra testar
`ReportFileWriter.kt`/`ReportShareIntent.kt` inteiros — mas dos dois
caminhos que `decisions/0019` já fixou (um por versão do Android), só
o mais novo (Android 10 em diante) dá pra testar assim. O mais antigo
(Android 7 a 9) depende de um retorno do Android que o Robolectric
nunca dispara.

*Detalhe técnico:*
- Raciocínio completo, com as três fontes que confirmam isso — em
  [analysis.md#2026-08-17-investigacao-de-teste-de-reportfilewriter-e-reportshareintent](<analysis.md#2026-08-17-investigacao-de-teste-de-reportfilewriter-e-reportshareintent>);
  não repetido aqui.
- Nota de acompanhamento acrescentada em
  [decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>) —
  a ferramenta escolhida (Robolectric) não muda pro caminho novo, só o
  alcance da decisão original, que presumia cobrir os dois caminhos.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. Entrada nova em "Achados" (append, sem
reescrever) também conta como mudança de conteúdo real. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial. | Criação inicial |
| 0.2.0 | 14-08-2026 | Achado "Posição de tema/evento com buraco ou duplicada não é detectada" acrescentado. | Desenho do pacote `session` revelou divergência no pacote `hierarchy` já existente |
| 0.3.0 | 14-08-2026 | Achado "Violação de hierarquia era relatada, mas o pacote inteiro ainda ficava disponível pra jogo" acrescentado. | Revisão do desenho do pacote `content`, em conversa direta antes de dar a tarefa como concluída |
| 0.4.0 | 15-08-2026 | Achado "Registro interno de `session` incompleto frente a EI-REG-01" acrescentado. | Revisão do registro de sessão antes de escrever o pacote `report` |
| 0.5.0 | 15-08-2026 | Achado "Mecanismo de PDF de decisions/0019 incompatível com o módulo core" acrescentado. | Revisão do mecanismo de PDF antes de escrever o pacote `report` |
| 0.6.0 | 17-08-2026 | Achado "Caminho antigo de `ReportFileWriter.kt` não testável com Robolectric" acrescentado. | Tentativa de escrever o teste de `ReportFileWriter.kt`/`ReportShareIntent.kt` |
