package dev.jn.enois.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.jn.enois.domain.model.SessionState
import dev.jn.enois.presentation.auth.login.LoginRoute
import dev.jn.enois.presentation.auth.register.RegisterRoute
import dev.jn.enois.presentation.feed.FeedRoute
import dev.jn.enois.presentation.postdetails.PostDetailRoute
import dev.jn.enois.ui.animation.LocalSharedTransitionScope
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {

    val sessionViewModel: SessionViewModel = koinViewModel()
    val sessionState by sessionViewModel.state.collectAsState()
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
                    popTransitionSpec = {
                        EnterTransition.None togetherWith ExitTransition.None
                    },
                    predictivePopTransitionSpec = {
                        EnterTransition.None togetherWith ExitTransition.None
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
                            )
                        }

                        entry<AppScreen.PostDetailsScreen> { key ->
                            PostDetailRoute(
                                postId = key.postId,
                                onBack = {
                                    backStack.removeLastOrNull()
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}