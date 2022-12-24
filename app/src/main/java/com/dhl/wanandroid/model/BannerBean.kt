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


//文章
data class ArticleData(
        @Json(name = "curPage") val curPage: Int,
        @Json(name = "datas") var datas: MutableList<Article>,
        @Json(name = "offset") val offset: Int,
        @Json(name = "over") val over: Boolean,
        @Json(name = "pageCount") val pageCount: Int,
        @Json(name = "size") val size: Int,
        @Json(name = "total") val total: Int
)
data class Article(
        @Json(name = "apkLink") val apkLink: String,
        @Json(name = "audit") val audit: Int,
        @Json(name = "author") val author: String,
        @Json(name = "chapterId") val chapterId: Int,
        @Json(name = "chapterName") val chapterName: String,
        @Json(name = "collect") var collect: Boolean,
        @Json(name = "courseId") val courseId: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "envelopePic") val envelopePic: String,
        @Json(name = "fresh") val fresh: Boolean,
        @Json(name = "id") val id: Int,
        @Json(name = "link") val link: String,
        @Json(name = "niceDate") val niceDate: String,
        @Json(name = "niceShareDate") val niceShareDate: String,
        @Json(name = "origin") val origin: String,
        @Json(name = "prefix") val prefix: String,
        @Json(name = "projectLink") val projectLink: String,
        @Json(name = "publishTime") val publishTime: Long,
        @Json(name = "shareDate") val shareDate: String,
        @Json(name = "shareUser") val shareUser: String,
        @Json(name = "superChapterId") val superChapterId: Int,
        @Json(name = "superChapterName") val superChapterName: String,
        @Json(name = "tags") val tags: MutableList<Tag>,
        @Json(name = "title") val title: String,
        @Json(name = "type") val type: Int,
        @Json(name = "userId") val userId: Int,
        @Json(name = "visible") val visible: Int,
        @Json(name = "zan") val zan: Int,
        @Json(name = "top") var top: String
)
data class Tag(
        @Json(name = "name") val name: String,
        @Json(name = "url") val url: String
)
