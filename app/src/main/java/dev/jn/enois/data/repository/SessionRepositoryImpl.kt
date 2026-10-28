package dev.jn.enois.data.repository

import dev.jn.enois.data.local.session.PersistentCookiesStorage
import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.AuthApi
import dev.jn.enois.data.remote.error.ApiException
import dev.jn.enois.domain.model.AuthSession
import dev.jn.enois.domain.model.SessionState
import dev.jn.enois.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.cancellation.CancellationException

class SessionRepositoryImpl(
    private val authApi: AuthApi,
    private val cookies: PersistentCookiesStorage,
) : SessionRepository {

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    override val state: StateFlow<SessionState> = _state.asStateFlow()

    override suspend fun updateSession(session: AuthSession) {
        _state.value = SessionState.Authenticated(session.user)
    }

    override suspend fun restoreSession() {
        _state.value = SessionState.Loading

        if (!cookies.hasRefreshCookie()) {
            _state.value = SessionState.Unauthenticated
            return
        }

        try {
            authApi.refreshToken()
            val user = authApi.getMyProfile().toDomain()
            _state.value = SessionState.Authenticated(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiException) {
            if (e.statusCode in listOf(400, 401, 403)) {
                val profile = runCatching { authApi.getMyProfile() }.getOrNull()
                if (profile != null) {
                    _state.value = SessionState.Authenticated(profile.toDomain())
                } else {
                    cookies.clear()
                    _state.value = SessionState.Unauthenticated
                }
            } else {
                _state.value = SessionState.RestoreFailed
            }
        } catch (_: Exception) {
            _state.value = SessionState.RestoreFailed
        }
    }

    override suspend fun logout() {
        runCatching { authApi.logout() }
        cookies.clear()
        _state.value = SessionState.Unauthenticated
    }
}