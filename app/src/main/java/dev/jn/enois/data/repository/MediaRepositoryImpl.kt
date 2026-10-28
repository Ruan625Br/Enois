package dev.jn.enois.data.repository

import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.MediaApi
import dev.jn.enois.data.remote.dto.media.MediaDto
import dev.jn.enois.domain.model.Media
import dev.jn.enois.domain.repository.MediaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.cancellation.CancellationException

class MediaRepositoryImpl(
    private val mediaApi: MediaApi
) : MediaRepository {

    private val mutex = Mutex()
    private val cache = mutableMapOf<String, CacheEntry>()

    override suspend fun getMedia(id: String): Result<Media> {
        return try {
            val now = System.currentTimeMillis()

            val cached = mutex.withLock {
                cache[id]?.takeIf {
                    now - it.cacheAt < CACHE_TTL_MS
                }?.media
            }

            if (cached != null) {
                return Result.success(cached.toDomain())
            }

            val media = mediaApi.getMedia(id)

            mutex.withLock {
                cache[id] = CacheEntry(
                    media = media,
                    cacheAt = System.currentTimeMillis()
                )
            }

            Result.success(media.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMedias(ids: List<String>): Result<List<Media>> {
        return try {
            val results = coroutineScope {
                ids.distinct().map { id ->
                    async { getMedia(id) }
                }.awaitAll()
            }

            val failure = results.firstOrNull { it.isFailure }

            if (failure != null) {
                Result.failure(
                    failure.exceptionOrNull()
                        ?: IllegalStateException("Failed to load media")
                )
            } else {
                Result.success(
                    results.map { it.getOrThrow() }
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private data class CacheEntry(
        val media: MediaDto,
        val cacheAt: Long
    )

    private companion object {
        const val CACHE_TTL_MS = 60_000L
    }
}