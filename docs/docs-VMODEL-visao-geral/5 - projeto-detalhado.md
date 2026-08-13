# NEXO — Projeto Detalhado
## Módulo: Conceito Geral (Motor)

| Campo | Valor |
|---|---|
| Projeto | NEXO |
| Módulo | Conceito Geral (Motor) |
| Etapa (V-Model) | Projeto Detalhado |
| Documento(s) de origem | `4 - projeto-arquitetonico.md` v1.0.0 (normativo principal); `3 - especificacao-conceito-geral.md` v1.0.0, `2 - requisitos-conceito-geral.md` v1.0.0 e `1 - documento-de-conceito-geral.md` v1.0.0 (referência) |
| Versão | 1.0.1 |
| Data | 13-08-2026 |
| Situação | Aprovado |
| Licença | Todos os direitos reservados — ver [LICENSE](../../LICENSE) |

---

## 1. Objetivo

Descer, item por item, cada pendência tratável nesta etapa que o Projeto Arquitetônico deixou explícita em sua seção 9 ("Itens deferidos ao Projeto Detalhado"): o modelo exato de cada componente físico, o esquema exato do arquivo de conteúdo, a biblioteca exata usada dentro do aplicativo, o algoritmo exato de busca aproximada, e a forma exata de distribuição do aplicativo. A seção 9 do Projeto Arquitetônico registra um sétimo item, o texto legal do termo de consentimento (DA-RET-16), que a própria seção 9 já classifica como pertencente a uma rodada própria, fora desta cascata (ver §2.2) — por isso ele não entra nesta descida item por item. Este documento não decide categoria de tecnologia — isso já foi decidido no Projeto Arquitetônico — decide peça específica dentro da categoria já escolhida. A etapa seguinte da cascata é a Implementação (ver `docs/prompt model.txt`).

---

## 2. Escopo

### 2.1 Dentro do escopo

- Modelo exato do módulo leitor NFC e do microcontrolador do acessório leitor externo (DA-LEI-03(b)).
- Definição exata do serviço e das características Bluetooth de baixa energia (BLE) usados entre acessório e aplicativo (DA-CON-02).
- Esquema exato (campo a campo) do arquivo JSON do pacote de conteúdo (DA-IMP-01).
- Mecanismo exato de leitura do arquivo ZIP dentro do aplicativo Android (DA-IMP-02).
- Algoritmo exato de busca aproximada usado na navegação (DA-NAV-03).
- Forma exata de distribuição do aplicativo, sujeita à preferência por caminhos sem custo e abertos já registrada em DA-IMP-06.

### 2.2 Fora do escopo

- Texto legal do termo de consentimento e privacidade (DA-RET-16) — o próprio Projeto Arquitetônico já registrou que isso fica para uma rodada própria, fora da cascata do motor.
- Aparência visual de qualquer tela — nunca entra em nenhuma etapa do motor (ver Conceito, §15; Projeto Arquitetônico, §2.2).
- Ferramenta de autoria de conteúdo (interface para quem monta uma instância preencher o pacote) — só a estrutura de dados que essa ferramenta, quando existir, precisa produzir.
- Código-fonte da implementação em si — este documento descreve o que a implementação precisa satisfazer, não escreve a implementação.

---

## 3. Documentos relacionados

| Documento | Papel em relação a este documento |
|---|---|
| `4 - projeto-arquitetonico.md` | Fonte normativa direta. Cada decisão abaixo resolve um item da lista de pendências (seção 9) desse documento, ou aprofunda uma Decisão de Arquitetura (DA) já existente. |
| `3 - especificacao-conceito-geral.md` | Referência de segundo nível, citada quando o item de origem também cita um item de especificação (EI). |
| `2 - requisitos-conceito-geral.md` e `1 - documento-de-conceito-geral.md` | Referência de terceiro nível, citadas quando o item de origem remonta a um requisito ou a uma seção do conceito. |

---

## 4. Termos adicionais desta etapa

| Termo | Definição |
|---|---|
| GATT | Generic Attribute Profile — a camada do Bluetooth de baixa energia (BLE) que organiza dados trocados entre dois aparelhos em serviços e características, cada um identificado por um UUID (BLUETOOTH SIG, [s.d.]). |
| Serviço custom (BLE) | Um serviço GATT identificado por um UUID de 128 bits, criado por quem desenvolve o produto, em vez de um UUID de 16 bits já adotado oficialmente pelo Bluetooth SIG. |
| Característica (BLE) | Dentro de um serviço GATT, um valor específico que pode ser lido, escrito ou notificado — por exemplo, o identificador de uma etiqueta lida. |
| Distância de edição | O número mínimo de inserções, remoções ou substituições de caractere necessárias para transformar um texto em outro — a base do algoritmo usado na busca aproximada (LEVENSHTEIN, 1966). |
| Loja participante | Para efeito da política de verificação de desenvolvedor do Android (seção 6.3.3), uma das lojas de aplicativos explicitamente listadas por essa política como sujeitas ao prazo regional de setembro de 2026 (GOOGLE, [s.d.]a). **[REVISAR-EXTERNO]** — ver nota no início da seção 6.3.3. |

---

## 5. Convenção de identificação

Formato: `PD-[CATEGORIA]-[número]` (item de Projeto Detalhado). Reaproveita as siglas de categoria já usadas no Projeto Arquitetônico. A seção 9 daquele documento registra pendência em cinco categorias; só quatro voltam a aparecer aqui, porque são as que este documento resolve: Leitura e identificação de peça (LEI), Conectividade (CON), Importação de conteúdo (IMP), Navegação — ordenação e busca (NAV). A quinta, Retorno da tela (RET) — o texto legal do termo de consentimento, DA-RET-16 —, fica fora do escopo deste documento (ver §2.2), porque a própria seção 9 do Projeto Arquitetônico já registrou esse item como pendência de uma rodada própria, fora desta cascata. Nenhuma categoria nova foi criada nesta etapa.

Cada item indica, na coluna "Origem", a Decisão de Arquitetura (DA) ou o ponto da lista de pendências (seção 9 do Projeto Arquitetônico) que ele resolve.

---

## 6. Decisões de projeto detalhado por categoria

### 6.1 Leitura e identificação de peça (LEI)

| ID | Descrição | Origem |
|---|---|---|
| PD-LEI-01 | O módulo leitor do acessório externo (DA-LEI-03(b)) é o **NXP PN532/C1** (originalmente lançado pela Philips Semiconductors, hoje sob a NXP). Atende integralmente DA-LEI-01: opera a 13,56 MHz, modo leitor/escritor ISO/IEC 14443A, compatível com a base sobre a qual o NFC Forum define a etiqueta Tipo 2. Interfaces de comunicação com o microcontrolador hospedeiro disponíveis: SPI, I2C e UART de alta velocidade — a escolha entre elas é PD-LEI-03. Encapsulamento HVQFN40 (6×6 mm) (NXP SEMICONDUCTORS, 2011). | DA-LEI-03(b); DA-LEI-01 |
| PD-LEI-02 | O microcontrolador do acessório externo é o **Espressif ESP32-D0WD-V3** — núcleo duplo, Wi-Fi 802.11b/g/n e Bluetooth 4.2 BR/EDR e Bluetooth LE integrados no mesmo chip, dispensando um rádio BLE separado para atender DA-CON-02. Atenção: a variante sem o sufixo "-V3" (`ESP32-D0WD`) está marcada pelo próprio fabricante como "não recomendada para novos projetos" (Not Recommended for New Designs, NRND); a variante `-V3` (revisão de silício 3.0/3.14) segue como corrente. É essa distinção — não visível em nenhuma busca superficial pelo nome genérico "ESP32" — que fixa o número exato de peça aqui (ESPRESSIF SYSTEMS, [2026]). | DA-CON-02; DA-LEI-03(b) |
| PD-LEI-03 | A ligação física entre o PN532 e o ESP32-D0WD-V3 usa a interface **I2C**: o ESP32 tem duas controladoras I2C integradas, e o I2C exige menos linhas de sinal que o SPI (dois fios de dados contra quatro), reduzindo o número de trilhas e pontos de solda do acessório. O PN532 opera como periférico I2C; a seleção do modo de interface do PN532 é feita pelo estado dos pinos I0/I1 no boot, conforme descrito no manual do fabricante (NXP SEMICONDUCTORS, 2011, seção 8). | PD-LEI-01; PD-LEI-02 |

### 6.2 Conectividade (CON)

| ID | Descrição | Origem |
|---|---|---|
| PD-CON-01 | O serviço BLE entre acessório e aplicativo é o **Nordic UART Service (NUS)** — um serviço GATT *custom* (UUID de 128 bits, fora da faixa de 16 bits reservada a serviços adotados oficialmente pelo Bluetooth SIG), originado pela Nordic Semiconductor e hoje reimplementado por múltiplos fabricantes de rádio BLE, incluindo bibliotecas prontas para o ESP32. Adotar um serviço já amplamente implementado evita desenhar um protocolo próprio do zero — que precisaria da mesma validação de robustez que um serviço já testado em milhões de dispositivos já tem. Fica registrado que o NUS **não é um perfil GATT adotado oficialmente pelo Bluetooth SIG**: é uma convenção de fabricante que virou padrão de fato — distinção que este documento não esconde (NORDIC SEMICONDUCTOR ASA, 2018-). | DA-CON-02 |
| PD-CON-02 | UUIDs exatos do serviço (conforme a implementação de referência da própria Nordic Semiconductor): Serviço `6e400001-b5a3-f393-e0a9-e50e24dcca9e`; característica RX (escrita, do aplicativo para o acessório) `6e400002-b5a3-f393-e0a9-e50e24dcca9e`; característica TX (notificação, do acessório para o aplicativo) `6e400003-b5a3-f393-e0a9-e50e24dcca9e` (NORDIC SEMICONDUCTOR ASA, 2018-). Um UUID de 128 bits é exatamente o formato que a Especificação Central do Bluetooth reserva a serviços não adotados pelo Bluetooth SIG (BLUETOOTH SIG, [s.d.], seção 2.5.4). | PD-CON-01; BLUETOOTH SIG, [s.d.] |
| PD-CON-03 | O acessório opera como servidor GATT (papel *peripheral*, anunciando o serviço); o aparelho de jogo opera como cliente GATT (papel *central*, conectando-se ao acessório) — arranjo comum em aparelhos móveis, que tipicamente atuam como central. Cada leitura bem-sucedida de etiqueta pelo PN532 é enviada ao aplicativo como uma notificação na característica TX, contendo o identificador da etiqueta (o mesmo identificador associado a um fotograma no pacote de conteúdo — ver PD-IMP-01, campo `tag_id`). A característica RX (escrita) é reservada, seguindo o par completo do serviço padrão, ainda que nenhuma comunicação nesse sentido (aplicativo → acessório) seja exigida pelos requisitos atuais — manter o par completo, em vez de reimplementar só metade do serviço, preserva a compatibilidade com bibliotecas de cliente NUS já existentes dos dois lados. | PD-CON-01; PD-CON-02; DA-LEI-02 |
| PD-CON-04 | O tamanho máximo de dado transmitido por notificação numa única mensagem segue o MTU de ATT negociado na conexão, descontados os 3 bytes de cabeçalho do protocolo — no caso de um identificador de etiqueta (poucos bytes), isso nunca chega perto do limite, então nenhuma fragmentação de mensagem é necessária (NORDIC SEMICONDUCTOR ASA, 2018-). | PD-CON-01; PD-CON-02 |

### 6.3 Importação de conteúdo (IMP)

#### 6.3.1 Esquema do pacote de conteúdo (PD-IMP-01)

O arquivo de conteúdo (DA-IMP-01) segue o formato JSON Schema, Draft 2020-12 — a mesma convenção de contrato de dado já usada no restante do projeto (ver `modulos/_template/schemas/README.md`) — descrito pela IETF em rascunho de especificação de acesso aberto (INTERNET ENGINEERING TASK FORCE, 2022). O esquema completo:

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://nexo.example/schemas/pacote-de-conteudo.schema.json",
  "title": "Pacote de conteúdo do NEXO",
  "type": "object",
  "required": ["schema_version", "instance"],
  "additionalProperties": false,
  "properties": {
    "schema_version": {
      "type": "string",
      "const": "1.0.0"
    },
    "instance": { "$ref": "#/$defs/instance" }
  },
  "$defs": {
    "instance": {
      "type": "object",
      "required": ["name", "retention_period", "themes"],
      "additionalProperties": false,
      "properties": {
        "name": {
          "type": "string",
          "minLength": 1
        },
        "retention_period": {
          "type": "string",
          "pattern": "^P(?!$)(\\d+Y)?(\\d+M)?(\\d+D)?$"
        },
        "themes": {
          "type": "array",
          "minItems": 1,
          "items": { "$ref": "#/$defs/theme" }
        }
      }
    },
    "theme": {
      "type": "object",
      "required": ["name", "ordering", "events"],
      "additionalProperties": false,
      "properties": {
        "name": { "type": "string", "minLength": 1 },
        "ordering": {
          "type": "string",
          "enum": ["ordered", "standalone"]
        },
        "position": {
          "type": "integer",
          "minimum": 1
        },
        "events": {
          "type": "array",
          "minItems": 1,
          "items": { "$ref": "#/$defs/event" }
        }
      },
      "allOf": [
        {
          "if": { "properties": { "ordering": { "const": "ordered" } } },
          "then": { "required": ["position"] }
        },
        {
          "if": { "properties": { "ordering": { "const": "standalone" } } },
          "then": { "not": { "required": ["position"] } }
        }
      ]
    },
    "event": {
      "type": "object",
      "required": ["name", "ordering", "zero_mark", "hint_enabled", "frames"],
      "additionalProperties": false,
      "properties": {
        "name": { "type": "string", "minLength": 1 },
        "ordering": {
          "type": "string",
          "enum": ["ordered", "standalone"]
        },
        "position": {
          "type": "integer",
          "minimum": 1
        },
        "zero_mark": {
          "type": "object",
          "required": ["image"],
          "additionalProperties": false,
          "properties": {
            "image": {
              "type": "string",
              "minLength": 1
            }
          }
        },
        "hint_enabled": {
          "type": "boolean"
        },
        "hint_content": {
          "type": "string",
          "minLength": 1
        },
        "frames": {
          "type": "array",
          "minItems": 1,
          "items": { "$ref": "#/$defs/frame" }
        }
      },
      "allOf": [
        {
          "if": { "properties": { "ordering": { "const": "ordered" } } },
          "then": { "required": ["position"] }
        },
        {
          "if": { "properties": { "ordering": { "const": "standalone" } } },
          "then": { "not": { "required": ["position"] } }
        },
        {
          "if": { "properties": { "hint_enabled": { "const": true } } },
          "then": { "required": ["hint_content"] }
        },
        {
          "if": { "properties": { "hint_enabled": { "const": false } } },
          "then": { "not": { "required": ["hint_content"] } }
        }
      ]
    },
    "frame": {
      "type": "object",
      "required": ["tag_id", "image"],
      "additionalProperties": false,
      "properties": {
        "tag_id": {
          "type": "string",
          "pattern": "^[0-9A-F]+$"
        },
        "image": {
          "type": "string",
          "minLength": 1
        },
        "confirmation_text": {
          "type": "string"
        }
      }
    }
  }
}
```

Nota sobre unicidade: JSON Schema, por desenho, valida a estrutura de um documento isolado — não consegue expressar sozinho uma regra do tipo "nenhum `tag_id` se repete em todo o pacote" (essa regra atravessa vários pontos da árvore). Essa checagem é feita pelo motor no momento da importação, como parte da validação já prevista em DA-CFG-01 — o esquema acima cobre a forma de cada item; a checagem de unicidade cobre a relação entre itens.

| ID | Descrição | Origem |
|---|---|---|
| PD-IMP-01 | Esquema acima adotado como o esquema exato do pacote de conteúdo. Nomes de campo em inglês (`name`, `ordering`, `frames`, etc.) — decisão deste documento: manter compatibilidade com o vocabulário do próprio padrão JSON Schema e das bibliotecas de validação (`fastjsonschema`, já referenciada em `modulos/_template/schemas/README.md`), que são, elas mesmas, documentadas em inglês. Idioma de campo é uma escolha de formato de dado, não comunicação com pessoa — não está sujeita a nenhuma convenção de linguagem simples e sem jargão que rege a comunicação em torno deste projeto. | DA-IMP-01; Doc. Projeto Arquitetônico §9 |
| PD-IMP-02 | Cada instância, tema, evento e fotograma do pacote é validado individualmente na importação, item a item, reaproveitando a regra já fixada em DA-CFG-01/02/03: um item com erro de esquema (campo obrigatório ausente, tipo errado, `tag_id` duplicado) é recusado sozinho, sem impedir a importação do restante do pacote; o motor aponta o caminho exato do item recusado (ex.: `instance.themes[2].events[0].frames[5]`) e o motivo. | DA-CFG-01; DA-CFG-03 |

#### 6.3.2 Leitura do arquivo ZIP (PD-IMP-03)

| ID | Descrição | Origem |
|---|---|---|
| PD-IMP-03 | A leitura do pacote de conteúdo (arquivo ZIP — DA-IMP-02) usa a classe `java.util.zip.ZipFile`, parte do próprio SDK do Android/Java, sem nenhuma biblioteca externa. `ZipFile` foi escolhida em vez de `ZipInputStream` porque dá acesso aleatório por nome de entrada — necessário aqui, já que o motor precisa localizar uma imagem específica (referenciada por caminho relativo dentro do JSON, ver PD-IMP-01) sem precisar percorrer o arquivo inteiro sequencialmente toda vez (GOOGLE, [s.d.]b). Como o motor nunca cria um pacote de conteúdo (isso é tarefa de uma ferramenta de autoria, fora do escopo desta rodada — ver Premissas, seção 8), `ZipOutputStream` não é necessária do lado do motor. | DA-IMP-02 |

#### 6.3.3 Distribuição do aplicativo (PD-IMP-04)

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** todo item abaixo marcado com `[REVISAR-EXTERNO]` descreve uma regra de um terceiro fora do controle do NEXO (a política de instalação de aplicativos do Android, definida pela Google) — nunca uma decisão deste projeto. Regra de terceiro pode mudar sem aviso, a qualquer momento, inclusive depois deste documento ser dado como pronto. Quem ler este documento depois — inclusive em uma sessão futura — deve tratar todo conteúdo marcado como **possivelmente desatualizado** e reconfirmar na fonte oficial (seção 10, Referências) antes de agir sobre ele (comprar conta de desenvolvedor, planejar distribuição em escala, etc.), em vez de tomar o texto abaixo como fato permanente.

| ID | Descrição | Origem |
|---|---|---|
| PD-IMP-04 | O aplicativo é distribuído como arquivo instalável direto (APK), transferido e instalado manualmente — o mesmo padrão já fixado para o pacote de conteúdo em DA-IMP-04/05, sem loja, sem conta de desenvolvedor obrigatória, sem custo de licença. Desde o Android 8 (Oreo), a instalação de um APK fora de uma loja exige que a pessoa conceda, uma única vez, a permissão "Instalar apps desconhecidos" ao aplicativo usado para abrir o arquivo (gerenciador de arquivo, navegador) — substituiu o antigo interruptor global "Fontes desconhecidas" por uma permissão concedida app a app, desenhada para dificultar que alguém seja induzido a instalar algo sem essa concessão explícita (CUNNINGHAM, 2017). | DA-IMP-04; DA-IMP-05; DA-IMP-06 |
| PD-IMP-05 | `[REVISAR-EXTERNO]` Fica registrada, só para acompanhamento, uma mudança de política do Android anunciada em 2026, vigente **na data de acesso das fontes abaixo (12 ago. 2026)** — não uma decisão deste documento: a partir de 30 de setembro de 2026, aparelhos certificados passam a exigir que aplicativos distribuídos por lojas participantes explicitamente listadas (Google Play, HONOR App Market, OPPO App Market, Galaxy Store, Palm Store, V-Appstore, GetApps) venham de um desenvolvedor verificado — com expansão global prevista para 2027 (GOOGLE, [s.d.]a). Quatro pontos, confirmados na fonte oficial na mesma data de acesso: (1) o prazo de setembro de 2026 vale só para as lojas participantes listadas — instalação direta de APK (PD-IMP-04) e lojas não listadas não são afetadas nessa fase inicial; (2) instalação via ADB (ferramenta de linha de comando para desenvolvedor) segue livre de verificação, a qualquer momento; (3) mesmo depois da expansão global de 2027, instalar sem verificação continua possível — não vira proibido, fica com mais fricção, por um "fluxo avançado" único (ativar modo desenvolvedor, esperar 24 horas, reconfirmar por biometria ou PIN, então liberar por 7 dias ou indefinidamente) (FORSYTHE, 2026); (4) existe uma conta de desenvolvedor gratuita, sem exigência de documento de identidade, para quem distribui a até 20 aparelhos — suficiente para piloto ou sala de aula isolada, insuficiente para distribuição maior (ponto que volta a aparecer nas Premissas, seção 8, e em PD-IMP-07 abaixo). | DA-IMP-06; GOOGLE, [s.d.]a; GOOGLE, [s.d.]c; FORSYTHE, 2026 |
| PD-IMP-07 | `[REVISAR-EXTERNO]` Existe um caminho de conta de desenvolvedor verificada que **não** exige distribuir pela Google Play: o "Android Developer Console", com taxa única (não recorrente) de US$ 25 — mesmo valor já cobrado hoje pelo cadastro na Play Console, só que sem obrigar publicação na loja. Registrado aqui como a opção mais provável, quando o NEXO crescer além de piloto (mais de 20 aparelhos, limite da conta gratuita de PD-IMP-05), porque uma taxa única de US$ 25 tende a custar menos, em atrito de uso, do que pedir que cada pessoa que instale o aplicativo repita o fluxo avançado de 24 horas descrito em PD-IMP-05. **Nenhuma decisão foi tomada aqui** — só o registro de que a opção existe e de qual é o custo exato, confirmado na fonte oficial na data de acesso. | DA-IMP-06; GOOGLE, [s.d.]a; GOOGLE, [s.d.]c |

### 6.4 Navegação — busca aproximada (NAV)

| ID | Descrição | Origem |
|---|---|---|
| PD-NAV-01 | O algoritmo usado para tolerar pequena diferença de digitação (DA-NAV-03) é a **distância de edição de Levenshtein** — o número mínimo de inserções, remoções ou substituições de um caractere necessárias para transformar o termo buscado no nome cadastrado, publicado originalmente por Vladimir Levenshtein (LEVENSHTEIN, 1966). É o algoritmo de referência para esse tipo de comparação — o próprio conceito de "distância de edição" usado pela literatura de ciência da computação deriva diretamente desse trabalho. | DA-NAV-03 |
| PD-NAV-02 | Um nome cadastrado entra no resultado da busca quando sua distância de Levenshtein até o termo digitado for menor ou igual a um limiar — este documento fixa o limiar em 20% do comprimento do termo digitado, arredondado para baixo, com mínimo de 1 (ex.: termo de 4 a 5 caracteres tolera 1 erro; termo de 10 a 14 caracteres tolera 2). Diferente dos itens anteriores desta seção, este número não vem de nenhuma norma ou fonte externa — é uma escolha de engenharia deste documento, proporcional ao tamanho do termo (termos curtos toleram proporcionalmente menos erro, para não devolver resultados sem relação nenhuma com o que foi digitado). Fica registrado como parâmetro ajustável na implementação, não como constante rígida. | PD-NAV-01 |

---

## 7. Restrições

- Este documento não decide layout, cor ou fonte de nenhuma tela — nunca entra em nenhuma etapa do motor (ver Projeto Arquitetônico, §7).
- O esquema em PD-IMP-01 não valida unicidade de `tag_id` nem de nome entre irmãos — essas checagens acontecem no motor durante a importação (DA-CFG-01), não no arquivo de esquema em si.
- PD-LEI-01 e PD-LEI-02 fixam peças específicas para o acessório leitor externo (DA-LEI-03(b)) — não têm relação com o caminho (a) de leitura direta pela antena do próprio aparelho de jogo (DA-LEI-03(a)), que não depende de nenhum componente adicional.
- O texto legal do termo de consentimento (DA-RET-16) permanece fora do escopo de todo este documento, sem exceção — ver seção 2.2.
- PD-IMP-05 registra uma política externa (verificação de desenvolvedor do Android) como algo a acompanhar, não como uma decisão de arquitetura tomada por este documento — o motor não depende dela para funcionar na forma de distribuição escolhida em PD-IMP-04.

---

## 8. Premissas

- A ferramenta de autoria de conteúdo (interface para preencher o esquema de PD-IMP-01) ainda não existe e fica fora do escopo desta rodada — hoje, montar um pacote de conteúdo válido exige escrever o JSON à mão ou com um editor de texto simples, apoiado pelo próprio esquema para validação (mesma premissa já registrada no Projeto Arquitetônico, §8).
- O limiar de PD-NAV-02 é um ponto de partida razoável, não testado ainda com usuários reais — ajustá-lo depois de observação de uso real é esperado, não uma falha do valor inicial.
- `[REVISAR-EXTERNO]` A migração de uma conta de distribuição limitada (até 20 aparelhos, PD-IMP-05) para uma conta de distribuição plena (PD-IMP-07) é aceita pela política do Android; o caminho inverso não é — relevante caso o NEXO cresça além de uso em piloto (GOOGLE, [s.d.]c).
- `[REVISAR-EXTERNO]` Todo item marcado `[REVISAR-EXTERNO]` neste documento (seção 6.3.3 e este item) descreve política de terceiro, não decisão do NEXO — antes de qualquer decisão real de distribuição (comprar conta, escolher canal), reconfirmar na fonte oficial se o texto ainda bate com a regra vigente naquele momento, já que a data de acesso registrada (12 ago. 2026) pode estar defasada.

---

## 9. Itens deferidos à Implementação

- Código-fonte de cada componente decidido aqui: firmware do acessório (PN532 + ESP32-D0WD-V3), lógica de importação e validação do pacote de conteúdo no aplicativo, lógica de busca aproximada.
- Testes de unidade, integração, sistema e aceitação, seguindo a cascata ascendente descrita em `docs/prompt model.txt`.
- Empacotamento final do instalável (APK assinado) e, se aplicável, registro de desenvolvedor verificado (PD-IMP-07) — decisão a confirmar na fonte oficial vigente naquele momento, dado o marcador `[REVISAR-EXTERNO]` que cobre esse item.
- Validação em campo do limiar de busca aproximada (PD-NAV-02) com usuários reais.

---

## 10. Referências

Fontes externas consultadas para as decisões desta etapa, no formato definido pela norma ABNT NBR 6023 (Informação e documentação — Referências). Citadas no corpo do documento como (AUTOR, ano).

BLUETOOTH SIG. **Bluetooth Core Specification — Part G: Generic Attribute Profile (GATT)**. Version 5.4. [S.l.], [s.d.]. Disponível em: https://www.bluetooth.com/wp-content/uploads/Files/Specification/HTML/Core-54/out/en/host/generic-attribute-profile--gatt-.html. Acesso em: 12 ago. 2026.

CUNNINGHAM, Edward. **Making it safer to get apps on Android O**. Android Developers Blog, 22 ago. 2017. Disponível em: https://android-developers.googleblog.com/2017/08/making-it-safer-to-get-apps-on-android-o.html. Acesso em: 12 ago. 2026.

ESPRESSIF SYSTEMS. **ESP32 Series Datasheet**. Version 5.3. Shanghai, [2026]. Disponível em: https://www.espressif.com/sites/default/files/documentation/esp32_datasheet_en.pdf. Acesso em: 12 ago. 2026.

FORSYTHE, Matthew. **Android developer verification: Balancing openness and choice with safety**. Android Developers Blog, 19 mar. 2026. Disponível em: https://android-developers.googleblog.com/2026/03/android-developer-verification.html. Acesso em: 12 ago. 2026.

GOOGLE. **Android developer verification**. Android Developers, [s.d.]a. Disponível em: https://developer.android.com/developer-verification. Acesso em: 12 ago. 2026.

GOOGLE. **java.util.zip — API reference**. Android Developers, [s.d.]b. Disponível em: https://developer.android.com/reference/java/util/zip/package-summary. Acesso em: 12 ago. 2026.

GOOGLE. **Frequently asked questions — Android developer verification**. Android Developers, [s.d.]c. Disponível em: https://developer.android.com/developer-verification/guides/faq. Acesso em: 12 ago. 2026.

INTERNET ENGINEERING TASK FORCE. **JSON Schema: A Media Type for Describing JSON Documents** (draft-bhutton-json-schema-01). A. Wright, H. Andrews, B. Hutton, G. Dennis (ed.). [S.l.], 16 jun. 2022. Disponível em: https://json-schema.org/draft/2020-12/json-schema-core. Acesso em: 12 ago. 2026.

LEVENSHTEIN, V. I. **Binary codes capable of correcting deletions, insertions, and reversals**. Soviet Physics Doklady, v. 10, n. 8, p. 707-710, 1966. Tradução de: Двоичные коды с исправлением выпадений, вставок и замещений символов. Doklady Akademii Nauk SSSR, v. 163, n. 4, p. 845-848, 1965.

NORDIC SEMICONDUCTOR ASA. **nus.h — Nordic UART (NUS) GATT Service API** [código-fonte]. In: nRF Connect SDK (sdk-nrf), branch main. [S.l.], 2018-. Disponível em: https://github.com/nrfconnect/sdk-nrf/blob/main/include/bluetooth/services/nus.h. Acesso em: 12 ago. 2026.

NXP SEMICONDUCTORS (anteriormente Philips Semiconductors). **PN532/C1 — NFC controller: short form data sheet**. Rev. 1.2. [S.l.], 2011. Disponível em: https://cdn-shop.adafruit.com/datasheets/pn532ds.pdf. Acesso em: 12 ago. 2026.

---

## 11. Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 12-08-2026 | Criação inicial: modelo exato do módulo NFC e do microcontrolador do acessório leitor (PN532/C1 + ESP32-D0WD-V3, com a ressalva de descontinuação da variante sem "-V3"), serviço BLE exato entre acessório e aplicativo (Nordic UART Service, com UUIDs), esquema JSON completo do pacote de conteúdo, mecanismo exato de leitura do ZIP no Android (`java.util.zip.ZipFile`), algoritmo exato de busca aproximada (distância de Levenshtein, com limiar proposto) e forma exata de distribuição do aplicativo (instalação direta, com registro da política de verificação de desenvolvedor do Android e da opção paga fora da Play Store, ambas marcadas `[REVISAR-EXTERNO]` por dependerem de regra de terceiro que pode mudar sem aviso). Resolve os itens da seção 9 do Projeto Arquitetônico v0.1.0 que cabem a esta etapa — o texto legal do termo de consentimento (DA-RET-16), sétimo item daquela seção, permanece fora do escopo deste documento (ver §2.2). | Projeto Detalhado |
| 1.0.0 | 12-08-2026 | Documento aprovado — primeira versão estável do Projeto Detalhado do módulo Conceito Geral (Motor). | Aprovação da cascata do motor |
| 1.0.1 | 13-08-2026 | PD-IMP-01: removida menção a uma regra de arquivo de configuração de ferramenta (CLAUDE.md), sem documento próprio explicando seu uso dentro do projeto e sem sentido fora desse contexto; mantida a decisão em si (nomes de campo em inglês) e o motivo técnico (vocabulário do padrão JSON Schema e de `fastjsonschema`). | Correção de redação |
