# Decisions — Motor

<!-- module-doc-type: decisions-index -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Decisions — Índice |
| Versão | 0.19.0 |
| Data | 18-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Decisões (ADR) específicas deste módulo — um arquivo por decisão,
> numerado (`0001-titulo-curto.md`, `0002-...`).
>
> Não é uma etapa fixa do fluxo do módulo — nasce em qualquer ponto (ao
> decidir o conceito entre alternativas, ao desenhar a arquitetura, ao
> formalizar o schema, ao implementar), sempre que existe uma escolha
> real entre alternativas que precisa ficar registrada com o contexto
> que a motivou.
>
> Cada arquivo começa com um resumo em linguagem simples (porta de
> entrada, não conta como um dos quatro campos fixos), seguido dos
> quatro campos fixos:
> - **Status** — só o estado (`proposto` / `aceito` / `substituído pelo
>   ADR-NNNN`).
> - **Contexto** — o que motivou a decisão, o diagnóstico.
> - **Decisão** — o que foi decidido.
> - **Consequências** — o que foi verificado, o que mudou de fato, o que
>   ficou de fora.
>
> Nunca editado depois de aceito pra revisar a decisão em si — decisão
> que muda gera um ADR novo, que substitui o antigo (o antigo marcado
> como substituído no campo Status, não apagado). Edição depois de
> aceito é permitida só como manutenção que não muda a decisão em si:
> corrigir erro factual, marcar transição de Status, acrescentar nota de
> acompanhamento datada com verificação posterior -- essa nota também
> começa com um resumo simples antes do detalhe técnico.

## Índice

| ADR | Título |
|---|---|
| [0001](0001-linguagem-do-aplicativo.md) | Linguagem do aplicativo |
| [0002](0002-framework-do-firmware-do-acessorio.md) | Framework do firmware do acessório leitor |
| [0003](0003-estrutura-de-modulos-do-aplicativo.md) | Estrutura de módulos do aplicativo |
| [0004](0004-desenho-do-algoritmo-de-busca-aproximada.md) | Desenho do algoritmo de busca aproximada |
| [0005](0005-abordagem-de-teste-do-nucleo-do-motor.md) | Abordagem de teste do núcleo do motor |
| [0006](0006-localizacao-do-projeto-gradle-no-repositorio.md) | Localização do projeto Gradle no repositório |
| [0007](0007-desenho-do-pacote-hierarchy.md) | Desenho do pacote hierarchy |
| [0008](0008-representacao-do-estado-da-sessao.md) | Representação do estado da sessão em curso |
| [0009](0009-calculo-do-recorte-continuo-de-sessao.md) | Cálculo do recorte contíguo de uma sessão |
| [0010](0010-persistencia-do-estado-de-sessao-pausada.md) | Persistência do estado de sessão pausada em disco |
| [0011](0011-formato-de-serializacao-do-estado-de-sessao.md) | Formato de serialização do estado de sessão persistido |
| [0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md) | Versões de plataforma e build do módulo app |
| [0013](0013-desenho-do-pacote-content.md) | Desenho do pacote content |
| [0014](0014-busca-aproximada-com-termo-vazio.md) | Comportamento da busca aproximada com termo vazio |
| [0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md) | Fronteira entre core e app no pacote connectivity |
| [0016](0016-formato-do-identificador-na-notificacao-bluetooth.md) | Formato do identificador de peça na notificação Bluetooth |
| [0017](0017-quem-decide-a-tecnologia-de-leitura.md) | Quem decide a tecnologia de leitura: NFC ou Bluetooth |
| [0018](0018-estrategia-de-permissao-de-bluetooth-e-nfc.md) | Estratégia de permissão de Bluetooth e NFC no aplicativo |
| [0019](0019-mecanismo-de-geracao-guarda-e-compartilhamento-do-relatorio.md) | Mecanismo de geração, guarda e compartilhamento do relatório de sessão |
| [0020](0020-ligacao-entre-leitura-de-peca-e-a-tela.md) | Ligação entre leitura de peça, lógica de sessão e tela |
| [0021](0021-quem-monta-o-texto-de-resumo-e-sintese.md) | Quem monta o texto de resumo e síntese exibido nas telas |
| [0022](0022-conteudo-do-estado-exposto-pelo-viewmodel.md) | Conteúdo do estado exposto pelo ViewModel |
| [0023](0023-geracao-do-relatorio-de-saida-antes-de-apagar-a-sessao.md) | Geração do relatório de saída antes de apagar a sessão pausada |
| [0024](0024-mecanismo-do-gatilho-de-ociosidade.md) | Mecanismo do gatilho de ociosidade |
| [0025](0025-ferramenta-de-teste-do-modulo-app.md) | Ferramenta de teste dos cinco pontos pendentes do módulo app |
| [0026](0026-forma-de-sessionstate-tipos-de-content-e-construtor-do-viewmodel.md) | Forma de SessionState, tipos de content usados pelo ViewModel, e construtor de SessionViewModel |
| [0027](0027-sessionstate-referencia-o-evento-atual-pelo-nome.md) | SessionState referencia o evento atual pelo nome, não guarda a lista da sessão duplicada |
| [0028](0028-combinacao-do-recorte-de-temas-e-eventos-numa-sessao.md) | Combinação do recorte de temas e de eventos numa sessão que atravessa mais de um tema |

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
deste documento (ADR nova acrescentada à lista, por exemplo) sobe a
versão (SemVer) e ganha uma linha nova aqui, junto com o campo Versão
da tabela de cabeçalho, que sempre reflete a última linha desta
tabela. Esta versão é do próprio índice -- cada ADR individual não
leva versão própria, o histórico dela é o campo Status (substituído
pelo ADR-NNNN). -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial: ADRs 0001 e 0002. | Criação inicial |
| 0.2.0 | 13-08-2026 | ADR 0003 acrescentada à lista. | Resolução da pendência de estrutura de pastas do Android |
| 0.3.0 | 13-08-2026 | ADRs 0004 e 0005 acrescentadas à lista. | Desenho e primeira implementação do pacote `search` |
| 0.4.0 | 14-08-2026 | ADR 0006 acrescentada à lista. | Decisão sobre onde o projeto Gradle mora no repositório |
| 0.5.0 | 14-08-2026 | ADR 0007 acrescentada à lista. | Desenho do pacote `hierarchy` |
| 0.6.0 | 14-08-2026 | ADR 0008 acrescentada à lista. | Representação do estado da sessão em curso, primeiro ponto de desenho do pacote `session` |
| 0.7.0 | 14-08-2026 | ADR 0010 acrescentada à lista (ADR 0009 ainda como `proposto`, aguardando revisão, por isso ainda fora deste índice). | Persistência do estado de sessão pausada em disco, último ponto de desenho do pacote `session` |
| 0.8.0 | 14-08-2026 | ADR 0009 acrescentada à lista, depois de revisão confirmada. | Cálculo do recorte contíguo de uma sessão |
| 0.9.0 | 14-08-2026 | ADR 0011 acrescentada à lista. | Formato de serialização (JSON, `kotlinx.serialization`) do estado de sessão persistido |
| 0.10.0 | 14-08-2026 | ADRs 0012 e 0013 acrescentadas ao índice, retroativamente — já existiam como arquivo desde a tarefa que as criou, mas nunca tinham entrado nesta lista. | Checagem mecânica ao abrir este arquivo pra acrescentar a ADR 0014 |
| 0.11.0 | 14-08-2026 | ADR 0014 acrescentada à lista. | Formalização do comportamento da busca aproximada com termo vazio |
| 0.12.0 | 14-08-2026 | ADRs 0015 e 0016 acrescentadas à lista. | Fronteira core/app e formato de dado do pacote `connectivity` |
| 0.13.0 | 14-08-2026 | ADR 0017 acrescentada à lista. | Estratégia de permissão de Bluetooth e NFC do pacote `connectivity` |
| 0.14.0 | 14-08-2026 | ADR 0017 renomeada de "Estratégia de permissão..." para "Quem decide a tecnologia de leitura" (o assunto real dela); ADR 0018 acrescentada à lista, herdando o título e o assunto de permissão que a 0017 tinha antes. | Correção de escopo: a automação de escolha entre NFC e Bluetooth foi questionada e revertida antes do commit inicial da 0017 |
| 0.15.0 | 15-08-2026 | ADRs 0019, 0020, 0021 e 0022 acrescentadas ao índice, as três primeiras retroativamente — já existiam como arquivo, mas nunca tinham entrado nesta lista. | Checagem mecânica ao abrir este arquivo pra acrescentar a ADR 0022 |
| 0.16.0 | 16-08-2026 | ADRs 0023 e 0024 acrescentadas ao índice, retroativamente — já existiam como arquivo, mas nunca tinham entrado nesta lista. ADR 0025 acrescentada. | Checagem mecânica ao abrir este arquivo pra acrescentar a ADR 0025 |
| 0.17.0 | 18-08-2026 | ADR 0026 acrescentada à lista. | Formalização da forma de `SessionState`, dos tipos de `content` usados pelo `ViewModel`, e do construtor de `SessionViewModel` |
| 0.18.0 | 18-08-2026 | ADR 0027 acrescentada à lista. | Correção da forma de `SessionState` (decisão 2 de decisions/0026), achada ao rodar o teste de `SessionViewModel.kt` contra o código real |
| 0.19.0 | 18-08-2026 | ADR 0028 acrescentada à lista. | Combinação do recorte de temas e de eventos numa sessão que atravessa mais de um tema |
