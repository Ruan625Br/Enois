package dev.jn.enois.data.repository

import kotlinx.coroutines.CancellationException

abstract class BaseRepository {

    protected suspend inline fun <T> apiCall(
        crossinline block: suspend () -> T
    ): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}