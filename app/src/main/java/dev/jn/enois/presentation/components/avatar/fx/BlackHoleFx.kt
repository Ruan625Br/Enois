package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val rand = SeededRandom(43)
private const val INCLINATION = -10f
private const val SPOT_LAP = 2.4f

private val Doppler = Brush.linearGradient(
    0f to rgb(0xFF5A1F, 0.55f), 0.45f to rgb(0xFFB347, 0.85f), 1f to rgb(0xFFF8E7),
    start = Offset(-80f, 0f), end = Offset(80f, 0f),
)
private val Umbra = Brush.radialGradient(
    0f to Color.Black.copy(alpha = 0.3f), 0.5f to Color.Black.copy(alpha = 0.55f),
    0.75f to Color.Black.copy(alpha = 0.88f), 0.9f to Color.Black,
    center = Offset.Zero, radius = 50f,
)
private val Halo = Brush.radialGradient(
    0.62f to rgb(0xFF9A3C, 0f), 0.7f to rgb(0xFF9A3C, 0.2f), 1f to rgb(0xFF9A3C, 0f),
    center = Offset.Zero, radius = 80f,
)
private val Lens = Brush.radialGradient(
    0.76f to rgb(0xFFF3D6), 0.86f to rgb(0xFFB347, 0.85f), 1f to rgb(0xFF7A1A, 0f),
    center = Offset.Zero, radius = 68f,
)

private class Band(val path: Path, val width: Float, val dur: Float, val dash: FloatArray, val period: Float)
private class Star(val x: Float, val y: Float, val r: Float, val delay: Float)

private fun dashes(): Pair<FloatArray, Float> {
    val parts = DoubleArray(6) { i -> r2(if (i % 2 == 1) 0.8 + rand.next() * 2.2 else 10 + rand.next() * 24) }
    return FloatArray(6) { parts[it].toFloat() } to r2(parts.sum()).toFloat()
}

private fun ellipse(rx: Double, ry: Double) = Path().apply {
    val r = Rect(-rx.toFloat(), -ry.toFloat(), rx.toFloat(), ry.toFloat())
    arcTo(r, 0f, 180f, true)      // começa à direita e vai no sentido horário, como o <ellipse>
    arcTo(r, 180f, 180f, false)
}

private fun halfArc(rx: Double, ry: Double, top: Boolean) = Path().apply {
    arcTo(Rect(-rx.toFloat(), -ry.toFloat(), rx.toFloat(), ry.toFloat()), 180f, if (top) 180f else -180f, true)
}

private val DISK = List(12) { i ->
    val rx = 55 + i * 2.3
    val ry = r2(rx * 0.13)
    val width = r2(1.5 - i * 0.06)
    val dur = r2(1.1 + i * 0.32)
    val (dash, period) = dashes()
    Band(ellipse(rx, ry), width.toFloat(), dur.toFloat(), dash, period)
}

private val TOP_ARCS = listOf(53.0, 55.0, 57.0, 59.0, 61.0).map { r ->
    val ry = r2(r + (r - 52) * 0.45)
    val dur = r2(1.3 + (r - 53) * 0.25)
    val (dash, period) = dashes()
    Band(halfArc(r, ry, true), 1.5f, dur.toFloat(), dash, period)
}

private val BOTTOM_ARCS = listOf(52.0, 53.6, 55.2).map { r ->
    val ry = r2(r * 0.96)
    val dur = r2(1.6 + (r - 52) * 0.3)
    val (dash, period) = dashes()
    Band(halfArc(r, ry, false), 0.9f, dur.toFloat(), dash, period)
}

private val STARS = List(16) {
    val p = polar(60 + rand.next() * 20, rand.next() * 360)
    val r = r2(0.3 + rand.next() * 0.5).toFloat()
    val delay = r2(-rand.next() * 3).toFloat()
    Star(p.x, p.y, r, delay)
}

private val TOP_BODY = Path().apply {
    moveTo(-51.5f, 0f)
    arcTo(Rect(-51.5f, -51.5f, 51.5f, 51.5f), 180f, 180f, false)
    lineTo(62f, 0f)
    arcTo(Rect(-62f, -68f, 62f, 68f), 0f, -180f, false)
    close()
}
private val BOTTOM_BODY = Path().apply {
    moveTo(-51.5f, 0f)
    arcTo(Rect(-51.5f, -51.5f, 51.5f, 51.5f), 180f, -180f, false)
    lineTo(55f, 0f)
    arcTo(Rect(-55f, -54f, 55f, 54f), 0f, 180f, false)
    close()
}
private val DISK_BODY = Path().apply {
    fillType = PathFillType.EvenOdd
    addOval(Rect(-82f, -11f, 82f, 11f))
    addOval(Rect(-54f, -6f, 54f, 6f))
}

/** mask "atras": a metade de trás do disco some atrás da sombra (meio-disco de cima, r=51). */
private val BACK_MASK = Path().apply {
    moveTo(-51f, 0f)
    arcTo(Rect(-51f, -51f, 51f, 51f), 180f, 180f, false)
    close()
}

private val SpotColor = rgb(0xFFFAF0)
private val PhotonColor = rgb(0xFFF1CF)


private fun DrawScope.softFill(path: Path, brush: Brush, alpha: Float, sigma: Float) {
    glow(sigma, 1f) { w, a -> drawPath(path, brush, a * alpha * 2f, Stroke(w)) }
    drawPath(path, brush, alpha)
}

private fun DrawScope.strand(b: Band, dir: Float, t: Float, alpha: Float) {
    val fx = PathEffect.dashPathEffect(b.dash, -dir * b.period * cycle(t, b.dur))
    drawPath(b.path, Doppler, alpha * 0.22f, Stroke(b.width + 2.6f, cap = StrokeCap.Round, pathEffect = fx))
    drawPath(b.path, Doppler, alpha, Stroke(b.width, cap = StrokeCap.Round, pathEffect = fx))
}

internal fun DrawScope.drawBlackHole(t: Float) {
    val pulse = kf(cycle(t, 4f), CssEaseInOut, 0f to 1f, 0.5f to 0.6f, 1f to 1f)
    drawCircle(Halo, 80f, Offset.Zero, pulse)

    rotate(360f * cycle(t, 60f), Offset.Zero) {
        for (s in STARS) {
            val op = kf(cycle(t, 3f, s.delay), CssEaseInOut, 0f to 1f, 0.5f to 0.25f, 1f to 1f)
            drawCircle(Color.White, s.r, Offset(s.x, s.y), op)
        }
    }

    drawCircle(Umbra, 50f, Offset.Zero)
    ring(Color.Black, 50.6f, 1.6f)

    rotate(INCLINATION, Offset.Zero) {
        softFill(TOP_BODY, Lens, 1f, 2.2f)
        softFill(BOTTOM_BODY, Doppler, 0.55f, 2.2f)
        for (b in TOP_ARCS) strand(b, 1f, t, 0.8f)
        for (b in BOTTOM_ARCS) strand(b, -1f, t, 0.8f * 0.55f)

        val photon = kf(cycle(t, 2.2f), CssEaseInOut, 0f to 1f, 0.5f to 0.7f, 1f to 1f)
        glow(1.4f, 1f) { w, a -> ring(PhotonColor, 51.3f, 0.9f + w, a * photon) }
        ring(PhotonColor, 51.3f, 0.9f, photon)

        clipPath(BACK_MASK, ClipOp.Difference) {
            softFill(DISK_BODY, Doppler, 0.85f, 2.2f)
            for (b in DISK) strand(b, 1f, t, 0.85f)

            for (phase in floatArrayOf(0f, 0.5f)) {
                val th = 2f * PI.toFloat() * (t / SPOT_LAP + phase)
                val x = 62f * cos(th)
                val y = 6.8f * sin(th)
                glow(1.4f, 1f) { w, a ->
                    drawOval(SpotColor, Offset(x - 2.4f - w / 2f, y - 1f - w / 2f), Size(4.8f + w, 2f + w), a)
                }
                drawOval(SpotColor, Offset(x - 2.4f, y - 1f), Size(4.8f, 2f))
            }
        }
    }
}