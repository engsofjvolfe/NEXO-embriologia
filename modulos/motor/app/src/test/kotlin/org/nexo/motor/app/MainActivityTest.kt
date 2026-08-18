package org.nexo.motor.app

import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.nexo.motor.app.connectivity.PieceReadListener
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
}
