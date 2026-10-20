package dev.jn.enois.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jn.enois.domain.repository.SessionRepository
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionRepository: SessionRepository
): ViewModel() {

    val state = sessionRepository.state

    init {
        viewModelScope.launch {
            sessionRepository.restoreSession()
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