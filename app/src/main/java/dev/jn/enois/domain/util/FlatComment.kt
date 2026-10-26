package dev.jn.enois.domain.util

import dev.jn.enois.domain.model.Comment

data class FlatComment(
    val comment: Comment,
    val depth: Int,
    val ancestorIds: List<String>,
    val hasChildren: Boolean,
    val isCollapsed: Boolean,
    val descendantCount: Int,
    val continues: List<Boolean>,
)


fun flattenComments(comments: List<Comment>, collapsed: Set<String>): List<FlatComment> {
    if (comments.isEmpty()) return emptyList()
    val byId = comments.associateBy { it.id }

    fun inCycle(c: Comment): Boolean {
        val seen = hashSetOf(c.id)
        var current = c
        while (true) {
            val parent = current.parentCommentId?.let(byId::get) ?: return false
            if (parent.id == c.id) return true
            if (!seen.add(parent.id)) return false
            current = parent
        }
    }

    val parentOf: Map<String, String?> = comments.associate { c ->
        c.id to if (inCycle(c)) null else c.parentCommentId?.takeIf(byId::containsKey)
    }

    val order = compareBy<Comment>({ it.createdAt }, { it.id })
    val childrenOf = comments
        .filter { parentOf[it.id] != null }
        .groupBy { parentOf.getValue(it.id)!! }
        .mapValues { (_, list) -> list.sortedWith(order) }
    val roots = comments.filter { parentOf[it.id] == null }.sortedByDescending { it.pinned }

    val counts = HashMap<String, Int>()
    fun count(id: String): Int = counts.getOrPut(id) {
        childrenOf[id].orEmpty().sumOf { 1 + count(it.id) }
    }

    val out = ArrayList<FlatComment>(comments.size)
    fun visit(comment: Comment, ancestors: List<String>, continues: List<Boolean>) {
        val children = childrenOf[comment.id].orEmpty()
        val isCollapsed = children.isNotEmpty() && comment.id in collapsed
        out += FlatComment(
            comment = comment,
            depth = ancestors.size,
            ancestorIds = ancestors,
            hasChildren = children.isNotEmpty(),
            isCollapsed = isCollapsed,
            descendantCount = count(comment.id),
            continues = continues,
        )
        if (!isCollapsed) {
            children.forEachIndexed { i, child ->
                visit(child, ancestors + comment.id, continues + (i != children.lastIndex))
            }
        }
    }
    roots.forEach { visit(it, emptyList(), emptyList()) }
    return out
}