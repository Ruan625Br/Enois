package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

private val rand = SeededRandom(11)

private val Orange = rgb(0xF97316)
private val EmberColor = rgb(0xFDBA74)

private val Vignette = Brush.radialGradient(
    0.78f to rgb(0xEA580C, 0f),
    1f to rgb(0xEA580C, 0.6f),
    center = Offset.Zero,
    radius = 50f,
)

private class Flame(
    val x: Float, val y: Float, val angle: Float,
    val path: Path, val fill: Brush, val dur: Float, val delay: Float,
)

private class Ember(val x: Float, val y: Float, val r: Float, val drift: Float, val dur: Float, val delay: Float)

private fun labareda(w: Double, h: Double): Triple<Path, Float, Float> {
    val a = r2(w).toFloat()
    val b = r2(h).toFloat()
    val c = r2(w * 0.15).toFloat()
    val p = Path().apply {
        moveTo(-a, 0f)
        cubicTo(-a, -b * 0.45f, -c, -b * 0.55f, 0f, -b)
        cubicTo(c, -b * 0.55f, a, -b * 0.45f, a, 0f)
        cubicTo(a * 0.55f, a * 0.9f, -a * 0.55f, a * 0.9f, -a, 0f)
        close()
    }
    return Triple(p, -b, a * 0.675f)
}

private fun chamas(count: Int, radius: Double, scale: Double, inner: Boolean): List<Flame> =
    List(count) { i ->
        val theta = i * 360.0 / count + (rand.next() - 0.5) * 6
        val rad = theta * PI / 180
        val pos = polar(radius, theta)
        val angle = r2(atan2(sin(rad), cos(rad) + 0.6) * 180 / PI).toFloat()
        val top = (1 + cos(rad)) / 2
        val (path, topY, bottomY) = labareda(
            (3.2 + rand.next() * 1.4) * scale,
            (7 + 13 * top + rand.next() * 5) * scale,
        )
        val dur = r2(0.5 + rand.next() * 0.45).toFloat()
        val delay = r2(-rand.next()).toFloat()
        val fill = if (inner) {
            vGrad(bottomY, topY, 0f to rgb(0xFFFBEB), 0.5f to rgb(0xFDE047), 1f to rgb(0xF97316, 0f))
        } else {
            vGrad(
                bottomY, topY,
                0f to rgb(0xFDE047), 0.35f to rgb(0xFB923C), 0.75f to rgb(0xEF4444), 1f to rgb(0x7F1D1D, 0f),
            )
        }
        Flame(pos.x, pos.y, angle, path, fill, dur, delay)
    }

private val OUTER = chamas(26, 47.0, 1.0, inner = false)
private val INNER = chamas(20, 48.0, 0.6, inner = true)
private val EMBERS = List(9) {
    val p = polar(52 + rand.next() * 6, (rand.next() - 0.5) * 170)
    val r = r2(0.7 + rand.next() * 0.7).toFloat()
    val drift = r2((rand.next() - 0.5) * 14).toFloat()
    val dur = r2(1.4 + rand.next()).toFloat()
    val delay = r2(-rand.next() * 2.4).toFloat()
    Ember(p.x, p.y, r, drift, dur, delay)
}

private val Scratch = Matrix()

private fun DrawScope.drawFlames(t: Float, list: List<Flame>) {
    for (f in list) {
        val p = cycle(t, f.dur, f.delay)
        val sx = kf(p, CssEaseInOut, 0f to 1f, 0.25f to 0.88f, 0.5f to 1.06f, 0.75f to 0.92f, 1f to 1f)
        val sy = kf(p, CssEaseInOut, 0f to 1f, 0.25f to 1.2f, 0.5f to 0.85f, 0.75f to 1.12f, 1f to 1f)
        val sk = kf(p, CssEaseInOut, 0f to 0f, 0.25f to 5f, 0.5f to 0f, 0.75f to -5f, 1f to 0f)

        Scratch.reset()
        Scratch.values[0] = sx
        Scratch.values[4] = sx * tan(sk * PI.toFloat() / 180f)
        Scratch.values[5] = sy

        withTransform({
            translate(f.x, f.y)
            rotate(f.angle, Offset.Zero)
            transform(Scratch)
        }) {
            drawPath(f.path, f.fill, 0.35f, Stroke(2.4f)) // halo (blur do filtro "calor")
            drawPath(f.path, f.fill)
        }
    }
}

internal fun DrawScope.drawInferno(t: Float) {
    drawCircle(Vignette, 50f, Offset.Zero)

    val k = kf(cycle(t, 1.3f), CssEaseInOut, 0f to 0f, 0.5f to 1f, 1f to 0f)
    glow(1.8f, 2.2f) { w, a -> ring(Orange, 49.5f, 3.5f + 1.5f * k + w, a * (1f - 0.4f * k)) }

    drawFlames(t, OUTER)
    drawFlames(t, INNER)

    for (e in EMBERS) {
        val p = cycle(t, e.dur, e.delay)
        val m = CssEaseOut.transform(p)
        val op = kf(p, CssEaseOut, 0f to 0f, 0.15f to 1f, 1f to 0f)
        drawCircle(EmberColor, e.r * (1f - 0.7f * m), Offset(e.x + e.drift * m, e.y - 30f * m), op)
    }
}