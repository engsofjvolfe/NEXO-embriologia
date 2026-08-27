# Handoff — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Handoff |
| Versão | 0.1.0 |
| Data | 27-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Resumo curto de "onde este módulo está" e "o que fazer a seguir" nele.
> Última etapa do fluxo — atualizado depois de qualquer uma das outras
> mudar. Só ponteiro, nunca detalhe, nunca resumo — detalhe de verdade
> mora em `concept.md`, `tasks.md`, `findings.md` ou `decisions/`.
>
> Cada linha segue [a regra de escrita geral](../../README.md#como-escrever):
> link markdown de verdade + uma frase curta, nunca uma descrição do
> que o conteúdo diz.

## Índice
- [Estado atual](#estado-atual)
- [Próximo passo](#proximo-passo)
- [Controle de versão](#controle-de-versão)

## Estado atual

- [concept.md](concept.md) — o que o módulo deve ser e como deve se
  comportar.
- [architecture.md](architecture.md) — como o módulo é construído por
  dentro.
- [`MANUAL.md`](../../../MANUAL.md) — fonte normativa direta deste
  módulo, na raiz do repositório.
- [decisions/0001-cobertura-do-gancho-de-leitura-obrigatoria.md](<../decisions/0001-cobertura-do-gancho-de-leitura-obrigatoria.md>) —
  gancho de leitura obrigatória passa a cobrir toda ferramenta, não só
  escrita.
- [decisions/0002-comparacao-de-caminho-ignora-maiuscula-e-minuscula.md](<../decisions/0002-comparacao-de-caminho-ignora-maiuscula-e-minuscula.md>) —
  comparação de caminho de worktree corrigida pro Windows.
- [decisions/0003-deteccao-de-esquema-embutido-em-documento.md](<../decisions/0003-deteccao-de-esquema-embutido-em-documento.md>) —
  checagem de esquema puro passa a cobrir esquema embutido em
  documento.
- [decisions/0004-checagem-de-instrucoes-do-usuario-no-gancho-de-stop-existente.md](<../decisions/0004-checagem-de-instrucoes-do-usuario-no-gancho-de-stop-existente.md>) —
  revisor do fim da resposta passa a conferir instrução do usuário não
  atendida.
- [decisions/0005-formalizacao-do-sistema-de-conformidade-como-modulo.md](<../decisions/0005-formalizacao-do-sistema-de-conformidade-como-modulo.md>) —
  por que este sistema virou módulo, com `MANUAL.md` continuando como
  fonte normativa.
- [findings.md](findings.md) — achados confirmados até agora.
- [analysis.md](analysis.md) — registro de como cada investigação
  deste módulo foi feita.
- [pitfalls.md](pitfalls.md) — armadilhas de ferramenta já encontradas.
- [tasks.md, Em aberto](<tasks.md#em-aberto>) — pendências abertas.

## Próximo passo

- [tasks.md, Em aberto](<tasks.md#em-aberto>) — confirmação de ponta a
  ponta numa sessão nova é a pendência mais direta; a auditoria linha
  a linha do `CLAUDE.md` é a mais ampla, combinada pra depois.

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial. | Criação inicial do módulo |
