package dev.jn.enois.data.remote.api

import dev.jn.enois.data.remote.dto.profile.UserProfileDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class UserProfileApi(
    private val client: HttpClient
) {
    suspend fun getCurrentUserProfile(): UserProfileDto {
        return client.get("me") {
        }.body()
    }
}