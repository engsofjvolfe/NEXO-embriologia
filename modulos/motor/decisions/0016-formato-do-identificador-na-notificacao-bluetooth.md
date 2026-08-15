# 0016 — Formato do identificador de peça na notificação Bluetooth

Resumo em linguagem simples: quando o acessório externo lê uma peça e
avisa o aplicativo por Bluetooth, ele manda os bytes brutos do
identificador físico da etiqueta — sem transformar em texto antes de
mandar. É o aplicativo, do lado de dentro, quem transforma esses bytes
no formato de texto já usado no resto do sistema, com a mesma conta
que já faz isso pra leitura direta por NFC. Essa escolha não vem de
nenhuma exigência técnica do Bluetooth em si — o serviço usado (Nordic
UART Service) aceita qualquer formato de bytes, sem opinião própria —
é uma escolha deste projeto, tomada agora porque o programinha que roda
no acessório (o firmware) ainda nem foi escrito: dá pra decidir o
formato antes, em vez de esperar pra descobrir depois o que o firmware
"acabou fazendo".

Convenção dos códigos citados abaixo:
- `DA-LEI` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.1.
- `PD-LEI` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.1.
- `PD-CON` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.2.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. O trecho abaixo marcado
descreve código-fonte de terceiro (Nordic Semiconductor) — não uma
decisão deste projeto, sujeito a mudar em qualquer revisão futura
dessa documentação. Quem ler este documento depois deve tratar esse
conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra qualquer
decisão nova.

**Status:** aceito

**Contexto:**

[decisions/0015](0015-fronteira-entre-core-e-app-no-pacote-connectivity.md)
já decidiu que `core/connectivity/TagId.kt` guarda `tagIdFromBytes`,
uma função pura que transforma o identificador físico bruto (bytes) no
mesmo formato de texto hexadecimal usado no campo `tag_id` do pacote de
conteúdo (PD-IMP-01). Essa função foi escrita pensando no caminho de
leitura direta por NFC, onde o Android devolve o identificador da
etiqueta como bytes brutos. Ficou em aberto se o caminho via acessório
usa a mesma função, ou se o próprio acessório já entrega o texto
pronto.

PD-CON-03 só diz que a notificação da característica TX "contém o
identificador da etiqueta", sem fixar o formato exato dos bytes. O
Projeto Arquitetônico, §2.2, defere de propósito o "protocolo exato de
comunicação... formato de mensagem" para o Projeto Detalhado — e o
Projeto Detalhado, por sua vez, também não desce a esse nível (checado
lendo os dois documentos por completo, não só a citação já conhecida):
fixa os UUIDs e os papéis (cliente/servidor), mas não o formato exato
do dado transportado.

`[REVISAR-EXTERNO]` O próprio serviço Bluetooth usado (Nordic UART
Service) também não impõe formato: checando o código-fonte oficial da
API (`nus.h`, no repositório oficial da Nordic Semiconductor, já citado
como fonte em PD-CON-02), a função de envio (`bt_nus_send`) recebe um
`const uint8_t *data` — bytes puros, sem exigência de estrutura. O
transporte é um cano de bytes; decidir o que passa por dentro dele é
responsabilidade de quem constrói os dois lados — neste caso, este
mesmo projeto (NORDIC SEMICONDUCTOR ASA, 2018-).

Duas alternativas reais:

(a) O acessório converte o identificador físico bruto pro texto
hexadecimal (o mesmo formato do `tag_id`) antes de mandar, e manda
texto puro (bytes ASCII) na notificação. O aplicativo só lê a
notificação como texto, sem chamar `tagIdFromBytes` neste caminho — só
chamaria essa função no caminho de leitura direta por NFC.

(b) O acessório manda o identificador físico bruto, sem conversão
nenhuma, exatamente como o módulo leitor PN532 devolve (PD-LEI-01). O
aplicativo aplica `tagIdFromBytes` nos dois caminhos — a mesma função,
os dois casos.

**Decisão:** alternativa (b).

DA-LEI-06 já exige que os dois caminhos de leitura sejam "tratados do
mesmo jeito" pela lógica de validação. A alternativa (b) cumpre essa
exigência de um jeito mais forte que (a): não é só que o resultado
final (o texto do `tag_id`) fique igual nos dois casos — é literalmente
o mesmo código, a mesma função (`tagIdFromBytes`, já escrita e testada
em `core/connectivity/TagId.kt`), rodando pros dois casos. A
alternativa (a) obrigaria o firmware do acessório (C++, ainda não
escrito) a reimplementar, numa linguagem diferente, a mesma conversão
que o Kotlin já faz — duas implementações da mesma regra, com risco
real de divergência (caixa alta/baixa, zero à esquerda) que só
apareceria testando contra hardware de verdade, tarde demais pra
corrigir barato.

A alternativa (a) também pede mais trabalho do firmware sem ganho
nenhum: o PN532 já entrega o identificador em bytes brutos (NXP
SEMICONDUCTORS, 2011, já citado em PD-LEI-01) — repassar esses bytes
sem alterar é o caminho de menor esforço pro firmware, e mantém a
única conversão (que já existe, testada) num lugar só.

**Consequências:**

`tagIdFromBytes` passa a ser chamada pelos dois lados de `app` que
tocam hardware de verdade — o `Service` de Bluetooth (sobre a
notificação recebida da característica TX) e a leitura NFC direta
(sobre o identificador bruto devolvido pela `Tag` do Android) — nenhum
dos dois faz conversão própria. Quando o firmware do acessório for
escrito (pendência separada, ainda não iniciada — ver
[tasks.md](<../docs/tasks.md#em-aberto>)), ele segue esta decisão:
repassa os bytes brutos do PN532 sem conversão, sem montar texto
nenhum do lado dele.

## Referências

Fonte externa citada no Contexto, no formato definido pela norma ABNT
NBR 6023 (Informação e documentação — Referências). Citada no corpo do
documento como (ENTIDADE, ano).

NORDIC SEMICONDUCTOR ASA. **nus.h — Nordic UART (NUS) GATT Service
API** [código-fonte]. In: nRF Connect SDK (sdk-nrf), branch main.
[S.l.], 2018-. Disponível em:
https://github.com/nrfconnect/sdk-nrf/blob/main/include/bluetooth/services/nus.h.
Acesso em: 14 ago. 2026.
