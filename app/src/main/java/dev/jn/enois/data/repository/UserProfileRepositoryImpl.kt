package dev.jn.enois.data.repository

import dev.jn.enois.data.mapper.toDomain
import dev.jn.enois.data.remote.api.UserProfileApi
import dev.jn.enois.domain.repository.UserProfileRepository

class UserProfileRepositoryImpl(
    private val userProfileApi: UserProfileApi,
) : UserProfileRepository, BaseRepository() {

    override suspend fun getCurrentProfile() = apiCall {
        val response = userProfileApi.getCurrentUserProfile()
        response.toDomain()
    }

    override suspend fun getProfileById(id: String) = apiCall {
        val response = userProfileApi.getById(id)
        response.toDomain()
    }
}