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
public val inbox: ImageVector
  get() {
    if (_inbox != null) {
      return _inbox!!
    }
    _inbox =
      ImageVector.Builder(
          name = "inbox",
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
            moveTo(5f, 21f)
            quadTo(4.18f, 21f, 3.59f, 20.41f)
            reflectiveQuadTo(3f, 19f)
            verticalLineTo(5f)
            quadTo(3f, 4.17f, 3.59f, 3.59f)
            reflectiveQuadTo(5f, 3f)
            horizontalLineTo(19f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(21f, 5f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(19f, 21f)
            horizontalLineTo(5f)
            close()
            moveTo(5f, 19f)
            horizontalLineTo(19f)
            verticalLineTo(16f)
            horizontalLineTo(16f)
            quadToRelative(-0.75f, 0.95f, -1.79f, 1.48f)
            reflectiveQuadTo(12f, 18f)
            reflectiveQuadTo(9.79f, 17.48f)
            reflectiveQuadTo(8f, 16f)
            horizontalLineTo(5f)
            verticalLineToRelative(3f)
            close()
            moveToRelative(7f, -3f)
            quadToRelative(0.8f, 0f, 1.48f, -0.41f)
            reflectiveQuadToRelative(1.1f, -1.09f)
            quadToRelative(0.15f, -0.23f, 0.38f, -0.36f)
            reflectiveQuadTo(15.45f, 14f)
            horizontalLineTo(19f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            verticalLineToRelative(9f)
            horizontalLineTo(8.55f)
            quadToRelative(0.28f, 0f, 0.5f, 0.14f)
            reflectiveQuadTo(9.43f, 14.5f)
            quadToRelative(0.43f, 0.67f, 1.1f, 1.09f)
            reflectiveQuadTo(12f, 16f)
            close()
            moveTo(5f, 19f)
            horizontalLineTo(8f)
            quadToRelative(0.75f, 0f, 1.79f, 0f)
            reflectiveQuadTo(12f, 19f)
            reflectiveQuadToRelative(2.21f, 0f)
            reflectiveQuadTo(16f, 19f)
            horizontalLineToRelative(3f)
            horizontalLineTo(5f)
            close()
          }
        }
        .build()
    return _inbox!!
  }

private var _inbox: ImageVector? = null