package dev.jn.enois.di

import dev.jn.enois.data.repository.AuthRepositoryImpl
import dev.jn.enois.data.repository.FeedRepositoryImpl
import dev.jn.enois.domain.repository.AuthRepository
import dev.jn.enois.domain.repository.FeedRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<FeedRepository> {
        FeedRepositoryImpl(get(), get())
    }
}