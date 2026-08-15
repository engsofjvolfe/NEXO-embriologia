package org.nexo.motor.app.connectivity

import org.nexo.motor.core.connectivity.ConnectionState

fun interface ConnectionStateListener {
    fun onConnectionStateChanged(state: ConnectionState)
}
