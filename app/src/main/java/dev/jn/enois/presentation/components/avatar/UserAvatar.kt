package dev.jn.enois.presentation.components.avatar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import dev.jn.enois.domain.model.AvatarFrameType
import dev.jn.enois.presentation.components.avatar.fx.AvatarFrameFx
import dev.mttmcknn.seaglass.seaGlass

@Composable
fun UserAvatar(
    url: String,
    userName: String,
    modifier: Modifier = Modifier,
    frame: AvatarFrameType? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    shape: Shape = MaterialTheme.shapes.medium,
    size: Dp = 40.dp,
) {

    val context = LocalPlatformContext.current

    val urlResolved = remember(url) {
        resolveCdnUrl(url)
    }

    val model = remember(context, urlResolved) {
        ImageRequest.Builder(context)
            .data(urlResolved)
            .build()
    }
    var failed by remember(urlResolved) { mutableStateOf(false) }
    var loading by remember(urlResolved) { mutableStateOf(true) }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .matchParentSize()
                .clip(if (frame != null) CircleShape else shape),
        ) {
            if (loading || failed) {
                UserAvatarFallback(userName = userName, style = textStyle)
            }

            if (!failed) {
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    onLoading = { loading = true },
                    onSuccess = { loading = false },
                    onError = {
                        loading = false
                        failed = true
                    },
                )
            }
        }

        if (frame != null) {
            AvatarFrameFx(frame = frame, modifier = Modifier.requiredSize(size * 1.6f))
        }
    }
}

@Composable
fun UserAvatarFallback(
    userName: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
) {

    val initials = remember(userName) {
        getUserNameInitials(userName)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .seaGlass(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = style,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getUserNameInitials(name: String): String {
    val parts = name.trim().split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

    if (parts.isEmpty()) return ""
    if (parts.size == 1) return parts[0].first().uppercase()

    val first = parts.first().first()
    val last = parts.last().first()

    return "$first$last".uppercase()
}

fun resolveCdnUrl(url: String): String {
    val cleaned = url.replace("%2F", "/")

    if (cleaned.startsWith("http://") ||
        cleaned.startsWith("https://")
    ) {
        return cleaned
    }

    val baseUrl = "https://ik.imagekit.io/socialcoisa"
    val separator = if (cleaned.startsWith("/")) "" else "/"

    return "$baseUrl$separator$cleaned"
}