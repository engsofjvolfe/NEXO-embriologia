# 0036 — Ferramenta e fidelidade do protótipo navegável

Resumo em linguagem simples: o passo 4 do método de desenho visual
(`architecture.md#interface`) pede um protótipo clicável, avaliado
contra as boas práticas de usabilidade, antes de qualquer tela virar
código de verdade. Faltava decidir com que ferramenta montar esse
protótipo, e com que nível de detalhe visual — cópia fiel da aparência
já decidida, ou só um rascunho grosseiro. Esta ADR decide: um arquivo
HTML/CSS/JavaScript autocontido (sem servidor, sem conta externa,
sem custo), com a aparência real já decidida (cor, tipografia, forma —
`decisions/0035`) e o leiaute real já decidido (`design/wireframe.md`)
— nunca um rascunho grosseiro, porque a aparência e o leiaute já
passaram das etapas 2 e 3 do método.

Convenção dos códigos citados abaixo:
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.

**Status:** aceito

**Contexto:**

`architecture.md`, seção Interface, já fixa o método de quatro passos
(arquitetura de informação → wireframe → sistema visual → protótipo
navegável e avaliação) e já cita a fonte da avaliação em si (NIELSEN,
1994, dez heurísticas de usabilidade). O que falta, e nenhum documento
decide ainda, é a ferramenta e o nível de detalhe visual do protótipo
em si — o "como montar", não o "contra o que avaliar".

Três alternativas reais consideradas:

1. **Ferramenta de desenho de protótipo com interface gráfica própria**
   (por exemplo Figma) — comum no mercado, mas exige conta externa,
   guarda o trabalho num formato próprio (não é texto simples,
   versionado como o resto deste repositório em Git), e normalmente
   depende de um plano pago pra funcionalidade completa de protótipo
   clicável em equipe.
2. **Componentes reais do Jetpack Compose, com o modo de pré-visualização
   interativa do Android Studio** (`@Preview`) — descartada porque isso
   já é escrever código de verdade em Kotlin, o mesmo código de
   produção que o método (`architecture.md#interface`) explicitamente
   quer deixar pra depois do protótipo ("antes de virar código de
   verdade"). Usar Compose aqui apagaria a fronteira entre as etapas 3
   e 4 do método.
3. **Arquivo HTML/CSS/JavaScript autocontido, sem biblioteca externa,
   sem servidor** (alternativa escolhida, ver Decisão) — abre direto no
   navegador, sem instalação, sem conta, sem custo; texto simples,
   versionável no Git como qualquer outro arquivo deste módulo (mesmo
   já usado em `design/cor-semente-candidatas.html`).

A escolha pela alternativa 3 se apoia em dois pontos, cada um checado
em fonte oficial:

- Sobre qual ferramenta escolher: o Nielsen Norman Group lista cinco
  fatores — "tipo de projeto e objetivos, custo, capacidades da
  ferramenta, facilidade de aprendizado e uso, e aprovação de quem
  decide" (BROWN, [s.d.]). Neste projeto — um só responsável, sem
  orçamento de ferramenta declarado em nenhum documento, preferência já
  registrada por tecnologia aberta e sem custo onde existe alternativa
  equivalente (Projeto Detalhado, DA-IMP-06) — os cinco fatores
  apontam pra uma ferramenta sem custo, sem conta externa, fácil de
  abrir e de versionar.
- Sobre o nível de detalhe visual (fidelidade): a mesma fonte descreve
  quando alta fidelidade é apropriada — "quando... detalhes visuais
  importam" e o comportamento do sistema precisa ser realista pra quem
  avalia reagir de forma real ao protótipo (NIELSEN NORMAN GROUP,
  [s.d.]). Como a aparência (`decisions/0035`) e o leiaute
  (`design/wireframe.md`) já estão decididos — não é mais uma fase de
  ideia solta —, o protótipo precisa refletir esses dois de verdade,
  não um rascunho. A mesma fonte também nota que a avaliação
  heurística em si "pode acontecer em qualquer estágio... de esboços à
  mão até protótipos de alta fidelidade" (NIELSEN NORMAN GROUP,
  [s.d.]) — ou seja, a fidelidade alta aqui não é exigência da
  avaliação heurística em si, é escolha decorrente de a aparência já
  estar pronta, sem motivo pra fingir o contrário.

**Decisão:**

1. O protótipo navegável é um único arquivo HTML autocontido (CSS e
   JavaScript embutidos no próprio arquivo, sem biblioteca externa,
   sem passo de build, sem servidor) — abre direto num navegador comum,
   clicando em qualquer computador ou aparelho, sem instalar nada.
2. Fidelidade alta: cor, tipografia e forma exatas de
   [decisions/0035](<0035-sistema-visual-cor-tipografia-forma-contraste.md>)
   (laranja `#FF6D1F`, azul royal `#2D5FE0`, fonte do sistema, cantos
   arredondados no padrão do Material), e posição de cada elemento
   exata de
   [`design/wireframe.md`](<../design/wireframe.md>) — nunca uma versão
   simplificada ou provisória de nenhum dos dois.
3. Cobre as 17 entradas de tela da tabela DA-RET, incluindo os oito
   estados do `SessionScreen` (`decisions/0022`) com o gatilho de
   toque já decidido em `decisions/0032`, a navegação em acordeão já
   decidida em `decisions/0030`, e o formato de aparelho decidido em
   `decisions/0033` (celular como referência completa; leiaute de
   tablet à parte só na tela de Configuração da sessão).
4. Mora em `design/`, ao lado de `wireframe.md` e
   `cor-semente-candidatas.html` — mesma pasta, mesmo motivo (material
   visual, fora do conjunto fixo de documentos de `docs/`).
5. Fora desta decisão: o conteúdo da avaliação heurística em si (o que
   cada uma das dez heurísticas de Nielsen encontra) — isso é
   `design/avaliacao-heuristica.md`, documento separado, escrito depois
   do protótipo existir.

**Consequências:**

Nenhum código de `core` ou `app` muda — o protótipo não é código de
produção, é material de desenho, like `wireframe.md`. Fecha, junto com
a avaliação heurística, a pendência "Montar o protótipo navegável e
avaliar contra as boas práticas de usabilidade" em `tasks.md`, último
passo do método fixado em `architecture.md#interface`.

Nenhuma fonte paga ou proprietária fica registrada como dependência
deste módulo — mesma preferência por tecnologia aberta já usada em
todo o resto do projeto (JSON, ZIP, Bluetooth, NFC padrão — Projeto
Detalhado, DA-IMP-06), agora estendida, por analogia (não por exigência
literal daquela regra, que é sobre o pacote de conteúdo, não sobre
ferramenta de desenho), à ferramenta usada pra montar este protótipo.

## Referências

Fontes externas consultadas para embasar esta decisão, no formato
definido pela norma ABNT NBR 6023 (Informação e documentação —
Referências). Citações traduzidas livremente no corpo do documento;
texto original preservado entre aspas antes da tradução quando citado
diretamente. Citadas no corpo do documento como (ENTIDADE, ano) ou
(SOBRENOME, ano).

BROWN, Megan. **UX Prototyping: 5 Factors for Selecting the Right
Tool** (vídeo). Nielsen Norman Group, [s.d.]. Disponível em:
https://www.nngroup.com/videos/prototyping-tool/. Acesso em: 30 ago.
2026.

NIELSEN NORMAN GROUP. **UX Prototypes: Low Fidelity vs. High
Fidelity**. [S.l.], [s.d.]. Disponível em:
https://www.nngroup.com/articles/ux-prototype-hi-lo-fidelity/. Acesso
em: 30 ago. 2026.
