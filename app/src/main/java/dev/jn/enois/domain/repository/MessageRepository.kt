package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.domain.model.CursorPage
import dev.jn.enois.domain.model.Message
import dev.jn.enois.domain.model.ReactionType

interface MessageRepository {
    suspend fun listMessages(
        conversationId: String,
        cursor: String?,
        limit: Int = 50
    ): Result<CursorPage<Message>>

    suspend fun sendMessage(
        conversationId: String,
        text: String,
        mediaIds: List<String> = emptyList(),
        spoiler: Boolean = false,
        idempotencyKey: String? = null
    ): Result<Message>

    suspend fun react(conversationId: String, messageId: String, type: ReactionType): Result<String>
    suspend fun markAsRead(conversationId: String): Result<Unit>
    suspend fun openDirect(userId: String): Result<Conversation>
    suspend fun hideMessages(conversationId: String, ids: List<String>): Result<Unit>
    suspend fun hideConversations(ids: List<String>): Result<Unit>
    suspend fun unhideConversations(ids: List<String>): Result<Unit>
    suspend fun listConversations(cursor: String?, limit: Int = 20): Result<CursorPage<Conversation>>

    suspend fun getConversation(id: String): Result<Conversation>

    suspend fun archivedConversations(cursor: String?, limit: Int = 20): Result<CursorPage<Conversation>>
}