# 0045 — Gatilho de Consentimento e de Importar conteúdo no encadeamento do `MotorApp`

Resumo em linguagem simples: `ConsentScreen` e `ImportContentScreen` já existem, prontas e
testadas, mas nenhum botão do aplicativo leva até elas — ficam soltas, fora do caminho que a
pessoa realmente percorre ao usar o motor. Esta ADR decide, pra cada uma, a partir de qual outra
tela ela é alcançada, e o que acontece depois. Resumo da decisão: "Importar conteúdo" ganha um
botão sempre disponível na tela de Navegação, sem depender de nenhuma sessão em curso; já
"Consentimento" aparece sempre que a pessoa está prestes a iniciar uma sessão (nunca antes disso),
a não ser que ela já tenha escolhido, numa vez anterior, lembrar a própria decisão — escolha que
nunca é automática (nenhuma caixa vem pré-marcada) e que continua sempre visível e reversível na
tela de configuração, nunca escondida.

Convenção dos códigos citados abaixo:
- `EI-REG` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.13.
- `DA-IMP` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.4.
- `DA-RET` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.6.

**Status:** aceito

**Contexto:**

`architecture.md`, seção "Ponto de entrada real (MotorApp)", registra que `ConsentScreen` e
`ImportContentScreen` "não têm gatilho real ainda... nenhuma tela do encadeamento leva até elas" —
pendência espelhada em `tasks.md`. O que cada uma mostra já está fixado, sem alternativa em
aberto: `DA-RET-16` (Importar conteúdo, o seletor de arquivo padrão do Android, sem exigir
autenticação nem administração central — `DA-IMP-04`) e `DA-RET-17` (Consentimento, exibido antes
de registrar qualquer dado que identifique a pessoa — `EI-REG-03`), ambas também já com o esqueleto
de tela fechado em `design/wireframe.md`. O que falta, e que nenhum documento decide ainda, é de
qual outra tela cada uma é alcançada e o que acontece ao voltar — o mesmo nível de decisão que
`decisions/0040` já resolveu para as demais quinze telas (tipo fechado `AppScreen`, guardado em
estado local, cada transição uma atribuição direta dentro do retorno que a própria tela expõe).

Levantamento de boas práticas, feito antes de decidir o gatilho de Consentimento — por envolver
dado de identificação de pessoa, é o ponto com mais risco de escolha ruim:

- **A lei não exige repetir o pedido a cada sessão.** Lei nº 13.709/2018 (LGPD), art. 8º, §5º,
  texto obtido direto da fonte oficial (Presidência da República): "o consentimento pode ser
  revogado a qualquer momento mediante manifestação expressa do titular, por procedimento gratuito
  e facilitado, ratificados os tratamentos realizados sob amparo do consentimento anteriormente
  manifestado enquanto não houver requerimento de eliminação" (BRASIL, 2018, art. 8º, §5º). Ou
  seja: um consentimento já dado continua valendo — a lei não obriga pedir de novo a cada sessão —,
  mas revogá-lo precisa continuar sempre fácil e sem custo, a qualquer momento.
- **Nunca pré-marcado, nunca genérico.** Mesmo artigo, caput e §4º: o consentimento "deverá ser
  fornecido por escrito ou por outro meio que demonstre a manifestação de vontade do titular", e
  "autorizações genéricas para o tratamento de dados pessoais serão nulas" (BRASIL, 2018, art. 8º).
  Cada escolha — concordar com os termos, e (nova, ver Decisão) lembrar essa escolha depois —
  precisa ser um ato explícito e específico da pessoa, nunca inferido nem assumido por padrão.
- **Pedir no momento certo, não de antemão.** Levantamento da Nielsen Norman Group sobre pedido de
  permissão em aplicativo móvel: pedir no momento em que a pessoa está prestes a usar a
  funcionalidade que depende daquele dado dá contexto pro pedido e aumenta a chance de
  entendimento — pedir de antemão, fora de contexto, tende a ser visto como oportuno ou confuso
  (NIELSEN NORMAN GROUP, [s.d.]a). Mesmo princípio já usado neste módulo pra permissão de
  Bluetooth, pedida "no momento em que uma sessão de jogo está prestes a começar... nunca de
  antemão" (`decisions/0018`).
- **Controle sempre visível, nunca escondido em página extra.** Levantamento da mesma fonte sobre
  banners de consentimento de cookie: as opções devem ficar "imediatamente disponíveis, em vez de
  forçar a pessoa a clicar em páginas extras" (NIELSEN NORMAN GROUP, [s.d.]b) — orienta tanto o
  botão de Importar conteúdo (sempre visível na Navegação, nunca dentro de um submenu) quanto o
  lembrete da escolha de consentimento (ver Decisão), que precisa ficar à vista, não enterrado numa
  tela de ajustes separada.
- **Reversível sem repetir todo o diálogo.** Heurística de usabilidade "controle e liberdade do
  usuário" (Nielsen, já a base da avaliação heurística deste módulo — `decisions/0036`): a pessoa
  precisa conseguir corrigir ou voltar atrás de uma escolha já feita sem precisar refazer um
  diálogo inteiro (NIELSEN NORMAN GROUP, [s.d.]c) — sustenta o lembrete como caminho de volta pra
  `ConsentScreen`, em vez de essa escolha virar definitiva.

**Decisão:**

1. **Importar conteúdo é alcançado por um botão próprio, sempre visível, na tela de Navegação
   (`AppScreen.Navigation`)** — ao lado do campo de busca já fixado em `wireframe.md`, nunca
   escondido atrás de outro menu. Disponível sempre, independente de existir ou não conteúdo já
   importado, e independente de qualquer sessão em curso (a tela de Navegação só é alcançada,
   por `EI-NAV-01`, quando não há sessão pausada). Ao terminar (pacote aceito ou lista de
   violações, já decidido em `wireframe.md` e `decisions/0013`), a pessoa volta pra
   `AppScreen.Navigation` por um controle "Voltar", nunca uma saída automática. Este ponto não
   depende de nenhuma pesquisa externa — é aplicação direta de `DA-IMP-04` ("sem exigir... qualquer
   autenticação... não existe papel de administração restringindo essa ação") e do mesmo princípio
   de controle sempre visível citado acima.

2. **Consentimento é alcançado a partir da tela de Configuração da sessão
   (`AppScreen.Configuration`), no instante em que "Iniciar sessão" é escolhido** — nunca antes
   disso (nunca na abertura do aplicativo, nunca embutido em outra tela) — aplicação direta do
   princípio de pedir no momento certo, citado acima, e do próprio `EI-REG-03` ("antes de registrar
   qualquer dado que identifique a pessoa"): é exatamente ali, ao iniciar a sessão que vai gerar o
   registro, que esse dado passaria a ser coletado.

3. **A tela só é mostrada de fato quando não existe, ainda, uma escolha lembrada.** Existindo uma
   escolha já lembrada (ver item 4), "Iniciar sessão" leva direto a `AppScreen.Game`, sem passar
   por `AppScreen.Consent` — mas `AppScreen.Configuration` passa a exibir, sempre visível, um
   lembrete curto e não bloqueante da escolha em vigor (por exemplo: "Identificação: ativada —
   toque pra rever o consentimento" ou "Identificação: desativada — toque pra rever"), que leva de
   volta pra `AppScreen.Consent` a qualquer momento, sem exigir passar de novo pela sessão inteira.
   Esse lembrete é o que garante, ao mesmo tempo, a exigência legal de revogação "gratuita e
   facilitada, a qualquer momento" (LGPD, art. 8º, §5º) e a heurística de controle e liberdade do
   usuário citadas acima — nunca uma escolha que desaparece de vista depois de feita uma vez.

4. **Lembrar a escolha é, em si, uma decisão explícita e separada — nunca implícita em concordar
   com os termos.** `ConsentScreen` ganha, junto da caixa já decidida em `wireframe.md` ("Li e
   concordo", que continua desmarcada por padrão, controlando o botão "Continuar"), uma segunda
   caixa independente, também desmarcada por padrão: "Lembrar minha escolha nas próximas sessões".
   As duas caixas nunca se pré-marcam uma à outra. Sem marcar a segunda, a tela volta a aparecer a
   cada nova sessão (comportamento padrão, sem lembrete nenhum em `AppScreen.Configuration`, porque
   não há escolha alguma lembrada pra mostrar). Aplicação direta do art. 8º, caput e §4º, da LGPD
   (manifestação de vontade explícita; proibição de autorização genérica) e da diretriz já citada
   contra pré-marcar caixa de consentimento.

5. **A escolha lembrada (marcou "Li e concordo": sim/não; marcou "lembrar": sim/não) é guardada com
   Jetpack DataStore (Preferences DataStore)** — a forma hoje recomendada pela própria documentação
   oficial do Android pra um dado pequeno de chave-valor, em substituição a `SharedPreferences`
   (GOOGLE, [s.d.]). Guardada inteiramente dentro do módulo `app`, nunca em `core` — mesma divisão
   de responsabilidade já fixada em `decisions/0010` pra persistência de sessão pausada (`core` não
   ganha dependência nova do Android; quem decide o mecanismo de guarda de verdade é sempre `app`),
   ainda mais direta aqui: essa escolha nunca foi, em nenhum momento, um dado de sessão de jogo —
   é preferência de interface sobre quando mostrar uma tela, então nunca teve motivo pra entrar em
   `core`. Nenhum outro dado (nome, papel, ou qualquer dado que de fato identifique a pessoa) é
   guardado por este mecanismo — nenhum campo desse tipo existe ainda em `ConsentScreen` nem em
   nenhuma outra tela do motor; permanece de fora do escopo desta ADR.

API pública, esboço (mesma postura de esboço já usada em `decisions/0010` — formato exato de
arquivo interno do `DataStore` fica pra implementação):

```
app/ui/
  ConsentPreference.kt   data class RememberedConsent(val given: Boolean)
                         suspend fun saveConsentChoice(context: Context, choice: RememberedConsent?)
                         fun consentChoiceFlow(context: Context): Flow<RememberedConsent?>
```

`saveConsentChoice(context, null)` apaga a escolha lembrada (equivalente a nunca ter marcado
"lembrar") — mesmo caminho usado pela revogação a qualquer momento, citada acima (LGPD, art. 8º,
§5º): tocar no lembrete em `AppScreen.Configuration` leva de volta a `AppScreen.Consent`; marcar
"Li e concordo" sem marcar "lembrar" ali chama `saveConsentChoice(context, null)`.

**Consequências:**

Encadeamento revisado (acréscimo às linhas já fixadas em `decisions/0040`, `architecture.md`,
"Ponto de entrada real"):

```
AppScreen.Navigation     escolher um item -> AppScreen.Configuration
                          "Importar conteúdo" -> AppScreen.ImportContent
AppScreen.ImportContent  "Voltar" -> AppScreen.Navigation
AppScreen.Configuration  "Iniciar sessão", sem escolha lembrada -> AppScreen.Consent
                          "Iniciar sessão", com escolha lembrada -> AppScreen.Game
                          toque no lembrete de consentimento -> AppScreen.Consent
AppScreen.Consent        "Continuar" (com "Li e concordo" marcado) -> AppScreen.Game
```

Dependência nova em `gradle/libs.versions.toml`, só no módulo `app`:
`androidx.datastore:datastore-preferences`. `wireframe.md` ganha, na mesma tarefa que esta ADR, a
segunda caixa de `ConsentScreen` e o lembrete de `SessionConfigurationScreen` — os dois elementos
novos que esta decisão introduz e que ainda não tinham posição fixada. `architecture.md` é
atualizado para refletir o encadeamento acima e apontar pra esta ADR, em vez de listar Consentimento
e Importar conteúdo como sem gatilho.

Dois pontos ficam explicitamente fora do alcance desta ADR, sem que a resolução do gatilho os
resolva de tabela: (1) o pacote de conteúdo aceito por `ImportContentScreen` ainda não é persistido
nem aparece na lista de `AppScreen.Navigation` — depende da pendência já registrada em `tasks.md`
("Decidir onde o conteúdo importado fica guardado"); (2) nenhum campo de nome ou papel existe ainda
em `ConsentScreen` — o dado que `EI-REG-03` descreve continua sem um formulário que o colete,
questão separada da de quando a tela aparece.

## Referências

Fontes externas consultadas para embasar esta decisão, no formato definido pela norma ABNT NBR 6023
(Informação e documentação — Referências). Citadas no corpo do documento como (ENTIDADE, ano).

BRASIL. **Lei nº 13.709, de 14 de agosto de 2018**. Lei Geral de Proteção de Dados Pessoais (LGPD),
art. 8º. Brasília, DF: Presidência da República, 2018. Disponível em:
https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm. Acesso em: 07 set. 2026.

GOOGLE. **Data layer — DataStore**. App architecture, Android Developers, [s.d.]. Disponível em:
https://developer.android.com/topic/libraries/architecture/datastore. Acesso em: 07 set. 2026.

NIELSEN NORMAN GROUP. **3 Design Considerations for Effective Mobile-App Permission Requests**,
[s.d.]a. Disponível em: https://www.nngroup.com/articles/permission-requests/. Acesso em: 07 set.
2026.

NIELSEN NORMAN GROUP. **Cookie Permissions 101**, [s.d.]b. Disponível em:
https://www.nngroup.com/articles/cookie-permissions/. Acesso em: 07 set. 2026.

NIELSEN NORMAN GROUP. **User Control and Freedom (Usability Heuristic #3)**, [s.d.]c. Disponível
em: https://www.nngroup.com/articles/user-control-and-freedom/. Acesso em: 07 set. 2026.
