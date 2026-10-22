package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.reaction.PostActionDto
import dev.jn.enois.data.remote.dto.reaction.ReactionListResponseDto
import dev.jn.enois.data.remote.dto.reaction.ReactionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import java.util.UUID

class ReactionApi(
    private val client: HttpClient
) {

    suspend fun react(postId: String, request: ReactionRequest, token: String): PostActionDto {
        return client.post("feed/posts/$postId/reactions") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            header("X-Session-Token", token)

            setBody(request)
        }.body()
    }

    suspend fun removeReaction(postId: String, token: String): PostActionDto {
        return client.delete("feed/posts/$postId/reactions") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            header("X-Session-Token", token)
        }.body()
    }

    suspend fun getReactions(postId: String, token: String): ReactionListResponseDto {
        return client.get("feed/posts/$postId/reactions") {
            header("X-Session-Token", token)
        }.body()
    }
}