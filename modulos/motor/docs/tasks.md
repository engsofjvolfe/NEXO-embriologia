# Tasks — Motor

<!-- module-doc-type: tasks -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Tasks |
| Versão | 0.48.0 |
| Data | 29-08-2026 |
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

- [ ] **Substituir o módulo leitor NFC do acessório — o PN532/C1 já
      foi marcado pelo fabricante como não recomendado pra projeto
      novo.**

      *Resumo simples:* o chip de leitura NFC escolhido pro acessório
      físico (PD-LEI-01) foi marcado pela própria NXP como "não
      recomendado pra projeto novo" (NRND — Not Recommended for New
      Designs) — a peça ainda existe à venda, mas o fabricante
      recomenda migrar pra outra. Qual chip exatamente vira o
      substituto ainda precisa ser confirmado com calma — um
      candidato (PN7160) já apareceu, mas essa parte específica ainda
      não está bem estabelecida, então fica em aberto por enquanto,
      sem fechar nele.

      *Detalhe técnico:* PD-LEI-01, do
      [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>)
      (já aprovado, imutável — divergência vira pendência aqui, nunca
      reescrita naquele documento), fixa o NXP PN532/C1 — só essa parte
      (o PN532 estar descontinuado) está confirmada. Não afeta nenhum
      trabalho já feito ou em andamento no pacote `connectivity` até
      agora: o aplicativo fala só com o Bluetooth do acessório (Nordic
      UART Service, PD-CON-01 a PD-CON-04), nunca diretamente com o
      chip leitor — a troca de chip só afeta o firmware do acessório
      (pendência acima, ainda não iniciada) e, possivelmente, a ligação
      elétrica já fixada em PD-LEI-03 (I2C entre o leitor e o
      microcontrolador). Sem decisão tomada ainda sobre qual chip
      exatamente, nem sobre como fazer a troca; registrado aqui só pra
      não perder o alerta antes que o firmware comece a ser escrito —
      a escolha do substituto fica pra quando essa pesquisa for feita
      com mais cuidado.

- [ ] **Confirmar se o acessório leitor (Bluetooth + NFC) precisa de
      homologação ANATEL antes de poder ser usado ou vendido no
      Brasil.**

      *Resumo simples:* toda a cascata do motor (documentos 1 a 5,
      aprovados) decidiu o hardware do acessório — leitor NFC
      (PD-LEI-01), microcontrolador com Bluetooth (PD-LEI-02), serviço
      Bluetooth (PD-CON-01) — sem nunca considerar que, no Brasil, um
      aparelho com rádio (Bluetooth ou NFC com transmissor) só pode
      ser usado ou vendido depois de passar por um processo de
      aprovação da ANATEL, a agência que regula telecomunicação no
      país. Não foi decidido ainda como (ou se) esse processo vai
      acontecer.

      *Detalhe técnico:* três fontes oficiais confirmam a exigência,
      cada uma checada por leitura direta do texto, nunca por resumo
      de terceiro:
      - Lei nº 9.472, de 16 de julho de 1997 (Lei Geral de
        Telecomunicações), art. 162, §2º: veda "a utilização de
        equipamentos emissores de radiofrequência sem certificação
        expedida ou aceita pela Agência" (BRASIL, 1997).
      - Resolução ANATEL nº 715, de 23 de outubro de 2019 (Regulamento
        de Avaliação da Conformidade e de Homologação de Produtos
        para Telecomunicações), art. 55: "a homologação é
        pré-requisito obrigatório para a utilização e a
        comercialização, no País, dos produtos abrangidos por este
        Regulamento" (BRASIL, 2019) — cobre tanto uso quanto venda,
        não só venda.
      - Resolução ANATEL nº 680, de 27 de junho de 2017 (Regulamento
        sobre Equipamentos de Radiocomunicação de Radiação Restrita),
        art. 4º: equipamento de radiação restrita "deve possuir
        certificação emitida ou aceita pela Anatel" (BRASIL, 2017).
        Essa resolução não cita "Bluetooth" nem "NFC" pelo nome — ela
        classifica por faixa de radiofrequência (Anexo I) — mas duas
        faixas listadas ali cobrem exatamente o acessório do NEXO:
        2.400 a 2.483,5 MHz (a faixa que o Bluetooth usa) e 13,41 a
        14,01 MHz (cobre os 13,56 MHz que PD-LEI-01 já fixa pro leitor
        NFC). A ligação entre "Bluetooth/NFC" e essas faixas é leitura
        técnica direta da frequência de operação de cada tecnologia,
        não uma frase literal da norma — nenhuma das três fontes usa
        as palavras "Bluetooth" ou "NFC".

      Nenhum documento da cascata aprovada (imutável, ver
      [docs/docs-VMODEL-visao-geral/README.md](<../../../docs/docs-VMODEL-visao-geral/README.md>))
      trata desse ponto — a pendência nasce aqui, nunca como edição
      retroativa de nenhum dos cinco documentos. Investigação completa
      registrada em
      [analysis.md](<analysis.md#2026-08-15-verificacao-de-exigencia-de-homologacao-anatel>).
      Relacionada à pendência "Substituir o módulo leitor NFC do
      acessório" acima — mesma investigação de origem, mas achado
      independente: mesmo trocando o chip leitor, a exigência de
      homologação continua valendo, porque não depende de qual chip
      específico é usado, só da faixa de frequência em que ele opera.
      Ainda sem decisão, sem responsável, sem desenho de como esse
      processo se encaixaria no fluxo do projeto — só o registro de
      que ele existe. Mesma lógica do marcador `[REVISAR-EXTERNO]` já
      usada no Projeto Detalhado (PD-IMP-05, PD-IMP-07): é regra de
      terceiro (ANATEL), pode mudar — reconfirmar na fonte oficial
      antes de agir (comprar homologação, iniciar processo).

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
      resolvida. Essa camada mora dentro do módulo motor, como uma
      seção própria em `architecture.md`
      ("[Interface](architecture.md#interface)"), separada do "núcleo
      do motor" — não vira módulo separado; motivo completo,
      alternativas descartadas e adiadas em
      [decisions/0029](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>).
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
      confirmação de saída) — como esses estados se agrupam já está
      decidido
      ([decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>):
      `SessionScreen` é um tipo fechado, uma variante por entrada da
      tabela DA-RET — uma tela só, mudando de conteúdo por dentro); o
      gatilho exato por toque que move de um pro outro já está
      decidido, com fonte oficial pra cada situação — ver
      [decisions/0032](<../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md>)
      (o gatilho por temporizador, usado só pela ociosidade, já estava
      resolvido antes, independente da aparência — ver
      [decisions/0024](<../decisions/0024-mecanismo-do-gatilho-de-ociosidade.md>)).
      Dentro disso, o padrão de navegação entre instância, tema e
      evento — expansão em acordeão, nunca troca de tela inteira,
      igual em celular e tablet, incluindo o caso de um nível com
      muitas entradas (resolvido pela busca aproximada já existente,
      sem pendência nova) — já está decidido, ver
      [decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>).
      O conteúdo de cada estado já está fechado
      ([decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>)),
      e o `ViewModel` já expõe um método por ação prevista na
      Especificação (pular, reconhecer uma tela transitória, continuar
      pro próximo evento, pedir/cancelar/confirmar saída — ver
      [architecture.md, Ligação com o núcleo do motor](<architecture.md#ligação-com-o-núcleo-do-motor>)).
      Sem responsável definido ainda (designer, ou o próprio usuário).
      Ponto específico a cobrir quando esse desenho acontecer: a tela
      "Aguardando tentativa" (DA-RET-06) precisa mostrar, de algum
      jeito, se o acessório Bluetooth está conectado, procurando, ou
      desconectado — o dado já existe pronto pra isso
      (`ConnectionState`, ver
      [architecture.md, pacote `connectivity`](<architecture.md#pacote-connectivity--desenho-interno>)),
      só falta decidir como ele aparece. Mesma pergunta pra NFC/Bluetooth
      desligados no aparelho — como avisar a pessoa disso ainda não
      tem resposta nem no dado, nem na aparência. Limite a respeitar
      nesse desenho:
      [Documento de Conceito](<../../../docs/docs-VMODEL-visao-geral/1 - documento-de-conceito-geral.md>),
      seção 8 ("a tela... confirma, não anuncia") — esse indicador
      precisa ser discreto, nunca virar uma explicação ou aviso que
      compita com essa regra.

      O botão de pausar já tem o que chamar — `onPauseRequested()`,
      ver
      [findings.md](<findings.md#2026-08-27-sessionviewmodel-ganha-onpauserequested>)
      — resta só o desenho visual do controle em si, junto com o
      restante das 16 entradas de tela sem wireframe.

- [ ] **Escrever os testes instrumentados de `ReportPdfRenderer.kt` e
      do caminho antigo de `ReportFileWriter.kt` (Android 7 a 9).**

      *Resumo simples:* os dois exigem um aparelho ou emulador Android
      de verdade ligado pra rodar — nenhuma ferramenta sem aparelho
      simula o desenho do PDF nem o retorno que o Android dá quando o
      arquivo termina de ser escrito nesses aparelhos mais antigos.

      *Detalhe técnico:* ver
      [decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>),
      nota de acompanhamento, e
      [findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric](<findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric>).
      Nenhum arquivo de teste escrito ainda.

- [ ] **Avaliar isolar `BleAccessoryService.kt`/`MainActivity.kt` atrás
      de uma interface trocável por implementação falsa no teste
      (injeção de dependência).**

      *Resumo simples:* reduziria o quanto de código dessas duas
      classes depende de simulação de Android, seguindo a mesma
      recomendação oficial já citada em `decisions/0015` — mas exige
      refatorar as duas classes, reforma de arquitetura, não escolha de
      ferramenta de teste.

      *Detalhe técnico:* considerada em
      [decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>)
      e não necessária pra decidir a ferramenta de teste desta tarefa —
      a parte que decodifica o identificador (`tagIdFromBytes`) já está
      isolada numa função pura, com teste próprio. A ideia continua
      válida por outro motivo (reduzir acoplamento com classe do
      Android), registrada aqui como pendência própria, sem desenho nem
      decisão ainda.

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
      `search/`, `session/`, `content/`, `connectivity/`, e ainda por
      escrever, `report/` e `summary/` —, nunca por tipo técnico de
      arquivo misturado. Falta: (1) uma explicação, sem termo técnico,
      de como ler essa árvore de pastas; (2) conferir de novo, conforme
      `report` e `summary` forem escritos, se essa divisão continua
      fazendo sentido ou se algum pacote cresceu demais e merece ser
      dividido.

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
      pacote — é responsabilidade do pacote `summary`, ver
      [decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>)
      ([architecture.md, pacote `session`](<architecture.md#pacote-session--desenho-interno>)).
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
- [x] **Escrever o código-fonte do lado `app` do pacote `connectivity`
      (`Service` de Bluetooth, leitura NFC na `Activity` de entrada).**
      Resolvido — ver
      [decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>),
      [decisions/0017](<../decisions/0017-quem-decide-a-tecnologia-de-leitura.md>)
      e
      [decisions/0018](<../decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md>).
      Testado por compilação real (`gradlew :app:assembleDebug`,
      `BUILD SUCCESSFUL`) e, depois, por teste automatizado — ver
      "Escrever o teste de `MainActivity.kt` (leitura NFC)" e
      "Escrever o teste de `BleAccessoryService.kt` (Bluetooth)" abaixo.
      Uma armadilha de ferramenta encontrada no caminho, ver
      [pitfalls.md](<pitfalls.md#armadilhas>).
- [x] **Decidir quem monta o texto de resumo/síntese exibido nas
      telas de fim de evento e de cadeia.** Resolvido — ver
      [decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>).
      Escrita do código em si segue como pendência própria, acima.
- [x] **Escrever o código-fonte do pacote `summary`, e ajustar
      `content` pra validar o campo novo `summary_fragment`.**
      Resolvido — ver
      [decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>).
      `schema_version` de `content` sobe pra `2.0.0`,
      `summary_fragment` agora obrigatório em cada fotograma.
- [x] **Escrever o código-fonte do pacote `report` (núcleo do
      aplicativo).** Resolvido — ver
      [decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>),
      nota de acompanhamento incluída. Dividido entre `core/report/`
      (dado puro, testado com `kotlin-test`) e `app/report/` (desenho
      do PDF, escrita no aparelho, atalho de compartilhar) — achado que
      motivou essa divisão em
      [findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core](<findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core>).
      `ReportFileWriter.kt`/`ReportShareIntent.kt` já testados (caminho
      novo) — ver Resolvidas. `ReportPdfRenderer.kt` e o caminho antigo
      de `ReportFileWriter.kt` seguem como pendência de teste
      instrumentado, acima.
- [x] **Escrever o código-fonte da ligação entre leitura de peça,
      sessão e tela (`ViewModel`).** Resolvido — ver
      [decisions/0020](<../decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md>)
      (mecanismo) e
      [decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>)
      (conteúdo do estado). `app/ui/SessionUiState.kt` e
      `SessionViewModel.kt` escritos; `core/session` ganhou
      `referenceImage` (EI-SES-04), achado durante a ADR. Testado só
      por compilação real (`gradlew :app:assembleDebug`,
      `gradlew :core:test`) — sem teste automatizado do `ViewModel` em
      si ainda, mesma pendência "Escrever o teste de
      `SessionViewModel.kt`" acima. Gerar o relatório de saída
      e o gatilho de ociosidade ficam de fora, viram pendências
      próprias acima.
- [x] **Gerar o relatório de saída antes de apagar a sessão pausada
      (EI-PAU-04).** Resolvido — ver
      [decisions/0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md](<../decisions/0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md>).
      `onExitConfirmed` passa a exigir a função de escrita do relatório
      como parâmetro, sem depender da tela de resultado (DA-RET-14)
      existir. Testado só por compilação real (`gradlew
      :app:assembleDebug`, `gradlew :core:test`) — sem teste
      automatizado ainda, mesma pendência "Escrever o teste de
      `SessionViewModel.kt`" acima (já cobre o `SessionViewModel`
      inteiro, esta função incluída).
- [x] **Decidir o gatilho de ociosidade (EI-PAU-06).** Resolvido — ver
      [decisions/0024-mecanismo-do-gatilho-de-ociosidade.md](<../decisions/0024-mecanismo-do-gatilho-de-ociosidade.md>).
      `SessionViewModel` conta o tempo por corrotina (`viewModelScope`),
      reiniciada a cada tentativa nova; vencido o `idleThresholdMillis`
      configurado sem tentativa nova, chama `goIdle` e grava o estado
      em disco (`saveSessionState`). Testado só por compilação real
      (`gradlew :app:assembleDebug`, `gradlew :core:test`) — sem teste
      automatizado ainda, mesma pendência "Escrever o teste de
      `SessionViewModel.kt`" acima (já cobre o `SessionViewModel`
      inteiro, este relógio incluído).
- [x] **Decidir ferramenta de teste pro módulo `app`.** Resolvido —
      ver
      [decisions/0025-ferramenta-de-teste-do-modulo-app.md](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>).
      Escrita dos testes em si segue como pendências próprias, acima.
- [x] **Escrever o teste de `MainActivity.kt` (leitura NFC).**
      Resolvido — prova EI-VAL-02: `onTagDiscovered` decodifica o
      identificador bruto da etiqueta e repassa pro `PieceReadListener`.
      Ferramenta: Robolectric ([decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>)).
      Duas armadilhas de ferramenta encontradas e resolvidas, ver
      [pitfalls.md](<pitfalls.md#armadilhas>); investigação completa em
      [analysis.md](<analysis.md#2026-08-16-implementacao-do-teste-de-mainactivity-nfc>);
      lacuna de precisão em `architecture.md` (nome exato da propriedade
      que expõe o `PieceReadListener`) corrigida no caminho. Testado ao
      vivo: `gradlew :app:testDebugUnitTest --tests
      "org.nexo.motor.app.MainActivityTest"`, `BUILD SUCCESSFUL`.
- [x] **Escrever o teste de `BleAccessoryService.kt` (Bluetooth).**
      Resolvido — prova EI-VAL-02: `onCharacteristicChanged` decodifica
      o identificador bruto recebido por Bluetooth e repassa pro
      `PieceReadListener`. Ferramenta: Robolectric
      ([decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>)).
      Uma armadilha de ferramenta encontrada e resolvida, ver
      [pitfalls.md](<pitfalls.md#armadilhas>); investigação completa em
      [analysis.md](<analysis.md#2026-08-17-implementacao-do-teste-de-bleaccessoryservice-bluetooth>);
      lacuna de precisão em `architecture.md` (nomes exatos dos métodos
      que expõem os dois escutadores) corrigida no caminho. Testado ao
      vivo: `gradlew :app:testDebugUnitTest --tests
      "org.nexo.motor.app.connectivity.BleAccessoryServiceTest"`,
      `BUILD SUCCESSFUL`.
- [x] **Escrever o teste de `ReportFileWriter.kt`/`ReportShareIntent.kt`
      (caminho novo, Android 10 em diante).** Resolvido — prova
      DA-ARM-01 (`writeReportCsv` escreve o conteúdo no armazenamento
      local) e DA-ARM-02 (`buildReportShareIntent` só monta e devolve o
      `Intent`, nunca dispara nada sozinho). Ferramenta: Robolectric
      ([decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>)).
      Caminho antigo (Android 7 a 9) confirmado exigir teste
      instrumentado — ver
      [findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric](<findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric>)
      e pendência acima. Investigação completa em
      [analysis.md](<analysis.md#2026-08-17-investigacao-de-teste-de-reportfilewriter-e-reportshareintent>).
      Testado ao vivo: `gradlew :app:testDebugUnitTest --tests
      "org.nexo.motor.app.report.ReportFileWriterTest"`,
      `BUILD SUCCESSFUL`.
- [x] **Formalizar, via ADR, a forma exata de `SessionState`, dos tipos
      do pacote `content` usados pelo `ViewModel`, e do construtor de
      `SessionViewModel`.** Resolvido — ver
      [decisions/0026](<../decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md>),
      forma de `SessionState` corrigida em
      [decisions/0027](<../decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md>).
      Desbloqueou a pendência "Escrever o teste de
      `SessionViewModel.kt`", já resolvida abaixo. Um ponto ficou de
      fora, virando pendência própria, também já resolvida abaixo
      ("Decidir, via ADR, como combinar o recorte de temas...").
- [x] **Escrever o teste de `SessionViewModel.kt`.** Resolvido — prova
      EI-VAL-01/02 (aceitar/rejeitar tentativa), EI-RET-01/02
      (referência e negativa), EI-PAU-03 (pedido/cancelamento de
      saída), EI-PAU-04 (relatório escrito antes de apagar o estado
      retomável, decisions/0023) e EI-PAU-01/06 (gatilho de
      ociosidade, com e sem reinício pelo tempo de uma tentativa nova,
      decisions/0024). Ferramenta: `kotlin-test-junit` +
      `kotlinx-coroutines-test`, sem Robolectric
      ([decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>)).
      Escrito só a partir do que `architecture.md`/`decisions/0026`
      já documentavam, sem abrir nenhum arquivo de `src/main` antes
      de rodar — a primeira rodada não compilou contra o código real,
      revelando a divergência resolvida em
      [decisions/0027](<../decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md>);
      investigação completa em
      [analysis.md](<analysis.md#2026-08-18-escrita-do-teste-de-sessionviewmodel-e-correcao-da-forma-de-sessionstate>).
      Testado ao vivo: `gradlew :app:testDebugUnitTest --tests
      "org.nexo.motor.app.ui.SessionViewModelTest"`, `BUILD
      SUCCESSFUL`, 10 testes; suíte completa (`:app:testDebugUnitTest
      :core:test`) rodada de novo, sem quebra.
- [x] **Decidir, via ADR, como combinar o recorte de temas com o
      recorte de eventos de cada tema numa única lista plana de
      eventos, pra uma sessão que atravessa mais de um tema.**
      Resolvido — ver
      [decisions/0028](<../decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md>).
- [x] **Escrever o código e o teste de `sessionEventNames`
      (combinação do recorte de temas e de eventos numa sessão que
      atravessa mais de um tema).** Resolvido — prova EI-SES-06/07
      (sessão dentro de um tema só, sessão atravessando dois temas sem
      tema no meio, sessão atravessando três temas com o do meio
      inteiro, sessão cobrindo do primeiro ao último evento). Escrito
      só a partir de `decisions/0028`, sem abrir nenhum arquivo de
      `src/main` antes de rodar; implementação escrita só depois do
      teste já existir, sem overfit num sentido nem no outro.
      Investigação completa em
      [analysis.md](<analysis.md#2026-08-18-combinacao-do-recorte-de-temas-e-eventos-numa-sessao-multitema>).
      Testado ao vivo: `gradlew :core:test --tests
      "org.nexo.motor.core.session.SessionScopeTest"`, `BUILD
      SUCCESSFUL`; suíte completa (`:core:test :app:testDebugUnitTest`)
      rodada de novo, sem quebra.
- [x] **Decidir a ferramenta de desenho de tela do módulo `app`
      (Jetpack Compose ou Views tradicionais).** Resolvido — ver
      [decisions/0031](<../decisions/0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md>).
      Pendência identificada durante o desenho do padrão de navegação
      hierárquica de conteúdo, numa worktree separada (ainda não
      mesclada em `develop` no momento em que esta foi resolvida) —
      por isso nunca chegou a aparecer aqui como "Em aberto" antes de
      já vir riscada.

## Referências

Fontes externas citadas nas pendências "Validar em campo o limiar de
busca aproximada" e "Confirmar se o acessório leitor precisa de
homologação ANATEL", no formato definido pela norma ABNT NBR 6023
(Informação e documentação — Referências). Citadas no corpo do
documento como (AUTOR, ano).

BRASIL. Lei nº 9.472, de 16 de julho de 1997. Dispõe sobre a
organização dos serviços de telecomunicações, a criação e
funcionamento de um órgão regulador e outros aspectos institucionais,
nos termos da Emenda Constitucional nº 8, de 1995 (Lei Geral de
Telecomunicações). Diário Oficial da União, Brasília, DF, 17 jul.
1997. Disponível em: https://www.planalto.gov.br/ccivil_03/leis/l9472.htm.
Acesso em: 15 ago. 2026.

BRASIL. Agência Nacional de Telecomunicações. Resolução nº 680, de 27
de junho de 2017. Aprova o Regulamento sobre Equipamentos de
Radiocomunicação de Radiação Restrita. Diário Oficial da União,
Brasília, DF, 29 jun. 2017. Disponível em:
https://www.anatel.gov.br/legislacao/resolucoes/2017/936-resolucao-680.
Acesso em: 15 ago. 2026.

BRASIL. Agência Nacional de Telecomunicações. Resolução nº 715, de 23
de outubro de 2019. Aprova o Regulamento de Avaliação da Conformidade
e de Homologação de Produtos para Telecomunicações. Diário Oficial da
União, Brasília, DF, 2019. Disponível em:
https://informacoes.anatel.gov.br/legislacao/resolucoes/2019/1350-resolucao-715.
Acesso em: 15 ago. 2026.

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
| 0.15.0 | 15-08-2026 | Pendência "Escrever o código-fonte do lado `app` do pacote `connectivity`" resolvida — movida para Resolvidas. Pendência nova "Decidir ferramenta de teste pro módulo `app`" acrescentada. | Resolução de [decisions/0015](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>), [decisions/0017](<../decisions/0017-quem-decide-a-tecnologia-de-leitura.md>) e [decisions/0018](<../decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md>) |
| 0.16.0 | 15-08-2026 | Pendência "Desenhar a aparência visual das telas" ganha ponto específico: "Aguardando tentativa" (DA-RET-06) precisa cobrir o `ConnectionState` do acessório, de forma discreta (Documento de Conceito, seção 8). | `Service` de Bluetooth passa a expor o próprio estado de conexão |
| 0.17.0 | 15-08-2026 | Pendência "Substituir o módulo leitor NFC do acessório" abrandada: só o PN532 descontinuado está confirmado — o PN7160 como substituto exato ainda não está bem estabelecido, fica em aberto sem fechar nele. | Pedido direto, revisão antes de fechar a tarefa |
| 0.18.0 | 15-08-2026 | Pendência nova "Confirmar se o acessório leitor precisa de homologação ANATEL" acrescentada, com três fontes legais citadas (Lei 9.472/1997, Resoluções ANATEL 680/2017 e 715/2019). | Achado revelado durante a pesquisa sobre substituição do chip PN532 |
| 0.19.0 | 15-08-2026 | Pendência "Escrever o código-fonte do pacote `report`" ganha ponteiro pro desenho interno já decidido, ainda não riscada (falta o código em si). | Resolução de [decisions/0019](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>) |
| 0.20.0 | 15-08-2026 | Pendência nova "Escrever o código-fonte da ligação entre leitura de peça, sessão e tela (`ViewModel`)" acrescentada. | Resolução de [decisions/0020](<../decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md>) |
| 0.21.0 | 15-08-2026 | Pendência "Decidir quem monta o texto de resumo/síntese" resolvida — movida para Resolvidas. Pendência nova "Escrever o código-fonte do pacote `summary`, e ajustar `content` pra validar o campo novo `summary_fragment`" acrescentada; `report` e `summary` acrescentados na lista de pacotes da pendência "Explicar a organização de pastas do `core`". | Resolução de [decisions/0021](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>) |
| 0.22.0 | 15-08-2026 | Pendências "Escrever o código-fonte do pacote `report`" e "Escrever o código-fonte do pacote `summary`, e ajustar `content`" resolvidas — movidas para Resolvidas. Pendência "Decidir ferramenta de teste pro módulo `app`" passa a citar também o lado `app` de `report`. | Escrita dos pacotes `summary` e `report`; achado sobre `PdfDocument` incompatível com `core` (`findings.md`) motivou dividir `report` entre `core` e `app` |
| 0.23.0 | 15-08-2026 | Pendência "Escrever o código-fonte da ligação entre leitura de peça, sessão e tela (`ViewModel`)" resolvida — movida para Resolvidas. Duas pendências novas acrescentadas ("Gerar o relatório de saída antes de apagar a sessão pausada", "Decidir o gatilho de ociosidade"). | Resolução de [decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>); escrita de `SessionUiState.kt`/`SessionViewModel.kt`, incluindo `onExitConfirmed` |
| 0.24.0 | 16-08-2026 | Pendência "Gerar o relatório de saída antes de apagar a sessão pausada (EI-PAU-04)" resolvida — movida para Resolvidas, já com a nota de teste (testado só por compilação real, sem teste automatizado — mesma pendência "Decidir ferramenta de teste pro módulo `app`"). | Resolução de [decisions/0023](<../decisions/0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md>) |
| 0.25.0 | 16-08-2026 | Pendência "Decidir ferramenta de teste pro módulo `app`" passa a citar o `SessionViewModel` (incluindo `onExitConfirmed`), que faltava — checado contra toda ocorrência de "testado só por compilação real" em `architecture.md`, confirmando que são só três pontos afetados hoje (`connectivity`/`app`, `report`/`app`, `SessionViewModel`). Item resolvido do pacote `report` ganha o ponteiro de volta pra essa pendência, que também faltava (mesmo padrão já usado no item do `connectivity`). | Auditoria direta, durante a revisão final da tarefa do relatório de saída |
| 0.26.0 | 16-08-2026 | Corrigida a direção do ponteiro de volta em três itens resolvidos (`connectivity`, `report`, `ViewModel`): diziam "abaixo", mas a pendência "Decidir ferramenta de teste pro módulo `app`" fica na seção "Em aberto", antes de "Resolvidas" no arquivo — o certo é "acima", já usado corretamente no item novo do relatório de saída. | Correção direta, durante a mesma revisão |
| 0.27.0 | 16-08-2026 | Linha `0.24.0` ganha a informação que faltava: o item resolvido do relatório de saída já incluía a nota de teste desde a criação, não só depois — corrige um registro incompleto do próprio histórico. | Revisão final da tarefa do relatório de saída |
| 0.28.0 | 16-08-2026 | Pendência "Decidir o gatilho de ociosidade (EI-PAU-06)" resolvida — movida para Resolvidas. | Resolução de [decisions/0024-mecanismo-do-gatilho-de-ociosidade.md](<../decisions/0024-mecanismo-do-gatilho-de-ociosidade.md>) |
| 0.29.0 | 16-08-2026 | Pendência "Desenhar a aparência visual das telas do motor" corrigida: não cita mais "temporizador" como gatilho ainda pendente de desenho visual — esse gatilho (ociosidade) já está resolvido, independente da aparência; só o gatilho por toque continua pendente. | Checagem mecânica antes de abrir o PR, achado por releitura completa da cadeia de documentos |
| 0.30.0 | 16-08-2026 | Pendência "Decidir ferramenta de teste pro módulo `app`" detalhada: as três categorias (`connectivity`, `report`/`app`, `SessionViewModel`) explicitadas em cinco arquivos/comportamentos concretos que exigem teste próprio (`BleAccessoryService.kt`, `MainActivity.kt`, `ReportPdfRenderer.kt`, `ReportFileWriter.kt`/`ReportShareIntent.kt`, `onExitConfirmed` e o gatilho de ociosidade dentro de `SessionViewModel`). | Ambiguidade encontrada na auditoria desta mesma pendência, antes de escrever qualquer teste |
| 0.31.0 | 16-08-2026 | Pendência "Decidir ferramenta de teste pro módulo `app`" resolvida — movida para Resolvidas. Três pendências novas acrescentadas: "Escrever os testes de `BleAccessoryService.kt`, `MainActivity.kt`, `ReportFileWriter.kt`/`ReportShareIntent.kt` e `SessionViewModel.kt`" (sem aparelho nem emulador), "Escrever o teste instrumentado de `ReportPdfRenderer.kt`" (exige aparelho ou emulador ligado) e "Avaliar isolar `BleAccessoryService.kt`/`MainActivity.kt` atrás de interface" (reforma de arquitetura considerada na ADR, não escolhida por não ser necessária pra decidir ferramenta). | Resolução de [decisions/0025-ferramenta-de-teste-do-modulo-app.md](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>) |
| 0.32.0 | 16-08-2026 | Pendência "Escrever o teste de `MainActivity.kt`" resolvida — movida para Resolvidas, separada da pendência dos outros três pontos. | Primeiro teste real do módulo `app` escrito e rodado (NFC); investigação em [analysis.md](<analysis.md#2026-08-16-implementacao-do-teste-de-mainactivity-nfc>) |
| 0.33.0 | 17-08-2026 | Pendência "Escrever o teste de `BleAccessoryService.kt`" resolvida — movida para Resolvidas, separada da pendência dos dois pontos restantes (`ReportFileWriter.kt`/`ReportShareIntent.kt`, `SessionViewModel.kt`). Ponteiros desatualizados corrigidos em cinco itens já resolvidos que citavam o nome antigo da pendência. | Segundo teste real do módulo `app` escrito e rodado (Bluetooth); investigação em [analysis.md](<analysis.md#2026-08-17-implementacao-do-teste-de-bleaccessoryservice-bluetooth>) |
| 0.34.0 | 17-08-2026 | Pendência "Escrever os testes de `ReportFileWriter.kt`/`ReportShareIntent.kt` e `SessionViewModel.kt`" restrita ao caminho novo de `ReportFileWriter.kt` (Android 10 em diante). Pendência "Escrever o teste instrumentado de `ReportPdfRenderer.kt`" passa a incluir também o caminho antigo de `ReportFileWriter.kt` (Android 7 a 9), que se revelou exigir aparelho pelo mesmo motivo. | Achado [findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric](<findings.md#2026-08-17-caminho-antigo-de-reportfilewriter-nao-testavel-com-robolectric>), nota de acompanhamento em [decisions/0025](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>) |
| 0.35.0 | 17-08-2026 | Pendência "Escrever o teste de `ReportFileWriter.kt`/`ReportShareIntent.kt` (caminho novo)" resolvida — movida para Resolvidas, restando só `SessionViewModel.kt` na pendência aberta. Ponteiros desatualizados corrigidos em quatro itens já resolvidos que citavam o nome antigo da pendência. | Terceiro teste real do módulo `app` escrito e rodado; investigação em [analysis.md](<analysis.md#2026-08-17-investigacao-de-teste-de-reportfilewriter-e-reportshareintent>) |
| 0.36.0 | 17-08-2026 | Pendência nova "Formalizar, via ADR, a forma exata de `SessionState`, dos tipos de `content` usados pelo `ViewModel`, e do construtor de `SessionViewModel`" acrescentada, bloqueando "Escrever o teste de `SessionViewModel.kt`". | Achado durante a tentativa de escrever esse teste — investigação em [analysis.md](<analysis.md#2026-08-17-lacuna-na-forma-de-sessionstate-e-dos-tipos-de-content>) |
| 0.37.0 | 18-08-2026 | Pendência "Formalizar, via ADR, a forma exata de `SessionState`..." resolvida — movida para Resolvidas, desbloqueando "Escrever o teste de `SessionViewModel.kt`". Pendência nova "Decidir, via ADR, como combinar o recorte de temas com o recorte de eventos..." acrescentada. | Resolução de [decisions/0026](<../decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md>) |
| 0.38.0 | 18-08-2026 | Pendência "Escrever o teste de `SessionViewModel.kt`" resolvida — movida para Resolvidas, dez testes escritos e rodados. Ponteiro do item "Formalizar, via ADR, a forma exata de `SessionState`..." atualizado com a correção de `decisions/0027`. | Quarto e último teste real do módulo `app` escrito e rodado; achado durante a escrita levou a [decisions/0027](<../decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md>) |
| 0.39.0 | 18-08-2026 | Pendência "Decidir, via ADR, como combinar o recorte de temas..." resolvida — movida para Resolvidas. Pendência nova "Escrever o código e o teste de `sessionEventNames`" acrescentada em seu lugar. | Resolução de [decisions/0028](<../decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md>) |
| 0.40.0 | 18-08-2026 | Pendência "Escrever o código e o teste de `sessionEventNames`" resolvida — movida para Resolvidas, quatro testes escritos e rodados. | `sessionEventNames` implementada e testada, sem quebra na suíte completa |
| 0.41.0 | 18-08-2026 | Pendência "Desenhar a aparência visual das telas do motor" ganha ponteiro pra ADR: o trecho "já decidido... não há fronteira real que justifique separar" passa a apontar pra [decisions/0029](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>), no lugar da frase solta sem análise registrada. | Resolução de [decisions/0029](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>) |
| 0.42.0 | 22-08-2026 | Pendência "Desenhar a aparência visual das telas do motor" ganha ponteiro pra ADR nova (padrão de navegação por acordeão, incluindo o caso de um nível com muitas entradas, resolvido pela busca aproximada já existente, sem pendência própria), e ponteiro pro achado já registrado sobre o botão de pausar ainda sem código correspondente, que não constava aqui. | Resolução de [decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>) |
| 0.43.0 | 22-08-2026 | Pendência nova "Decidir a ferramenta de desenho de tela do módulo `app` (Jetpack Compose ou Views tradicionais)" acrescentada — revelada ao decidir que o acordeão de `decisions/0030` precisa renderizar de forma preguiçosa, mecanismo cujo nome exato depende dessa escolha, ainda não feita em nenhum documento. | Achado durante a escrita de [decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>) |
| 0.44.0 | 22-08-2026 | Corrigido: o trecho "como agrupar esses estados em telas físicas... parte do desenho visual pendente" estava desatualizado desde 15-08-2026 — `decisions/0022` já resolve isso (`SessionScreen`, tipo fechado, uma variante por entrada da tabela DA-RET, uma tela só) e nunca tinha sido conectado aqui. | Achado ao revisar a pendência por completo, na mesma sessão da ADR 0030 |
| 0.45.0 | 22-08-2026 | Pendência nova "Decidir a ferramenta de desenho de tela do módulo `app`" acrescentada já resolvida, movida direto para Resolvidas — nunca existiu aqui como "Em aberto", porque foi identificada numa worktree separada, ainda não mesclada nesta. | Resolução de [decisions/0031](<../decisions/0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md>) |
| 0.46.0 | 22-08-2026 | Corrigida a duplicidade que a mescla de `develop` trouxe: o item "Decidir a ferramenta de desenho de tela do módulo `app`" acrescentado na linha `0.43.0` desta mesma tabela saiu de "Em aberto" — já resolvido em `develop` (linha `0.45.0`), enquanto essa worktree ainda não tinha essa informação. | Reconciliação ao mesclar `develop` (`decisions/0031`) nesta worktree |
| 0.47.0 | 27-08-2026 | Pendência "Desenhar a aparência visual das telas do motor" perde o bloqueio de código sobre o botão de pausar — `onPauseRequested()` já existe; só o desenho visual do controle segue pendente. | Escrita e teste de `onPauseRequested()` em `SessionViewModel.kt` |
| 0.48.0 | 29-08-2026 | Pendência "Desenhar a aparência visual das telas do motor" ganha ponteiro pra ADR nova: o gatilho de toque entre os oito estados da tela de jogo, antes só citado como pendente, agora está decidido, com duas fontes oficiais independentes (Material Design 3, Nielsen Norman Group). Aparência visual em si, leiaute das 16 entradas de tela restantes, indicador de conexão e protótipo continuam pendentes. | Resolução de [decisions/0032](<../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md>) |
