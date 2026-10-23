package dev.jn.enois.di

import dev.jn.enois.BuildConfig
import dev.jn.enois.data.remote.api.AuthApi
import dev.jn.enois.data.remote.api.FeedApi
import dev.jn.enois.data.remote.api.MediaApi
import dev.jn.enois.data.remote.api.ReactionApi
import dev.jn.enois.data.remote.api.UserProfileApi
import dev.jn.enois.domain.model.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single {
        val config = get<AppConfig>()

        HttpClient(Android) {

            install(Logging) {
                logger = Logger.ANDROID
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            }

            defaultRequest {
                url(config.apiBaseUrl)
                contentType(ContentType.Application.Json)
                headers {
                    header("X-Client-Origin", config.webOrigin)
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    singleOf(::AuthApi)
    singleOf(::FeedApi)
    singleOf(::UserProfileApi)
    singleOf(::MediaApi)
    singleOf(::ReactionApi)
}