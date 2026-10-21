package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.feed.FeedPostDto
import dev.jn.enois.data.remote.dto.feed.FeedResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

class FeedApi(
    private val client: HttpClient
) {

    suspend fun getFeed(
        token: String,
        tab: String = "FOR_YOU",
        cursor: String? = null,
        limit: Int = 20
    ): FeedResponseDto {
        require(limit in 1..50)

        return client.get("feed") {
            header("X-Session-Token", token)

            parameter("tab", tab)
            parameter("limit", limit)

            if (cursor != null) {
                parameter("cursor", cursor)
            }
        }.body()
    }

    suspend fun getPost(
        token: String,
        postId: String,
    ): FeedPostDto {

        return client.get("feed/posts/$postId") {
            header("X-Session-Token", token)
        }.body()
    }
}