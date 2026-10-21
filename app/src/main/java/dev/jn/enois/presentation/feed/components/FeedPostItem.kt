package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.FeedPost

@Composable
fun FeedPostItem(
    post: FeedPost, modifier: Modifier = Modifier) {

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            PostHeader(post = post)
            Text(
                text = post.content
            )

        }
    }
}

@Composable
private fun PostHeader(
    post: FeedPost,
    modifier: Modifier = Modifier) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        UserAvatar(
            url = post.authorAvatar ?: ""
        )
        Text(
            text = post.authorUsername
        )
    }
}