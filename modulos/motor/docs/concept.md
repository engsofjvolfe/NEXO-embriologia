# Concept — Motor

<!-- module-doc-type: concept -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Concept |
| Versão | 0.8.0 |
| Data | 31-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Descreve o desenho pretendido do módulo — o que ele deve ser e como
> deve se comportar. É sempre o primeiro passo do módulo, com código
> já existente ou não -- guia o resto (`architecture.md`, `schemas/`,
> implementação).
>
> Se o módulo já tem código, `analysis.md`/`findings.md` checam, a
> qualquer momento, se esse código bate com o requisito que este
> arquivo descreve -- nunca o contrário: descobrir o que o código já
> faz não muda o que este arquivo diz que deveria fazer. Divergência
> vira pendência em `tasks.md`, resolvida corrigindo o código, nunca
> reescrevendo este arquivo pra bater com ele.
>
> Quando o módulo tem contrato de dado, carrega um bloco YAML puro
> descrevendo esse contrato — fonte única da qual `schemas/*.json` é
> gerado (nunca escrito à mão em paralelo).
>
> Não é um relato de investigação (isso é `analysis.md`) nem uma lista
> de achados (isso é `findings.md`). Edita-se
> por cima pra ajustes pequenos ao mesmo desenho; se o conceito inteiro
> for repensado, este arquivo é substituído por um novo (a troca em si
> vira um registro em `decisions/`), não silenciosamente reescrito por
> cima do que havia antes.
>
> Cada seção segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

## Índice
- [Convenção dos códigos](#convenção-dos-códigos)
- [Escopo](#escopo)
- [Fluxo](#fluxo)
- [Contrato de dado](#contrato-de-dado)
- [Controle de versão](#controle-de-versão)

## Convenção dos códigos

- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

## Escopo

*Em resumo:* este módulo é o "motor" do NEXO — o mecanismo genérico
que roda o simulador tátil de sequências, sem conhecer nenhum assunto
específico. Na prática, é o que quem joga usa de verdade: um
aplicativo Android, e um acessório físico opcional pra ler as peças.

*Em detalhe técnico:* o que este módulo deve ser e como deve se
comportar já está inteiramente decidido na cascata de documentos em
[`docs/docs-VMODEL-visao-geral/`](../../../docs/docs-VMODEL-visao-geral/)
— conceito, requisitos, especificação, projeto arquitetônico e projeto
detalhado, todos na versão 1.0.0, aprovados. Este `concept.md` não
repete esse conteúdo — aponta pra ele como fonte normativa direta, e
existe só pra dar a este módulo o ponto de entrada que todo módulo tem
dentro de `modulos/` (ver
[`modulos/README.md`, Como navegar](../../README.md#como-navegar)).

Dentro do escopo: tudo que o Projeto Detalhado
([`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>))
já decidiu — leitura e identificação de peça por NFC, conectividade
por Bluetooth de baixo consumo entre acessório e aplicativo,
importação de conteúdo (esquema do pacote, leitura do arquivo
compactado), navegação com busca aproximada — mais o fluxo funcional
das telas já decidido no Projeto Arquitetônico
([`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
seção 6.6). Também dentro do escopo: a aparência visual das telas em
si (cor, fonte, layout, e como as 17 entradas de tela do Projeto
Arquitetônico se agrupam em telas físicas) — uma única casca,
compartilhada e reaproveitada por toda instância, nunca desenhada de
novo a cada uma. Essa camada mora dentro deste módulo, como uma seção
própria em `architecture.md` (ver [Interface](architecture.md#interface))
— não vira módulo separado. Motivo completo, alternativas descartadas
e adiadas:
[decisions/0029](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>).
O valor exato da aparência (cor, fonte, posição de cada elemento) já
está decidido — ver [decisions/0035](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>)
(sistema visual) e [`design/wireframe.md`](<../design/wireframe.md>)
(posição de cada elemento). O passo final do método — protótipo
clicável e avaliação contra as boas práticas de usabilidade — também já
está pronto, ver [decisions/0036](<../decisions/0036-ferramenta-e-fidelidade-do-prototipo-navegavel.md>)
e [`design/avaliacao-heuristica.md`](<../design/avaliacao-heuristica.md>).
O agrupamento das 17 entradas em telas físicas já está parcialmente
resolvido: as 10 que são variações de conteúdo dentro da sessão em jogo
formam uma tela só (`SessionScreen`, tipo fechado — ver
[decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>));
só as outras 7 (páginas de navegação de fato) seguem sem agrupamento
físico decidido.

Fora do escopo: o texto legal do termo de consentimento
(explicitamente fora da cascata do motor, ver Projeto Detalhado §2.2),
e qualquer conteúdo específico de uma instância (nome de tema, evento,
imagem, texto — isso é trabalho de quem monta cada aplicação do motor,
nunca deste módulo).

## Fluxo

*Em resumo:* como uma peça é lida, como uma tentativa é validada, o
que a tela mostra em cada momento, como uma sessão começa, pausa e
termina — todo esse comportamento já está descrito por inteiro no
documento de Conceito e refinado, passo a passo, nos quatro documentos
seguintes da mesma cascata.

*Em detalhe técnico:* ponto de entrada pra cada assunto, dentro da
cascata já aprovada:

| Assunto | Onde está decidido |
|---|---|
| Filosofia, hierarquia de conteúdo, mecânica de peça e tentativa | [`1 - documento-de-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>), seções 1 a 15 |
| Requisitos funcionais e não funcionais derivados dessa mecânica | [`2 - requisitos-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/2 - requisitos-conceito-geral.md>) |
| Regra de comportamento concreta por trás de cada requisito | [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>) |
| Componentes físicos e lógicos, conectividade, armazenamento | [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>) |
| Quais telas existem e o que cada uma mostra | [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6 |
| Aparência visual — cor, fonte, layout — já decidida (nunca fixada nesta cascata; é decisão do módulo, ver acima) | [decisions/0035](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>) e [`design/wireframe.md`](<../design/wireframe.md>) |
| Peça exata de cada componente (chip, serviço Bluetooth, esquema de arquivo, algoritmo de busca) | [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>) |

Qualquer mudança de comportamento do motor começa por essa cascata, não
por este arquivo — `concept.md` só existe pra apontar pra ela.
Divergência entre o código deste módulo (quando existir) e o que a
cascata decidiu vira achado em `findings.md`, nunca motivo pra
reescrever a cascata.

## Contrato de dado

*Em resumo:* o motor troca dados com o mundo fora dele por um único
ponto — o pacote de conteúdo, o arquivo que reúne toda uma instância
(temas, eventos, fotogramas, textos, e a ligação entre cada etiqueta
física e o fotograma que ela representa). O contrato abaixo é a forma
exata desse arquivo.

*Em detalhe técnico:* mesma decisão já tomada no
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>)
(PD-IMP-01), reproduzida aqui no formato que este projeto usa pra
gerar `schemas/`. Fonte única — qualquer mudança de estrutura de dado
começa aqui, nunca direto em `schemas/pacote-de-conteudo.schema.json`.

O campo `frame`, abaixo, é o fotograma descrito no
[documento de Conceito](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
seção 4: um retrato de um momento específico de um processo, entregue
fora de ordem pra quem joga reconstituir a sequência. O nome do campo
está em inglês só porque o
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
seção 6.3.1 (PD-IMP-01), já decidiu usar nomes de campo em inglês
neste arquivo específico — o conceito, em português, continua sendo
sempre "fotograma".

Dois campos de texto do fotograma têm papel diferente, mesmo os dois
escritos por quem monta o conteúdo: `confirmation_text` (opcional)
aparece sozinho, na hora em que a pessoa acerta aquela peça específica
— pode ficar de fora, sem problema. `summary_fragment` (obrigatório)
nunca aparece sozinho — só entra, já combinado com o de peças
vizinhas, na síntese de fim de sessão sem pulo; por isso é sempre
exigido, mesmo quando `confirmation_text` não é. Motivo completo:
[decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>).

O valor de `schema_version` é `2.0.0`. O campo novo `summary_fragment`
(ver abaixo) é obrigatório, então um pacote de conteúdo válido na
versão anterior deixa de ser válido sem alteração — mudança que quebra
compatibilidade, por isso o número maior sobe, não só o do meio
(convenção SemVer, já usada em todo o projeto — ver
[modulos/README.md](../../README.md)). Esse campo extrapola o que o
Projeto Detalhado (PD-IMP-01, aprovado, imutável) fixa — legítimo
porque este `concept.md`, não aquele documento, é quem evolui o
contrato de dado (ver nota acima, "Fonte única"); PD-IMP-01 continua
correto para a versão que ele descreve. Motivo completo da mudança,
com as alternativas descartadas antes de chegar nela:
[decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>).

```yaml
schema_version: "2.0.0"

instance:
  type: object
  required: [name, retention_period, themes]
  properties:
    name:
      type: string
      minLength: 1
    retention_period:
      type: string
      pattern: '^P(?!$)(\d+Y)?(\d+M)?(\d+D)?$'
    themes:
      type: array
      minItems: 1
      items: { $ref: theme }

theme:
  type: object
  required: [name, ordering, events]
  properties:
    name:
      type: string
      minLength: 1
    ordering:
      type: string
      enum: [ordered, standalone]
    position:
      type: integer
      minimum: 1
    events:
      type: array
      minItems: 1
      items: { $ref: event }
  conditional:
    - if: { ordering: ordered }
      then: { required: [position] }
    - if: { ordering: standalone }
      then: { forbidden: [position] }

event:
  type: object
  required: [name, ordering, zero_mark, hint_enabled, frames]
  properties:
    name:
      type: string
      minLength: 1
    ordering:
      type: string
      enum: [ordered, standalone]
    position:
      type: integer
      minimum: 1
    zero_mark:
      type: object
      required: [image]
      properties:
        image: { type: string, minLength: 1 }
    hint_enabled:
      type: boolean
    hint_content:
      type: string
      minLength: 1
    frames:
      type: array
      minItems: 1
      items: { $ref: frame }
  conditional:
    - if: { ordering: ordered }
      then: { required: [position] }
    - if: { ordering: standalone }
      then: { forbidden: [position] }
    - if: { hint_enabled: true }
      then: { required: [hint_content] }
    - if: { hint_enabled: false }
      then: { forbidden: [hint_content] }

frame:
  type: object
  required: [tag_id, image, summary_fragment]
  properties:
    tag_id:
      type: string
      pattern: '^[0-9A-F]+$'
    image:
      type: string
      minLength: 1
    confirmation_text:
      type: string
    summary_fragment:
      type: string
      minLength: 1
```

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial: escopo, fluxo e contrato de dado do pacote de conteúdo. | Criação inicial |
| 0.2.0 | 14-08-2026 | `schema_version` do contrato de dado sobe de `0.1.0` para `1.0.0`, cumprindo a condição já registrada na versão anterior deste documento. | Escrita do pacote `content`, primeiro código a validar pacotes de conteúdo reais contra este contrato |
| 0.3.0 | 15-08-2026 | `schema_version` do contrato de dado sobe de `1.0.0` para `2.0.0`: campo novo `summary_fragment`, obrigatório, em `frame` — mudança que quebra compatibilidade com pacotes de conteúdo já válidos na versão anterior. | Resolução de [decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>) |
| 0.4.0 | 18-08-2026 | Escopo corrigido: a aparência visual das telas passa de "fora do escopo" para "dentro do escopo" deste módulo — casca única, compartilhada por toda instância, nunca desenhada por instância nem em módulo separado; motivo completo em [decisions/0029](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>). | Resolução de [decisions/0029](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>) |
| 0.6.0 | 22-08-2026 | Precisado o agrupamento das 17 entradas de tela: 10 delas (variações de conteúdo dentro da sessão em jogo) já têm o agrupamento em telas físicas fechado desde [decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>); só as outras 7 seguem sem essa decisão. | Revisão da pendência de desenho visual, na mesma sessão de [decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>) |
| 0.7.0 | 30-08-2026 | Corrigidas duas frases desatualizadas: a aparência visual (cor, fonte, layout) não estava mais "sem decisão" desde [decisions/0035](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>) e `design/wireframe.md`, mas o texto ainda dizia isso, em dois lugares (seção Escopo e tabela da seção Fluxo). | Auditoria de leitura completa antes da tarefa do protótipo navegável |
| 0.8.0 | 31-08-2026 | Corrigida frase que ainda dizia "o passo final do método... segue como pendência" — o protótipo navegável e a avaliação heurística, resolvidos na mesma tarefa que escreveu a versão anterior deste documento, nunca tinham sido refletidos aqui. | Achado na revisão de PR (revisor-visao-de-conjunto) |
