package com.dhl.wanandroid.api

import com.dhl.wanandroid.model.*
import com.youth.banner.Banner
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @Title: ApiService
 * @Package
 * @Description: api
 * @author dhl
 * @date 2022 12-20
 * @version V2.0
 */
interface ApiService {


    /**
     * 获取Banner
     */
    @GET("banner/json")
    suspend fun getBanner():Response<HttpData<MutableList<BannerBean>>>

    @GET("article/list/{pageNum}/json")
    suspend fun getArticleList(@Path("pageNum")pageNum:Int):Response<HttpData<ArticleData>>


}