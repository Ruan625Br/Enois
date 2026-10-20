package dev.jn.enois.domain.model

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState

    data class Authenticated(
        val user: User
    ) : SessionState

    data object RestoreFailed : SessionState

}