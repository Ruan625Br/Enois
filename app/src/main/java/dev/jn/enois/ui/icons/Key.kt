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
public val key: ImageVector
  get() {
    if (_key != null) {
      return _key!!
    }
    _key =
      ImageVector.Builder(
          name = "key",
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
            moveTo(5.59f, 13.41f)
            quadTo(5f, 12.83f, 5f, 12f)
            reflectiveQuadTo(5.59f, 10.59f)
            reflectiveQuadTo(7f, 10f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            quadTo(9f, 11.18f, 9f, 12f)
            reflectiveQuadTo(8.41f, 13.41f)
            quadTo(7.83f, 14f, 7f, 14f)
            reflectiveQuadTo(5.59f, 13.41f)
            close()
            moveTo(7f, 18f)
            quadTo(4.5f, 18f, 2.75f, 16.25f)
            reflectiveQuadTo(1f, 12f)
            reflectiveQuadTo(2.75f, 7.75f)
            reflectiveQuadTo(7f, 6f)
            quadToRelative(1.68f, 0f, 3.04f, 0.82f)
            reflectiveQuadTo(12.2f, 9f)
            horizontalLineToRelative(8.38f)
            quadToRelative(0.2f, 0f, 0.39f, 0.07f)
            reflectiveQuadTo(21.3f, 9.3f)
            lineToRelative(2f, 2f)
            quadToRelative(0.15f, 0.15f, 0.21f, 0.33f)
            reflectiveQuadTo(23.58f, 12f)
            reflectiveQuadToRelative(-0.06f, 0.38f)
            reflectiveQuadTo(23.3f, 12.7f)
            lineToRelative(-3.18f, 3.18f)
            quadTo(20f, 16f, 19.83f, 16.08f)
            reflectiveQuadToRelative(-0.35f, 0.1f)
            reflectiveQuadTo(19.13f, 16.15f)
            reflectiveQuadTo(18.8f, 15.98f)
            lineTo(17.5f, 15f)
            lineToRelative(-1.43f, 1.07f)
            quadToRelative(-0.12f, 0.1f, -0.27f, 0.15f)
            quadToRelative(-0.15f, 0.05f, -0.3f, 0.05f)
            reflectiveQuadTo(15.19f, 16.23f)
            quadTo(15.03f, 16.18f, 14.9f, 16.08f)
            lineTo(13.38f, 15f)
            horizontalLineTo(12.2f)
            quadToRelative(-0.8f, 1.35f, -2.16f, 2.18f)
            reflectiveQuadTo(7f, 18f)
            close()
            moveTo(7f, 16f)
            quadToRelative(1.4f, 0f, 2.46f, -0.85f)
            reflectiveQuadTo(10.88f, 13f)
            horizontalLineTo(14f)
            lineToRelative(1.45f, 1.02f)
            quadToRelative(0f, 0f, 0f, 0.01f)
            reflectiveQuadToRelative(0f, -0.01f)
            lineTo(17.5f, 12.5f)
            lineToRelative(1.78f, 1.38f)
            lineTo(21.15f, 12f)
            quadToRelative(-0.03f, 0f, -0.01f, 0f)
            reflectiveQuadToRelative(0.01f, 0f)
            lineToRelative(-1f, -1f)
            quadToRelative(0f, -0.03f, 0f, -0.01f)
            reflectiveQuadToRelative(0f, 0.01f)
            horizontalLineTo(10.88f)
            quadTo(10.53f, 9.7f, 9.46f, 8.85f)
            reflectiveQuadTo(7f, 8f)
            quadTo(5.35f, 8f, 4.18f, 9.17f)
            reflectiveQuadTo(3f, 12f)
            reflectiveQuadToRelative(1.17f, 2.82f)
            reflectiveQuadTo(7f, 16f)
            close()
          }
        }
        .build()
    return _key!!
  }

private var _key: ImageVector? = null