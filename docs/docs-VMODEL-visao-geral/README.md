# Documentos VMODEL — Visão Geral

<!-- doc-type: readme -->

Os cinco documentos desta pasta formam a cascata do modelo em V do
motor — conceito, requisitos, especificação, projeto arquitetônico e
projeto detalhado, cada um com o próprio campo `Situação` no cabeçalho.

## Regra de imutabilidade

Um documento desta pasta, uma vez com `Situação: Aprovado` no
cabeçalho, não é mais editado — nunca, por nenhum motivo. Nem correção
de redação, nem erro factual pequeno, nem ajuste de versão. Nenhuma
exceção vale aqui — nem mesmo a que existe pra ADR em `decisions/` de
um módulo (ver `modulos/_template/decisions/README.md`), que permite
"corrigir erro factual" depois de aceita.

Qualquer ajuste necessário depois da aprovação nasce em outro lugar: um
documento de módulo (`modulos/<nome>/docs/`), uma ADR nova, ou uma nota
separada — nunca reescrevendo o que já foi aprovado nesta pasta.

Por quê: o valor desses cinco documentos como registro histórico e
fonte de verdade da cascata do V-Model depende de eles nunca mudarem
debaixo dos pés de quem já leu ou decidiu algo com base neles.
