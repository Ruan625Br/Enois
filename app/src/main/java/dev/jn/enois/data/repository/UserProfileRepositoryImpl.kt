package dev.jn.enois.data.repository

import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.UserProfileApi
import dev.jn.enois.domain.model.UserProfile
import dev.jn.enois.domain.repository.UserProfileRepository
import kotlin.coroutines.cancellation.CancellationException

class UserProfileRepositoryImpl(
    private val userProfileApi: UserProfileApi,
) : UserProfileRepository {

    override suspend fun getCurrentProfile(): Result<UserProfile> {
        return try {
            val response = userProfileApi.getCurrentUserProfile()
            Result.success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}