package com.study.news.api

import android.util.Log
import com.study.news.helper.requestToCurl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ApiInterceptor @Inject constructor() :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // we can write log url here
        val request = chain.request().newBuilder()

        request.addHeader("Content-Type", "application/json")

        try {
            Log.e("CURL : ", requestToCurl(request.build()))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // add token here with the header
        return chain.proceed(request.build())
    }

}