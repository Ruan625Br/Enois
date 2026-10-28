package dev.jn.enois.di

import dev.jn.enois.data.repository.SessionRepositoryImpl
import dev.jn.enois.domain.repository.SessionRepository
import org.koin.dsl.module

val sessionModule = module {
    single<SessionRepository> {
        SessionRepositoryImpl(
            authApi = get(),
            cookies = get()
        )
    }
}