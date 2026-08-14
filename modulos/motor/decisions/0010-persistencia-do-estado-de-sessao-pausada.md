# 0010 — Persistência do estado de sessão pausada em disco

Resumo em linguagem simples: quando alguém pausa uma partida (ou o
aparelho desliga no meio), o sistema precisa lembrar, na próxima vez
que abrir, exatamente onde a pessoa parou. Isso só funciona se esse
estado for salvo de verdade no aparelho, sobrevivendo ao aplicativo
fechar. A decisão aqui é como `session` grava e lê esse arquivo sem
quebrar a regra já existente de que o núcleo do motor não depende de
nada específico do Android.

Convenção dos códigos citados abaixo:
- `EI-PAU` — [`3 - especificacao-conceito-geral.md`](<../../../docs/docs-VMODEL-visao-geral/3 - especificacao-conceito-geral.md>), seção 6.12.
- `DA-ARM` — [`4 - projeto-arquitetonico.md`](<../../../docs/docs-VMODEL-visao-geral/4 - projeto-arquitetonico.md>), seção 6.3.
- `PD-IMP` — [`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>), seção 6.3.

**Nota de leitura — marcador `[REVISAR-EXTERNO]`:** mesmo marcador já
usado nas ADRs anteriores deste módulo. Os trechos abaixo marcados
descrevem documentação oficial de um terceiro (Android/Google) — não
uma decisão deste projeto, e sujeitos a mudar em qualquer revisão
futura dessa documentação. Quem ler este documento depois deve tratar
esse conteúdo como possivelmente desatualizado e reconfirmar na fonte
oficial (seção Referências) antes de usar como base pra mudar o
código.

**Status:** aceito

**Contexto:** releitura completa da cascata aprovada (documentos 1 a
5) confirma que nenhum documento decide o mecanismo técnico de guarda
do estado de sessão — só que ele é sempre local, nunca em servidor
(DA-ARM-01). O Projeto Detalhado é explícito, na própria seção 5, que
só desceu a esse nível de detalhe pra quatro categorias (Leitura,
Conectividade, Importação, Navegação); Armazenamento nunca voltou a
aparecer lá. EI-PAU-01 e EI-PAU-02 exigem que o estado sobreviva a uma
pausa, uma ociosidade, ou o aparelho sendo desligado sem passar pela
ação de sair — mas não dizem como.

O ponto concreto que falta resolver: a forma oficialmente recomendada
pelo Android de guardar um arquivo privado do aplicativo —
`context.filesDir`, `context.openFileOutput()` — depende inteiramente
de um `Context`. `[REVISAR-EXTERNO]` Confirmado direto na documentação
oficial: toda função dessa API pede um objeto `Context` como
parâmetro; em troca, o Android garante isolamento de outros
aplicativos, criptografia automática a partir do Android 10 (API 29),
e remoção automática do arquivo quando o aplicativo é desinstalado
(GOOGLE, [s.d.]a). Só que `core` — onde `session` mora — foi decidido,
desde [decisions/0003](0003-estrutura-de-modulos-do-aplicativo.md),
como sem nenhuma dependência de classe do Android. `Context` é
exatamente uma dessas classes.

Duas formas de resolver esse conflito foram consideradas:

1. `[REVISAR-EXTERNO]` Seguir o padrão que o próprio guia oficial de
   arquitetura do Android recomenda pra esse tipo de situação: a
   camada que decide as coisas (o "domínio", equivalente a `core`)
   nunca toca `Context` diretamente — só conhece uma interface
   abstrata; a implementação de verdade, que usa `Context`, vive numa
   camada de fora (`app`), injetada por trás dessa interface
   (GOOGLE, [s.d.]b).
2. Reaproveitar o precedente que este mesmo projeto já fixou pro
   mesmo tipo de problema: o pacote `content` (PD-IMP-03, ainda não
   escrito) vai ler o pacote de conteúdo com `java.util.zip.ZipFile`
   — uma classe do Java puro, não do Android, que só pede um caminho
   de arquivo, nunca um `Context`. Nenhuma interface abstrata foi
   criada pra isso; o caminho, de fato, é só passado pra dentro de
   `core` como parâmetro.

A diferença entre as duas não é "qual é mais robusta" isolado do
contexto — é se o motivo que justifica a primeira (isolar uma
dependência instável, presa a uma plataforma, difícil de testar) se
aplica aqui. `Context` tem esse problema; `java.io.File` não —
é uma peça padrão do Java, estável há décadas, roda igual em qualquer
JVM (Android incluído), e testável sem simular nada do Android (um
arquivo temporário comum já resolve, mesma abordagem de teste de JVM
pura já fixada em
[decisions/0005](0005-abordagem-de-teste-do-nucleo-do-motor.md)).
Criar uma interface só pra isolar `File` seria proteger contra um
problema que `File` não tem. Nenhum ponto da cascata aprovada sinaliza
uma necessidade concreta de trocar de mecanismo de guarda (banco de
dados local, por exemplo) — DA-ARM-01 fixa só "local, sem servidor",
nada além disso.

**Decisão:**

1. **`session` grava e lê o estado de sessão pausada usando só
   `java.io.File` (Java puro), nunca uma API de armazenamento do
   Android** (`SharedPreferences`, `Room`, `DataStore` — todas
   dependentes de `Context`). O caminho do arquivo é recebido como
   parâmetro, nunca decidido dentro de `core`.

2. **Decidir o caminho de verdade — chamar `context.filesDir`, montar
   o nome do arquivo — é responsabilidade do módulo `app`**, o único
   que tem acesso a `Context`. `core` só sabe "escrever bytes num
   lugar", sem saber que esse lugar é o armazenamento interno
   oficialmente recomendado do Android.

3. **Mesmo padrão já fixado para o pacote `content`
   ([`5 - projeto-detalhado.md`](<../../../docs/docs-VMODEL-visao-geral/5 - projeto-detalhado.md>),
   PD-IMP-03) — caminho de arquivo como parâmetro, sem interface de
   abstração** — aplicado agora à escrita, não só à leitura.

API pública, esboço (o formato exato do arquivo — JSON ou outro — fica
para a implementação, mesma postura já usada em
[decisions/0008](0008-representacao-do-estado-da-sessao.md) para o
registro interno da sessão):

```
core/session/
  SessionStatePersistence.kt   saveSessionState(state: SessionState, file: File)
                                loadSessionState(file: File): SessionState?
```

**Consequências:** `core` continua sem nenhuma dependência de classe
do Android, testável por teste de unidade comum da JVM (arquivo
temporário, sem emulador) — mesma consistência já mantida em `search`,
`hierarchy` e no restante de `session`. `app` ganha, de graça, as
garantias oficiais do Android (isolamento, criptografia a partir da
API 29, remoção ao desinstalar) sem que `core` precise saber que elas
existem. Se um dia aparecer necessidade real de trocar o mecanismo de
guarda (banco de dados, por exemplo), extrair uma interface por cima
de uma função já existente é um refactor barato e seguro — essa
decisão não impede isso, só não o antecipa sem necessidade concreta.

Com esta ADR, os quatro pontos de desenho originalmente levantados
para o pacote `session` ficam resolvidos:
[decisions/0008](0008-representacao-do-estado-da-sessao.md)
(representação do estado), o mesmo documento (fronteira com
`report`, resolvida como consequência), 
[decisions/0009](0009-calculo-do-recorte-continuo-de-sessao.md)
(cálculo do recorte contíguo) e esta (onde o estado persiste em
disco).

## Referências

Fontes externas citadas no Contexto, no formato definido pela norma
ABNT NBR 6023 (Informação e documentação — Referências). Citadas no
corpo do documento como (ENTIDADE, ano).

GOOGLE. **Access app-specific files**. Android Developers, [s.d.]a.
Disponível em:
https://developer.android.com/training/data-storage/app-specific.
Acesso em: 14 ago. 2026.

GOOGLE. **Domain layer**. App architecture — Android Developers,
[s.d.]b. Disponível em:
https://developer.android.com/topic/architecture/domain-layer.
Acesso em: 14 ago. 2026.
