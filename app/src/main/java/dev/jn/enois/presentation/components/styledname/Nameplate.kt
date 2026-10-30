package dev.jn.enois.presentation.components.styledname

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
internal fun Nameplate(
    art: NameplateArt,
    modifier: Modifier = Modifier,
    inline: Boolean = true,
    content: @Composable () -> Unit,
) {
    val iconSize = if (inline) 14.dp else 24.dp
    Box(
        modifier
            .clip(RoundedCornerShape(if (inline) 7.dp else 10.dp))
            .drawBehind { with(art.background) { paint() } }
    ) {
        Box(
            Modifier.padding(
                start = if (inline) 8.dp else 10.dp,
                end = if (inline) 34.dp else 58.dp,
                top = if (inline) 0.dp else 6.dp,
                bottom = if (inline) 0.dp else 6.dp,
            )
        ) { content() }

        Row(
            Modifier.align(Alignment.CenterEnd).padding(end = 4.dp).alpha(0.9f),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            art.icons.forEach { icon ->
                AsyncImage(
                    model = "file:///android_asset/icons/$icon.svg",
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}