package org.nexo.motor.app.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.nexo.motor.core.connectivity.ConnectionState
import org.nexo.motor.core.content.ContentEvent
import org.nexo.motor.core.content.ContentInstance
import org.nexo.motor.core.content.ContentTheme
import org.nexo.motor.core.content.Frame
import org.nexo.motor.core.hierarchy.Ordering
import org.nexo.motor.core.report.EventConfiguration
import org.nexo.motor.core.report.SessionConfiguration
import org.nexo.motor.core.session.SessionState
import org.nexo.motor.core.session.loadSessionState
import java.io.File

/**
 * Prova a ligação entre leitura de peça, `session`, `content` e a tela (decisions/0020, 0022, 0023,
 * 0024, 0026) -- nunca as regras que já são testadas dentro de `core/session` em si. Forma exata de
 * `SessionState`, dos tipos de `content` e do construtor de `SessionViewModel`: decisions/0026 e
 * decisions/0027. Ferramenta: kotlin-test-junit, sem Robolectric (decisions/0025, ponto 3) -- nenhum
 * tipo usado aqui depende de classe do Android.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUpMainDispatcher() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDownMainDispatcher() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    // reserva um caminho único e apaga o arquivo em seguida -- loadSessionState(file) só devolve
    // null quando o arquivo não existe (nenhuma sessão salva ainda); um arquivo vazio, mas
    // existente, não é a mesma coisa, e não representa nenhum cenário real (o caminho decidido por
    // `app`, context.filesDir, nunca chega a existir antes da primeira gravação)
    private fun tempStateFile(): File =
        File.createTempFile("session-state-test", ".json").apply {
            delete()
            deleteOnExit()
        }

    private fun sampleContent(): ContentInstance {
        val evento1 = ContentEvent(
            name = "Evento 1",
            ordering = Ordering.Ordered(1),
            zeroMarkImage = "zero-1.png",
            hintEnabled = false,
            hintContent = null,
            frames = listOf(
                Frame(tagId = "AA", image = "frame-1a.png", confirmationText = "confirma 1a", summaryFragment = "fragmento 1a"),
                Frame(tagId = "BB", image = "frame-1b.png", confirmationText = "confirma 1b", summaryFragment = "fragmento 1b"),
            ),
        )
        val evento2 = ContentEvent(
            name = "Evento 2",
            ordering = Ordering.Ordered(2),
            zeroMarkImage = "zero-2.png",
            hintEnabled = false,
            hintContent = null,
            frames = listOf(
                Frame(tagId = "CC", image = "frame-2a.png", confirmationText = "confirma 2a", summaryFragment = "fragmento 2a"),
            ),
        )
        val tema = ContentTheme(name = "Tema", ordering = Ordering.Ordered(1), events = listOf(evento1, evento2))
        return ContentInstance(name = "Instância de teste", retentionPeriod = "P1Y", themes = listOf(tema))
    }

    private fun sampleConfiguration(idleThresholdMillis: Long = 60_000L): SessionConfiguration =
        SessionConfiguration(
            eventNames = listOf("Evento 1", "Evento 2"),
            startingPosition = 1,
            idleThresholdMillis = idleThresholdMillis,
            events = listOf(
                EventConfiguration(eventName = "Evento 1", skipEnabled = true, hintThreshold = 3, studyThreshold = 5),
                EventConfiguration(eventName = "Evento 2", skipEnabled = true, hintThreshold = 3, studyThreshold = 5),
            ),
        )

    private fun sampleInitialState(): SessionState =
        SessionState(
            expectedEventName = "Evento 1",
            expectedPosition = 1,
            paused = false,
            log = emptyList(),
        )

    private fun newViewModel(
        content: ContentInstance = sampleContent(),
        configuration: SessionConfiguration = sampleConfiguration(),
        stateFile: File = tempStateFile(),
    ): SessionViewModel = SessionViewModel(
        instance = content,
        configuration = configuration,
        initialState = sampleInitialState(),
        pausedStateFile = stateFile,
    )

    @Test
    fun `estado inicial mostra a referencia antes da primeira tentativa -- EI-SES-04`() {
        val viewModel = newViewModel()

        val screen = viewModel.uiState.value.screen
        assertIs<SessionScreen.Reference>(screen)
        assertEquals("zero-1.png", screen.referenceImage)
        assertFalse(viewModel.uiState.value.exitConfirmationRequested)
    }

    @Test
    fun `reconhecer a tela de referencia avanca para aguardando tentativa -- EI-RET-01`() {
        val viewModel = newViewModel()

        viewModel.onScreenAcknowledged()

        assertIs<SessionScreen.AwaitingAttempt>(viewModel.uiState.value.screen)
    }

    @Test
    fun `peca correta repassada pelo tag_id esperado vira confirmacao de acerto -- EI-VAL-02`() {
        val viewModel = newViewModel()
        viewModel.onScreenAcknowledged()

        viewModel.onPieceRead("AA")

        val screen = viewModel.uiState.value.screen
        assertIs<SessionScreen.AttemptAccepted>(screen)
        assertEquals("confirma 1a", screen.confirmationText)
    }

    @Test
    fun `peca que nao corresponde a posicao esperada vira negativa -- EI-VAL-01, EI-RET-02`() {
        val viewModel = newViewModel()
        viewModel.onScreenAcknowledged()

        viewModel.onPieceRead("ZZ")

        assertIs<SessionScreen.AttemptRejected>(viewModel.uiState.value.screen)
    }

    @Test
    fun `mudanca de estado de conexao chega ate a tela sem alterar a tela de jogo -- pacote connectivity`() {
        val viewModel = newViewModel()
        viewModel.onScreenAcknowledged()

        viewModel.onConnectionStateChanged(ConnectionState.CONNECTED)

        val screen = viewModel.uiState.value.screen
        assertIs<SessionScreen.AwaitingAttempt>(screen)
        assertEquals(ConnectionState.CONNECTED, screen.connectionState)
    }

    @Test
    fun `pedido de saida sinaliza confirmacao sem trocar a tela em jogo -- EI-PAU-03`() {
        val viewModel = newViewModel()
        viewModel.onScreenAcknowledged()

        viewModel.onExitRequested()

        assertTrue(viewModel.uiState.value.exitConfirmationRequested)
        assertIs<SessionScreen.AwaitingAttempt>(viewModel.uiState.value.screen)
    }

    @Test
    fun `cancelar a saida desliga o sinalizador de confirmacao -- EI-PAU-03`() {
        val viewModel = newViewModel()
        viewModel.onExitRequested()

        viewModel.onExitCancelled()

        assertFalse(viewModel.uiState.value.exitConfirmationRequested)
    }

    @Test
    fun `confirmar a saida escreve o relatorio antes de apagar o estado retomavel -- EI-PAU-04, decisions0023`() {
        val stateFile = tempStateFile()
        val viewModel = newViewModel(stateFile = stateFile)
        var writtenCsv: String? = null
        var writtenPdfLines: List<String>? = null

        viewModel.onExitConfirmed(writeReport = { csv, pdfLines ->
            writtenCsv = csv
            writtenPdfLines = pdfLines
        })

        assertTrue(writtenCsv != null, "o relatorio precisa ser escrito antes do estado ser apagado")
        assertTrue(writtenPdfLines != null)
        assertNull(loadSessionState(stateFile), "estado retomavel precisa estar apagado depois da saida confirmada")
    }

    @Test
    fun `ociosidade alem do limiar configurado guarda o estado em disco -- EI-PAU-01, EI-PAU-06, decisions0024`() = runTest(dispatcher) {
        val stateFile = tempStateFile()
        val idleThresholdMillis = 5_000L
        val viewModel = newViewModel(configuration = sampleConfiguration(idleThresholdMillis = idleThresholdMillis), stateFile = stateFile)

        dispatcher.scheduler.advanceTimeBy(idleThresholdMillis + 1)
        dispatcher.scheduler.runCurrent()

        val savedState = loadSessionState(stateFile)
        assertTrue(savedState != null, "sessao ociosa precisa ter o estado gravado em disco")
        assertTrue(savedState!!.paused, "ociosidade tem o mesmo efeito de pausa -- Documento de Conceito, seção 12")
        assertTrue(savedState.log.any { it is org.nexo.motor.core.session.SessionEvent.WentIdle }, "o gatilho precisa ficar registrado no log, distinto de uma pausa explícita")
    }

    @Test
    fun `tentativa nova reinicia o relogio de ociosidade -- EI-PAU-06`() = runTest(dispatcher) {
        val stateFile = tempStateFile()
        val idleThresholdMillis = 5_000L
        val viewModel = newViewModel(configuration = sampleConfiguration(idleThresholdMillis = idleThresholdMillis), stateFile = stateFile)
        viewModel.onScreenAcknowledged()

        dispatcher.scheduler.advanceTimeBy(idleThresholdMillis - 1)
        viewModel.onPieceRead("AA")
        dispatcher.scheduler.advanceTimeBy(idleThresholdMillis - 1)
        dispatcher.scheduler.runCurrent()

        assertNull(loadSessionState(stateFile), "uma tentativa recente nao pode deixar a sessao cair em ociosidade")
    }
}
