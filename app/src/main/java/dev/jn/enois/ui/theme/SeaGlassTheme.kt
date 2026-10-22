package dev.jn.enois.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.mttmcknn.seaglass.*

val SeaGlassPalette.Companion.MintDark: SeaGlassPalette get() = SeaGlassPalette(
    body = Color(0xFF2B5A47),
    edge = Color(0xFF7FD1B0),
    ink  = Color(0xFFD9F7E8),
)

val SeaGlassPalette.Companion.LilacDark: SeaGlassPalette get() = SeaGlassPalette(
    body = Color(0xFF4B3770),
    edge = Color(0xFFB79CEB),
    ink  = Color(0xFFEFE3FF),
)

val SeaGlassPalette.Companion.LagoonDark: SeaGlassPalette get() = SeaGlassPalette(
    body = Color(0xFF2A4F6E), edge = Color(0xFF7DB8E8), ink = Color(0xFFDCEEFF),
)
val SeaGlassPalette.Companion.RoseDark: SeaGlassPalette get() = SeaGlassPalette(
    body = Color(0xFF6B3345), edge = Color(0xFFE59AB0), ink = Color(0xFFFFE4EC),
)

val SeaGlassStyle.Companion.ReferenceDark: SeaGlassStyle get() = SeaGlassStyle(
    frost = 0.42f,
    grain = 0.48f,
    tumble = 0.025f,
    thickness = 6.dp,
    bulge = 0.65f,
    elevation = 10.dp,
    lightDegrees = 315f,
    transmission = 0.72f,
)

@Composable
fun AppSeaGlassTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    SeaGlassTheme(
        style = if (dark) SeaGlassStyle.ReferenceDark else SeaGlassStyle.Reference,
        palette = if (dark) SeaGlassPalette.RoseDark else SeaGlassPalette.Mint,
        content = content,
    )
}