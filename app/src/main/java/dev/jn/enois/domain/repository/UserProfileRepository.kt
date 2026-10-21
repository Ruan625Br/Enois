package dev.jn.enois.domain.repository

import dev.jn.enois.domain.model.UserProfile

interface UserProfileRepository {

    suspend fun getCurrentProfile(): Result<UserProfile>
}