package dev.jn.enois.presentation.components.styledname

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

internal sealed interface PlateBackground {
    fun DrawScope.paint()
}

internal class HGradient(private vararg val stops: Pair<Float, Color>) : PlateBackground {
    override fun DrawScope.paint() =
        drawRect(Brush.horizontalGradient(*stops, startX = 0f, endX = size.width))
}

internal fun hGradient(colors: List<Color>) =
    HGradient(*colors.mapIndexed { i, c -> i / (colors.size - 1f) to c }.toTypedArray())

internal class DiagonalStripes(val a: Color, val b: Color, val periodDp: Float) : PlateBackground {
    override fun DrawScope.paint() {
        val d = periodDp.dp.toPx() * 0.70710677f
        drawRect(
            Brush.linearGradient(
                0f to a, 0.5f to a, 0.5f to b, 1f to b,
                start = Offset.Zero, end = Offset(d, d), tileMode = TileMode.Repeated,
            )
        )
    }
}

internal class Ruled(
    val base: Color,
    val line: Color,
    val horizontal: Boolean,
    val vertical: Boolean,
) : PlateBackground {
    override fun DrawScope.paint() {
        drawRect(base)
        val step = 10.dp.toPx()
        val t = 1.dp.toPx()
        if (horizontal) {
            var y = size.height - step
            while (y > -step) {
                drawRect(line, Offset(0f, y), Size(size.width, t)); y -= step
            }
        }
        if (vertical) {
            var x = 9.dp.toPx()
            while (x < size.width) {
                drawRect(line, Offset(x, 0f), Size(t, size.height)); x += step
            }
        }
    }
}

internal object Chita : PlateBackground {
    override fun DrawScope.paint() {
        drawRect(Brush.horizontalGradient(listOf(Color(0xFFDC2626), Color(0xFFB91C1C))))
        drawCircle(Color(0xFFF472B6), 6.dp.toPx(), Offset(size.width * 0.2f, size.height * 0.5f))
        drawCircle(Color(0xFFFACC15), 5.dp.toPx(), Offset(size.width * 0.6f, size.height * 0.3f))
    }
}