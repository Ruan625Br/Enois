package dev.jn.enois.data.repository

import dev.jn.enois.data.remote.api.CommentApi
import dev.jn.enois.data.remote.dto.comment.CommentDto
import dev.jn.enois.data.remote.dto.comment.CreateCommentRequest
import dev.jn.enois.data.remote.dto.comment.toDomain
import dev.jn.enois.domain.model.CommentPage
import dev.jn.enois.domain.model.CommentRepository

class CommentRepositoryImpl(private val api: CommentApi) : CommentRepository {

    override suspend fun getComments(postId: String, cursor: String?, limit: Int) = runCatching {
        val page = api.getComments(postId, cursor, limit)
        CommentPage(comments = page.items.map(CommentDto::toDomain), nextCursor = page.nextCursor)
    }

    override suspend fun createComment(
        postId: String,
        content: String,
        parentCommentId: String?,
        mentionedUserIds: List<String>
    ) =
        runCatching {
            api.createComment(postId, CreateCommentRequest(
                content = content,
                parentCommentId = parentCommentId,
                mentionedUserIds = mentionedUserIds
            )).toDomain()
        }
}