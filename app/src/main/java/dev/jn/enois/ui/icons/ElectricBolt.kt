package dev.jn.enois.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val electric_bolt: ImageVector
  get() {
    if (_electric_bolt != null) {
      return _electric_bolt!!
    }
    _electric_bolt =
      ImageVector.Builder(
          name = "electric_bolt",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(7f, 22f)
            lineToRelative(4f, -7.5f)
            lineToRelative(-8f, -1f)
            lineTo(15f, 2f)
            horizontalLineToRelative(2f)
            lineTo(13f, 9.5f)
            lineToRelative(8f, 1f)
            lineTo(9f, 22f)
            horizontalLineTo(7f)
            close()
            moveToRelative(5.55f, -6.18f)
            lineToRelative(4.02f, -3.85f)
            lineTo(9.85f, 11.13f)
            lineTo(11.43f, 8.2f)
            lineToRelative(-4f, 3.85f)
            lineToRelative(6.7f, 0.83f)
            lineToRelative(-1.57f, 2.95f)
            close()
            moveTo(12f, 12f)
            close()
          }
        }
        .build()
    return _electric_bolt!!
  }

private var _electric_bolt: ImageVector? = null