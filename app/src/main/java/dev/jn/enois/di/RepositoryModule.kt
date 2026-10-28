package dev.jn.enois.di

import dev.jn.enois.data.repository.AuthRepositoryImpl
import dev.jn.enois.data.repository.CommentRepositoryImpl
import dev.jn.enois.data.repository.FeedRepositoryImpl
import dev.jn.enois.data.repository.MediaRepositoryImpl
import dev.jn.enois.data.repository.MessageRepositoryImpl
import dev.jn.enois.data.repository.ReactionRepositoryImpl
import dev.jn.enois.data.repository.UserProfileRepositoryImpl
import dev.jn.enois.domain.model.CommentRepository
import dev.jn.enois.domain.repository.AuthRepository
import dev.jn.enois.domain.repository.FeedRepository
import dev.jn.enois.domain.repository.MediaRepository
import dev.jn.enois.domain.repository.MessageRepository
import dev.jn.enois.domain.repository.ReactionRepository
import dev.jn.enois.domain.repository.UserProfileRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<FeedRepository> {
        FeedRepositoryImpl(get())
    }

    single<UserProfileRepository> {
        UserProfileRepositoryImpl(get())
    }

    single<MediaRepository> {
        MediaRepositoryImpl(get())
    }

    single<ReactionRepository> {
        ReactionRepositoryImpl(get())
    }

    single<CommentRepository> { CommentRepositoryImpl(get()) }

    single<MessageRepository> { MessageRepositoryImpl(get()) }
}