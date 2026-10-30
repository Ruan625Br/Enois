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
public val hardware: ImageVector
  get() {
    if (_hardware != null) {
      return _hardware!!
    }
    _hardware =
      ImageVector.Builder(
          name = "hardware",
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
            moveTo(10f, 21f)
            quadTo(9.58f, 21f, 9.29f, 20.71f)
            quadTo(9f, 20.43f, 9f, 20f)
            verticalLineTo(8f)
            horizontalLineTo(4f)
            quadTo(4f, 5.93f, 5.46f, 4.46f)
            reflectiveQuadTo(9f, 3f)
            horizontalLineToRelative(6f)
            verticalLineTo(6f)
            lineTo(18f, 3f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(8f)
            horizontalLineTo(18f)
            lineTo(15f, 8f)
            verticalLineTo(20f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(14f, 21f)
            horizontalLineTo(10f)
            close()
            moveToRelative(1f, -2f)
            horizontalLineToRelative(2f)
            verticalLineTo(13f)
            horizontalLineTo(12f)
            horizontalLineTo(11f)
            verticalLineToRelative(6f)
            close()
            moveToRelative(0f, -8f)
            horizontalLineToRelative(2f)
            verticalLineTo(5f)
            horizontalLineTo(9f)
            quadTo(8.35f, 5f, 7.78f, 5.26f)
            reflectiveQuadTo(6.78f, 6f)
            horizontalLineTo(11f)
            verticalLineToRelative(5f)
            close()
            moveToRelative(1f, 1f)
            close()
          }
        }
        .build()
    return _hardware!!
  }

private var _hardware: ImageVector? = null