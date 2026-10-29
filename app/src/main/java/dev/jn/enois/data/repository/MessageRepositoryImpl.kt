package dev.jn.enois.data.repository

import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.MessageApi
import dev.jn.enois.data.remote.dto.message.ConversationDto
import dev.jn.enois.data.remote.dto.message.MessageDto
import dev.jn.enois.data.remote.dto.message.SendMessageRequest
import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.domain.model.CursorPage
import dev.jn.enois.domain.model.Message
import dev.jn.enois.domain.model.ReactionType
import dev.jn.enois.domain.repository.MessageRepository
import java.util.UUID

class MessageRepositoryImpl(
    private val messageApi: MessageApi
) : BaseRepository(), MessageRepository {


    override suspend fun listMessages(
        conversationId: String,
        cursor: String?,
        limit: Int
    ): Result<CursorPage<Message>> {
        return apiCall {
            val response = messageApi.messages(conversationId, cursor, limit)
            val page = response.toDomain(MessageDto::toDomain)
            page
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        text: String,
        mediaIds: List<String>,
        spoiler: Boolean,
        idempotencyKey: String?
    ): Result<Message> = apiCall {
        val key = idempotencyKey ?: UUID.randomUUID().toString()
        val request = SendMessageRequest(text, mediaIds, spoiler)
        val response = messageApi.sendMessage(conversationId, request, key)
        response.toDomain()
    }

    override suspend fun react(
        conversationId: String,
        messageId: String,
        type: ReactionType
    ): Result<String> = apiCall {
        messageApi.react(conversationId, messageId, type).status
    }

    override suspend fun markAsRead(conversationId: String): Result<Unit> = apiCall {
        messageApi.markAsRead(conversationId)
    }

    override suspend fun openDirect(userId: String): Result<Conversation> = apiCall {
        val response = messageApi.openDirect(userId)
        response.toDomain()
    }

    override suspend fun hideMessages(
        conversationId: String,
        ids: List<String>
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun hideConversations(ids: List<String>): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun unhideConversations(ids: List<String>): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun listConversations(
        cursor: String?,
        limit: Int
    ): Result<CursorPage<Conversation>> = apiCall {
        val response = messageApi.conversations(cursor, limit)
        val page = response.toDomain(ConversationDto::toDomain)
        page
    }

    override suspend fun getConversation(id: String): Result<Conversation> = apiCall {
        messageApi.conversation(id).toDomain()
    }

    override suspend fun archivedConversations(
        cursor: String?,
        limit: Int
    ): Result<CursorPage<Conversation>> = apiCall {
        val response = messageApi.archived(cursor, limit)
        val page = response.toDomain(ConversationDto::toDomain)
        page
    }
}