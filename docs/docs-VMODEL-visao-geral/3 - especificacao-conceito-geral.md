# NEXO — Documento de Especificação
## Módulo: Conceito Geral (Motor)

| Campo | Valor |
|---|---|
| Projeto | NEXO |
| Módulo | Conceito Geral (Motor) |
| Etapa (V-Model) | Especificação |
| Documento(s) de origem | `2 - requisitos-conceito-geral.md` v1.0.0 (normativo principal); `1 - documento-de-conceito-geral.md` v1.0.0 (referência) |
| Versão | 1.0.0 |
| Data | 12-08-2026 |
| Situação | Aprovado |
| Licença | Todos os direitos reservados — ver [LICENSE](../../LICENSE) |

---

## 1. Objetivo

Descrever como o motor do NEXO atende, na prática, cada requisito definido em `2 - requisitos-conceito-geral.md`: a regra de comportamento concreta por trás de cada RF e RNF, os critérios de decisão envolvidos, e a estrutura de cada parâmetro configurável. Este documento não decide tecnologia, nem como as partes do sistema se conectam entre si — isso cabe ao Projeto Arquitetônico, etapa seguinte.

---

## 2. Escopo

### 2.1 Dentro do escopo
- A regra concreta de comportamento por trás de cada requisito funcional e não funcional do documento de Requisitos.
- A estrutura de cada parâmetro configurável: nome, o momento em que é definido — na montagem do conteúdo (por nível da hierarquia) ou na configuração da sessão —, e se é obrigatório nesse momento.
- O mecanismo de entrada e seleção de conteúdo (tema, instância), item que o documento de Requisitos já havia deferido para esta etapa.
- O procedimento de consentimento, retenção e exclusão de dados pessoais, exigido pela LGPD e também deferido para esta etapa.

### 2.2 Fora do escopo
- Tecnologia de implementação (leitura de peça, armazenamento, hardware).
- Formato exato de exportação do relatório.
- Aparência visual e fluxo funcional de qualquer tela.
- Qualquer valor numérico de configuração (limiar de erro, tempo de ociosidade, prazo de retenção de dados). O motor nunca impõe esses valores — cada conteúdo define o seu. Isso não é uma pendência desta etapa; é uma decisão permanente, já registrada no documento de Conceito (§15) e nos Requisitos (§2.2).

---

## 3. Documentos relacionados

| Documento | Papel em relação a este documento |
|---|---|
| `1 - documento-de-conceito-geral.md` | Referência de segundo nível, citada quando o requisito de origem também cita uma seção do conceito. |
| `2 - requisitos-conceito-geral.md` | Fonte normativa direta. Cada item de especificação abaixo refere-se a um requisito deste documento. |

---

## 4. Termos adicionais desta etapa

| Termo | Definição |
|---|---|
| Posição esperada | A posição da sequência, dentro da sessão em curso, que o motor aceita como próxima tentativa válida. |
| Contador de erro por posição | Quantidade de tentativas seguidas, sem sucesso, feitas contra uma mesma posição — usado para liberar a dica. Diferente do contador de erro do evento (RF-ERR-02), que soma todos os erros do evento. |
| Limiar | O número de ocorrências (erros, tempo) configurado para disparar um comportamento — por exemplo, o limiar de erros que libera a dica. |
| Item avulso | Tema ou evento sem posição declarada dentro do seu grupo — não entra em nenhum cálculo de contiguidade. |

---

## 5. Convenção de identificação

Formato: `EI-[CATEGORIA]-[número]` (Item de Especificação), reaproveitando as siglas de categoria do documento de Requisitos (HIE, EST, INT, VAL, ERR, PUL, RET, DIC, SES, ENC, PAU, REG, CFG, MOD, APR, PRI, NAV, CON).

Duas categorias novas foram criadas sem seção correspondente no documento de Conceito: Navegação e entrada (NAV) e Conectividade (CON). Os requisitos que as sustentam (RF-NAV-01, RF-NAV-02, RNF-CON-01) também não citam uma seção do conceito como origem — a nota da seção 5 do documento de Requisitos explica por quê.

Cada item indica, na coluna "Requisito de origem", o RF ou RNF do qual deriva.

---

## 6. Especificação por categoria

### 6.1 Hierarquia de conteúdo (HIE)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-HIE-01 | Cada instância, tema e evento é identificado por um nome próprio, único dentro do nível imediatamente acima dele — um tema não repete nome dentro da mesma instância; um evento não repete nome dentro do mesmo tema. | RF-HIE-01, RF-HIE-02 |
| EI-HIE-02 | A pertença de um tema a uma instância, e de um evento a um tema, é definida no momento em que esse tema ou evento é criado, e não muda depois sem uma ação explícita de quem monta o conteúdo. | RF-HIE-03 |
| EI-HIE-03 | Para cada tema dentro de uma instância, e para cada evento dentro de um tema, quem monta o conteúdo declara, na criação, se aquele item tem ordem em relação aos demais do mesmo grupo ou é avulso. Essa declaração é individual: dois eventos do mesmo tema podem ter respostas diferentes entre si. | RF-HIE-04 |
| EI-HIE-04 | Quando um tema ou evento é declarado "com ordem", ele recebe também uma posição dentro do seu grupo (1º, 2º, 3º...), suficiente para determinar depois quais itens são vizinhos — necessário para a composição de sessão (ver SES). Um item avulso não recebe essa posição. | RF-HIE-04, RF-SES-06, RF-SES-07 |
| EI-HIE-05 | Dentro de uma sequência, todo fotograma tem uma posição obrigatória (1º, 2º, 3º...); não existe fotograma avulso nem posição em aberto no meio da sequência. | RF-HIE-05 |

### 6.2 Estrutura de sequência (EST)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-EST-01 | Uma sequência é a lista de fotogramas de um evento, ordenada por posição, sem lacunas — não existe posição 3 sem que a posição 2 exista. | RF-EST-01, RF-HIE-05 |
| EI-EST-02 | A quantidade de posições de uma sequência é definida por quem monta o evento; o motor não impõe mínimo nem máximo. | RF-EST-02 |
| EI-EST-03 | O motor associa a cada posição apenas o fotograma correspondente. A única relação reconhecida entre posições é a de ordem (a posição N vem antes da N+1) — nenhuma relação de causa é armazenada nem avaliada. | RF-EST-03 |

### 6.3 Interação do usuário (INT)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-INT-01 | As peças entregues a quem joga não trazem indicação da posição que ocupam, e a ordem de entrega não corresponde à ordem real da sequência. | RF-INT-01 |
| EI-INT-02 | Antes de uma peça ser apresentada como tentativa, o motor não expõe qual seria a peça esperada para a posição atual. | RF-INT-02 |
| EI-INT-03 | O motor processa uma peça por vez. Duas ou mais peças apresentadas ao mesmo tempo não geram uma validação conjunta — cada uma é tratada como tentativa isolada, na ordem em que chegam. A posição física de uma peça em relação a outra não deve ser considerada. | RF-INT-03 |

### 6.4 Validação (VAL)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-VAL-01 | Em cada momento de uma sessão em curso existe exatamente uma posição esperada: a próxima posição ainda não preenchida (ou, havendo pulo, a posição seguinte à pulada — ver PUL). | RF-VAL-01 |
| EI-VAL-02 | Ao receber uma peça, o motor compara seu identificador ao identificador da posição esperada. Havendo correspondência, a posição esperada avança e a peça é marcada como preenchida naquela sessão. Não havendo correspondência, nenhuma posição muda de estado. | RF-VAL-02 |
| EI-VAL-03 | A origem da peça apresentada (evento, tema, sessão a que pertence, ou já ter sido usada antes) não é considerada na comparação — toda peça que não seja a esperada recebe o mesmo tratamento de não correspondência. | RF-VAL-03 |

### 6.5 Erro e tentativa (ERR)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-ERR-01 | O motor não impõe limite de tentativas nem tempo mínimo de espera entre uma tentativa e a seguinte. | RF-ERR-01 |
| EI-ERR-02 | A contagem de erros de um evento é derivada do registro de tentativas rejeitadas daquele evento (ver EI-REG-01) — cada tentativa rejeitada soma um erro à contagem do evento a que pertence, sem nunca zerar durante a sessão, mesmo quando uma posição avança ou é pulada. O motor não guarda essa contagem como um valor à parte do registro: ela é sempre lida a partir dele, para não correr o risco de duas fontes divergentes sobre o mesmo dado. Essa informação compõe o relatório final (ver REG) e pode auxiliar no estudo posterior daquele evento. | RF-ERR-02 |

### 6.6 Pular (PUL)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-PUL-01 | Pular não é resultado de uma tentativa: é uma ação separada, disponível a quem joga a qualquer momento em que uma posição esteja em aberto, independentemente de quantos erros já ocorreram ali. | RF-PUL-01, RF-PUL-04 |
| EI-PUL-02 | O motor não aciona pular sozinho nem o sugere ativamente; a decisão é sempre de quem joga, com uso esperado depois de tentativas (e da dica, se disponível) sem sucesso. | RF-PUL-02 |
| EI-PUL-03 | A disponibilidade de pular é escolhida por quem configura a sessão, no momento em que ela começa, evento por evento dentro do alcance da sessão — não é um parâmetro fixado de antemão no conteúdo. Desativada para um evento, a ação simplesmente não é oferecida ali. | RF-PUL-03 |
| EI-PUL-04 | Ao pular, a posição atual é marcada como perdida (sem peça) e a posição esperada avança para a seguinte — como em um acerto, exceto que a posição perdida não é preenchida nem tentada de novo naquela sessão. | RF-PUL-05 |
| EI-PUL-05 | Havendo ao menos uma posição perdida por pulo dentro de um evento, ao final desse evento o resumo normal não é exibido. Em seu lugar, o motor monta, uma única vez, uma mensagem com três partes: a lista do que foi respondido, a indicação do intervalo sem resposta (sem revelar o conteúdo daquelas posições) e uma sugestão de estudo daquele trecho — nunca do tema inteiro, e sem qualquer referência ao conteúdo pulado. Essa mensagem aparece só no evento em que o pulo ocorreu; não é reexibida ao final da cadeia (ver EI-ENC-03). | RF-PUL-06 |

### 6.7 Retorno da tela (RET)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-RET-01 | Entre a exibição da referência (ver SES) e a tentativa de quem joga, o motor não exibe nenhum conteúdo que antecipe a posição esperada. | RF-RET-01 |
| EI-RET-02 | Quando uma tentativa não corresponde à posição esperada, a tela exibe uma mensagem única e padronizada de não correspondência, igual em todo caso de erro, sem identificar a peça correta nem o motivo da rejeição. | RF-RET-02 |
| EI-RET-03 | Quando uma tentativa corresponde, a tela exibe, junto da confirmação, o texto cadastrado para aquele fotograma especificamente — o cadastro desse texto é opcional por parte de quem monta o conteúdo (ver EI-CFG-01), mas, uma vez cadastrado, a exibição não é opcional. Funciona de forma independente de qualquer fotograma anterior. | RF-RET-03 |
| EI-RET-04 | Ao final de uma sessão sem posições perdidas, o motor reúne os textos e fotogramas do trecho jogado (do ponto de início até o fim da sessão) numa síntese, exibida em tela própria, distinta da confirmação peça a peça. | RF-RET-04 |

### 6.8 Dica e sugestão de estudo (DIC)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-DIC-01 | O motor conta, por posição, quantas tentativas seguidas sem sucesso ocorreram (esse contador zera quando a posição é preenchida, por acerto ou por pulo). Ao atingir o limiar escolhido na configuração da sessão, a dica passa a ficar disponível para aquela posição. | RF-DIC-01, RF-DIC-04 |
| EI-DIC-02 | O conteúdo de cada dica — quanto ela revela — é parâmetro do evento, escrito por quem monta o conteúdo; o motor não define um conteúdo padrão. Já o limiar de erros que libera a dica é escolhido na configuração de cada sessão (ver EI-DIC-04), não no evento. | RF-DIC-02, RF-DIC-04 |
| EI-DIC-03 | Se, mesmo com a dica usada, a posição continuar sem ser preenchida após um segundo limiar (também escolhido na configuração da sessão), o motor passa a exibir a sugestão de estudo, junto com a opção de pular, se disponível. Nenhuma das duas ações é obrigatória. | RF-DIC-03 |
| EI-DIC-04 | Os dois limiares — o que libera a dica e o que libera a sugestão de estudo — são escolhidos na configuração de cada sessão, junto com a disponibilidade de pular (ver EI-PUL-03) e o tempo de ociosidade (ver EI-PAU-06); podem variar de sessão para sessão, mesmo dentro do mesmo evento, mas não mudam depois de a sessão já estar em curso. O conteúdo da dica em si é diferente: é escrito por quem monta o conteúdo, uma única vez por evento, sem herança de tema ou instância. | RF-DIC-04 |

### 6.9 Marco zero e ponto de início (SES)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-SES-01 | Cada evento tem um marco zero próprio, cadastrado por quem monta o conteúdo, sem peça física correspondente. | RF-SES-01 |
| EI-SES-02 | Ao entrar em um evento como ponto de entrada de uma sessão — sozinho, ou como primeiro evento de uma cadeia —, quem joga escolhe, entre as posições já cadastradas, qual será o ponto de início da sessão; por padrão, essa escolha é a posição 1. Eventos que vêm encadeados depois do primeiro não repetem essa escolha — começam sempre na primeira posição (ver EI-ENC-02). | RF-SES-02 |
| EI-SES-03 | O motor não exige que as posições anteriores ao ponto de início escolhido tenham peça física disponível — a sessão começa considerando a posição esperada igual ao ponto de início escolhido. | RF-SES-03 |
| EI-SES-04 | A referência exibida antes da primeira tentativa segue esta ordem de critério: (1) se o ponto de início não é a posição 1, a referência é o fotograma imediatamente anterior; (2) senão, se o evento é o primeiro da sessão, a referência é o marco zero daquele evento; (3) senão (evento encadeado depois de outro), a referência é a última posição preenchida do evento anterior — se nenhuma posição do evento anterior foi preenchida (todas as tentadas ali foram puladas, ou nenhuma foi tentada), a referência é o marco zero do evento atual, nunca uma posição pulada: mostrar o conteúdo de uma posição pulada violaria a proibição de revelar esse conteúdo (ver EI-PUL-05). | RF-SES-04 |

### 6.10 Composição de sessão (SES)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-SES-05 | Uma sessão pode ser definida sobre um único evento — que tem uma única sequência —, cobrindo essa sequência a partir do ponto de início escolhido. | RF-SES-05 |
| EI-SES-06 | Uma sessão pode ser definida sobre um grupo de eventos do mesmo tema, desde que contíguo segundo as posições declaradas em HIE — não é permitido incluir o evento 1 e o evento 3 sem o evento 2. | RF-SES-06 |
| EI-SES-07 | Pela mesma regra de contiguidade, uma sessão pode ser definida sobre um grupo de temas encadeáveis da mesma instância, podendo atravessar um tema inteiro e seguir para o próximo do grupo. | RF-SES-07 |
| EI-SES-08 | Antes de montar uma sessão que atravesse mais de um evento ou tema, o motor verifica se todos os itens envolvidos têm posição declarada. Um item avulso não entra em grupo contíguo com outro; uma sessão que o inclua fica limitada a esse item sozinho. | RF-SES-08 |

### 6.11 Encadeamento e resumos (ENC)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-ENC-01 | Ao preencher a última posição de um evento dentro de uma cadeia, o motor exibe o resumo daquele evento e, havendo próximo evento na cadeia, oferece um controle para continuar. | RF-ENC-01 |
| EI-ENC-02 | Ao continuar, o motor troca apenas o nome do evento exibido e passa a aguardar a peça da primeira posição do próximo evento; a referência nesse momento segue EI-SES-04, caso 3 — normalmente a última peça preenchida do evento anterior, não o marco zero do novo evento, mesmo que ele exista; só recai no marco zero do novo evento na exceção prevista naquele caso (nenhuma posição do evento anterior preenchida). | RF-ENC-02 |
| EI-ENC-03 | Ao preencher a última posição do último evento de uma cadeia com mais de um evento, o motor monta, além do resumo individual desse último evento (ou da mensagem de três partes, se esse último evento teve posição perdida por pulo — ver EI-PUL-05), uma segunda síntese cobrindo a cadeia inteira, do início da sessão até o fim. Se não houve posição perdida por pulo em nenhum evento da cadeia, essa síntese é a narrativa contínua descrita em EI-RET-04. Se houve ao menos uma posição perdida em algum evento da cadeia, a síntese se limita a um total consolidado (quantas posições foram preenchidas e quantas foram perdidas na cadeia inteira), sem repetir a mensagem de três partes já mostrada ao final do evento em que o pulo ocorreu. As duas — resumo (ou mensagem de pulo) do último evento, e síntese da cadeia — aparecem juntas, sem que uma substitua a outra. | RF-ENC-03 |

### 6.12 Pausa, ocioso e saída (PAU)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-PAU-01 | O motor reconhece dois gatilhos para o mesmo efeito de interrupção: um período sem nenhuma tentativa (ociosidade) e uma ação explícita de pausar. Em ambos os casos, grava a posição esperada, o evento e a sessão em curso, e marca a sessão como pausada. | RF-PAU-01 |
| EI-PAU-02 | O motor trata o desligamento sem passar pela ação de sair do mesmo jeito que uma pausa: ao ligar de novo, encontra a sessão marcada como pausada e a oferece para retomada. | RF-PAU-02 |
| EI-PAU-03 | A ação de sair é distinta da de pausar e exige confirmação explícita antes de ser efetivada. Confirmada, o motor apaga o estado retomável daquela sessão. | RF-PAU-03 |
| EI-PAU-04 | Mesmo quando a sessão é encerrada por sair, o motor gera o relatório daquela sessão (ver REG), cobrindo tudo o que aconteceu até a saída, antes de apagar o estado retomável. | RF-PAU-04 |
| EI-PAU-05 | O motor garante que nunca existem duas sessões pausadas ao mesmo tempo. Essa garantia decorre da regra de entrada (EI-NAV-01): como nenhuma sessão nova começa enquanto existir uma pausada, a sessão que eventualmente vier a ser pausada é sempre a única em curso naquele momento — não existe um segundo estado pausado para checar. | RF-PAU-05 |
| EI-PAU-06 | O motor conta o tempo sem nenhuma tentativa dentro da sessão em curso. Ao atingir o limiar escolhido na configuração daquela sessão, aciona a ociosidade (EI-PAU-01). Esse limiar pode variar de sessão para sessão, mesmo dentro do mesmo evento — mesma regra de DIC (EI-DIC-04) e de PUL (EI-PUL-03). O motor não impõe valor padrão. | RF-PAU-06 |

### 6.13 Registro e relatório (REG)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-REG-01 | Durante a sessão, o motor registra a configuração usada para iniciá-la (evento(s) ou tema(s) envolvidos, ponto de início, disponibilidade de pular, limiar de erro para dica e para sugestão de estudo, tempo de ociosidade) e cada evento ocorrido durante o jogo — tentativa aceita, tentativa rejeitada, dica usada, sugestão de estudo exibida, posição pulada, pausa ou ociosidade acionada — com a posição, o evento, e o momento em que cada um ocorreu. | RF-REG-01 |
| EI-REG-02 | Ao final de toda sessão, inclusive quando encerrada por sair antes do fim, o motor monta um relatório a partir do registro acumulado até aquele momento. | RF-REG-02 |
| EI-REG-03 | Antes de registrar qualquer dado que identifique a pessoa, o motor solicita consentimento explícito, informando para que aqueles dados serão usados — a manifestação livre, informada e inequívoca exigida pela LGPD (BRASIL, 2018, art. 8º). Sem consentimento, a sessão segue sendo registrada normalmente, só sem os dados de identificação. Esse registro também depende de aprovação de comitê de ética aplicável ao contexto de uso — cabe a quem aplica o sistema naquele contexto (não a um papel de administração dentro do motor) confirmar essa aprovação antes de habilitar a coleta. | RF-REG-03, RNF-PRI-01 |
| EI-REG-04 | Erros, posições preenchidas ou perdidas, e a configuração usada são sempre registrados, com ou sem consentimento para dados de identificação. | RF-REG-04 |
| EI-REG-05 | O relatório fica salvo localmente no aparelho onde a sessão aconteceu, em mais de um formato legível (formatos exatos cabem ao Projeto Arquitetônico), sem exigir login nem autenticação. | RF-REG-05 |
| EI-REG-06 | Quem jogou vê o próprio resultado imediatamente ao final da sessão, sem login, e continua podendo consultar o mesmo relatório depois, livremente, quantas vezes quiser, direto no aparelho onde foi gerado. Nenhum outro papel tem acesso automático a esse dado. | RF-REG-06 |
| EI-REG-07 | Compartilhar o relatório com alguém além de quem jogou (por exemplo, a pessoa responsável pelo conteúdo) é uma ação que só a própria pessoa que jogou pode iniciar, feita depois que o relatório já foi gerado e entregue a ela — o motor nunca envia ou expõe esse dado por conta própria. | RF-REG-07 |
| EI-REG-08 | Dados que identificam a pessoa ficam no mesmo aparelho onde foram registrados; excluí-los (apagando o relatório ou removendo o aplicativo) é uma ação que a própria pessoa controla diretamente, sem depender de um mecanismo administrativo central — não existe repositório externo aos dados dela para pedir exclusão, na linha do direito de eliminação previsto na LGPD (BRASIL, 2018, art. 16). | RNF-PRI-01 |
| EI-REG-09 | O prazo de retenção configurado para uma instância (EI-CFG-01) expressa a duração da finalidade declarada no consentimento — em geral, o tempo em que a atividade ou pesquisa que motivou a coleta está em curso, salvo quando o próprio dado precisa ser mantido como comprovação documental daquele trabalho, conforme o encerramento do tratamento previsto na LGPD (BRASIL, 2018, art. 15, art. 16). O motor não aplica esse prazo automaticamente: informa a finalidade e o prazo no momento do consentimento (EI-REG-03) e oferece o meio de exclusão (EI-REG-08). Descartar o dado de forma adequada quando o prazo se encerra — incluindo, se for o caso, destruir uma cópia física — é responsabilidade de quem detém o dado a cada momento (quem jogou, ou um terceiro a quem ela tenha repassado o relatório), nunca do motor. | RNF-PRI-01 |

### 6.14 Configuração (CFG)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-CFG-01 | Cada parâmetro configurável tem, nesta especificação, um nome, o momento em que é definido e se é obrigatório nesse momento. Nenhum tem valor definido pelo motor — até ser preenchido, ele simplesmente não existe.<br><br>Parâmetros de conteúdo, definidos por quem monta o conteúdo, um por nível da hierarquia:<br><br>**Nome** — todo nível — obrigatório.<br>**Marco zero** — evento — obrigatório.<br>**Fotogramas da sequência** — evento — obrigatório.<br>**Texto de confirmação de acerto** — fotograma — opcional.<br>**Ordem ou avulso** — tema (dentro da instância), evento (dentro do tema) — obrigatório declarar, mesmo que a resposta seja "avulso".<br>**Posição no grupo** — tema, evento — obrigatório quando "com ordem".<br>**Conteúdo da dica** — evento — obrigatório se a dica estiver habilitada.<br>**Prazo de retenção de dados pessoais** — instância — obrigatório; sem valor padrão do motor (ver EI-REG-09).<br><br>Parâmetros de sessão, escolhidos por quem inicia cada sessão, no momento em que ela é configurada — podem variar de sessão para sessão, mesmo dentro do mesmo evento; nenhum tem valor padrão do motor:<br><br>**Disponibilidade de pular** — obrigatório declarar a cada sessão, evento por evento dentro do alcance dela (ver EI-PUL-03).<br>**Critério de dica e sugestão de estudo** — dois valores do mesmo critério (RF-DIC-04): o limiar de erro que libera a dica, e o limiar que libera a sugestão de estudo — os dois obrigatórios, evento por evento dentro do alcance da sessão, se a dica estiver habilitada naquele evento.<br>**Tempo de ociosidade** — um único valor para a sessão inteira, não repetido por evento — obrigatório. | RF-CFG-01 |
| EI-CFG-02 | Cada instância criada recebe um nome próprio, único entre as instâncias existentes — é esse nome que distingue conteúdos de instâncias diferentes entre si. | RF-CFG-02 |

### 6.15 Navegação e entrada (NAV)

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-NAV-01 | Ao ser aberto, o motor verifica se existe alguma sessão pausada. Havendo uma, só é possível escolher entre retomá-la ou sair dela (ver PAU) — nenhuma sessão nova começa antes dessa escolha. | RF-PAU-05, RF-NAV-02 |
| EI-NAV-02 | Não havendo sessão pausada, o motor apresenta as instâncias disponíveis; ao escolher uma, apresenta os temas dela; ao escolher um tema, apresenta os eventos dele — sempre respeitando a hierarquia fixa instância → tema → evento. | RF-HIE-01, RF-NAV-01 |
| EI-NAV-03 | Ao escolher um tema ou evento que faz parte de um grupo com ordem declarada, o motor oferece montar uma sessão que siga a partir dali, cobrindo o restante do grupo contíguo (ver SES); ao escolher um item avulso, a sessão fica limitada a esse item. Essa escolha de alcance é feita na entrada, antes da primeira tentativa, e não muda depois. | RF-SES-06, RF-SES-07, RF-SES-08 |
| EI-NAV-04 | Ao entrar em um evento como ponto de entrada da sessão (ver EI-SES-02), o motor oferece a escolha do ponto de início entre as posições já cadastradas, antes de exibir a referência e aguardar a primeira tentativa. Eventos encadeados depois do primeiro não passam por essa tela (ver EI-ENC-02). | RF-SES-02 |
| EI-NAV-05 | Junto com a escolha do ponto de início, o motor oferece a configuração da sessão: para cada evento dentro do alcance escolhido (ver EI-SES-08), se pular está disponível, e o limiar de erro que libera a dica e a sugestão de estudo (quando a dica estiver habilitada naquele evento); além disso, um único tempo de ociosidade, válido para a sessão inteira, não repetido por evento. Toda essa configuração é feita uma única vez, na tela de início, antes da primeira tentativa, e nenhum desses valores muda depois de a sessão estar em curso. Os três parâmetros — disponibilidade de pular, limiares de dica e sugestão de estudo, e tempo de ociosidade — compartilham exatamente essa regra, sem exceção: todos são decididos nessa mesma tela, no mesmo momento. O formato de cada um é diferente por necessidade, não por inconsistência entre eles: pular e os limiares de dica/estudo variam evento a evento porque a dificuldade de cada evento é diferente; o tempo de ociosidade é um único valor porque mede o comportamento da pessoa ao longo da sessão inteira, não de um evento específico. | RF-PUL-03, RF-DIC-04, RF-PAU-06 |

---

## 7. Especificação dos requisitos não funcionais

| ID | Descrição | Requisito de origem |
|---|---|---|
| EI-MOD-01 | Nenhuma regra deste documento faz referência a um assunto, disciplina ou tipo de conteúdo específico; toda regra opera sobre nomes, posições, contadores e parâmetros genéricos, do jeito definido nas categorias da seção 6. | RNF-MOD-01 |
| EI-MOD-02 | Criar uma nova instância significa apenas cadastrar seus temas, eventos, sequências e parâmetros, seguindo as regras já descritas — nenhuma regra desta especificação precisa ser revista por causa de uma instância nova. | RNF-MOD-02 |
| EI-APR-01 | Nenhuma regra desta especificação impõe limite de tentativas, de tempo entre tentativas, ou de repetições, em nenhuma posição de nenhum evento — e isso não é um parâmetro configurável, é fixo. | RNF-APR-01 |
| EI-PRI-01 | As regras de REG (EI-REG-03, EI-REG-08, EI-REG-09 — consentimento, exclusão e retenção) são a forma como o motor atende à exigência de seguir a LGPD (BRASIL, 2018). | RNF-PRI-01 |
| EI-CON-01 | Nenhuma regra desta especificação (leitura, validação, dica, resumo, registro, geração do relatório) pressupõe uma etapa de rede externa em curso — a forma concreta como isso é garantido cabe ao Projeto Arquitetônico. | RNF-CON-01 |

---

## 8. Restrições

- Esta especificação não decide como as peças são fisicamente identificadas pelo motor (tecnologia de leitura) — isso é Projeto Arquitetônico.
- Nenhum valor numérico de configuração (limiar de erro, tempo de ociosidade, prazo de retenção) é definido aqui; cada conteúdo define o seu, sem exceção (ver EI-CFG-01).
- A aparência visual e o fluxo funcional de qualquer tela (referência, confirmação, erro, dica, resumo, síntese) não são definidos aqui — apenas o conteúdo funcional que cada uma precisa carregar.
- O formato exato de exportação do relatório não é definido aqui (EI-REG-05 só exige "mais de um formato legível").
- A ordem exata em que instâncias, temas e eventos são listados na navegação (EI-NAV-02) não é definida aqui — apenas que a hierarquia é respeitada.

---

## 9. Premissas

- Quem monta o conteúdo preenche os parâmetros obrigatórios listados em EI-CFG-01 antes de publicar um evento, tema ou instância; o que acontece se um parâmetro obrigatório estiver ausente é uma regra de validação de cadastro, a ser definida no Projeto Arquitetônico.
- A aprovação de comitê de ética (EI-REG-03) é obtida fora do motor, por quem administra o conteúdo; o motor não verifica essa aprovação, apenas depende dela estar em vigor antes de a coleta de dados de identificação ser habilitada.
- O identificador de cada peça (usado na validação, EI-VAL-02) já existe e está disponível para o motor no momento da leitura; como esse identificador é gerado ou lido é assunto de Projeto Arquitetônico.
- O motor nunca é o responsável legal pelos dados que coleta — apenas fornece os meios (consentimento informado, registro, exclusão, exportação) para que quem detém o dado a cada momento cumpra essa responsabilidade (ver EI-REG-09).

---

## 10. Itens deferidos ao Projeto Arquitetônico

- Tecnologia de leitura e identificação de peças.
- Estrutura de armazenamento dos dados de configuração, registro e relatório.
- Formatos exatos de exportação do relatório.
- Fluxo funcional de cada tela — quais existem, o que cada uma mostra, o que leva de uma à outra.
- Regra de validação de cadastro de conteúdo (o que acontece quando um parâmetro obrigatório não é preenchido).
- Critério de ordenação da lista de instâncias, temas e eventos na navegação (EI-NAV-02).
- Forma como um conteúdo novo chega até o motor, sem depender de uma administração central.

Nota: valores numéricos de configuração (limiares, tempos, prazos) não entram nesta lista — eles não são uma etapa futura do motor, e sim responsabilidade permanente de quem monta cada instância de conteúdo, ou de quem configura cada sessão, conforme o parâmetro (ver 2.2 e EI-CFG-01). Pela mesma razão, a aparência visual de qualquer tela (cor, fonte, layout) também não entra nesta lista: não é resolvida em nenhuma etapa da documentação do motor — é decisão de quem constrói a interface de cada instância (ver Conceito, §15).

---

## 11. Referências

Fonte externa citada na seção 6.13 e na seção 7 deste documento, no formato definido pela norma ABNT NBR 6023 (Informação e documentação — Referências). Citada no corpo do documento como (BRASIL, ano, art. N).

BRASIL. **Lei nº 13.709, de 14 de agosto de 2018**. Lei Geral de Proteção de Dados Pessoais (LGPD). Brasília, DF: Presidência da República, 2018. Disponível em: https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm. Acesso em: 12 ago. 2026.

---

## 12. Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 11-08-2026 | Criação inicial: especificação completa do módulo Conceito Geral (Motor), cobrindo todos os RF e RNF de `2 - requisitos-conceito-geral.md`, incluindo a categoria de Navegação e Entrada (NAV) — com a tela de configuração da sessão (pular, limiares de dica e sugestão de estudo, tempo de ociosidade) —, a categoria de Conectividade (CON), e o procedimento de LGPD (consentimento, retenção, exclusão). | Levantamento de especificação |
| 1.0.0 | 12-08-2026 | Documento aprovado — primeira versão estável da Especificação do módulo Conceito Geral (Motor). | Aprovação da cascata do motor |
