package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.FeedPage
import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.FeedTab

interface FeedRepository {

    suspend fun getFeed(
        tab: FeedTab,
        cursor: String? = null,
        limit: Int = 20
    ): Result<FeedPage>

    suspend fun getPost(
        postId: String,
    ): Result<FeedPost>
}