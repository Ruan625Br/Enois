package dev.jn.enois.presentation.media

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Stable
class MediaViewModel(
    private val mediaRepository: MediaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState = _uiState.asStateFlow()

    private val loadingJobs = mutableMapOf<String, Job>()


    fun loadMedia(ids: List<String>) {
        ids.distinct().forEach { id ->
            val currentState = _uiState.value.mediaById[id]

            if (currentState is MediaLoadState.Success ||
                currentState is MediaLoadState.Loading ||
                loadingJobs[id]?.isActive == true
            ) {
                return@forEach
            }

            _uiState.update { state ->
                state.copy(
                    mediaById = state.mediaById +
                            (id to MediaLoadState.Loading)
                )
            }

            loadingJobs[id] = viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = mediaRepository.getMedia(id)

                    result.fold(
                        onSuccess = { media ->
                            _uiState.update { state ->
                                state.copy(
                                    mediaById = state.mediaById +
                                            (id to MediaLoadState.Success(media))
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update { state ->
                                state.copy(
                                    mediaById = state.mediaById +
                                            (id to MediaLoadState.Error(
                                                error.message ?: "Unknown error"
                                            ))
                                )
                            }
                        }
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(
                            mediaById = state.mediaById +
                                    (id to MediaLoadState.Error(
                                        e.message ?: "Falha ao carregar mídia"
                                    ))
                        )
                    }
                } finally {
                    loadingJobs.remove(id)
                }
            }
        }
    }
}