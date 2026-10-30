package dev.jn.enois.presentation.components.styledname

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.graphics.Color
import dev.jn.enois.domain.model.FreeProfileNameTheme
import dev.jn.enois.domain.model.FreeProfileNameTheme.CANDY
import dev.jn.enois.domain.model.FreeProfileNameTheme.DEFAULT
import dev.jn.enois.domain.model.FreeProfileNameTheme.GLITCH
import dev.jn.enois.domain.model.FreeProfileNameTheme.GOLD
import dev.jn.enois.domain.model.FreeProfileNameTheme.GOTHIC
import dev.jn.enois.domain.model.FreeProfileNameTheme.ICE
import dev.jn.enois.domain.model.FreeProfileNameTheme.MATRIX
import dev.jn.enois.domain.model.FreeProfileNameTheme.NEON
import dev.jn.enois.domain.model.FreeProfileNameTheme.OCEAN
import dev.jn.enois.domain.model.FreeProfileNameTheme.PIXEL
import dev.jn.enois.domain.model.FreeProfileNameTheme.RETRO
import dev.jn.enois.domain.model.FreeProfileNameTheme.SUNSET
import dev.jn.enois.domain.model.ProfileNameFont
import dev.jn.enois.domain.model.ProfileNameTheme
import dev.jn.enois.domain.model.SeasonalNameTheme

internal data class NameShadow(
    val color: Color,
    val dx: Float = 0f,
    val dy: Float = 0f,
    val blur: Float = 0f,
)

internal sealed interface NameMotion {
    val durationMs: Int

    data object None : NameMotion {
        override val durationMs = 0
    }

    data class Pan(
        override val durationMs: Int,
        val scale: Float,
        val pingPong: Boolean,
    ) : NameMotion

    data object Glitch : NameMotion {
        override val durationMs = 2400
    }
}

private val EaseInOut = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private const val SHIMMER_TRAVEL = 1.8f

internal fun NameMotion.Pan.fraction(p: Float): Float =
    if (pingPong) EaseInOut.transform(if (p < 0.5f) p * 2f else 2f - p * 2f)
    else SHIMMER_TRAVEL * p


private fun rgba(r: Int, g: Int, b: Int, a: Float) = Color(r / 255f, g / 255f, b / 255f, a)

internal object NameThemes {
    private val default = NameThemeSpec(ProfileNameFont.SYSTEM, listOf(Color(0xFFFF7A18)))

    private val free: Map<FreeProfileNameTheme, NameThemeSpec> = mapOf(
        DEFAULT to default,
        PIXEL to NameThemeSpec(
            ProfileNameFont.PIXEL, listOf(Color(0xFF9CFF57), Color(0xFF36D98A)),
            letterSpacingEm = 0.04f,
            shadows = listOf(NameShadow(rgba(54, 217, 138, .35f), dx = 2f)),
        ),
        GOTHIC to NameThemeSpec(
            ProfileNameFont.GOTHIC, listOf(Color(0xFFE7C6FF), Color(0xFF9B5DE5)),
            letterSpacingEm = 0.02f,
            shadows = listOf(NameShadow(rgba(155, 93, 229, .3f), blur = 12f)),
        ),
        NEON to NameThemeSpec(
            ProfileNameFont.DISPLAY,
            listOf(Color(0xFFFF4FA3), Color(0xFF8B5CF6), Color(0xFF0EA5E9)),
            shadows = listOf(
                NameShadow(rgba(255, 79, 163, .45f), blur = 12f),
                NameShadow(rgba(139, 92, 246, .3f), blur = 24f),
            ),
            motion = NameMotion.Pan(3200, scale = 2f, pingPong = true),
        ),
        GLITCH to NameThemeSpec(
            ProfileNameFont.MONO, listOf(Color(0xFF22D3EE), Color(0xFFFF4FA3)),
            letterSpacingEm = 0.03f,
            shadows = listOf(
                NameShadow(rgba(34, 211, 238, .65f), dx = 2f),
                NameShadow(rgba(255, 79, 163, .65f), dx = -2f),
            ),
            motion = NameMotion.Glitch,
        ),
        CANDY to NameThemeSpec(
            ProfileNameFont.ROUNDED,
            listOf(Color(0xFFFF7AB6), Color(0xFFFFC6FF), Color(0xFFFFD166)),
            shadows = listOf(NameShadow(rgba(255, 126, 182, .35f), blur = 10f)),
            motion = NameMotion.Pan(3500, scale = 1.8f, pingPong = false),
        ),
        RETRO to NameThemeSpec(
            ProfileNameFont.ROUNDED, listOf(Color(0xFFF59E0B), Color(0xFFEF4444)),
            letterSpacingEm = 0.04f,
            shadows = listOf(NameShadow(rgba(239, 68, 68, .4f), dx = 1f, dy = 1f)),
        ),
        OCEAN to NameThemeSpec(
            ProfileNameFont.SERIF, listOf(Color(0xFF0EA5E9), Color(0xFF22D3EE)),
            shadows = listOf(NameShadow(rgba(34, 211, 238, .35f), blur = 12f)),
            motion = NameMotion.Pan(4200, scale = 2f, pingPong = true),
        ),
        SUNSET to NameThemeSpec(
            ProfileNameFont.DISPLAY, listOf(Color(0xFFFB7185), Color(0xFFF59E0B)),
            shadows = listOf(NameShadow(rgba(251, 113, 133, .35f), blur = 12f)),
            motion = NameMotion.Pan(4000, scale = 1.8f, pingPong = false),
        ),
        MATRIX to NameThemeSpec(
            ProfileNameFont.MONO, listOf(Color(0xFF22C55E), Color(0xFF86EFAC)),
            letterSpacingEm = 0.05f,
            shadows = listOf(NameShadow(rgba(34, 197, 94, .5f), blur = 10f)),
        ),
        GOLD to NameThemeSpec(
            ProfileNameFont.DISPLAY, listOf(Color(0xFFFACC15), Color(0xFFB45309)),
            shadows = listOf(NameShadow(rgba(250, 204, 21, .35f), blur = 12f)),
            motion = NameMotion.Pan(4500, scale = 2f, pingPong = false),
        ),
        ICE to NameThemeSpec(
            ProfileNameFont.SERIF, listOf(Color(0xFF7DD3FC), Color(0xFFE0F2FE)),
            letterSpacingEm = 0.02f,
            shadows = listOf(NameShadow(rgba(125, 211, 252, .45f), blur = 10f)),
        ),
    )

    private fun hex(v: Int) = Color(0xFF000000L or v.toLong())
    private fun cs(vararg v: Int) = v.map(::hex)
    private fun h(vararg s: Pair<Float, Int>) =
        NamePaint(false, s.map { it.first to hex(it.second) })

    private fun v(vararg s: Pair<Float, Int>) =
        NamePaint(true, s.map { it.first to hex(it.second) })

    private val Primary = Color.Unspecified

    private val seasonal: Map<SeasonalNameTheme, NameThemeSpec> = mapOf(
        SeasonalNameTheme.SACI to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0x16A34A, 0x65A30D, 0xDC2626),
            paint = h(0f to 0x16A34A, 0.45f to 0x65A30D, 1f to 0xDC2626),
        ),
        SeasonalNameTheme.FUMACA to NameThemeSpec(
            ProfileNameFont.SERIF, cs(0x475569, 0x94A3B8, 0xCBD5E1),
            paint = h(0f to 0x475569, 0.7f to 0x94A3B8, 1f to 0xCBD5E1),
        ),
        SeasonalNameTheme.NOITE to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xF97316, 0xA855F7),
        ),
        SeasonalNameTheme.ASSOMBRADO to NameThemeSpec(
            ProfileNameFont.DISPLAY, cs(0x84CC16, 0x16A34A),
            paint = v(0f to 0x84CC16, 1f to 0x16A34A),
            fontFamily = SeasonalFonts.Creepster, fontScale = 1.04f,
        ),
        SeasonalNameTheme.BRASA to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xF59E0B, 0xEF4444, 0xB91C1C),
            paint = h(0f to 0xF59E0B, 0.6f to 0xEF4444, 1f to 0xB91C1C),
        ),
        SeasonalNameTheme.RIO to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0x06B6D4, 0x0F766E, 0x134E4A),
            paint = h(0f to 0x06B6D4, 0.6f to 0x0F766E, 1f to 0x134E4A),
        ),
        SeasonalNameTheme.LENDA to NameThemeSpec(
            ProfileNameFont.SERIF, cs(0x92400E, 0xB45309),
        ),
        SeasonalNameTheme.PISCA to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xDC2626, 0x16A34A, 0xCA8A04, 0x2563EB),
            paint = h(
                0f to 0xDC2626, 0.2f to 0xDC2626,
                0.2f to 0x16A34A, 0.4f to 0x16A34A,
                0.4f to 0xCA8A04, 0.6f to 0xCA8A04,
                0.6f to 0x2563EB, 0.8f to 0x2563EB,
                0.8f to 0xDC2626, 1f to 0xDC2626,
            ),
        ),
        SeasonalNameTheme.NATALINO to NameThemeSpec(
            ProfileNameFont.CURSIVE, cs(0xB91C1C, 0x15803D),
            fontFamily = SeasonalFonts.MountainsOfChristmas, fontScale = 1.08f,
        ),
        SeasonalNameTheme.VIRADA to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xF59E0B, 0xFDE047, 0xB45309),
            paint = h(0f to 0xF59E0B, 0.5f to 0xFDE047, 1f to 0xB45309),
        ),
        SeasonalNameTheme.BRANCO to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0x64748B, 0x94A3B8, 0x475569),
            paint = h(0f to 0x64748B, 0.5f to 0x94A3B8, 1f to 0x475569),
        ),
        SeasonalNameTheme.FOLIA to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xEC4899, 0xF59E0B, 0x22C55E, 0x3B82F6),
        ),
        SeasonalNameTheme.PURPURINA to NameThemeSpec(
            ProfileNameFont.CURSIVE, cs(0xDB2777, 0xF59E0B),
            fontFamily = SeasonalFonts.Pacifico, fontScale = 0.96f,
        ),
        SeasonalNameTheme.ARRASTA_PE to NameThemeSpec(
            ProfileNameFont.SLAB, cs(0x92400E, 0xD97706),
            fontFamily = SeasonalFonts.Rye, fontScale = 0.88f,
        ),
        SeasonalNameTheme.CHITA to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xDC2626, 0xCA8A04, 0x2563EB),
            paint = h(
                0f to 0xDC2626, 0.33f to 0xDC2626,
                0.33f to 0xCA8A04, 0.66f to 0xCA8A04,
                0.66f to 0x2563EB, 1f to 0x2563EB,
            ),
        ),
        SeasonalNameTheme.CORREIO to NameThemeSpec(
            ProfileNameFont.CURSIVE, cs(0xE11D48, 0xF472B6),
            fontFamily = SeasonalFonts.Pacifico, fontScale = 0.96f,
        ),
        SeasonalNameTheme.IPE to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xFACC15, 0xCA8A04, 0xA16207),
            paint = h(0f to 0xFACC15, 0.6f to 0xCA8A04, 1f to 0xA16207),
        ),
        SeasonalNameTheme.ARARA to NameThemeSpec(
            ProfileNameFont.SYSTEM, cs(0xDC2626, 0x2563EB, 0xFACC15),
            paint = h(0f to 0xDC2626, 0.55f to 0x2563EB, 1f to 0xFACC15),
        ),
        SeasonalNameTheme.TERMINAL to NameThemeSpec(
            ProfileNameFont.MONO, listOf(Primary, Primary),
        ),
        SeasonalNameTheme.BIT_8 to NameThemeSpec(
            ProfileNameFont.PIXEL, cs(0x6746F5, 0x22D3EE), fontScale = 0.6f,
        ),
        SeasonalNameTheme.FOSFORO to NameThemeSpec(
            ProfileNameFont.MONO, cs(0x16A34A, 0x4ADE80),
        ),
        SeasonalNameTheme.GIZ to NameThemeSpec(
            ProfileNameFont.COMIC, cs(0xF43F5E, 0xF59E0B, 0x22C55E, 0x3B82F6),
            fontFamily = SeasonalFonts.GochiHand, fontScale = 1.12f,
        ),
    )

    fun spec(theme: ProfileNameTheme?): NameThemeSpec = when (theme) {
        is FreeProfileNameTheme -> free[theme] ?: default
        is SeasonalNameTheme -> seasonal[theme] ?: default
        else -> default
    }
}