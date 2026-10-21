package dev.jn.enois.presentation.postdetails

import dev.jn.enois.domain.model.FeedPost

class PostDetailsCache {
    private val posts = mutableMapOf<String, FeedPost>()

    fun put(post: FeedPost) {
        posts[post.id] = post
    }

    fun get(postId: String): FeedPost? {
        return posts[postId]
    }

    fun remove(postId: String) {
        posts.remove(postId)
    }
}