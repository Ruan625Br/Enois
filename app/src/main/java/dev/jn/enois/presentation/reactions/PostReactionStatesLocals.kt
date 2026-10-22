package dev.jn.enois.presentation.reactions

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPostReactionStates =
    staticCompositionLocalOf<SharedTransitionScope> {
        error(
            "SharedTransitionScope não foi fornecido. " +
                    "Envolva o NavDisplay com SharedTransitionLayout."
        )
    }