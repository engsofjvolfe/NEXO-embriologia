package org.nexo.motor.core.session

import org.nexo.motor.core.hierarchy.Ordering

fun <T> sessionScope(siblings: List<T>, ordering: (T) -> Ordering, from: T, until: T): List<T> {
    if (ordering(from) is Ordering.Standalone) return listOf(from)

    val ordered = siblings
        .filter { ordering(it) !is Ordering.Standalone }
        .sortedBy { (ordering(it) as Ordering.Ordered).position }

    val fromIndex = ordered.indexOf(from)
    val untilIndex = ordered.indexOf(until)
    return ordered.subList(fromIndex, untilIndex + 1)
}
