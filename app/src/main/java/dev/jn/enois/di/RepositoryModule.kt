package dev.jn.enois.di

import dev.jn.enois.data.repository.AuthRepositoryImpl
import dev.jn.enois.data.repository.FeedRepositoryImpl
import dev.jn.enois.data.repository.UserProfileRepositoryImpl
import dev.jn.enois.domain.repository.AuthRepository
import dev.jn.enois.domain.repository.FeedRepository
import dev.jn.enois.domain.repository.UserProfileRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<FeedRepository> {
        FeedRepositoryImpl(get(), get())
    }

    single<UserProfileRepository> {
        UserProfileRepositoryImpl(get(), get())
    }
}