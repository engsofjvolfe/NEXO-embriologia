# 0002 — Framework do firmware do acessório leitor

Resumo em linguagem simples: o firmware do acessório (o pequeno
aparelho externo com o chip leitor de NFC e o microcontrolador) vai
ser escrito em C++, usando o framework Arduino, com o PlatformIO como
ferramenta de build.

Convenção dos códigos citados abaixo:
- `PD-LEI` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.1.
- `PD-CON` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.2.

**Status:** aceito

**Contexto:** o
[Projeto Detalhado](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>)
fixou o hardware exato do acessório — módulo leitor NXP PN532/C1
(PD-LEI-01) e microcontrolador Espressif ESP32-D0WD-V3 (PD-LEI-02) —
e o serviço Bluetooth exato que ele precisa implementar, o Nordic
UART Service (PD-CON-01, PD-CON-02) — mas não decidiu em que
linguagem nem com qual framework esse firmware seria escrito. Duas
alternativas reais foram consideradas: (a) Python — mais
especificamente MicroPython, já que Python "puro" não roda em
microcontrolador —, usando PlatformIO; (b) C++, usando o framework
Arduino, também via PlatformIO.

**Decisão:** C++ com framework Arduino, via PlatformIO.

**Consequências:** tanto o driver do PN532 quanto a implementação de
um serviço Bluetooth customizado (como o Nordic UART Service) têm
bibliotecas prontas, maduras e amplamente usadas na combinação ESP32 +
Arduino — caminho com bem menos código pra escrever do zero. Em
MicroPython, o suporte a Bluetooth de baixo consumo no ESP32 existe,
mas montar um serviço customizado como o NUS é bem menos documentado,
com menos exemplo pronto pra seguir. O PlatformIO em si é uma
ferramenta majoritariamente voltada a projetos C/C++; usá-lo com
C++/Arduino é o caminho mais direto dela — usá-lo com MicroPython é
possível, mas incomum. O firmware da placa fica em linguagem diferente
da do aplicativo (Kotlin, ver ADR 0001) — aceitável, porque são dois
ambientes de execução completamente separados (microcontrolador e
Android) que nunca compartilham código entre si.
