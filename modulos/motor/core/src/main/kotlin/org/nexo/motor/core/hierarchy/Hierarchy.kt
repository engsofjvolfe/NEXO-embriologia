package org.nexo.motor.core.hierarchy

sealed interface Ordering {
    data class Ordered(val position: Int) : Ordering
    data object Standalone : Ordering
}

data class Event(val name: String, val ordering: Ordering)

data class Theme(val name: String, val ordering: Ordering, val events: List<Event>)

data class Instance(val name: String, val themes: List<Theme>)
