# Módulos — NEXO

Índice dos módulos de código do projeto, o fluxo de trabalho que
qualquer um deles segue, e a convenção de escrita usada em todo
documento de módulo. Este arquivo é a fonte única desses três
assuntos — nenhum módulo redefine sua própria versão de nenhum dos
três.

## Índice
- [Módulos](#módulos)
- [Como navegar](#como-navegar)
- [Como escrever](#como-escrever)

## Módulos

| Nome | Pasta | O que é |
|---|---|---|
| Motor | [`motor/`](motor/) | O mecanismo genérico do NEXO — simulador tátil de sequências, agnóstico a qualquer disciplina. Implementa o que a cascata de documentação em [`docs/docs-VMODEL-visao-geral/`](../docs/docs-VMODEL-visao-geral/) já decidiu (conceito, requisitos, especificação, projeto arquitetônico, projeto detalhado — todos versão 1.0.0, aprovados). |

`_template/` não é um módulo — é o molde a partir do qual todo módulo
novo nasce (ver [Como navegar](#como-navegar)). `preview/` guarda a
documentação do ambiente de teste isolado, não um módulo de produto.

## Como navegar

Todo módulo nasce copiando `_template/` (`docs/`, `decisions/`,
`schemas/`) e segue, sempre, a mesma ordem de leitura e de escrita —
nunca a ordem inversa:

1. **`docs/concept.md`** — o que o módulo deve ser e como deve se
   comportar. Sempre o primeiro documento, com código já existente ou
   não. Quando o módulo já tem uma cascata própria de documentação
   fora de `modulos/` (caso do Motor, cuja cascata completa mora em
   `docs/docs-VMODEL-visao-geral/`), `concept.md` não repete esse
   conteúdo — aponta pra ele como fonte normativa.
2. **`docs/architecture.md`** — como construir, a partir do que
   `concept.md` já decidiu: layout de arquivos, pacotes, fronteiras.
   Nunca escrito a partir de código já existente.
3. **`schemas/`** — gerado do bloco YAML dentro de `concept.md`, para
   todo módulo que tenha uma fronteira de dado real. Nunca escrito à
   mão em paralelo ao YAML.
4. **Implementação** — o código em si, derivado de `architecture.md` e
   de `schemas/`, nunca o contrário. Se a implementação revela algo
   que o desenho não previu, isso vira entrada em `docs/findings.md`
   (achado) ou `docs/pitfalls.md` (armadilha de ferramenta), nunca uma
   reescrita silenciosa de `concept.md` ou `architecture.md` pra bater
   com o código.

Por que essa ordem, e não a inversa (escrever código e documentar
depois): documentar a partir do código já escrito registra o que o
código faz, não o que ele deveria fazer — qualquer erro de desenho
vira permanente, porque a documentação só concorda com ele. Descer
sempre de `concept.md` pra baixo mantém o documento como fonte da
verdade; o código é quem tem que bater com o documento, não o
contrário. Divergência encontrada (código não bate com o que
`concept.md`/`architecture.md` já decidiu) sempre vira pendência em
`docs/tasks.md`, resolvida corrigindo o código — nunca reescrevendo o
documento pra bater com o código.

`docs/analysis.md` e `docs/findings.md` entram em qualquer ponto desse
fluxo em que existir código pra checar contra o desenho — não é uma
etapa fixa numerada, é um ciclo que se repete sempre que há algo novo
pra verificar. `decisions/` (ADR) nasce em qualquer um dos quatro
passos acima, sempre que aparece uma escolha real entre alternativas
que precisa ficar registrada com o contexto que a motivou —
`docs/tasks.md` nunca fecha uma pendência que envolveu essa escolha
sem apontar pra a ADR correspondente. `docs/handoff.md` é sempre a
última coisa atualizada dentro do módulo, depois de qualquer uma das
outras mudar.

## Como escrever

Regra de formato única, usada em todo documento de todo módulo (e,
onde cabível, nos documentos gerais da raiz também):

- **Resumo simples primeiro, detalhe técnico depois.** Toda seção ou
  entrada começa com uma ou duas frases em linguagem comum — o que é,
  sem jargão — antes de qualquer detalhe técnico. Quem só precisa
  saber "o que é isso" para na primeira parte; quem precisa construir
  ou revisar continua para a segunda.
- **Tom impessoal.** Nunca "o proprietário pediu X" ou "decidimos Y" —
  sempre o fato observado, testado ou decidido, direto. Única exceção:
  `docs/analysis.md`, onde narrar o processo de investigação (o que
  foi lido, checado, o raciocínio) é o próprio propósito do arquivo.
- **Entrada datada, nunca reescrita.** Em `docs/analysis.md`,
  `docs/findings.md` e `docs/pitfalls.md`, cada entrada leva uma âncora
  no formato `AAAA-MM-DD-título-curto` e nunca é editada depois de
  escrita — se algo deixa de valer, ganha uma entrada nova, datada,
  dizendo o que mudou, nunca uma edição por cima da antiga. Mesma
  lógica em `decisions/`: uma ADR aceita não é reescrita pra revisar a
  decisão em si — decisão que muda gera uma ADR nova, que substitui a
  antiga (a antiga marcada como substituída, nunca apagada).
- **Campos fixos por tipo de arquivo:** `docs/analysis.md` leva
  `Levou a` (link pro achado gerado, ou `ainda sem conclusão`);
  `docs/findings.md` leva `Confirmado por` (`leitura de código` |
  `teste ao vivo` | `leitura de código e teste ao vivo`); toda ADR em
  `decisions/` leva um resumo em linguagem simples (porta de entrada,
  não conta como um dos campos fixos) seguido de `Status`, `Contexto`,
  `Decisão`, `Consequências`, nessa ordem.
- **`docs/handoff.md` só aponta.** Link markdown de verdade mais uma
  frase curta — nunca uma descrição do que o conteúdo diz. Arquivo que
  só existe uma vez por módulo (`concept.md`, `architecture.md`,
  `tasks.md`, `findings.md`, `pitfalls.md`, `analysis.md`) leva frase
  genérica sobre o papel do arquivo, nunca o assunto específico de
  dentro dele. Link pra dentro de `decisions/` é a exceção: nomear a
  decisão específica ali é a única forma de diferenciar uma ADR da
  outra na lista.
- **Pendência resolvida nunca é apagada.** Em `docs/tasks.md`, todo
  item riscado (`- [x]`) continua na seção "Resolvidas", apontando pro
  achado ou pra ADR que resolveu ele — apagar destruiria o histórico de
  por que uma decisão foi tomada.
- **Esquema de dado é dado puro.** Qualquer esquema (`schemas/*.json`,
  ou um bloco YAML equivalente dentro de `concept.md`) carrega só a
  estrutura em si — sem campo de narrativa, sem exemplo de uso, sem
  explicação de por quê. Esse tipo de contexto mora no texto ao redor
  do esquema, nunca dentro dele.
- **Código citado de outra fonte, numa nota única, direto pro lugar
  certo.** Se um documento cita códigos de identificação de outra
  fonte (ex.: um ID de requisito ou de decisão de outro documento),
  a explicação de origem mora numa nota única, perto do início do
  documento — nunca espalhada, nunca repetida a cada menção no corpo
  do texto. Só entra ali o padrão de código realmente citado *naquele
  documento específico* (não todo padrão que existe na fonte) — uma
  linha por padrão, direto pro documento e a seção exata onde o
  conteúdo dele está (ex.: `PD-IMP` — documento 5, seção 6.3). Nunca
  decompor em sistemas abstratos separados (o que o prefixo significa
  de um lado, o que a sigla de categoria significa do outro) — isso
  obriga quem lê a recombinar duas tabelas de cabeça só pra achar uma
  coisa. Uma linha, indo direto pro lugar certo, resolve.
