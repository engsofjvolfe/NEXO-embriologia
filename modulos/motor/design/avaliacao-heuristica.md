# Avaliação heurística — protótipo navegável do motor

<!-- module-doc-type: design-avaliacao-heuristica -->

| Campo | Valor |
|---|---|
| Módulo | Motor |
| Documento | Avaliação heurística |
| Versão | 0.1.0 |
| Data | 30-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Passo 4 do método de desenho visual ([architecture.md, Interface](<../docs/architecture.md#interface>)):
> aplicação das dez heurísticas de usabilidade de Nielsen (1994) sobre
> [`prototipo-navegavel.html`](prototipo-navegavel.html), o protótipo clicável decidido em
> [decisions/0036](<../decisions/0036-ferramenta-e-fidelidade-do-prototipo-navegavel.md>). Cada
> heurística abaixo é explicada em linguagem simples antes de receber um veredito
> (atende / atende parcialmente / risco) e, quando cabível, aponta a decisão da cascata que
> sustenta o comportamento observado ou que precisaria mudar. Mora em `design/`, ao lado do
> protótipo em si — fora do conjunto fixo de documentos de `docs/`.

## Índice
- [Avaliação](#avaliação)
- [Resumo](#resumo)
- [Controle de versão](#controle-de-versão)

## Avaliação

### 1. Visibilidade do estado do sistema

*Em resumo:* o sistema deve sempre mostrar o que está acontecendo, sem deixar a pessoa adivinhando.

**Atende.** O indicador de conexão do acessório ("● conectado" / "◐ procurando" / "○ desconectado")
aparece só durante "Aguardando tentativa" — exatamente o único momento em que a pessoa precisa
saber se o leitor está pronto para receber uma peça (ver
[`design/wireframe.md`](wireframe.md#tela-de-jogo--posição-dos-elementos-comuns)). Fora desse
estado, a interface fica deliberadamente silenciosa — coerente com o Documento de Conceito, seção
8 ("a tela... confirma, não anuncia").

**Risco identificado:** nada no protótipo mostra quantas tentativas erradas faltam até a dica ou a
sugestão de estudo aparecerem (os limiares de `EI-DIC-04` são configurados na tela de
Configuração da sessão, mas nunca expostos de volta durante o jogo). É coerente com a mecânica
pedagógica (a dica é oferecida no momento certo, não pedida — RF-DIC-01), mas vale confirmar, na
validação com pessoas reais, se essa opacidade é desejada ou se é uma lacuna.

### 2. Correspondência entre o sistema e o mundo real

*Em resumo:* o texto da tela deve falar a língua de quem usa, não a língua de quem programou.

**Atende.** A mensagem de negativa é fixa e neutra ("Essa peça não é a próxima da sequência.") —
nunca um código de erro nem jargão técnico, seguindo `DA-RET-08` e a proibição de indicar a peça
certa ou o motivo do erro (`EI-RET-02`).

### 3. Controle e liberdade do usuário

*Em resumo:* a pessoa precisa conseguir sair ou desfazer uma ação sem se sentir presa.

**Atende.** "Pular peça" está sempre disponível durante a espera e durante a sugestão de estudo
(`RF-PUL-04`), nunca condicionado a um limiar. O botão de pausar (canto superior direito) não pede
confirmação — grava o progresso e pode ser retomado depois, sem perda (`EI-PAU-01`,
[findings.md](<../docs/findings.md#2026-08-27-sessionviewmodel-ganha-onpauserequested>)). Só
"Sair" (canto superior esquerdo) é destrutivo, e está atrás de um diálogo de confirmação com duas
ações nomeadas ("Cancelar" / "Sair") — nunca dispensável tocando fora dele
([decisions/0032](<../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md>), item 5).
Ter as duas opções (pausar sem custo, sair com custo e confirmação) dá à pessoa uma saída barata
pra qualquer interrupção — a única perda real e avisada é a de sair de propósito.

### 4. Consistência e padrões

*Em resumo:* o mesmo gesto deve sempre significar a mesma coisa, em toda tela.

**Atende, com uma inconsistência deliberada a observar.** O gatilho de toque segue, sem exceção,
a regra fechada em `decisions/0032`: toque em qualquer lugar da tela nas situações sem decisão
real (confirmação, negativa, dica); botão nomeado só onde existe escolha ou consequência (fim de
evento, pular, sair). A única tela que mistura os dois padrões na mesma superfície é "Sugestão de
estudo" — toque fora do botão dispensa a sugestão, mas o botão "Pular peça" continua nomeado por
cima do toque livre. Isso é decisão deliberada, já registrada como a única exceção em
`decisions/0032`, item 2 — não uma inconsistência acidental — mas é o único estado com esse
padrão híbrido; recomenda-se observar essa tela especificamente numa validação com pessoas reais,
por ser onde mais chance existe de um toque acidental disparar "pular" sem intenção.

### 5. Prevenção de erros

*Em resumo:* melhor impedir o erro de acontecer do que só avisar depois que já aconteceu.

**Atende.** No protótipo, o botão "Continuar" da tela de Consentimento fica desabilitado até a
caixa "Li e concordo" ser marcada — impossível avançar sem o consentimento explícito exigido por
`EI-REG-03`. A única ação destrutiva de toda a interface (sair de uma sessão) está atrás de um
diálogo de confirmação de dois passos, nunca de um toque só.

### 6. Reconhecimento em vez de memorização

*Em resumo:* a pessoa deve poder reconhecer a opção certa na tela, em vez de ter que lembrar de
algo que não está mais visível.

**Atende parcialmente.** A pessoa nunca precisa lembrar em que posição começou (`EI-SES-04` decide
isso pelo motor) nem qual peça pulou (a mensagem de pulo nunca revela nem exige lembrar qual foi,
`EI-PUL-05`). Ponto de atenção: na tela "Resumo do evento", o protótipo mostra só o texto de
síntese (`summary_fragment`, concatenado por `core/summary`) — nenhum resumo visual à parte (por
exemplo, uma lista de acertos e erros daquele evento). Não é falha de interface: o conteúdo desse
texto é escrito por quem monta a instância (Documento de Conceito, seção 15), fora do escopo deste
módulo — mas fica registrado aqui como algo a observar quando um pacote de conteúdo real for usado
na validação com pessoas.

### 7. Flexibilidade e eficiência de uso

*Em resumo:* quem já conhece o sistema deve conseguir usá-lo rápido; quem é novo não deve se
perder.

**Atende, dentro do que a cascata permite.** Não existe atalho de teclado nem gesto alternativo no
protótipo — coerente com o público-alvo real (uso guiado por uma peça física lida por NFC/Bluetooth,
não um aplicativo de produtividade de uso repetido intenso). Nenhuma mudança recomendada.

### 8. Estética e design minimalista

*Em resumo:* a tela não deve ter informação ou enfeite que não ajuda quem está usando.

**Atende.** Todas as telas transitórias do protótipo (confirmação, negativa, dica, sugestão de
estudo) têm um único bloco central de texto, sem elemento decorativo. A cor primária/secundária
(`decisions/0035`) é usada só em botão e destaque de seleção — nunca pra sinalizar acerto ou erro
com uma cor própria (verde/vermelho), o que manteria a tela deliberadamente neutra mesmo ao
confirmar (Documento de Conceito, seção 8: "a tela... confirma, não anuncia"). Essa ausência de
cor de acerto/erro é uma leitura possível dessa regra, aplicada por este protótipo — não uma
exigência explícita de nenhum documento da cascata; um sinal visual sutil (por exemplo, um traço
fino colorido, sem texto nem ícone chamativo) também atenderia à mesma regra. Fica registrado como
ponto a validar, não como decisão fechada.

### 9. Ajudar a pessoa a reconhecer, diagnosticar e corrigir erros

*Em resumo:* quando dá erro, o sistema deveria ajudar a entender o que aconteceu e como corrigir.

**Risco identificado, já esperado pela própria cascata.** A mensagem de negativa é
propositalmente genérica e nunca indica a peça certa (`DA-RET-08`, `EI-RET-02`) — decisão
pedagógica central do motor (forçar reconstrução por tentativa, não entregar a resposta,
Documento de Conceito, seções 1 e 6), em tensão direta com o que esta heurística pede em qualquer
outro tipo de sistema. Não é recomendação de mudança — é a peça central do conceito do NEXO —, mas
fica registrado aqui como uma tensão consciente entre o método de avaliação (pensado para sistemas
em geral) e o propósito pedagógico específico deste produto.

### 10. Ajuda e documentação

*Em resumo:* mesmo um sistema fácil de usar às vezes precisa de uma ajuda disponível, sem forçar
leitura de manual.

**Atende, dentro do escopo.** O termo de consentimento tem tela própria, acessível antes de
qualquer coleta de dado que identifique a pessoa (`EI-REG-03`). Não existe tela de ajuda ou tutorial
do jogo em si no protótipo — nenhum documento da cascata pede uma, e o padrão de silêncio ativo do
Conceito (seção 8) sugere que isso é proposital: a peça física e o texto de confirmação são, eles
mesmos, o tutorial. Sinalizado aqui, sem recomendar acrescentar sem pedido explícito.

## Resumo

Oito das dez heurísticas atendem sem ressalva relevante; duas (visibilidade do estado do sistema,
diagnóstico de erro) têm um ponto de atenção cada, e nenhuma delas exige mudar uma decisão já
fechada na cascata — são, no máximo, pontos pra observar numa validação com pessoas reais:

1. Nenhuma indicação, durante o jogo, de quantas tentativas faltam pra dica/sugestão de estudo
   aparecerem (heurística 1) — decisão consciente (a dica é oferecida, não pedida), mas vale
   confirmar se a opacidade incomoda.
2. A tela "Sugestão de estudo" mistura toque livre com um botão nomeado sobreposto — única tela
   com esse padrão híbrido (heurística 4), decisão deliberada de `decisions/0032`, mas recomenda-se
   observar especificamente essa tela na validação.
3. A mensagem de negativa nunca aponta o erro específico (heurística 9) — tensão consciente entre
   o método de avaliação (pensado pra sistemas em geral) e o propósito pedagógico do motor
   (Documento de Conceito, seções 1 e 6); não é uma falha a corrigir.

Nenhum ponto acima bloqueia a etapa seguinte (implementação em Compose) — todos são observações a
confirmar com pessoas reais usando o sistema, não bugs de interface encontrados no protótipo.

## Controle de versão

<!-- uma linha por versão publicada deste documento, mais antiga no
topo -- nunca reescrita, só acrescentada; toda mudança de conteúdo real
do documento sobe a versão (SemVer) e ganha uma linha nova aqui, junto
com o campo Versão da tabela de cabeçalho, que sempre reflete a
última linha desta tabela. -->

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 30-08-2026 | Criação inicial: avaliação das dez heurísticas de Nielsen sobre `prototipo-navegavel.html`. | Resolução da pendência "Montar o protótipo navegável e avaliar contra as boas práticas de usabilidade" |
