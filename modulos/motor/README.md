# Motor

<!-- doc-type: readme -->

O motor é o mecanismo por trás do NEXO — o programa que faz o sistema
funcionar de verdade, sem saber nada sobre nenhum assunto específico.

## Qual problema ele resolve

Sem o motor, cada vez que alguém quisesse aplicar a ideia do NEXO
(reconstruir um processo com as próprias mãos, fora de ordem) a um
assunto novo — embriologia, história, química, o que for —, teria que
construir o mecanismo inteiro de novo, do zero. O motor existe pra que
isso nunca precise acontecer: ele é construído uma única vez, e
qualquer assunto novo entra nele só como conteúdo, sem tocar no
mecanismo em si.

## Situação atual

Em construção. O que o motor precisa ser e fazer já está totalmente
decidido (ver [`concept.md`](docs/concept.md)), como o código vai se
organizar por dentro também já está desenhado (ver
[`architecture.md`](docs/architecture.md)), e as duas linguagens de
programação envolvidas já foram escolhidas (ver [`decisions/`](decisions/)).
Parte do código já existe: a lógica interna do aplicativo (cadastro e
navegação de conteúdo, busca, e a lógica de uma partida jogada) e o
projeto Android mínimo que a hospeda, ainda sem nenhuma tela de
verdade. Acompanhar o que muda em [`docs/handoff.md`](docs/handoff.md).

## Como funciona, em linguagem simples

O motor tem duas partes. A principal é um aplicativo que roda no
aparelho de quem joga — é ele que sabe a ordem certa de cada peça,
confirma ou nega uma tentativa, e guarda o resultado ao final. A
segunda é um pequeno acessório físico, usado só quando o aparelho não
consegue ler as peças sozinho: ele só lê a peça e avisa o aplicativo
qual foi — não guarda nada, não decide nada.

## Para quem é

Pra quem joga: o motor é a base de qualquer instância do NEXO — quem
joga nunca interage com o motor diretamente, só com o conteúdo
montado em cima dele. Pra quem monta conteúdo novo (um professor, por
exemplo): o motor é o que permite montar uma instância nova
preenchendo um arquivo de conteúdo, sem escrever nenhuma linha de
código.

## Licença

Todos os direitos reservados — ver [LICENSE](../../LICENSE).

## Créditos

Mesma autoria do projeto NEXO — ver [README.md](../../README.md) da
raiz.
