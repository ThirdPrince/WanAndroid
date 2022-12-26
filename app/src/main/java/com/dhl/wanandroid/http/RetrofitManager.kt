package com.dhl.wanandroid.http

import com.dhl.wanandroid.api.ApiService
import com.dhl.wanandroid.app.Constants
import com.dhl.wanandroid.app.MyApplication
import com.dhl.wanandroid.http.interceptor.CacheInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * @Title: RetrofitManager
 * @Package $
 * @Description: Retrofit 基本封装
 * @author dhl
 * @date 2022-1220
 * @version V2.0
 */
object RetrofitManager {

    private const val CONNECTION_TIME_OUT = 10L
    private const val READ_TIME_OUT = 10L

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    /**
     * 懒加载
     */
    private val retrofit: Retrofit by lazy {
        buildRetrofit(Constants.BASE_URL, buildHttpClient())
    }

    private val cacheFile = File(MyApplication.context.externalCacheDir, "cache")
    private val cache = Cache(cacheFile, 5 * 1024 * 1024)


    /**
     * 构建自己的OKHttp
     */
    private fun buildHttpClient(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(CacheInterceptor())
                .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .cache(cache)
                .proxy(Proxy.NO_PROXY)

    }

    /**
     * 构建 Retrofit
     */
    private fun buildRetrofit(baseUrl: String, build: OkHttpClient.Builder): Retrofit {

        val client = build.build()
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client).build()
    }

}