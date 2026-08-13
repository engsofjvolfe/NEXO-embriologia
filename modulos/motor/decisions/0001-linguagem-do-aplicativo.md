# 0001 — Linguagem do aplicativo

Resumo em linguagem simples: o aplicativo (o programa que roda no
aparelho de quem joga) vai ser escrito em Kotlin — a linguagem que o
próprio Google recomenda hoje pra aplicativos Android novos.

Convenção do código citado abaixo:
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Status:** aceito

**Contexto:** nenhum documento da cascata
([`docs/docs-VMODEL-visao-geral/`](<../../../docs/docs-VMODEL-visao-geral/>))
fixou a linguagem de programação do aplicativo — só decidiu peças que
tanto Java quanto Kotlin conseguem usar igual, como a classe
`java.util.zip.ZipFile`, do próprio SDK do Android/Java (
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
PD-IMP-03). Pra `architecture.md` poder descer ao nível de layout de
código, era preciso escolher entre as duas linguagens usuais de
desenvolvimento Android: Java e Kotlin.

**Decisão:** Kotlin.

**Consequências:** Kotlin é a linguagem oficialmente recomendada pelo
Google pra desenvolvimento Android desde 2019, com acesso total às
mesmas bibliotecas do SDK que os documentos já citam (inclusive
`java.util.zip.ZipFile`), sintaxe mais enxuta que Java, e proteção
nativa contra erro de referência nula — um tipo de erro comum em Java.
Quem for acompanhar a implementação sem conhecimento prévio de Kotlin
vai precisar aprender a linguagem em paralelo ao código sendo escrito;
nenhum outro custo de aprendizado foi avaliado além desse.
