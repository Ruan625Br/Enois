package dev.jn.enois.presentation.components.styledname

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import dev.jn.enois.domain.model.ProfileNameFont
import dev.jn.enois.ui.theme.fontProvider


private fun googleFontFamily(name: String) = FontFamily(
    Font(
        googleFont = GoogleFont(name),
        fontProvider = fontProvider
    )
)

private val pressStart2PFontFamily = googleFontFamily("Press Start 2P")
private val unifrakturCookFontFamily = googleFontFamily("UnifrakturCook")
private val antonFontFamily = googleFontFamily("Anton")
private val comicNeueFontFamily = googleFontFamily("Comic Neue")
private val robotoSlabFontFamily = googleFontFamily("Roboto Slab")

private val condensedFontFamily =
    FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))


internal fun ProfileNameFont.family(): FontFamily = when (this) {
    ProfileNameFont.SYSTEM -> FontFamily.Default
    ProfileNameFont.SERIF -> FontFamily.Serif
    ProfileNameFont.MONO -> FontFamily.Monospace
    ProfileNameFont.ROUNDED -> FontFamily.SansSerif
    ProfileNameFont.DISPLAY -> antonFontFamily
    ProfileNameFont.PIXEL -> pressStart2PFontFamily
    ProfileNameFont.GOTHIC -> unifrakturCookFontFamily
    ProfileNameFont.CURSIVE -> FontFamily.Cursive
    ProfileNameFont.CONDENSED -> condensedFontFamily
    ProfileNameFont.COMIC -> comicNeueFontFamily
    ProfileNameFont.SLAB -> robotoSlabFontFamily
}

internal object SeasonalFonts {

    private fun googleFontFamily(
        name: String,
        weight: FontWeight = FontWeight.Normal
    ) = FontFamily(
        Font(
            googleFont = GoogleFont(name),
            fontProvider = fontProvider,
            weight = weight
        )
    )

    val Creepster = googleFontFamily("Creepster")

    val MountainsOfChristmas = googleFontFamily(
        "Mountains of Christmas",
        FontWeight.Bold
    )

    val Pacifico = googleFontFamily("Pacifico")

    val Rye = googleFontFamily("Rye")

    val GochiHand = googleFontFamily("Gochi Hand")
}