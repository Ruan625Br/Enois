package dev.jn.enois.presentation.profile

import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.domain.repository.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class CurrentUserStore(
    private val repository: UserProfileRepository,
    private val profiles: UserProfileCache,
    private val scope: CoroutineScope,
) {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()
    val userId: StateFlow<String?> =
        profile.map { it?.id }.distinctUntilChanged().stateIn(scope, SharingStarted.Eagerly, null)

    private val loading = AtomicBoolean(false)

    fun ensureLoaded() {
        if (_profile.value != null || !loading.compareAndSet(false, true)) return
        scope.launch {
            repository.getCurrentProfile().onSuccess { me ->
                _profile.value = me
                profiles.put(me)
            }
            loading.set(false)
        }
    }

    fun set(profile: UserProfile) { _profile.value = profile; profiles.put(profile) }

    fun clear() { _profile.value = null }
}