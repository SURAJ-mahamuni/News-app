package com.study.news.api.apiManagerHelper

import com.study.news.api.EndPointItems
import com.study.news.api.endPointHelper.HttpMethod
import okhttp3.MultipartBody
import retrofit2.Response

object NetworkCallingHandler {
    suspend fun networkCall(
        endPointItems: EndPointItems,
        api: RetrofitApi,
        postData: Any,
        path: String = "",
    ): Response<Any> {
        return when (endPointItems.httpMethod) {

            HttpMethod.POST -> {
                if (path.isNullOrEmpty()) {
                    api.postMethod(
                        path = endPointItems.path,
                        headers = endPointItems.header,
                        raw = postData
                    )
                } else {
                    api.postDataWithQuery(
                        path = path,
                        raw = postData
                    )
                }
            }

            HttpMethod.PUT -> {
                api.putMethod(
                    path = endPointItems.path,
                    headers = endPointItems.header,
                    raw = postData
                )
            }

            else -> {
                api.postMethod(
                    path = endPointItems.path,
                    headers = endPointItems.header,
                    raw = postData
                )
            }
        }
    }

    suspend fun <T> networkCall(
        endPointItems: EndPointItems,
        api: RetrofitApi,
        postData: MultipartBody,
        path: String,
    ): Response<T> {
        return when (endPointItems.httpMethod) {

            HttpMethod.MULTIPART_POST -> {
                if (path.isNotEmpty()) {
                    api.multipart(
                        path = path,
                        file = postData
                    )
                } else {
                    api.multipart(
                        path = endPointItems.path,
                        file = postData
                    )
                }
            }

            else -> {
                api.multipart(
                    path = path,
                    file = postData
                )
            }
        }
    }

    suspend fun <T> networkCall(
        endPointItems: EndPointItems,
        api: RetrofitApi,
        path: String,
    ): Response<T> {
        return when (endPointItems.httpMethod) {
            HttpMethod.GET -> {
                if (path.isNullOrEmpty()) {
                    api.getData<T>(endPointItems.path)
                } else {
                    api.getData<T>(path)
                }
            }

            else -> {
                api.getData<T>(endPointItems.path)
            }
        }
    }
}