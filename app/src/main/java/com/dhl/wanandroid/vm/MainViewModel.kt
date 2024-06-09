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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
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
    private val _resultArticle = MutableLiveData<List<Article>>()
    val resultArticle: LiveData<List<Article>>
        get() = _resultArticle


    private val _articles = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val articles: StateFlow<PagingData<Article>> = _articles.asStateFlow()
    private val articleListCache = mutableListOf<Article>()

    init {
        viewModelScope.launch {
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

        }

    }

    /**
     * 获取Banner
     */
    fun getBanner(): LiveData<RepoResult<MutableList<BannerBean>>> {

        return resultBanner
    }

    fun getArticles(): Flow<PagingData<Article>> {
        val apiCall: suspend (Int) -> Response<HttpData<ArticleData>> = { page->
            api.getArticleList(page)
        }
        return Pager(PagingConfig(pageSize = 20)) {
            ArticlePagingSource(apiCall, cache = articleListCache)
        }.flow.cachedIn(viewModelScope)
    }

    fun getTopArticles(): LiveData<List<Article>> {
        viewModelScope.launch {
            val topArticlesResponse = api.getTopArticle()
            if (topArticlesResponse?.isSuccessful) {
                val topArticles =
                    topArticlesResponse.body()?.data?.map { it.apply { isTop = true } }
                        ?: emptyList()
                _resultArticle.value = topArticles
            }

        }
       return resultArticle
    }


    private suspend fun fetchArticles(pageNum: Int): Response<HttpData<ArticleData>> =

        coroutineScope {
                Log.d(tag, "fetchArticles start")
                val deferredTopArticles = if (pageNum == 0) async { api.getTopArticle() } else null
                val deferredArticles = async { api.getArticleList(pageNum) }
                val topArticlesResponse = deferredTopArticles?.await()
                val articlesResponse = deferredArticles.await()
                Log.d(tag, "fetchArticles over")
                articleListCache.clear()
                if (pageNum == 0) {
                    if (topArticlesResponse?.isSuccessful == true && articlesResponse.isSuccessful) {
                        val topArticles =
                            topArticlesResponse.body()?.data?.map { it.apply { isTop = true } }
                                ?: emptyList()
                        val articles = articlesResponse.body()?.data?.datas ?: emptyList()
                        articleListCache.addAll(articles)
                        articleListCache.addAll(0, topArticles)
                        val httpData =
                            HttpData(ArticleData(0, datas = articleListCache, 0, false, 0, 0, 0))
                        Response.success(httpData)
                    } else {
                        articlesResponse // Prioritize articlesResponse error if exists
                    }
                } else {
                    articlesResponse
                }
            }


}