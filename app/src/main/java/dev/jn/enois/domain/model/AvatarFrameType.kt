package dev.jn.enois.domain.model

import android.util.Log

enum class AvatarFrameType(
    val fxName: String,
    val isFx: Boolean = false,
    val isAnimated: Boolean = true,
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
    HOLOGRAM("moldura-holograma", isFx = true);

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
