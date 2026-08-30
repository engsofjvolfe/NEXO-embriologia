# Wireframe — Motor

<!-- module-doc-type: wireframe -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Wireframe |
| Versão | 0.1.0 |
| Data | 30-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Esqueleto de cada tela do motor — onde cada botão, texto e campo vai,
> sem cor nem fonte ainda (passo 2 do método fixado em
> [architecture.md, Interface](architecture.md#interface)). Deriva só
> do que já está decidido: a tabela DA-RET
> ([Projeto Arquitetônico](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
> seção 6.6), [decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>)
> (conteúdo da tela de jogo), [decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>)
> (acordeão), [decisions/0032](<../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md>)
> (gatilho de toque), [decisions/0033](<../decisions/0033-formato-de-aparelho-leiaute-responsivo.md>)
> (formato de aparelho) e [decisions/0034](<../decisions/0034-mecanismo-de-carregamento-preguicoso-do-acordeao-de-navegacao.md>)
> (`LazyColumn`). Nenhum campo novo é inventado aqui — só posição.
>
> Cada seção segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

## Índice
- [Formato de referência](#formato-de-referência)
- [Tela de jogo — posição dos elementos comuns](#tela-de-jogo--posição-dos-elementos-comuns)
- [Sessão pausada (DA-RET-01)](#sessão-pausada-da-ret-01)
- [Navegação (DA-RET-02)](#navegação-da-ret-02)
- [Ponto de início / Configuração da sessão (DA-RET-03/04)](#ponto-de-início--configuração-da-sessão-da-ret-0304)
- [Resultado / relatório (DA-RET-14)](#resultado--relatório-da-ret-14)
- [Importar conteúdo (DA-RET-16)](#importar-conteúdo-da-ret-16)
- [Consentimento (DA-RET-17)](#consentimento-da-ret-17)
- [Controle de versão](#controle-de-versão)

## Formato de referência

*Em resumo:* toda tela abaixo é desenhada primeiro para celular
retrato — essa é sempre a versão completa. O tablet só muda de leiaute
onde `decisions/0033` já reconheceu necessidade real (Configuração da
sessão); nas demais, o tablet mostra o mesmo conteúdo do celular,
centralizado numa coluna com margens, sem informação nova — por isso
as outras telas abaixo não repetem uma versão tablet própria.

## Tela de jogo — posição dos elementos comuns

*Em resumo:* a tela de jogo já é uma única tela (`SessionScreen`,
[decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>)),
com conteúdo e gatilho de toque já fechados
([decisions/0032](<../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md>)).
Falta só onde, na tela, cada controle fica.

*Em detalhe técnico:*

- **Controle de sair** (abre `DA-RET-15`) — canto superior esquerdo,
  texto discreto "Sair", presente em todas as nove situações da tela
  de jogo (as oito de `SessionScreen` mais o diálogo de confirmação
  sobreposto). Fica sempre no mesmo lugar entre uma situação e outra,
  pra não exigir reaprender a posição a cada troca de conteúdo.
- **Controle de pausar** (`onPauseRequested()`, já escrito — ver
  [findings.md](<findings.md#2026-08-27-sessionviewmodel-ganha-onpauserequested>))
  — canto superior direito, ícone (sem texto, por não exigir
  confirmação — `EI-PAU-03` só exige isso pra sair). Não aparece
  durante a Confirmação de saída (`DA-RET-15`), já que o diálogo
  sobreposto ocupa esse momento.
- **Indicador de conexão do acessório** (`ConnectionState`, ver
  [architecture.md, pacote `connectivity`](<architecture.md#pacote-connectivity--desenho-interno>))
  — só na tela "Aguardando tentativa" (`DA-RET-06`, o único estado com
  o campo `connectionState` em `SessionScreen`). Um texto curto, canto
  superior direito, abaixo do controle de pausar: "● conectado",
  "◐ procurando" ou "○ desconectado" — as três variantes de
  `ConnectionState`, nunca simultâneas, sem ícone colorido nem
  destaque, pra respeitar o Documento de Conceito, seção 8 ("a
  tela... confirma, não anuncia"). Quando o campo é nulo (aparelho lê
  NFC direto, sem acessório em uso), o indicador simplesmente não
  aparece — não é um quarto estado, é ausência de estado.
- **Aviso de NFC/Bluetooth desligado no aparelho** — mesma posição do
  indicador de conexão, substituindo-o quando aplicável (nunca os dois
  ao mesmo tempo): um texto igualmente discreto, ex. "NFC desligado" ou
  "Bluetooth desligado", só quando a leitura correspondente está
  indisponível por estar desligada no sistema, não por falha de
  leitura. Mesmo limite do Conceito, seção 8: aviso, nunca instrução
  ("vá em Configurações e ligue") — a pessoa decide o que fazer com a
  informação.
- **Botão "Pular peça"** — já decidido como sempre presente durante
  `AwaitingAttempt` e `StudySuggestionShown`
  ([decisions/0032](<../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md>),
  itens 2 e 6) — canto inferior esquerdo, sem destaque em
  `AwaitingAttempt` (RF-PUL-02, sistema não recomenda), com destaque
  normal em `StudySuggestionShown` (é a alternativa real apontada ali).
- **Botão "Continuar"** (`EventSummary`, `SkipMessageShown`) — parte
  inferior da tela, ocupando a largura útil; texto muda pra "Ver
  resultado" quando `hasNextEvent` é falso
  ([decisions/0022](<../decisions/0022-conteudo-do-estado-exposto-pelo-viewmodel.md>),
  ponto 2).
- **Conteúdo central** (imagem de referência, texto de confirmação,
  dica, síntese) — ocupa o espaço entre os controles acima, sempre
  centralizado.

## Sessão pausada (DA-RET-01)

*Em resumo:* única tela quando existe sessão pausada — bloqueia
qualquer sessão nova até a pessoa escolher (`EI-NAV-01`).

*Em detalhe técnico:* conteúdo centralizado verticalmente: nome do
evento em que a sessão parou, seguido de dois botões empilhados —
"Retomar" (destaque) e "Sair da sessão" (sem destaque). Nenhum outro
elemento na tela; nenhuma barra de navegação, porque não há nada além
dessa escolha nesse momento (`RF-PAU-05`).

## Navegação (DA-RET-02)

*Em resumo:* lista única, em acordeão
([decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>)),
renderizada como uma `LazyColumn` achatada
([decisions/0034](<../decisions/0034-mecanismo-de-carregamento-preguicoso-do-acordeao-de-navegacao.md>)).

*Em detalhe técnico:* campo de busca fixo no topo (`DA-NAV-02`), sempre
visível mesmo com a lista rolada; abaixo dele, a `LazyColumn` com um
item por instância — tocar expande os temas dela logo abaixo, no
mesmo lugar, sem esconder as outras instâncias; tocar num tema expande
os eventos dele do mesmo jeito. Cada item mostra só o nome; a
indicação de "aberto/fechado" (ícone, cor) é aplicação do sistema
visual, fora deste documento. Ao tocar num evento (ou tema) que faz
parte de um grupo com ordem, a escolha de até onde a sessão vai
aparece encaixada logo abaixo do próprio item tocado, nunca em tela
separada ([decisions/0030](<../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md>)
item 3) — expande no mesmo lugar que o resto do acordeão, ocupando a
posição que o "próximo nível" teria. Nenhuma barra de navegação
superior além do campo de busca.

## Ponto de início / Configuração da sessão (DA-RET-03/04)

*Em resumo:* já é uma única tela (`EI-NAV-05`) — aparece uma vez, no
início da sessão, antes da primeira tentativa. É a única tela que
ganha leiaute de tablet diferente
([decisions/0033](<../decisions/0033-formato-de-aparelho-leiaute-responsivo.md>)).

*Em detalhe técnico, celular:* rolagem única, de cima pra baixo:
1. "Começar em:" seguido de uma lista de opções (botão de rádio) — uma
   por posição já cadastrada no evento de entrada, primeira marcada
   por padrão (`EI-SES-02`). Só aparece se o evento de entrada tem mais
   de uma posição.
2. Um bloco por evento dentro do alcance escolhido da sessão
   (`EI-SES-08`): nome do evento, alternador "Pular disponível", campo
   "limiar de erro — dica", campo "limiar de erro — sugestão de
   estudo" (os dois só se a dica estiver habilitada nesse evento).
3. Um único campo "Tempo de ociosidade", fora dos blocos por evento —
   vale pra sessão inteira, não se repete (`EI-NAV-05`).
4. Botão "Iniciar sessão", fixo na parte inferior da tela.

*Em detalhe técnico, tablet (leiaute próprio):* duas colunas dentro da
mesma tela — à esquerda, a lista dos eventos dentro do alcance da
sessão (nomes, sem os campos); à direita, os campos do evento
selecionado na lista (alternador de pular, limiares de dica/estudo).
A escolha de posição de início e o campo de tempo de ociosidade ficam
acima das duas colunas, sempre visíveis; o botão "Iniciar sessão" fica
abaixo delas. A pessoa ajusta um evento de cada vez sem perder de vista
quais já foram configurados — perfil mais próximo do padrão "painel de
apoio" que do "lista-detalhe" (o formulário de configuração só faz
sentido junto da lista de eventos que ele configura — ver
[decisions/0033](<../decisions/0033-formato-de-aparelho-leiaute-responsivo.md>),
Consequências).

## Resultado / relatório (DA-RET-14)

*Em resumo:* mostrada ao final de toda sessão, e continua acessível
depois, fora de sessão, sem login (`EI-REG-06`).

*Em detalhe técnico:* topo — três números lado a lado: erros, pulos,
pausas, contados a partir do registro da sessão. Abaixo, três linhas
de ação, empilhadas: "Exportar CSV", "Exportar PDF", "Compartilhar"
(este último monta o `Intent` de `ReportShareIntent.kt`, já escrito).
Na base da tela, "Voltar à navegação" — único jeito de sair dessa
tela quando ela foi aberta a partir da navegação livre (fora de
sessão); quando aberta ao final de uma sessão de verdade, essa mesma
ação leva de volta pra Navegação (`DA-RET-02`), não pra sessão que já
terminou.

## Importar conteúdo (DA-RET-16)

*Em resumo:* usa o seletor de arquivo padrão do Android
(`DA-IMP-04`) — a tela do motor em si só mostra o resultado.

*Em detalhe técnico:* botão único, "Selecionar arquivo (.zip)", no
topo — abre o seletor do próprio sistema, fora do controle deste
desenho. Abaixo, uma área de resultado, vazia até a pessoa escolher um
arquivo: depois de escolhido, mostra "Pacote aceito" (regra tudo ou
nada — [decisions/0013](<../decisions/0013-desenho-do-pacote-content.md>))
ou a lista completa de violações encontradas, cada uma com o item
recusado e o motivo (`DA-CFG-03`).

## Consentimento (DA-RET-17)

*Em resumo:* só aparece antes de registrar dado que identifique a
pessoa (`EI-REG-03`) — dado de jogo em si nunca depende dessa tela.

*Em detalhe técnico:* texto explicativo (conteúdo legal exato fora do
escopo da cascata do motor — Projeto Arquitetônico, §2.2) ocupando a
maior parte da tela, rolável se não couber. Abaixo dele, uma caixa de
marcar "Li e concordo". O botão "Continuar" fica desabilitado até a
caixa ser marcada — único controle da tela além dela.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 30-08-2026 | Criação inicial: esqueleto das 16 entradas de tela restantes (elementos comuns da tela de jogo — sair, pausar, indicador de conexão, aviso de rádio desligado, pular, continuar — e as 6 telas de navegação de fato). | Resolução da pendência "Desenhar o esqueleto das 16 entradas de tela restantes" |
