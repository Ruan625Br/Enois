package dev.jn.enois.presentation.profile

import android.os.SystemClock
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.domain.repository.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.ConcurrentHashMap

sealed interface ProfileState {
    data class Loaded(val profile: UserProfile, val fetchedAt: Long) : ProfileState
    data class Failed(val at: Long) : ProfileState
}

class UserProfileCache(
    private val repository: UserProfileRepository,
    private val scope: CoroutineScope,
) {
    private val _states = MutableStateFlow<Map<String, ProfileState>>(emptyMap())
    private val inFlight = ConcurrentHashMap.newKeySet<String>()
    private val gate = Semaphore(4)

    fun observe(id: String): Flow<ProfileState?> =
        _states.map { it[id] }.distinctUntilChanged()

    fun put(profile: UserProfile) =
        _states.update { it + (profile.id to ProfileState.Loaded(profile, now())) }

    fun ensure(id: String) {
        if (!isStale(_states.value[id])) return
        if (!inFlight.add(id)) return
        scope.launch {
            try {
                val result = gate.withPermit { repository.getProfileById(id) }
                _states.update { map ->
                    val old = map[id] as? ProfileState.Loaded
                    val next: ProfileState = result.fold(
                        onSuccess = { ProfileState.Loaded(it, now()) },
                        onFailure = { old?.copy(fetchedAt = now()) ?: ProfileState.Failed(now()) },
                    )
                    map + (id to next)
                }
            } finally {
                inFlight.remove(id)
            }
        }
    }

    fun clear() {
        _states.value = emptyMap()
    }

    private fun isStale(s: ProfileState?) = when (s) {
        null -> true
        is ProfileState.Loaded -> now() - s.fetchedAt > TTL_MS
        is ProfileState.Failed -> now() - s.at > RETRY_MS
    }

    private fun now() = SystemClock.elapsedRealtime()

    private companion object {
        const val TTL_MS = 5 * 60_000L
        const val RETRY_MS = 30_000L
    }
}