package dev.jn.enois.domain.model

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
    SOLAR("moldura-solar");

    companion object {
        private val BY_FX_NAME: Map<String, AvatarFrameType> = entries.associateBy { it.fxName }

        val FX_FRAMES: List<AvatarFrameType> = entries.filter { it.isFx }
        val ANIMATED_FRAMES: List<AvatarFrameType> = entries.filter { it.isAnimated }


        fun fromFxName(fxName: String?): AvatarFrameType? {
            return BY_FX_NAME[fxName]
        }
    }
}
