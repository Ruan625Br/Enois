package dev.jn.enois.data.local.session

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.fillDefaults
import io.ktor.client.plugins.cookies.matches
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PersistentCookiesStorage(
    private val secureStore: SecureCookieStore,
) : CookiesStorage {

    private val mutex = Mutex()
    private var cache: MutableList<Cookie>? = null

    private suspend fun load(): MutableList<Cookie> =
        cache ?: secureStore.read().toMutableList().also { cache = it }

    private fun Cookie.isExpired(now: Long) =
        expires?.timestamp?.let { it <= now } == true

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        val list = load()
        val now = System.currentTimeMillis()
        if (list.removeAll { it.isExpired(now) }) secureStore.write(list)
        list.filter { it.matches(requestUrl) }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) = mutex.withLock {
        if (cookie.name.isBlank()) return@withLock

        val now = System.currentTimeMillis()
        var filled = cookie.fillDefaults(requestUrl)
        val maxAge = filled.maxAge ?: 0
        if (filled.expires == null && maxAge > 0) {
            filled = filled.copy(expires = GMTDate(now + maxAge * 1000L))
        }

        val list = load()
        list.removeAll {
            it.name == filled.name && it.domain == filled.domain && it.path == filled.path
        }
        if (!filled.isExpired(now)) list.add(filled)
        secureStore.write(list)
    }

    suspend fun hasRefreshCookie(): Boolean = mutex.withLock {
        val now = System.currentTimeMillis()
        load().any { it.name == REFRESH_COOKIE && !it.isExpired(now) }
    }

    suspend fun clear() = mutex.withLock {
        cache = mutableListOf()
        secureStore.clear()
    }

    override fun close() {}

    private companion object {
        const val REFRESH_COOKIE = "enois_refresh"
    }
}