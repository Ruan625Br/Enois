package dev.jn.enois.domain.model

import androidx.compose.runtime.Stable

enum class ConversationKind { DIRECT, GROUP, JOB, SERVICE }

@Stable
data class Conversation(
    val id: String,
    val kind: ConversationKind,
    val title: String?,
    val participantIds: List<String>,
    val lastMessage: String?,
    val time: String?,
    val unread: Int,
    val otherLastReadAt: String?,
    val otherLastReadMillis: Long?,
    val lastMessageE2e: Boolean,
    val lastMessageIv: String?,
)

@Stable
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val mediaIds: List<String>,
    val mediaSpoiler: Boolean,
    val time: String,
    val timeMillis: Long,
    val reactions: Map<ReactionType, Int>,
    val myReaction: ReactionType?,
    val isE2e: Boolean,
    val iv: String?,
    val status: MessageStatus = MessageStatus.Sent,
){
    companion object {
        fun pending(id: String, conversationId: String, senderId: String, text: String): Message {
            return Message(
                id = id,
                conversationId = conversationId,
                senderId = senderId,
                text = text,
                mediaIds = emptyList(),
                mediaSpoiler = false,
                time = "",
                timeMillis = System.currentTimeMillis(),
                reactions = emptyMap(),
                myReaction = null,
                isE2e = false,
                iv = null,
                status = MessageStatus.Sending,
            )
        }
    }
}

enum class MessageStatus { Sending, Sent, Failed }

data class CursorPage<T>(val items: List<T>, val nextCursor: String?)
