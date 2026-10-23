package dev.jn.enois.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppScreen : NavKey {

    @Serializable
    data object SplashScreen : AppScreen
    @Serializable
    data object LoginScreen : AppScreen
    @Serializable
    data object RegisterScreen : AppScreen

    @Serializable
    data object FeedScreen : AppScreen

    @Serializable
    data class PostDetailsScreen(val postId: String) : AppScreen

    @Serializable
    data class MediaViewer(val mediaId: String) : AppScreen

    @Serializable
    data object ChatScreen : AppScreen
}
