package dev.jn.enois.di

import dev.jn.enois.data.repository.AuthRepositoryImpl
import dev.jn.enois.domain.repository.AuthRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }
}