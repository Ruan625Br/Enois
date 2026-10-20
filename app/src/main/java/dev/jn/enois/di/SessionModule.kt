package dev.jn.enois.di

import dev.jn.enois.data.local.session.EncryptedTokenStorage
import dev.jn.enois.data.local.session.TokenStorage
import dev.jn.enois.data.repository.SessionRepositoryImpl
import dev.jn.enois.domain.repository.SessionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sessionModule = module {
    single<TokenStorage> {
        EncryptedTokenStorage(
            context = androidContext()
        )
    }

    single<SessionRepository> {
        SessionRepositoryImpl(
            authApi = get(),
            tokenStorage = get()
        )
    }
}