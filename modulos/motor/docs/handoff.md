# Handoff — Motor

<!-- module-doc-type: handoff -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Handoff |
| Versão | 0.18.0 |
| Data | 15-08-2026 |
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
