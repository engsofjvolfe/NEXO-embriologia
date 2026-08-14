# Tasks — Motor

<!-- module-doc-type: tasks -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Tasks |
| Versão | 0.4.0 |
| Data | 13-08-2026 |
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

- [ ] **Escrever o código-fonte do pacote `hierarchy` (núcleo do
      aplicativo).**

      *Resumo simples:* cadastro e navegação entre instância, tema e
      evento — a estrutura que organiza o conteúdo já importado.

      *Detalhe técnico:* pacote `core/hierarchy/`, ver
      [architecture.md, layout do aparelho de jogo](<architecture.md#aparelho-de-jogo-aplicativo>).
      Comportamento descrito junto da lógica de sessão em
      [architecture.md, núcleo do motor](<architecture.md#núcleo-do-motor>)
      e em
      [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>).
      Estrutura de dado de instância/tema/evento: bloco YAML em
      [concept.md, Contrato de dado](<concept.md#contrato-de-dado>).

- [ ] **Escrever o código-fonte do pacote `session` (núcleo do
      aplicativo).**

      *Resumo simples:* a lógica de uma partida em si — validar uma
      tentativa, marcar erro, pular, dar dica, encadear eventos,
      pausar/retomar, sair.

      *Detalhe técnico:* pacote `core/session/`, ver
      [architecture.md, layout do aparelho de jogo](<architecture.md#aparelho-de-jogo-aplicativo>).
      Comportamento especificado em
      [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>)
      (categoria `EI-VAL` pra validação — ver
      [architecture.md, núcleo do motor](<architecture.md#núcleo-do-motor>)).

- [ ] **Escrever o código-fonte do pacote `content` (núcleo do
      aplicativo).**

      *Resumo simples:* ler o arquivo de pacote de conteúdo (o
      contrato já fechado no `concept.md`) e recusar qualquer arquivo
      que não bata exatamente com ele.

      *Detalhe técnico:* pacote `core/content/`, ver
      [architecture.md, layout do aparelho de jogo](<architecture.md#aparelho-de-jogo-aplicativo>).
      Importação e validação item a item: PD-IMP-01, PD-IMP-02;
      leitura do arquivo compactado com `java.util.zip.ZipFile`, sem
      biblioteca externa (PD-IMP-03). Contrato de dado a validar:
      [concept.md, Contrato de dado](<concept.md#contrato-de-dado>),
      gerado em [`schemas/`](<../schemas/>).

- [ ] **Escrever o código-fonte do pacote `connectivity` (núcleo do
      aplicativo).**

      *Resumo simples:* ler o identificador de uma peça física, direto
      pela antena do aparelho ou repassado pelo acessório externo por
      Bluetooth.

      *Detalhe técnico:* pacote `core/connectivity/`, ver
      [architecture.md, layout do aparelho de jogo](<architecture.md#aparelho-de-jogo-aplicativo>).
      Dois caminhos de leitura (DA-LEI-03): direto (DA-LEI-04) ou via
      acessório (DA-LEI-06) — a validação trata os dois do mesmo jeito.
      Papel de cliente GATT (central), conectando ao Nordic UART
      Service que o acessório anuncia: PD-CON-01 a PD-CON-04. Fronteira
      de dado com o acessório:
      [architecture.md, fronteira de dado entre aplicativo e acessório](<architecture.md#fronteira-de-dado-entre-aplicativo-e-acessório>).

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
