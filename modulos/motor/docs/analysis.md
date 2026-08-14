# Analysis — Motor

<!-- module-doc-type: analysis -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Analysis |
| Versão | 0.3.0 |
| Data | 14-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Registro datado de como uma investigação foi feita neste módulo — o
> que foi lido, o que foi checado, o raciocínio seguido. Acontece junto
> com `findings.md`, não numa etapa depois — é o relato de como se
> chegou a cada achado. É o único lugar onde narrativa de processo é
> esperada; por existir aqui, `concept.md` nunca precisa carregar esse
> tipo de conteúdo. Cada entrada é datada e não é reescrita depois — uma
> investigação nova ganha entrada nova.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, campo `Levou a` com link pro achado gerado (ou
> `ainda sem conclusão`), resumo simples, depois detalhe técnico.

## Índice
- [Investigações](#investigacoes)
- [Controle de versão](#controle-de-versão)

## Investigações

### <a id="2026-08-14-esqueleto-minimo-do-modulo-app"></a>2026-08-14 — Esqueleto mínimo do módulo `app`

**Levou a:** [decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>),
[pitfalls.md#2026-08-14-agp-9-nao-aceita-mais-plugin-kotlin-android](<pitfalls.md#2026-08-14-agp-9-nao-aceita-mais-plugin-kotlin-android>),
[pitfalls.md#2026-08-14-kotlin-embutido-do-agp-exige-versao-igual-no-core](<pitfalls.md#2026-08-14-kotlin-embutido-do-agp-exige-versao-igual-no-core>)

*Resumo simples:* o esqueleto do módulo `app` (manifesto,
`Application`, `Activity` sem tela) foi escrito, compilado e aberto de
verdade num emulador Android — não só "deveria funcionar". Duas
versões de ferramenta de build precisaram ser ajustadas no caminho
(ver `pitfalls.md`).

*Detalhe técnico:*
- SDK de linha de comando do Android instalado (`cmdline-tools`,
  `platform-tools`, plataforma 36, `build-tools` 36.0.0, `emulator`,
  imagem de sistema `system-images;android-34;google_apis;x86_64`),
  licenças aceitas, AVD `nexo_motor_test` criado (perfil Pixel 6) —
  pré-requisito de ambiente pra qualquer um dos passos abaixo.
- Pesquisa direta em fontes oficiais (developer.android.com, acesso em
  14 ago. 2026) pra decidir `minSdk`, `compileSdk`/`targetSdk` e a
  versão do Android Gradle Plugin — detalhe completo e alternativas
  descartadas em
  [decisions/0012](<../decisions/0012-versoes-de-plataforma-e-build-do-modulo-app.md>).
- Duas tentativas de build falharam antes de compilar de verdade,
  ambas por comportamento de ferramenta não documentado em nenhum
  lugar do projeto até agora: o plugin `org.jetbrains.kotlin.android`
  não é mais aceito a partir do AGP 9 (suporte a Kotlin embutido), e o
  Kotlin embutido no AGP 9.3.0 não lê código compilado com uma versão
  de Kotlin mais nova que a que ele mesmo traz — obrigou a descer a
  versão de Kotlin do `core` de 2.4.10 pra 2.2.10. As duas ficaram
  registradas em `pitfalls.md`, não só corrigidas em silêncio.
- Depois da correção: `gradlew :core:test` (suíte já existente do
  `core`, três pacotes) rodada de novo, sem quebra — confirma que
  baixar a versão do Kotlin não mudou nenhum comportamento.
  `gradlew :app:assembleDebug` compilou com sucesso.
- Teste ao vivo: `gradlew :app:installDebug` instalou o APK de debug
  no emulador; `adb shell am start -n org.nexo.motor.app/.MainActivity`
  abriu a tela; `adb shell dumpsys activity activities` confirmou
  `topResumedActivity=...org.nexo.motor.app/.MainActivity` (em
  primeiro plano) e o processo rodando (`adb shell pidof
  org.nexo.motor.app`); `adb logcat` do processo sem nenhum erro ou
  encerramento inesperado, só avisos de desempenho normais de emulador
  com renderização por software (SwiftShader).

### <a id="2026-08-14-revisao-de-cobertura-de-teste-dos-pacotes-core"></a>2026-08-14 — Revisão de cobertura de teste dos pacotes `search`, `hierarchy`, `session` e `content`

**Levou a:** ainda sem conclusão

*Resumo simples:* checagem de cada teste automatizado já existente
nesses quatro pacotes contra a cascata de documentação (não contra o
código de implementação, nunca aberto durante esta investigação), pra
responder se o teste prova mesmo a regra documentada ou só espelha o
que o código já fazia. A maioria dos testes já cita, no próprio nome,
o requisito que prova, e a asserção bate com o texto do documento —
nenhum sinal de teste ajustado ao resultado do código. Cinco pontos
específicos, documentados mas sem nenhum teste provando-os, foram
encontrados e fechados com teste novo.

*Detalhe técnico:*
- Leitura completa, antes de abrir qualquer teste: os cinco documentos
  da cascata aprovada (1 a 5), as ADRs 0004, 0005, 0007 a 0011 e 0013,
  `findings.md`, `pitfalls.md` e `architecture.md` — pra ter, de forma
  independente do código, o que cada pacote deveria fazer antes de ler
  o que cada teste afirma.
- Método: comparar só o nome e o corpo de cada teste com a regra
  documentada correspondente — nunca abrir `core/src/main/` durante a
  checagem, pra não correr o risco de confirmar que o teste bate com o
  que o código já faz, em vez de checar se bate com o que o código
  deveria fazer.
- Cinco lacunas encontradas, cada uma com requisito ou decisão já
  documentada mas nenhum teste cobrindo:
  1. `search`: nenhum teste provava que um item com distância acima do
     limiar (PD-NAV-02) fica de fora do resultado — só a função do
     limiar em si era testada isoladamente.
  2. `session`: `useHint`, listada em
     [architecture.md](<architecture.md#pacote-session--desenho-interno>)
     como parte da API pública de `SessionTransitions.kt`, nunca era
     chamada por nenhum teste.
  3. `session`: `deleteSessionState`, também listada em
     `architecture.md`, ligada a "sair apaga o progresso retomável"
     (EI-PAU-03), nunca era chamada por nenhum teste.
  4. `content`: a "varredura completa, não fail-fast" (decisão 3 da
     [ADR 0013](<../decisions/0013-desenho-do-pacote-content.md>)) nunca
     era comprovada com duas violações independentes ocorrendo juntas
     na mesma importação — todo teste existente provocava só um
     problema de cada vez.
  5. `session`: um teste já existente, citando EI-VAL-03 ("origem da
     peça não é considerada"), na verdade não podia provar isso —
     `recordAttempt` nunca recebe "origem" como parâmetro, só a peça
     apresentada e a esperada. O teste foi reescrito pra afirmar só o
     que de fato checa (duas peças erradas quaisquer recebem o mesmo
     tratamento), sem trocar o comportamento provado.
- Teste novo escrito pra cada um dos cinco pontos, sempre a partir da
  regra documentada, nunca do resultado observado do código.
  `gradlew :core:test` rodado de verdade depois: 81 testes (77 antes),
  0 falhas — nenhuma divergência de comportamento encontrada; a
  implementação já atendia todos os cinco pontos, só faltava o teste
  escrito provando isso.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. Entrada nova em "Investigações" (append,
sem reescrever) também conta como mudança de conteúdo real. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial. | Criação inicial |
| 0.2.0 | 14-08-2026 | Acrescentada a investigação do esqueleto mínimo do módulo `app` (instalação do SDK, pesquisa de versões, duas armadilhas de ferramenta, teste ao vivo no emulador). | Resolução da pendência "Escrever o esqueleto mínimo do módulo `app`" |
| 0.3.0 | 14-08-2026 | Acrescentada a investigação de cobertura de teste dos pacotes `search`, `hierarchy`, `session` e `content` (cinco lacunas encontradas e fechadas com teste novo, sem divergência de comportamento). | Revisão de cobertura de teste, pedido direto |
