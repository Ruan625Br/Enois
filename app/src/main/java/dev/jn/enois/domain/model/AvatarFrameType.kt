package dev.jn.enois.domain.model

import android.util.Log

enum class AvatarFrameType(
    val fxName: String,
    val isFx: Boolean = false,
    val isAnimated: Boolean = true,
    val isSeasonal: Boolean = false,
) {
    STORM("moldura-tempestade", isFx = true),
    INFERNO("moldura-inferno", isFx = true),
    BLACK_HOLE("moldura-buraco-negro", isFx = true),
    ORBIT("moldura-orbita", isFx = true),
    GLACIAL("moldura-glacial", isFx = true),
    TOXIC("moldura-toxica", isFx = true),

    AURORA("moldura-aurora"),
    PULSAR("moldura-pulsar"),
    SOLAR("moldura-solar"),
    NEBULA("moldura-nebulosa", isFx = true),
    SUPERNOVA("moldura-supernova", isFx = true),
    GALAXIA("moldura-galaxia", isFx = true),
    PORTAL("moldura-portal", isFx = true),
    HOLOGRAM("moldura-holograma", isFx = true),

    // Sazonais (SEASONAL_FX_FRAMES da web)
    REDEMOINHO("moldura-redemoinho", isFx = true, isSeasonal = true),
    CARAPUCA("moldura-carapuca", isFx = true, isSeasonal = true),
    CACHIMBO("moldura-cachimbo", isFx = true, isSeasonal = true),
    FOLHAS("moldura-folhas", isFx = true, isSeasonal = true),
    MATA("moldura-mata", isFx = true, isSeasonal = true),
    SACI_COMPLETO("moldura-saci-completo", isFx = true, isSeasonal = true),
    ASAS_MORCEGO("moldura-asas-morcego", isFx = true, isSeasonal = true),
    CHIFRES("moldura-chifres", isFx = true, isSeasonal = true),
    ABOBORA("moldura-abobora", isFx = true, isSeasonal = true),
    TEIA("moldura-teia", isFx = true, isSeasonal = true),
    FANTASMAS("moldura-fantasmas", isFx = true, isSeasonal = true),
    CALDEIRAO("moldura-caldeirao", isFx = true, isSeasonal = true),
    LUA("moldura-lua", isFx = true, isSeasonal = true),
    VELAS("moldura-velas", isFx = true, isSeasonal = true),
    CURUPIRA("moldura-curupira", isFx = true, isSeasonal = true),
    BOITATA("moldura-boitata", isFx = true, isSeasonal = true),
    IARA("moldura-iara", isFx = true, isSeasonal = true),
    MULA("moldura-mula", isFx = true, isSeasonal = true),
    BOTO("moldura-boto", isFx = true, isSeasonal = true),
    VITORIA("moldura-vitoria", isFx = true, isSeasonal = true),
    LOBISOMEM("moldura-lobisomem", isFx = true, isSeasonal = true),
    PISCA("moldura-pisca", isFx = true, isSeasonal = true),
    NEVE("moldura-neve", isFx = true, isSeasonal = true),
    GUIRLANDA("moldura-guirlanda", isFx = true, isSeasonal = true),
    PRESENTES("moldura-presentes", isFx = true, isSeasonal = true),
    ESTRELA("moldura-estrela", isFx = true, isSeasonal = true),
    NOEL("moldura-noel", isFx = true, isSeasonal = true),
    FOGOS("moldura-fogos", isFx = true, isSeasonal = true),
    SETE_ONDAS("moldura-sete-ondas", isFx = true, isSeasonal = true),
    MEIA_NOITE("moldura-meia-noite", isFx = true, isSeasonal = true),
    UVAS("moldura-uvas", isFx = true, isSeasonal = true),
    SERPENTINA("moldura-serpentina", isFx = true, isSeasonal = true),
    CONFETE("moldura-confete", isFx = true, isSeasonal = true),
    PLUMAS("moldura-plumas", isFx = true, isSeasonal = true),
    FREVO("moldura-frevo", isFx = true, isSeasonal = true),
    PURPURINA("moldura-purpurina", isFx = true, isSeasonal = true),
    PANDEIRO("moldura-pandeiro", isFx = true, isSeasonal = true),
    BANDEIRINHAS("moldura-bandeirinhas", isFx = true, isSeasonal = true),
    FOGUEIRA("moldura-fogueira", isFx = true, isSeasonal = true),
    CHAPEU_PALHA("moldura-chapeu-palha", isFx = true, isSeasonal = true),
    MILHARAL("moldura-milharal", isFx = true, isSeasonal = true),
    CHITA("moldura-chita", isFx = true, isSeasonal = true),
    ESTRELINHA("moldura-estrelinha", isFx = true, isSeasonal = true),
    CORACOES("moldura-coracoes", isFx = true, isSeasonal = true),
    CARTAS("moldura-cartas", isFx = true, isSeasonal = true),
    LACO("moldura-laco", isFx = true, isSeasonal = true),
    ASAS_ARARA("moldura-asas-arara", isFx = true, isSeasonal = true),
    COROA_FLORES("moldura-coroa-flores", isFx = true, isSeasonal = true),
    IPE("moldura-ipe", isFx = true, isSeasonal = true),
    ARARA("moldura-arara", isFx = true, isSeasonal = true),
    TUCANO("moldura-tucano", isFx = true, isSeasonal = true),
    BEIJA_FLOR("moldura-beija-flor", isFx = true, isSeasonal = true),
    MANDACARU("moldura-mandacaru", isFx = true, isSeasonal = true),
    FLORADA("moldura-florada", isFx = true, isSeasonal = true),
    FONE("moldura-fone", isFx = true, isSeasonal = true),
    HEX_100("moldura-0x100", isFx = true, isSeasonal = true),
    TERMINAL("moldura-terminal", isFx = true, isSeasonal = true),
    TOALHA("moldura-toalha", isFx = true, isSeasonal = true),
    D20("moldura-d20", isFx = true, isSeasonal = true),
    PIXEL("moldura-pixel", isFx = true, isSeasonal = true),
    BUGS("moldura-bugs", isFx = true, isSeasonal = true),
    PIPAS("moldura-pipas", isFx = true, isSeasonal = true),
    PIAO("moldura-piao", isFx = true, isSeasonal = true),
    BOLHAS("moldura-bolhas", isFx = true, isSeasonal = true),
    GUDE("moldura-gude", isFx = true, isSeasonal = true),
    FIGURINHAS("moldura-figurinhas", isFx = true, isSeasonal = true);

    companion object {
        private val BY_FX_NAME: Map<String, AvatarFrameType> = entries.associateBy { it.fxName }

        val FX_FRAMES: List<AvatarFrameType> = entries.filter { it.isFx }
        val ANIMATED_FRAMES: List<AvatarFrameType> = entries.filter { it.isAnimated }


        fun fromFxName(fxName: String?): AvatarFrameType? {
            val frame = BY_FX_NAME[fxName]
            if (frame == null) {
                Log.w("AvatarFrameType", "Unknown frame: $fxName")
            }
            return frame
        }
    }
}