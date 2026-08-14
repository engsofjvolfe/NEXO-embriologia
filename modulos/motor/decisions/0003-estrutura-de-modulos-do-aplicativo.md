# 0003 — Estrutura de módulos do aplicativo

Resumo em linguagem simples: o código do aplicativo (a parte que roda no
aparelho de quem joga) vai ficar dividido, desde o início, em duas gavetas
separadas dentro do mesmo projeto — uma só com a lógica que decide as
coisas (sem saber desenhar nada na tela) e outra só com as telas (que só
mostram o que a primeira decide, sem decidir nada por conta própria).
Essas duas gavetas são "módulos" de um jeito que a própria ferramenta que
monta o aplicativo (Gradle) entende e obriga a respeitar — não é só um
combinado de pasta que alguém pode quebrar sem querer.

Convenção dos códigos citados abaixo:
- `RNF-MOD` — [`2 - requisitos-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/2 - requisitos-conceito-geral.md>), seção 7.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado no [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
seção 6.3.3. Todo trecho abaixo marcado descreve a recomendação atual
de um terceiro (a documentação oficial do Android) — não uma decisão
deste projeto, e sujeita a mudar sem aviso. Quem ler este documento
depois deve tratar esse conteúdo como possivelmente desatualizado e
reconfirmar na fonte oficial (seção Referências) antes de usar como
base pra qualquer decisão nova.

**Status:** aceito

**Contexto:** [architecture.md](<../docs/architecture.md#aparelho-de-jogo-aplicativo>)
já havia decidido que o aplicativo se divide em duas partes — núcleo
(decide) e interface (mostra) — mas deixava em aberto como isso se
traduz em pastas e pacotes de um projeto Android real, registrando a
pendência em `tasks.md`. Duas alternativas reais foram consideradas:

(a) Projeto de módulo único: todo o código do aplicativo dentro de um só
módulo Gradle, com a separação núcleo/interface mantida só por convenção
de pasta ou pacote (ex.: pacotes `core` e `ui` dentro do mesmo módulo).
Mais simples de configurar no início, mas a separação depende
inteiramente de disciplina de quem escreve o código — nada barra, em
tempo de compilação, uma tela importar direto uma peça interna do
núcleo, ou o núcleo acabar importando uma classe de interface do Android
por engano.

(b) Projeto multi-módulo Gradle, com o núcleo e a interface vivendo em
módulos Gradle separados. Um módulo Gradle só enxerga o que outro expõe
publicamente e só compila contra um módulo que declarou como dependência
dele — a fronteira vira uma regra da própria ferramenta de build, não um
combinado informal. `[REVISAR-EXTERNO]` É também a estrutura que a
documentação oficial do Android recomenda para projetos com expectativa
de crescimento, descrevendo módulo como partes "fracamente acopladas e
autocontidas" de uma base de código, cada uma com "propósito claro"
(GOOGLE, [s.d.]b), e recomendando, como princípio central de
arquitetura, "separação de responsabilidades" e "reduzir dependência de
classes do Android" nas camadas que não são de interface, para melhorar
testabilidade e reduzir acoplamento (GOOGLE, [s.d.]a).

`[REVISAR-EXTERNO]` A mesma documentação oficial também alerta que
módulos demais, cedo demais, trazem custo (mais configuração, mais
tempo de build) sem benefício, numa base de código que ainda não
cresceu o bastante para justificar — recomendando começar pequeno e
desmembrar conforme a necessidade aparece, não modularizar de forma
prematura (GOOGLE, [s.d.]b).

**Decisão:** projeto Android multi-módulo, com dois módulos Gradle nesta
primeira versão:

- `core` — reúne tudo já listado como "núcleo do motor" em
  [architecture.md](<../docs/architecture.md#núcleo-do-motor>): lógica
  de sessão (validação, erro, pular, dica, pausa, registro), importação
  e validação do pacote de conteúdo, busca aproximada, guarda local de
  configuração/registro/relatório, e o papel de cliente BLE ao usar o
  acessório externo. Não depende de nenhuma classe de interface do
  Android (sem `Activity`, sem `Context` de tela, sem `Composable`) — só
  Kotlin puro e as partes do SDK do Android/Java que não são de
  interface (ex.: `java.util.zip.ZipFile`, já decidido em PD-IMP-03).
  Essa ausência de dependência de interface torna esse módulo testável
  com teste de unidade comum, sem precisar de aparelho ou emulador
  Android — atende de forma mais direta a uma pendência futura da mesma
  lista de `tasks.md` (escrever os testes).
- `app` — o módulo de entrada do Android (`Application`, `Activity`),
  hospedando a interface (telas) descrita em
  [architecture.md](<../docs/architecture.md#interface>). Depende do
  `core`; nunca o contrário — essa direção única de dependência é o que
  torna mecânica, e não só combinada, a regra que `architecture.md` já
  registrava em palavras ("o núcleo nunca decide aparência — só
  estado").

Dentro do módulo `core`, os pacotes são organizados por assunto
funcional — as mesmas categorias já usadas em toda a cascata de
documentação do motor (sessão/validação, hierarquia de conteúdo,
importação, busca/navegação, conectividade, registro/relatório) — não
por camada técnica genérica (`data`, `domain`, `ui` misturando assuntos
diferentes dentro de cada uma). Reunir todo código de um mesmo assunto
(ex.: tudo de importação de conteúdo) num único lugar segue a mesma
lógica de agrupamento por categoria que a documentação já usa (HIE, VAL,
PUL etc. — agrupamento por assunto, não por tipo de arquivo):

```
core/
  src/main/kotlin/org/nexo/motor/core/
    hierarchy/      instância, tema, evento — cadastro e navegação hierárquica
    session/        sessão em curso: validação, erro, pular, dica, encadeamento, pausa/ocioso/saída
    search/         busca aproximada (Levenshtein)
    content/        importação e validação do pacote de conteúdo
    connectivity/   cliente BLE, leitura de peça (NFC direta ou via acessório)
    report/         registro de sessão, relatório, exportação CSV/PDF
app/
  src/main/kotlin/org/nexo/motor/app/
    ui/             telas (Activities/Composables) — fluxo funcional definido em
                     architecture.md, seção Interface
```

O módulo `app` não é desmembrado em módulos de funcionalidade (ex.: um
módulo só para navegação, um só para relatório) nesta primeira versão —
como agrupar as 17 entradas de tela já listadas no Projeto Arquitetônico
(seção 6.6) em telas físicas é, ele mesmo, parte do desenho visual ainda
pendente (ver `tasks.md`); desmembrar a interface antes dessa decisão
arriscaria criar fronteiras de módulo em cima de um agrupamento de tela
que ainda pode mudar. Fica registrado como direção provável para quando
esse desenho visual acontecer, seguindo a mesma recomendação oficial de
desmembrar conforme a necessidade aparece (GOOGLE, [s.d.]b), sem
precisar reestruturar o que já existe — dividir um módulo existente em
módulos menores depois é incremental, não uma reescrita.

Nomes de pacote e classe em inglês (`core`, `session`, `content`, não
"núcleo", "sessão", "conteúdo") — mesma lógica já registrada em PD-IMP-01
do Projeto Detalhado para os nomes de campo do esquema do pacote de
conteúdo: nome de código é vocabulário de máquina e do próprio
ecossistema Kotlin/Android, documentado e usado em inglês em toda a sua
biblioteca padrão — identificador de código é convenção do ambiente
técnico em que o projeto é escrito, não comunicação com pessoa.

**Consequências:** a separação núcleo/interface que `architecture.md` já
exigia em texto passa a ser garantida pelo compilador — uma tentativa de
o módulo `core` importar uma classe de interface do módulo `app` quebra
a build, não passa despercebida numa revisão de código. O módulo `core`,
sem dependência de interface, fica testável por teste de unidade comum
da JVM, sem emulador. RNF-MOD-01 e RNF-MOD-02 (motor agnóstico a
conteúdo, nova instância sem alterar a lógica central) continuam válidos
sem mudança: nenhum dos dois módulos conhece conteúdo de nenhuma
instância específica — o pacote de conteúdo (dado) é lido em tempo de
execução pelo módulo `core`, nunca compilado dentro dele.

Custo aceito: dois arquivos de configuração de build (um por módulo) em
vez de um só, e uma pequena camada extra de indireção para o módulo
`app` chamar o `core` através de uma interface pública, em vez de acesso
direto a qualquer classe interna. Considerado um custo baixo diante da
garantia de fronteira ganha.

Fica de fora desta decisão, registrado só como direção provável:
desmembrar o módulo `app` em módulos de funcionalidade menores, quando o
desenho visual das telas (pendência em `tasks.md`) definir como as 17
entradas de tela se agrupam fisicamente.

## Referências

Fontes externas citadas no Contexto e na Decisão, no formato definido
pela norma ABNT NBR 6023 (Informação e documentação — Referências).
Citadas no corpo do documento como (ENTIDADE, ano).

GOOGLE. **App architecture**. Android Developers, [s.d.]a. Disponível
em: https://developer.android.com/topic/architecture. Acesso em: 13
ago. 2026.

GOOGLE. **Guide to Android app modularization**. Android Developers,
[s.d.]b. Disponível em: https://developer.android.com/topic/modularization.
Acesso em: 13 ago. 2026.
