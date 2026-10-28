package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.atan2

private val rand = SeededRandom(151)

private const val LAP = 5.6f
private const val SHIFT = -1.6f
private val Cream = rgb(0xFFF1D4)
private val PlasmaGold = rgb(0xFFE0A2)
private val PlasmaOrange = rgb(0xFF9E65)
private val WaveCream = rgb(0xFFE7B7)
private val WaveSalmon = rgb(0xFFB5A1)
private val EjectLine = rgb(0xFFCC97)
private val EjectDot = rgb(0xFFF3D4)
private val RimBrown = rgb(0xC85933)


private val RAYS = Path().apply {
    for (i in 0 until 28) {
        val angle = i * (360.0 / 28) + rand.next() * 5
        val tip = if (i % 7 == 0) 77.0 else 61 + rand.next() * 11
        val width = if (i % 7 == 0) 2.8 else 0.8 + rand.next() * 1.2
        val a = polar(52.0, angle - width)
        val b = polar(tip, angle)
        val c = polar(52.0, angle + width)
        moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); close()
    }
}

private class Ejection(val angle: Float, val r: Float, val delay: Float)

private val EJECTIONS = List(18) { i ->
    Ejection(
        angle = r2(i * 20 + rand.next() * 12).toFloat(),
        r = r2(0.45 + rand.next() * 0.8).toFloat(),
        delay = r2(-1.5 - rand.next() * 0.5).toFloat(),
    )
}

private fun Path.svgArc(r: Float, x1: Float, y1: Float, x2: Float, y2: Float) {
    val start = atan2(y1, x1) * 180f / PI.toFloat()
    val end = atan2(y2, x2) * 180f / PI.toFloat()
    var sweep = (end - start) % 360f
    if (sweep < 0f) sweep += 360f
    moveTo(x1, y1)
    arcTo(Rect(-r, -r, r, r), start, sweep, false)
}

private val PLASMA_GOLD = Path().apply {
    svgArc(53.5f, -29f, -45f, 53f, 8f)
    svgArc(53.5f, 24f, 48f, -49f, -21f)
}
private val PLASMA_ORANGE = Path().apply {
    svgArc(56f, -50f, 25f, -28f, -48f)
    svgArc(56f, 53f, -17f, 35f, 44f)
}

private val Corona = Brush.radialGradient(
    0.58f to rgb(0xF76324, 0f), 0.65f to rgb(0xFFB04F, 0.2f), 0.685f to rgb(0xFFF1CE, 0.95f),
    0.72f to rgb(0xFFD484, 0.75f), 0.8f to rgb(0xFF8A42, 0.35f), 1f to rgb(0xF04038, 0f),
    center = Offset.Zero, radius = 78f,
)
private val RayFill = Brush.radialGradient(
    0.64f to rgb(0xFFF6DD),
    0.73f to rgb(0xFFCF87),
    0.88f to rgb(0xFF9A4A, 0.8f),
    1f to rgb(0xD46139, 0.15f),
    center = Offset.Zero,
    radius = 78f,
)
private val Edge = Brush.radialGradient(
    0.78f to rgb(0x6B2631, 0f), 1f to rgb(0x752B34, 0.55f),
    center = Offset.Zero, radius = 50f,
)


internal fun DrawScope.drawSupernova(t: Float, still: Boolean = false) {
    val p = cycle(t, LAP, SHIFT)

    val haloOp = if (still) 1f else kf(p, CssEaseOut, 0f to 0.65f, 0.12f to 1f, 1f to 0.65f)
    val haloSc = if (still) 1f else kf(p, CssEaseOut, 0f to 0.97f, 0.12f to 1.025f, 1f to 0.97f)
    withTransform({ scale(haloSc, haloSc, Offset.Zero) }) {
        drawCircle(Corona, 78f, Offset.Zero, haloOp)
    }
    drawCircle(Edge, 50f, Offset.Zero)
    ring(RimBrown, 52.4f, 2.4f, 0.55f)

    val crownOp =
        if (still) 1f else kf(p, CssEaseOut, 0f to 0.6f, 0.12f to 1f, 0.45f to 0.65f, 1f to 0.6f)
    val crownSc = if (still) 1f else kf(
        p,
        CssEaseOut,
        0f to 0.97f,
        0.12f to 1.035f,
        0.45f to 0.98f,
        1f to 0.97f
    )
    val crownRot =
        if (still) 0f else kf(p, CssEaseOut, 0f to -2f, 0.12f to 0f, 0.45f to 2f, 1f to -2f)
    withTransform({
        scale(crownSc, crownSc, Offset.Zero)
        rotate(crownRot, Offset.Zero)
    }) {
        glow(1.2f, 1f) { w, a -> drawPath(RAYS, RayFill, a * crownOp, Stroke(w)) }
        drawPath(RAYS, RayFill, crownOp)
    }

    val coreOp = if (still) 1f else kf(p, CssEaseOut, 0f to 0.6f, 0.12f to 1f, 1f to 0.6f)
    glow(1.2f, 1f) { w, a -> ring(Cream, 51.2f, 1.4f + w, a * coreOp) }
    ring(Cream, 51.2f, 1.4f, coreOp)

    rotate(if (still) 0f else 360f * cycle(t, 24f), Offset.Zero) {
        glow(1.2f, 1f) { w, a ->
            drawPath(PLASMA_GOLD, PlasmaGold, a, Stroke(2.2f + w, cap = StrokeCap.Round))
            drawPath(PLASMA_ORANGE, PlasmaOrange, a, Stroke(1.2f + w, cap = StrokeCap.Round))
        }
        drawPath(PLASMA_GOLD, PlasmaGold, 1f, Stroke(2.2f, cap = StrokeCap.Round))
        drawPath(PLASMA_ORANGE, PlasmaOrange, 1f, Stroke(1.2f, cap = StrokeCap.Round))
    }

    if (still) return

    for (phase in floatArrayOf(0f, 0.38f)) {
        val wp = cycle(t, LAP, SHIFT - phase)
        val op = kf(wp, CssEaseOut, 0f to 0f, 0.08f to 0.8f, 0.38f to 0f, 1f to 0f)
        if (op <= 0.01f) continue
        val sc = kf(wp, CssEaseOut, 0f to 0.93f, 0.38f to 1.4f, 1f to 1.4f)
        val second = phase > 0f
        withTransform({ scale(sc, sc, Offset.Zero) }) {
            ring(if (second) WaveSalmon else WaveCream, 56f, if (second) 0.65f else 1.1f, op)
        }
    }

    for (e in EJECTIONS) {
        val ep = cycle(t, LAP, e.delay)
        val op = kf(ep, CssEaseOut, 0f to 0f, 0.1f to 1f, 0.38f to 0f, 1f to 0f)
        if (op <= 0.01f) continue
        val ty = kf(ep, CssEaseOut, 0f to -53f, 0.38f to -77f, 1f to -77f)
        val sc = kf(ep, CssEaseOut, 0f to 0.45f, 0.38f to 0.65f, 1f to 0.65f)
        withTransform({
            rotate(e.angle, Offset.Zero)
            translate(0f, ty)
            scale(sc, sc, Offset.Zero)
        }) {
            drawLine(EjectLine, Offset.Zero, Offset(0f, 3.5f), strokeWidth = 0.55f, alpha = op)
            drawCircle(EjectDot, e.r, Offset.Zero, op)
        }
    }
}