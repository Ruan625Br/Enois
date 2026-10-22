package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

private val rand = SeededRandom(23)
private const val LAP = 3.6f

private val IceRing = rgb(0xE0F2FE)
private val IceEdge = rgb(0xF0F9FF)
private val IceGlow = rgb(0xBAE6FD)

private val Cold = Brush.radialGradient(
    0.8f to rgb(0xBAE6FD, 0f),
    1f to rgb(0xE0F2FE, 0.65f),
    center = Offset.Zero,
    radius = 50f,
)

private class Shard(val path: Path, val fill: Brush, val tilt: Float)

private class Crystal(
    val x: Float, val y: Float, val angle: Float,
    val main: Shard, val left: Shard, val right: Shard, val delay: Float,
)

private class Sparkle(val x: Float, val y: Float, val delay: Float, val dur: Float)
private class Frost(val x: Float, val y: Float, val r: Float, val dx: Float, val dy: Float, val delay: Float)


private fun shard(w: Double, h: Double, tilt: Double): Shard {
    val wf = w.toFloat()
    val hf = h.toFloat()
    val path = Path().apply {
        moveTo(-wf, 0f)
        lineTo(-wf * 0.55f, -hf * 0.82f)
        lineTo(0f, -hf)
        lineTo(wf * 0.55f, -hf * 0.82f)
        lineTo(wf, 0f)
        close()
    }
    val fill = vGrad(
        0f, -hf,
        0f to rgb(0x0EA5E9, 0.85f), 0.55f to rgb(0xBAE6FD, 0.9f), 1f to Color.White,
    )
    return Shard(path, fill, tilt.toFloat())
}

private val CRYSTALS = List(14) { i ->
    val angle = r2(i * 360.0 / 14 + (rand.next() - 0.5) * 10)
    val pos = polar(48.0, angle)
    val h = r2(9 + rand.next() * 10)
    val lh = r2(h * (0.45 + rand.next() * 0.2))
    val lt = r2(-26 - rand.next() * 10)
    val rh = r2(h * (0.45 + rand.next() * 0.2))
    val rt = r2(24 + rand.next() * 12)
    Crystal(
        pos.x, pos.y, angle.toFloat(),
        main = shard(2.6, h, 0.0),
        left = shard(1.8, lh, lt),
        right = shard(1.8, rh, rt),

        delay = r2(-LAP + angle / 360.0 * LAP).toFloat(),
    )
}

private val SPARKLES = List(6) { i ->
    val p = polar(56 + rand.next() * 10, i * 60 + rand.next() * 40)
    val delay = r2(-rand.next() * 2.4).toFloat()
    val dur = r2(1.8 + rand.next()).toFloat()
    Sparkle(p.x, p.y, delay, dur)
}

private val FROST = List(8) { i ->
    val angle = i * 45 + rand.next() * 30
    val p = polar(51.0, angle)
    val d = polar(12 + rand.next() * 8, angle)
    val r = r2(0.6 + rand.next() * 0.6).toFloat()
    val delay = r2(-rand.next() * 3).toFloat()
    Frost(p.x, p.y, r, d.x, d.y, delay)
}

private val SparklePath = Path().apply {
    moveTo(0f, -4f); lineTo(1f, -1f); lineTo(4f, 0f); lineTo(1f, 1f)
    lineTo(0f, 4f); lineTo(-1f, 1f); lineTo(-4f, 0f); lineTo(-1f, -1f)
    close()
}

private fun DrawScope.drawShard(s: Shard) {
    withTransform({ rotate(s.tilt, Offset.Zero) }) {
        drawPath(s.path, s.fill)
        drawPath(s.path, IceEdge, 1f, Stroke(0.5f, join = StrokeJoin.Round))
    }
}

internal fun DrawScope.drawGlacial(t: Float) {
    drawCircle(Cold, 50f, Offset.Zero)

    val ringOp = kf(cycle(t, 3f), CssEaseInOut, 0f to 1f, 0.5f to 0.6f, 1f to 1f)
    glow(1.4f, 1.4f) { w, a -> ring(IceRing, 50f, 2.2f + w, a * ringOp) }
    ring(IceRing, 50f, 2.2f, ringOp)

    for (c in CRYSTALS) {
        withTransform({
            translate(c.x, c.y)
            rotate(c.angle, Offset.Zero)
        }) {
            glow(1.4f, 1f) { w, a ->
                drawPath(c.main.path, IceGlow, a, Stroke(w, join = StrokeJoin.Round))
            }
            drawShard(c.left)
            drawShard(c.right)
            drawShard(c.main)

            val p = cycle(t, LAP, c.delay)
            val op = kf(p, CssEaseInOut, 0f to 0f, 0.08f to 0.85f, 0.2f to 0f, 1f to 0f)
            if (op > 0.01f) drawPath(c.main.path, Color.White, op)
        }
    }

    for (g in FROST) {
        val p = cycle(t, 3f, g.delay)
        val m = CssEaseOut.transform(p)
        val op = kf(p, CssEaseOut, 0f to 0f, 0.2f to 0.9f, 1f to 0f)
        drawCircle(IceEdge, g.r, Offset(g.x + g.dx * m, g.y + g.dy * m), op)
    }

    for (s in SPARKLES) {
        val k = kf(cycle(t, s.dur, s.delay), CssEaseInOut, 0f to 0f, 0.5f to 1f, 1f to 0f)
        if (k < 0.02f) continue
        withTransform({
            translate(s.x, s.y)
            rotate(45f * k, Offset.Zero)
            scale(k, k, Offset.Zero)
        }) {
            drawPath(SparklePath, Color.White, k)
        }
    }
}