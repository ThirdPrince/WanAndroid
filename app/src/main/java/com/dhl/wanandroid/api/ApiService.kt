package com.dhl.wanandroid.api

import com.dhl.wanandroid.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * @Title: ApiService
 * @Package com.dhl.wanandroid.api
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
    suspend fun getBanner(): Response<HttpData<MutableList<BannerBean>>>


    /**
     * top 置顶
7     * /top/json
     */
    @GET("article/top/json")
    suspend fun getTopArticle(): Response<HttpData<MutableList<Article>>>

    /**
     * 主页文章
     *  https://wanandroid.com/article/list/1/json
     */
    @GET("article/list/{pageNum}/json")
    suspend fun getArticleList(@Path("pageNum") pageNum: Int): Response<HttpData<ArticleData>>

    /**
     * 知识体系
     */
    @GET("tree/json")
    suspend fun getKnowledge(): Response<HttpData<MutableList<KnowledgeTreeData>>>


    /**
     * 知识体系文章
     *
     */
    @GET("article/list/{pageNum}/json")
    suspend fun getKnowledgeArticleList(
        @Path("pageNum") pageNum: Int,
        @Query("cid") cid: Int
    ): Response<HttpData<ArticleData>>


    /**
     * 微信公众号
     */
    @GET("wxarticle/chapters/json")
    suspend fun getWxArticleChapters(): Response<HttpData<MutableList<BaseData>>>

    /**
     * 微信公众号文章
     * https://wanandroid.com/wxarticle/list/408/1/json
     */
    @GET("wxarticle/list/{id}/{pageNum}/json")
    suspend fun getWxArticleList(
        @Path("id") id: Int,
        @Path("pageNum") pageNum: Int
    ): Response<HttpData<ArticleData>>


    /**
     * Nav 导航
     * https://wanandroid.com/navi/json
     */
    @GET("navi/json")
    suspend fun getNav(): Response<HttpData<MutableList<NavBean>>>

    /**
     * 项目
     */
    @GET("project/tree/json")
    suspend fun getProject(): Response<HttpData<MutableList<BaseData>>>

    /**
     * 搜索热词
     */
    @GET("hotkey/json")
    suspend fun getHotSearch(): Response<HttpData<MutableList<HotSearchBean>>>

    /**
     * 搜索
     */

    @POST("article/query/{page}/json")
    @FormUrlEncoded
    suspend fun getSearchKey(
        @Path("page") page: Int,
        @Field("k") key: String
    ): Response<HttpData<ArticleData>>

    /**
     * 广场
     *  https://wanandroid.com/user_article/list/0/json
     */
    @GET("article/list/{pageNum}/json")
    suspend fun getSquareList(@Path("pageNum") pageNum: Int): Response<HttpData<ArticleData>>

}