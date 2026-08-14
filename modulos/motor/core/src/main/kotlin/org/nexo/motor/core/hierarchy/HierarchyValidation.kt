package org.nexo.motor.core.hierarchy

sealed interface HierarchyViolation {
    data class DuplicateThemeName(val instanceName: String, val name: String) : HierarchyViolation
    data class DuplicateEventName(val instanceName: String, val themeName: String, val name: String) : HierarchyViolation
}

fun validate(instance: Instance): List<HierarchyViolation> {
    val themeViolations = duplicateNames(instance.themes) { it.name }
        .map { HierarchyViolation.DuplicateThemeName(instanceName = instance.name, name = it) }

    val eventViolations = instance.themes.flatMap { theme ->
        duplicateNames(theme.events) { it.name }
            .map { HierarchyViolation.DuplicateEventName(instanceName = instance.name, themeName = theme.name, name = it) }
    }

    return themeViolations + eventViolations
}

private fun <T> duplicateNames(items: List<T>, nameOf: (T) -> String): List<String> =
    items.map(nameOf)
        .groupingBy { it }
        .eachCount()
        .filterValues { it > 1 }
        .keys
        .toList()
