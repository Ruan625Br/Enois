package dev.jn.enois.presentation.media

import dev.jn.enois.domain.model.Media

sealed interface MediaLoadState {
    data object Loading : MediaLoadState
    data class Success(val media: Media) : MediaLoadState
    data class Error(val message: String) : MediaLoadState
}

data class MediaUiState(
    val mediaById: Map<String, MediaLoadState> = emptyMap()
)