package org.nexo.motor.core.report

import org.nexo.motor.core.session.SessionEvent

data class EventConfiguration(
    val eventName: String,
    val skipEnabled: Boolean,
    val hintThreshold: Int?,
    val studyThreshold: Int?,
)

data class SessionConfiguration(
    val eventNames: List<String>,
    val startingPosition: Int,
    val idleThresholdMillis: Long,
    val events: List<EventConfiguration>,
)

fun sessionEventTypeName(event: SessionEvent): String = when (event) {
    is SessionEvent.AttemptAccepted -> "tentativa aceita"
    is SessionEvent.AttemptRejected -> "tentativa rejeitada"
    is SessionEvent.HintUsed -> "dica usada"
    is SessionEvent.StudySuggestionShown -> "sugestão de estudo exibida"
    is SessionEvent.PositionSkipped -> "posição pulada"
    is SessionEvent.Paused -> "pausa"
    is SessionEvent.WentIdle -> "ociosidade"
}

private fun csvField(value: String): String =
    if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }

private fun csvRow(vararg fields: String): String = fields.joinToString(",") { csvField(it) }

fun buildReportCsv(configuration: SessionConfiguration, log: List<SessionEvent>): String {
    val lines = mutableListOf<String>()
    lines += csvRow("campo", "valor")
    lines += csvRow("eventos", configuration.eventNames.joinToString(" > "))
    lines += csvRow("ponto de início", configuration.startingPosition.toString())
    lines += csvRow("tempo de ociosidade (ms)", configuration.idleThresholdMillis.toString())
    configuration.events.forEach { event ->
        lines += csvRow("pular habilitado — ${event.eventName}", event.skipEnabled.toString())
        lines += csvRow("limiar de dica — ${event.eventName}", event.hintThreshold?.toString() ?: "")
        lines += csvRow("limiar de sugestão de estudo — ${event.eventName}", event.studyThreshold?.toString() ?: "")
    }
    lines += ""
    lines += csvRow("tipo", "evento", "posição", "horário (ms desde a época)")
    log.forEach { event ->
        lines += csvRow(sessionEventTypeName(event), event.eventName, event.position.toString(), event.timestamp.toString())
    }
    return lines.joinToString("\r\n")
}

fun buildReportPdfLines(configuration: SessionConfiguration, log: List<SessionEvent>): List<String> {
    val lines = mutableListOf<String>()
    lines += "Relatório de sessão"
    lines += "Eventos: ${configuration.eventNames.joinToString(" > ")}"
    lines += "Ponto de início: posição ${configuration.startingPosition}"
    lines += "Tempo de ociosidade: ${configuration.idleThresholdMillis} ms"
    configuration.events.forEach { event ->
        val pular = if (event.skipEnabled) "sim" else "não"
        lines += "Evento ${event.eventName} — pular: $pular, " +
            "limiar de dica: ${event.hintThreshold ?: "-"}, " +
            "limiar de sugestão de estudo: ${event.studyThreshold ?: "-"}"
    }
    lines += ""
    lines += "Histórico:"
    log.forEach { event ->
        lines += "${sessionEventTypeName(event)} — ${event.eventName}, posição ${event.position}, horário ${event.timestamp}"
    }
    return lines
}
