package dev.jn.enois.data.repository

import dev.jn.enois.data.local.session.TokenStorage
import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.AuthApi
import dev.jn.enois.data.remote.dto.auth.RefreshTokenRequest
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
    private val tokenStorage: TokenStorage
) : SessionRepository {

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)

    override val state: StateFlow<SessionState> =
        _state.asStateFlow()

    override suspend fun updateSession(session: AuthSession) {
        tokenStorage.saveTokens(
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
        )

        _state.value = SessionState.Authenticated(session.user)
    }

    override suspend fun restoreSession() {
        _state.value = SessionState.Loading

        val tokens = tokenStorage.getTokens()

        if (tokens == null) {
            _state.value = SessionState.Unauthenticated
            return
        }

        try {
            val response = authApi.refreshToken(
                RefreshTokenRequest(
                    refreshToken = tokens.refreshToken,
                )
            )
            val session = response.toDomain()
            tokenStorage.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken,
            )

            _state.value = SessionState.Authenticated(session.user)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: ApiException) {
            if (exception.statusCode == 401 ||
                exception.statusCode == 403
            ) {
                tokenStorage.clear()
                _state.value = SessionState.Unauthenticated
            } else {
                _state.value = SessionState.RestoreFailed
            }
        } catch (_: Exception) {
            _state.value = SessionState.RestoreFailed
        }
    }

    override suspend fun logout() {
        tokenStorage.clear()
        _state.value = SessionState.Unauthenticated
    }
}