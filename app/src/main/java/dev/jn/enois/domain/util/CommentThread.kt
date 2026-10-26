package dev.jn.enois.domain.util

import dev.jn.enois.domain.model.Comment

data class CommentThread(
    val root: Comment,
    val replies: List<Comment>,
)

fun buildThreads(comments: List<Comment>): List<CommentThread> {
    if (comments.isEmpty()) return emptyList()

    val byId = comments.associateBy { it.id }

    fun rootIdOf(comment: Comment): String {
        val visited = hashSetOf(comment.id)
        var current = comment
        while (true) {
            val parent = current.parentCommentId?.let(byId::get) ?: return current.id
            if (!visited.add(parent.id)) return comment.id
            current = parent
        }
    }

    val rootIdByComment = comments.associate { it.id to rootIdOf(it) }

    val repliesByRoot = comments
        .filter { rootIdByComment.getValue(it.id) != it.id }
        .groupBy { rootIdByComment.getValue(it.id) }

    val replyOrder = compareBy<Comment>({ it.createdAt }, { it.id })

    return comments
        .filter { rootIdByComment.getValue(it.id) == it.id }
        .sortedByDescending { it.pinned }
        .map { root ->
            CommentThread(
                root = root,
                replies = repliesByRoot[root.id].orEmpty().sortedWith(replyOrder),
            )
        }
}