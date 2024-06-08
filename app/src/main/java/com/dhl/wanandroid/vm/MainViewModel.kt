package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dhl.wanandroid.adapter.ArticlePagingSource
import com.dhl.wanandroid.model.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * @Title: MainViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description:主页的ViewModel
 * @author dhl
 * @date 2022 12 21
 * @version V1.0
 * LiveData的postValue和setValue方法是protected，而MutableLiveData这两个方法则是public，
 * 也就是说 Livedata 只允许调用observe方法被动监听数据变化，而MutableLiveData除了监听变化外，还可以用postValue和setValue方法发射数据。
 */
class MainViewModel : BaseViewModel() {

    val tag = "MainViewModel"


    /**
     * banner LiveData
     */
    private val _resultBanner = MutableLiveData<RepoResult<MutableList<BannerBean>>>()

    val resultBanner: LiveData<RepoResult<MutableList<BannerBean>>>
        get() = _resultBanner


    /**
     * 文章LiveData
     */
    private val _resultArticle = MutableLiveData<RepoResult<MutableList<Article>>>()
    val resultArticle: LiveData<RepoResult<MutableList<Article>>>
        get() = _resultArticle


    /**
     * 获取Banner
     */
    fun getBanner(): LiveData<RepoResult<MutableList<BannerBean>>> {
        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultBanner.value = throwable.message?.let { RepoResult(it) }
            Log.e("CoroutinesViewModel", throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getBanner()
            Log.i(tag, " response=${response}")
            var data = response.body()?.data
            if (data != null) {
                _resultBanner.value = RepoResult(data!!, "")
            } else {
                _resultBanner.value = RepoResult(response.message())
            }

        }
        return resultBanner
    }
    fun getArticles(pageNum: Int = 0): Flow<PagingData<Article>> {
        val apiCall: suspend (Int) -> Response<HttpData<ArticleData>> = {
            fetchArticles(it)
        }
        return Pager(PagingConfig(pageSize = 20)) {
            ArticlePagingSource(apiCall)
        }.flow.cachedIn(viewModelScope)
    }

    private suspend fun fetchArticles(pageNum: Int): Response<HttpData<ArticleData>> = coroutineScope {
        val deferredTopArticles = if (pageNum == 0) async { api.getTopArticle() } else null
        val deferredArticles = async { api.getArticleList(pageNum) }

        val topArticlesResponse = deferredTopArticles?.await()
        val articlesResponse = deferredArticles.await()
        val articleList = mutableListOf<Article>()
        if (pageNum == 0) {
            if (topArticlesResponse?.isSuccessful == true && articlesResponse.isSuccessful) {
                val topArticles = topArticlesResponse.body()?.data?.map { it.apply { isTop = true } } ?: emptyList()
                val articles = articlesResponse.body()?.data?.datas ?: emptyList()
                articleList.addAll(articles)
                articleList.addAll(0,topArticles)
                val httpData = HttpData(ArticleData(0,datas = articleList,0,false,0,0,0))
                Response.success(httpData)
            } else {
                articlesResponse // Prioritize articlesResponse error if exists
            }
        } else {
            articlesResponse
        }
    }


}