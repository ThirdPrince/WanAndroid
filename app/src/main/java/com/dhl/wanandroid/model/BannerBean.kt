package com.dhl.wanandroid.model

import com.squareup.moshi.Json

/**
 * @Title: $
 * @Package $
 * @Description: $(用一句话描述)
 * @author $
 * @date $
 * @version V1.0
 */

data class HttpData<T>(
        @Json(name = "data") val data: T
)
data class BannerBean(@Json(name = "desc") val desc: String,
                  @Json(name = "id") val id: Int,
                  @Json(name = "imagePath") val imagePath: String,
                  @Json(name = "isVisible") val isVisible: Int,
                  @Json(name = "order") val order: Int,
                  @Json(name = "title") val title: String,
                  @Json(name = "type") val type: Int,
                  @Json(name = "url") val url: String)
