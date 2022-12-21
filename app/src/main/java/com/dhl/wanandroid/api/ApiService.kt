package com.dhl.wanandroid.api

import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.model.BannerInfo
import com.dhl.wanandroid.model.HttpData
import com.youth.banner.Banner
import retrofit2.Response
import retrofit2.http.GET

/**
 * @Title: ApiService
 * @Package $
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
    suspend fun getBanner():Response<HttpData<List<BannerBean>>>

}