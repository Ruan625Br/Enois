package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform

private val rand = SeededRandom(31)

private val Slime = rgb(0x84CC16)
private val LimeFill = rgb(0xBEF264)
private val LimeStroke = rgb(0xD9F99D)
private val Rim = rgb(0xECFCCB)

private val Aura = Brush.radialGradient(
    0.60f to rgb(0x84CC16, 0f),
    0.68f to rgb(0x84CC16, 0.45f),
    0.95f to rgb(0x84CC16, 0f),
    center = Offset.Zero,
    radius = 80f,
)
private val Vinheta = Brush.radialGradient(
    0.8f to rgb(0x4D7C0F, 0f),
    1f to rgb(0x4D7C0F, 0.55f),
    center = Offset.Zero,
    radius = 50f,
)

private class Blob(val angle: Float, val r: Float, val dur: Float, val delay: Float)
private class Drop(val pos: Offset, val dur: Float, val delay: Float)
private class Bubble(val x: Float, val y: Float, val r: Float, val dur: Float, val delay: Float)

private val BLOBS = List(12) { i ->
    val angle = r2(i * 30 + (rand.next() - 0.5) * 12).toFloat()
    val r = r2(3.2 + rand.next() * 1.8).toFloat()
    val dur = r2(1.6 + rand.next()).toFloat()
    val delay = r2(-rand.next() * 2.6).toFloat()
    Blob(angle, r, dur, delay)
}

private val DROPS = listOf(152.0 to 2.4, 183.0 to 3.1, 213.0 to 2.7).map { (angle, dur) ->
    Drop(polar(51.0, angle), dur.toFloat(), r2(-rand.next() * dur).toFloat())
}

private val BUBBLES = List(7) {
    val p = polar(53 + rand.next() * 6, (rand.next() - 0.5) * 200)
    val r = r2(1.2 + rand.next() * 1.4).toFloat()
    val dur = r2(1.6 + rand.next() * 1.2).toFloat()
    val delay = r2(-rand.next() * 2.8).toFloat()
    Bubble(p.x, p.y, r, dur, delay)
}


internal fun DrawScope.drawToxic(t: Float) {
    val auraOp = kf(cycle(t, 2.4f), CssEaseInOut, 0f to 1f, 0.5f to 0.55f, 1f to 1f)
    drawCircle(Aura, 80f, Offset.Zero, auraOp)
    drawCircle(Vinheta, 50f, Offset.Zero)

    glow(2.5f, 1.2f) { w, a -> ring(Slime, 50.5f, 4.5f + w, a) }
    ring(Slime, 50.5f, 4.5f)

    rotate(360f * cycle(t, 16f), Offset.Zero) {
        for (b in BLOBS) {
            val s = kf(cycle(t, b.dur, b.delay), CssEaseInOut, 0f to 0.75f, 0.5f to 1.3f, 1f to 0.75f)
            withTransform({
                rotate(b.angle, Offset.Zero)
                translate(0f, -50.5f)
            }) {
                drawCircle(Slime, b.r * s, Offset.Zero)
            }
        }
    }

    for (d in DROPS) {
        val p = cycle(t, d.dur, d.delay)
        val ty = kf(p, EaseDrop, 0f to 0f, 0.35f to 3f, 1f to 28f)
        val sc = kf(p, EaseDrop, 0f to 0.5f, 0.35f to 1f, 1f to 0.3f)
        val radius = 3.2f * sc
        val neck = (1f - (ty - 1f) / 12f).coerceIn(0f, 1f)
        if (neck > 0f) {
            drawLine(
                Slime, d.pos, Offset(d.pos.x, d.pos.y + ty),
                strokeWidth = radius * 2f * 0.8f * neck, cap = StrokeCap.Round,
            )
        }
        drawCircle(Slime, radius, Offset(d.pos.x, d.pos.y + ty))
    }

    ring(Rim, 49f, 0.7f, 0.7f)

    for (b in BUBBLES) {
        val p = cycle(t, b.dur, b.delay)
        val ty = kf(p, CssEaseOut, 0f to 0f, 0.85f to -16f, 1f to -18f)
        val sc = kf(p, CssEaseOut, 0f to 0.4f, 0.85f to 1f, 1f to 1.6f)
        val op = kf(p, CssEaseOut, 0f to 0f, 0.15f to 1f, 0.85f to 1f, 1f to 0f)
        if (op < 0.01f) continue
        val c = Offset(b.x, b.y + ty)
        drawCircle(LimeFill, b.r * sc, c, 0.25f * op)
        drawCircle(LimeStroke, b.r * sc, c, op, Stroke(0.6f * sc))
    }
}