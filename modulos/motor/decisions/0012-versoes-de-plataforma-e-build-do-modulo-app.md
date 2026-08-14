# 0012 — Versões de plataforma e build do módulo app

Resumo em linguagem simples: fixa quatro números que o módulo `app`
(a parte do aplicativo com tela) precisa pra existir de verdade — a
versão mais antiga de Android que ele ainda roda, a versão de Android
que ele usa como alvo, e as versões das duas ferramentas que compilam
tudo isso (o "Android Gradle Plugin" e o Gradle já em uso pelo
projeto). Nenhum desses números tinha sido decidido antes, porque até
agora só existia o módulo `core`, que não depende de nada disso.

Convenção dos códigos citados abaixo:
- `PD-CON` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.2.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado no [Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
seção 6.3.3, e em
[decisions/0003](<0003-estrutura-de-modulos-do-aplicativo.md>). Todo
trecho abaixo marcado descreve a política atual de um terceiro (Google
Play, ou o próprio Android) — não uma decisão deste projeto, e sujeita
a mudar todo ano. Quem ler este documento depois deve tratar esse
conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra qualquer
decisão nova.

**Status:** aceito

**Contexto:** [decisions/0006](<0006-localizacao-do-projeto-gradle-no-repositorio.md>)
já tinha fixado onde o projeto Gradle mora; faltava decidir com o que
ele compila. Um projeto Android real exige quatro números que nenhum
documento da cascata nem `architecture.md` desce a esse nível de
detalhe: a versão de Android mais antiga que o aplicativo aceita
rodar (`minSdk`), a versão usada como alvo de compilação e
comportamento (`compileSdk`/`targetSdk`, tipicamente o mesmo número), e
a versão do Android Gradle Plugin (AGP) — o plugin que ensina o Gradle
a montar um aplicativo Android, sem o qual só dá pra compilar Kotlin
puro, como o módulo `core` já faz hoje.

`[REVISAR-EXTERNO]` Pesquisa direta na documentação oficial, em
14 ago. 2026: a versão estável mais recente do Android é a 16 (nível
de API 36) (GOOGLE, [s.d.]c); a Google Play já exige, a partir de
31 ago. 2026, que todo aplicativo novo enviado tenha como alvo pelo
menos o nível de API 36 (GOOGLE, [s.d.]b); a versão estável mais
recente do Android Gradle Plugin é a 9.3.0, que exige Gradle 9.5.0 ou
mais novo (GOOGLE, [s.d.]a) — o Gradle já em uso neste projeto
(9.7.0, ver [`gradle-wrapper.properties`](<../gradle/wrapper/gradle-wrapper.properties>))
já atende essa exigência, sem precisar de atualização.

Pra `minSdk` (versão mais antiga aceita), duas alternativas reais:

(a) `minSdk` 24 (Android 7.0, de 2016) — baseline "moderna" comum pra
projeto novo em 2026, sem exigência documentada em nenhum lugar da
cascata de suportar aparelho mais antigo que isso. Um agregador
independente de estatística de mercado (COMMANDLINUX.COM, 2026) —
não uma fonte oficial da Google, que descontinuou seu painel público
de distribuição de versão — reporta cada versão anterior à 7.0 com
menos de 1% de participação de mercado em ago. 2026, o que dá
confirmação de ordem de grandeza (não um número exato) de que a perda
de alcance dessa escolha é desprezível. Contrapartida: o núcleo BLE
(Bluetooth de baixo consumo, usado pela conectividade com o
acessório leitor — DA-LEI-06, PD-CON-01 a PD-CON-04) muda de modelo de
permissão na versão 12 do Android (nível de API 31) — antes disso, ler
aparelhos próximos por Bluetooth exige permissão de localização; a
partir dali, passa a exigir as permissões dedicadas `BLUETOOTH_SCAN` e
`BLUETOOTH_CONNECT` (GOOGLE, [s.d.]d) — então `minSdk` 24 obriga o
pacote `connectivity` (ainda não escrito) a tratar os dois modelos.

(b) `minSdk` 31 (Android 12, de 2021) — elimina de vez o modelo antigo
de permissão de Bluetooth, simplificando o futuro pacote
`connectivity`. Descartada: excluiria aparelho Android 11 ou anterior
sem que nenhum documento da cascata registre motivo pra restringir o
público-alvo a aparelho tão recente — o ganho de simplicidade de
código não paga o custo de alcance.

**Decisão:**

- `minSdk` = 24 (Android 7.0) — alternativa (a) acima.
- `compileSdk` = `targetSdk` = 36 (Android 16) — acompanha a exigência
  atual da Google Play pra aplicativo novo, `[REVISAR-EXTERNO]`
  reconfirmar antes de cada envio futuro à loja, já que esse número
  sobe todo ano.
- Android Gradle Plugin = 9.3.0 — versão estável mais recente,
  compatível com o Gradle 9.7.0 já em uso, sem exigir atualização de
  wrapper.
- Versão do Kotlin do módulo `app`: gerenciada pelo próprio Android
  Gradle Plugin (suporte embutido a Kotlin desde a versão 9.0 do AGP,
  ver [pitfalls.md](<../docs/pitfalls.md>)) — o módulo `app` não declara
  plugin nem versão de Kotlin separados. Esse suporte embutido só lê
  código compilado com Kotlin até a faixa 2.2.x/2.3.x; por isso a
  versão de Kotlin do módulo `core` (ver
  [`gradle/libs.versions.toml`](<../gradle/libs.versions.toml>)) desceu
  de 2.4.10 pra 2.2.10 nesta tarefa — não é mais livre pra divergir do
  que o AGP embute, apesar de `core` continuar sem depender do Android
  Gradle Plugin em si (ver detalhe completo do problema e da tentativa
  descartada de resolver pelo lado do `app` em
  [pitfalls.md](<../docs/pitfalls.md#2026-08-14-kotlin-embutido-do-agp-exige-versao-igual-no-core>)).
- Nome de pacote (`namespace`) do módulo `app` = `org.nexo.motor.app`,
  já fixado em [decisions/0003](<0003-estrutura-de-modulos-do-aplicativo.md>)
  pra organização de código — reaproveitado aqui, sem decisão nova.

**Consequências:** o módulo `app` passa a existir de verdade, compilável
e instalável num aparelho ou emulador Android 7.0 em diante. O módulo
`core`, já com três pacotes escritos (`search`, `hierarchy`, `session`),
teve sua versão de Kotlin rebaixada de 2.4.10 pra 2.2.10 como efeito
colateral direto de acrescentar o `app` — mudança de número de versão
de ferramenta, não de comportamento de código; suíte de teste do `core`
rodada de novo depois da mudança, sem quebra. O tratamento
dos dois modelos de permissão de Bluetooth (localização, antes da versão
12; permissão dedicada, a partir dela) fica registrado como trabalho
pendente do pacote `connectivity`, ainda não escrito — este documento
não implementa esse tratamento, só deixa registrado por que ele vai ser
necessário. `compileSdk`/`targetSdk` e a versão do Android Gradle Plugin
são exatamente o tipo de número que muda de ano em ano por decisão de
terceiro — quem reabrir este documento no futuro deve reconfirmar os
valores atuais na fonte oficial antes de tratá-los como corretos, não
assumir que continuam válidos só porque estão escritos aqui.

## Referências

Fontes citadas no Contexto e na Decisão, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

COMMANDLINUX.COM. **Android Version Distribution 2026 — Latest Adoption
Statistics**. [S.l.], 2026. Disponível em:
https://commandlinux.com/statistics/android-version-distribution/.
Acesso em: 14 ago. 2026. Nota sobre a fonte: agregador independente de
estatística, não um painel oficial da Google — usado aqui só como
confirmação de ordem de grandeza, não como número exato.

GOOGLE. **About the Android Gradle plugin**. Android Developers,
[s.d.]a. Disponível em: https://developer.android.com/build/releases/about-agp.
Acesso em: 14 ago. 2026.

GOOGLE. **Meet Google Play's target API level requirement**. Android
Developers, [s.d.]b. Disponível em:
https://developer.android.com/google/play/requirements/target-sdk.
Acesso em: 14 ago. 2026.

GOOGLE. **SDK Platform release notes**. Android Developers, [s.d.]c.
Disponível em: https://developer.android.com/tools/releases/platforms.
Acesso em: 14 ago. 2026.

GOOGLE. **Bluetooth permissions**. Android Developers, [s.d.]d.
Disponível em: https://developer.android.com/develop/connectivity/bluetooth/bt-permissions.
Acesso em: 14 ago. 2026.
