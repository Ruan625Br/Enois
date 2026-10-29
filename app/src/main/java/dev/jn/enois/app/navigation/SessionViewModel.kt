package dev.jn.enois.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.repository.EmoteRepository
import dev.jn.enois.domain.repository.SessionRepository
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionRepository: SessionRepository,
    private val emoteRepository: EmoteRepository
): ViewModel() {

    val state = sessionRepository.state
    val emotes = emoteRepository.index

    init {
        viewModelScope.launch {
            sessionRepository.restoreSession()
            emoteRepository.refresh()
        }
    }

    fun retryRestore() {
        viewModelScope.launch {
            sessionRepository.restoreSession()
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionRepository.logout()
        }
    }

}