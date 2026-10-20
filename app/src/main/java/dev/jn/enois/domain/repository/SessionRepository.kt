package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.AuthSession
import dev.jn.enois.domain.model.SessionState
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val state: StateFlow<SessionState>

    suspend fun restoreSession()


    suspend fun updateSession(session: AuthSession)


    suspend fun logout()
}