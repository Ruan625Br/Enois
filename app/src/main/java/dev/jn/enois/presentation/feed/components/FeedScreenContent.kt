package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.jn.enois.domain.model.FeedPost

@Composable
fun FeedScreenContent(
    posts: LazyPagingItems<FeedPost>,
    modifier: Modifier = Modifier) {

    LazyColumn(
        modifier = modifier
    ) {
        items(
            count = posts.itemCount,
            key = posts.itemKey { it.id }) { index ->

            posts[index]?.let { post ->
                FeedPostItem(post = post)
            }
        }

        item {
            when (posts.loadState.append) {
                is LoadState.Loading -> {
                    CircularProgressIndicator()
                }
                is LoadState.Error -> {
                    Button(onClick = posts::retry) {
                        Text(
                            text = "Retry"
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}