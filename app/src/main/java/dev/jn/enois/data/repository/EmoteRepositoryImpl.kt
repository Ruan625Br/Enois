package dev.jn.enois.data.repository

import android.os.SystemClock
import dev.jn.enois.data.local.emote.EmoteIndexStore
import dev.jn.enois.data.remote.api.EmoteApi
import dev.jn.enois.domain.model.AppConfig
import dev.jn.enois.domain.model.Emote
import dev.jn.enois.domain.model.EmotePage
import dev.jn.enois.domain.repository.EmoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EmoteRepositoryImpl(
    private val api: EmoteApi,
    private val store: EmoteIndexStore,
    private val config: AppConfig,
) : EmoteRepository {

    private val _index = MutableStateFlow(
        store.read()?.let { absolute(validated(it)) }.orEmpty()   // 1ª pintura sem rede
    )
    override val index = _index.asStateFlow()

    private val mutex = Mutex()
    private var lastRefresh = 0L

    override suspend fun refresh() = mutex.withLock {
        val now = SystemClock.elapsedRealtime()
        if (lastRefresh != 0L && now - lastRefresh < SIX_HOURS) return@withLock
        try {
            val raw = api.index().entries
            val ok = validated(raw)
            if (ok.isNotEmpty()) {
                store.write(ok)
                _index.value = absolute(ok)
                lastRefresh = now
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun page(page: Int, query: String?): Result<EmotePage> {
        val dto = api.page(page, PAGE_SIZE, query)
        return try {
            Result.success(
                EmotePage(
                    items = dto.items.filter { PATH.matches(it.image) && PATH.matches(it.thumb) }
                        .map {
                            Emote(it.code, url(it.image), url(it.thumb), it.animated)
                        },
                    page = dto.page,
                    totalPages = dto.totalPages,
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    private fun validated(raw: Map<String, String>) = raw.entries.asSequence()
        .filter { CODE.matches(it.key) && PATH.matches(it.value) }
        .take(5000)
        .associate { it.key to it.value }

    private fun absolute(paths: Map<String, String>) = paths.mapValues { url(it.value) }
    private fun url(path: String) = config.apiBaseUrl.trimEnd('/') + path

    companion object {
        const val PAGE_SIZE = 40
        private const val SIX_HOURS = 6 * 60 * 60 * 1000L
        private val CODE = Regex("""^[^\s\[\]`]{1,64}$""")
        private val PATH =
            Regex("""^/emotes/image/[A-Za-z0-9]+/[A-Za-z0-9_-]+\?[A-Za-z0-9=&_.-]*$""")
    }
}