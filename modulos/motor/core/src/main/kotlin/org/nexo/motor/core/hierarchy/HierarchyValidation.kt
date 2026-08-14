package org.nexo.motor.core.hierarchy

sealed interface HierarchyViolation {
    data class DuplicateThemeName(val instanceName: String, val name: String) : HierarchyViolation
    data class DuplicateEventName(val instanceName: String, val themeName: String, val name: String) : HierarchyViolation
    data class NonContiguousThemePositions(val instanceName: String, val positions: List<Int>) : HierarchyViolation
    data class NonContiguousEventPositions(val instanceName: String, val themeName: String, val positions: List<Int>) : HierarchyViolation
}

fun validate(instance: Instance): List<HierarchyViolation> {
    val themeViolations = duplicateNames(instance.themes) { it.name }
        .map { HierarchyViolation.DuplicateThemeName(instanceName = instance.name, name = it) }

    val themePositionViolations = nonContiguousPositions(instance.themes) { it.ordering }
        .map { HierarchyViolation.NonContiguousThemePositions(instanceName = instance.name, positions = it) }

    val eventViolations = instance.themes.flatMap { theme ->
        duplicateNames(theme.events) { it.name }
            .map { HierarchyViolation.DuplicateEventName(instanceName = instance.name, themeName = theme.name, name = it) }
    }

    val eventPositionViolations = instance.themes.flatMap { theme ->
        nonContiguousPositions(theme.events) { it.ordering }
            .map { HierarchyViolation.NonContiguousEventPositions(instanceName = instance.name, themeName = theme.name, positions = it) }
    }

    return themeViolations + themePositionViolations + eventViolations + eventPositionViolations
}

private fun <T> duplicateNames(items: List<T>, nameOf: (T) -> String): List<String> =
    items.map(nameOf)
        .groupingBy { it }
        .eachCount()
        .filterValues { it > 1 }
        .keys
        .toList()

private fun <T> nonContiguousPositions(items: List<T>, orderingOf: (T) -> Ordering): List<List<Int>> {
    val positions = items.mapNotNull { (orderingOf(it) as? Ordering.Ordered)?.position }
    if (positions.isEmpty()) return emptyList()

    val sorted = positions.sorted()
    val expected = (1..positions.size).toList()
    return if (sorted == expected) emptyList() else listOf(sorted)
}
