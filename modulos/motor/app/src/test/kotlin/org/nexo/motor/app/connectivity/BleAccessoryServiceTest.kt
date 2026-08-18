package org.nexo.motor.app.connectivity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import java.nio.ByteBuffer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.nexo.motor.core.connectivity.NordicUartService
import org.nexo.motor.core.connectivity.tagIdFromBytes
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ServiceController
import org.robolectric.util.ReflectionHelpers
import org.robolectric.util.ReflectionHelpers.ClassParameter

@RunWith(RobolectricTestRunner::class)
class BleAccessoryServiceTest {

    /** Bytes de propaganda BLE anunciando o UUID de 128 bits do Nordic UART Service. */
    private fun advertisingDataFor(serviceUuid: java.util.UUID): ByteArray {
        val buffer = ByteBuffer.allocate(16)
        buffer.putLong(serviceUuid.mostSignificantBits)
        buffer.putLong(serviceUuid.leastSignificantBits)
        val uuidBytesBigEndian = buffer.array()
        val uuidBytesLittleEndian = uuidBytesBigEndian.reversed().toByteArray()
        // Estrutura AD: [tamanho][tipo 0x07 = lista completa de UUID de 128 bits][16 bytes do UUID]
        return byteArrayOf(0x11, 0x07) + uuidBytesLittleEndian
    }

    @Test
    fun `EI-VAL-02 - notificacao Bluetooth decodifica o identificador bruto e repassa pro PieceReadListener`() {
        val application = RuntimeEnvironment.getApplication()
        shadowOf(application).grantPermissions(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )

        val controller: ServiceController<BleAccessoryService> =
            org.robolectric.Robolectric.buildService(BleAccessoryService::class.java)
        val service = controller.create().get()

        var receivedTagId: String? = null
        service.setPieceReadListener(PieceReadListener { tagId -> receivedTagId = tagId })

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val scanner = bluetoothAdapter.bluetoothLeScanner
        val device = bluetoothAdapter.getRemoteDevice("00:11:22:33:44:55")
        val scanRecord: ScanRecord = ReflectionHelpers.callStaticMethod(
            ScanRecord::class.java,
            "parseFromBytes",
            ClassParameter.from(ByteArray::class.java, advertisingDataFor(NordicUartService.SERVICE_UUID)),
        )
        val scanResult = ScanResult(device, scanRecord, -50, System.nanoTime())
        shadowOf(scanner).addScanResult(scanResult)

        service.startScanAndConnect()

        val gatt = shadowOf(device).bluetoothGatts.first()
        val gattCallback = shadowOf(gatt).gattCallback

        val txCharacteristic = BluetoothGattCharacteristic(
            NordicUartService.TX_CHARACTERISTIC_UUID,
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            0,
        )
        val rawId = byteArrayOf(0x04.toByte(), 0xA2.toByte(), 0x19.toByte(), 0x3B.toByte())
        @Suppress("DEPRECATION")
        txCharacteristic.value = rawId
        gattCallback.onCharacteristicChanged(gatt, txCharacteristic)

        assertEquals(tagIdFromBytes(rawId), receivedTagId)
    }
}
