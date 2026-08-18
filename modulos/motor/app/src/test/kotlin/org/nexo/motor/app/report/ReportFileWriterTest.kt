package org.nexo.motor.app.report

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ReportFileWriterTest {

    @Test
    fun `DA-ARM-01 - writeReportCsv escreve o conteudo no armazenamento local, caminho novo`() {
        val application = RuntimeEnvironment.getApplication()
        val resolver = application.contentResolver

        // ShadowContentResolver.insert() sem provedor registrado devolve um
        // endereço proprio, previsivel: a URI base + "/" + contador (1 na
        // primeira chamada de um teste novo).
        val expectedUri = Uri.parse(MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString() + "/1")
        val outputStream = ByteArrayOutputStream()
        shadowOf(resolver).registerOutputStream(expectedUri, outputStream)

        var receivedUri: Uri? = null
        val csvContent = "posicao,evento,momento\n1,Evento A,1000\n"
        writeReportCsv(application, "relatorio.csv", csvContent) { uri -> receivedUri = uri }

        assertEquals(expectedUri, receivedUri)
        assertEquals(csvContent, outputStream.toString(Charsets.UTF_8.name()))
    }

    @Test
    fun `DA-ARM-02 - buildReportShareIntent so monta e devolve o Intent, nunca dispara nada`() {
        val csvUri = Uri.parse("content://media/external/downloads/1")
        val pdfUri = Uri.parse("content://media/external/downloads/2")

        val intent = buildReportShareIntent(csvUri, pdfUri)

        assertEquals(Intent.ACTION_SEND_MULTIPLE, intent.action)
        assertEquals("*/*", intent.type)
        val extraUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
        assertTrue(extraUris != null && extraUris.containsAll(listOf(csvUri, pdfUri)))
        // A função só monta o Intent — nunca chama startActivity nem qualquer
        // outro disparo; não existe estado global pra verificar isso, a
        // garantia vem da própria assinatura da função (devolve Intent, não Unit).
        assertNull(shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity)
    }
}
