package dev.jn.enois.di

import dev.jn.enois.data.remote.api.AuthApi
import dev.jn.enois.data.remote.api.FeedApi
import dev.jn.enois.data.remote.api.MediaApi
import dev.jn.enois.data.remote.api.ReactionApi
import dev.jn.enois.data.remote.api.UserProfileApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient(Android) {

            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }

            defaultRequest {
                url("https://enois.ia.br/api/")
                contentType(ContentType.Application.Json)
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