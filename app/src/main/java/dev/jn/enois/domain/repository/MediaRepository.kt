package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.Media

interface MediaRepository {

    suspend fun getMedia(id: String): Result<Media>

    suspend fun getMedias(ids: List<String>): Result<List<Media>>
}