package dev.jn.enois.presentation.components.richtext

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SpoilerMedia(active: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    if (!active) {
        content(); return
    }

    var revealed by remember(active) { mutableStateOf(false) }
    val canBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Box(modifier.clip(RoundedCornerShape(12.dp))) {
        Box(if (!revealed && canBlur) Modifier.blur(24.dp) else Modifier) { content() }

        if (!revealed) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = if (canBlur) 0.45f else 0.97f))
                    .clickable(onClickLabel = "Mostrar conteúdo") { revealed = true },
                contentAlignment = Alignment.Center,
            ) {
                Text("Mostrar conteúdo", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}