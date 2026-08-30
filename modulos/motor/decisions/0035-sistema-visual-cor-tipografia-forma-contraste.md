# 0035 — Sistema visual: cor, tipografia, forma, contraste e área de toque

Resumo em linguagem simples: decide o passo 3 do método de desenho
visual já fixado em `architecture.md` (aplicar cor, fonte, formato de
canto e regra de legibilidade sobre o esqueleto de tela já pronto,
`design/wireframe.md`). O sistema visual usado é o Material Design 3,
do Google, já preparado pelo Jetpack Compose. A cor de identidade é
laranja e azul; a fonte é a padrão do próprio Android; o formato de
canto de botão e caixa segue o padrão pronto do Material; o nível de
legibilidade exigido é o mais usado no mercado; a área mínima de toque
segue a regra técnica que o próprio Android já fixa.

**Status:** aceito

**Contexto:**

`architecture.md`, seção Interface, já fixa um método de quatro passos
pro desenho visual das telas do motor (arquitetura de informação →
wireframe → sistema visual → protótipo navegável e avaliação),
apoiado no processo mais amplo de design centrado no ser humano (ISO
9241-210, já citada em `architecture.md, Referências`). O passo 2
(wireframe) já está resolvido — ver `design/wireframe.md`. Este ADR
resolve o passo 3: aplicar cor, tipografia, forma, contraste e área de
toque sobre esse esqueleto — pendência registrada em `tasks.md` como
"Aplicar o sistema visual (Material Design) sobre o esqueleto de tela
já pronto".

Investigação completa, com trecho literal de cada fonte, em
[analysis.md](<../docs/analysis.md#2026-08-30-pesquisa-do-sistema-visual-material-design-e-cor-semente>).

**Decisão:**

1. Sistema visual: Material Design 3 (GOOGLE, [s.d.]a), já preparado
   nativamente pelo Jetpack Compose, ferramenta de desenho de tela já
   escolhida em
   [decisions/0031](0031-jetpack-compose-como-ferramenta-de-desenho-de-tela.md)
   (`androidx.compose.material3`).
2. Cor: cor dinâmica quando o aparelho suporta (Android 12 ou mais
   novo, gerada a partir do papel de parede da pessoa); nos demais
   aparelhos, cor semente fixa — laranja `#FF6D1F` e azul royal
   `#2D5FE0`, uma das seis combinações comparadas em
   [design/cor-semente-candidatas.html](<../design/cor-semente-candidatas.html>).
3. Tema: claro e escuro, os dois suportados desde já, seguindo por
   padrão o que a pessoa já configurou no sistema Android
   (`isSystemInDarkTheme()`). Escolha manual entre os dois, direto
   dentro do aplicativo, fica de fora deste ADR — depende de uma tela
   de configurações que ainda não existe, pendência própria.
4. Tipografia: fonte padrão do sistema Android (Roboto) — nenhuma
   fonte própria trazida pro aplicativo. Escala de texto no padrão do
   Material 3: cinco categorias (display, headline, title, body,
   label), cada uma em três tamanhos.
5. Forma: escala de arredondamento padrão do Material 3, cinco
   tamanhos (4dp a 24dp), sem ajuste próprio.
6. Contraste: nível comum da WCAG (nível AA) — 4,5 para 1 em texto
   normal, 3 para 1 em texto grande e em componente gráfico não
   textual.
7. Área de toque: 48dp por 48dp, mínimo, pra todo controle tocável —
   regra técnica já fixada pelo próprio Android.

**Consequências:**

Fecha a pendência "Aplicar o sistema visual (Material Design) sobre o
esqueleto de tela já pronto", registrada em
[tasks.md](<../docs/tasks.md#em-aberto>), desbloqueando "Montar o
protótipo navegável e avaliar contra as boas práticas de usabilidade".

Nenhum código escrito ainda — aplicar de verdade essas escolhas (um
`MaterialTheme` com `colorScheme`/`typography`/`shapes` em Kotlin) fica
pra quando a implementação da interface começar, fora do escopo deste
ADR, mesma separação entre desenho e código já usada em
[decisions/0030](0030-padrao-de-navegacao-hierarquica-de-conteudo.md),
[decisions/0033](0033-formato-de-aparelho-leiaute-responsivo.md) e
[decisions/0034](0034-mecanismo-de-carregamento-preguicoso-do-acordeao-de-navegacao.md).

Escolha manual entre tema claro e escuro (ponto 3) e o menu de
configurações que essa escolha depende seguem como pendência própria em
`tasks.md`, fora do escopo deste ADR.

## Referências

Fontes externas consultadas para embasar esta decisão, no formato
definido pela norma ABNT NBR 6023 (Informação e documentação —
Referências). Citações traduzidas livremente no corpo do documento;
texto original preservado entre aspas antes da tradução quando citado
diretamente. Citadas no corpo do documento como (AUTOR, ano).

GOOGLE. **Material Design 3 in Jetpack Compose**. Android Developers,
[s.d.]a. Disponível em:
https://developer.android.com/develop/ui/compose/designsystems/material3.
Acesso em: 30 ago. 2026.

GOOGLE. **Make apps more accessible**. Android Developers, [s.d.]b.
Disponível em: https://developer.android.com/guide/topics/ui/accessibility/apps.
Acesso em: 30 ago. 2026.

GOOGLE. **Enable users to personalize their color experience**
(cor dinâmica). Android Developers, [s.d.]c. Disponível em:
https://developer.android.com/develop/ui/views/theming/dynamic-colors.
Acesso em: 30 ago. 2026.

GOOGLE. **Staying true to your identity: Material branding**. Google
Design, [s.d.]. Disponível em:
https://design.google/library/staying-true-to-your-identity-material-branding.
Acesso em: 30 ago. 2026.

GOOGLE. **Customizing Material color**. Codelabs — Android Developers,
[s.d.]. Disponível em:
https://codelabs.developers.google.com/customizing-material-color.
Acesso em: 30 ago. 2026.

WORLD WIDE WEB CONSORTIUM. **Understanding Success Criterion 1.4.3:
Contrast (Minimum)**. W3C — Web Accessibility Initiative, [s.d.].
Disponível em: https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html.
Acesso em: 30 ago. 2026.
