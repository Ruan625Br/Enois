package dev.jn.enois.di

import dev.jn.enois.BuildConfig
import dev.jn.enois.domain.model.AppConfig
import org.koin.dsl.module

val configModule = module {
    single { AppConfig(BuildConfig.API_BASE_URL, BuildConfig.WEB_ORIGIN) }
}