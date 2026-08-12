# NEXO — Projeto Arquitetônico
## Módulo: Conceito Geral (Motor)

| Campo | Valor |
|---|---|
| Projeto | NEXO |
| Módulo | Conceito Geral (Motor) |
| Etapa (V-Model) | Projeto Arquitetônico |
| Documento(s) de origem | `3 - especificacao-conceito-geral.md` v1.0.0 (normativo principal); `2 - requisitos-conceito-geral.md` v1.0.0 e `1 - documento-de-conceito-geral.md` v1.0.0 (referência) |
| Versão | 1.0.0 |
| Data | 12-08-2026 |
| Situação | Aprovado |
| Licença | Todos os direitos reservados — ver [LICENSE](../../LICENSE) |

---

## 1. Objetivo

Decidir como as peças do motor do NEXO se conectam entre si, resolvendo os pontos que o documento de Especificação deixou em aberto para esta etapa (seção 10 dele): a tecnologia de leitura e identificação das peças físicas; onde os dados ficam guardados; como o motor funciona sem depender de internet; como um conteúdo novo chega até o aplicativo; os formatos de exportação do relatório; o fluxo funcional de cada tela (sem tratar de aparência visual); a regra de validação de conteúdo incompleto; e o critério de ordenação/busca da navegação.

Este documento decide a **categoria** de tecnologia envolvida em cada peça e como as peças se conectam entre si — não desce ao nível de peça específica de hardware, biblioteca exata ou formato de arquivo campo a campo. Isso cabe ao Projeto Detalhado, etapa seguinte da cascata (ver `docs/prompt model.txt`).

---

## 2. Escopo

### 2.1 Dentro do escopo

- Os componentes físicos e lógicos que compõem uma instalação do motor, e como eles se conectam entre si.
- A categoria de tecnologia usada para identificar uma peça física.
- O funcionamento de uma sessão sem depender de conexão à internet.
- A forma de levar conteúdo novo até o aplicativo, sem administração central.
- O formato de guarda e de exportação do relatório de sessão.
- O fluxo funcional das telas do motor: quais existem, o que cada uma mostra, o que leva de uma à outra — não a aparência delas.
- A regra de validação aplicada a um conteúdo incompleto.
- O critério de ordenação e busca da navegação.

### 2.2 Fora do escopo

- Modelo exato de microcontrolador, de leitor de etiqueta, ou de etiqueta (marca, número de peça) — Projeto Detalhado.
- Protocolo exato de comunicação entre o acessório leitor e o aplicativo (formato de mensagem, biblioteca usada) — Projeto Detalhado.
- Esquema exato do arquivo de conteúdo, campo a campo (nomes, tipos de dado) — Projeto Detalhado / pasta `schemas/` de um futuro módulo.
- Aparência visual de qualquer tela (cor, fonte, layout) — nunca entra em nenhuma etapa do motor; é decisão de quem constrói a interface de cada instância, de acordo com o próprio conceito (§15).
- Texto legal do termo de consentimento e privacidade (LGPD) — tratado à parte, fora desta rodada de trabalho.
- Forma exata de distribuição do aplicativo (loja de aplicativos, arquivo direto) — Projeto Detalhado.

---

## 3. Documentos relacionados

| Documento | Papel em relação a este documento |
|---|---|
| `3 - especificacao-conceito-geral.md` | Fonte normativa direta. Cada decisão abaixo resolve um item da lista de pendências (seção 10) desse documento, ou um item de especificação (EI) já existente. |
| `2 - requisitos-conceito-geral.md` | Referência de segundo nível, citada quando o item de origem também cita um requisito (RF/RNF). |
| `1 - documento-de-conceito-geral.md` | Referência de terceiro nível, citada quando o item de origem também cita uma seção do conceito. |

---

## 4. Termos adicionais desta etapa

| Termo | Definição |
|---|---|
| Aparelho de jogo | O aparelho Android de quem joga, rodando o aplicativo do NEXO. É onde a lógica do motor roda e onde os dados da sessão ficam guardados. |
| Acessório leitor | Conjunto formado por um microcontrolador e um leitor de etiquetas, usado apenas quando o aparelho de jogo não tem antena própria de leitura. Não guarda conteúdo nem executa lógica de jogo — só relata ao aplicativo qual etiqueta foi lida. |
| Etiqueta | Identificador físico passivo (sem bateria própria), fixado numa peça, lido por aproximação. |
| Pacote de conteúdo | Arquivo único que reúne o catálogo de uma instância — temas, eventos, sequências, textos, imagens, e a associação entre cada etiqueta física e o fotograma que ela representa — pronto para ser importado no aplicativo. |
| Sessão local | Uma sessão de jogo, do início ao fim, contida inteiramente no aparelho de jogo, sem depender de nenhum outro sistema externo para funcionar. |

---

## 5. Convenção de identificação

Formato: `DA-[CATEGORIA]-[número]` (Decisão de Arquitetura). Reaproveita as siglas já usadas nos documentos anteriores e acrescenta três novas, criadas nesta etapa:

| Categoria | Sigla |
|---|---|
| Leitura e identificação de peça | LEI |
| Armazenamento | ARM |
| Importação de conteúdo | IMP |

Cada item indica, na coluna "Origem", o item de Especificação (EI) ou o ponto da lista de pendências (seção 10 do documento de Especificação) que ele resolve.

---

## 6. Decisões de arquitetura por categoria

### 6.1 Leitura e identificação de peça (LEI)

| ID | Descrição | Origem |
|---|---|---|
| DA-LEI-01 | Cada peça física é identificada por uma etiqueta passiva, sem bateria, no padrão usado por NFC — frequência de 13,56 MHz, compatível com o padrão internacional ISO 14443 e com a especificação de Tag Tipo 2 da NFC Forum (NFC FORUM, [s.d.]) (a mesma família de etiqueta usada em cartão de transporte, crachá de acesso, etc.). | Doc. Especificação §10, item "tecnologia de leitura e identificação de peças" |
| DA-LEI-02 | O número que identifica cada etiqueta é único e é associado ao fotograma correspondente no momento em que o conteúdo é montado (ver IMP). O motor nunca infere essa associação — ela sempre chega pronta, dentro do pacote de conteúdo. | DA-LEI-01; EI-VAL-02 |
| DA-LEI-03 | Existem dois caminhos físicos de leitura, escolhidos automaticamente conforme o hardware disponível no aparelho de jogo: (a) leitura direta pela antena própria do aparelho; (b) leitura por um acessório leitor externo, para aparelhos sem essa antena. | DA-LEI-01 |
| DA-LEI-04 | No caminho (a), o aplicativo mantém a leitura ativa continuamente enquanto está em primeiro plano, usando o modo de leitura em primeiro plano do próprio sistema Android (GOOGLE, [s.d.]a, [s.d.]b), sem exigir nenhuma ação extra da pessoa a cada peça (nem uma tela ou aviso do sistema aparecendo no meio) — preserva a regra de que a tela só confirma, nunca interrompe (RF-RET-01). | DA-LEI-03(a); RF-RET-01 |
| DA-LEI-05 | No caminho (b), o acessório leitor combina duas medidas para garantir que só uma etiqueta seja lida por vez: potência de leitura reduzida (encolhendo o alcance natural para poucos centímetros) e um encaixe físico dimensionado para caber apenas uma peça de cada vez, posicionado diretamente sobre a antena do leitor. | DA-LEI-03(b); EI-INT-03 |
| DA-LEI-06 | Em ambos os caminhos, o restante do motor trata a leitura da mesma forma — a lógica de validação (EI-VAL-*) não distingue se a peça foi lida pela antena do próprio aparelho ou pelo acessório externo. | DA-LEI-03 |

### 6.2 Conectividade (CON)

| ID | Descrição | Origem |
|---|---|---|
| DA-CON-01 | Nenhuma etapa de uma sessão local depende de conexão à internet: leitura, validação, dica, resumo, síntese, geração e consulta do relatório funcionam inteiramente dentro do aparelho de jogo. | RNF-CON-01; EI-CON-01 |
| DA-CON-02 | A ligação entre o acessório leitor externo e o aplicativo usa Bluetooth de baixo consumo (BLE) — não uma rede Wi-Fi própria. Essa escolha evita que o aparelho de jogo precise se desconectar da própria rede/internet normal para jogar (comportamento de desconexão automática de rede sem internet documentado em GOOGLE, [s.d.]c, [s.d.]d), reduz o consumo de energia do acessório e usa o mecanismo de emparelhamento do próprio padrão Bluetooth (BLUETOOTH SIG, 2020) para garantir uma ligação reconhecida entre os dois aparelhos. | DA-LEI-03(b); DA-CON-01 |
| DA-CON-03 | A ligação por Bluetooth entre acessório e aplicativo fica associada a um único aparelho de jogo por vez — reforça, na camada de conectividade, a regra de que só uma sessão existe por vez (RF-PAU-05). | DA-CON-02; RF-PAU-05 |
| DA-CON-04 | A obtenção do pacote de conteúdo (ver IMP) pode envolver internet normalmente — por exemplo, baixar o pacote de algum repositório antes de transferi-lo ao aparelho. A exigência de não depender de internet (DA-CON-01) é sobre a sessão de jogo em si, não sobre como o conteúdo chegou até o aparelho. | RNF-CON-01 |

### 6.3 Armazenamento (ARM)

| ID | Descrição | Origem |
|---|---|---|
| DA-ARM-01 | Todos os dados de uma sessão — configuração usada, registro de tentativas, relatório final — ficam guardados exclusivamente no armazenamento local do aparelho de jogo. Não existe servidor central, nem qualquer outro local que receba esses dados automaticamente. | Doc. Especificação §10, item "estrutura de armazenamento"; EI-REG-05 |
| DA-ARM-02 | Compartilhar o relatório de uma sessão com terceiros é uma ação voluntária e explícita de quem jogou, feita depois que o relatório já foi entregue a ela — nunca automática, nunca disparada pelo motor. | EI-REG-07 |
| DA-ARM-03 | Como o conteúdo pode ser jogado em qualquer aparelho com o aplicativo instalado, existe o risco de a pessoa usar um aparelho que não é o próprio — nesse caso, os dados da sessão ficam no aparelho usado, não no dela. A política de coleta, uso e privacidade de dados (a redigir — fora do escopo desta etapa) informa isso a quem joga, no momento em que o relatório é entregue. | EI-REG-03 |

### 6.4 Importação de conteúdo (IMP)

| ID | Descrição | Origem |
|---|---|---|
| DA-IMP-01 | O conteúdo de uma instância (temas, eventos, sequências, fotogramas, textos, e a associação entre etiqueta física e fotograma) é descrito num arquivo em formato de texto aberto e legível (JSON), no formato definido pela RFC 8259 (INTERNET ENGINEERING TASK FORCE, 2017) — sem depender de programa pago ou proprietário para ser criado ou editado. | Doc. Especificação §10, item "conteúdo sem administração central" |
| DA-IMP-02 | Esse arquivo, junto com as imagens usadas pelos fotogramas, é empacotado num único arquivo compactado (formato ZIP, também aberto e sem custo, conforme a especificação pública mantida pela PKWARE — PKWARE, 2022) — o pacote de conteúdo. | DA-IMP-01 |
| DA-IMP-03 | A instalação do aplicativo já inclui, de fábrica, ao menos um pacote de conteúdo completo, pronto para uso, sem exigir nenhuma importação no primeiro uso. | DA-IMP-02 |
| DA-IMP-04 | A importação de um pacote de conteúdo novo é feita pela própria pessoa, pela tela padrão do sistema Android de escolher arquivo, sem exigir internet nem qualquer autenticação — não existe papel de administração restringindo essa ação. | DA-CON-01; DA-IMP-02 |
| DA-IMP-05 | A transferência do pacote de conteúdo até o aparelho (por exemplo, por cabo USB ou Bluetooth) fica fora do controle do motor — ele só exige que o arquivo já esteja acessível no armazenamento local do aparelho no momento da importação. | DA-IMP-04 |
| DA-IMP-06 | O motor prefere, em cada camada onde há escolha, tecnologia e formato aberto e sem custo de licença (JSON, ZIP, Bluetooth, NFC padrão) — evita depender de formato ou serviço proprietário sempre que existir alternativa aberta equivalente. | Premissa de projeto (ver seção 8) |

### 6.5 Validação de conteúdo (CFG)

| ID | Descrição | Origem |
|---|---|---|
| DA-CFG-01 | Ao importar um pacote de conteúdo, o motor verifica, em cada nível da hierarquia presente no pacote (instância, tema, evento), se todos os parâmetros obrigatórios de conteúdo daquele nível — a metade de EI-CFG-01 definida por quem monta o conteúdo, não a metade definida na configuração da sessão — estão preenchidos: por exemplo, o prazo de retenção de uma instância, a declaração de ordem/avulso de um tema, ou o marco zero de um evento. Um item com qualquer parâmetro obrigatório ausente não é importado — o motor aponta exatamente quais campos faltaram, e em qual item (instância, tema ou evento). | Doc. Especificação §10, item "regra de validação de cadastro"; EI-CFG-01 |
| DA-CFG-02 | Não existe estado de conteúdo "incompleto, mas disponível para jogo", em nenhum nível: uma instância, um tema ou um evento só ficam disponíveis depois de importados com sucesso, ou seja, depois de completos. | DA-CFG-01 |
| DA-CFG-03 | Um pacote pode conter itens completos e incompletos ao mesmo tempo, em qualquer nível; o motor importa e libera para jogo somente os completos, listando os recusados, o nível a que cada um pertence, e o motivo de cada recusa. | DA-CFG-01, DA-CFG-02 — ver premissa na seção 8 |

### 6.6 Fluxo funcional das telas (RET)

Lista das telas que o motor precisa apresentar, e o que cada uma mostra — sem decidir aparência (cor, fonte, layout). A ordem abaixo segue a sequência natural de uso, não uma numeração fixa.

| ID | Tela | Conteúdo funcional | Origem |
|---|---|---|---|
| DA-RET-01 | Sessão pausada | Ao abrir o aplicativo, se existir uma sessão pausada, mostra só a opção de retomá-la ou sair dela — nenhuma sessão nova começa antes dessa escolha. | EI-NAV-01; RF-PAU-05 |
| DA-RET-02 | Navegação | Lista de instâncias, depois temas, depois eventos, respeitando a hierarquia; com busca e ordenação (ver NAV). | EI-NAV-02 |
| DA-RET-03 | Ponto de início | Ao entrar num evento como ponto de entrada da sessão, oferece escolher entre as posições já cadastradas; padrão é a primeira. Não aparece em eventos encadeados depois do primeiro (ver EI-ENC-02). | EI-SES-02 |
| DA-RET-04 | Configuração da sessão | Junto com a tela de ponto de início, oferece, para cada evento dentro do alcance da sessão, se pular está disponível e o limiar de erro que libera a dica e a sugestão de estudo (quando a dica estiver habilitada); oferece também um único tempo de ociosidade, válido para a sessão inteira, não repetido por evento. Toda a tela aparece uma única vez, no início, antes da primeira tentativa. | EI-NAV-05 |
| DA-RET-05 | Referência | Mostra o marco zero, o fotograma anterior, ou a última peça do evento anterior, conforme a regra da seção EI-SES-04 — antes de qualquer tentativa. | EI-SES-04; EI-RET-01 |
| DA-RET-06 | Aguardando tentativa | Estado de espera por uma leitura de peça; não antecipa nem anuncia nada. | EI-RET-01 |
| DA-RET-07 | Confirmação de acerto | Mostra, opcionalmente, o texto curto cadastrado para aquele fotograma. | EI-RET-03 |
| DA-RET-08 | Negativa | Mensagem única e padronizada de não correspondência, igual em todo caso de erro. | EI-RET-02 |
| DA-RET-09 | Dica | Aparece ao atingir o limiar de erros escolhido na configuração da sessão (ver DA-RET-04) para a posição atual. | EI-DIC-01 |
| DA-RET-10 | Sugestão de estudo | Aparece se, mesmo com a dica, a posição continuar sem ser preenchida; junto com a opção de pular, se disponível. | EI-DIC-03 |
| DA-RET-11 | Resumo de evento | Ao final de um evento sem posições perdidas por pulo. | EI-ENC-01; EI-RET-04 |
| DA-RET-12 | Mensagem de pulo | Aparece ao final do evento em que houve ao menos uma posição perdida por pulo, substituindo o resumo individual desse evento — lista o que foi respondido, aponta o intervalo sem resposta, sugere estudo daquele trecho. Aparece uma única vez, não é reexibida na síntese de cadeia. | EI-PUL-05 |
| DA-RET-13 | Síntese de cadeia | Ao final de uma cadeia com mais de um evento. Se nenhum evento da cadeia teve posição perdida por pulo, mostra a narrativa contínua do trecho jogado; se algum evento teve, mostra só o total consolidado de posições preenchidas e perdidas na cadeia inteira, sem repetir a mensagem de três partes já exibida no evento em que o pulo ocorreu. | EI-ENC-03, EI-PUL-05 |
| DA-RET-14 | Resultado / relatório | Mostrado ao final de toda sessão (inclusive ao sair antes do fim); continua acessível depois, livremente, a partir do armazenamento local (ver ARM, REG). | EI-REG-06 |
| DA-RET-15 | Confirmação de saída | Aviso explícito antes de apagar o progresso retomável de uma sessão. | EI-PAU-03 |
| DA-RET-16 | Importar conteúdo | Tela de escolher arquivo (padrão do sistema Android) para importar um pacote de conteúdo novo. | DA-IMP-04 |
| DA-RET-17 | Consentimento | Antes de registrar qualquer dado que identifique a pessoa, explica para que esses dados serão usados e pede consentimento explícito. O texto legal exato fica fora do escopo desta etapa. | EI-REG-03 |

### 6.7 Registro e relatório — formatos (REG)

| ID | Descrição | Origem |
|---|---|---|
| DA-REG-01 | O relatório de uma sessão é exportado em ao menos dois formatos: um voltado a análise de dados (uma tabela, formato CSV, conforme a RFC 4180 — INTERNET ENGINEERING TASK FORCE, 2005 —, aberta em qualquer planilha) e um legível por qualquer pessoa (texto narrado ou lista de tópicos, formato PDF, conforme a norma ISO 32000-2 — INTERNATIONAL ORGANIZATION FOR STANDARDIZATION, 2020). | Doc. Especificação §10, item "formatos exatos de exportação"; EI-REG-05 |
| DA-REG-02 | Os dois formatos são gerados a partir do mesmo registro (EI-REG-01), sem exigir nova coleta de dado. | DA-REG-01 |
| DA-REG-03 | A geração e a exportação dos dois formatos não dependem de internet — ficam salvas localmente, prontas para compartilhamento manual por quem jogou, se ela quiser (ver ARM-02). | DA-CON-01; DA-ARM-02 |

### 6.8 Navegação — ordenação e busca (NAV)

| ID | Descrição | Origem |
|---|---|---|
| DA-NAV-01 | Por padrão, instâncias, temas e eventos aparecem em ordem alfabética pelo nome. | Doc. Especificação §10, item "critério de ordenação" |
| DA-NAV-02 | A pessoa pode filtrar essa lista e buscar por texto livre. | DA-NAV-01 |
| DA-NAV-03 | A busca tolera pequena diferença de digitação (erro de digitação, nome aproximado) — não exige correspondência exata com o nome cadastrado. | DA-NAV-02 |

---

## 7. Restrições

- Este documento não define modelo, marca ou número de peça de nenhum componente físico — isso é Projeto Detalhado (ver seção 2.2).
- Este documento não define aparência visual de nenhuma tela — apenas o conteúdo funcional que cada uma carrega (ver DA-RET).
- O motor não deve, em nenhuma hipótese, exigir conexão à internet para qualquer etapa de uma sessão local (DA-CON-01).
- O motor não deve manter nem depender de nenhum servidor central, nem de um papel de administração com acesso automático a dados de terceiros (DA-ARM-01).
- O motor não deve importar, nem deixar disponível para jogo, uma instância, tema ou evento com parâmetro obrigatório ausente (DA-CFG-01, DA-CFG-02).
- O texto legal do termo de consentimento e privacidade não é escrito neste documento — apenas a exigência de que a tela de consentimento (DA-RET-16) exista e a informação de que ela precisa cobrir (ver EI-REG-03).

---

## 8. Premissas

- Quem prepara um pacote de conteúdo tem acesso a um editor de texto simples (ou, no futuro, uma ferramenta própria de autoria, fora do escopo desta etapa) capaz de produzir um arquivo JSON válido — o formato em si não exige nenhum programa pago.
- A rejeição de um item incompleto na importação (DA-CFG-03) é tratada item a item (instância, tema ou evento), não pacote inteiro — um pacote com um item incompleto e outros completos importa o que está pronto e aponta o que falta no resto. Essa é uma escolha deste documento, não uma exigência que já vinha do documento de Especificação; pode ser revista se, na prática, um comportamento "tudo ou nada" (recusar o pacote inteiro se qualquer item estiver incompleto) se mostrar mais adequado.
- O aparelho de jogo é sempre um aparelho Android — o motor, nesta rodada de arquitetura, não cobre nenhum outro sistema operacional de aparelho móvel.
- Etiquetas fisicamente compatíveis (13,56 MHz, padrão NFC) são adquiridas de fornecedor próprio para esse fim.
- Quem prepara o conteúdo é um papel distinto de quem joga, mas sem login, painel ou autenticação própria dentro do motor — qualquer pessoa com acesso físico a um aparelho pode importar um pacote de conteúdo novo.

---

## 9. Itens deferidos ao Projeto Detalhado

- Modelo exato de microcontrolador e de módulo leitor do acessório externo (DA-LEI-03b).
- Definição exata do serviço/característica Bluetooth (perfil BLE) usado entre acessório e aplicativo (DA-CON-02).
- Esquema exato do arquivo JSON do pacote de conteúdo — nomes de campo, tipos de dado, validações (DA-IMP-01).
- Biblioteca ou mecanismo exato de leitura/escrita do arquivo ZIP dentro do aplicativo Android (DA-IMP-02).
- Algoritmo exato de busca aproximada usado na navegação (DA-NAV-03).
- Forma exata de distribuição do aplicativo (loja de aplicativos, arquivo instalável direto) — sujeita à preferência por caminhos sem custo e abertos (DA-IMP-06).
- Texto legal do termo de consentimento e privacidade (DA-RET-16) — a redigir em rodada própria, fora desta etapa.

---

## 10. Referências

Fontes externas consultadas para embasar as decisões de arquitetura desta etapa, no formato definido pela norma ABNT NBR 6023 (Informação e documentação — Referências). Citadas no corpo do documento como (ENTIDADE, ano).

BLUETOOTH SIG. **Bluetooth® LE secure connections – numeric comparison**. [S.l.], 2020. Disponível em: https://www.bluetooth.com/blog/bluetooth-pairing-part-4/. Acesso em: 11 ago. 2026.

GOOGLE. **Advanced NFC overview**. Android Developers, [s.d.]a. Disponível em: https://developer.android.com/develop/connectivity/nfc/advanced-nfc. Acesso em: 11 ago. 2026.

GOOGLE. **NfcAdapter.ReaderCallback**. Android Developers, [s.d.]b. Disponível em: https://developer.android.com/reference/android/nfc/NfcAdapter.ReaderCallback. Acesso em: 11 ago. 2026.

GOOGLE. **Use a local-only Wi-Fi hotspot**. Android Developers, [s.d.]c. Disponível em: https://developer.android.com/develop/connectivity/wifi/localonlyhotspot. Acesso em: 11 ago. 2026.

GOOGLE. **ConnectivityManager**. Android Developers, [s.d.]d. Disponível em: https://developer.android.com/reference/android/net/ConnectivityManager. Acesso em: 11 ago. 2026.

INTERNATIONAL ORGANIZATION FOR STANDARDIZATION. **ISO 32000-2:2020 — Document management — Portable document format — Part 2: PDF 2.0**. Geneva: ISO, 2020. Disponível em: https://www.iso.org/standard/75839.html. Acesso em: 12 ago. 2026.

INTERNET ENGINEERING TASK FORCE. **RFC 4180: common format and MIME type for Comma-Separated Values (CSV) files**. [S.l.], 2005. Disponível em: https://www.rfc-editor.org/info/rfc4180. Acesso em: 12 ago. 2026.

INTERNET ENGINEERING TASK FORCE. **RFC 8259: the JavaScript Object Notation (JSON) data interchange format**. [S.l.], 2017. Disponível em: https://www.rfc-editor.org/info/rfc8259. Acesso em: 12 ago. 2026.

NFC FORUM. **Type 2 Tag Specification**. [S.l.], [s.d.]. Disponível em: https://nfc-forum.org/build/specifications/type-2-tag-specification/. Acesso em: 11 ago. 2026.

PKWARE. **APPNOTE.TXT — .ZIP file format specification**. [S.l.], 2022. Disponível em: https://pkware.cachefly.net/webdocs/casestudies/APPNOTE.TXT. Acesso em: 12 ago. 2026.

Nota sobre datação: as páginas de documentação técnica acima (Google, Bluetooth SIG, NFC Forum) são mantidas e atualizadas continuamente pelos próprios fabricantes/organizações donas do padrão, sem data de publicação fixa divulgada — por isso `[s.d.]` (sem data), conforme previsto na própria norma para esse caso. A data de acesso (11 ago. 2026) é o dado que ancora a referência no tempo.

---

## 11. Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 11-08-2026 | Criação inicial: decisões de arquitetura para leitura e identificação de peça (etiqueta NFC de 13,56 MHz, dois caminhos de leitura), conectividade (funcionamento sem internet, Bluetooth de baixo consumo entre acessório e aplicativo), armazenamento local sem servidor central, importação de conteúdo por pacote ZIP/JSON sem administração, validação de conteúdo incompleto, fluxo funcional das telas, formatos de exportação do relatório, e critério de ordenação/busca da navegação. Resolve os itens da seção 10 do documento de Especificação v0.1.0. | Projeto Arquitetônico |
| 1.0.0 | 12-08-2026 | Documento aprovado — primeira versão estável do Projeto Arquitetônico do módulo Conceito Geral (Motor). | Aprovação da cascata do motor |
