package com.study.news.api.apiManagerHelper


sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val error: NetworkError? = null,
) {
    class Success<T>(data: T?) : NetworkResult<T>(data)

    class Error<T>(message: String?, data: T? = null, error: NetworkError) :
        NetworkResult<T>(data, message, error)

    class Loading<T> : NetworkResult<T>()
}