package dev.jn.enois.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import androidx.navigation3.ui.defaultTransitionSpec
import dev.jn.enois.domain.model.SessionState
import dev.jn.enois.presentation.animation.LocalSharedTransitionScope
import dev.jn.enois.presentation.auth.login.LoginRoute
import dev.jn.enois.presentation.auth.register.RegisterRoute
import dev.jn.enois.presentation.chat.list.ConversationsRoute
import dev.jn.enois.presentation.chat.thread.ChatRoute
import dev.jn.enois.presentation.components.richtext.ProvideEmotes
import dev.jn.enois.presentation.feed.FeedRoute
import dev.jn.enois.presentation.media.viewer.MediaViewerRoute
import dev.jn.enois.presentation.postdetails.PostDetailRoute
import org.koin.androidx.compose.koinViewModel


private const val TRANSITION_KIND = "enois.transition.kind"
private val SharedElementMeta = mapOf<String, Any>(TRANSITION_KIND to "shared")
private val SlideMeta = mapOf<String, Any>(TRANSITION_KIND to "slide")

private fun Scene<NavKey>.kind(): Any? =
    entries.lastOrNull()?.metadata?.get(TRANSITION_KIND)

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {

    val sessionViewModel: SessionViewModel = koinViewModel()
    val sessionState by sessionViewModel.state.collectAsState()
    val emotes by sessionViewModel.emotes.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(AppScreen.SplashScreen)


    LaunchedEffect(sessionState) {
        val destination = when (sessionState) {
            SessionState.Loading ->
                AppScreen.SplashScreen

            SessionState.Unauthenticated ->
                AppScreen.LoginScreen

            is SessionState.Authenticated ->
                AppScreen.FeedScreen

            SessionState.RestoreFailed ->
                AppScreen.SplashScreen
        }
        if (backStack.lastOrNull() != destination) {
            backStack.clear()
            backStack.add(destination)
        }
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        SharedTransitionLayout {
            ProvideEmotes(emotes) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this
                ) {
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        },
                        transitionSpec = {
                            when (targetState.kind()) {
                                "shared" -> EnterTransition.None togetherWith ExitTransition.None
                                "slide" -> slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(300)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { -it / 4 },
                                    animationSpec = tween(300)
                                )

                                else -> defaultTransitionSpec<NavKey>().invoke(this)
                            }
                        },
                        popTransitionSpec = {
                            when (initialState.kind()) {
                                "shared" -> EnterTransition.None togetherWith ExitTransition.None
                                "slide" -> slideInHorizontally(
                                    initialOffsetX = { -it / 4 },
                                    animationSpec = tween(300)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(300)
                                )

                                else -> defaultPopTransitionSpec<NavKey>().invoke(this)
                            }
                        },
                        predictivePopTransitionSpec = { swipeEdge ->
                            when (initialState.kind()) {
                                "slide" -> slideInHorizontally(
                                    initialOffsetX = { -it / 4 },
                                    animationSpec = tween(300)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(300)
                                )

                                else -> EnterTransition.None togetherWith ExitTransition.None
                            }
                        },
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<AppScreen.SplashScreen> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            entry<AppScreen.LoginScreen> {
                                LoginRoute(
                                    onNavigateToRegister = {
                                        backStack.add(AppScreen.RegisterScreen)
                                    }
                                )
                            }
                            entry<AppScreen.RegisterScreen> {
                                RegisterRoute(
                                    onNavigateToLogin = {
                                        backStack.add(AppScreen.LoginScreen)
                                    }
                                )
                            }
                            entry<AppScreen.FeedScreen> {
                                FeedRoute(
                                    onNavigateToPostDetails = { postId ->
                                        backStack.add(AppScreen.PostDetailsScreen(postId))
                                    },
                                    onNavigateToMediaViewer = { mediaId ->
                                        backStack.add(AppScreen.MediaViewer(mediaId))
                                    },
                                    onNavigateToConversations = {
                                        backStack.add(AppScreen.ConversationsScreen)
                                    }
                                )
                            }

                            entry<AppScreen.PostDetailsScreen>(metadata = SharedElementMeta) { key ->
                                PostDetailRoute(
                                    postId = key.postId,
                                    onBack = {
                                        backStack.removeLastOrNull()
                                    },
                                    onNavigateToMediaViewer = { mediaId ->
                                        backStack.add(AppScreen.MediaViewer(mediaId))
                                    }
                                )
                            }

                            entry<AppScreen.MediaViewer>(metadata = SharedElementMeta) { key ->
                                MediaViewerRoute(
                                    mediaId = key.mediaId
                                )
                            }

                            entry<AppScreen.ChatScreen>(metadata = SlideMeta) { key ->
                                ChatRoute(
                                    conversationId = key.conversationId,
                                    onBack = {
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }

                            entry<AppScreen.ConversationsScreen>(metadata = SlideMeta) {
                                ConversationsRoute(
                                    onBack = {
                                        backStack.removeLastOrNull()
                                    },
                                    openConversation = { conversationId ->
                                        backStack.add(AppScreen.ChatScreen(conversationId))
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}