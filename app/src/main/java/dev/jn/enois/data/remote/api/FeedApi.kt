package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.feed.FeedPostDto
import dev.jn.enois.data.remote.dto.feed.FeedResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class FeedApi(
    private val client: HttpClient
) {

    suspend fun getFeed(
        tab: String = "FOR_YOU",
        cursor: String? = null,
        limit: Int = 20
    ): FeedResponseDto {
        require(limit in 1..50)

        return client.get("feed") {
            parameter("tab", tab)
            parameter("limit", limit)

            if (cursor != null) {
                parameter("cursor", cursor)
            }
        }.body()
    }

    suspend fun getPost(
        postId: String,
    ): FeedPostDto {

        return client.get("feed/posts/$postId").body()
    }
}