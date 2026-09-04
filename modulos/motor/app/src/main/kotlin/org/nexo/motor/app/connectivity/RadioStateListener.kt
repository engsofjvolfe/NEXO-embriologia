package org.nexo.motor.app.connectivity

import org.nexo.motor.core.connectivity.Radio

fun interface RadioStateListener {
    fun onRadioStateChanged(radio: Radio, enabled: Boolean)
}
