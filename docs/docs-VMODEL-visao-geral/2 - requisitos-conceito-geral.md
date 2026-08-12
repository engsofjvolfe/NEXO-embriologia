# NEXO — Documento de Requisitos
## Módulo: Conceito Geral (Motor)

| Campo | Valor |
|---|---|
| Projeto | NEXO |
| Módulo | Conceito Geral (Motor) |
| Etapa (V-Model) | Requisitos |
| Documento de origem (normativo) | `1 - documento-de-conceito-geral.md` |
| Versão | 1.0.0 |
| Data | 12-08-2026 |
| Situação | Aprovado |
| Licença | Todos os direitos reservados — ver [LICENSE](../../LICENSE) |

---

## 1. Objetivo

Definir, a partir do documento de conceito do NEXO, o conjunto de requisitos que o motor precisa atender para funcionar como um simulador tátil de sequências, agnóstico a qualquer disciplina ou conteúdo específico.

Este documento trata exclusivamente do motor — a estrutura que qualquer instância do NEXO vai usar. Ele não trata do conteúdo de nenhuma instância.

---

## 2. Escopo

### 2.1 Dentro do escopo
- A hierarquia genérica de conteúdo (instância, tema, evento, sequência) e as regras de ordem entre seus níveis.
- A estrutura de sequência (fotogramas, ordem, avanço) e a lógica de validação de tentativa.
- As regras de erro, dica, sugestão de estudo e pular.
- As regras de retorno da tela ao usuário.
- As regras de composição, início, encadeamento e interrupção (pausa, ocioso, saída) de uma sessão de uso.
- As regras de registro e relatório de uma sessão.
- Os requisitos que tornam o motor independente de conteúdo e extensível a novas instâncias.

### 2.2 Fora do escopo
- Conteúdo de qualquer instância específica (nomes de temas, eventos, textos, imagens, número de fotogramas de uma sequência real).
- Escolha visual, artística ou de interface de qualquer peça ou tela.
- Tecnologia ou plataforma de implementação (hardware de leitura, banco de dados, formato exato de exportação).
- Valores numéricos padrão de configuração (quantos erros até a dica aparecer, duração exata de ociosidade, etc.) — cada conteúdo define os seus; o motor não impõe um padrão.
- Procedimento detalhado de conformidade com a LGPD (consentimento, retenção, exclusão) — cabe à Especificação.

---

## 3. Documentos relacionados

| Documento | Papel em relação a este documento |
|---|---|
| `1 - documento-de-conceito-geral.md` | Fonte normativa. Todos os requisitos abaixo derivam exclusivamente dele. |

---

## 4. Definições e terminologia

| Termo | Definição |
|---|---|
| Motor | A estrutura genérica do NEXO, sem conteúdo próprio, descrita no documento de conceito. |
| Instância | A aplicação do motor a uma área de conteúdo; nível mais amplo da hierarquia. |
| Tema | Um recorte dentro de uma instância. |
| Evento | Um processo específico dentro de um tema. |
| Sequência | A lista ordenada de fotogramas que compõe um evento. |
| Fotograma | Um estado de um processo, congelado em um instante específico. |
| Peça | A representação física de um fotograma, entregue fora de ordem. |
| Marco zero | Imagem própria de um evento, sem carta física, que representa o estado antes do primeiro fotograma daquele evento. |
| Tentativa | O ato de apresentar uma peça como candidata à próxima posição da sessão em curso. |
| Sessão | Uma instância de uso do motor — cobre uma sequência, um recorte contíguo de eventos, ou um recorte contíguo de temas encadeáveis. |
| Ponto de início | O fotograma a partir do qual uma sessão começa. Por padrão, o primeiro fotograma do primeiro evento da sessão. |
| Dica | Auxílio opcional oferecido após erro repetido na mesma posição. |
| Sugestão de estudo | Recomendação, não obrigatória, para sair e estudar um tema, oferecida quando a dica não é suficiente. |
| Pular | Mecanismo que avança a sessão sem que a peça correta tenha sido encontrada, deixando aquela posição perdida naquela sessão. |
| Resumo (ou síntese) | Texto apresentado ao final de um evento ou de uma cadeia de eventos, amarrando os fotogramas do trecho jogado. |
| Pausa / Ocioso | Interrupções que guardam o estado de uma sessão para retomada posterior. |
| Registro | Dados coletados sobre uma sessão: configuração inicial e eventos ocorridos durante o jogo. |
| Relatório | Documento gerado a partir do registro, ao final de uma sessão. |

---

## 5. Convenção de identificação dos requisitos

Formato: `RF-[CATEGORIA]-[número]` para requisitos funcionais, `RNF-[CATEGORIA]-[número]` para requisitos não funcionais.

| Categoria | Sigla |
|---|---|
| Hierarquia de conteúdo | HIE |
| Estrutura de sequência | EST |
| Interação do usuário | INT |
| Validação | VAL |
| Erro e tentativa | ERR |
| Pular | PUL |
| Retorno da tela | RET |
| Dica e sugestão de estudo | DIC |
| Composição de sessão | SES |
| Encadeamento e resumos | ENC |
| Pausa, ocioso e saída | PAU |
| Registro e relatório | REG |
| Configuração | CFG |
| Modularidade | MOD |
| Aprendizagem | APR |
| Privacidade | PRI |
| Navegação e entrada | NAV |
| Conectividade | CON |

Cada requisito indica sua origem como `§N`, referente à seção correspondente do documento de conceito.

**Nota sobre origem:** o conceito descreve a mecânica de jogo, mas, por escolha deliberada, não desce ao nível de como uma pessoa chega até um tema ou instância antes de começar a jogar, nem ao nível de que ambiente técnico o motor precisa pra funcionar. Os requisitos das categorias NAV e CON, por isso, não citam uma seção do conceito: a coluna "Origem" indica isso explicitamente, em vez de apontar para um `§N` que não existe. NAV cobre a entrada e a navegação até uma sessão começar; CON cobre a exigência de o motor funcionar sem depender de internet, algo que a escolha de tecnologia (fora do escopo deste documento, ver §2.2) torna necessário deixar registrado aqui. Isso é uma exceção, não a regra — todo outro requisito deste documento deriva do conceito normalmente.

---

## 6. Requisitos Funcionais

### 6.1 Hierarquia de conteúdo

| ID | Descrição | Origem |
|---|---|---|
| RF-HIE-01 | O conteúdo se organiza em quatro níveis fixos: instância, tema, evento e sequência. | §2 |
| RF-HIE-02 | Conteúdo de instâncias diferentes nunca se mistura. | §2 |
| RF-HIE-03 | O que se encaixa em cada nível da hierarquia é decisão de quem monta o conteúdo, podendo variar entre aplicações do motor. | §1, §2 |
| RF-HIE-04 | Dentro de um tema, os eventos podem ter ordem entre si ou ser avulsos. Dentro de uma instância, os temas podem ter ordem entre si ou ser avulsos. Essa decisão é tomada por quem organiza o conteúdo, no momento em que cada tema ou evento é criado — não de uma vez para toda a instância. | §2 |
| RF-HIE-05 | Fotogramas dentro de uma sequência são sempre estritamente ordenados; a possibilidade de itens avulsos não existe nesse nível. | §2 |

### 6.2 Estrutura de sequência

| ID | Descrição | Origem |
|---|---|---|
| RF-EST-01 | Uma sequência é uma lista ordenada de fotogramas de um evento. | §3, §4 |
| RF-EST-02 | A quantidade de fotogramas de uma sequência não é fixa; cada uma define a própria quantidade. | §4 |
| RF-EST-03 | A relação entre dois fotogramas consecutivos é de sucessão. O motor não representa nem valida relação de causa entre eles. | §4 |

### 6.3 Interação do usuário

| ID | Descrição | Origem |
|---|---|---|
| RF-INT-01 | As peças de uma sequência são entregues fora da ordem em que ocorrem. | §3 |
| RF-INT-02 | Quem joga tenta posicionar uma peça; o motor não revela a ordem correta antes da tentativa. | §3, §8 |
| RF-INT-03 | O motor identifica peças uma de cada vez. Não há leitura simultânea de mais de uma peça, nem validação baseada em posição espacial entre peças. | §6 |

### 6.4 Validação

| ID | Descrição | Origem |
|---|---|---|
| RF-VAL-01 | A validação verifica somente se a peça apresentada corresponde à próxima posição esperada da sessão em curso. | §6 |
| RF-VAL-02 | Se a peça corresponder, a sessão avança. Se não corresponder, a tentativa é rejeitada e a sessão permanece no ponto em que estava. | §6 |
| RF-VAL-03 | Qualquer peça que não seja a esperada naquele ponto é tratada da mesma forma — inclusive peças de outro evento, de outro tema, ou já utilizadas antes. | §6 |

### 6.5 Erro e tentativa

| ID | Descrição | Origem |
|---|---|---|
| RF-ERR-01 | Não há limite de tentativas erradas, nem intervalo mínimo obrigatório entre uma tentativa e a próxima. | §6 |
| RF-ERR-02 | Cada evento mantém uma contagem de erros que nunca zera durante a sessão — acumula do início ao fim, junto com a posição em que cada erro ocorreu. | §6 |

### 6.6 Pular

| ID | Descrição | Origem |
|---|---|---|
| RF-PUL-01 | Pular é uma possibilidade separada da lógica normal de validação, não um terceiro resultado de tentativa. | §7 |
| RF-PUL-02 | O uso esperado de pular só acontece depois de tentativa (e dica, se disponível) sem sucesso; o motor não recomenda o uso. | §7, §9 |
| RF-PUL-03 | A capacidade de pular é escolhida por quem inicia a sessão, no momento em que ela começa — não é um parâmetro fixado previamente no conteúdo. | §7 |
| RF-PUL-04 | Pular funciona peça a peça, a qualquer momento, sem limite de quantidade. | §7 |
| RF-PUL-05 | Uma peça pulada fica perdida naquela sessão; a sessão continua sem ela. | §7 |
| RF-PUL-06 | Havendo pulo em um evento, o resumo normal desse evento é substituído por uma mensagem que lista o que foi respondido, aponta o intervalo sem resposta e sugere estudo, sem revelar o conteúdo pulado. Essa mensagem aparece uma única vez, no evento em que o pulo ocorreu; não é repetida na síntese da cadeia (RF-ENC-03). | §7 |

### 6.7 Retorno da tela

| ID | Descrição | Origem |
|---|---|---|
| RF-RET-01 | Não há explicação antes ou durante a tentativa. | §8 |
| RF-RET-02 | Ao errar, a tela sinaliza apenas que a peça não corresponde à posição esperada, sem indicar qual seria a peça certa nem o motivo do erro. | §8 |
| RF-RET-03 | Ao acertar, a tela exibe um texto curto que situa aquele fotograma, funcionando de forma independente de fotogramas anteriores. | §8 |
| RF-RET-04 | Ao final de uma sessão sem peças puladas, a tela apresenta uma síntese do trecho jogado — do ponto em que a sessão começou até o fim. | §8 |

### 6.8 Dica e sugestão de estudo

| ID | Descrição | Origem |
|---|---|---|
| RF-DIC-01 | Se a pessoa errar repetidamente a mesma posição, o motor pode oferecer uma dica. | §9 |
| RF-DIC-02 | O conteúdo de cada dica — quanto ela revela — é configurável, definido por quem monta o conteúdo. | §9 |
| RF-DIC-03 | Se, mesmo com a dica, a pessoa continuar sem acertar, o motor pode sugerir sair e estudar o tema, sem obrigar. | §9 |
| RF-DIC-04 | O limiar de erros que libera a dica, e o limiar que libera a sugestão de estudo, são definidos na configuração da sessão — mesma regra usada para a disponibilidade de pular (RF-PUL-03) e para o tempo de ociosidade (RF-PAU-06) — podendo variar de sessão para sessão dentro do mesmo evento. | §7, §9, §12 |

### 6.9 Marco zero e ponto de início

| ID | Descrição | Origem |
|---|---|---|
| RF-SES-01 | Cada evento tem seu próprio marco zero: uma imagem sem carta física, representando o estado antes do primeiro fotograma. | §5 |
| RF-SES-02 | Por padrão, uma sessão começa no primeiro fotograma de um evento; quem joga pode escolher começar em outro ponto já existente. | §5 |
| RF-SES-03 | Fotogramas anteriores ao ponto de início escolhido não precisam existir fisicamente naquela sessão. | §5, §6 |
| RF-SES-04 | A referência mostrada antes da primeira peça de uma sessão é: o fotograma imediatamente anterior ao ponto escolhido, se houver; o marco zero do evento, se o evento é ponto de entrada da sessão e ela começa no primeiro fotograma; ou a última peça preenchida do evento anterior, se o evento vem encadeado — ou o marco zero do evento atual, se nenhuma peça do evento anterior foi preenchida. | §5 |

### 6.10 Composição de sessão

| ID | Descrição | Origem |
|---|---|---|
| RF-SES-05 | Uma sessão pode cobrir a sequência inteira de um único evento. | §10 |
| RF-SES-06 | Uma sessão pode cobrir um recorte contíguo de eventos dentro do mesmo tema, nunca alternado. | §10 |
| RF-SES-07 | Uma sessão pode cobrir um recorte contíguo de temas encadeáveis dentro da mesma instância, seguindo a mesma regra de contiguidade, podendo atravessar um tema inteiro e continuar no próximo. | §10 |
| RF-SES-08 | Essa composição só é possível onde existe ordem declarada; um tema ou evento avulso não se encadeia com outro dentro da mesma sessão. | §2, §10 |

### 6.11 Encadeamento e resumos

| ID | Descrição | Origem |
|---|---|---|
| RF-ENC-01 | Ao final de um evento dentro de uma cadeia, aparece o resumo daquele evento e, se houver próximo evento, um controle para continuar. | §11 |
| RF-ENC-02 | Ao continuar, muda apenas o nome do evento exibido; a próxima peça já é aguardada, sem imagem de transição — a referência é a última peça do evento anterior, não o marco zero do novo evento. | §5, §11 |
| RF-ENC-03 | Ao final de uma cadeia com mais de um evento, existe uma síntese que cobre a cadeia inteira, além dos resumos individuais, sem substituir nenhum deles. Essa síntese é a narrativa contínua quando não houve pulo em nenhum evento da cadeia; havendo pulo em algum deles, ela se limita a um total de posições preenchidas e perdidas na cadeia inteira, sem repetir a mensagem de RF-PUL-06. | §11 |

### 6.12 Pausa, ocioso e saída

| ID | Descrição | Origem |
|---|---|---|
| RF-PAU-01 | Ociosidade e pausa têm o mesmo efeito: o estado é guardado e retomado exatamente de onde parou. A diferença entre as duas é apenas o que dispara cada uma. | §12 |
| RF-PAU-02 | Desligar o sistema sem sair conta como pausa. | §12 |
| RF-PAU-03 | Sair apaga o progresso retomável da sessão e exige confirmação antes de ser efetivado. | §12 |
| RF-PAU-04 | Sair não apaga o relatório da sessão; um relatório é gerado cobrindo o que aconteceu até aquele momento. | §12, §14 |
| RF-PAU-05 | O motor nunca mantém mais de uma sessão ativa ou pausada ao mesmo tempo. | §12 |
| RF-PAU-06 | O tempo sem interação que caracteriza ociosidade é escolhido na configuração da sessão — mesma regra usada para dica e sugestão de estudo (RF-DIC-04) e para a disponibilidade de pular (RF-PUL-03): pode variar de sessão para sessão, mesmo dentro do mesmo evento. | §12 |

### 6.13 Registro e relatório

| ID | Descrição | Origem |
|---|---|---|
| RF-REG-01 | O motor pode manter um registro de sessão: configurações escolhidas no início, e cada evento ocorrido durante o jogo — erro, pulo, pausa ou ociosidade — junto com a posição e o momento em que ocorreu. | §14 |
| RF-REG-02 | Um relatório é gerado ao final de cada sessão, inclusive quando encerrada por "sair" antes do fim. | §14 |
| RF-REG-03 | Dados que identificam a pessoa são opcionais, registrados apenas mediante consentimento e sujeitos a aprovação de comitê de ética aplicável ao contexto de uso. | §14 |
| RF-REG-04 | Dados do jogo em si (erros, posições, configuração usada) são sempre registrados, com ou sem identificação pessoal. | §14 |
| RF-REG-05 | O relatório de uma sessão é guardado no aparelho onde a sessão aconteceu, em mais de um formato legível, sem exigir login nem autenticação de quem jogou. | §14 |
| RF-REG-06 | Quem jogou acessa o próprio relatório livremente, quantas vezes quiser, direto no aparelho onde foi gerado — não existe um papel de administração com acesso automático, central ou permanente a esse relatório. | §14 |
| RF-REG-07 | Compartilhar o relatório com terceiros é uma ação voluntária de quem jogou, feita depois que o relatório já foi entregue a ela; o motor nunca compartilha ou envia esse dado por conta própria. | §14 |

### 6.14 Configuração

| ID | Descrição | Origem |
|---|---|---|
| RF-CFG-01 | Todos os parâmetros de sequência, evento, tema e instância (marco zero, quantidade de fotogramas, posições, textos, ordem/avulso, conteúdo da dica) são definidos manualmente por quem monta o conteúdo. Nada é inferido automaticamente pelo motor. Três parâmetros não entram nessa lista, por serem escolhidos por quem inicia a sessão, a cada sessão (RF-PUL-03, RF-DIC-04, RF-PAU-06): a disponibilidade de pular, o limiar de erro que libera a dica e a sugestão de estudo, e o tempo de ociosidade. | §15 |
| RF-CFG-02 | Cada aplicação do motor a um conteúdo é uma instância com nome próprio. | §2, §15 |

### 6.15 Navegação e entrada

| ID | Descrição | Origem |
|---|---|---|
| RF-NAV-01 | Antes de iniciar uma sessão, a pessoa escolhe, entre o que está disponível, qual instância, tema ou evento abrir, respeitando a hierarquia (instância → tema → evento). | Sem seção no conceito — ver nota da seção 5 |
| RF-NAV-02 | Existindo uma sessão pausada, o motor não a descarta automaticamente para abrir espaço a uma nova: exige que a pessoa escolha entre retomar essa sessão ou sair dela antes de qualquer nova sessão começar. | RF-PAU-05; sem seção no conceito — ver nota da seção 5 |

---

## 7. Requisitos Não Funcionais

| ID | Descrição | Origem |
|---|---|---|
| RNF-MOD-01 | O motor não conhece nenhuma disciplina específica; opera apenas sobre a hierarquia e a mecânica descritas no conceito. | §15 |
| RNF-MOD-02 | Uma nova instância, sobre qualquer assunto, deve poder ser adicionada sem qualquer alteração na lógica central do motor. | §15 |
| RNF-APR-01 | A repetição de tentativas é o mecanismo de aprendizagem do motor, não um efeito colateral a ser evitado (ROEDIGER; KARPICKE, 2006; KOLB, 1984). O motor deve permitir tentativas repetidas sem limite. | §6, §13 |
| RNF-PRI-01 | O motor deve seguir a Lei Geral de Proteção de Dados (LGPD) ao coletar e guardar dados de quem joga (BRASIL, 2018). | §14 |
| RNF-CON-01 | Nenhuma etapa de uma sessão de jogo (leitura, validação, dica, resumo, registro, geração do relatório) depende de conexão à internet. | Sem seção no conceito — ver nota da seção 5 |

---

## 8. Restrições

- O motor não deve avaliar se uma peça está fisicamente correta em si — apenas se está na posição certa (RF-VAL-01).
- O motor não representa nem avalia relação de causa entre fotogramas — reconhece apenas sucessão; uma causa eventualmente retratada é conteúdo do fotograma, não uma regra avaliada pelo motor (RF-EST-03).
- O motor não deve antecipar, explicar ou anunciar conteúdo antes da tentativa (RF-RET-01).
- Ao errar, o motor não deve indicar qual seria a peça correta nem o motivo do erro (RF-RET-02).
- Pular não deve ser tratado como resultado normal de validação, nem recomendado pelo sistema (RF-PUL-01, RF-PUL-02).
- Nenhum parâmetro de conteúdo deve ser inferido automaticamente pelo motor (RF-CFG-01).
- O marco zero de um evento não deve ser confundido com uma peça física (RF-SES-01).
- O motor não deve exigir presença física de fotogramas anteriores ao ponto de início de uma sessão (RF-SES-03).
- O motor nunca deve manter mais de uma sessão ativa ou pausada ao mesmo tempo (RF-PAU-05).
- O motor não deve conceder login próprio a quem joga para acesso a relatórios (RF-REG-06).
- O motor não deve exigir uma administração central com acesso automático a relatórios de sessões alheias (RF-REG-06).
- O motor não deve depender de conexão à internet em nenhuma etapa de uma sessão de jogo (RNF-CON-01).

---

## 9. Premissas

- Cada instância é montada por uma pessoa responsável pelo conteúdo — essa responsabilidade não pertence ao motor.
- O motor pressupõe que quem joga interage com uma peça de cada vez, não com múltiplas peças em simultâneo.
- A escolha do ponto de início de uma sessão, a disponibilidade de pular, o limiar de erro para dica e sugestão de estudo, e o tempo de ociosidade são feitos por qualquer pessoa que for jogar, não apenas por quem montou o conteúdo — o motor não distingue quem faz essa escolha.
- Quem monta o conteúdo é um papel distinto de quem joga, mas não existe, para esse papel, autenticação nem acesso a dados de sessões de terceiros — sua responsabilidade se limita a preparar o conteúdo em si (ver Projeto Arquitetônico).

---

## 10. Itens deferidos à Especificação

- A estrutura de cada parâmetro configurável — nome, se é definido na montagem do conteúdo ou na configuração da sessão, e se é obrigatório nesse momento. O momento certo de cada parâmetro já está definido acima (RF-CFG-01, RF-PUL-03, RF-DIC-04, RF-PAU-06); falta descer ao nível de estrutura.
- O procedimento detalhado de conformidade com a LGPD (forma de consentimento, prazo de retenção, processo de exclusão de dados) — o princípio já está estabelecido em RNF-PRI-01.

Nota: nenhum valor numérico de configuração (limiar de erro, tempo de ociosidade, prazo de retenção) entra nesta lista. O motor nunca impõe um valor padrão para nenhum deles, em nenhuma etapa — cada conteúdo, ou cada sessão, conforme o parâmetro, define o seu (ver §2.2).

---

## 11. Referências

Fontes externas citadas nas seções 6 e 7 deste documento, no formato definido pela norma ABNT NBR 6023 (Informação e documentação — Referências). Citadas no corpo do documento como (AUTOR, ano). Embasamento pedagógico completo (dificuldades desejáveis, prática deliberada) está em `1 - documento-de-conceito-geral.md`, seção 16.

BRASIL. **Lei nº 13.709, de 14 de agosto de 2018**. Lei Geral de Proteção de Dados Pessoais (LGPD). Brasília, DF: Presidência da República, 2018. Disponível em: https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm. Acesso em: 12 ago. 2026.

KOLB, D. A. **Experiential learning**: experience as the source of learning and development. Englewood Cliffs: Prentice Hall, 1984.

ROEDIGER III, H. L.; KARPICKE, J. D. **The power of testing memory**: basic research and implications for educational practice. Perspectives on Psychological Science, v. 1, n. 3, p. 181-210, 2006. Disponível em: https://doi.org/10.1111/j.1745-6916.2006.00012.x. Acesso em: 12 ago. 2026.

---

## 12. Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 11-08-2026 | Criação inicial completa dos requisitos funcionais e não funcionais do módulo Conceito Geral (Motor), com base em `1 - documento-de-conceito-geral.md`: hierarquia de quatro níveis, estrutura de sequência, interação, validação, erro e tentativa, pular, retorno da tela, dica e sugestão de estudo (com o limiar de cada uma escolhido na configuração da sessão), marco zero e composição de sessão, encadeamento e resumos, pausa/ocioso/saída (com tempo de ociosidade também escolhido na configuração da sessão), registro e relatório com privacidade, configuração, navegação e entrada, e conectividade. | Levantamento de requisitos |
| 1.0.0 | 12-08-2026 | Documento aprovado — primeira versão estável dos Requisitos do módulo Conceito Geral (Motor). | Aprovação da cascata do motor |
