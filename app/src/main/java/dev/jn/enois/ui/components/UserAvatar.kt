package dev.jn.enois.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
fun UserAvatar(
    url: String,
    userName: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    shape: Shape = MaterialTheme.shapes.medium
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
    var loadFallback by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(true) }

    Box(
        modifier
            .size(45.dp)
            .clip(shape)
    ) {
        if (isLoading) {
            UserAvatarFallback(
                userName = userName,
                style = textStyle,
            )
        }
        if (loadFallback) {
            UserAvatarFallback(
                userName = userName,
                style = textStyle,
            )
        } else {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                onLoading = {
                    isLoading = true
                },
                onSuccess = {
                    isLoading = false
                },
                onError = {
                    isLoading = false
                    loadFallback = true
                }
            )
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
            .background(
                MaterialTheme.colorScheme.primaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = style,
            color = MaterialTheme.colorScheme.onPrimaryContainer
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