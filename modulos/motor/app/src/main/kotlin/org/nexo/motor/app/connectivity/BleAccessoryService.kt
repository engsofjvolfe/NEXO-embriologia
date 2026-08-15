package org.nexo.motor.app.connectivity

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.ParcelUuid
import java.util.UUID
import org.nexo.motor.core.connectivity.ConnectionState
import org.nexo.motor.core.connectivity.NordicUartService
import org.nexo.motor.core.connectivity.tagIdFromBytes

private val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID =
    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

class BleAccessoryService : Service() {

    private val binder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var pieceReadListener: PieceReadListener? = null
    private var connectionStateListener: ConnectionStateListener? = null
    private var connectionState: ConnectionState = ConnectionState.DISCONNECTED

    inner class LocalBinder : Binder() {
        fun getService(): BleAccessoryService = this@BleAccessoryService
    }

    override fun onBind(intent: Intent): IBinder = binder

    fun setPieceReadListener(listener: PieceReadListener?) {
        pieceReadListener = listener
    }

    fun setConnectionStateListener(listener: ConnectionStateListener?) {
        connectionStateListener = listener
    }

    fun currentConnectionState(): ConnectionState = connectionState

    fun startScanAndConnect(): Boolean {
        val manager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = manager?.adapter ?: return false
        val scanner = adapter.bluetoothLeScanner ?: return false
        bluetoothAdapter = adapter

        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(NordicUartService.SERVICE_UUID))
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner.startScan(listOf(filter), settings, scanCallback)
        updateConnectionState(ConnectionState.SCANNING)
        return true
    }

    fun disconnect() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        bluetoothGatt?.disconnect()
    }

    override fun onDestroy() {
        disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        updateConnectionState(ConnectionState.DISCONNECTED)
        super.onDestroy()
    }

    private fun updateConnectionState(newState: ConnectionState) {
        connectionState = newState
        connectionStateListener?.onConnectionStateChanged(newState)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
            bluetoothGatt = result.device.connectGatt(this@BleAccessoryService, false, gattCallback)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    updateConnectionState(ConnectionState.CONNECTED)
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    gatt.close()
                    bluetoothGatt = null
                    updateConnectionState(ConnectionState.DISCONNECTED)
                }
            }
        }

        @Suppress("DEPRECATION")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(NordicUartService.SERVICE_UUID) ?: return
            val txCharacteristic =
                service.getCharacteristic(NordicUartService.TX_CHARACTERISTIC_UUID) ?: return
            gatt.setCharacteristicNotification(txCharacteristic, true)

            val descriptor = txCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID) ?: return
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            if (characteristic.uuid != NordicUartService.TX_CHARACTERISTIC_UUID) return
            val value = characteristic.value ?: return
            pieceReadListener?.onPieceRead(tagIdFromBytes(value))
        }
    }
}
