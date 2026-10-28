package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.message.ConversationDto
import dev.jn.enois.data.remote.dto.message.CursorPageDto
import dev.jn.enois.data.remote.dto.message.MessageDto
import dev.jn.enois.data.remote.dto.message.OpenDirectRequest
import dev.jn.enois.data.remote.dto.message.ReactRequest
import dev.jn.enois.data.remote.dto.message.ReactResponse
import dev.jn.enois.data.remote.dto.message.SendMessageRequest
import dev.jn.enois.data.remote.error.bodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class MessageApi(private val client: HttpClient) {

    suspend fun conversations(cursor: String?, limit: Int): CursorPageDto<ConversationDto> =
        client.get("conversations") {
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.bodyOrThrow("Falha ao carregar conversas.")

    suspend fun conversation(id: String): ConversationDto {
        return client.get("conversations/$id")
            .bodyOrThrow("Falha ao carregar conversa.")
    }

    suspend fun archived(cursor: String?, limit: Int): CursorPageDto<ConversationDto> =
        client.get("conversations/archived") {
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.bodyOrThrow("Falha ao carregar arquivadas.")

    suspend fun openDirect(userId: String): ConversationDto =
        client.post("conversations/direct") {
            setBody(OpenDirectRequest(userId))
        }.bodyOrThrow("Falha ao abrir conversa.")

    suspend fun messages(id: String, cursor: String?, limit: Int): CursorPageDto<MessageDto> =
        client.get("conversations/$id/messages") {
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.bodyOrThrow("Falha ao carregar mensagens.")

    suspend fun sendMessage(
        id: String,
        body: SendMessageRequest,
        idempotencyKey: String
    ): MessageDto =
        client.post("conversations/$id/messages") {
            header("Idempotency-Key", idempotencyKey)
            setBody(body)
        }.bodyOrThrow("Falha ao enviar mensagem.")

    suspend fun markAsRead(id: String) {
        return client.post("conversations/$id/read")
            .bodyOrThrow("Falha ao marcar como lido.")
    }

    suspend fun react(id: String, messageId: String, type: String): ReactResponse =
        client.post("conversations/$id/messages/$messageId/reactions") {
            setBody(ReactRequest(type))
        }.bodyOrThrow("Falha ao reagir.")

    /*    suspend fun hideConversations(ids: List<String>) {
            client.post("conversations/hide") { setBody(IdsRequest(ids)) }
                .bodyOrThrow<HideResponse>("Falha ao apagar conversas.")
        }

        suspend fun unhideConversations(ids: List<String>) {
            client.post("conversations/unhide") { setBody(IdsRequest(ids)) }
                .bodyOrThrow<RestoreResponse>("Falha ao restaurar conversas.")
        }
        */
}