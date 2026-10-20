package dev.jn.enois.di

import dev.jn.enois.domain.usecase.LoginUseCase
import dev.jn.enois.domain.usecase.LogoutUseCase
import dev.jn.enois.domain.usecase.SignUpUseCase
import org.koin.dsl.module


val useCaseModule = module {
    factory { LoginUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { LogoutUseCase(get()) }
}
