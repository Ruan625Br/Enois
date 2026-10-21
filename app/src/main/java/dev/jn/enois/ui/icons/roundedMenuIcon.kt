package dev.jn.enois.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

val AppIcons.roundedMenuIcon by lazy {
    ImageVector.Builder(
        name = "Rounded.Menu",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString(
                "M4,18L20,18c0.55,0 1,-0.45 1,-1s-0.45,-1 -1,-1L4,16c-0.55,0 -1,0.45 -1,1s0.45,1 1,1zM4,13L20,13c0.55,0 1,-0.45 1,-1s-0.45,-1 -1,-1L4,11c-0.55,0 -1,0.45 -1,1s0.45,1 1,1zM3,7c0,0.55 0.45,1 1,1h16c0.55,0 1,-0.45 1,-1s-0.45,-1 -1,-1L4,6c-0.55,0 -1,0.45 -1,1z",
            ).toNodes(),
            fill = SolidColor(Color.Black),
        )
    }.build()
}

