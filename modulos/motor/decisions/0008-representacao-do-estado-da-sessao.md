# 0008 — Representação do estado da sessão em curso

Resumo em linguagem simples: enquanto alguém joga, o sistema precisa
manter uma "ficha" do que está acontecendo agora — qual peça se
espera, quantos erros já aconteceram, se a dica já pode aparecer. A
decisão aqui é como montar essa ficha: um objeto único, responsável
por ela mesma (recomendação oficial do Android para esse tipo de
estado), que carrega dentro um pequeno diário de tudo que já
aconteceu na sessão — e os números que a Especificação já exige nunca
serem guardados soltos (contagem de erro do evento, tentativas
seguidas sem sucesso numa posição) são sempre calculados a partir
desse diário, nunca de um contador à parte.

Convenção dos códigos citados abaixo:
- `EI-VAL` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.4.
- `EI-ERR` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.5.
- `EI-DIC` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.8.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seções 6.9 e 6.10.
- `EI-PAU` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.12.
- `EI-REG` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.13.
- `DA-ARM` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.3.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Todo trecho abaixo marcado
descreve a recomendação atual de um terceiro (documentação oficial do
Android; documentação oficial da Microsoft sobre um padrão geral de
arquitetura de software) — nunca uma decisão deste projeto, e sujeito
a mudar sem aviso. Quem ler este documento depois deve tratar esse
conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra qualquer
decisão nova.

**Status:** aceito

**Contexto:** nenhum documento já existente resolve esta pergunta —
conferido por leitura completa antes de escrever esta ADR: nem a
cascata aprovada ([`docs/docs-VMODEL-visao-geral/`](<../../../docs/docs-VMODEL-visao-geral/>),
documentos 1 a 5), nem as sete ADRs anteriores deste módulo. A
cascata para, em relação a armazenamento, no nível "os dados de uma
sessão ficam só no aparelho, nunca em servidor central" (DA-ARM-01) —
nenhum documento desce ao nível de como isso é representado em
código. A decisão 0007 (`hierarchy`) já registrava, como pendência
explícita deixada de fora, que "o cálculo de que itens formam um
recorte contíguo... pertence ao pacote `session`, ainda não
desenhado" — confirmando que esta é território novo, não uma revisão
de algo já decidido.

O ponto concreto: EI-VAL-01 exige que exista sempre "exatamente uma
posição esperada"; EI-ERR-02 exige uma contagem de erros por evento
que "nunca zera durante a sessão"; EI-DIC-01 exige uma contagem de
tentativas seguidas sem sucesso por posição, que zera quando a
posição é preenchida; EI-PAU-01 exige guardar posição, evento e
sessão ao pausar. Tudo isso precisa de algum objeto que represente "o
estado da sessão agora" — mas nenhum documento decide a forma desse
objeto.

Um ponto já não é livre, porque a própria Especificação já decidiu:
EI-ERR-02 exige que a contagem de erros do evento "nunca guarda [...]
como um valor à parte do registro: ela é sempre lida a partir dele,
para não correr o risco de duas fontes divergentes sobre o mesmo
dado." Ou seja, pelo menos para esse número específico, guardar um
contador solto que é atualizado por conta própria já está descartado
pela própria Especificação — falta só decidir a forma geral que
aplica esse princípio no código.

Duas fontes oficiais foram consultadas, cada uma representando um
extremo real do problema:

1. `[REVISAR-EXTERNO]` A documentação oficial do Android (a
   plataforma já escolhida para este projeto, ver
   [decisions/0001](0001-linguagem-do-aplicativo.md)) recomenda o
   padrão "state holder": um objeto único, responsável por guardar o
   estado atual e a lógica que o modifica — "fonte única de verdade"
   (*single source of truth*) —, com o estado fluindo numa única
   direção (dado → lógica → apresentação) e mudando só através de
   ações bem definidas, nunca de vários lugares mexendo nele por
   conta própria (GOOGLE, [s.d.]). Esse padrão descreve *um* objeto
   de estado atual, atualizado em lugar — não um histórico completo
   sendo relido a cada consulta.

2. `[REVISAR-EXTERNO]` A documentação oficial da Microsoft sobre o
   padrão *Event Sourcing* — o oposto: em vez de guardar só o estado
   atual, guardar cada ação ocorrida, em ordem, como um "diário"
   somente de acréscimo, e sempre recalcular o estado atual relendo
   esse diário (processo chamado de *rehydration*). A própria
   documentação lista a vantagem que interessa aqui — "auditability":
   nunca existe o risco de duas fontes divergentes sobre o mesmo
   dado, porque só existe uma fonte, o diário — mas também avisa, com
   todas as letras, que é "a complex pattern that introduces
   significant trade-offs" e que, "for most systems and most parts of
   a system, traditional data management is sufficient"; recomenda
   aplicar o padrão "selectively", nunca como decisão de tudo ou nada
   para um sistema inteiro (MICROSOFT, [s.d.]).

Nenhuma das duas fontes, sozinha, encaixa no problema deste projeto:
a primeira não cobre a exigência que a Especificação já fez para a
contagem de erro (derivar de um registro, nunca guardar solta); a
segunda é desenhada para sistemas com múltiplos processos escrevendo
ao mesmo tempo no mesmo dado (conflito de escrita concorrente,
necessidade de auditoria entre serviços) — nenhuma dessas condições
existe aqui: um aparelho, uma pessoa, uma sessão por vez, garantido
pela própria Especificação (EI-PAU-05: "o motor garante que nunca
existem duas sessões pausadas ao mesmo tempo") e já refletido na
arquitetura (DA-ARM-01: guarda local, sem servidor central).

**Decisão:**

1. **O estado da sessão é um único objeto imutável — um "retrato" do
   momento atual —, substituído por um novo retrato a cada transição
   (tentativa, pulo, dica usada, pausa), nunca alterado em lugar.**
   Segue a recomendação de fonte única de verdade do Android, e a
   mesma lógica de imutabilidade já usada em
   [decisions/0007](0007-desenho-do-pacote-hierarchy.md) para o
   pacote `hierarchy` (tipos que não permitem estado inválido).

2. **Esse retrato carrega dentro um registro interno, em ordem, do
   que já aconteceu na sessão** (tentativa aceita, tentativa
   rejeitada, dica usada, posição pulada, pausa/ociosidade
   acionada) — cobrindo exatamente os fatos que EI-REG-01 já exige
   que a sessão registre. Isso é a ideia do "diário" da segunda
   fonte, aplicada só onde ela resolve um problema real, não como
   infraestrutura própria de armazenamento de eventos (sem sistema
   de fila, sem versionamento de esquema de evento, sem
   reconstrução de estado por repetição de todo o histórico) —
   coerente com a recomendação da própria Microsoft de aplicar o
   padrão "selectively".

3. **Todo número que a Especificação exige nunca ser guardado à
   parte é sempre calculado a partir desse registro interno, nunca
   de um contador próprio:** a contagem de erros por evento
   (EI-ERR-02) e, pela mesma lógica, a contagem de tentativas
   seguidas sem sucesso por posição que libera a dica (EI-DIC-01).

4. **Todo outro campo do retrato (posição esperada, se a sessão está
   pausada) é um campo direto, atualizado por uma função de
   transição explícita — não recalculado por repetição do
   registro.** A Especificação não exige esse nível de rigor para
   esses campos, e recalcular tudo por repetição seria adotar o
   padrão pesado da segunda fonte inteiro, contrariando a própria
   recomendação dela de uso seletivo.

**Consequências:** o registro interno do retrato (ponto 2) passa a
ser exatamente o material que o pacote `report` (ainda não desenhado)
vai precisar para montar o relatório de EI-REG-01/02 — `session`
expõe esse registro, `report` decide como guardá-lo e exportá-lo,
sem que `session` precise saber nada sobre formato de arquivo. Fica
de fora desta decisão, registrado só como pendência futura: onde
esse retrato fica salvo em disco entre uma abertura do aplicativo e
outra, para atender EI-PAU-01/02 (retomar uma sessão pausada depois
de o aplicativo fechar) — DA-ARM-01 já fixa que é sempre local, nunca
em servidor, mas não qual pacote grava isso nem qual mecanismo
exato; e como calcular um recorte contíguo de eventos/temas
(EI-SES-06 a 08), pendência já apontada em
[decisions/0007](0007-desenho-do-pacote-hierarchy.md). As duas ficam
para ADRs próprias.

Custo aceito: o retrato da sessão fica um pouco mais complexo que uma
lista simples de contadores soltos, porque combina campos diretos com
cálculo derivado do registro interno para alguns números específicos
— mas essa complexidade não é nova, só formaliza em código uma
exigência que a própria Especificação (EI-ERR-02) já fazia em texto.
Não adotar o padrão pesado (Event Sourcing) para a sessão inteira
significa também não ganhar, de graça, algumas vantagens que ele
ofereceria (por exemplo, desfazer uma ação relendo o diário) — aceito
porque nenhum requisito da Especificação pede esse tipo de operação.

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **State holders and UI state**. Android Developers, [s.d.].
Disponível em:
https://developer.android.com/topic/architecture/ui-layer/stateholders.
Acesso em: 14 ago. 2026.

MICROSOFT. **Event Sourcing pattern**. Azure Architecture Center —
Microsoft Learn, [s.d.]. Disponível em:
https://learn.microsoft.com/en-us/azure/architecture/patterns/event-sourcing.
Acesso em: 14 ago. 2026.
