# Architecture — <nome do módulo>

<!-- module-doc-type: architecture -->

> Descreve como o módulo é construído por dentro — layout de arquivos,
> pacote, fronteiras, fluxo de dados técnico. É o "como" que corresponde
> ao "o quê" do `concept.md`; lido logo em seguida, quando existir.
>
> Implementação de código deriva sempre daqui e do contrato em
> `schemas/` — nunca o contrário: nunca escrever código primeiro e
> desenhar arquitetura/schema depois só pra bater com o que já foi
> escrito.
>
> Só cobre a parte do módulo cujo "como construir" já foi desenhado a
> partir do `concept.md` — nunca escrito a partir do código já
> existente. Se `concept.md` já cobre uma parte sem esse desenho
> ainda, isso é dito explicitamente aqui, com ponteiro pra pendência
> em `tasks.md` — nunca um documento incompleto sem explicação.
>
> Vale igual quando o módulo já tem código: o "como" aqui é sempre o
> correto, desenhado a partir do requisito em `concept.md`, nunca um
> espelho do código já existente. Se o código atual não bate com o
> "como" desenhado aqui, quem muda é o código, não a arquitetura -- a
> correção fica pendência em `tasks.md` até acontecer.
>
> Cada seção segue [a regra de escrita geral](../../README.md#como-escrever):
> resumo simples primeiro, detalhe técnico depois.

## Índice
- [Layout](#layout)

## Layout

<!--
*Em resumo:* ...

*Em detalhe técnico:* ...

Se alguma parte do concept.md ainda não tem arquitetura desenhada:
"A parte de <assunto> (ver concept.md#<ancora>) ainda não tem
arquitetura -- desenho de como construir pendente, ver
tasks.md#em-aberto."
-->
