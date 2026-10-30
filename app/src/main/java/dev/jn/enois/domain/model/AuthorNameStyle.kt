package dev.jn.enois.domain.model

import androidx.compose.runtime.Stable

@Stable
data class AuthorNameStyle(
    val theme: ProfileNameTheme? = null,
    val font: ProfileNameFont? = null,
    val color: String? = null,
    val nameplate: String? = null
)


sealed interface ProfileNameTheme


enum class FreeProfileNameTheme : ProfileNameTheme {
    DEFAULT,
    PIXEL,
    GOTHIC,
    NEON,
    GLITCH,
    CANDY,
    RETRO,
    OCEAN,
    SUNSET,
    MATRIX,
    GOLD,
    ICE;

    companion object {
        fun fromName(name: String): FreeProfileNameTheme? =
            entries.find { it.name == name }
    }
}

enum class SeasonalNameTheme(
    val id: String
) : ProfileNameTheme {
    SACI("nome-saci"),
    FUMACA("nome-fumaca"),
    NOITE("nome-noite"),
    ASSOMBRADO("nome-assombrado"),
    BRASA("nome-brasa"),
    RIO("nome-rio"),
    LENDA("nome-lenda"),
    PISCA("nome-pisca"),
    NATALINO("nome-natalino"),
    VIRADA("nome-virada"),
    BRANCO("nome-branco"),
    FOLIA("nome-folia"),
    PURPURINA("nome-purpurina"),
    ARRASTA_PE("nome-arrasta-pe"),
    CHITA("nome-chita"),
    CORREIO("nome-correio"),
    IPE("nome-ipe"),
    ARARA("nome-arara"),
    TERMINAL("nome-terminal"),
    BIT_8("nome-8bit"),
    FOSFORO("nome-fosforo"),
    GIZ("nome-giz");

    companion object {
        fun fromId(id: String): SeasonalNameTheme? =
            entries.find { it.id == id }
    }
}

enum class ProfileNameFont {
    SYSTEM,
    SERIF,
    MONO,
    ROUNDED,
    DISPLAY,
    PIXEL,
    GOTHIC,
    CURSIVE,
    CONDENSED,
    COMIC,
    SLAB;


    companion object {
        fun fromName(name: String?): ProfileNameFont? {
            return entries.find { it.name == name }
        }
    }
}