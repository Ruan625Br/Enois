package dev.jn.enois.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import dev.mttmcknn.seaglass.SeaGlassPalette
import dev.mttmcknn.seaglass.SeaGlassStyle
import dev.mttmcknn.seaglass.SeaGlassTheme

enum class EnoisGlass(val light: SeaGlassPalette, val dark: SeaGlassPalette) {
    Mint(SeaGlassPalette.Mint, SeaGlassPalette.MintDark),
    Lilac(SeaGlassPalette.Lilac, SeaGlassPalette.LilacDark),
    Rose(SeaGlassPalette.Rose, SeaGlassPalette.RoseDark),
    Lagoon(SeaGlassPalette.Lagoon, SeaGlassPalette.LagoonDark),
}

fun SeaGlassPalette.toColorScheme(dark: Boolean): ColorScheme {
    val black = Color.Black
    val white = Color.White
    return if (dark) {
        darkColorScheme(
            primary = edge,
            onPrimary = lerp(body, black, .65f),
            primaryContainer = body,
            onPrimaryContainer = ink,
            secondary = lerp(edge, body, .45f),
            onSecondary = lerp(body, black, .65f),
            secondaryContainer = lerp(body, black, .35f),
            onSecondaryContainer = ink,
            tertiary = lerp(edge, ink, .4f),
            onTertiary = lerp(body, black, .65f),
            tertiaryContainer = lerp(body, edge, .25f),
            onTertiaryContainer = ink,
            background = lerp(body, black, .84f),
            onBackground = ink,
            surface = lerp(body, black, .84f),
            onSurface = ink,
            surfaceVariant = lerp(body, black, .62f),
            onSurfaceVariant = lerp(ink, body, .3f),
            surfaceContainerLowest = lerp(body, black, .90f),
            surfaceContainerLow = lerp(body, black, .80f),
            surfaceContainer = lerp(body, black, .74f),
            surfaceContainerHigh = lerp(body, black, .68f),
            surfaceContainerHighest = lerp(body, black, .62f),
            outline = lerp(edge, body, .5f),
            outlineVariant = lerp(body, black, .35f),
        )
    } else {
        val onSurface = lerp(ink, black, .55f)
        lightColorScheme(
            primary = ink,
            onPrimary = white,
            primaryContainer = body,
            onPrimaryContainer = lerp(ink, black, .35f),
            secondary = lerp(ink, body, .35f),
            onSecondary = white,
            secondaryContainer = lerp(body, white, .45f),
            onSecondaryContainer = onSurface,
            tertiary = lerp(ink, edge, .3f),
            onTertiary = white,
            tertiaryContainer = lerp(edge, body, .5f),
            onTertiaryContainer = onSurface,
            background = lerp(body, white, .88f),
            onBackground = onSurface,
            surface = lerp(body, white, .88f),
            onSurface = onSurface,
            surfaceVariant = lerp(body, white, .6f),
            onSurfaceVariant = lerp(ink, black, .25f),
            surfaceContainerLowest = white,
            surfaceContainerLow = lerp(body, white, .82f),
            surfaceContainer = lerp(body, white, .76f),
            surfaceContainerHigh = lerp(body, white, .70f),
            surfaceContainerHighest = lerp(body, white, .64f),
            outline = lerp(ink, body, .45f),
            outlineVariant = lerp(body, white, .35f),
        )
    }
}

@Composable
fun EnoisTheme(
    glass: EnoisGlass = EnoisGlass.Mint,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) glass.dark else glass.light
    val colorScheme = remember(palette, darkTheme) { palette.toColorScheme(darkTheme) }

    MaterialTheme(colorScheme = colorScheme, typography = Typography) {
        SeaGlassTheme(
            style = if (darkTheme) SeaGlassStyle.ReferenceDark else SeaGlassStyle.Reference,
            palette = palette,
            content = content,
        )
    }
}