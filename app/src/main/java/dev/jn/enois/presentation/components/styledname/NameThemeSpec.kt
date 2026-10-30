package dev.jn.enois.presentation.components.styledname

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import dev.jn.enois.domain.model.ProfileNameFont

internal class NamePaint(val vertical: Boolean, val stops: List<Pair<Float, Color>>)

internal data class NameThemeSpec(
    val font: ProfileNameFont,
    val colors: List<Color>,
    val paint: NamePaint? = null,
    val fontFamily: FontFamily? = null,
    val fontScale: Float = 1f,
    val letterSpacingEm: Float = 0f,
    val shadows: List<NameShadow> = emptyList(),
    val motion: NameMotion = NameMotion.None,
)

internal fun evenStops(colors: List<Color>): List<Pair<Float, Color>> =
    colors.mapIndexed { i, c -> i / (colors.size - 1f) to c }