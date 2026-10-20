package dev.jn.enois.data.remote.error

class ApiException(
    val statusCode: Int,
    val errorCode: String?,
    message: String
) : Exception(message)