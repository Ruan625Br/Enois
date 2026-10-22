package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.floor

private val rand = SeededRandom(7)
private val Sky = rgb(0x38BDF8)
private val SkyRing = rgb(0x7DD3FC)
private val Ice = rgb(0xF0F9FF)

private val Aura = Brush.radialGradient(
    0.60f to Sky.copy(alpha = 0f),
    0.66f to Sky.copy(alpha = 0.5f),
    0.92f to rgb(0x6366F1, 0f),
    center = Offset.Zero,
    radius = 80f,
)

private class Bolt(val path: Path, val dur: Float, val delay: Float)

private fun buildBolt(n: Int): Path {
    val start = n * 30 + rand.next() * 20
    val span = 45 + rand.next() * 60
    val steps = floor(span / 8 + 0.5).toInt()
    val pts = List(steps + 1) { i ->
        polar(50 + (i % 2) * 5 + rand.next() * 4, start + span * i / steps + (rand.next() - 0.5) * 4)
    }
    val path = Path().apply {
        moveTo(pts[0].x, pts[0].y)
        for (i in 1..steps) lineTo(pts[i].x, pts[i].y)
    }
    val branches = 1 + floor(rand.next() * 2).toInt()
    repeat(branches) {
        val i = 1 + floor(rand.next() * (steps - 1)).toInt()
        var radius = 54.0
        var angle = start + span * i / steps
        path.moveTo(pts[i].x, pts[i].y)
        repeat(3) {
            radius += 4 + rand.next() * 4
            angle += (rand.next() - 0.5) * 14
            val p = polar(radius, angle)
            path.lineTo(p.x, p.y)
        }
    }
    return path
}

private val BOLTS: List<Bolt> = List(12) { buildBolt(it) }.mapIndexed { i, path ->
    val dur = r2(0.9 + (i % 3) * 0.27)
    // fases pela razão áurea: raios vizinhos quase nunca acendem juntos
    Bolt(path, dur.toFloat(), r2(-((i * 0.618) % 1.0) * dur).toFloat())
}

internal fun DrawScope.drawStorm(t: Float) {
    // moldura-tempestade-relampago 3.1s linear
    val flash = kf(
        cycle(t, 3.1f), LinearEasing,
        0f to 0.45f, 0.44f to 0.45f, 0.46f to 1f, 0.49f to 0.5f, 0.52f to 1f, 0.58f to 0.45f, 1f to 0.45f,
    )
    drawCircle(Aura, 80f, Offset.Zero, flash)

    val ringOp = if (cycle(t, 0.4f) < 0.5f) 1f else 0.55f
    glow(1.6f, 2f) { w, a -> ring(SkyRing, 50.5f, 2f + w, a * ringOp) }
    ring(SkyRing, 50.5f, 2f, ringOp)

    rotate(360f * cycle(t, 14f), Offset.Zero) {
        for (b in BOLTS) {
            val op = kf(
                cycle(t, b.dur, b.delay), LinearEasing,
                0f to 1f, 0.05f to 0.3f, 0.08f to 1f, 0.18f to 1f, 0.21f to 0.4f,
                0.24f to 1f, 0.30f to 1f, 0.31f to 0f, 1f to 0f,
            )
            if (op < 0.02f) continue
            glow(1.6f, 1.5f) { w, a ->
                drawPath(b.path, Sky, a * op, Stroke(3.2f + w, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            drawPath(b.path, Sky, 0.55f * op, Stroke(3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(b.path, Ice, op, Stroke(1.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}