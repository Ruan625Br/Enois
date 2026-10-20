package dev.jn.enois.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.jn.enois.presentation.auth.login.LoginRoute
import dev.jn.enois.presentation.auth.register.RegisterRoute

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {

    val loginBackStack = rememberNavBackStack()
    val backStack = remember { mutableStateListOf<Any>(AppScreen.LoginScreen) }

    val addToBackStack: (NavKey) -> Unit = {
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            entryProvider = entryProvider {
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
            }
        )
    }
}