package org.nexo.motor.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.nexo.motor.app.connectivity.PieceReadListener
import org.nexo.motor.app.connectivity.RadioStateListener
import org.nexo.motor.app.ui.MotorApp
import org.nexo.motor.app.ui.theme.NexoMotorTheme
import org.nexo.motor.core.connectivity.Radio
import org.nexo.motor.core.connectivity.tagIdFromBytes

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    var pieceReadListener: PieceReadListener? = null
    var radioStateListener: RadioStateListener? = null

    private val nfcStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) return
            notifyNfcState()
        }
    }

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
        notifyNfcState()
        val filter = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // RECEIVER_EXPORTED é exigido a partir do Android 13 (API 33) pra receber
            // broadcasts de apps de sistema altamente privilegiados -- decisions/0044.
            registerReceiver(nfcStateReceiver, filter, RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(nfcStateReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
        unregisterReceiver(nfcStateReceiver)
    }

    override fun onTagDiscovered(tag: Tag) {
        pieceReadListener?.onPieceRead(tagIdFromBytes(tag.id))
    }

    private fun notifyNfcState() {
        nfcAdapter?.let { adapter -> radioStateListener?.onRadioStateChanged(Radio.NFC, adapter.isEnabled) }
    }
}
