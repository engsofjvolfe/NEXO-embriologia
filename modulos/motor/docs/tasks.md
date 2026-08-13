# Tasks — Motor

<!-- module-doc-type: tasks -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Tasks |
| Versão | 0.1.0 |
| Data | 12-08-2026 |
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
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.
- `PD-NAV` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.4.

## Índice
- [Em aberto](#em-aberto)
- [Resolvidas](#resolvidas)
- [Controle de versão](#controle-de-versão)

## Em aberto

- [ ] **Escrever o código-fonte de cada componente já desenhado.**

      *Resumo simples:* o firmware do acessório leitor e a lógica do
      aplicativo (importação de conteúdo, busca aproximada) ainda não
      têm nenhuma linha escrita — só o desenho de como devem ser.

      *Detalhe técnico:* cobre o firmware do acessório (PN532 +
      ESP32-D0WD-V3, em C++/Arduino via PlatformIO — ver
      [decisions/0002](<../decisions/0002-framework-do-firmware-do-acessorio.md>))
      e, no aplicativo (Kotlin — ver
      [decisions/0001](<../decisions/0001-linguagem-do-aplicativo.md>)),
      a lógica de importação e validação do pacote de conteúdo e a
      lógica de busca aproximada. Origem:
      [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
      seção 9.

- [ ] **Decidir a estrutura exata de pastas do projeto Android.**

      *Resumo simples:* `architecture.md` já diz que o código do
      aplicativo é Kotlin, mas não desce ao nível de quais pastas e
      pacotes existem dentro do projeto.

      *Detalhe técnico:* ver `architecture.md`, seção
      "Aparelho de jogo (aplicativo)".

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
      seção 8.

## Resolvidas

- [x] **Escolher a linguagem de programação do aplicativo.** Resolvido
      — ver
      [decisions/0001-linguagem-do-aplicativo.md](<../decisions/0001-linguagem-do-aplicativo.md>).
- [x] **Escolher a linguagem e o framework do firmware do acessório
      leitor.** Resolvido — ver
      [decisions/0002-framework-do-firmware-do-acessorio.md](<../decisions/0002-framework-do-firmware-do-acessorio.md>).

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
