package dev.jn.enois.di

import dev.jn.enois.app.navigation.SessionViewModel
import dev.jn.enois.presentation.auth.login.LoginViewModel
import dev.jn.enois.presentation.auth.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::SessionViewModel)
}