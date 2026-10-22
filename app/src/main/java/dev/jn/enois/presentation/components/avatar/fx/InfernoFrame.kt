package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun InfernoFrame(modifier: Modifier = Modifier) {

    val infiniteTransition = rememberInfiniteTransition(
        label = "inferno"
    )

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flameProgress",
    )

    Canvas(modifier = modifier) {
        val center = Offset(
            x = size.width / 2f,
            y = size.height / 2f,
        )

        val radius = size.minDimension * 0.39f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x55EA580C),
                    Color.Transparent,
                ),
                center = center,
                radius = radius * 1.5f,
            ),
            radius = radius * 1.5f,
            center = center,
        )

        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFFDE047),
                    Color(0xFFEF4444),
                    Color(0xFF7F1D1D),
                    Color(0xFFFB923C),
                    Color(0xFFFDE047),
                ),
                center = center,
            ),
            radius = radius,
            center = center,
            style = Stroke(
                width = size.minDimension * (0.035f + progress * 0.012f),
            ),
        )
    }
}