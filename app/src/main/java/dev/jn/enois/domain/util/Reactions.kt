package dev.jn.enois.domain.util

import dev.jn.enois.domain.model.ReactionType

fun Map<ReactionType, Int>.withReaction(previous: ReactionType?, next: ReactionType?): Map<ReactionType, Int> {
    val counts = toMutableMap()
    previous?.let { p ->
        val left = (counts[p] ?: 0) - 1
        if (left > 0) counts[p] = left else counts.remove(p)
    }
    next?.let { n -> counts[n] = (counts[n] ?: 0) + 1 }
    return counts
}