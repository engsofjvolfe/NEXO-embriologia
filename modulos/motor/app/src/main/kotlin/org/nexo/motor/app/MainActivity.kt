package org.nexo.motor.app

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.nexo.motor.app.connectivity.PieceReadListener
import org.nexo.motor.app.ui.MotorApp
import org.nexo.motor.app.ui.theme.NexoMotorTheme
import org.nexo.motor.core.connectivity.tagIdFromBytes

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    var pieceReadListener: PieceReadListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        // MotorApp() é um navegador de demonstração entre as 7 telas escritas nesta tarefa --
        // não é a navegação final do motor (ver comentário no topo de app/ui/MotorApp.kt e
        // tasks.md, pendência sobre onde o conteúdo importado fica guardado no aparelho).
        setContent {
            NexoMotorTheme {
                MotorApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag) {
        pieceReadListener?.onPieceRead(tagIdFromBytes(tag.id))
    }
}
