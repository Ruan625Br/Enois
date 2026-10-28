package dev.jn.enois.di

import dev.jn.enois.app.navigation.SessionViewModel
import dev.jn.enois.presentation.auth.login.LoginViewModel
import dev.jn.enois.presentation.auth.register.RegisterViewModel
import dev.jn.enois.presentation.chat.ConversationCache
import dev.jn.enois.presentation.chat.list.ConversationsViewModel
import dev.jn.enois.presentation.chat.thread.ChatViewModel
import dev.jn.enois.presentation.components.comments.CommentsViewModel
import dev.jn.enois.presentation.feed.FeedViewModel
import dev.jn.enois.presentation.media.MediaViewModel
import dev.jn.enois.presentation.postdetails.PostDetailsCache
import dev.jn.enois.presentation.postdetails.PostDetailsViewModel
import dev.jn.enois.presentation.profile.CurrentUserStore
import dev.jn.enois.presentation.profile.UserProfileCache
import dev.jn.enois.presentation.reactions.ReactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    viewModelOf(::CommentsViewModel)
    viewModelOf(::ConversationsViewModel)
    viewModelOf(::ChatViewModel)

    single { PostDetailsCache() }
    single { UserProfileCache(get(), CoroutineScope(SupervisorJob() + Dispatchers.Default)) }
    single { CurrentUserStore(get(), get(), CoroutineScope(SupervisorJob() + Dispatchers.Default)) }
    single { ConversationCache() }
}