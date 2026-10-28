package dev.jn.enois.di

import dev.jn.enois.BuildConfig
import dev.jn.enois.data.local.session.PersistentCookiesStorage
import dev.jn.enois.data.local.session.SecureCookieStore
import dev.jn.enois.data.remote.api.AuthApi
import dev.jn.enois.data.remote.api.CommentApi
import dev.jn.enois.data.remote.api.FeedApi
import dev.jn.enois.data.remote.api.MediaApi
import dev.jn.enois.data.remote.api.ReactionApi
import dev.jn.enois.data.remote.api.UserProfileApi
import dev.jn.enois.domain.model.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single {
        val config = get<AppConfig>()
        val cookies = get<PersistentCookiesStorage>()

        HttpClient(Android) {

            install(Logging) {
                logger = Logger.ANDROID
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            }

            install(HttpSend)

            install(HttpCookies) {
                storage = get<PersistentCookiesStorage>()
            }

            defaultRequest {
                url(config.apiBaseUrl)
                contentType(ContentType.Application.Json)
                headers {
                    header("X-Client-Origin", config.webOrigin)
                    header("X-CSRF-Protection", "1")
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }.also { client ->
            val refreshMutex = Mutex()
            var refreshGeneration = 0

            client.plugin(HttpSend).intercept { request ->
                val generationAtSend = refreshGeneration
                val call = execute(request)

                val isAuthRoute = request.url.encodedPath.contains("/auth/")
                if (call.response.status != HttpStatusCode.Unauthorized || isAuthRoute) {
                    return@intercept call
                }

                val renewed = refreshMutex.withLock {
                    if (generationAtSend != refreshGeneration) {
                        true
                    } else {
                        val response = runCatching { client.post("auth/refresh") }.getOrNull()
                        when {
                            response == null -> false
                            response.status.isSuccess() -> {
                                refreshGeneration++
                                true
                            }

                            response.status.value in listOf(400, 401, 403) -> {
                                cookies.clear()
                                false
                            }

                            else -> false
                        }
                    }
                }

                if (renewed) execute(request) else call
            }
        }

    }

    single { SecureCookieStore(androidContext()) }
    singleOf(::PersistentCookiesStorage) bind CookiesStorage::class

    singleOf(::AuthApi)
    singleOf(::FeedApi)
    singleOf(::UserProfileApi)
    singleOf(::MediaApi)
    singleOf(::ReactionApi)
    singleOf(::CommentApi)
}