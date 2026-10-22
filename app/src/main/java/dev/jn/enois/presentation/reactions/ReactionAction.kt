package dev.jn.enois.presentation.reactions

import dev.jn.enois.domain.model.ReactionType

sealed interface ReactionAction {

    data class Click(val postId: String, val type: ReactionType) : ReactionAction
}