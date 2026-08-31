"use strict";

/* ---------- referências de DOM, buscadas uma única vez ---------- */
const elTela = document.getElementById("tela");
const elMoldura = document.getElementById("moldura");
const elSimulador = document.getElementById("controlesSimulador");
const elEstadoAtual = document.getElementById("estadoAtual");
const chkTablet = document.getElementById("chkTablet");
const chkEscuro = document.getElementById("chkEscuro");
const chkPausada = document.getElementById("chkPausada");

/* ---------- dado do protótipo ----------
   Nomes de campo em português e simplificados de propósito: isto não é um pacote de conteúdo real
   (contrato em schemas/pacote-de-conteudo.schema.json, campos em inglês — name/themes/events/frames,
   com zero_mark/hint_enabled/ordering/tag_id obrigatórios) — é só o texto de exemplo que aparece nas
   telas do protótipo (nome do tema, do evento, quantidade de posições), sem passar pela validação de
   importação nem precisar dos demais campos que um pacote de verdade exige. */
const instanciaExemplo = {
  nome: "Instância de exemplo",
  temas: [
    { nome: "Tema A", eventos: [
      { nome: "Evento 1", frames: 4 },
      { nome: "Evento 2", frames: 3 }
    ]},
    { nome: "Tema B", eventos: [
      { nome: "Evento 1", frames: 5 }
    ]}
  ]
};

/* estados de SessionScreen (decisions/0022) que voltam pra AwaitingAttempt com toque livre
   (decisions/0032) — nenhuma das três envolve decisão real, mesmo tratamento pras três */
const ESTADOS_COM_TOQUE_LIVRE_PARA_AGUARDANDO = ["AttemptRejected", "HintShown", "StudySuggestionShown"];

/* valores de exemplo deste protótipo pros três parâmetros de configuração da sessão (EI-NAV-05) —
   nomeados aqui (em vez de literais espalhados) só pra não haver risco de o texto exibido e a lógica
   de simulação ficarem fora de sincronia; nenhum dos três é um valor-padrão do motor em si — RF-CFG-01/
   RF-DIC-04/RF-PAU-06 proíbem o motor de impor qualquer padrão, cada sessão real escolhe o próprio */
const LIMIAR_ERRO_DICA_EXEMPLO = 3;
const LIMIAR_ERRO_SUGESTAO_ESTUDO_EXEMPLO = 6;
const TEMPO_OCIOSIDADE_EXEMPLO_SEGUNDOS = 60;

let estado = {
  tela: "carregando",      // paused | nav | config | session | resultado | importar | consentimento
  temaAberto: null,
  temaIndex: null,         // posição, dentro de instanciaExemplo.temas, da sessão em curso
  eventoIndex: null,       // posição, dentro do tema acima, do evento em curso — permite saber se há próximo
  eventoAlvoConfig: null,
  eventoTabletSelecionado: 0,
  sessionSubEstado: "Reference", // 8 estados de SessionScreen (decisions/0022)
  posicaoAtual: 1,
  totalPosicoes: 4,
  errosSeguidos: 0,
  saiuPedido: false,
  conexao: "conectado", // conectado | procurando | desconectado | null (sem acessório)
  limiarDica: LIMIAR_ERRO_DICA_EXEMPLO,
  limiarSugestaoEstudo: LIMIAR_ERRO_SUGESTAO_ESTUDO_EXEMPLO,
  tempoOciosidadeSegundos: TEMPO_OCIOSIDADE_EXEMPLO_SEGUNDOS
};

function aoAbrirApp(){
  estado.tela = chkPausada.checked ? "paused" : "nav";
  renderizar();
}

function irPara(tela, extra){
  estado.tela = tela;
  Object.assign(estado, extra || {});
  renderizar();
}

/* ---------- fragmentos reaproveitados entre telas (DRY) ---------- */

// EI-NAV-05: os três campos de configuração de um evento são os mesmos, esteja a tela em leiaute
// de celular ou de tablet (decisions/0033) — os três são campo editável de verdade (não texto fixo),
// já que EI-CFG-01/RF-DIC-04/RF-PAU-06 tratam os três como igualmente escolhidos por quem inicia a
// sessão, nunca impostos pelo motor; mesmo nível de interatividade que "Pular disponível" já tinha.
function blocoConfigEvento(nomeEvento){
  return `
    <strong>${nomeEvento}</strong>
    <div class="linha-campo"><span>Pular disponível</span><input type="checkbox" checked></div>
    <div class="linha-campo"><span>Limiar de erro — dica</span>
      <input type="number" min="1" value="${estado.limiarDica}"
             onchange="estado.limiarDica=Number(this.value)||${LIMIAR_ERRO_DICA_EXEMPLO}"></div>
    <div class="linha-campo"><span>Limiar de erro — sugestão de estudo</span>
      <input type="number" min="1" value="${estado.limiarSugestaoEstudo}"
             onchange="estado.limiarSugestaoEstudo=Number(this.value)||${LIMIAR_ERRO_SUGESTAO_ESTUDO_EXEMPLO}"></div>`;
}

// Negativa, dica e sugestão de estudo não têm decisão real (RF-DIC-01, RF-DIC-03) —
// toque em qualquer lugar da tela volta pra "Aguardando tentativa" (decisions/0032, item 1/2).
function corpoComToqueLivreParaAguardando(rotulo, texto){
  return `
    <div class="toque-livre" onclick="irPara('session',{sessionSubEstado:'AwaitingAttempt'})"></div>
    <div class="conteudo-central">
      <div class="rotulo-tela">${rotulo}</div>
      <p class="texto-principal">${texto}</p>
    </div>`;
}

// EI-ENC-01/EI-ENC-02: existindo próximo evento na cadeia, o botão continua pra ele (direto pra
// "Aguardando tentativa", sem tela de referência própria); sem próximo evento, vai pro resultado.
function eventoAtualTemProximo(){
  if (estado.temaIndex === null || estado.eventoIndex === null) return false;
  return estado.eventoIndex < instanciaExemplo.temas[estado.temaIndex].eventos.length - 1;
}

function rodapeContinuarOuVerResultado(){
  return eventoAtualTemProximo()
    ? `<div class="rodape-app"><div></div><button class="acao primario" onclick="continuarProximoEvento()">Continuar</button></div>`
    : `<div class="rodape-app"><div></div><button class="acao primario" onclick="irPara('resultado')">Ver resultado</button></div>`;
}

function continuarProximoEvento(){
  estado.eventoIndex++;
  const proximoEvento = instanciaExemplo.temas[estado.temaIndex].eventos[estado.eventoIndex];
  // EI-ENC-02: "a próxima peça já é aguardada, sem nenhuma imagem de transição" — vai direto pra
  // AwaitingAttempt, nunca por uma tela de Referência própria do novo evento.
  irPara("session", {
    sessionSubEstado: "AwaitingAttempt",
    posicaoAtual: 1,
    totalPosicoes: proximoEvento.frames,
    errosSeguidos: 0,
    eventoAlvoConfig: proximoEvento.nome
  });
}

/* ---------- render principal ---------- */
function renderizar(){
  elTela.dataset.theme = chkEscuro.checked ? "escuro" : "claro";
  elMoldura.className = "moldura " + (chkTablet.checked && estado.tela === "config" ? "tablet" : "celular");

  let html = "";
  let simulador = "";
  let estadoTexto = "";

  switch (estado.tela){
    case "paused":
      ({ html, simulador, estadoTexto } = telaSessaoPausada());
      break;
    case "nav":
      ({ html, simulador, estadoTexto } = telaNavegacao());
      break;
    case "config":
      ({ html, simulador, estadoTexto } = telaConfiguracaoDaSessao());
      break;
    case "session":
      html = renderizarSessionScreen();
      simulador = simuladorDeSessao();
      estadoTexto = "SessionScreen — " + estado.sessionSubEstado;
      break;
    case "resultado":
      ({ html, simulador, estadoTexto } = telaResultado());
      break;
    case "importar":
      ({ html, simulador, estadoTexto } = telaImportarConteudo());
      break;
    case "consentimento":
      ({ html, simulador, estadoTexto } = telaConsentimento());
      break;
  }

  elTela.innerHTML = html;
  elSimulador.innerHTML = simulador;
  elEstadoAtual.textContent = estadoTexto;
}

/* ---------- DA-RET-01 — Sessão pausada ---------- */
function telaSessaoPausada(){
  const html = `
    <div class="conteudo-central">
      <div class="rotulo-tela">Sessão pausada</div>
      <p class="texto-principal">Você parou em "Evento 1".</p>
      <button class="acao primario" onclick="irPara('session', {sessionSubEstado:'AwaitingAttempt'})">Retomar</button>
      <button class="acao secundario" onclick="irPara('nav')">Sair da sessão</button>
    </div>`;
  const simulador = `<p>Única tela quando existe sessão pausada (RF-PAU-05) — nenhuma outra ação disponível.</p>`;
  return { html, simulador, estadoTexto: "DA-RET-01 — Sessão pausada" };
}

/* ---------- DA-RET-02 — Navegação (acordeão, decisions/0030) ---------- */
function telaNavegacao(){
  const linhasTemas = instanciaExemplo.temas.map((tema, indiceTema) => {
    const linhaTema = `
      <div class="item-nivel item-tema" onclick="estado.temaAberto=(estado.temaAberto===${indiceTema}?null:${indiceTema});renderizar()">
        ${tema.nome} ${estado.temaAberto === indiceTema ? "▾" : "▸"}
      </div>`;
    const linhasEventos = estado.temaAberto === indiceTema
      ? tema.eventos.map((evento, indiceEvento) => `
          <div class="item-nivel item-evento" onclick="abrirEvento(${indiceTema},${indiceEvento})">${evento.nome}</div>
        `).join("")
      : "";
    return linhaTema + linhasEventos;
  }).join("");

  const html = `
    <div class="topo-app"><div></div><div></div></div>
    <div class="campo-busca">Buscar instância, tema ou evento…</div>
    <div class="lista-acordeao">
      <div class="item-nivel item-instancia" onclick="estado.temaAberto=null;renderizar()">${instanciaExemplo.nome} ▾</div>
      ${linhasTemas}
      <div class="item-nivel item-tema" style="opacity:.6" onclick="irPara('importar')">Importar conteúdo (DA-RET-16) →</div>
    </div>`;
  const simulador = `<p>Acordeão: tocar expande a lista do nível seguinte embaixo do próprio item
    (<a href="../decisions/0030-padrao-de-navegacao-hierarquica-de-conteudo.md">decisions/0030</a>), sem trocar de tela.</p>`;
  return { html, simulador, estadoTexto: "DA-RET-02 — Navegação (acordeão)" };
}

function abrirEvento(indiceTema, indiceEvento){
  const evento = instanciaExemplo.temas[indiceTema].eventos[indiceEvento];
  estado.temaIndex = indiceTema;
  estado.eventoIndex = indiceEvento;
  estado.eventoAlvoConfig = evento.nome;
  estado.totalPosicoes = evento.frames;
  irPara("config");
}

/* ---------- DA-RET-03/04 — Ponto de início / Configuração da sessão (EI-NAV-05, mesma tela) ---------- */
function telaConfiguracaoDaSessao(){
  const nomeEvento = estado.eventoAlvoConfig || "Evento 1";
  const iniciarSessao = "irPara('session',{sessionSubEstado:'Reference', posicaoAtual:1, errosSeguidos:0})";

  const html = chkTablet.checked
    ? `
      <div class="topo-app"><button class="botao-sair" onclick="irPara('nav')">← Voltar</button></div>
      <div style="padding:8px 16px;">
        <div class="rotulo-secao">Começar em</div>
        <div class="linha-campo"><label><input type="radio" name="inicio" checked> Posição 1 (padrão)</label></div>
        <div class="rotulo-secao">Tempo de ociosidade (sessão inteira)</div>
        <div class="linha-campo"><span>Pausar sozinho após (segundos)</span>
          <input type="number" min="1" value="${estado.tempoOciosidadeSegundos}"
                 onchange="estado.tempoOciosidadeSegundos=Number(this.value)||${TEMPO_OCIOSIDADE_EXEMPLO_SEGUNDOS}"></div>
      </div>
      <div class="duas-colunas-tablet">
        <div class="coluna-lista-eventos">
          ${instanciaExemplo.temas[0].eventos.map((evento, indice) => `
            <div class="item-evento-tablet ${estado.eventoTabletSelecionado === indice ? "selecionado" : ""}"
                 onclick="estado.eventoTabletSelecionado=${indice};renderizar()">${evento.nome}</div>
          `).join("")}
        </div>
        <div class="coluna-formulario-evento">
          ${blocoConfigEvento(instanciaExemplo.temas[0].eventos[estado.eventoTabletSelecionado].nome)}
        </div>
      </div>
      <div class="rodape-app" style="justify-content:flex-end;">
        <button class="acao primario" onclick="${iniciarSessao}">Iniciar sessão</button>
      </div>`
    : `
      <div class="topo-app"><button class="botao-sair" onclick="irPara('nav')">← Voltar</button></div>
      <div class="config-lista-fone">
        <div class="rotulo-secao">Começar em</div>
        <div class="linha-campo"><label><input type="radio" name="inicio" checked> Posição 1 (padrão)</label></div>
        <div class="linha-campo"><label><input type="radio" name="inicio"> Posição 2</label></div>
        <div class="rotulo-secao">Eventos desta sessão</div>
        <div class="bloco-evento-config">${blocoConfigEvento(nomeEvento)}</div>
        <div class="rotulo-secao">Tempo de ociosidade (sessão inteira)</div>
        <div class="linha-campo"><span>Pausar sozinho após (segundos)</span>
          <input type="number" min="1" value="${estado.tempoOciosidadeSegundos}"
                 onchange="estado.tempoOciosidadeSegundos=Number(this.value)||${TEMPO_OCIOSIDADE_EXEMPLO_SEGUNDOS}"></div>
      </div>
      <div class="rodape-fixo-fone">
        <button class="acao primario" style="width:100%" onclick="${iniciarSessao}">Iniciar sessão</button>
      </div>`;

  const simulador = `<p>Marque "Formato tablet" acima pra ver o leiaute de duas colunas —
    única tela com leiaute de tablet dedicado
    (<a href="../decisions/0033-formato-de-aparelho-leiaute-responsivo.md">decisions/0033</a>).</p>`;
  return { html, simulador, estadoTexto: "DA-RET-03/04 — Ponto de início / Configuração da sessão (EI-NAV-05, mesma tela)" };
}

/* ---------- SessionScreen: os oito estados de decisions/0022, gatilho de decisions/0032 ---------- */
function renderizarSessionScreen(){
  const s = estado.sessionSubEstado;

  const topo = `
    <div class="topo-app">
      <button class="botao-sair" onclick="estado.saiuPedido=true;renderizar()">Sair</button>
      <div class="coluna-topo-direita">
        <button class="botao-pausar" title="Pausar" onclick="irPara('paused')">Pausar</button>
        ${s === "AwaitingAttempt" ? `<span class="indicador-conexao">${textoConexao()}</span>` : ""}
      </div>
    </div>`;

  const dialogoSaida = estado.saiuPedido ? `
    <div class="dialogo-overlay">
      <div class="caixa-dialogo">
        <p>O progresso desta sessão será perdido.</p>
        <div class="acoes">
          <button class="acao texto" onclick="estado.saiuPedido=false;renderizar()">Cancelar</button>
          <button class="acao primario" onclick="irPara('resultado')">Sair</button>
        </div>
      </div>
    </div>` : "";

  let corpo;
  if (ESTADOS_COM_TOQUE_LIVRE_PARA_AGUARDANDO.includes(s)){
    const textos = {
      AttemptRejected: ["Negativa", "Essa peça não é a próxima da sequência."],
      HintShown: ["Dica", "Texto de dica (hint_content) cadastrado para este evento."],
      StudySuggestionShown: ["Sugestão de estudo", "Considere revisar este tema antes de continuar."]
    };
    corpo = corpoComToqueLivreParaAguardando(...textos[s]);
    if (s === "StudySuggestionShown"){
      // única exceção com toque livre: o botão "Pular peça" continua nomeado
      // por cima do toque livre (decisions/0032, item 2)
      corpo += `<div class="rodape-app"><button class="acao discreto" style="z-index:1;position:relative" onclick="pularPeca()">Pular peça</button><div></div></div>`;
    }
  } else if (s === "Reference"){
    corpo = `<div class="conteudo-central"><div class="rotulo-tela">Referência</div>
      <div class="imagem-referencia">marco zero / peça anterior</div>
      <p class="texto-principal">Aguardando a próxima peça…</p></div>`;
  } else if (s === "AwaitingAttempt"){
    corpo = `<div class="conteudo-central"><div class="rotulo-tela">Aguardando tentativa</div>
      <div class="imagem-referencia">— sem imagem, só espera —</div>
      <p class="texto-principal">Posição ${estado.posicaoAtual} de ${estado.totalPosicoes}</p></div>
      <div class="rodape-app"><button class="acao discreto" onclick="pularPeca()">Pular peça</button><div></div></div>`;
  } else if (s === "AttemptAccepted"){
    corpo = `<div class="toque-livre" onclick="avancarAposAcerto()"></div>
      <div class="conteudo-central"><div class="rotulo-tela">Confirmação de acerto</div>
      <p class="texto-principal">Texto de confirmação cadastrado para este fotograma (confirmation_text).</p></div>`;
  } else if (s === "EventSummary"){
    corpo = `<div class="conteudo-central"><div class="rotulo-tela">Resumo do evento</div>
      <p class="texto-principal">Síntese contínua, concatenando os summary_fragment de cada fotograma respondido neste evento.</p></div>`
      + rodapeContinuarOuVerResultado();
  } else if (s === "SkipMessageShown"){
    corpo = `<div class="conteudo-central"><div class="rotulo-tela">Mensagem de pulo</div>
      <p class="texto-principal">Você respondeu as posições 1 e 2. As posições 3 e 4 ficaram sem resposta — considere estudar esse trecho.</p></div>`
      + rodapeContinuarOuVerResultado();
  }

  return topo + corpo + dialogoSaida;
}

function textoConexao(){
  const rotulos = { conectado: "● conectado", procurando: "◐ procurando", desconectado: "○ desconectado" };
  return rotulos[estado.conexao] || "";
}

function avancarAposAcerto(){
  if (estado.posicaoAtual >= estado.totalPosicoes){
    irPara("session", { sessionSubEstado: "EventSummary" });
  } else {
    estado.posicaoAtual++;
    irPara("session", { sessionSubEstado: "AwaitingAttempt", errosSeguidos: 0 });
  }
}

function pularPeca(){
  // EI-PUL-04: pular funciona a qualquer momento, sem limite — a posição
  // fica perdida (RF-PUL-05), então o protótipo sempre encerra o evento
  // com a mensagem de pulo, nunca com o resumo normal (EI-PUL-05).
  irPara("session", { sessionSubEstado: "SkipMessageShown" });
}

/* ---------- painel simulador — representa a peça física, nunca a tela real ---------- */
function simuladorDeSessao(){
  const s = estado.sessionSubEstado;

  if (s === "AwaitingAttempt"){
    return `
      <p>Representam a leitura de uma peça física (NFC/Bluetooth):</p>
      <button onclick="irPara('session',{sessionSubEstado:'AttemptAccepted'})">Simular peça certa <span class="badge-simulado">HW</span></button>
      <button onclick="simularErro()">Simular peça errada <span class="badge-simulado">HW</span></button>
      <hr>
      <p>Conexão do acessório:</p>
      <button onclick="estado.conexao='conectado';renderizar()">conectado</button>
      <button onclick="estado.conexao='procurando';renderizar()">procurando</button>
      <button onclick="estado.conexao='desconectado';renderizar()">desconectado</button>
      <button onclick="estado.conexao=null;renderizar()">sem acessório (NFC direto)</button>`;
  }
  if (ESTADOS_COM_TOQUE_LIVRE_PARA_AGUARDANDO.includes(s)){
    return `<p>Toque em qualquer lugar da tela (fora do botão "Pular peça", quando presente) pra continuar —
      gatilho decidido em <a href="../decisions/0032-gatilho-de-toque-entre-estados-do-sessionscreen.md">decisions/0032</a>.</p>`;
  }
  if (s === "AttemptAccepted"){
    return `<p>Toque em qualquer lugar da tela pra continuar.</p>`;
  }
  return `<p>Fim do evento — use o botão da própria tela.</p>`;
}

function simularErro(){
  estado.errosSeguidos++;
  if (estado.errosSeguidos >= estado.limiarSugestaoEstudo){
    irPara("session", { sessionSubEstado: "StudySuggestionShown" });
  } else if (estado.errosSeguidos >= estado.limiarDica){
    irPara("session", { sessionSubEstado: "HintShown" });
  } else {
    irPara("session", { sessionSubEstado: "AttemptRejected" });
  }
}

/* ---------- DA-RET-14 — Resultado / relatório ---------- */
function telaResultado(){
  const html = `
    <div class="topo-app"><div></div><div></div></div>
    <div class="conteudo-central">
      <div class="rotulo-tela">Resultado da sessão</div>
      <div class="numeros-resultado">
        <div class="numero-resultado"><div class="valor">2</div><div class="rotulo">erros</div></div>
        <div class="numero-resultado"><div class="valor">0</div><div class="rotulo">pulos</div></div>
        <div class="numero-resultado"><div class="valor">1</div><div class="rotulo">pausas</div></div>
      </div>
      <div class="lista-acoes-relatorio">
        <button class="acao secundario">Exportar CSV</button>
        <button class="acao secundario">Exportar PDF</button>
        <button class="acao secundario">Compartilhar</button>
      </div>
    </div>
    <div class="rodape-app"><button class="acao texto" style="width:100%" onclick="irPara('nav')">Voltar à navegação</button></div>`;
  const simulador = `<p>Mostrado ao final de toda sessão (inclusive ao sair) e continua acessível depois, sem login (EI-REG-06).</p>`;
  return { html, simulador, estadoTexto: "DA-RET-14 — Resultado / relatório" };
}

/* ---------- DA-RET-16 — Importar conteúdo ---------- */
function telaImportarConteudo(){
  const html = `
    <div class="topo-app"><button class="botao-sair" onclick="irPara('nav')">← Voltar</button></div>
    <div class="conteudo-central">
      <button class="acao primario">Selecionar arquivo (.zip)</button>
      <p class="texto-principal" style="font-size:13px;color:var(--on-surface-variant)">Nenhum arquivo selecionado ainda.</p>
    </div>`;
  const simulador = `<p>Usa o seletor de arquivo padrão do Android (DA-IMP-04) — fora do controle deste desenho.</p>`;
  return { html, simulador, estadoTexto: "DA-RET-16 — Importar conteúdo" };
}

/* ---------- DA-RET-17 — Consentimento ---------- */
function telaConsentimento(){
  const jaConcordou = document.getElementById("chkConsentimento")
    ? document.getElementById("chkConsentimento").checked
    : false;
  const html = `
    <div class="conteudo-central" style="justify-content:flex-start; padding-top:40px;">
      <p class="texto-principal" style="font-size:13px; text-align:left;">
        [Texto legal do termo de consentimento — fora do escopo da cascata do motor, Projeto Arquitetônico §2.2]
      </p>
      <label class="checkbox-linha"><input type="checkbox" id="chkConsentimento" onchange="renderizar()"> Li e concordo</label>
    </div>
    <div class="rodape-app">
      <button class="acao primario" style="width:100%" ${jaConcordou ? "" : "disabled"} onclick="irPara('nav')">Continuar</button>
    </div>`;
  const simulador = `<p>Só aparece antes de registrar dado que identifique a pessoa (EI-REG-03).</p>`;
  return { html, simulador, estadoTexto: "DA-RET-17 — Consentimento" };
}

/* ---------- inicialização ---------- */
chkTablet.addEventListener("change", renderizar);
chkEscuro.addEventListener("change", renderizar);
chkPausada.addEventListener("change", aoAbrirApp);

aoAbrirApp();
