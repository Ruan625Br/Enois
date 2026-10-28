package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import dev.jn.enois.domain.model.AvatarFrameType


@Composable
fun AvatarFrameFx(frame: AvatarFrameType, modifier: Modifier = Modifier) {
    val clock = LocalAvatarFxClock.current ?: rememberFreeRunningClock()
    val still = false
    val galaxia = if (frame == AvatarFrameType.GALAXIA) rememberGalaxiaImages() else null


    Canvas(modifier) {
        val t = clock.floatValue
        val s = size.minDimension / 160f
        withTransform({
            translate(size.width / 2f, size.height / 2f)
            scale(s, s, Offset.Zero)
        }) {
            when (frame) {
                AvatarFrameType.AURORA -> drawAurora(t)
                AvatarFrameType.PULSAR -> drawPulsar(t)
                AvatarFrameType.SOLAR -> drawSolar(t)
                AvatarFrameType.STORM -> drawStorm(t)
                AvatarFrameType.INFERNO -> drawInferno(t)
                AvatarFrameType.BLACK_HOLE -> drawBlackHole(t)
                AvatarFrameType.ORBIT -> drawOrbit(t)
                AvatarFrameType.GLACIAL -> drawGlacial(t)
                AvatarFrameType.TOXIC -> drawToxic(t)
                AvatarFrameType.HOLOGRAM -> drawHologram(t)
                AvatarFrameType.NEBULA -> drawNebula(t, still)
                AvatarFrameType.SUPERNOVA -> drawSupernova(t, still)
                AvatarFrameType.PORTAL -> drawPortal(t, still)
                AvatarFrameType.GALAXIA -> galaxia?.let { drawGalaxia(t, it, still) }
            }
        }
    }
}

private fun DrawScope.insetGlow(color: Color, a: Float) {
    if (a <= 0.01f) return
    drawCircle(
        Brush.radialGradient(
            0.7f to Color.Transparent,
            1f to color.copy(alpha = 0.85f * a),
            center = Offset.Zero,
            radius = 50f,
        ),
        50f,
        Offset.Zero,
    )
}

private fun DrawScope.drawAurora(t: Float) {
    val hue = (255.1f + 360f * cycle(t, 4f)) % 360f
    ring(Color.hsv(hue, 0.444f, 0.98f), 47f, 6f)
}

private fun DrawScope.drawPulsar(t: Float) {
    val k = kf(cycle(t, 2f), CssEaseInOut, 0f to 0f, 0.5f to 1f, 1f to 0f)
    ring(lerp(rgb(0xA78BFA), rgb(0xF5D0FE), k), 47f, 6f)
    insetGlow(rgb(0xC084FC), k)
}

private fun DrawScope.drawSolar(t: Float) {
    val k = kf(cycle(t, 2.5f), CssEaseInOut, 0f to 0f, 0.5f to 1f, 1f to 0f)
    ring(lerp(rgb(0xFBBF24), rgb(0xFB7185), k), 47f, 6f)
    insetGlow(rgb(0xFBBF24), k)
}