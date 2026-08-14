# 0013 — Desenho do pacote content

Resumo em linguagem simples: o pacote `content` é a parte do motor que
lê o arquivo compactado que reúne todo o conteúdo de uma área (temas,
eventos, fotogramas, textos) e confere se ele bate exatamente com o
formato já combinado. A decisão de verdade registrada aqui é uma só:
**em qualquer nível da hierarquia — falta um tema numa instância,
falta um evento num tema, falta uma sequência de fotogramas num
evento, ou é só um campo isolado — o pacote inteiro é recusado.** Não
importa se é um problema ou vinte, nem em que nível ele está: a
resposta é sempre a mesma. O motor nunca edita nem apaga nada do
arquivo original — ele só lê e decide, sem meio-termo, entre devolver
a instância completa ou recusar tudo, avisando onde está cada
problema. Isso revisa uma escolha que já vinha registrada no Projeto
Arquitetônico, pelo mecanismo que o próprio documento já previa pra
isso — ver Contexto. As outras três coisas registradas aqui não são
decisões concorrentes com essa regra — são detalhes técnicos que
existem só pra fazer essa regra funcionar: onde achar o arquivo de
dados dentro do pacote compactado, como ler o pacote inteiro de uma
vez pra listar todos os problemas (não só o primeiro), e como detectar
um identificador físico repetido, que só aparece depois de ler tudo.

Convenção dos códigos citados abaixo:
- `EI-HIE` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.1.
- `DA-CFG` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.5.
- `DA-IMP` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.4.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Todo trecho abaixo marcado
descreve documentação oficial de terceiro (Kotlin/JetBrains, npm,
W3C) — não uma decisão deste projeto, sujeita a mudar em qualquer
revisão futura dessa documentação. Quem ler este documento depois deve
tratar esse conteúdo como possivelmente desatualizado e reconfirmar na
fonte oficial (seção Referências) antes de usar como base pra mudar o
código.

**Status:** aceito

**Contexto:**

DA-IMP-01/02 já fixam que o conteúdo vem num arquivo JSON, empacotado
junto das imagens num único arquivo ZIP; PD-IMP-01 já fixa o esquema
exato desse JSON (campo a campo); PD-IMP-03 já fixa
`java.util.zip.ZipFile` como mecanismo de leitura do ZIP. Nenhum desses
documentos desce ao ponto mais importante que falta resolver — o que
fazer com um pacote que tem parte válida e parte inválida ao mesmo
tempo — nem a um punhado de detalhes técnicos menores que a resposta a
esse ponto principal exige.

**O ponto principal.** DA-CFG-03 já registra uma resposta: "o motor
importa e libera para jogo somente os completos, listando os
recusados" — dando a entender que bastaria deixar de fora, da
instância devolvida, só a parte com problema, com o resto seguindo
disponível pra jogo. PD-IMP-02 repete a mesma ideia.

Só que o próprio Projeto Arquitetônico, na seção de premissas, já
marca essa escolha como provisória: "Essa é uma escolha deste
documento, não uma exigência que já vinha do documento de
Especificação; pode ser revista se, na prática, um comportamento
'tudo ou nada' (recusar o pacote inteiro se qualquer item estiver
incompleto) se mostrar mais adequado."

Dois problemas concretos, discutidos durante esta tarefa, mostram por
que "deixar só a parte ruim de fora" — mesmo tentando fazer isso com
cuidado — não resolve o caso, em nenhum nível da hierarquia (instância,
tema, evento ou sequência de fotogramas):

- **Grupo inteiro, não item isolado.** Quando o problema não é de um
  item sozinho, mas de um grupo inteiro — por exemplo, dois eventos
  declarados nas posições 1 e 3, sem o 2 —, não existe um único item
  "culpado" identificável. Deixar o grupo inteiro de fora (os dois
  eventos, sem adivinhar qual dos dois estava errado) chegou a ser
  considerado durante esta tarefa, mas foi descartado: mesmo assim, o
  restante do pacote ainda seria devolvido como "pronto pra jogo" com
  um pedaço faltando — exatamente o estado que DA-CFG-02 já proíbe
  existir, em qualquer nível.
- **Item isolado, mas ainda incompleto.** Mesmo um erro simples,
  isolado, num único campo de um único evento — esse sim com um único
  item claramente identificável —, ainda representa conteúdo que quem
  montou o pacote não terminou de escrever. Devolver o resto do
  pacote como pronto pra jogo, com essa peça faltando, não é diferente
  de devolver um evento com metade dos fotogramas: incompleto, mesmo
  que o resto esteja certo.

Não existe, portanto, um nível da hierarquia onde "deixar passar o que
está certo" seja seguro — a mesma pergunta, e a mesma resposta, valem
pra instância, tema, evento e fotograma.

**Os detalhes técnicos que a resposta acima exige:**

1. **Nome do arquivo JSON dentro do ZIP.** DA-IMP-02 diz que o JSON e
   as imagens "são empacotados num único arquivo compactado", sem
   fixar um nome pro arquivo JSON em si. Sem um nome combinado de
   antemão, o motor não tem como saber qual entrada do ZIP abrir —
   isso vale não importa qual seja a regra de aceitação escolhida
   acima.

2. **Como ler o pacote inteiro, de uma vez, listando todos os
   problemas em vez de parar no primeiro.** Pra aplicar a resposta
   acima com justiça — avisando exatamente tudo que está errado, não
   só a primeira coisa encontrada —, o motor precisa terminar de
   examinar o arquivo inteiro antes de decidir. O esquema de PD-IMP-01
   é um JSON Schema comum, que descreve a forma válida de um documento
   inteiro, não como percorrer um documento inválido item por item,
   catalogando cada problema.

   Duas formas reais de fazer essa varredura existem: decodificar o
   JSON inteiro direto pra um tipo Kotlin tipado (como `session` já
   faz pro próprio estado,
   [decisions/0011](0011-formato-de-serializacao-do-estado-de-sessao.md))
   — mas isso para no primeiro erro de tipo, sem conseguir ver o resto
   do documento; ou percorrer o JSON como uma árvore genérica,
   validando item por item, sem que um problema numa parte impeça de
   continuar checando o resto.

   Essa mesma varredura completa é também o que permite localizar um
   fotograma com problema (que, diferente de tema e evento, não tem
   campo `position` declarado — EI-HIE-04 —, só o índice dele dentro
   do array `frames`) e detectar um identificador de peça física
   (`tag_id`) repetido em qualquer lugar do pacote — a nota de
   PD-IMP-01 já avisa que "JSON Schema, por desenho, valida a
   estrutura de um documento isolado — não consegue expressar sozinho
   uma regra do tipo 'nenhum `tag_id` se repete em todo o pacote'" e
   que "essa checagem é feita pelo motor no momento da importação" —
   só é possível depois que o pacote inteiro já foi lido, nunca
   olhando um fotograma sozinho.

**Decisão:**

1. **Regra de aceitação: tudo ou nada.** Havendo qualquer violação —
   um campo isolado, uma combinação ambígua entre vários itens, um
   `tag_id` repetido —, em qualquer nível da hierarquia, o pacote
   inteiro é recusado.

   `ContentImportResult.instance` só vem preenchido quando a lista de
   violações está totalmente vazia; havendo qualquer uma, `instance`
   vem nulo e a lista completa de violações (nunca só a primeira
   encontrada) é devolvida, cada uma com o caminho exato de onde está
   o problema. O motor nunca deixa nada de fora silenciosamente, nem
   edita ou reescreve o arquivo original — ele só lê e decide.

   Isso revisa, pra este pacote especificamente, a escolha "item
   recusado sozinho, sem impedir o restante" registrada em
   DA-CFG-03/PD-IMP-02 — usando o mecanismo que o próprio Projeto
   Arquitetônico já previu pra essa revisão (ver Contexto), nunca
   reescrevendo o texto desses documentos, que permanecem aprovados e
   imutáveis como estavam.

   Motivo prático, além do já registrado no Contexto: um pacote
   "quase certo" que passa, com uma parte silenciosamente fora do ar,
   é mais fácil de não notar do que um pacote que simplesmente não
   abre — a segunda reação força quem montou o conteúdo a ir direto
   corrigir, antes de qualquer coisa ficar disponível pra quem for
   jogar.

2. **Nome fixo do manifesto: `content.json`, na raiz do arquivo ZIP.**
   Nenhuma norma obriga esse nome exato — é uma convenção deste
   projeto —, mas segue o mesmo padrão já usado, de forma consolidada,
   por outros formatos de pacote com um manifesto JSON de nome fixo na
   raiz: o `package.json` de todo pacote npm (NPM, [s.d.]) e o
   `manifest.json` histórico de aplicativo web progressivo, hoje
   convivendo com a extensão `.webmanifest` recomendada pela
   especificação mais recente do W3C (W3C, 2020). `[REVISAR-EXTERNO]`

3. **Varredura por árvore JSON genérica (`JsonElement`), não
   decodificação tipada direta.** O JSON do manifesto é parseado com
   `Json.parseToJsonElement` (kotlinx.serialization, já em uso no
   projeto desde
   [decisions/0011](0011-formato-de-serializacao-do-estado-de-sessao.md)),
   que "permite inspecionar, modificar e montar estruturas JSON
   diretamente, antes de convertê-las pra um tipo Kotlin ou pra texto"
   (JETBRAINS, [s.d.]). `[REVISAR-EXTERNO]`

   A partir dessa árvore, cada tema, evento e fotograma é validado
   individualmente: um item com campo obrigatório ausente, tipo
   errado, ou combinação proibida (`position` presente com `ordering:
   standalone`, `hint_content` ausente com `hint_enabled: true`, e as
   demais combinações já fixadas em PD-IMP-01) gera uma violação, com
   o caminho exato (ex.: `instance.themes[2].events[0]`, ou
   `instance.themes[2].events[0].frames[1]` pra um fotograma, já que
   este não tem `position` própria) e o motivo — sem impedir a
   validação do restante da árvore.

   Os itens que dá pra montar (mesmo os que vêm de um irmão inválido)
   são reunidos numa `ContentInstance`, que passa por mais duas
   checagens antes da Decisão 1 valer a palavra final:
   `hierarchy.validate()`
   ([decisions/0007](0007-desenho-do-pacote-hierarchy.md)) — reaproveita,
   sem duplicar, a checagem de nome repetido e de posição contígua
   (EI-HIE-01, EI-HIE-04) já escrita e testada ali — e a checagem de
   `tag_id` único em todo o pacote.

   Alternativa descartada: uma biblioteca externa de validação de
   JSON Schema (por exemplo, um validador Java/Kotlin que leia
   `schemas/pacote-de-conteudo.schema.json` diretamente) devolveria
   erros como texto genérico de caminho JSON, não como o mesmo tipo
   selado de violação (`HierarchyViolation`, `ContentViolation`) já
   usado no resto do núcleo — trocaria uma dependência nova por uma
   validação pouco mais automática, sem ganhar nada em cima do que
   `kotlinx.serialization` (já uma dependência do projeto) já resolve.

**Consequências:**

O pacote `content` (`core/content/`) ganha três arquivos:

- `Content.kt` — tipos de domínio (`Frame`, `ContentEvent`,
  `ContentTheme`, `ContentInstance`) e o tipo selado
  `ContentViolation`, com variantes `InvalidManifest`, `InvalidTheme`,
  `InvalidEvent`, `InvalidFrame`, `DuplicateTagId` e `Hierarchy`.
- `ContentImport.kt` — `importContentPackage(json: String):
  ContentImportResult`, a validação descrita acima.
- `ContentPackageArchive.kt` — leitura do ZIP via `ZipFile`, isolada
  da lógica de validação (o parsing do JSON não precisa saber que
  veio de um arquivo compactado, mesma separação de assunto já usada
  entre `SessionState.kt` e `SessionStatePersistence.kt`).

Nenhum tipo do pacote depende de classe do Android — testável por
teste de unidade comum, com `kotlin-test` + JUnit Jupiter
([decisions/0005](0005-abordagem-de-teste-do-nucleo-do-motor.md)).

`content` passa a depender de `hierarchy` (reaproveita `Ordering`,
`Instance`, `Theme`, `Event` e `validate()`) — primeira dependência
entre pacotes de `core` além de bibliotecas externas; aceita porque
evita duplicar a mesma checagem de nome/posição em dois lugares
divergentes. `session` continua sem depender de `content` nem
vice-versa (RNF-MOD-01,
[decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md)) — quem
liga as duas pontas (o `tag_id` esperado numa tentativa, o texto de
confirmação de um fotograma) é responsabilidade do módulo `app`,
ainda pendente (ver [tasks.md](<../docs/tasks.md#em-aberto>), "Decidir
quem monta o texto de resumo/síntese").

Nunca existe mesclagem de mais de um arquivo numa mesma importação:
`importContentPackage` recebe um único manifesto e devolve uma única
instância (completa) ou nenhuma — decidir o que fazer quando um nome
de instância já existente é reimportado (substituir, manter as duas)
é responsabilidade de uma camada de armazenamento ainda não desenhada,
fora do escopo deste pacote.

A Decisão 1 acima é a defesa de último nível, não a primeira — o jeito
mais direto de reduzir a frequência desse tipo de erro é impedir que
ele seja escrito, não recuperar dele depois de escrito. Isso já cabe
na pendência "Criar uma ferramenta de autoria de conteúdo amigável"
([TASKS.md, raiz](<../../../TASKS.md#em-aberto>)), que ganhou uma nota
apontando pra este ponto — ver a própria pendência.

## Referências

Fontes externas citadas no Contexto e na Decisão, no formato definido
pela norma ABNT NBR 6023 (Informação e documentação — Referências).
Citadas no corpo do documento como (ENTIDADE, ano).

JETBRAINS. **JSON elements**. Kotlin Programming Language, [s.d.].
Disponível em: https://kotlinlang.org/docs/serialization-json-elements.html.
Acesso em: 14 ago. 2026.

NPM, INC. **package.json**. npm Docs, [s.d.]. Disponível em:
https://docs.npmjs.com/cli/v10/configuring-npm/package-json. Acesso
em: 14 ago. 2026.

WORLD WIDE WEB CONSORTIUM (W3C). **Web App Manifest**. W3C Working
Draft, 19 out. 2020. Disponível em:
https://www.w3.org/TR/2020/WD-appmanifest-20201019/. Acesso em: 14
ago. 2026.
