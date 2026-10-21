package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.jn.enois.domain.model.FeedPost

@Composable
fun FeedPostItem(
    post: FeedPost, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
    ) {
        Text(
            text = post.authorUsername
        )
        Text(
            text = post.content
        )
    }
}