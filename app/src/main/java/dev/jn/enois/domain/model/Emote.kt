package dev.jn.enois.domain.model

data class Emote(
    val code: String,
    val imageUrl: String,
    val thumbUrl: String,
    val animated: Boolean
)

data class EmotePage(val items: List<Emote>, val page: Int, val totalPages: Int)