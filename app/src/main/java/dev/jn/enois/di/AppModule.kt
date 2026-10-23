package dev.jn.enois.di

import org.koin.dsl.module

val appModules = module {
    includes(
        configModule,
        networkModule,
        repositoryModule,
        sessionModule,
        useCaseModule,
        presentationModule
    )
}