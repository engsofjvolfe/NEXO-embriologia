package org.nexo.motor.core.session

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val json = Json { prettyPrint = true }

fun saveSessionState(state: SessionState, file: File) {
    file.writeText(json.encodeToString(state))
}

fun loadSessionState(file: File): SessionState? {
    if (!file.exists()) return null
    return json.decodeFromString<SessionState>(file.readText())
}

fun deleteSessionState(file: File) {
    file.delete()
}
