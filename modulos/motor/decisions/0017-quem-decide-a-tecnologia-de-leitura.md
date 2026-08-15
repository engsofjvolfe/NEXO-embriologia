# 0017 — Quem decide a tecnologia de leitura: NFC ou Bluetooth

Resumo em linguagem simples: o aplicativo não escolhe sozinho, escondido,
se vai usar a antena NFC do próprio aparelho ou o acessório externo por
Bluetooth. **É a pessoa quem decide**, do jeito mais direto que existe:
ligando, no próprio aparelho, o rádio que ela quer usar (Bluetooth ou
NFC) — o aplicativo fica pronto pra funcionar com qualquer um dos dois,
sem tentar adivinhar ou decidir por conta própria. Essa decisão diverge,
de propósito, de uma frase do Projeto Arquitetônico já aprovado
(DA-LEI-03) — ver Contexto e Decisão abaixo pra entender exatamente
onde e por quê.

Convenção dos códigos citados abaixo:
- `DA-LEI` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.1.

**Status:** aceito

**Contexto:**

O Projeto Arquitetônico já aprovado (documento imutável — ver
[README do VMODEL](<../../../docs/docs-VMODEL-visao-geral/README.md>))
registra, em DA-LEI-03: *"Existem dois caminhos físicos de leitura,
escolhidos automaticamente conforme o hardware disponível no aparelho
de jogo: (a) leitura direta pela antena própria do aparelho; (b)
leitura por um acessório leitor externo, para aparelhos sem essa
antena."* E na definição do termo "Acessório leitor" (seção 4):
*"usado apenas quando o aparelho de jogo não tem antena própria de
leitura."*

Ao desenhar o pacote `connectivity` (ver
[decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md)
e
[decisions/0016](0016-formato-do-identificador-na-notificacao-bluetooth.md)),
esse texto foi seguido ao pé da letra: o desenho inicial previa o
aplicativo checar sozinho (`PackageManager.hasSystemFeature(FEATURE_NFC)`)
se o aparelho tem antena NFC, e decidir por conta própria qual caminho
usar, sem nenhuma ação da pessoa — inclusive ligando o Bluetooth e
pedindo a permissão dele de forma automática, assim que detectasse a
ausência de NFC.

Essa leitura foi questionada diretamente durante esta tarefa, com um
motivo concreto, já demonstrado na prática: a primeira leitura do
próprio texto de DA-LEI-03, feita por quem escreveu o requisito
original, foi "a pessoa escolhe qual usar" — o oposto do que o
documento diz por escrito. Se quem já conhece o projeto lê o próprio
requisito aprovado e entende o contrário do que ele diz, o
comportamento "automático e invisível" é frágil o bastante pra não ser
a base de uma decisão de arquitetura sem reconfirmar a intenção real
primeiro.

**Decisão:**

O aplicativo dá suporte aos dois caminhos de leitura ao mesmo tempo,
sempre — nunca escolhe um no lugar do outro. Qual caminho "funciona de
verdade" a qualquer momento depende só de qual rádio a pessoa ligou no
aparelho dela (fora do controle do aplicativo) e de qual permissão ela
concedeu, exatamente como qualquer aplicativo comum que oferece mais de
um jeito de fazer a mesma coisa.

Isso diverge da leitura literal de DA-LEI-03 ("escolhidos
automaticamente conforme o hardware disponível") — a parte que
continua válida desse item é que **os dois caminhos existem e são
tratados do mesmo jeito pela validação** (DA-LEI-06, que não muda); a
parte que deixa de valer, neste módulo, é o mecanismo de escolha
"automático e escondido" entre eles. Pela regra de imutabilidade, o
texto de DA-LEI-03 no Projeto Arquitetônico não é alterado — esta ADR
é o "outro lugar" onde o ajuste nasce, exatamente como o próprio README
do VMODEL prevê.

**Consequências:**

- Nenhuma checagem de hardware (`hasSystemFeature`) decide, sozinha,
  qual caminho usar — ela deixa de ser gatilho de qualquer coisa.
- A permissão de Bluetooth não é mais pedida "quando detectar que falta
  NFC" — o novo momento certo de pedir cada permissão fica registrado
  em [decisions/0018](0018-estrategia-de-permissao-de-bluetooth-e-nfc.md).
- [architecture.md, "Núcleo do motor"](<../docs/architecture.md#núcleo-do-motor>)
  e a seção "Pacote `connectivity`" citavam DA-LEI-03 como algo
  implementado à risca — ambas precisam de ajuste de texto pra não
  sugerir a escolha automática, apontando pra esta ADR.
- Como a pessoa sabe se está conectado, e como ela é avisada se o
  rádio que ela quer usar está desligado no aparelho — perguntas
  levantadas na mesma conversa — continuam em aberto, registradas em
  [tasks.md](<../docs/tasks.md#em-aberto>): são perguntas de
  experiência (o que a pessoa vê), não de qual tecnologia é usada por
  baixo.

**Nota de acompanhamento — 15-08-2026:**

*Resumo simples:* o Documento de Conceito (documento 1, a base de toda
a cascata) já dizia, antes mesmo do Projeto Arquitetônico existir, que
nada no sistema deveria ser decidido automaticamente — reforça, de um
lugar mais fundamental, a mesma decisão já tomada acima.

*Detalhe técnico:* lendo o
[Documento de Conceito](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>)
por completo (seção 15, "Modularidade e configuração manual"): "Os
parâmetros do sistema são definidos manualmente em dois momentos
distintos. Nada é inferido ou calculado automaticamente pelo sistema
— tudo é uma escolha explícita de configuração, num momento ou no
outro." Essa seção lista dois momentos (montagem do conteúdo,
configuração de sessão) sem citar tecnologia de leitura de peça entre
eles — mas o princípio geral ("nada é automático") já mostra, de forma
independente da questão sobre a leitura de DA-LEI-03, que a leitura
literal desse item do Projeto Arquitetônico ("escolhidos
automaticamente conforme o hardware disponível") já divergia do
documento mais fundamental da cascata, não só de uma intenção verbal
nunca escrita em lugar nenhum. A decisão acima corrige os dois pontos
ao mesmo tempo, sem que isso tivesse sido percebido no momento em que
foi tomada.
