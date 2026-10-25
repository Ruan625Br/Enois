package dev.jn.enois.data.remote.error

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class ApiException(
    val statusCode: Int,
    val errorCode: String?,
    message: String
) : Exception(message)

suspend inline fun <reified T> HttpResponse.bodyOrThrow(fallback: String): T {
    if (!status.isSuccess()) {
        val error = runCatching { body<ApiError>() }.getOrNull()
        throw ApiException(status.value, error?.code, error?.message ?: fallback)
    }
    return body()
}
