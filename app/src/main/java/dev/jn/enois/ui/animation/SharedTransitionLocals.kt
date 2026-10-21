package dev.jn.enois.ui.animation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSharedTransitionScope =
    staticCompositionLocalOf<SharedTransitionScope> {
        error(
            "SharedTransitionScope não foi fornecido. " +
                    "Envolva o NavDisplay com SharedTransitionLayout."
        )
    }