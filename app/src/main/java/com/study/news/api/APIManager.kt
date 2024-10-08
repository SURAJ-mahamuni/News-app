package com.study.news.api

import android.content.Context
import com.study.news.helper.NetworkUtils
import com.study.news.api.apiManagerHelper.NetworkCallingHandler
import com.study.news.api.apiManagerHelper.ResponseHandler
import com.study.news.api.apiManagerHelper.RetrofitApi
import com.study.news.api.apiManagerHelper.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject


class APIManager
@Inject constructor(
    private val api: RetrofitApi,
    @ApplicationContext private val appContext: Context,
) {

    //this is for get request also you can add value in end point
    suspend fun <T> request(
        endPointItems: EndPointItems,
        path: String = "",
    ): NetworkResult<T> {

        val networkCallingHandler = if (NetworkUtils.isInternetAvailable(appContext)) {
            try {
                NetworkCallingHandler.networkCall<T>(endPointItems, api, path)
            } catch (e: Exception) {
                null
            }
        } else null

        return ResponseHandler.getResponse(networkCallingHandler)
    }


    suspend fun request(
        endPointItems: EndPointItems,
        postData: Any,
    ): Response<Any>? {

        try {
            val networkCallingHandler = if (NetworkUtils.isInternetAvailable(appContext)) {
                NetworkCallingHandler.networkCall(endPointItems, api, postData)
            } else {
                Response.error(500, null)
            }
            return networkCallingHandler
        } catch (e: Exception) {
            return null
        }

    }

    suspend fun <T, R> request(
        endPointItems: EndPointItems,
        postData: Any,
        path: String = "",
    ): Response<Any>? {

        try {
            val networkCallingHandler = if (NetworkUtils.isInternetAvailable(appContext)) {
                NetworkCallingHandler.networkCall(endPointItems, api, postData, path)
            } else {
                Response.error(500, null)
            }
            return networkCallingHandler
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun <T> request(
        endPointItems: EndPointItems,
        postData: MultipartBody,
        path: String = "",
    ): Response<T>? {

        try {
            val networkCallingHandler = if (NetworkUtils.isInternetAvailable(appContext)) {
                NetworkCallingHandler.networkCall<T>(endPointItems, api, postData, path)
            } else {
                Response.error(500, null)
            }
            return networkCallingHandler
        } catch (e: Exception) {
            return null
        }
    }

}