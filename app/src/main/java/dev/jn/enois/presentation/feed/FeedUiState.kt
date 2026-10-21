package dev.jn.enois.presentation.feed

import dev.jn.enois.domain.model.FeedTab
import dev.jn.enois.domain.model.UserProfile

data class FeedUiState(
    val userProfile: UserProfile? = null,
    val selectedTab: FeedTab = FeedTab.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)