package org.nexo.motor.app

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.os.Looper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.nexo.motor.app.connectivity.PieceReadListener
import org.nexo.motor.app.connectivity.RadioStateListener
import org.nexo.motor.core.connectivity.Radio
import org.nexo.motor.core.connectivity.tagIdFromBytes
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.util.ReflectionHelpers
import org.robolectric.util.ReflectionHelpers.ClassParameter

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private fun mockTagWithId(rawId: ByteArray): Tag {
        return if (RuntimeEnvironment.getApiLevel() <= TIRAMISU) {
            ReflectionHelpers.callStaticMethod(
                Tag::class.java,
                "createMockTag",
                ClassParameter.from(ByteArray::class.java, rawId),
                ClassParameter.from(IntArray::class.java, IntArray(0)),
                ClassParameter.from(Array<Bundle>::class.java, arrayOf<Bundle>()),
            )
        } else {
            ReflectionHelpers.callStaticMethod(
                Tag::class.java,
                "createMockTag",
                ClassParameter.from(ByteArray::class.java, rawId),
                ClassParameter.from(IntArray::class.java, IntArray(0)),
                ClassParameter.from(Array<Bundle>::class.java, arrayOf<Bundle>()),
                ClassParameter.from(Long::class.javaPrimitiveType, 0L),
            )
        }
    }

    @Test
    fun `EI-VAL-02 - leitura NFC decodifica o identificador bruto e repassa pro PieceReadListener`() {
        val application = RuntimeEnvironment.getApplication()
        shadowOf(application.packageManager).setSystemFeature(PackageManager.FEATURE_NFC, true)

        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().resume().get()

        var receivedTagId: String? = null
        activity.pieceReadListener = PieceReadListener { tagId -> receivedTagId = tagId }

        val rawId = byteArrayOf(0x04.toByte(), 0xA2.toByte(), 0x19.toByte(), 0x3B.toByte())
        val tag = mockTagWithId(rawId)

        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        shadowOf(nfcAdapter).dispatchTagDiscovered(tag)

        assertEquals(tagIdFromBytes(rawId), receivedTagId)
    }

    @Test
    fun `NFC ligado -- onResume avisa o RadioStateListener com enabled=true -- decisions0044`() {
        val application = RuntimeEnvironment.getApplication()
        shadowOf(application.packageManager).setSystemFeature(PackageManager.FEATURE_NFC, true)

        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().get()
        shadowOf(NfcAdapter.getDefaultAdapter(activity)).setEnabled(true)
        var lastRadio: Radio? = null
        var lastEnabled: Boolean? = null
        activity.radioStateListener = RadioStateListener { radio, enabled ->
            lastRadio = radio
            lastEnabled = enabled
        }

        controller.resume()

        assertEquals(Radio.NFC, lastRadio)
        assertTrue(lastEnabled!!)
    }

    @Test
    fun `NFC desligado -- onResume avisa o RadioStateListener com enabled=false -- decisions0044`() {
        val application = RuntimeEnvironment.getApplication()
        shadowOf(application.packageManager).setSystemFeature(PackageManager.FEATURE_NFC, true)

        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().get()
        shadowOf(NfcAdapter.getDefaultAdapter(activity)).setEnabled(false)
        var lastEnabled: Boolean? = null
        activity.radioStateListener = RadioStateListener { _, enabled -> lastEnabled = enabled }

        controller.resume()

        assertFalse(lastEnabled!!)
    }

    @Test
    fun `NFC desligado em tempo real, com a tela em primeiro plano, chega pelo broadcast do sistema -- decisions0044`() {
        val application = RuntimeEnvironment.getApplication()
        shadowOf(application.packageManager).setSystemFeature(PackageManager.FEATURE_NFC, true)

        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().get()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        shadowOf(nfcAdapter).setEnabled(true)
        var lastEnabled: Boolean? = null
        activity.radioStateListener = RadioStateListener { _, enabled -> lastEnabled = enabled }
        controller.resume()
        assertTrue(lastEnabled!!)

        shadowOf(nfcAdapter).setEnabled(false)
        activity.sendBroadcast(Intent(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))
        shadowOf(Looper.getMainLooper()).idle()

        assertFalse(lastEnabled!!)
    }

    @Test
    fun `onPause desregistra o receptor -- broadcast depois nao chega mais no RadioStateListener -- decisions0044`() {
        val application = RuntimeEnvironment.getApplication()
        shadowOf(application.packageManager).setSystemFeature(PackageManager.FEATURE_NFC, true)

        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().get()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        shadowOf(nfcAdapter).setEnabled(true)
        var callCount = 0
        activity.radioStateListener = RadioStateListener { _, _ -> callCount++ }
        controller.resume()
        val callsAfterResume = callCount
        controller.pause()

        shadowOf(nfcAdapter).setEnabled(false)
        activity.sendBroadcast(Intent(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(callsAfterResume, callCount)
    }
}
