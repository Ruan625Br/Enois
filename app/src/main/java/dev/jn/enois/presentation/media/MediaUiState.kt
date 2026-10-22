package dev.jn.enois.presentation.media

import androidx.compose.runtime.Stable
import dev.jn.enois.domain.model.Media

sealed interface MediaLoadState {
    data object Loading : MediaLoadState
    data class Success(val media: Media) : MediaLoadState
    data class Error(val message: String) : MediaLoadState
}

@Stable
data class MediaUiState(
    val mediaById: Map<String, MediaLoadState> = emptyMap()
)