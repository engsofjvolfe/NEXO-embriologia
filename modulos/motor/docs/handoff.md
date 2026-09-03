# Handoff — Motor

<!-- module-doc-type: handoff -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Handoff |
| Versão | 0.50.0 |
| Data | 03-09-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Resumo curto de "onde este módulo está" e "o que fazer a seguir" nele.
> Última etapa do fluxo — atualizado depois de qualquer uma das outras
> mudar. Só ponteiro, nunca detalhe, nunca resumo — detalhe de verdade
> mora em `concept.md`, `tasks.md`, `findings.md` ou `decisions/`. Fica
> desatualizado rápido se carregar mais que isso. Modelo: o
> `HANDOFF.md` da raiz do projeto, que só aponta.
>
> Cada linha segue [a regra de escrita geral](../../README.md#como-escrever):
> link markdown de verdade + uma frase curta, nunca uma descrição do
> que o conteúdo diz. Arquivo que só existe uma vez por módulo
> (`concept.md`, `architecture.md`, `tasks.md`, `findings.md`,
> `pitfalls.md`, `analysis.md`) leva frase genérica, sobre o papel do
> arquivo, nunca o assunto específico do que tem dentro dele -- ex.:
> "achados confirmados até agora", nunca "achado sobre X". Link pra
> dentro de `decisions/` é a exceção: como pode haver várias ADRs,
> nomear a decisão específica ali não é descrição de conteúdo, é a
> única forma de diferenciar uma ADR da outra na lista.

## Índice
- [Estado atual](#estado-atual)
- [Próximo passo](#proximo-passo)
- [Controle de versão](#controle-de-versão)

## Estado atual

- [concept.md](concept.md) — o que o módulo deve ser e como deve se
  comportar.
- [architecture.md](architecture.md) — como o módulo é construído por
  dentro.
- [schemas/](../schemas/) — contrato de dado das fronteiras do módulo.
- [decisions/0001-linguagem-do-aplicativo.md](<../decisions/0001-linguagem-do-aplicativo.md>) —
  Kotlin escolhido pro aplicativo.
- [decisions/0002-framework-do-firmware-do-acessorio.md](<../decisions/0002-framework-do-firmware-do-acessorio.md>) —
  C++/Arduino/PlatformIO escolhido pro firmware do acessório.
- [decisions/0003-estrutura-de-modulos-do-aplicativo.md](<../decisions/0003-estrutura-de-modulos-do-aplicativo.md>) —
  estrutura de módulos e pacotes do projeto Android decidida.
- [decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md](<../decisions/0004-desenho-do-algoritmo-de-busca-aproximada.md>) —
  desenho interno do algoritmo de busca aproximada decidido.
- [decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md](<../decisions/0005-abordagem-de-teste-do-nucleo-do-motor.md>) —
  ferramenta de teste do módulo `core` decidida.
- [decisions/0006-localizacao-do-projeto-gradle-no-repositorio.md](<../decisions/0006-localizacao-do-projeto-gradle-no-repositorio.md>) —
  localização do projeto Gradle no repositório decidida.
- [decisions/0007-desenho-do-pacote-hierarchy.md](<../decisions/0007-desenho-do-pacote-hierarchy.md>) —
  desenho interno do pacote `hierarchy` decidido.
- [decisions/0008-representacao-do-estado-da-sessao.md](<../decisions/0008-representacao-do-estado-da-sessao.md>) —
  representação do estado da sessão em curso decidida.
- [decisions/0009-calculo-do-recorte-continuo-de-sessao.md](<../decisions/0009-calculo-do-recorte-continuo-de-sessao.md>) —
  cálculo do recorte contíguo de uma sessão decidido.
- [decisions/0010-persistencia-do-estado-de-sessao-pausada.md](<../decisions/0010-persistencia-do-estado-de-sessao-pausada.md>) —
  persistência do estado de sessão pausada em disco decidida.
- [decisions/0011-formato-de-serializacao-do-estado-de-sessao.md](<../decisions/0011-formato-de-serializacao-do-estado-de-sessao.md>) —
  formato de serialização (JSON) do estado de sessão decidido.
- [decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>) —
  versões de SDK, alvo de API e Android Gradle Plugin do módulo `app`
  decididas.
- [decisions/0013-desenho-do-pacote-content.md](<../decisions/0013-desenho-do-pacote-content.md>) —
  desenho interno do pacote `content` decidido.
- [decisions/0014-busca-aproximada-com-termo-vazio.md](<../decisions/0014-busca-aproximada-com-termo-vazio.md>) —
  comportamento da busca aproximada com termo vazio decidido.
- [decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md](<../decisions/0015-fronteira-entre-core-e-app-no-pacote-connectivity.md>) —
  fronteira entre `core` e `app` no pacote `connectivity` decidida.
- [decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md](<../decisions/0016-formato-do-identificador-na-notificacao-bluetooth.md>) —
  formato do identificador de peça na notificação Bluetooth decidido.
- [decisions/0017-quem-decide-a-tecnologia-de-leitura.md](<../decisions/0017-quem-decide-a-tecnologia-de-leitura.md>) —
  quem decide a tecnologia de leitura (NFC ou Bluetooth) decidido.
- [decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md](<../decisions/0018-estrategia-de-permissao-de-bluetooth-e-nfc.md>) —
  estratégia de permissão de Bluetooth e NFC decidida.
- [decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md](<../decisions/0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md>) —
  mecanismo de geração, guarda e compartilhamento do relatório
  decidido.
- [decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md](<../decisions/0020-ligacao-entre-leitura-de-peca-e-a-tela.md>) —
  ligação entre leitura de peça, lógica de sessão e tela decidida.
- [decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md](<../decisions/0021-quem-monta-o-texto-de-resumo-e-sintese.md>) —
  quem monta o texto de resumo e síntese decidido.
- [decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>) —
  conteúdo do estado exposto pelo `ViewModel` decidido.
- [decisions/0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md](<../decisions/0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md>) —
  geração do relatório de saída antes de apagar a sessão pausada
  decidida.
- [decisions/0024-mecanismo-do-gatilho-de-ociosidade.md](<../decisions/0024-mecanismo-do-gatilho-de-ociosidade.md>) —
  mecanismo do gatilho de ociosidade (EI-PAU-06) decidido.
- [decisions/0025-ferramenta-de-teste-do-modulo-app.md](<../decisions/0025-ferramenta-de-teste-do-modulo-app.md>) —
  ferramenta de teste dos cinco pontos pendentes do módulo `app`
  decidida.
- [decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md](<../decisions/0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md>) —
  forma exata de `SessionState`, dos tipos de `content` usados pelo
  `ViewModel`, e do construtor de `SessionViewModel` decidida.
- [decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md](<../decisions/0027-sessionstate-referencia-o-evento-atual-pelo-nome.md>) —
  campo do evento em curso de `SessionState` corrigido.
- [decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md](<../decisions/0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md>) —
  combinação do recorte de temas e de eventos numa sessão multi-tema
  decidida.
- [decisions/0029-aparencia-visual-das-telas-mora-no-motor.md](<../decisions/0029-aparencia-visual-das-telas-mora-no-motor.md>) —
  responsabilidade pela aparência visual das telas decidida.
- [decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>) —
  padrão de navegação hierárquica (acordeão) entre instância, tema e
  evento decidido.
- [decisions/0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md](<../decisions/0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md>) —
  Jetpack Compose escolhido como ferramenta de desenho de tela do
  módulo `app`.
- [decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md](<../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md>) —
  gatilho de toque entre os oito estados da tela de jogo decidido, com
  fonte oficial.
- [decisions/0033-formato-de-aparelho-leiaute-responsivo.md](<../decisions/0033-formato-de-aparelho-leiaute-responsivo.md>) —
  formato de aparelho (celular primeiro, quando o tablet ganha leiaute
  diferente) decidido, com fonte oficial.
- [decisions/0034-mecanismo-de-carregamento-preguicoso-do-acordeao-de-navegacao.md](<../decisions/0034-mecanismo-de-carregamento-preguicoso-do-acordeao-de-navegacao.md>) —
  mecanismo de carregamento aos poucos do acordeão de navegação
  decidido, com fonte oficial.
- [decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md](<../decisions/0035-sistema-visual-cor-tipografia-forma-contraste.md>) —
  sistema visual (cor, tipografia, forma, contraste, área de toque)
  decidido, com fonte oficial.
- [wireframe.md](../design/wireframe.md) — leiaute de tela.
- [decisions/0036-ferramenta-e-fidelidade-do-prototipo-navegavel.md](<../decisions/0036-ferramenta-e-fidelidade-do-prototipo-navegavel.md>) —
  ferramenta e fidelidade do protótipo navegável decididas.
- [design/prototipo-navegavel.html](<../design/prototipo-navegavel.html>) —
  protótipo clicável das 17 entradas de tela, fechando o método de
  desenho visual.
- [design/avaliacao-heuristica.md](<../design/avaliacao-heuristica.md>) —
  avaliação do protótipo contra as dez heurísticas de Nielsen.
- [decisions/0037-ferramenta-de-teste-das-telas-compose.md](<../decisions/0037-ferramenta-de-teste-das-telas-compose.md>) —
  ferramenta de teste das telas em Jetpack Compose decidida
  (Robolectric, sem aparelho).
- [app/src/main/kotlin/org/nexo/motor/app/ui/SessionGameScreen.kt](<../app/src/main/kotlin/org/nexo/motor/app/ui/SessionGameScreen.kt>) —
  primeira tela real do motor, a tela de jogo, testada de verdade.
- [decisions/0038-carregamento-de-imagem-de-fotograma-na-tela.md](<../decisions/0038-carregamento-de-imagem-de-fotograma-na-tela.md>) —
  carregamento de imagem de fotograma na tela decidido.
- [decisions/0039-fonte-de-icone-dos-controles-de-tela.md](<../decisions/0039-fonte-de-icone-dos-controles-de-tela.md>) —
  fonte de ícone dos controles de tela decidida (rótulo de texto).
- [app/src/main/kotlin/org/nexo/motor/app/ui/](<../app/src/main/kotlin/org/nexo/motor/app/ui/>) —
  as 17 entradas de tela do motor, escritas e testadas.
- [decisions/0040-mecanismo-de-navegacao-entre-telas-do-motor.md](<../decisions/0040-mecanismo-de-navegacao-entre-telas-do-motor.md>) —
  mecanismo de navegação entre as telas decidido.
- [app/src/main/kotlin/org/nexo/motor/app/ui/MotorApp.kt](<../app/src/main/kotlin/org/nexo/motor/app/ui/MotorApp.kt>) —
  ponto de entrada real do módulo `app`, testado (`MotorAppTest.kt`).
- [decisions/0041-fonte-de-icone-do-botao-de-pausar.md](<../decisions/0041-fonte-de-icone-do-botao-de-pausar.md>) —
  fonte de ícone do botão de pausar revisada (ícone oficial).
- [decisions/0042-conteudo-de-teste-visual-isolado-por-tipo-de-build.md](<../decisions/0042-conteudo-de-teste-visual-isolado-por-tipo-de-build.md>) —
  conteúdo de teste visual isolado por tipo de build decidido.
- [decisions/0043-mecanismo-de-classificacao-de-tamanho-de-janela.md](<../decisions/0043-mecanismo-de-classificacao-de-tamanho-de-janela.md>) —
  mecanismo de classificação de tamanho de janela (`isTabletLayout`)
  decidido.
- [findings.md](findings.md) — achados confirmados até agora.
- [analysis.md](analysis.md) — registro de como cada investigação
  deste módulo foi feita.
- [pitfalls.md](pitfalls.md) — armadilhas de ferramenta já encontradas.
- [tasks.md, Resolvidas](tasks.md#resolvidas) — pendências já
  corrigidas.

## Próximo passo

- [tasks.md, Em aberto](tasks.md#em-aberto) — pendências abertas.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial. | Criação inicial |
| 0.2.0 | 13-08-2026 | Acrescentado ponteiro para decisions/0003-estrutura-de-modulos-do-aplicativo.md. | Resolução da pendência de estrutura de pastas do Android |
| 0.3.0 | 14-08-2026 | Acrescentado ponteiro para decisions/0004, 0005 e 0006-localizacao-do-projeto-gradle-no-repositorio.md. | Primeiro código do módulo (pacote `search`) |
| 0.4.0 | 14-08-2026 | Acrescentado ponteiro para decisions/0007-desenho-do-pacote-hierarchy.md. | Segundo pacote do módulo `core` escrito (pacote `hierarchy`) |
| 0.5.0 | 14-08-2026 | Acrescentado ponteiro para decisions/0008 a 0011 e para findings.md. | Terceiro pacote do módulo `core` escrito (pacote `session`), com um achado de `hierarchy` revelado no caminho |
| 0.6.0 | 14-08-2026 | Acrescentado ponteiro para decisions/0012 e para pitfalls.md. | Esqueleto mínimo do módulo `app` escrito e testado ao vivo num emulador |
| 0.7.0 | 14-08-2026 | Acrescentado ponteiro para decisions/0013. | Quarto pacote do módulo `core` escrito (pacote `content`) |
| 0.8.0 | 14-08-2026 | Acrescentado ponteiro para analysis.md, que já existia mas nunca tinha linha própria aqui. | Revisão de cobertura de teste dos pacotes `search`, `hierarchy`, `session` e `content` |
| 0.9.0 | 14-08-2026 | Acrescentado ponteiro para decisions/0014. | Formalização do comportamento da busca aproximada com termo vazio |
| 0.10.0 | 14-08-2026 | Acrescentado ponteiro para decisions/0015 e 0016. | Lado `core` do pacote `connectivity` escrito e testado (contrato: UUIDs do Nordic UART Service, decodificação de identificador físico); lado `app` (`Service` de Bluetooth, leitura NFC) segue como pendência em `tasks.md` |
| 0.11.0 | 15-08-2026 | Acrescentado ponteiro para decisions/0017 e 0018. | Registrado que a pessoa decide entre NFC e Bluetooth, nunca o aplicativo sozinho; estratégia de permissão redesenhada em cima dessa decisão |
| 0.12.0 | 15-08-2026 | Nenhum ponteiro novo (pacote `connectivity` já citado). | Lado `app` do pacote `connectivity` escrito e testado por compilação real — pacote completo, `core` e `app` |
| 0.13.0 | 15-08-2026 | Nenhum ponteiro novo (`tasks.md` e `analysis.md` já citados). | Pendência nova em `tasks.md` sobre exigência de homologação ANATEL pro acessório leitor, com investigação registrada em `analysis.md` |
| 0.14.0 | 15-08-2026 | Acrescentado ponteiro para decisions/0019. | Mecanismo de geração, guarda e compartilhamento do relatório decidido (pacote `report`) |
| 0.15.0 | 15-08-2026 | Acrescentado ponteiro para decisions/0020. | Ligação entre leitura de peça, lógica de sessão e tela decidida (`ViewModel`) |
| 0.16.0 | 15-08-2026 | Acrescentado ponteiro para decisions/0021. | Quem monta o texto de resumo e síntese decidido (pacote `summary`, com mudança de contrato de dado) |
| 0.17.0 | 15-08-2026 | Nenhum ponteiro novo (decisions/0008 e 0019 já citadas, `findings.md`/`analysis.md`/`tasks.md` genéricos). | Registro de sessão completado frente a EI-REG-01; `content` atualizado pro contrato `2.0.0`; pacotes `summary` e `report` escritos, `report` dividido entre `core` e `app` |
| 0.18.0 | 15-08-2026 | Acrescentado ponteiro para decisions/0022. | Correção de decisions/0020 (conteúdo do estado não dependia do desenho visual); conteúdo do estado do `ViewModel` decidido e escrito (`SessionUiState.kt`, `SessionViewModel.kt`, apagando o estado retomável na saída confirmada); gerar o relatório de saída e o gatilho de ociosidade seguem como pendências em `tasks.md` |
| 0.19.0 | 16-08-2026 | Acrescentado ponteiro para decisions/0023. | Geração do relatório de saída antes de apagar a sessão pausada resolvida (EI-PAU-04): `onExitConfirmed` passa a exigir a função de escrita do relatório como parâmetro; auditoria de consistência em `tasks.md` corrigiu dois pontos que já estavam desatualizados antes desta tarefa (o `SessionViewModel` não constava na pendência "Decidir ferramenta de teste pro módulo `app`", e o item resolvido do pacote `report` não apontava de volta pra ela) |
| 0.20.0 | 16-08-2026 | Acrescentado ponteiro para decisions/0024. | Gatilho de ociosidade (EI-PAU-06) resolvido: `SessionViewModel` conta o tempo por corrotina (`viewModelScope`), reiniciada a cada tentativa nova; vencido o prazo sem tentativa, chama `goIdle` e grava o estado em disco, com `onExitConfirmed` cancelando esse relógio antes de apagar o estado retomável |
| 0.21.0 | 16-08-2026 | Acrescentado ponteiro para decisions/0025. | Ferramenta de teste dos cinco pontos pendentes do módulo `app` decidida; escrita dos testes em si segue como pendências próprias em `tasks.md` |
| 0.22.0 | 17-08-2026 | Nenhum ponteiro novo (`tasks.md`/`pitfalls.md`/`analysis.md` já citados). | Primeiros dois testes reais do módulo `app` escritos e rodados (`MainActivity.kt`, NFC; `BleAccessoryService.kt`, Bluetooth) — dois pontos restantes (`ReportFileWriter.kt`/`ReportShareIntent.kt`, `SessionViewModel.kt`) seguem como pendência em `tasks.md` |
| 0.23.0 | 17-08-2026 | Nenhum ponteiro novo (`decisions/0025`/`findings.md`/`tasks.md` já citados). | Caminho antigo de `ReportFileWriter.kt` (Android 7 a 9) confirmado exigir teste instrumentado, mesma categoria de `ReportPdfRenderer.kt` — nota de acompanhamento em `decisions/0025`, pendência ajustada em `tasks.md` |
| 0.24.0 | 17-08-2026 | Nenhum ponteiro novo (`tasks.md`/`analysis.md` já citados). | Terceiro teste real do módulo `app` escrito e rodado (`ReportFileWriter.kt`/`ReportShareIntent.kt`, caminho novo) — só `SessionViewModel.kt` segue como pendência sem aparelho em `tasks.md` |
| 0.25.0 | 17-08-2026 | Nenhum ponteiro novo (`tasks.md`/`analysis.md` já citados). | Tentativa de escrever o teste de `SessionViewModel.kt` revelou que a forma exata de `SessionState` e dos tipos de `content` nunca foi decidida em documento nenhum — pendência nova em `tasks.md`, bloqueando o teste até uma ADR própria resolver isso |
| 0.26.0 | 18-08-2026 | Acrescentado ponteiro para decisions/0026. | Forma exata de `SessionState`, dos tipos de `content` usados pelo `ViewModel`, e do construtor de `SessionViewModel` decidida — desbloqueia o teste de `SessionViewModel.kt`; um ponto (sessão atravessando mais de um tema) ficou de fora, vira pendência nova em `tasks.md` |
| 0.27.0 | 18-08-2026 | Acrescentado ponteiro para decisions/0027. | Quarto e último teste real do módulo `app` escrito e rodado (`SessionViewModel.kt`, dez testes) — divergência real entre a ADR 0026 e o código encontrada ao rodar o teste, corrigida via decisions/0027 (código já estava certo, a ADR que precisava se ajustar) |
| 0.28.0 | 18-08-2026 | Acrescentado ponteiro para decisions/0028. | Combinação do recorte de temas e de eventos numa sessão multi-tema decidida — escrita do código (`sessionEventNames`) e do teste seguem como pendência própria em `tasks.md` |
| 0.29.0 | 18-08-2026 | Nenhum ponteiro novo (decisions/0028 já citada). | `sessionEventNames` implementada e testada (quatro testes) — pendência de código e teste, aberta na versão anterior, resolvida |
| 0.30.0 | 18-08-2026 | Acrescentado ponteiro para decisions/0029. | Responsabilidade pela aparência visual das telas decidida — `concept.md`, `architecture.md` e `tasks.md` passam a apontar pra essa ADR; desenho visual em si segue como pendência própria em `tasks.md` |
| 0.31.0 | 22-08-2026 | Acrescentado ponteiro para decisions/0030. | Padrão de navegação hierárquica (acordeão) entre instância, tema e evento decidido, incluindo o caso de um nível com muitas entradas (resolvido pela busca aproximada já existente); `architecture.md` e `tasks.md` passam a apontar pra essa ADR; pendência nova em `tasks.md` sobre qual ferramenta de tela o módulo `app` usa; desenho visual das demais telas segue como pendência própria em `tasks.md` |
| 0.32.0 | 22-08-2026 | Nenhum ponteiro novo (analysis.md e tasks.md já citados). | Investigação registrada em `analysis.md`: três pendências de tela ("Ponto de início"/"Configuração da sessão" mesma tela; telas físicas do Grupo B; confirmação do botão de pausar) já estavam resolvidas em documentos existentes, nunca conectadas antes; a de Grupo B, desatualizada em `tasks.md` desde 15-08-2026, corrigida em commit separado |
| 0.33.0 | 22-08-2026 | Acrescentado ponteiro para decisions/0031. | Jetpack Compose decidido como ferramenta de desenho de tela do módulo `app`, com fonte oficial pra cada ponto — não decide aparência visual em si, que segue pendente em `tasks.md` |
| 0.36.0 | 22-08-2026 | Nenhum ponteiro novo (concept.md, architecture.md, decisions/0003, 0029 e 0031 já citados). | A mesma frase imprecisa sobre o agrupamento das 17 entradas de tela precisada em cinco lugares, critério corrigido pra decidir entre nota de acompanhamento e edição direta: conta a data (informação já existia antes de o texto ser escrito?), não se a worktree já foi mesclada. `decisions/0003` e `architecture.md`/"Layout" (antes de `decisions/0022` existir) levam nota de acompanhamento; `decisions/0029`, `decisions/0031` e `concept.md` (escritos depois) corrigidos direto — investigação em `analysis.md` |
| 0.37.0 | 27-08-2026 | Nenhum ponteiro novo (`architecture.md`/`findings.md`/`tasks.md` já citados). | `onPauseRequested()` escrito em `SessionViewModel.kt`, fechando o achado sobre a ação de pausar por toque; teste escrito antes do código, suíte completa sem quebra; pendência de desenho visual perde o bloqueio de código sobre o botão de pausar |
| 0.38.0 | 29-08-2026 | Acrescentado ponteiro para decisions/0032. | Gatilho de toque entre os oito estados da tela de jogo decidido, com duas fontes oficiais independentes (Material Design 3, Nielsen Norman Group) |
| 0.39.0 | 30-08-2026 | Acrescentado ponteiro para decisions/0033. | Formato de aparelho (celular primeiro, tablet só com leiaute diferente onde há necessidade real reconhecida) decidido, com fonte oficial (Android Developers) |
| 0.40.0 | 30-08-2026 | Acrescentado ponteiro para decisions/0034. | Mecanismo de carregamento aos poucos do acordeão de navegação decidido, com fonte oficial (Android Developers) |
| 0.41.0 | 30-08-2026 | Acrescentado ponteiro para wireframe.md. | Esqueleto (leiaute) das 16 entradas de tela restantes escrito |
| 0.42.0 | 30-08-2026 | Link pra `wireframe.md` corrigido. | Reorganização de pasta do módulo |
| 0.43.0 | 30-08-2026 | Acrescentado ponteiro para decisions/0035. | Resolução de decisions/0035 |
| 0.44.0 | 30-08-2026 | Acrescentado ponteiro para decisions/0036, `design/prototipo-navegavel.html` e `design/avaliacao-heuristica.md`. | Resolução de decisions/0036 |
| 0.45.0 | 01-09-2026 | Acrescentado ponteiro para decisions/0037 e `SessionGameScreen.kt`. | Resolução de decisions/0037 |
| 0.46.0 | 01-09-2026 | Acrescentado ponteiro para decisions/0038, 0039, 0040, 0041, `app/ui/` e `MotorApp.kt`. | Resolução de decisions/0040 e 0041 |
| 0.47.0 | 02-09-2026 | Acrescentado ponteiro para decisions/0042. | Resolução de decisions/0042 |
| 0.48.0 | 02-09-2026 | Acrescentado ponteiro para decisions/0043. | Resolução de decisions/0043 |
| 0.49.0 | 03-09-2026 | Nenhum ponteiro novo (`findings.md`, `analysis.md`, `tasks.md` e `architecture.md` já citados). | Revisão de PR das telas do motor |
| 0.50.0 | 03-09-2026 | Nenhum ponteiro novo (`findings.md`, `analysis.md`, `tasks.md` e `architecture.md` já citados). | Segunda rodada de revisão de PR das telas do motor |
