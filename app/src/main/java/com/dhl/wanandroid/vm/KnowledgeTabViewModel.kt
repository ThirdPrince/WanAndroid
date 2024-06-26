package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dhl.wanandroid.adapter.ArticlePagingSource
import com.dhl.wanandroid.http.RetrofitManager
import com.dhl.wanandroid.model.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * @Title: KnowledgeTabViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description:指示体系的Tab
 * @author dhl
 * @date 2022 12 28
 * @version V1.0
 * LiveData的postValue和setValue方法是protected，而MutableLiveData这两个方法则是public，
 * 也就是说Livedata只允许调用observe方法被动监听数据变化，而MutableLiveData除了监听变化外，还可以用postValue和setValue方法发射数据。
 */
class KnowledgeTabViewModel : BaseViewModel() {

    val tag = "MainViewModel"


    /**
     * 文章LiveData
     */
    private val _resultArticle = MutableLiveData<RepoResult<MutableList<Article>>>()
    private val resultArticle: LiveData<RepoResult<MutableList<Article>>>
        get() = _resultArticle


    /**
     * 获取文章
     */
//    fun getArticle(pageNum: Int, cid: Int): LiveData<RepoResult<MutableList<Article>>> {
//        val exception = CoroutineExceptionHandler { _, throwable ->
//            _resultArticle.value = throwable.message?.let { RepoResult(it) }
//            Log.e(tag, throwable.message!!)
//        }
//
//        viewModelScope.launch(exception) {
//            val response = api.getKnowledgeArticleList(pageNum, cid)
//            Log.i(tag, " response=${response}")
//            val data = response.body()?.data
//            if (data != null) {
//                _resultArticle.value = RepoResult(response.body()?.data?.datas!!, "")
//            } else {
//                _resultArticle.value = RepoResult(response.message())
//            }
//
//
//        }
//        return resultArticle
//    }

    fun getArticles(cid: Int): Flow<PagingData<Article>> {
        val apiCall: suspend (Int) -> Response<HttpData<ArticleData>> = { page ->
            api.getKnowledgeArticleList(page, cid)
        }
        return Pager(PagingConfig(pageSize = 20)) {
            ArticlePagingSource(apiCall)
        }.flow.cachedIn(viewModelScope)
    }


}