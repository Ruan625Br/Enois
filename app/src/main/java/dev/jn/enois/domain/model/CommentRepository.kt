package dev.jn.enois.domain.model

interface CommentRepository {
    suspend fun getComments(postId: String, cursor: String?, limit: Int): Result<CommentPage>
    suspend fun createComment(
        postId: String, content: String, parentCommentId: String?, mentionedUserIds: List<String>,
    ): Result<Comment>
}