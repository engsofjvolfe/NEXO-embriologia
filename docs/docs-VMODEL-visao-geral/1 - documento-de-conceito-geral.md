# NEXO
## Documento de Conceito

| Campo | Valor |
|---|---|
| Projeto | NEXO |
| Documento | Documento de Conceito |
| Versão | 1.0.0 |
| Data | 12-08-2026 |
| Situação | Aprovado |
| Licença | Todos os direitos reservados — ver [LICENSE](../../LICENSE) |

---

Núcleo Encadeado, Xis, Orgânico.

---

## 1. Filosofia

O NEXO parte de uma constatação simples: explicação não é aprendizado. Assistir a um processo sendo narrado — mesmo que bem narrado, mesmo que com boas imagens — deixa pouco rastro na memória de quem assiste. O que fixa conhecimento é o esforço de reconstruir algo por conta própria, errar, corrigir e tentar de novo (ROEDIGER; KARPICKE, 2006; DUNLOSKY et al., 2013).

O sistema não existe para explicar nenhum assunto específico. Existe para obrigar quem o usa a reconstruir, com as próprias mãos, a sequência de um processo — sem receber a resposta de graça. A tela não ensina; ela confirma. Quem ensina é a tentativa (BJORK; BJORK, 2011).

Não existe um nível "correto" de detalhe em nenhum processo. Toda fonte — um livro, uma aula, um professor — escolhe onde parar de detalhar, e essa escolha nunca é neutra nem universal; é feita em função de quem escreve, de quanto tempo tem, do público a que se destina. O NEXO assume, de forma explícita, que a granularidade de qualquer parte do conteúdo — do processo mais específico até a área mais ampla que o contém — é uma decisão de quem monta o conteúdo, não uma verdade a ser descoberta na literatura. Dois processos parecidos podem ser divididos de formas completamente diferentes, e isso não é inconsistência: é reflexo de que cada um foi decomposto de acordo com uma escolha própria, feita por quem o construiu.

## 2. Hierarquia de conteúdo

O conteúdo de uma aplicação do NEXO se organiza em quatro níveis, sempre os mesmos. O que muda, de aplicação para aplicação, é o que cada pessoa que monta o conteúdo decide encaixar em cada nível — a mesma lógica de granularidade da seção 1, agora estendida a toda a estrutura, não só à quantidade de fotogramas de um processo.

- **Instância** é a aplicação do motor a uma área de conteúdo. É o nível mais amplo; tudo o que existe abaixo dele pertence só a ele — conteúdo de instâncias diferentes nunca se mistura.
- **Tema** é um recorte dentro de uma instância.
- **Evento** é um processo específico dentro de um tema.
- **Sequência** é a lista ordenada de fotogramas que compõe um evento — o nível mais baixo, onde a mecânica de peça e tentativa descrita nas seções seguintes realmente acontece.

Um evento que, sob outro recorte, poderia ser tratado como um tema — ou um tema que poderia ser uma instância inteira — não é uma inconsistência. É a mesma escolha de granularidade da seção 1, aplicada a este nível da estrutura: quem monta o conteúdo decide onde cada parte do assunto se encaixa, e essa decisão pode ser diferente em cada aplicação do sistema.

Dentro de um tema, os eventos podem ter ordem entre si (um evento é continuação lógica do anterior) ou podem ser avulsos (sem relação de ordem entre eles). O mesmo vale um nível acima: dentro de uma instância, os temas podem ter ordem entre si ou podem ser avulsos. Essa é uma decisão tomada por quem organiza o conteúdo, no momento em que cada tema ou evento é criado — não é inferida pela posição em que ficou guardado, nem decidida de uma vez para toda a instância.

Essa flexibilidade não existe um nível abaixo. Dentro de um evento, os fotogramas de uma sequência são sempre estritamente ordenados — isso não é uma escolha de configuração, é o que define o que é uma sequência.

## 3. O que é o sistema

O NEXO é um simulador tátil de sequências. Um evento é decomposto em uma série ordenada de estados — seus fotogramas (seção 4). Cada estado vira uma peça física. Quem usa o sistema recebe as peças de uma sequência fora de ordem e precisa descobrir, por tentativa, qual é a ordem correta em que elas ocorreram.

O sistema não julga se a peça está fisicamente correta em si — ele julga se ela está na posição certa da sequência que está em jogo. Não há personagem que "causa" a próxima etapa, não há controle que avança a história sozinho. Há apenas um conjunto de fotografias de momentos e a tarefa de reconstituir a linha do tempo entre elas.

## 4. A peça como fotograma

A unidade central do sistema é o fotograma: um estado de um processo, congelado em um instante específico. A relação entre um fotograma e o seguinte é sempre de sucessão — como era, como ficou. Essa sucessão pode, quando fizer sentido para o conteúdo, também retratar uma relação de causa e efeito entre os dois estados — mas isso é um dado a mais dentro do retrato, nunca uma exigência do sistema. O sistema sempre mostra que um estado veio antes do outro; pode, ou não, mostrar também o que provocou essa mudança.

A peça não é personagem nem cenário: é um retrato de um momento. Quando esse retrato admite uma causa por trás da mudança, ela continua sendo parte do próprio fotograma — um desenho com uma pequena legenda, por exemplo — nunca um personagem à parte, com existência ou comportamento próprio fora daquele instante retratado. Para posicioná-la corretamente, quem joga precisa reconhecer o retrato — o que ele mostra, e o que pode ter agido sobre aquele estado, quando isso for mostrado — e saber onde ele se encaixa na linha do tempo.

Essa é a propriedade que torna o sistema agnóstico ao tipo de fenômeno que representa. Um encontro entre dois elementos distintos e uma transformação com ou sem agente externo identificável são, do ponto de vista do sistema, a mesma coisa: dois fotogramas em sequência.

O número de fotogramas que compõem uma sequência é decidido por quem monta o conteúdo, não por uma regra fixa. Um evento pode ser representado por três, por doze, por 200; a escolha depende do quanto se deseja aprofundar aquele evento específico, não de uma fórmula geral aplicada a todos os eventos.

## 5. Marco zero e ponto de início de sessão

Cada evento tem um marco zero: uma imagem própria da tela, sem carta física correspondente, que representa o estado do processo antes do primeiro fotograma.

Uma sessão de uso pode começar em qualquer fotograma já existente no evento — por padrão, no primeiro. Fotogramas anteriores ao ponto escolhido não precisam existir fisicamente naquela sessão: o sistema lê peças individualmente (seção 6), então não depende da presença de peças anteriores.

A referência mostrada na tela antes da primeira peça de uma sessão depende de onde ela começa:

- Se a sessão começa em qualquer fotograma além do primeiro, a referência é o fotograma imediatamente anterior ao ponto escolhido.
- Se o evento é o ponto de entrada da sessão — jogado sozinho, ou como primeiro evento de uma cadeia — e a sessão começa no primeiro fotograma, a referência é o marco zero daquele evento.
- Se o evento vem encadeado depois de outro evento na mesma sessão, a última peça preenchida do evento anterior ocupa o lugar da referência. O marco zero do evento que está começando continua existindo, só não é mostrado nesse momento — exceto quando nenhuma peça do evento anterior foi preenchida (todas as tentadas ali foram puladas, ou nenhuma foi tentada): nesse caso não existe peça preenchida para servir de referência, e mostrar uma peça pulada revelaria um conteúdo que a seção 7 proíbe revelar, então a referência volta a ser o marco zero do evento que está começando.

## 6. Validação, tentativa e erro

O sistema identifica peças individualmente, uma de cada vez. Não há leitura simultânea de múltiplas peças nem dependência de posição espacial entre elas — a lógica de validação opera inteiramente sobre a ordem em que as peças são apresentadas, não sobre onde estão fisicamente dispostas.

Quando uma peça é apresentada, o sistema verifica apenas uma coisa: se ela corresponde à próxima posição esperada na sessão em curso. Se corresponder, a sessão avança. Se não corresponder, a tentativa é rejeitada e a sessão permanece no ponto em que estava — não importa se a peça pertence a outro evento, a outro tema ou já foi usada antes; qualquer peça que não seja a esperada naquele ponto é tratada da mesma forma.

Não existe limite de tentativas erradas, nem intervalo mínimo obrigatório entre uma tentativa e a próxima. A repetição continua sendo, por desenho, ilimitada (seção 13).

Cada evento mantém uma contagem de erros, que nunca zera durante a sessão — acumula do início ao fim, junto com a posição em que cada erro ocorreu, para compor o relatório final. Essa contagem é distinta da contagem por posição usada para liberar a dica (seção 9): a do evento soma todos os erros do evento inteiro, sem nunca reiniciar; a de posição soma só os erros seguidos contra a mesma posição, e reinicia sempre que a posição é preenchida, por acerto ou por pulo. A mesma lógica de registro — posição e momento — vale para pulos e para pausas (seção 14).

## 7. Pular

Além de acertar ou errar, existe uma terceira possibilidade, separada da lógica de validação da seção anterior: pular uma peça.

Pular é uma exceção explícita, não um resultado normal de tentativa — o sistema não a recomenda, e seu uso esperado só acontece depois que a pessoa já tentou e, se disponível, já usou a dica (seção 9) sem conseguir avançar. A disponibilidade de pular é decidida no momento em que uma sessão é configurada para começar — junto com o limiar de erro que libera a dica e a sugestão de estudo (seção 9) e o tempo de ociosidade que dispara a pausa automática (seção 12) — não é um parâmetro fixado de antemão no conteúdo do evento.

Pular funciona peça a peça, a qualquer momento, sem limite de quantas vezes. Uma peça pulada fica perdida naquela sessão — não encaixa mais em nenhuma posição depois —, e a sessão segue sem ela.

Quando há uma ou mais peças puladas dentro de um evento, o resumo que normalmente apareceria ao final desse evento (seção 8) não é mostrado. Em seu lugar, aparece, uma única vez, uma mensagem que lista o que foi respondido, aponta o intervalo que ficou sem resposta e sugere estudar aquele trecho, sem revelar o conteúdo pulado. Se esse evento faz parte de uma cadeia (seção 11), essa mensagem não é repetida ao final da cadeia — a síntese geral da cadeia segue a regra descrita na seção 11.

## 8. A tela

A tela cumpre uma função estritamente reativa: ela confirma, não anuncia. Não há explicação antes ou durante a tentativa de quem joga — isso eliminaria o esforço de reconstrução que é o núcleo pedagógico do sistema.

Ao errar uma tentativa, a tela também apenas confirma: sinaliza que a peça apresentada não corresponde à posição esperada naquele ponto, sem indicar qual seria a peça certa e sem explicar o motivo do erro. Essa negativa mínima é a única resposta a esse tipo de tentativa.

Ao acertar uma peça, a tela exibe um texto curto (algumas linhas, no máximo) que situa aquele fotograma: quando ocorre, o que caracteriza aquele momento. Esse texto não é uma aula — é uma legenda que reforça o que a mão acabou de descobrir: como era, o que agiu sobre aquilo(se cabível), como ficou. Ele funciona de forma independente de fotogramas anteriores, já que uma sessão pode começar em qualquer ponto de um evento.

Ao final de uma sessão sem peças puladas, o sistema apresenta uma síntese do trecho jogado — do ponto em que a sessão começou até o fim —, amarrando os fotogramas dessa sessão em uma narrativa contínua. Esse é o único momento em que o sistema oferece uma visão de conjunto daquele trecho; em qualquer outro ponto, quem joga só vê o fragmento com que está lidando. Se houve pulo, essa síntese não aparece — a seção 7 descreve o que aparece em seu lugar.

## 9. Dica e sugestão de estudo

Se a pessoa errar repetidamente a mesma posição, o sistema pode oferecer uma dica simples. A dica não substitui a tentativa — existe apenas para destravar quem está preso, depois de esforço suficiente já ter sido investido.

Se, mesmo depois de usar a dica, a pessoa continuar sem acertar, o sistema pode sugerir que ela saia e estude o tema antes de continuar — sem obrigar. A alternativa a essa sugestão é pular (seção 7), que o próprio sistema também não recomenda.

O conteúdo de cada dica — quanto ela revela — é escrito por quem monta o conteúdo, evento por evento, junto com o restante do material daquele evento. Já o critério de quando a dica aparece, e de quando a sugestão de estudo aparece, é decidido no momento em que uma sessão é configurada para começar — junto com a disponibilidade de pular (seção 7) e o tempo de ociosidade (seção 12) — podendo variar de sessão para sessão, mesmo dentro do mesmo evento.

## 10. Composição de uma sessão

Uma sessão de uso pode cobrir:

- a sequência inteira de um único evento;
- um recorte contíguo de eventos dentro do mesmo tema — nunca alternado: de um conjunto de eventos 1, 2 e 3, é possível jogar 1-2, 1-2-3 ou 2-3, mas nunca 1 e 3 sem o 2;
- um recorte contíguo de temas encadeáveis dentro da mesma instância, seguindo a mesma regra de contiguidade um nível acima — é possível atravessar um tema inteiro e continuar no próximo, ou parar num subconjunto contíguo de temas, sem precisar cobrir a instância inteira.

Essa composição só é possível onde existe ordem declarada (seção 2). Um tema avulso, sem relação de ordem com os demais, não se encadeia com nenhum outro dentro da mesma sessão; o mesmo vale para um evento avulso dentro de um tema.

## 11. Encadeamento de eventos e resumos

Quando uma sessão cobre mais de um evento, a transição entre eles segue uma sequência fixa: o evento termina, o resumo daquele evento aparece (seção 8), e um controle para continuar aparece junto — só quando existe um próximo evento na cadeia. Ao continuar, apenas o nome do evento muda na tela; a peça seguinte já é aguardada, sem nenhuma imagem de transição — o papel de referência é o da última peça do evento anterior (seção 5), não o marco zero do evento que está começando.

Ao final de uma cadeia com mais de um evento, existe também uma síntese que cobre a cadeia inteira, do ponto em que ela começou até o fim — além dos resumos de cada evento individual, sem substituir nenhum deles. Se não houve pulo em nenhum ponto da cadeia, essa síntese é a narrativa contínua descrita na seção 8. Se houve pulo em algum ponto da cadeia, essa síntese se limita a um total de posições preenchidas e perdidas na cadeia inteira, sem repetir a mensagem de três partes já mostrada ao final do evento em que o pulo ocorreu (seção 7).

## 12. Pausa, ocioso e saída

Uma sessão pode ser interrompida de formas diferentes, com efeitos diferentes:

- **Ociosidade** — um período sem nenhuma interação — e **pausa** — acionada por um controle explícito — têm o mesmo efeito: o estado da sessão é guardado, e retomado depois exatamente de onde parou, dentro do evento em que a pessoa estava. A única diferença entre as duas é o que dispara cada uma. Desligar o sistema sem sair conta como pausa, pelo mesmo motivo.
- O tempo sem interação que caracteriza ociosidade é decidido no momento em que a sessão é configurada para começar — a mesma regra usada para dica e sugestão de estudo (seção 9) e para a disponibilidade de pular (seção 7): escolhido a cada sessão, não fixado de antemão no conteúdo do evento.
- **Sair**, por um controle explícito e distinto do de pausar, apaga o progresso retomável daquela sessão — não é possível continuar de onde parou depois disso. Por apagar algo que não pode ser desfeito, sair exige confirmação antes de ser efetivado. Sair não apaga o relatório da sessão (seção 14): um relatório é gerado cobrindo o que aconteceu até aquele momento.

O sistema é inteiramente linear: nunca existe mais de uma sessão ativa ou pausada ao mesmo tempo. Não é possível pausar uma sessão no meio e iniciar outra enquanto a primeira permanece esperando.

## 13. Repetição e retenção

A mecânica de tentativa e erro não é um efeito colateral tolerável do sistema — é o mecanismo de aprendizagem em si, na mesma lógica do ciclo de aprender fazendo — agir, errar, refletir, tentar de novo — e da prática deliberada, guiada por correção de erro (KOLB, 1984; ERICSSON; KRAMPE; TESCH-RÖMER, 1993). Errar uma sequência e corrigi-la pode fixar mais do que acertar de primeira ou do que apenas observar a ordem correta sendo revelada (ROEDIGER; KARPICKE, 2006; DUNLOSKY et al., 2013). A repetição, nesse sistema, não é punição: é o meio pelo qual a sequência deixa de ser algo lido e passa a ser algo lembrado (BJORK; BJORK, 2011).

## 14. Registro e relatório

O sistema pode manter um registro de cada sessão: as configurações escolhidas no início, e o que aconteceu durante o jogo — erros, pulos e pausas, cada um com a posição e o momento em que ocorreu, e em qual evento. Esse registro gera um relatório ao final da sessão, mesmo quando ela é encerrada por "sair" antes do fim (seção 12); o relatório cobre o que aconteceu até aquele momento.

Dados que identificam a pessoa — nome, vínculo, papel, por exemplo se joga ou se administra o conteúdo — são opcionais: só são registrados mediante consentimento da própria pessoa, e sujeitos a qualquer aprovação de comitê de ética que se aplique ao contexto de uso. Dados do jogo em si — erros, posições, configuração usada — são sempre registrados, com ou sem identificação pessoal.

O relatório fica guardado no aparelho usado na sessão, em mais de um formato legível, à disposição de quem jogou: essa pessoa pode consultá-lo de novo depois, quantas vezes quiser, sem precisar de login. Não existe um papel de administração com acesso automático, central ou permanente a esse relatório — quem organiza o conteúdo não recebe os dados de ninguém por padrão. Se quem jogou decidir compartilhar o próprio relatório com alguém (por exemplo, a pessoa responsável pelo conteúdo), essa é uma escolha voluntária dela, feita depois que o relatório já foi entregue.

Por envolver dados de pessoas, o sistema deve seguir a Lei Geral de Proteção de Dados (LGPD) ao lidar com dados de quem joga.

## 15. Modularidade e configuração manual

O NEXO é uma estrutura vazia de conteúdo. Ele não sabe qual assunto está representando — sabe apenas operar sobre a hierarquia de instância, tema, evento e sequência (seção 2), e sobre a mecânica de peça, tentativa e tela descrita nas seções anteriores.

Os parâmetros do sistema são definidos manualmente em dois momentos distintos. Nada é inferido ou calculado automaticamente pelo sistema — tudo é uma escolha explícita de configuração, num momento ou no outro.

O primeiro momento é a montagem do conteúdo, feita por quem organiza cada instância: o marco zero de cada evento, quantos fotogramas compõem cada sequência, onde cada um se encaixa, qual texto acompanha cada acerto, se os eventos de um tema — ou os temas de uma instância — têm ordem entre si ou são avulsos, e o conteúdo de cada dica (seção 9).

O segundo momento é a configuração de cada sessão, feita por quem a inicia: a disponibilidade de pular (seção 7), o critério de quando a dica e a sugestão de estudo aparecem (seção 9), e o tempo de ociosidade que dispara a pausa automática (seção 12). Esses três podem variar de sessão para sessão, mesmo dentro do mesmo evento.

Essa característica é o que torna o sistema escalável: uma nova instância, sobre qualquer assunto, é adicionada criando seu próprio conjunto de temas, eventos, sequências e configurações, sem qualquer alteração na lógica central do sistema. O motor permanece o mesmo; muda apenas o conteúdo que roda dentro dele.

---

## 16. Referências

Fontes externas consultadas para embasar as escolhas de desenho pedagógico descritas neste documento, no formato definido pela norma ABNT NBR 6023 (Informação e documentação — Referências). Citadas no corpo do documento como (AUTOR, ano).

BJORK, E. L.; BJORK, R. A. **Making things hard on yourself, but in a good way**: creating desirable difficulties to enhance learning. In: GERNSBACHER, M. A. et al. (org.). Psychology and the real world: essays illustrating fundamental contributions to society. New York: Worth Publishers, 2011. p. 59-68.

DUNLOSKY, J. et al. **Improving students' learning with effective learning techniques**: promising directions from cognitive and educational psychology. Psychological Science in the Public Interest, v. 14, n. 1, p. 4-58, 2013. Disponível em: https://doi.org/10.1177/1529100612453266. Acesso em: 12 ago. 2026.

ERICSSON, K. A.; KRAMPE, R. T.; TESCH-RÖMER, C. **The role of deliberate practice in the acquisition of expert performance**. Psychological Review, v. 100, n. 3, p. 363-406, 1993. Disponível em: https://doi.org/10.1037/0033-295X.100.3.363. Acesso em: 12 ago. 2026.

KOLB, D. A. **Experiential learning**: experience as the source of learning and development. Englewood Cliffs: Prentice Hall, 1984.

ROEDIGER III, H. L.; KARPICKE, J. D. **The power of testing memory**: basic research and implications for educational practice. Perspectives on Psychological Science, v. 1, n. 3, p. 181-210, 2006. Disponível em: https://doi.org/10.1111/j.1745-6916.2006.00012.x. Acesso em: 12 ago. 2026.

---

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 11-08-2026 | Criação inicial: filosofia, hierarquia de quatro níveis, estrutura de sequência e fotograma, marco zero e ponto de início, validação e erro, pular, tela, dica e sugestão de estudo, composição de sessão, encadeamento e resumos, pausa/ocioso/saída, repetição e retenção, registro e relatório em mais de um formato, e modularidade e configuração manual em dois momentos — montagem do conteúdo (marco zero, fotogramas, textos, ordem/avulso, conteúdo da dica) e configuração de cada sessão (pular, limiar de dica e sugestão de estudo, tempo de ociosidade). | Criação inicial |
| 1.0.0 | 12-08-2026 | Documento aprovado — primeira versão estável do Documento de Conceito do motor. | Aprovação da cascata do motor |
