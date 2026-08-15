package org.nexo.motor.core.connectivity

fun tagIdFromBytes(bytes: ByteArray): String =
    bytes.joinToString(separator = "") { byte -> "%02X".format(byte) }
