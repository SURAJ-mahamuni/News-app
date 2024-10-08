package com.study.news.api.apiManagerHelper

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url


interface RetrofitApi {

    @GET
    suspend fun <T> getData(
        @Url path: String): Response<T>

    @POST
    suspend fun  postDataWithQuery(
        @Url path: String,
        @Body raw: Any
    ): Response<Any>


    @PUT
    suspend fun putMethod(
        @Body raw: Any,
        @Url path: String,
        @HeaderMap headers: Map<String, String>
    ): Response<Any>
    @POST
    suspend fun postMethod(
        @Body raw: Any,
        @Url path: String,
        @HeaderMap headers: Map<String, String>
    ): Response<Any>


    @POST
    suspend fun <T>multipart(
        @Body file: MultipartBody,
        @Url path: String
    ): Response<T>

}