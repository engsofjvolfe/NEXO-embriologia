# 0019 — Mecanismo de geração, guarda e compartilhamento do relatório de sessão

Resumo em linguagem simples: o relatório de cada sessão (já decidido em
dois formatos, CSV e PDF) é montado sozinho, sem a pessoa escolher
nada, assim que o jogo termina, e fica guardado na pasta "Downloads"
do aparelho — a mesma que qualquer gerenciador de arquivos do celular
já mostra, não uma área trancada dentro do aplicativo. A pessoa acha o
relatório sem precisar abrir o NEXO de novo. Compartilhar com alguém
já funcionaria de qualquer jeito, direto pelo gerenciador de arquivos
do aparelho — mas a própria tela de resultado, que já aparece sozinha
assim que a sessão termina (DA-RET-14), também vai oferecer um atalho
de compartilhar naquele momento, como conforto pra quem já quer mandar
o relatório na hora, sem precisar sair do aplicativo. Esse atalho
existe só ali, nesse instante — rever ou compartilhar um relatório de
uma sessão passada continua sendo feito pelo gerenciador de arquivos
do aparelho, não por uma tela própria do NEXO. Dois caminhos técnicos
diferentes cobrem toda a faixa de aparelho já prometida (Android 7 em
diante,
[decisions/0012](0012-versoes-de-plataforma-e-build-do-modulo-app.md)),
porque o Android mudou a forma oficial de fazer isso a partir do
Android 10.

Convenção dos códigos citados abaixo:
- `DA-ARM` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.3.
- `DA-REG` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.7.
- `EI-REG` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.13.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Todo trecho abaixo marcado
descreve documentação oficial de terceiro (Google/Android) — não uma
decisão deste projeto, sujeita a mudar em qualquer revisão futura
dessa documentação. Quem ler este documento depois deve tratar esse
conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra mudar código.

**Status:** aceito

**Contexto:**

O formato do relatório já está fechado desde o Projeto Arquitetônico —
não é assunto desta ADR: DA-REG-01 exige ao menos dois formatos (CSV,
conforme RFC 4180; PDF, conforme ISO 32000-2), DA-REG-02 exige que os
dois venham do mesmo registro interno, DA-REG-03 exige que fiquem
prontos para compartilhamento manual, sem depender de internet.
DA-ARM-01 exige guarda só local, sem servidor. EI-REG-05 e EI-REG-06
exigem que o relatório fique salvo no aparelho e continue acessível
depois, livremente, sem login. O Projeto Detalhado nunca desceu a esse
nível de detalhe para "Registro e relatório" (não existe seção
`PD-REG`) — o mecanismo exato fica em aberto até esta ADR.

Duas perguntas ficaram sem resposta até aqui: (1) com que ferramenta
gerar cada formato, sem biblioteca externa, e (2) onde o arquivo
resultante deveria morar no aparelho.

Sobre a pergunta 2, duas respostas foram consideradas:

1. Guardar numa área privada do aplicativo (mesmo padrão já usado por
   `session` para o estado de sessão pausada, ver
   [decisions/0010](0010-persistencia-do-estado-de-sessao-pausada.md)) —
   simples, mas o arquivo não aparece no gerenciador de arquivos do
   aparelho, e some se o aplicativo for desinstalado.
2. Guardar na pasta pública "Downloads" do aparelho — aparece no
   gerenciador de arquivos comum, sobrevive à desinstalação do
   aplicativo, e não depende de nenhuma etapa extra pra "aparecer" pra
   quem joga.

A opção 2 atende melhor EI-REG-06 ("continua podendo consultar o mesmo
relatório depois... direto no aparelho") de forma literal — um arquivo
comum, e não algo trancado atrás de uma tela do NEXO. Ela também já
resolve, sozinha, a exigência de DA-REG-03/DA-ARM-02 ("prontas para
compartilhamento manual"): um arquivo na pasta pública "Downloads" já
pode ser compartilhado por qualquer gerenciador de arquivos ou
aplicativo de mídia do próprio aparelho, sem depender de nada que o
NEXO precise construir — rever ou compartilhar um relatório de sessão
passada nunca exige abrir o NEXO de novo. Ainda assim, a tela de
resultado (DA-RET-14), que já aparece sozinha assim que a sessão
termina, vai oferecer um atalho de compartilhar naquele instante —
conforto pra quem já quer mandar o relatório na hora, sem sair do
aplicativo, não uma exigência de nenhum documento já aprovado. Esse
atalho existe só nesse momento específico; o NEXO não ganha, com esta
ADR, uma tela própria de "histórico de relatórios" pra rever e
compartilhar sessões antigas — isso continua sendo papel do
gerenciador de arquivos do aparelho.

`[REVISAR-EXTERNO]` A forma "oficial" de escrever um arquivo novo
numa pasta pública mudou no Android 10 (API 29): antes dela, o caminho
único era `Environment.getExternalStoragePublicDirectory()` +
permissão `WRITE_EXTERNAL_STORAGE`; a partir dela, existe uma coleção
dedicada (`MediaStore.Downloads`) que dispensa essa permissão
(GOOGLE, [s.d.]a). Como o projeto já prometeu suporte a partir do
Android 7 (`minSdk` 24, decisions/0012) — abaixo da versão em que a
coleção nova existe — nenhuma das duas respostas sozinha cobre a
faixa toda; as duas precisam conviver, cada uma na faixa de versão
certa, mesmo padrão já usado para permissão de Bluetooth em
[decisions/0018](0018-estrategia-de-permissao-de-bluetooth-e-nfc.md).

`[REVISAR-EXTERNO]` Na página de referência do método:
`getExternalStoragePublicDirectory(String type)` não está descontinuado
— existe desde a API 8 e continua funcionando em qualquer versão —, só
carrega uma nota recomendando alternativas "de melhor performance"
(`Context.getExternalFilesDir()` ou `MediaStore`) sem obrigar a troca.
O exemplo de código oficial mostra o padrão completo: obter a pasta
pública com esse método, escrever o arquivo com `FileOutputStream`
comum, depois chamar `MediaScannerConnection.scanFile()` — passo que
avisa o sistema sobre o arquivo novo "para que fique imediatamente
disponível para a pessoa", sem esperar uma varredura completa do
aparelho. O retorno dessa chamada (`onScanCompleted(String path, Uri
uri)`) já entrega o mesmo tipo de endereço (`content://...`) que a
coleção nova (`MediaStore.Downloads`) devolve direto — os dois
caminhos convergem no mesmo formato de resultado.

`[REVISAR-EXTERNO]` Sobre a permissão: `WRITE_EXTERNAL_STORAGE`
existe desde a API 4, é do tipo "dangerous" (exige pedido explícito em
tempo de execução, mesma categoria já tratada para Bluetooth em
decisions/0018), e a própria referência confirma o limite exato — em
tradução livre, "se o aplicativo tiver como alvo `Build.VERSION_CODES.R`
ou superior, essa permissão não tem nenhum efeito" — ou seja, a partir
do Android 11 (API 30) ela já não faz mais nada, o que também sinaliza
(mesmo sem citação direta) que a partir da API 29, quando a coleção
`MediaStore.Downloads` já existe, não há razão pra continuar pedindo
essa permissão de qualquer forma.

Sobre a pergunta 1 (ferramenta de geração), sem pendência de
compatibilidade — as duas classes cobrem a faixa toda de versão
sozinhas: `[REVISAR-EXTERNO]` `android.graphics.pdf.PdfDocument`
(pacote oficial do SDK do Android, sem biblioteca externa) desenha
cada página como um `Canvas` comum — mesmo padrão de uso confirmado
numa página irmã da documentação oficial, com exemplo de código
completo (GOOGLE, [s.d.]b). Para o CSV, não existe classe pronta no SDK —
é texto simples, escrito com as mesmas classes de E/S do Java já
usadas em `session` (decisions/0010), seguindo o escape de
vírgula/aspas que a RFC 4180 já define (RFC já citada em DA-REG-01,
sem necessidade de fonte nova).

**Decisão:**

1. **PDF gerado com `android.graphics.pdf.PdfDocument`** — cada página
   desenhada com `Canvas`, sem biblioteca externa, mesmo padrão de
   preferência por ferramenta já embutida no SDK já usado em
   `content` (`java.util.zip.ZipFile`, decisions/0013) e `session`
   (`java.io.File`, decisions/0010).

2. **CSV gerado como texto simples**, escrito com `java.io` comum,
   seguindo o escape de campo já exigido pela RFC 4180 (DA-REG-01) —
   sem biblioteca externa.

3. **O arquivo (dos dois formatos) é salvo na pasta pública
   "Downloads" do aparelho**, nunca numa área privada do aplicativo —
   por dois caminhos, conforme a versão do Android do aparelho:
   - **Android 10 (API 29) ou mais novo:** `MediaStore.Downloads`,
     sem pedir nenhuma permissão.
   - **Android 7, 8 ou 9 (API 24 a 28):** permissão
     `WRITE_EXTERNAL_STORAGE`, declarada com
     `android:maxSdkVersion="28"` (sem efeito, e portanto sem sentido
     declarar, da API 29 em diante) — pedida em tempo de execução, no
     mesmo momento em que o relatório está prestes a ser montado, mesma
     lógica de "pedir no contexto certo" já usada para Bluetooth em
     decisions/0018; caminho obtido com
     `Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)`,
     escrita com `FileOutputStream`, seguida de
     `MediaScannerConnection.scanFile()` para o arquivo aparecer no
     gerenciador de arquivos imediatamente, sem esperar uma varredura
     completa do aparelho.

4. **A tela de resultado (DA-RET-14) oferece um atalho de compartilhar
   o relatório recém-gerado**, disponível só naquele momento — não uma
   tela nova de histórico dentro do NEXO. Usa o endereço
   (`content://...`) que qualquer um dos dois caminhos do item 3 já
   devolve diretamente, com `Intent.ACTION_SEND_MULTIPLE` (os dois
   arquivos daquela sessão, CSV e PDF, juntos numa única ação) — sem
   precisar de `FileProvider`, porque o arquivo já nasce fora da área
   privada do aplicativo, com endereço público de fábrica. Como os dois
   formatos são diferentes entre si, o tipo declarado no `Intent`
   precisa ser genérico (`*/*`), não um tipo fixo — senão o Android
   pode restringir, sem necessidade, a lista de aplicativos oferecida
   pra receber o arquivo.

**Consequências:**

O pacote `report`, do lado que decide "o que" escrever (montar o texto
do CSV, desenhar as páginas do PDF a partir do registro da sessão),
pode continuar sem depender de classe do Android, testável como os
demais pacotes de `core` — mesma separação já usada em `content`
(leitura do ZIP separada da validação) e em `session` (estado separado
de persistência). A escrita de verdade no aparelho — que exige
`Context` (pra pedir permissão, resolver a coleção do MediaStore, ou
localizar a pasta pública) — é responsabilidade do módulo `app`,
mesmo padrão já fixado em decisions/0010 pra sessão pausada. O
`AndroidManifest.xml` do módulo `app` ganha a declaração de
`WRITE_EXTERNAL_STORAGE` com `maxSdkVersion="28"`, ao lado das já
existentes de Bluetooth e NFC (decisions/0018). Nenhum `FileProvider`
precisa ser configurado. O atalho de compartilhar fica
restrito à tela de resultado (DA-RET-14), logo depois da sessão
terminar — o NEXO não ganha, com esta ADR, nenhuma tela de histórico
pra rever ou compartilhar relatórios de sessões passadas.

**Nota de acompanhamento (15-08-2026):**

*Resumo simples:* essa decisão tinha um erro — dizia que a parte do
código que monta o relatório ficaria inteira livre de qualquer coisa
específica do Android, mas isso não é possível: desenhar o PDF é, por
natureza, uma tarefa do Android. A correção não troca a ferramenta
escolhida pra gerar o PDF, só ajusta em que lugar do projeto esse
pedaço de código mora.

*Detalhe técnico:* a frase "pode continuar sem depender de classe do
Android, testável como os demais pacotes de `core`", nas
Consequências, estava errada — corrigida agora, sem mudar a
ferramenta escolhida no ponto 1 (`PdfDocument` continua sendo a
ferramenta certa). Achado completo, com o raciocínio e o teste que o
confirmou, em
[findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core](<../docs/findings.md#2026-08-15-mecanismo-de-pdf-incompativel-com-core>) —
não repetido aqui.

`report` passa a ser dividido do mesmo jeito que `connectivity` já é
(ver [decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md)):
`core/report/` monta só dado (texto do CSV, lista de linhas do
conteúdo do PDF), sem nenhuma classe do Android, testável com
`kotlin-test`; `app/report/` desenha o PDF de verdade
(`PdfDocument`/`Canvas`), escreve os dois arquivos no aparelho (pontos
3 e 4 desta ADR, sem mudança) e monta o atalho de compartilhar —
testado só por compilação real
(`gradlew :app:assembleDebug`), mesmo padrão já usado no lado `app` de
`connectivity`.

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **Access media files from shared storage**. Android
Developers, [s.d.]a. Disponível em:
https://developer.android.com/training/data-storage/shared/media.
Acesso em: 15 ago. 2026.

GOOGLE. **Printing custom documents**. Android Developers, [s.d.]b.
Disponível em: https://developer.android.com/training/printing/custom-docs.
Acesso em: 15 ago. 2026.

GOOGLE. **Environment**. Android Developers — API reference, [s.d.]c.
Disponível em: https://developer.android.com/reference/android/os/Environment.
Acesso em: 15 ago. 2026.

INTERNET ENGINEERING TASK FORCE. **RFC 4180: common format and MIME
type for Comma-Separated Values (CSV) files**. [S.l.], 2005. Já citada
em [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>),
DA-REG-01 — não repetida aqui como fonte nova.
