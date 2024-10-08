package com.study.news.api.apiManagerHelper

import com.study.news.R

object ResponseMessageManager {
    fun getMessage(error: NetworkError?): Int {
        return when (error) {
            NetworkError.NullData -> {
                R.string.null_data
            }

            NetworkError.InvalidResponseCode -> {
                R.string.invalid_response_code
            }

            NetworkError.NOT_FOUND -> {
                R.string.not_found
            }

            NetworkError.NO_INTERNET -> {
                R.string.no_internet_connection
            }

            NetworkError.Unauthorized -> R.string.unauthorized

            null -> {
                R.string.null_data
            }

        }
    }
}