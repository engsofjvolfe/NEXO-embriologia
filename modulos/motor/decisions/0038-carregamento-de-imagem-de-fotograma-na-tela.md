# 0038 — Carregamento de imagem de fotograma na tela

Resumo em linguagem simples: `SessionScreen.Reference` (e campos parecidos noutras variantes)
carrega só o caminho da imagem do fotograma dentro do pacote de conteúdo — nunca os bytes da
imagem em si. Faltava decidir como esse caminho vira pixel de verdade na tela. Esta ADR decide:
`BitmapFactory` (parte do próprio Android, sem biblioteca nova) decodifica o `ByteArray` já
disponível via `ContentPackageArchive.readImage(path)`, fora da thread principal, com cache
simples por `remember`.

Convenção dos códigos citados abaixo:
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Status:** aceito

**Contexto:**

Achado durante a escrita da tela de jogo (`SessionGameScreen.kt`, decisions/0037): nenhum
documento decide como um caminho de imagem (`frame.image`/`zero_mark.image`, PD-IMP-01) chega a
aparecer de verdade numa tela Compose. `core/content/ContentPackageArchive.kt` já expõe
`readImage(path: String): ByteArray`
([architecture.md, pacote `content`](<../docs/architecture.md#pacote-content--desenho-interno>))
— o problema é só a última etapa, converter esse `ByteArray` num `Composable Image`.

Fonte oficial consultada (Android Developers, acesso em 01 set. 2026, guia de carregamento de
imagem em Compose): o caminho recomendado pra um `ByteArray` já em memória é
`BitmapFactory.decodeByteArray(bytes, 0, bytes.size)`, seguido de `.asImageBitmap()` (a conversão
do `Bitmap` do Android pro tipo `ImageBitmap` que o Compose usa), passado então pro `Composable
Image(bitmap = ...)`. A mesma fonte alerta que decodificar um `Bitmap` é uma operação cara e não
deve rodar na thread principal, e recomenda guardar o resultado em cache pra não decodificar o
mesmo `ByteArray` de novo a cada recomposição — a mesma fonte cita bibliotecas de terceiro (Coil,
Glide) como caminho pra casos mais complexos (cache entre navegações, redimensionamento
automático), sem exigir isso pro caso deste módulo.

Duas alternativas reais:

1. **`BitmapFactory` + `asImageBitmap()`** (alternativa escolhida, ver Decisão) — sem dependência
   nova, caminho oficial confirmado.
2. **Biblioteca de carregamento de imagem de terceiro** (Coil, já citada na mesma fonte oficial)
   — resolveria cache entre navegações e redimensionamento automático de graça, mas nenhum
   documento deste módulo levantou a necessidade desses dois problemas ainda (cada fotograma é
   uma imagem pequena, decidida por quem monta o conteúdo — DA-IMP-06 já registra preferência por
   caminho aberto e sem dependência nova onde a alternativa mais simples já resolve).

**Decisão:**

1. Decodificação via `BitmapFactory.decodeByteArray(bytes, 0, bytes.size)`, convertida com
   `.asImageBitmap()`, exibida com `Image(bitmap = ...)` — sem biblioteca de terceiro.
2. A decodificação roda fora da thread principal (`Dispatchers.Default`, já uma dependência
   existente do projeto — `kotlinx-coroutines-android`), nunca direto na composição — segue a
   mesma recomendação da fonte oficial.
3. Cache simples por `remember(caminhoDaImagem)` dentro do próprio Composable que mostra a
   imagem — evita decodificar de novo a cada recomposição da mesma tela, sem introduzir uma
   camada de cache entre telas diferentes (fora do escopo: guardar imagem já decodificada entre
   uma sessão e outra é assunto da pendência ainda em aberto sobre onde o conteúdo importado fica
   guardado no aparelho).
4. `ContentPackageArchive` (o `Closeable` que dá acesso a `readImage`) precisa estar acessível no
   momento em que a tela pede a imagem — hoje só existe durante a importação
   ([architecture.md, pacote `content`](<../docs/architecture.md#pacote-content--desenho-interno>)).
   Manter essa instância aberta e acessível pra tela consumir imagem sob demanda é trabalho de
   implementação, não desta ADR — fica registrado como parte da pendência maior de ligar a
   navegação a conteúdo de verdade (`tasks.md`).

**Consequências:**

Fecha a pendência "Decidir como o caminho de imagem de um fotograma vira pixel de verdade na
tela" em `tasks.md`. Nenhuma das 7 telas de navegação escritas depois desta ADR mostra imagem de
fotograma — só a tela de jogo (`SessionGameScreen.Reference`) usa isso, e a troca do texto
provisório por uma imagem de verdade depende do ponto 4 acima (acesso à instância de
`ContentPackageArchive`), que só existe quando a importação de conteúdo estiver ligada de
verdade — trabalho de implementação futuro, não desta ADR.

## Referências

Fonte externa consultada para embasar esta decisão, no formato definido pela norma ABNT NBR 6023
(Informação e documentação — Referências). Citada no corpo do documento como (GOOGLE, ano).

GOOGLE. **Load images**. Android Developers — Jetpack Compose, [s.d.]. Disponível em:
https://developer.android.com/develop/ui/compose/graphics/images/loading. Acesso em: 01 set.
2026.
