package org.nexo.motor.core.content

import org.nexo.motor.core.hierarchy.HierarchyViolation
import org.nexo.motor.core.hierarchy.Ordering

data class Frame(val tagId: String, val image: String, val confirmationText: String?, val summaryFragment: String)

data class ContentEvent(
    val name: String,
    val ordering: Ordering,
    val zeroMarkImage: String,
    val hintEnabled: Boolean,
    val hintContent: String?,
    val frames: List<Frame>,
)

data class ContentTheme(val name: String, val ordering: Ordering, val events: List<ContentEvent>)

data class ContentInstance(val name: String, val retentionPeriod: String, val themes: List<ContentTheme>)

sealed interface ContentViolation {
    data class InvalidManifest(val reason: String) : ContentViolation
    data class InvalidTheme(val path: String, val reason: String) : ContentViolation
    data class InvalidEvent(val path: String, val reason: String) : ContentViolation
    data class InvalidFrame(val path: String, val reason: String) : ContentViolation
    data class DuplicateTagId(val tagId: String, val framePaths: List<String>) : ContentViolation
    data class Hierarchy(val violation: HierarchyViolation) : ContentViolation
}

data class ContentImportResult(val instance: ContentInstance?, val violations: List<ContentViolation>)
