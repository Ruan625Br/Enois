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
public val heart_smile: ImageVector
  get() {
    if (_heart_smile != null) {
      return _heart_smile!!
    }
    _heart_smile =
      ImageVector.Builder(
          name = "heart_smile",
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
            moveTo(14.8f, 14.53f)
            quadTo(16.03f, 13.55f, 16.38f, 12f)
            horizontalLineTo(14.3f)
            quadTo(14f, 12.68f, 13.38f, 13.09f)
            reflectiveQuadTo(12f, 13.5f)
            reflectiveQuadTo(10.63f, 13.09f)
            quadTo(10f, 12.68f, 9.7f, 12f)
            horizontalLineTo(7.63f)
            quadToRelative(0.35f, 1.55f, 1.58f, 2.53f)
            reflectiveQuadTo(12f, 15.5f)
            reflectiveQuadToRelative(2.8f, -0.97f)
            close()
            moveTo(10.14f, 10.14f)
            quadTo(10.5f, 9.77f, 10.5f, 9.25f)
            reflectiveQuadTo(10.14f, 8.36f)
            reflectiveQuadTo(9.25f, 8f)
            reflectiveQuadTo(8.36f, 8.36f)
            quadTo(8f, 8.73f, 8f, 9.25f)
            quadToRelative(0f, 0.52f, 0.36f, 0.89f)
            reflectiveQuadTo(9.25f, 10.5f)
            reflectiveQuadToRelative(0.89f, -0.36f)
            close()
            moveToRelative(5.5f, 0f)
            quadTo(16f, 9.77f, 16f, 9.25f)
            reflectiveQuadTo(15.64f, 8.36f)
            reflectiveQuadTo(14.75f, 8f)
            reflectiveQuadTo(13.86f, 8.36f)
            quadTo(13.5f, 8.73f, 13.5f, 9.25f)
            quadToRelative(0f, 0.52f, 0.36f, 0.89f)
            reflectiveQuadToRelative(0.89f, 0.36f)
            quadToRelative(0.53f, 0f, 0.89f, -0.36f)
            close()
            moveTo(12f, 21f)
            lineTo(10.55f, 19.75f)
            quadTo(8.03f, 17.55f, 6.38f, 15.95f)
            quadTo(4.73f, 14.35f, 3.75f, 13.08f)
            quadTo(2.78f, 11.8f, 2.39f, 10.73f)
            reflectiveQuadTo(2f, 8.5f)
            quadTo(2f, 6.15f, 3.58f, 4.57f)
            reflectiveQuadTo(7.5f, 3f)
            quadTo(8.8f, 3f, 9.98f, 3.55f)
            reflectiveQuadTo(12f, 5.1f)
            quadToRelative(0.85f, -1f, 2.03f, -1.55f)
            reflectiveQuadTo(16.5f, 3f)
            quadToRelative(2.35f, 0f, 3.93f, 1.57f)
            reflectiveQuadTo(22f, 8.5f)
            quadToRelative(0f, 1.15f, -0.39f, 2.22f)
            reflectiveQuadToRelative(-1.36f, 2.35f)
            quadToRelative(-0.97f, 1.28f, -2.63f, 2.88f)
            quadToRelative(-1.65f, 1.6f, -4.17f, 3.8f)
            lineTo(12f, 21f)
            close()
            moveToRelative(0f, -2.7f)
            quadToRelative(2.4f, -2.07f, 3.95f, -3.53f)
            reflectiveQuadTo(18.4f, 12.21f)
            reflectiveQuadToRelative(1.25f, -1.97f)
            quadTo(20f, 9.38f, 20f, 8.5f)
            quadTo(20f, 7f, 19f, 6f)
            reflectiveQuadTo(16.5f, 5f)
            quadTo(15.33f, 5f, 14.33f, 5.66f)
            reflectiveQuadTo(12.95f, 7.35f)
            horizontalLineToRelative(-1.9f)
            quadTo(10.68f, 6.32f, 9.68f, 5.66f)
            reflectiveQuadTo(7.5f, 5f)
            quadTo(6f, 5f, 5f, 6f)
            reflectiveQuadTo(4f, 8.5f)
            quadToRelative(0f, 0.88f, 0.35f, 1.74f)
            reflectiveQuadTo(5.6f, 12.21f)
            quadToRelative(0.9f, 1.11f, 2.45f, 2.56f)
            reflectiveQuadTo(12f, 18.3f)
            close()
            moveToRelative(0f, -6.65f)
            close()
          }
        }
        .build()
    return _heart_smile!!
  }

private var _heart_smile: ImageVector? = null