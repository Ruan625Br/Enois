package dev.jn.enois.presentation.components.avatar.fx

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin


internal class SeededRandom(seed: Int) {
    private var a = seed

    fun next(): Double {
        a += 0x6d2b79f5
        var t = a
        t = (t xor (t ushr 15)) * (t or 1)
        t = t xor (t + (t xor (t ushr 7)) * (t or 61))
        return ((t xor (t ushr 14)).toLong() and 0xFFFFFFFFL).toDouble() / 4294967296.0
    }
}

internal fun r2(n: Double): Double = floor(n * 100.0 + 0.5) / 100.0

internal fun polar(radius: Double, degrees: Double): Offset {
    val rad = degrees * PI / 180.0
    return Offset(r2(radius * sin(rad)).toFloat(), r2(-radius * cos(rad)).toFloat())
}

internal fun rgb(hex: Int, alpha: Float = 1f): Color = Color(
    red = ((hex shr 16) and 0xFF) / 255f,
    green = ((hex shr 8) and 0xFF) / 255f,
    blue = (hex and 0xFF) / 255f,
    alpha = alpha,
)

internal val CssEaseInOut: Easing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
internal val CssEaseOut: Easing = CubicBezierEasing(0f, 0f, 0.58f, 1f)
internal val EaseSine: Easing = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)
internal val EaseDrop: Easing = CubicBezierEasing(0.55f, 0f, 0.85f, 0.4f)

internal fun cycle(t: Float, duration: Float, delay: Float = 0f): Float {
    val x = (t - delay) / duration
    return x - floor(x)
}

internal fun kf(p: Float, easing: Easing, vararg s: Pair<Float, Float>): Float {
    if (p <= s.first().first) return s.first().second
    for (i in 0 until s.size - 1) {
        val (o0, v0) = s[i]
        val (o1, v1) = s[i + 1]
        if (p <= o1) {
            val f = if (o1 == o0) 1f else (p - o0) / (o1 - o0)
            return v0 + (v1 - v0) * easing.transform(f)
        }
    }
    return s.last().second
}

internal fun vGrad(bottom: Float, top: Float, vararg stops: Pair<Float, Color>): Brush =
    Brush.linearGradient(*stops, start = Offset(0f, bottom), end = Offset(0f, top))


internal inline fun glow(sigma: Float, strength: Float = 1f, draw: (extra: Float, alpha: Float) -> Unit) {
    draw(sigma * 4.0f, 0.10f * strength)
    draw(sigma * 2.4f, 0.16f * strength)
    draw(sigma * 1.1f, 0.24f * strength)
}


internal fun DrawScope.ring(color: Color, r: Float, w: Float, alpha: Float = 1f) =
    drawCircle(color, r, Offset.Zero, alpha, Stroke(w))