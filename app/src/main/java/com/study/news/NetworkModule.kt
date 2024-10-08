package com.study.news

import com.study.news.api.ApiInterceptor
import com.study.news.api.apiManagerHelper.RetrofitApi
import com.study.news.helper.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit.Builder {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .baseUrl(Constants.BASE_URI)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: ApiInterceptor): OkHttpClient {
        return OkHttpClient.Builder().connectTimeout(200, TimeUnit.SECONDS)
            .writeTimeout(200, TimeUnit.SECONDS)
            .readTimeout(200, TimeUnit.SECONDS).addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun providesLoginAPI(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): RetrofitApi {
        return retrofitBuilder.client(okHttpClient).build().create(RetrofitApi::class.java)
    }


}