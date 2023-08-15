package com.livefront.gsonkotlinadapter.sample.di

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.livefront.gsonkotlinadapter.KotlinReflectiveTypeAdapterFactory
import com.livefront.gsonkotlinadapter.sample.network.internal.adapter.NetworkResultCallAdapterFactory
import com.livefront.gsonkotlinadapter.sample.network.internal.service.RandomUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun providesGson(): Gson = GsonBuilder()
        .registerTypeAdapterFactory(KotlinReflectiveTypeAdapterFactory.create())
        .create()

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor {
        Log.i("HttpClient", it)
    }
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun providesOkHttpClient(
        app: Application,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(File(app.cacheDir, CACHE_FILE), CACHE_MB))
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(30L, TimeUnit.SECONDS)
        .addNetworkInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun providesRetrofit(
        client: OkHttpClient,
        gson: Gson,
    ): Retrofit = Retrofit.Builder()
        .client(client)
        .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_DOMAIN)
        .build()

    @Provides
    @Singleton
    fun providesUserService(
        retrofit: Retrofit,
    ): RandomUserService = retrofit.create(RandomUserService::class.java)

    companion object {
        /**
         * The base domain of the API.
         */
        private const val BASE_DOMAIN: String = "https://randomuser.me/"

        /**
         * Cache file name.
         */
        private const val CACHE_FILE: String = "http_cache"

        /**
         * 50 MB cache.
         */
        private const val CACHE_MB: Long = 50L * 1024L * 1024L
    }
}
