package dev.jn.enois.presentation.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.jn.enois.domain.model.FeedPost

@Composable
fun FeedScreenContent(
    posts: LazyPagingItems<FeedPost>,
    listState: LazyListState,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {

    val contentPadding = PaddingValues(
        horizontal = 8.dp,
        vertical = 0.dp
    ) + innerPadding

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier,
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
}