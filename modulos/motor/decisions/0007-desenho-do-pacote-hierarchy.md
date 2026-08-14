# 0007 — Desenho do pacote hierarchy

Resumo em linguagem simples: já estava decidido que cada tema e cada
evento pode ser "com ordem" (ganha uma posição) ou "avulso" (sem
posição), e que nome não se repete dentro do mesmo grupo — mas nenhum
documento da cascata descia ao nível de como representar isso em
código, nem do que acontece quando duas coisas competem pelo mesmo
nome. Duas escolhas ficam registradas aqui: usar um tipo de dado que
torna impossível, em tempo de compilação, um item avulso carregar uma
posição por engano (ou um item com ordem não carregar nenhuma); e, ao
encontrar nome repetido, juntar a lista completa do que deu errado em
vez de parar no primeiro problema encontrado.

Convenção dos códigos citados abaixo:
- `EI-HIE` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.1.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.10.
- `DA-CFG` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.5.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado no [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
seção 6.3.3, e em [decisions/0004](0004-desenho-do-algoritmo-de-busca-aproximada.md)
e [decisions/0005](0005-abordagem-de-teste-do-nucleo-do-motor.md).
Cada trecho abaixo marcado descreve documentação oficial ou código
mantido por um terceiro (Kotlin/JetBrains; o arquivo de definição de
erro de API mantido pela Google) — não uma decisão deste projeto, e
sujeito a mudar em qualquer revisão futura dessa documentação ou desse
código, inclusive depois desta ADR ser aceita. Quem ler este documento
depois deve tratar esse conteúdo como possivelmente desatualizado e
reconfirmar na fonte oficial (seção Referências) antes de usar como
base pra mudar o código — uma nota de acompanhamento datada, permitida
pelo formato de ADR deste projeto (ver [decisions/README.md](README.md)),
é o jeito de registrar essa reconfirmação sem reescrever a decisão
original.

**Status:** aceito

**Contexto:** EI-HIE-01 a EI-HIE-04 já fixam o comportamento — nome não
se repete dentro do grupo imediatamente acima; cada tema (numa
instância) e cada evento (num tema) declara individualmente se tem
ordem ou é avulso; um item "com ordem" recebe posição, um item avulso
não recebe — mas, do mesmo jeito que aconteceu com o pacote `search`
(ver [decisions/0004](0004-desenho-do-algoritmo-de-busca-aproximada.md)),
nenhum documento da cascata desce ao nível de como representar essa
regra em código, nem do que fazer quando ela é violada. Dois pontos
ficam em aberto:

1. Como representar, no código, a diferença entre um item "com ordem"
   (que precisa de uma posição) e um item avulso (que não pode ter
   posição nenhuma) — um campo de tipo mais um campo de posição que
   pode ou não estar preenchido (copiando a forma do contrato de dado
   em `concept.md`), ou uma forma que o próprio compilador já sabe
   diferenciar?
2. Ao encontrar dois itens do mesmo grupo com o mesmo nome (violação de
   EI-HIE-01), o código para imediatamente no primeiro problema
   encontrado, ou junta a lista completa de problemas antes de
   devolver o resultado?

Pro primeiro ponto, a diferença entre as duas formas de representar
"com ordem"/"avulso" está em quando uma combinação inválida é barrada:
com um campo de posição opcional, um item avulso carregando uma
posição por engano só seria percebido se alguém escrever — e lembrar
de rodar — uma checagem em tempo de execução; com um tipo próprio,
essa combinação nunca chega a existir, porque não há como escrever
esse código no primeiro lugar. `[REVISAR-EXTERNO]` O mecanismo que o
Kotlin oferece pra isso é a *sealed class*/*sealed interface*: uma
hierarquia de tipos em que todas as subclasses diretas são conhecidas
em tempo de compilação, cada uma carregando só os dados que fazem
sentido pra ela, e sobre a qual o compilador cobra checagem exaustiva
de cada caso — um `when` sobre um tipo selado não precisa, e não
deveria, ter um `else` de escape (JETBRAINS, [s.d.]).

Pro segundo ponto, como confirmação de que devolver a lista completa
de problemas encontrados, em vez de parar no primeiro, é prática
adotada de fato, em escala, por uma empresa de software real: o modelo
padrão de erro de API do Google (`google.rpc.BadRequest`, parte do
repositório público `googleapis`, usado em toda a família de APIs do
Google Cloud) declara o campo `field_violations` como uma lista
(`repeated`), não um valor único — desenhado, por definição, pra
carregar mais de uma violação de campo na mesma resposta de erro
(GOOGLE, [s.d.]). `[REVISAR-EXTERNO]` Esse arquivo é mantido pela
Google e pode mudar em qualquer revisão futura do repositório. O mesmo
padrão também já tem precedente dentro da própria cascata do motor:
DA-CFG-03 e PD-IMP-02 já decidiram que um item de conteúdo incompleto
ou com erro "é recusado sozinho, sem impedir a importação do restante
do pacote", com o motor apontando "exatamente quais campos faltaram, e
em qual item". `hierarchy` segue essa mesma postura, aplicada agora à
regra de nome único.

**Decisão:**

1. **A relação "com ordem" ou "avulso" é um tipo próprio, com duas
   formas que o compilador já diferencia.** Uma *sealed interface*
   `Ordering`, com duas variantes: `Ordered(position: Int)` e
   `Standalone` — usada tanto por `Theme` quanto por `Event`, já que
   EI-HIE-03 aplica a mesma regra aos dois níveis. Não existe, no
   código, um jeito de montar um `Standalone` carregando uma posição,
   nem um jeito de montar um "com ordem" sem uma — o próprio tipo
   torna esse estado irrepresentável, em vez de depender de uma
   checagem em tempo de execução pra barrar essa combinação depois de
   já montada.

2. **A checagem de nome repetido devolve a lista completa de
   violações, nunca lança exceção na primeira encontrada.** Uma função
   de validação percorre todo o grupo (os temas de uma instância, os
   eventos de um tema) e devolve uma lista — vazia quando não há
   problema, com um item por nome repetido encontrado quando há. Essa
   lista é, ela mesma, um tipo selado (`HierarchyViolation`), pensado
   pra crescer com novas regras de violação no futuro sem quebrar quem
   já consome essa lista hoje.

**Consequências:** o pacote `hierarchy` (`core/hierarchy/`) passa a
expor `Instance`, `Theme`, `Event` e `Ordering` como os tipos que
representam a estrutura navegável do conteúdo, mais uma função de
validação que devolve `List<HierarchyViolation>`. Nenhum dos tipos
carrega campo de conteúdo de evento (marco zero, dica, fotogramas) —
isso seguiria pertencendo ao pacote `content`/`session`, quando
existirem, mesma fronteira que [decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)
já havia traçado por assunto funcional. Igual ao pacote `search`,
nenhum tipo ou função de `hierarchy` depende de classe de interface do
Android — testável por teste de unidade comum, com `kotlin-test` +
JUnit Jupiter, mesma ferramenta já fixada em
[decisions/0005](0005-abordagem-de-teste-do-nucleo-do-motor.md).

Fica de fora desta decisão, registrado só como pendência futura: o
cálculo de que itens formam um recorte contíguo dentro de um grupo
ordenado (EI-SES-06 a EI-SES-08), necessário pra compor uma sessão que
atravesse mais de um evento ou tema — isso pertence ao pacote
`session`, ainda não desenhado (ver
[tasks.md](<../docs/tasks.md#em-aberto>)). `hierarchy` expõe a lista
ordenada, com a posição de cada item; decidir e montar o recorte em
si fica pra quando `session` for desenhado, pra não desenhar uma API
em cima de uma necessidade ainda não concretizada.

## Referências

GOOGLE. **error_details.proto — google.rpc** [código-fonte]. In:
googleapis [repositório de código]. [S.l.], [s.d.]. Disponível em:
https://github.com/googleapis/googleapis/blob/master/google/rpc/error_details.proto.
Acesso em: 14 ago. 2026.

JETBRAINS. **Sealed classes and interfaces**. Kotlin Help, [s.d.].
Disponível em: https://kotlinlang.org/docs/sealed-classes.html. Acesso
em: 14 ago. 2026 (página com data de revisão de 29 jun. 2026).
