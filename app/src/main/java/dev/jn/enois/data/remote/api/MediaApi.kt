package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.media.MediaDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class MediaApi(
    private val client: HttpClient
) {

    suspend fun getMedia(id: String, token: String): MediaDto {
        return client.get("media/$id") {
            header("X-Session-Token", token)
        }.body()
    }
}