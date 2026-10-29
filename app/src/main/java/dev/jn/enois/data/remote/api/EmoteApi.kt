package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.emote.EmoteIndexDto
import dev.jn.enois.data.remote.dto.emote.EmotePageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class EmoteApi(private val client: HttpClient) {

    suspend fun index(): EmoteIndexDto = client.get("emotes/index").body()

    suspend fun page(page: Int, limit: Int, query: String?): EmotePageDto =
        client.get("emotes") {
            parameter("page", page)
            parameter("limit", limit)
            if (!query.isNullOrBlank()) parameter("q", query)
        }.body()
}