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

fun <Theme, Event> sessionEventNames(
    themes: List<Theme>,
    themeOrdering: (Theme) -> Ordering,
    eventsOf: (Theme) -> List<Event>,
    eventOrdering: (Event) -> Ordering,
    eventName: (Event) -> String,
    fromTheme: Theme,
    fromEvent: Event,
    untilTheme: Theme,
    untilEvent: Event,
): List<String> {
    val themeRange = sessionScope(themes, themeOrdering, fromTheme, untilTheme)

    return themeRange.flatMap { theme ->
        val events = eventsOf(theme)
        val orderedEvents = events.filter { eventOrdering(it) !is Ordering.Standalone }
        val first = if (theme == fromTheme) fromEvent else orderedEvents.minBy { (eventOrdering(it) as Ordering.Ordered).position }
        val last = if (theme == untilTheme) untilEvent else orderedEvents.maxBy { (eventOrdering(it) as Ordering.Ordered).position }
        sessionScope(events, eventOrdering, first, last).map(eventName)
    }
}
