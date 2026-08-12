# NEXO

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow) ![Fase](https://img.shields.io/badge/fase-documenta%C3%A7%C3%A3o-blue) ![Licença](https://img.shields.io/badge/licença-todos%20os%20direitos%20reservados-red)

Um sistema para aprender processos reconstruindo-os com as próprias mãos, em vez de só assistir alguém explicar.

## Qual problema ele resolve

Ver um processo sendo explicado — mesmo bem explicado — deixa pouco rastro na memória de quem assiste. O que fixa um conhecimento de verdade é o esforço de tentar reconstruir aquele processo sozinho, errar, corrigir e tentar de novo. O NEXO existe pra criar esse esforço: ele entrega as peças de um processo fora de ordem e pede que a pessoa descubra, por tentativa, qual é a sequência correta em que elas acontecem.

## Situação atual

O NEXO ainda não tem nenhuma parte funcionando — o projeto está na fase de desenhar, em documentos, exatamente o que o sistema deve fazer, antes de qualquer código ser escrito. É uma escolha deliberada: entender bem o problema primeiro evita construir a coisa errada rápido.

Hoje já estão prontos e aprovados todos os documentos que descrevem o motor (a parte central do sistema, que não conhece nenhum assunto específico — ver seção abaixo): o que ele é, o que ele precisa fazer, como ele atende a cada uma dessas necessidades, como suas peças se conectam entre si, e o detalhe exato de cada peça. Aqui a documentação fala com desenvolvedores de software, um público mais técnico. O próximo passo é começar a escrever o código do motor.

Acompanhar o que muda em [HANDOFF.md](HANDOFF.md).

## Como funciona, em linguagem simples

Um processo qualquer — uma etapa de um procedimento, uma sequência de eventos que acontece um depois do outro — é dividido numa série de "fotografias" de momentos daquele processo, um retrato de como ele estava em cada instante. Cada fotografia vira uma peça física. Quem usa o sistema recebe essas peças fora de ordem e precisa descobrir, por tentativa e erro, a ordem em que elas realmente acontecem. O sistema não explica nada antes: ele só confirma se a peça colocada está certa ou errada naquele ponto. Quem ensina é a própria tentativa, não uma explicação prévia.

O NEXO em si não sabe nada sobre nenhum assunto — ele é só o mecanismo por trás disso tudo (chamado, neste projeto, de "motor"). Qualquer processo com etapas serve: as fases de uma batalha pra um professor de história, os eventos de uma cirurgia pra quem ensina medicina, os passos de uma reação pra quem ensina química, o desenrolar de uma obra pra quem ensina literatura. O motor em si é software, e construí-lo exige código, como qualquer sistema — isso é trabalho de quem desenvolve o NEXO, ou alguém que entenda um pouco disso, feito uma vez. Usar o motor pra montar um conteúdo novo é outra coisa: cada aplicação prática dele, sobre um assunto específico, é feita preenchendo-o com conteúdo próprio — os eventos, as peças, os textos daquele assunto — sem mudar o mecanismo em si e sem que quem monta esse conteúdo (um professor, por exemplo) precise escrever qualquer linha de código.

## Para quem é

Pra quem aprende: qualquer pessoa que precise entender um processo com etapas já bem definidas, de qualquer área — não é necessário nenhum conhecimento técnico prévio pra usar o sistema.

Pra quem ensina: professores e educadores de qualquer disciplina que tenha um processo com ordem própria — história, medicina, química, língua, o que for. Se o seu conteúdo tem um "isso vem antes daquilo", o NEXO já sabe lidar com ele.

## Licença

Todos os direitos reservados. Nada deste projeto — documentos, esquemas técnicos, ou qualquer código que vier a existir — pode ser copiado, redistribuído, modificado ou usado sem autorização direta de quem detém os direitos, concedida caso a caso. Ver [LICENSE](LICENSE).

## Créditos

Autoria assinada como **N. Denominado** — de propósito: o nome verdadeiro por trás do projeto nunca foi decidido, e não vai ser, aqui.