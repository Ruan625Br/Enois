package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private class Comet(val tilt: Float, val color: Color, val lap: Float, val phase: Float)

private val COMETS = listOf(
    Comet(-18f, rgb(0xC4B5FD), 3.2f, 0f),
    Comet(34f, rgb(0x67E8F9), 4.4f, 0.35f),
    Comet(80f, rgb(0xF9A8D4), 5.6f, 0.7f),
)

private val TAIL = floatArrayOf(9f, 7.5f, 6.2f, 5f, 4f, 3.2f, 2.5f)


private val BACK_MASK = Path().apply {
    moveTo(-50.2f, 0f)
    arcTo(Rect(-50.2f, -50.2f, 50.2f, 50.2f), 180f, 180f, false)
    close()
}

private val OuterGlow = Brush.radialGradient(
    0f to Color.Transparent,
    0.86f to Color.Transparent,
    0.865f to rgb(0x8B5CF6, 0.45f),
    1f to rgb(0x8B5CF6, 0f),
    center = Offset.Zero,
    radius = 58f,
)

internal fun DrawScope.drawOrbit(t: Float) {
    drawCircle(OuterGlow, 58f, Offset.Zero)
    ring(rgb(0xC4B5FD, 0.7f), 50.6f, 1.2f)

    for (c in COMETS) {
        rotate(c.tilt, Offset.Zero) {
            clipPath(BACK_MASK, ClipOp.Difference) {
                drawOval(c.color, Offset(-66f, -17f), Size(132f, 34f), 0.3f, Stroke(0.6f))

                for (k in TAIL.indices) {

                    val th = 2f * PI.toFloat() * (t / c.lap + c.phase - 0.03f * k)
                    val p = Offset(-66f * cos(th), 17f * sin(th))
                    val r = TAIL[k] / 2f
                    val op = 1f - k * 0.12f
                    drawCircle(c.color, r + 2.2f, p, 0.18f * op)
                    drawCircle(c.color, r + 1.1f, p, 0.25f * op)
                    drawCircle(c.color, r, p, op)
                }
            }
        }
    }
}