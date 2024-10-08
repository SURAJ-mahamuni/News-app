package com.study.news.api.apiManagerHelper

import retrofit2.Response

object ResponseHandler {
    fun <T> getResponse(response: Response<T>?): NetworkResult<T> {
        return when {
            response == null -> {
                NetworkResult.Error(
                    message = "No Internet connection",
                    error = NetworkError.NO_INTERNET
                )
            }

            response?.code() == 1 -> {
                NetworkResult.Error(
                    message = response.message(),
                    error = NetworkError.Unauthorized
                )
            }
            response?.code() == 400 -> {
                NetworkResult.Error(
                    message = response.message(),
                    error = NetworkError.Unauthorized
                )
            }
            response?.code() == 500 -> {
                NetworkResult.Error(
                    message = "Something went wrong.",
                    error = NetworkError.NullData
                )
            }

            response?.code() == 401 || response.code() == 403 -> {
                NetworkResult.Error(
                    message = "Unauthorized",
                    error = NetworkError.Unauthorized
                )
            }

            response?.code() == 404 -> {
                NetworkResult.Error(
                    "Not found", response.body(), NetworkError.NOT_FOUND
                )
            }

            response?.isSuccessful ?: false && response?.body() != null -> {
                NetworkResult.Success(response.body())
            }

            response?.errorBody() != null -> {
                NetworkResult.Error(response.message(), response.body(), NetworkError.NullData)
            }


            response?.code() !in 200..299 -> {
                NetworkResult.Error(
                    response?.message(), response?.body(), NetworkError.InvalidResponseCode
                )
            }


            else -> {
                NetworkResult.Error(response?.message(), response?.body(), NetworkError.NullData)
            }
        }
    }
}