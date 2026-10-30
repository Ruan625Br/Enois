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
public val support_agent: ImageVector
  get() {
    if (_support_agent != null) {
      return _support_agent!!
    }
    _support_agent =
      ImageVector.Builder(
          name = "support_agent",
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
            moveTo(12f, 21f)
            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
            quadTo(11f, 20.43f, 11f, 20f)
            reflectiveQuadToRelative(0.29f, -0.71f)
            reflectiveQuadTo(12f, 19f)
            horizontalLineToRelative(7f)
            verticalLineTo(11.9f)
            quadTo(19f, 8.98f, 16.96f, 6.94f)
            reflectiveQuadTo(12f, 4.9f)
            quadTo(9.08f, 4.9f, 7.04f, 6.94f)
            reflectiveQuadTo(5f, 11.9f)
            verticalLineTo(17f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(4f, 18f)
            quadTo(3.18f, 18f, 2.59f, 17.41f)
            reflectiveQuadTo(2f, 16f)
            verticalLineTo(14f)
            quadTo(2f, 13.48f, 2.26f, 13.01f)
            reflectiveQuadTo(3f, 12.27f)
            lineTo(3.08f, 10.95f)
            quadTo(3.28f, 9.25f, 4.06f, 7.8f)
            reflectiveQuadTo(6.04f, 5.27f)
            reflectiveQuadTo(8.76f, 3.6f)
            reflectiveQuadTo(12f, 3f)
            reflectiveQuadToRelative(3.23f, 0.6f)
            reflectiveQuadToRelative(2.72f, 1.66f)
            reflectiveQuadToRelative(1.98f, 2.51f)
            quadToRelative(0.78f, 1.45f, 1f, 3.15f)
            lineTo(21f, 12.23f)
            quadToRelative(0.48f, 0.22f, 0.74f, 0.67f)
            reflectiveQuadTo(22f, 13.85f)
            verticalLineToRelative(2.3f)
            quadToRelative(0f, 0.5f, -0.26f, 0.95f)
            reflectiveQuadTo(21f, 17.77f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(19f, 21f)
            horizontalLineTo(12f)
            close()
            moveTo(8.29f, 13.71f)
            quadTo(8f, 13.43f, 8f, 13f)
            reflectiveQuadTo(8.29f, 12.29f)
            quadTo(8.58f, 12f, 9f, 12f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(10f, 13f)
            reflectiveQuadTo(9.71f, 13.71f)
            reflectiveQuadTo(9f, 14f)
            quadTo(8.58f, 14f, 8.29f, 13.71f)
            close()
            moveToRelative(6f, 0f)
            quadTo(14f, 13.43f, 14f, 13f)
            reflectiveQuadToRelative(0.29f, -0.71f)
            reflectiveQuadTo(15f, 12f)
            reflectiveQuadToRelative(0.71f, 0.29f)
            reflectiveQuadTo(16f, 13f)
            reflectiveQuadToRelative(-0.29f, 0.71f)
            reflectiveQuadTo(15f, 14f)
            reflectiveQuadTo(14.29f, 13.71f)
            close()
            moveTo(6.03f, 12.45f)
            quadTo(5.85f, 9.8f, 7.63f, 7.9f)
            reflectiveQuadTo(12.05f, 6f)
            quadToRelative(2.22f, 0f, 3.91f, 1.41f)
            reflectiveQuadTo(18f, 11.02f)
            quadTo(15.73f, 11f, 13.81f, 9.8f)
            reflectiveQuadTo(10.88f, 6.55f)
            quadToRelative(-0.4f, 2f, -1.69f, 3.56f)
            reflectiveQuadTo(6.03f, 12.45f)
            close()
          }
        }
        .build()
    return _support_agent!!
  }

private var _support_agent: ImageVector? = null