package dev.jn.enois.domain.model

enum class ReactionType(val emoji: String) {
    LIKE("👍"),
    LOVE("❤️"),
    LAUGH("😂"),
    ROFL("🤣"),
    WOW("😮"),
    SAD("😢"),
    ANGRY("😡"),
    DISGUST("🤢"),
    SKEPTICAL("🤨"),
    CELEBRATE("🎉"),
    SUPPORT("💪"),
    INSIGHTFUL("💡"),
    EYES("👀"),
    NOTE("📝"),
    FIRE("🔥"),
    CLAP("👏");

    companion object {
        private val BY_NAME: Map<String, ReactionType> = entries.associateBy { it.name.lowercase() }
        private val BY_EMOJI: Map<String, ReactionType> = entries.associateBy { it.emoji }


        fun fromValue(value: String?): ReactionType? {
            if (value.isNullOrBlank()) return null
            val trimmed = value.trim()
            return BY_NAME[trimmed.lowercase()] ?: BY_EMOJI[trimmed]
        }

        fun fromKey(key: String): ReactionType? =
            entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
    }
}
