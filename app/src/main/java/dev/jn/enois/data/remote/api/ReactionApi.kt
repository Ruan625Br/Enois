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

    suspend fun react(postId: String, request: ReactionRequest): PostActionDto {
        return client.post("feed/posts/$postId/reactions") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            setBody(request)
        }.body()
    }

    suspend fun removeReaction(postId: String): PostActionDto {
        return client.delete("feed/posts/$postId/reactions") {
            header("Idempotency-Key", UUID.randomUUID().toString())
        }.body()
    }

    suspend fun getReactions(postId: String): ReactionListResponseDto {
        return client.get("feed/posts/$postId/reactions").body()
    }
}