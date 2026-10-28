package dev.jn.enois.data.remote.dto.message

import kotlinx.serialization.Serializable

@Serializable
data class CursorPageDto<T>(
    val items: List<T> = emptyList(),
    val nextCursor: String? = null,
)

@Serializable
data class ConversationDto(
    val id: String,
    val kind: String,
    val title: String? = null,
    val participantIds: List<String> = emptyList(),
    val lastMessage: String? = null,
    val time: String? = null,
    val unread: Int = 0,
    val otherLastReadAt: String? = null,
    val lastMessageE2e: Boolean = false,
    val lastMessageIv: String? = null,
)

@Serializable
data class MessageDto(
    val id: String,
    val conversationId: String,
    val from: String,
    val text: String = "",
    val mediaIds: List<String> = emptyList(),
    val mediaSpoiler: Boolean = false,
    val time: String,
    val reactions: Map<String, Int> = emptyMap(),
    val myReaction: String? = null,
    val e2e: Boolean = false,
    val iv: String? = null,
)

@Serializable
data class SendMessageRequest(val text: String, val mediaIds: List<String>, val mediaSpoiler: Boolean)

@Serializable data class ReactRequest(val type: String)
@Serializable data class ReactResponse(val added: Boolean)
@Serializable data class IdsRequest(val ids: List<String>)
@Serializable data class OpenDirectRequest(val userId: String)