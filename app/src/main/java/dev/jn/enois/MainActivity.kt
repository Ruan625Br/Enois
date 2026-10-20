package dev.jn.enois

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.jn.enois.app.navigation.MainNavigation
import dev.jn.enois.ui.theme.EnoisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnoisTheme {
                MainNavigation()
            }
        }
    }
}
