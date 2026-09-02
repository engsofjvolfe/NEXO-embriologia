# 0040 — Mecanismo de navegação entre as telas do módulo `app`

Resumo em linguagem simples: com as sete telas de navegação e a tela de jogo já escritas, faltava
decidir como o aplicativo troca de uma pra outra de verdade — usando a biblioteca oficial do
Android pra isso (Jetpack Navigation Compose) ou um mecanismo mais simples, só com o próprio
Compose. Esta ADR decide: mecanismo simples (um tipo fechado guardado num estado local, sem
biblioteca nova) — a biblioteca oficial resolve problemas que este módulo não tem hoje (pilha de
retrocesso complexa, link direto de fora do aplicativo, animação de transição entre telas), e
introduzir ela agora, antes do fluxo real de conteúdo existir, arriscaria refazer tudo quando essa
pendência for resolvida.

**Status:** aceito

**Contexto:**

As sete telas de navegação e a tela de jogo (`SessionGameScreen`) já existem, cada uma testada
isoladamente. Faltava um Composable que decide qual delas mostrar a cada momento, e troca entre
elas conforme a pessoa toca em cada controle — nenhum documento da cascata, nem nenhuma ADR já
aceita, decide esse mecanismo.

Duas alternativas reais, confirmadas em fonte oficial (Android Developers, acesso em 01 set.
2026):

1. **Jetpack Navigation Compose** (biblioteca oficial) — a própria documentação a recomenda como
   padrão pra "qualquer aplicativo real", com pilha de retrocesso automática, suporte a link
   direto (*deep link*) e animação de transição entre destinos prontos.
2. **Estado local simples** (alternativa escolhida, ver Decisão) — um tipo fechado (`sealed
   interface`) guardado em `remember { mutableStateOf(...) }`, sem biblioteca nova.

Nenhum dos três motivos que a fonte oficial lista pra preferir a biblioteca se aplica a este ponto
específico da implementação, hoje:

- **Pilha de retrocesso complexa:** o motor já é "inteiramente linear: nunca existe mais de uma
  sessão ativa ou pausada ao mesmo tempo" (Documento de Conceito, seção 12; RF-PAU-05) — cada tela
  já sabe, sozinha, pra onde voltar (por exemplo, "Voltar à navegação" no Resultado), sem precisar
  de uma pilha genérica de histórico.
- **Link direto (deep link):** nenhum documento da cascata, em nenhuma etapa, menciona abrir o
  aplicativo direto numa tela específica a partir de fora dele.
- **Animação de transição:** nenhum documento (wireframe, decisions/0035) pede uma animação
  específica entre telas.

Além disso, boa parte das telas ligadas por este mecanismo hoje mostra dado de exemplo fixo, não
dado real — depende de uma pendência maior, ainda em aberto (onde o conteúdo importado fica
guardado no aparelho, pendência registrada em tasks.md). Introduzir uma biblioteca de navegação
completa agora, antes dessa pendência ser resolvida, arriscaria descrever destinos, argumentos e
grafo de navegação que teriam que ser refeitos assim que o fluxo real de conteúdo existir.

**Decisão:**

1. `app/ui/MotorApp.kt` guarda a tela atual num tipo fechado (`sealed interface AppScreen`),
   dentro de `remember { mutableStateOf(...) }` — sem `androidx.navigation`, sem grafo de
   navegação declarado.
2. Cada transição é uma atribuição direta (`current = AppScreen.X`) dentro do próprio callback que
   a tela já expõe (`onEntryClicked`, `onStartSessionRequested`, `onContinueRequested`, etc.) —
   nenhuma tela conhece a existência de outra, só devolve "aconteceu isso", quem decide o destino
   é sempre `MotorApp`.
3. Revisável, sem quebrar nenhuma tela já escrita (cada uma continua recebendo só dado e
   retornando eventos por função, sem depender de como é chamada), quando o fluxo real de
   conteúdo existir e a necessidade de pilha de retrocesso, link direto ou animação aparecer de
   verdade — nenhuma das três é hipotética descartada pra sempre, só adiada até existir motivo
   concreto.

**Consequências:**

Nenhuma dependência nova declarada em `gradle/libs.versions.toml`. `app/ui/MotorApp.kt` passa a
existir, testável (`MotorAppTest.kt`, decisions/0037), cobrindo o ponto de entrada real
(`EI-NAV-01`/`EI-NAV-02`) e o encadeamento entre as telas já escritas.

## Referências

Fonte externa consultada para embasar esta decisão, no formato definido pela norma ABNT NBR 6023
(Informação e documentação — Referências). Citada no corpo do documento como (GOOGLE, ano).

GOOGLE. **Principles of navigation**. Android Developers, [s.d.]. Disponível em:
https://developer.android.com/guide/navigation/design. Acesso em: 01 set. 2026.
