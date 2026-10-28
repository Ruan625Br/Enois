package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

private val rand = SeededRandom(127)

private val DustInk = rgb(0x201E51)
private val Lilac = rgb(0xE9D7FF)
private val Sky = rgb(0xD6F5FF)
private val Spark = rgb(0xFFF1FC)


private val CLOUDS: List<Path> = List(6) { i ->
    val pts = ArrayList<Offset>(98)
    for (side in intArrayOf(-1, 1)) {
        for (j in 0..48) {
            val t = (if (side == -1) j else 48 - j) / 48.0
            val radius = 61 + sin(t * PI * 2 + i) * 3
            val width = sin(t * PI).pow(0.65) * (if (i % 2 == 1) 6 else 10)
            pts += polar(radius + side * width, i * 60 + t * 112)
        }
    }
    Path().apply {
        moveTo(pts[0].x, pts[0].y)
        for (k in 1 until pts.size) lineTo(pts[k].x, pts[k].y)
        close()
    }
}

private class NebulaStar(val x: Float, val y: Float, val r: Float, val highlight: Boolean)

private val STARS = List(26) { i ->
    val p = polar(55 + rand.next() * 22, rand.next() * 360)
    NebulaStar(p.x, p.y, r2(0.3 + rand.next() * 0.65).toFloat(), i % 7 == 0)
}

private val DUST_STROKE = Path().apply {
    moveTo(-62f, 23f); quadraticTo(-66f, -14f, -48f, -33f)
    moveTo(15f, 64f); quadraticTo(43f, 59f, 57f, 36f)
}
private val VEILS_A = Path().apply {
    moveTo(-64f, 24f); cubicTo(-74f, -9f, -44f, -65f, -11f, -62f)
    moveTo(9f, 65f); cubicTo(39f, 68f, 65f, 40f, 64f, 17f)
}
private val VEILS_B = Path().apply {
    moveTo(-60f, 39f); cubicTo(-73f, 17f, -64f, -14f, -52f, -28f)
    moveTo(28f, -58f); cubicTo(54f, -57f, 65f, -35f, 62f, -11f)
}

private val Halo = Brush.radialGradient(
    0.58f to rgb(0x7038C7, 0f), 0.72f to rgb(0x7E40C7, 0.28f),
    0.86f to rgb(0x6668D6, 0.15f), 1f to rgb(0x6654BA, 0f),
    center = Offset.Zero, radius = 80f,
)
private val Edge = Brush.radialGradient(
    0.72f to rgb(0x27133E, 0f), 0.92f to rgb(0x291947, 0.3f), 1f to rgb(0x33204F, 0.75f),
    center = Offset.Zero, radius = 50f,
)

private val Gas = Brush.linearGradient(
    0f to rgb(0x6DA5FF), 0.3f to rgb(0x9B73EC), 0.55f to rgb(0xE47FC7),
    0.78f to rgb(0x777EE6), 1f to rgb(0x65D3ED),
    start = Offset(-65f, -45f), end = Offset(65f, 50f),
)


private val Cutout = Brush.radialGradient(
    0.60f to Color.Transparent, 0.67f to Color.White,
    0.92f to Color.White, 1f to Color.Transparent,
    center = Offset.Zero, radius = 80f,
)

private val LayerBounds = Rect(-80f, -80f, 80f, 80f)
private val LayerPaint = Paint()


internal fun DrawScope.drawNebula(t: Float, still: Boolean = false) {
    drawCircle(Halo, 80f, Offset.Zero)
    drawCircle(Edge, 50f, Offset.Zero)

    drawIntoCanvas { it.saveLayer(LayerBounds, LayerPaint) }

    rotate(if (still) 0f else 360f * cycle(t, 38f), Offset.Zero) {
        for (i in CLOUDS.indices) {
            val a = if (i % 2 == 1) 0.45f else 0.7f
            drawPath(CLOUDS[i], Gas, a * 0.35f, Stroke(1.8f))
            drawPath(CLOUDS[i], Gas, a)
        }
    }

    rotate(if (still) 0f else -360f * cycle(t, 53f), Offset.Zero) {
        for (i in CLOUDS.indices step 2) {
            withTransform({ scale(0.96f, 0.96f, Offset.Zero) }) {
                drawPath(CLOUDS[i], Gas, 0.2f)
            }
        }
        drawPath(DUST_STROKE, DustInk, 0.3f, Stroke(3f, cap = StrokeCap.Round))
    }

    val u = if (still) 0f else kf(cycle(t, 18f), CssEaseInOut, 0f to 0f, 0.5f to 1f, 1f to 0f)
    val rot = if (still) 0f else -5f + 13f * u
    val sc = if (still) 1f else 0.98f + 0.045f * u
    val op = if (still) 0.65f else 0.35f + 0.45f * u
    withTransform({
        rotate(rot, Offset.Zero)
        scale(sc, sc, Offset.Zero)
    }) {
        drawPath(VEILS_A, Gas, op, Stroke(1.4f, cap = StrokeCap.Round))
        drawPath(VEILS_B, Gas, op, Stroke(0.65f, cap = StrokeCap.Round))
    }

    drawCircle(Cutout, 80f, Offset.Zero, blendMode = BlendMode.DstIn)
    drawIntoCanvas { it.restore() }

    for (group in 0..1) {
        val gop = if (still) 1f else kf(
            cycle(t, 4.8f, -group * 2.4f), CssEaseInOut, 0f to 1f, 0.5f to 0.35f, 1f to 1f,
        )
        var k = 0
        for (i in STARS.indices) {
            if (i % 2 != group) continue
            val s = STARS[i]
            val color = if (k % 2 == 1) Lilac else Sky
            val c = Offset(s.x, s.y)
            drawCircle(color, s.r + 0.9f, c, 0.25f * gop)
            drawCircle(color, s.r, c, gop)
            if (s.highlight) {
                drawLine(
                    Spark,
                    Offset(s.x - 2.4f, s.y),
                    Offset(s.x + 2.4f, s.y),
                    strokeWidth = 0.55f,
                    alpha = gop
                )
                drawLine(
                    Spark,
                    Offset(s.x, s.y - 2.4f),
                    Offset(s.x, s.y + 2.4f),
                    strokeWidth = 0.55f,
                    alpha = gop
                )
            }
            k++
        }
    }
}