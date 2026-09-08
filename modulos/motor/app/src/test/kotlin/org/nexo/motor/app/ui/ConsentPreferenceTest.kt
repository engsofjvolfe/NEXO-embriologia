package org.nexo.motor.app.ui

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
class ConsentPreferenceTest {

    // O arquivo do DataStore sobrevive de um método de teste pro outro dentro da mesma sandbox do
    // Robolectric -- mesma armadilha que pausedSessionFile() já evita em MotorAppTest.kt, aqui
    // resolvida limpando antes de cada teste, não com arquivo próprio por teste.
    @Before
    fun limparEscolhaLembrada() = runBlocking {
        saveConsentChoice(RuntimeEnvironment.getApplication(), null)
    }

    // decisions/0045, item 5: nada é lembrado até a pessoa marcar "lembrar" pela primeira vez.
    @Test
    fun `sem escolha salva ainda, consentChoiceFlow comeca nulo`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()

        assertNull(consentChoiceFlow(context).first())
    }

    @Test
    fun `salvar uma escolha lembrada e depois le-la de volta igual`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()

        saveConsentChoice(context, RememberedConsent(given = true))

        assertEquals(RememberedConsent(given = true), consentChoiceFlow(context).first())
    }

    // decisions/0045, item 5 e LGPD art. 8º §5º: revogar (tocar no lembrete) apaga a escolha
    // salva, mesmo caminho de "nunca ter marcado lembrar".
    @Test
    fun `salvar null apaga a escolha lembrada`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        saveConsentChoice(context, RememberedConsent(given = true))

        saveConsentChoice(context, null)

        assertNull(consentChoiceFlow(context).first())
    }
}
