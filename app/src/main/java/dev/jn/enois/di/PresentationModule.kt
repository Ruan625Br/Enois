package dev.jn.enois.di

import dev.jn.enois.app.navigation.SessionViewModel
import dev.jn.enois.presentation.auth.login.LoginViewModel
import dev.jn.enois.presentation.auth.register.RegisterViewModel
import dev.jn.enois.presentation.feed.FeedViewModel
import dev.jn.enois.presentation.media.MediaViewModel
import dev.jn.enois.presentation.postdetails.PostDetailsCache
import dev.jn.enois.presentation.postdetails.PostDetailsViewModel
import dev.jn.enois.presentation.reactions.ReactionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::SessionViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::PostDetailsViewModel)
    viewModelOf(::MediaViewModel)
    viewModelOf(::ReactionViewModel)

    single { PostDetailsCache() }
}