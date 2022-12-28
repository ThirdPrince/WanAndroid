package com.dhl.wanandroid.api

import com.dhl.wanandroid.model.*
import com.youth.banner.Banner
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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
     * 主页Banner
     */
    @GET("banner/json")
    suspend fun getBanner():Response<HttpData<MutableList<BannerBean>>>

    /**
     * 主页文章
     */
    @GET("article/list/{pageNum}/json")
    suspend fun getArticleList(@Path("pageNum")pageNum:Int):Response<HttpData<ArticleData>>

    /**
     * 知识体系
     */
    @GET("tree/json")
    suspend fun getKnowledge():Response<HttpData<MutableList<KnowledgeTreeData>>>

    /**
     *
     */
    /**
     * 主页文章
     */
    @GET("article/list/{pageNum}/json")
    suspend fun getKnowledgeArticleList(@Path("pageNum")pageNum:Int,@Query("cid")cid:Int):Response<HttpData<ArticleData>>

}