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
public val remove_moderator: ImageVector
  get() {
    if (_remove_moderator != null) {
      return _remove_moderator!!
    }
    _remove_moderator =
      ImageVector.Builder(
          name = "remove_moderator",
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
            moveTo(20f, 6.38f)
            verticalLineTo(11.1f)
            quadToRelative(0f, 0.88f, -0.14f, 1.74f)
            reflectiveQuadToRelative(-0.41f, 1.71f)
            quadToRelative(-0.18f, 0.53f, -0.55f, 0.69f)
            reflectiveQuadToRelative(-0.73f, 0.04f)
            reflectiveQuadTo(17.59f, 14.84f)
            reflectiveQuadTo(17.5f, 14.08f)
            quadToRelative(0.25f, -0.7f, 0.38f, -1.46f)
            reflectiveQuadTo(18f, 11.1f)
            verticalLineTo(6.38f)
            lineTo(12f, 4.13f)
            lineTo(8.95f, 5.27f)
            quadTo(8.68f, 5.38f, 8.39f, 5.31f)
            reflectiveQuadTo(7.9f, 5.05f)
            quadTo(7.5f, 4.65f, 7.63f, 4.13f)
            reflectiveQuadTo(8.25f, 3.4f)
            lineTo(11.3f, 2.27f)
            quadTo(11.65f, 2.15f, 12f, 2.15f)
            reflectiveQuadToRelative(0.7f, 0.13f)
            lineToRelative(6f, 2.25f)
            quadToRelative(0.58f, 0.23f, 0.94f, 0.73f)
            reflectiveQuadTo(20f, 6.38f)
            close()
            moveTo(12f, 21.9f)
            quadToRelative(-0.1f, 0f, -0.63f, -0.1f)
            quadTo(8f, 20.68f, 6f, 17.64f)
            reflectiveQuadTo(4f, 11.1f)
            verticalLineTo(6.8f)
            lineTo(2.1f, 4.9f)
            quadTo(1.83f, 4.63f, 1.83f, 4.2f)
            reflectiveQuadTo(2.1f, 3.5f)
            quadTo(2.38f, 3.22f, 2.8f, 3.22f)
            reflectiveQuadTo(3.5f, 3.5f)
            lineToRelative(17f, 17f)
            quadToRelative(0.28f, 0.27f, 0.28f, 0.7f)
            reflectiveQuadTo(20.5f, 21.9f)
            quadToRelative(-0.27f, 0.28f, -0.7f, 0.28f)
            reflectiveQuadTo(19.1f, 21.9f)
            lineTo(16.55f, 19.35f)
            quadToRelative(-0.82f, 0.88f, -1.81f, 1.47f)
            quadToRelative(-0.99f, 0.6f, -2.11f, 0.98f)
            quadToRelative(-0.15f, 0.05f, -0.3f, 0.07f)
            reflectiveQuadTo(12f, 21.9f)
            close()
            moveTo(10.58f, 13.38f)
            close()
            moveTo(12.85f, 10f)
            close()
            moveTo(12f, 19.9f)
            quadToRelative(0.88f, -0.27f, 1.68f, -0.77f)
            reflectiveQuadToRelative(1.47f, -1.18f)
            lineTo(6f, 8.8f)
            verticalLineToRelative(2.3f)
            quadToRelative(0f, 3.03f, 1.7f, 5.5f)
            reflectiveQuadTo(12f, 19.9f)
            close()
          }
        }
        .build()
    return _remove_moderator!!
  }

private var _remove_moderator: ImageVector? = null