package dev.jn.enois.data.repository

import dev.jn.enois.data.local.session.TokenStorage
import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.UserProfileApi
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.domain.repository.UserProfileRepository
import kotlin.coroutines.cancellation.CancellationException

class UserProfileRepositoryImpl(
    private val userProfileApi: UserProfileApi,
    private val tokenStorage: TokenStorage
) : UserProfileRepository {

    override suspend fun getCurrentProfile(): Result<UserProfile> {
        return try {
            val tokens = tokenStorage.getTokens()
                ?: error("No authenticated session")

            val response = userProfileApi.getCurrentUserProfile(tokens.accessToken)

            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}