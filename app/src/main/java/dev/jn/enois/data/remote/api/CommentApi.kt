package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.comment.CommentDto
import dev.jn.enois.data.remote.dto.comment.CommentPageDto
import dev.jn.enois.data.remote.dto.comment.CreateCommentRequest
import dev.jn.enois.data.remote.error.bodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import java.util.UUID

class CommentApi(
    private val client: HttpClient
) {

    suspend fun getComments(postId: String, cursor: String?, limit: Int): CommentPageDto =
        client.get("feed/posts/$postId/comments") {
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.bodyOrThrow("Falha ao carregar comentários.")

    suspend fun createComment(postId: String, request: CreateCommentRequest): CommentDto =
        client.post("feed/posts/$postId/comments") {
            header("Idempotency-Key", UUID.randomUUID().toString())
            setBody(request)
        }.bodyOrThrow("Falha ao enviar comentário.")
}