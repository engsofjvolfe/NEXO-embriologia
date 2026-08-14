package org.nexo.motor.core.search

import java.text.Normalizer

private val diacriticMarks = Regex("\\p{Mn}+")

internal fun normalize(text: String): String {
    val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
    return diacriticMarks.replace(decomposed, "").lowercase()
}

fun levenshteinDistance(a: String, b: String): Int {
    if (a == b) return 0
    if (a.isEmpty()) return b.length
    if (b.isEmpty()) return a.length

    var previousRow = IntArray(b.length + 1) { it }
    for (i in 1..a.length) {
        val currentRow = IntArray(b.length + 1)
        currentRow[0] = i
        for (j in 1..b.length) {
            currentRow[j] = if (a[i - 1] == b[j - 1]) {
                previousRow[j - 1]
            } else {
                1 + minOf(previousRow[j - 1], previousRow[j], currentRow[j - 1])
            }
        }
        previousRow = currentRow
    }
    return previousRow[b.length]
}

fun substringLevenshteinDistance(query: String, text: String): Int {
    if (query.isEmpty()) return 0
    if (text.isEmpty()) return query.length

    var previousRow = IntArray(text.length + 1) { 0 }
    for (i in 1..query.length) {
        val currentRow = IntArray(text.length + 1)
        currentRow[0] = i
        for (j in 1..text.length) {
            currentRow[j] = if (query[i - 1] == text[j - 1]) {
                previousRow[j - 1]
            } else {
                1 + minOf(previousRow[j - 1], previousRow[j], currentRow[j - 1])
            }
        }
        previousRow = currentRow
    }
    return previousRow.min()
}

fun approximateSearchThreshold(queryLength: Int): Int = maxOf(1, queryLength / 5)

data class ApproximateMatch<T>(val item: T, val matchedWholeName: Boolean, val distance: Int)

fun <T> approximateSearch(items: List<T>, query: String, nameOf: (T) -> String): List<T> {
    val normalizedQuery = normalize(query.trim())
    if (normalizedQuery.isEmpty()) return items

    val threshold = approximateSearchThreshold(normalizedQuery.length)

    val matches = items.mapNotNull { item ->
        val normalizedName = normalize(nameOf(item))
        val wholeDistance = levenshteinDistance(normalizedQuery, normalizedName)
        if (wholeDistance <= threshold) {
            ApproximateMatch(item, matchedWholeName = true, distance = wholeDistance)
        } else {
            val trechoDistance = substringLevenshteinDistance(normalizedQuery, normalizedName)
            if (trechoDistance <= threshold) {
                ApproximateMatch(item, matchedWholeName = false, distance = trechoDistance)
            } else {
                null
            }
        }
    }

    return matches
        .sortedWith(compareBy({ match -> !match.matchedWholeName }, { match -> match.distance }))
        .map { it.item }
}
