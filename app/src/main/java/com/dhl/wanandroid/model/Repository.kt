package com.dhl.wanandroid.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

/**
 * @Title: 基本数据类
 * @Package com.dhl.wanandroid.model
 * @Description: 基本Bean
 * @author dhl
 * @date 2022 12 21
 * @version V2.0
 */

/**
 * 启动页
 */
@Entity
data class ImageSplash(@PrimaryKey val id: Int, var url: String,
                       val copyright: String, var imagePath: String)

data class HttpData<T>(
        @Json(name = "data") val data: T
)

/**
 * 玩android 基本数据
 */
data class BaseData(
        @Json(name = "children") val children: List<Any>,
        @Json(name = "courseId") val courseId: Int,
        @Json(name = "cover") val cover: String,
        @Json(name = "desc") val desc: String,
        @Json(name = "id") val id: Int,
        @Json(name = "lisense") val lisense: String,
        @Json(name = "lisenseLink") val lisenseLink: String,
        @Json(name = "name") val name: String,
        @Json(name = "order") val order: Int,
        @Json(name = "parentChapterId") val parentChapterId: Int,
        @Json(name = "visible") val visible: Int
)

/**
 * 首页的Banner
 */
data class BannerBean(@Json(name = "desc") val desc: String,
                      @Json(name = "id") val id: Int,
                      @Json(name = "imagePath") val imagePath: String,
                      @Json(name = "isVisible") val isVisible: Int,
                      @Json(name = "order") val order: Int,
                      @Json(name = "title") val title: String,
                      @Json(name = "type") val type: Int,
                      @Json(name = "url") val url: String)


/**
 * 包装了 MutableList<Article>
 */
data class ArticleData(
        @Json(name = "curPage") val curPage: Int,
        @Json(name = "datas") var datas: MutableList<Article>,
        @Json(name = "offset") val offset: Int,
        @Json(name = "over") val over: Boolean,
        @Json(name = "pageCount") val pageCount: Int,
        @Json(name = "size") val size: Int,
        @Json(name = "total") val total: Int
)

/**
 * 文章
 */
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
        @Json(name = "top") var isTop: Boolean
)

/**
 * 知识体系Tree
 */
data class KnowledgeTreeData(
        @Json(name = "children") val children: MutableList<Knowledge>,
        @Json(name = "courseId") val courseId: Int,
        @Json(name = "cover") val cover: String,
        @Json(name = "desc") val desc: String,
        @Json(name = "id") val id: Int,
        @Json(name = "lisense") val lisense: String,
        @Json(name = "lisenseLink") val lisenseLink: String,
        @Json(name = "name") val name: String,
        @Json(name = "order") val order: Int,
        @Json(name = "parentChapterId") val parentChapterId: Int,
        @Json(name = "visible") val visible: Int
) : Serializable

data class Knowledge(
        @Json(name = "children") val children: List<Any>,
        @Json(name = "courseId") val courseId: Int,
        @Json(name = "cover") val cover: String,
        @Json(name = "desc") val desc: String,
        @Json(name = "id") val id: Int,
        @Json(name = "lisense") val lisense: String,
        @Json(name = "lisenseLink") val lisenseLink: String,
        @Json(name = "name") val name: String,
        @Json(name = "order") val order: Int,
        @Json(name = "parentChapterId") val parentChapterId: Int,
        @Json(name = "visible") val visible: Int
) : Serializable

/**
 * 导航 Bean
 */
data class NavBean(@Json(name = "articles") val articles: MutableList<Article>,
                   @Json(name = "cid") val cid: Int,
                   @Json(name = "name") val name: String)

/**
 * 搜索热词
 */
data class HotSearchBean(
        @Json(name = "id") val id: Int,
        @Json(name = "link")val link: String,
        @Json(name = "name")val name: String,
        @Json(name = "order")val order: Int,
        @Json(name = "visible") val visible: Int
)


data class Tag(
        @Json(name = "name") val name: String,
        @Json(name = "url") val url: String
)


