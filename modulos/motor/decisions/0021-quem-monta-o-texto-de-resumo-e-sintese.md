# 0021 — Quem monta o texto de resumo e síntese exibido nas telas

Resumo em linguagem simples: as telas de fim de evento (síntese, sem
peça pulada), mensagem de pulo, e síntese de cadeia precisam de um
texto pronto — e nenhuma parte do código, hoje, sabe montar esse
texto. Esta ADR decide quem monta cada um dos três, e, pro caso mais
difícil (a síntese sem pulo), muda o contrato de dado que quem monta o
conteúdo precisa preencher: um texto novo, escrito peça por peça,
pensado desde o início pra se encaixar com os vizinhos.

Convenção dos códigos citados abaixo:
- `EI-RET` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.7.
- `EI-PUL` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.6.
- `EI-ENC` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.11.
- `EI-SES` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.10.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Status:** aceito

**Contexto:**

A pendência já estava registrada em `tasks.md` desde o desenho do
pacote `session`: `session` só guarda fatos (o quê, quando, em que
posição — nunca texto); `content` só guarda os textos que quem monta
o conteúdo escreveu; nenhum dos dois se conhece, de propósito
(RNF-MOD-01). Alguém, na camada de `app` (o `ViewModel` já decidido em
[decisions/0020](0020-ligacao-entre-leitura-de-peca-e-a-tela.md)),
precisa pegar dado dos dois e montar o texto final — mas "montar" quer
dizer coisas bem diferentes em cada um dos três casos:

1. **Mensagem de pulo (EI-PUL-05)** — a Especificação usa a palavra
   "**lista**", não "síntese": lista o que foi respondido, aponta o
   intervalo sem resposta (só a posição, nunca o conteúdo daquela
   posição — a proibição de revelar isso é o próprio motor de
   aprendizagem do sistema, Documento de Conceito, seções 1 e 13, não
   um detalhe técnico a contornar), e uma sugestão fixa de estudo. Não
   precisa de nenhum texto novo: usa o `confirmation_text` (já
   existente) das posições respondidas, e o número da posição das
   puladas. Totalmente mecânico.

2. **Síntese de cadeia, com pulo em algum evento (EI-ENC-03, segundo
   caso)** — também mecânico: só a contagem total de posições
   preenchidas e perdidas na cadeia inteira, sem texto nenhum.

3. **Síntese sem nenhuma peça pulada (EI-RET-04, e EI-ENC-03 primeiro
   caso, pra cadeia)** — aqui é onde a discussão pesou: a Especificação
   pede uma "**síntese**", "**narrativa contínua**" — não uma lista.
   Duas alternativas foram descartadas antes de chegar na decisão
   final:
   - Reaproveitar o `confirmation_text` de cada peça, só juntando em
     ordem: descartada — isso é reexibir o que já foi mostrado durante
     o jogo, não uma síntese; o `confirmation_text` foi escrito pra
     aparecer sozinho, peça a peça, não pra se encaixar num texto
     maior.
   - Um texto de síntese único, pronto, escrito por evento inteiro:
     descartada — a sessão pode começar no meio de um evento
     (EI-SES-02: a pessoa escolhe o ponto de início) e uma síntese de
     cadeia cobre vários eventos seguidos (EI-ENC-03) — um texto fixo
     por evento inteiro não se ajusta a "só a partir da posição 3", nem
     a "esses dois eventos, um atrás do outro" — escrever um texto
     pronto pra cada combinação possível de ponto de início e
     encadeamento não é viável (o número de combinações cresce demais
     conforme o conteúdo cresce).
   - Gerar o texto por algum mecanismo automático (inteligência
     artificial): fora de cogitação nesta ADR — depende de conexão à
     internet, o que contraria DA-CON-01 ("nenhuma etapa de uma sessão
     local depende de conexão à internet: leitura, validação, dica,
     **resumo, síntese**..."), do Projeto Arquitetônico, aprovado e
     imutável. Mudar essa exigência não é uma decisão deste documento —
     exigiria reabrir a cascata aprovada, fora do escopo desta rodada
     de trabalho.

**Decisão:**

1. **Pacote novo em `core`, `core/summary/`**, no mesmo espírito
   desacoplado do pacote `search` — recebe dado pronto de fora (o
   registro de `session`, os textos de `content`) e devolve o
   resultado pronto pra tela usar, sem conhecer nenhum dos dois
   pacotes por dentro. Implementa os três casos:
   - Mensagem de pulo (EI-PUL-05): devolve **dado organizado, não um
     texto único já pronto** — uma lista de itens (posição, respondida
     ou não, e o `confirmation_text` quando respondida) mais o
     intervalo de posições sem resposta. A tela decide, quando for
     desenhada (pendência separada em `tasks.md`), como filtrar, rolar
     ou paginar essa lista — este pacote só entrega o dado, organizado,
     nunca decide aparência.
   - Síntese de cadeia com pulo (EI-ENC-03, caso com pulo): só conta
     posições preenchidas/perdidas.
   - Síntese sem pulo, de evento ou de cadeia (EI-RET-04, EI-ENC-03
     sem pulo): aqui sim um texto único, contínuo — concatena, na
     ordem em que as posições foram realmente jogadas, do ponto real
     de início até o fim, atravessando quantos eventos precisar, o
     `summary_fragment` (ver item 2) de cada uma, nunca o
     `confirmation_text`.

2. **Novo campo no contrato de dado do fotograma (`frame`):
   `summary_fragment`, texto, obrigatório** — diferente do
   `confirmation_text` (que continua opcional, e continua existindo
   só pra aparecer sozinho, na hora do acerto). Escrito por quem monta
   o conteúdo, especificamente pensado pra se encaixar com o fragmento
   da peça vizinha, não pra ler sozinho. É obrigatório, ao contrário
   do `confirmation_text`, porque a síntese sem pulo é sempre exigida
   quando não há pulo (EI-RET-04) — se o fragmento faltasse em
   qualquer peça, a síntese ficaria com buraco justamente no caso em
   que não deveria ter buraco nenhum.

   Essa mudança extrapola o esquema que o Projeto Detalhado já fixou
   (PD-IMP-01) — mas o próprio `concept.md` deste módulo já registra
   que ele é a fonte que evolui esse contrato daqui pra frente
   ("qualquer mudança de estrutura de dado começa aqui, nunca direto
   em `schemas/`"), não uma cópia congelada de PD-IMP-01. O documento
   aprovado (PD-IMP-01) continua imutável e continua correto para a
   versão que ele descreve (`1.0.0`) — esta mudança sobe a versão do
   contrato em `concept.md`, sem reescrever nada do que já foi
   aprovado na cascata.

3. **Nenhuma regra de qualidade de texto é imposta pelo motor** — a
   mesma postura já registrada no Documento de Conceito, seção 1
   ("a granularidade de qualquer parte do conteúdo (...) é uma decisão
   de quem monta o conteúdo, não uma verdade a ser descoberta"): o
   motor não valida se um `summary_fragment` "flui bem" com o vizinho,
   só que ele existe. Qualidade de encaixe é responsabilidade de quem
   escreve, orientada por instrução clara de autoria (ainda não
   escrita — mesma pendência já registrada em `tasks.md` sobre uma
   ferramenta de autoria amigável).

**Consequências:**

O esquema do pacote de conteúdo (`concept.md`, bloco YAML, e
`schemas/pacote-de-conteudo.schema.json` gerado dele) ganha o campo
`summary_fragment` em `frame`, obrigatório — qualquer pacote de
conteúdo existente hoje (nenhum real ainda foi publicado além do que
`content` usa em teste) precisaria ser atualizado pra incluir esse
campo em cada fotograma. O pacote `content` (já escrito) precisa de
ajuste pra validar esse campo novo como obrigatório — pendência a
registrar em `tasks.md`. `core/summary/` é testável isoladamente, sem
depender de `session`, `content` nem de nenhuma classe do Android,
mesmo padrão de `search`.

## Referências

Nenhuma fonte externa nova citada nesta ADR — a decisão se apoia
inteiramente em documentos já aprovados deste projeto:

- [Documento de Conceito](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>), seções 1 e 13.
- [Especificação](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), EI-RET-04, EI-PUL-05, EI-ENC-03, EI-SES-02.
- [Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), DA-CON-01.
- [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), PD-IMP-01.
- [concept.md](<../docs/concept.md>) deste módulo.
