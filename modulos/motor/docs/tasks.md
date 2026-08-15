# Tasks — Motor

<!-- module-doc-type: tasks -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Tasks |
| Versão | 0.14.0 |
| Data | 14-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Lista mutável de pendências só deste módulo. Lida depois de
> `concept.md`/`architecture.md`, antes de mexer em qualquer coisa.
> Atualizada direto conforme resolve. Assim que uma pendência vira
> decisão de verdade, o item aqui vira só um ponteiro pra ADR em
> `decisions/` — nunca um resumo paralelo do que a decisão já diz.
> Pendência resolvida (com ou sem ADR) não é apagada — vira item
> riscado na seção `Resolvidas`.
>
> Antes de marcar uma pendência como resolvida: ela envolveu escolher
> entre pelo menos duas alternativas reais? Se sim, a ADR vem
> primeiro (`decisions/000N-titulo.md`) e o item aqui vira só
> ponteiro pra ela — nunca o parágrafo que já explica a escolha
> ficando só aqui, sem ADR nenhuma.
>
> Cada item segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

Convenção dos códigos citados aqui:
- `DA-LEI` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.1.
- `DA-ARM` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.3.
- `DA-REG` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.7.
- `PD-LEI` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.1.
- `PD-CON` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.2.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.
- `PD-NAV` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.4.

## Índice
- [Em aberto](#em-aberto)
- [Resolvidas](#resolvidas)
- [Referências](#referências)
- [Controle de versão](#controle-de-versão)

## Em aberto

- [ ] **Avaliar importação parcial de conteúdo (só um tema ou evento
      novo, sem reimportar a instância inteira).**

      *Resumo simples:* hoje só existe um jeito de trazer conteúdo pro
      aplicativo: importar a instância inteira, com todos os temas e
      eventos dentro do mesmo arquivo. Não existe (nem está desenhado)
      um jeito de acrescentar só um tema novo ou só um evento novo a
      uma instância que a pessoa já tem instalada.

      *Detalhe técnico:* essa limitação vem do próprio esquema já
      aprovado ([Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
      PD-IMP-01) — o arquivo sempre é um único bloco `instance`, sem
      forma alternativa pra descrever só um tema ou evento avulso.
      Mudar isso exigiria alterar o esquema em si, não é ajuste de
      implementação — pendência registrada só como possibilidade a
      avaliar depois, sem desenho, sem decisão, sem responsável ainda.
      Levantada durante a escrita do pacote `content`
      ([decisions/0013](<../decisions/0013-desenho-do-pacote-content.md>)),
      que hoje só sabe importar instância completa.

- [ ] **Escrever o código-fonte do lado `app` do pacote `connectivity`
      (`Service` de Bluetooth, leitura NFC na `Activity` de entrada).**

      *Resumo simples:* ler o identificador de uma peça física, direto
      pela antena do aparelho ou repassado pelo acessório externo por
      Bluetooth. A metade que só faz conta (sem tocar hardware) já foi
      escrita e testada; falta a metade que liga o rádio de verdade.

      *Detalhe técnico:* o lado `core/connectivity/` (contrato: UUIDs
      do Nordic UART Service, decodificação de identificador físico)
      já está escrito e testado — ver
      [architecture.md, pacote `connectivity`](<architecture.md#pacote-connectivity--desenho-interno>),
      [decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>)
      e
      [decisions/0016](<../decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md>).
      Falta o lado `app`: `connectivity/BleAccessoryService.kt` (cliente
      GATT de verdade, PD-CON-01 a PD-CON-04) e o código de leitura NFC
      direta na `Activity` de entrada já existente (`enableReaderMode`,
      DA-LEI-04) — os dois já com desenho fechado, sem pendência de
      decisão; falta só escrever. Nenhum dos dois espera nada: nem
      desenho visual de tela (a `Activity` de entrada já existe), nem o
      firmware do acessório (o formato do dado já está decidido em
      decisions/0016).

- [ ] **Escrever o código-fonte do pacote `report` (núcleo do
      aplicativo).**

      *Resumo simples:* guardar o histórico de cada sessão jogada e
      gerar o relatório em CSV e PDF.

      *Detalhe técnico:* pacote `core/report/`, ver
      [architecture.md, layout do aparelho de jogo](<architecture.md#aparelho-de-jogo-aplicativo>).
      Guarda local de configuração, registro e relatório, sem servidor
      central: DA-ARM-01. Exportação em CSV e PDF: DA-REG-01.

- [ ] **Escrever o firmware do acessório leitor.**

      *Resumo simples:* o programa que roda no acessório físico
      opcional — lê a etiqueta da peça e avisa o aplicativo, sem
      guardar conteúdo nem rodar lógica de jogo.

      *Detalhe técnico:* C++/Arduino via PlatformIO, ver
      [decisions/0002](<../decisions/0002-framework-do-firmware-do-acessorio.md>).
      Hardware já fixado: módulo leitor NXP PN532/C1 (PD-LEI-01) e
      microcontrolador Espressif ESP32-D0WD-V3 (PD-LEI-02), ligados por
      I2C (PD-LEI-03). Papel de servidor GATT (peripheral), anunciando
      o Nordic UART Service (PD-CON-01, PD-CON-03); a cada leitura
      bem-sucedida, notifica o identificador da etiqueta na
      característica TX (PD-CON-02, PD-CON-03). Ver
      [architecture.md, acessório leitor (firmware)](<architecture.md#acessório-leitor-firmware>).

- [ ] **Substituir o módulo leitor NFC do acessório (PN532/C1 →
      PN7160), já marcado pelo fabricante como não recomendado pra
      projeto novo.**

      *Resumo simples:* o chip de leitura NFC escolhido pro acessório
      físico (PD-LEI-01) foi marcado pela própria NXP como "não
      recomendado pra projeto novo" (NRND — Not Recommended for New
      Designs) — a peça ainda existe à venda, mas o fabricante já
      aponta outro chip, o PN7160, como substituto oficial.

      *Detalhe técnico:* PD-LEI-01, do
      [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>)
      (já aprovado, imutável — divergência vira pendência aqui, nunca
      reescrita naquele documento), fixa o NXP PN532/C1. Não afeta
      nenhum trabalho já feito ou em andamento no pacote `connectivity`
      até agora: o aplicativo fala só com o Bluetooth do acessório
      (Nordic UART Service, PD-CON-01 a PD-CON-04), nunca diretamente
      com o chip leitor — a troca de chip só afeta o firmware do
      acessório (pendência acima, ainda não iniciada) e, possivelmente,
      a ligação elétrica já fixada em PD-LEI-03 (I2C entre o leitor e o
      microcontrolador) — falta confirmar se o PN7160 mantém a mesma
      interface. Sem decisão tomada ainda sobre como fazer essa troca;
      registrado aqui só pra não perder o alerta antes que o firmware
      comece a ser escrito.

- [ ] **Desenhar a aparência visual das telas do motor.**

      *Resumo simples:* o fluxo funcional de cada tela já está
      decidido (quais existem, o que cada uma mostra — ver
      [Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
      seção 6.6), mas a aparência (cor, fonte, layout) nunca entrou em
      nenhum documento da cascata, de propósito.

      *Detalhe técnico:* pendência movida de `TASKS.md` da raiz pra
      cá, porque agora existe um módulo de verdade a que ela pertence
      — antes disso, era a única exclusão de escopo da cascata do
      motor sem nenhum documento apontando pra onde ela deveria ser
      resolvida. Já decidido: essa camada mora dentro do módulo motor,
      como uma seção própria em `architecture.md`
      ("[Interface](architecture.md#interface)"), separada do "núcleo
      do motor" — não vira módulo separado, porque a única coisa que
      varia de fato entre instâncias é o conteúdo (fotogramas, textos),
      não a aparência das telas; existindo só uma aparência
      compartilhada, não há fronteira real que justifique separar.
      Direção provável pra essa aparência, ainda não pesquisada nem
      decidida de verdade: uma casca única, neutra, no padrão Material
      Design do Google — decisão de fato (mockup, o que for necessário)
      fica pra quando esse trabalho começar. Das 17 entradas de tela, 7
      são páginas de navegação de fato (sessão pausada, navegação,
      ponto de início, configuração da sessão, resultado/relatório,
      importar conteúdo, consentimento) e 10 são estados/variações de
      conteúdo dentro da tela principal de jogo (referência, aguardando
      tentativa, confirmação de acerto, negativa, dica, sugestão de
      estudo, resumo de evento, mensagem de pulo, síntese de cadeia,
      confirmação de saída) — como agrupar esses estados em telas
      físicas é parte do próprio desenho visual pendente. Sem
      responsável definido ainda (designer, ou o próprio usuário).

- [ ] **Escrever os testes de unidade, integração, sistema e
      aceitação.**

      *Resumo simples:* nenhum teste existe ainda — só faz sentido
      escrever depois que o código acima existir.

      *Detalhe técnico:* segue a cascata ascendente descrita em
      `docs/prompt model.txt`: teste de unidade valida o
      [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
      integração valida o
      [Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
      sistema valida a
      [Especificação](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>),
      aceitação valida os
      [Requisitos](<../../../docs/docs-VMODEL-visao-geral/2 - requisitos-conceito-geral.md>)
      e o
      [Conceito](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>).

- [ ] **Empacotar o instalável final e decidir sobre canal de
      distribuição.**

      *Resumo simples:* falta gerar o arquivo instalável assinado do
      aplicativo e, se o motor crescer além de um piloto pequeno,
      decidir se vale registrar uma conta de desenvolvedor verificada.

      *Detalhe técnico:*
      [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
      PD-IMP-07 — marcado `[REVISAR-EXTERNO]`: antes de agir (comprar
      conta, escolher
      canal), reconfirmar na fonte oficial se a política ainda bate
      com o que está registrado lá, já que é regra de terceiro
      (Google), não decisão do NEXO.

- [ ] **Validar em campo o limiar de busca aproximada.**

      *Resumo simples:* o número escolhido pro quanto de erro de
      digitação a busca tolera
      ([Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
      PD-NAV-02) é um ponto de partida, nunca testado com gente de
      verdade usando o sistema.

      *Detalhe técnico:* ajustar depois de observação de uso real é
      esperado, não conserto de erro — mesma premissa já registrada
      no [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
      seção 8. Critério objetivo pra essa validação, não inventado
      aqui: **precisão** e **revocação** (precision e recall), as duas
      métricas padrão de avaliação de sistema de busca (MANNING;
      RAGHAVAN; SCHÜTZE, 2008, cap. 8) — precisão é a fração dos
      resultados devolvidos que realmente eram o item procurado;
      revocação é a fração das vezes em que o item procurado existia e
      foi mesmo devolvido. Aplicado aqui: reunir uma coleção de teste
      (pares "termo digitado com erro" → "item que deveria aparecer"),
      a partir de nomes reais de um pacote de conteúdo em uso; rodar a
      busca com o limiar atual contra essa coleção; contar quantas
      vezes o item certo apareceu (revocação) e quantos resultados
      errados vieram junto (precisão). Isso não substitui a validação
      em campo (que depende de comportamento real de digitação, não dá
      pra simular) — dá o critério numérico, repetível, pra decidir se
      um limiar novo é de fato melhor que o atual, em vez de julgar só
      pela impressão.

- [ ] **Explicar a organização de pastas do código de `core` em
      linguagem simples, e reavaliar se ainda faz sentido conforme
      mais pacotes nascerem.**

      *Resumo simples:* quem acompanha o projeto sem saber ler Kotlin
      não consegue confirmar sozinho onde cada coisa mora nem por quê
      — falta uma explicação acessível da organização que já existe,
      não um conserto nela.

      *Detalhe técnico:* a organização em si já está decidida
      ([decisions/0003](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>):
      uma pasta por assunto funcional dentro de `core` — `hierarchy/`,
      `search/`, `session/`, e futuramente `content/`,
      `connectivity/`, `report/` —, nunca por tipo técnico de arquivo
      misturado. Falta: (1) uma explicação, sem termo técnico, de como
      ler essa árvore de pastas; (2) conferir de novo, conforme
      `content`, `connectivity` e `report` forem escritos, se essa
      divisão continua fazendo sentido ou se algum pacote cresceu
      demais e merece ser dividido.

- [ ] **Decidir quem monta o texto de resumo/síntese exibido nas
      telas de fim de evento e de cadeia.**

      *Resumo simples:* as telas de resumo de evento, mensagem de
      pulo e síntese de cadeia (EI-RET-04, EI-PUL-05, EI-ENC-03)
      precisam de um texto montado a partir dos textos que quem
      monta o conteúdo já escreveu por fotograma — hoje nenhum
      pacote assumiu essa responsabilidade.

      *Detalhe técnico:* `session` (ver
      [architecture.md, pacote `session`](<architecture.md#pacote-session--desenho-interno>))
      registra só os fatos (o quê, quando, em que posição) — nunca
      monta texto. `content` (ainda não escrito) só importa e valida
      o pacote de conteúdo, sem responsabilidade de tela definida no
      próprio resumo da pendência dele. `report` (ainda não escrito)
      cobre histórico e exportação CSV/PDF, não tela ao vivo durante o
      jogo. Falta decidir se essa montagem é responsabilidade de um
      desses três, de uma função nova dentro de `app` (Interface), ou
      de um pacote ainda não previsto — telas em
      `architecture.md#interface` hoje só "mostram o que o núcleo
      decide", o que sugere que o texto pronto precisa chegar até elas
      já montado, não ser decidido ali.

## Resolvidas

- [x] **Escolher a linguagem de programação do aplicativo.** Resolvido
      — ver
      [decisions/0001-linguagem-do-aplicativo.md](<../decisions/0001-linguagem-do-aplicativo.md>).
- [x] **Escolher a linguagem e o framework do firmware do acessório
      leitor.** Resolvido — ver
      [decisions/0002-framework-do-firmware-do-acessorio.md](<../decisions/0002-framework-do-firmware-do-acessorio.md>).
- [x] **Decidir a estrutura exata de pastas do projeto Android.**
      Resolvido — ver
      [decisions/0003-estrutura-de-modulos-do-aplicativo.md](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>).
- [x] **Escrever o código-fonte do pacote `search` (núcleo do
      aplicativo).** Resolvido — ver
      [decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md](<../decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md>)
      e
      [decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>).
- [x] **Escrever o código-fonte do pacote `hierarchy` (núcleo do
      aplicativo).** Resolvido — ver
      [decisions/0007-desenho-do-pacote-hierarchy.md](<../decisions/0007-desenho-do-pacote-hierarchy.md>).
- [x] **Detectar posição de tema/evento com buraco ou duplicada na
      validação de `hierarchy`.** Resolvido — ver
      [findings.md#2026-08-14-posicao-com-buraco-nao-detectada](<findings.md#2026-08-14-posicao-com-buraco-nao-detectada>).
- [x] **Escrever o código-fonte do pacote `session` (núcleo do
      aplicativo).** Resolvido — ver
      [decisions/0008-representacao-do-estado-da-sessao.md](<../decisions/0008-representacao-do-estado-da-sessao.md>),
      [decisions/0009-calculo-do-recorte-continuo-de-sessao.md](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>),
      [decisions/0010-persistencia-do-estado-de-sessao-pausada.md](<../decisions/0010-persistencia-do-estado-de-sessao-pausada.md>)
      e
      [decisions/0011-formato-de-serializacao-do-estado-de-sessao.md](<../decisions/0011-formato-de-serializacao-do-estado-de-sessao.md>).
      A montagem de texto de resumos, sínteses e da mensagem de três
      partes de pulo (EI-PUL-05, EI-RET-04, EI-ENC-03) não entra neste
      pacote — depende do pacote `content`, ainda não escrito (ver
      [architecture.md, pacote `session`](<architecture.md#pacote-session--desenho-interno>)).
- [x] **Escrever o esqueleto mínimo do módulo `app`.** Resolvido — ver
      [decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>);
      testado ao vivo num emulador (ver
      [analysis.md](<analysis.md#2026-08-14-esqueleto-minimo-do-modulo-app>)).
      Duas armadilhas de ferramenta encontradas no caminho, ver
      [pitfalls.md](<pitfalls.md#armadilhas>).
- [x] **Escrever o código-fonte do pacote `content` (núcleo do
      aplicativo).** Resolvido — ver
      [decisions/0013-desenho-do-pacote-content.md](<../decisions/0013-desenho-do-pacote-content.md>).
      Uma armadilha de ferramenta encontrada no caminho, ver
      [pitfalls.md](<pitfalls.md#armadilhas>).

## Referências

Fonte externa citada na pendência "Validar em campo o limiar de busca
aproximada", no formato definido pela norma ABNT NBR 6023 (Informação
e documentação — Referências). Citada no corpo do documento como
(AUTOR, ano).

MANNING, Christopher D.; RAGHAVAN, Prabhakar; SCHÜTZE, Hinrich.
**Introduction to Information Retrieval**. Cambridge: Cambridge
University Press, 2008. Disponível em:
https://nlp.stanford.edu/IR-book/pdf/08eval.pdf. Acesso em: 13 ago.
2026.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. Pendência nova ou resolvida também conta
como mudança de conteúdo real. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial. | Criação inicial |
| 0.2.0 | 13-08-2026 | Pendência "Decidir a estrutura exata de pastas do projeto Android" resolvida — movida para Resolvidas. | Resolução de [decisions/0003-estrutura-de-modulos-do-aplicativo.md](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>) |
| 0.3.0 | 13-08-2026 | Pendência única "Escrever o código-fonte de cada componente já desenhado" dividida em sete pendências, uma por pacote/componente já fixado em `architecture.md` (`hierarchy`, `session`, `search`, `content`, `connectivity`, `report` e firmware do acessório). | Detalhamento de pendência existente, sem decisão nova |
| 0.4.0 | 13-08-2026 | Pendência "Validar em campo o limiar de busca aproximada" detalhada com critério objetivo de medição (precisão e revocação). | Detalhamento de pendência existente, sem decisão nova |
| 0.5.0 | 14-08-2026 | Pendência "Escrever o código-fonte do pacote `hierarchy`" resolvida — movida para Resolvidas. | Resolução de [decisions/0007-desenho-do-pacote-hierarchy.md](<../decisions/0007-desenho-do-pacote-hierarchy.md>) |
| 0.6.0 | 14-08-2026 | Pendência nova "Detectar posição de tema/evento com buraco ou duplicada" acrescentada já resolvida, movida direto para Resolvidas. | Achado revelado pelo desenho do pacote `session` |
| 0.7.0 | 14-08-2026 | Pendência nova "Explicar a organização de pastas do código de `core` em linguagem simples, e reavaliar conforme mais pacotes nascerem" acrescentada. | Pedido direto, durante o desenho do pacote `session` |
| 0.8.0 | 14-08-2026 | Pendência "Escrever o código-fonte do pacote `session`" resolvida — movida para Resolvidas. | Resolução de [decisions/0008](<../decisions/0008-representacao-do-estado-da-sessao.md>), [decisions/0009](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>), [decisions/0010](<../decisions/0010-persistencia-do-estado-de-sessao-pausada.md>) e [decisions/0011](<../decisions/0011-formato-de-serializacao-do-estado-de-sessao.md>) |
| 0.9.0 | 14-08-2026 | Pendências novas "Decidir quem monta o texto de resumo/síntese exibido nas telas" e "Escrever o esqueleto mínimo do módulo `app`" acrescentadas. | Lacuna revelada durante o desenho do pacote `session` |
| 0.10.0 | 14-08-2026 | Pendência "Escrever o esqueleto mínimo do módulo `app`" resolvida — movida para Resolvidas. | Resolução de [decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>) |
| 0.11.0 | 14-08-2026 | Pendência "Escrever o código-fonte do pacote `content`" resolvida — movida para Resolvidas. | Resolução de [decisions/0013-desenho-do-pacote-content.md](<../decisions/0013-desenho-do-pacote-content.md>) |
| 0.12.0 | 14-08-2026 | Pendência nova "Avaliar importação parcial de conteúdo (só um tema ou evento novo)" acrescentada. | Pergunta direta, durante a revisão do pacote `content` |
| 0.13.0 | 14-08-2026 | Pendência "Escrever o código-fonte do pacote `connectivity`" reduzida ao lado `app` (`Service` de Bluetooth, leitura NFC) — o lado `core` já foi escrito, testado e removido do escopo desta pendência. | Resolução parcial de [decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>) e [decisions/0016](<../decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md>) |
| 0.14.0 | 14-08-2026 | Pendência nova "Substituir o módulo leitor NFC do acessório (PN532/C1 → PN7160)" acrescentada, sobre PD-LEI-01. | Alerta trazido diretamente, já confirmado em sessão anterior |
