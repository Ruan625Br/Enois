package dev.jn.enois.data.local.session

data class StoredToken(
    val accessToken: String,
    val refreshToken: String,
)

interface TokenStorage {
    suspend fun getTokens(): StoredToken?

    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun clear()
}