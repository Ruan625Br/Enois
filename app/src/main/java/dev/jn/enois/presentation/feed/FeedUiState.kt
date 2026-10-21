package dev.jn.enois.presentation.feed

import dev.jn.enois.domain.model.FeedPost
import dev.jn.enois.domain.model.FeedTab

data class FeedUiState(
    val posts: List<FeedPost> = emptyList(),
    val selectedTab: FeedTab = FeedTab.ALL,
    val nextCursor: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)