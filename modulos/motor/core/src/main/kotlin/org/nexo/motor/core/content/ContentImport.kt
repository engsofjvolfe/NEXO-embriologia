package org.nexo.motor.core.content

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.nexo.motor.core.hierarchy.Event as HierarchyEvent
import org.nexo.motor.core.hierarchy.Instance as HierarchyInstance
import org.nexo.motor.core.hierarchy.Ordering
import org.nexo.motor.core.hierarchy.Theme as HierarchyTheme
import org.nexo.motor.core.hierarchy.validate as validateHierarchy

private const val SUPPORTED_SCHEMA_VERSION = "2.0.0"
private val RETENTION_PERIOD_PATTERN = Regex("^P(?!\$)(\\d+Y)?(\\d+M)?(\\d+D)?\$")
private val TAG_ID_PATTERN = Regex("^[0-9A-F]+\$")

fun importContentPackage(manifestJson: String): ContentImportResult {
    val root = try {
        val element = Json.parseToJsonElement(manifestJson)
        element as? JsonObject
            ?: return ContentImportResult(null, listOf(ContentViolation.InvalidManifest("documento raiz não é um objeto JSON")))
    } catch (e: SerializationException) {
        return ContentImportResult(null, listOf(ContentViolation.InvalidManifest("JSON inválido: ${e.message}")))
    }

    val schemaVersion = root["schema_version"].asStringOrNull()
    if (schemaVersion != SUPPORTED_SCHEMA_VERSION) {
        return ContentImportResult(
            null,
            listOf(ContentViolation.InvalidManifest("\"schema_version\" ausente ou incompatível (esperado \"$SUPPORTED_SCHEMA_VERSION\")")),
        )
    }

    val instanceObject = root["instance"] as? JsonObject
        ?: return ContentImportResult(null, listOf(ContentViolation.InvalidManifest("campo \"instance\" ausente ou não é um objeto")))

    val name = instanceObject["name"].asStringOrNull()
    val retentionPeriod = instanceObject["retention_period"].asStringOrNull()
    val themesJson = instanceObject["themes"] as? JsonArray

    val instanceErrors = mutableListOf<String>()
    if (name.isNullOrEmpty()) instanceErrors += "\"name\" ausente"
    if (retentionPeriod == null || !RETENTION_PERIOD_PATTERN.matches(retentionPeriod)) {
        instanceErrors += "\"retention_period\" ausente ou não bate com o padrão ISO 8601 de duração"
    }
    if (themesJson == null || themesJson.isEmpty()) instanceErrors += "\"themes\" ausente ou vazio"

    if (instanceErrors.isNotEmpty()) {
        return ContentImportResult(null, listOf(ContentViolation.InvalidManifest(instanceErrors.joinToString("; "))))
    }

    val violations = mutableListOf<ContentViolation>()
    val themes = themesJson!!.mapIndexed { i, themeJson -> parseTheme(themeJson, "instance.themes[$i]", violations) }
        .filterNotNull()

    val instance = ContentInstance(name = name!!, retentionPeriod = retentionPeriod!!, themes = themes)

    violations += duplicateTagIdViolations(instance)
    violations += validateHierarchy(instance.toHierarchyInstance()).map { ContentViolation.Hierarchy(it) }

    return if (violations.isEmpty()) ContentImportResult(instance, emptyList()) else ContentImportResult(null, violations)
}

private fun parseOrdering(obj: JsonObject, errors: MutableList<String>): Ordering? {
    val orderingValue = obj["ordering"].asStringOrNull()
    val position = obj["position"].asIntOrNull()
    return when (orderingValue) {
        "ordered" -> if (position == null) {
            errors += "\"position\" obrigatório quando \"ordering\" é \"ordered\""
            null
        } else {
            Ordering.Ordered(position)
        }

        "standalone" -> if (position != null) {
            errors += "\"position\" não deve existir quando \"ordering\" é \"standalone\""
            null
        } else {
            Ordering.Standalone
        }

        else -> {
            errors += "\"ordering\" ausente ou inválido (esperado \"ordered\" ou \"standalone\")"
            null
        }
    }
}

private fun parseTheme(json: JsonElement, path: String, violations: MutableList<ContentViolation>): ContentTheme? {
    val obj = json as? JsonObject
    if (obj == null) {
        violations += ContentViolation.InvalidTheme(path, "não é um objeto")
        return null
    }

    val errors = mutableListOf<String>()
    val name = obj["name"].asStringOrNull()
    if (name.isNullOrEmpty()) errors += "\"name\" ausente"

    val ordering = parseOrdering(obj, errors)

    val eventsJson = obj["events"] as? JsonArray
    if (eventsJson == null || eventsJson.isEmpty()) errors += "\"events\" ausente ou vazio"

    if (errors.isNotEmpty()) {
        violations += ContentViolation.InvalidTheme(path, errors.joinToString("; "))
        return null
    }

    val events = eventsJson!!.mapIndexed { i, eventJson -> parseEvent(eventJson, "$path.events[$i]", violations) }
        .filterNotNull()

    return ContentTheme(name = name!!, ordering = ordering!!, events = events)
}

private fun parseEvent(json: JsonElement, path: String, violations: MutableList<ContentViolation>): ContentEvent? {
    val obj = json as? JsonObject
    if (obj == null) {
        violations += ContentViolation.InvalidEvent(path, "não é um objeto")
        return null
    }

    val errors = mutableListOf<String>()
    val name = obj["name"].asStringOrNull()
    if (name.isNullOrEmpty()) errors += "\"name\" ausente"

    val ordering = parseOrdering(obj, errors)

    val zeroMarkImage = (obj["zero_mark"] as? JsonObject)?.get("image").asStringOrNull()
    if (zeroMarkImage.isNullOrEmpty()) errors += "\"zero_mark.image\" ausente"

    val hintEnabled = obj["hint_enabled"].asBooleanOrNull()
    if (hintEnabled == null) errors += "\"hint_enabled\" ausente ou não é booleano"

    val hintContent = obj["hint_content"].asStringOrNull()
    if (hintEnabled == true && hintContent.isNullOrEmpty()) errors += "\"hint_content\" obrigatório quando \"hint_enabled\" é true"
    if (hintEnabled == false && hintContent != null) errors += "\"hint_content\" não deve existir quando \"hint_enabled\" é false"

    val framesJson = obj["frames"] as? JsonArray
    if (framesJson == null || framesJson.isEmpty()) errors += "\"frames\" ausente ou vazio"

    if (errors.isNotEmpty()) {
        violations += ContentViolation.InvalidEvent(path, errors.joinToString("; "))
        return null
    }

    val frames = framesJson!!.mapIndexed { i, frameJson -> parseFrame(frameJson, "$path.frames[$i]", violations) }
        .filterNotNull()

    if (frames.isEmpty()) {
        violations += ContentViolation.InvalidEvent(path, "nenhum fotograma válido em \"frames\"")
        return null
    }

    return ContentEvent(
        name = name!!,
        ordering = ordering!!,
        zeroMarkImage = zeroMarkImage!!,
        hintEnabled = hintEnabled!!,
        hintContent = hintContent,
        frames = frames,
    )
}

private fun parseFrame(json: JsonElement, path: String, violations: MutableList<ContentViolation>): Frame? {
    val obj = json as? JsonObject
    if (obj == null) {
        violations += ContentViolation.InvalidFrame(path, "não é um objeto")
        return null
    }

    val tagId = obj["tag_id"].asStringOrNull()
    val image = obj["image"].asStringOrNull()
    val summaryFragment = obj["summary_fragment"].asStringOrNull()

    val errors = mutableListOf<String>()
    if (tagId.isNullOrEmpty()) {
        errors += "\"tag_id\" ausente"
    } else if (!TAG_ID_PATTERN.matches(tagId)) {
        errors += "\"tag_id\" não bate com o padrão [0-9A-F]+"
    }
    if (image.isNullOrEmpty()) errors += "\"image\" ausente"
    if (summaryFragment.isNullOrEmpty()) errors += "\"summary_fragment\" ausente"

    if (errors.isNotEmpty()) {
        violations += ContentViolation.InvalidFrame(path, errors.joinToString("; "))
        return null
    }

    return Frame(
        tagId = tagId!!,
        image = image!!,
        confirmationText = obj["confirmation_text"].asStringOrNull(),
        summaryFragment = summaryFragment!!,
    )
}

private fun ContentInstance.toHierarchyInstance(): HierarchyInstance =
    HierarchyInstance(
        name = name,
        themes = themes.map { theme ->
            HierarchyTheme(
                name = theme.name,
                ordering = theme.ordering,
                events = theme.events.map { event -> HierarchyEvent(name = event.name, ordering = event.ordering) },
            )
        },
    )

private fun duplicateTagIdViolations(instance: ContentInstance): List<ContentViolation.DuplicateTagId> {
    val tagIdToFramePaths = mutableMapOf<String, MutableList<String>>()
    instance.themes.forEachIndexed { themeIndex, theme ->
        theme.events.forEachIndexed { eventIndex, event ->
            event.frames.forEachIndexed { frameIndex, frame ->
                val framePath = "instance.themes[$themeIndex].events[$eventIndex].frames[$frameIndex]"
                tagIdToFramePaths.getOrPut(frame.tagId) { mutableListOf() }.add(framePath)
            }
        }
    }
    return tagIdToFramePaths
        .filterValues { it.size > 1 }
        .map { (tagId, paths) -> ContentViolation.DuplicateTagId(tagId, paths) }
}

private fun JsonElement?.asStringOrNull(): String? {
    val primitive = this as? JsonPrimitive ?: return null
    return if (primitive.isString) primitive.content else null
}

private fun JsonElement?.asBooleanOrNull(): Boolean? {
    val primitive = this as? JsonPrimitive ?: return null
    if (primitive.isString) return null
    return primitive.content.toBooleanStrictOrNull()
}

private fun JsonElement?.asIntOrNull(): Int? {
    val primitive = this as? JsonPrimitive ?: return null
    if (primitive.isString) return null
    return primitive.content.toIntOrNull()
}
