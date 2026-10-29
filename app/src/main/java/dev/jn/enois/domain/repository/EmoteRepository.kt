package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.EmotePage
import kotlinx.coroutines.flow.StateFlow

interface EmoteRepository {
    val index: StateFlow<Map<String, String>>
    suspend fun refresh()
    suspend fun page(page: Int, query: String?): Result<EmotePage>
}