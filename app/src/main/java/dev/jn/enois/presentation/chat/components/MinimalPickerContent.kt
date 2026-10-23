package dev.jn.enois.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.jn.enois.ui.icons.AppIcons

@Composable
fun MinimalPickerContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val importScope = rememberCoroutineScope()

    val leftButtonShape =
        RoundedCornerShape(topStart = 24.dp, topEnd = 10.dp, bottomStart = 24.dp, bottomEnd = 10.dp)
    val middleButtonShape = RoundedCornerShape(10.dp)
    val rightButtonShape =
        RoundedCornerShape(topStart = 10.dp, topEnd = 24.dp, bottomStart = 10.dp, bottomEnd = 24.dp)


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                MinimalFileButtonGroupedIconOnly(
                    icon = AppIcons.PhotoCamera,
                    shape = leftButtonShape,
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        onDismiss()
                    }
                )
            }

            MinimalFileButtonGroupedIconOnly(
                icon = AppIcons.Image,
                shape = middleButtonShape,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClick = {
                    onDismiss()
                }
            )

            MinimalFileButtonGroupedIconOnly(
                icon = AppIcons.Folder,
                shape = rightButtonShape,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClick = {
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun MinimalFileButtonGroupedIconOnly(
    icon: ImageVector,
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val buttonColor = MaterialTheme.colorScheme.surfaceContainerHighest

    Surface(
        onClick = onClick,
        shape = shape,
        color = buttonColor,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}