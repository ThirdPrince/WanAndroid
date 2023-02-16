package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.http.RetrofitManager
import com.dhl.wanandroid.model.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
class MainViewModel : ViewModel() {

    val tag = "MainViewModel"

    private val api by lazy { RetrofitManager.apiService }

    /**
     * banner LiveData
     */
    private val _resultBanner = MutableLiveData<RepoResult<MutableList<BannerBean>>>()

    private val resultBanner: LiveData<RepoResult<MutableList<BannerBean>>>
        get() = _resultBanner


    /**
     * 文章LiveData
     */
    private val _resultArticle = MutableLiveData<RepoResult<MutableList<Article>>>()
    private val resultArticle: LiveData<RepoResult<MutableList<Article>>>
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

    /**
     * 获取文章
     * 包括置顶文章
     */
    fun getArticle(pageNum: Int): LiveData<RepoResult<MutableList<Article>>> {
        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultArticle.value = throwable.message?.let { RepoResult(it) }
            Log.e(tag, throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val articleList = mutableListOf<Article>()
            val deferredArticle = async { api.getArticleList(pageNum) }
            if (pageNum == 0) {
                val deferredTop = async { api.getTopArticle() }
                var awaitTop = deferredTop.await()
                awaitTop.body()!!.data.forEach {
                    it.isTop = true
                    articleList.add(it)
                }
            }
            var awaitArticle = deferredArticle.await()
            articleList.addAll(awaitArticle.body()?.data!!.datas)
            articleList.let {
                _resultArticle.value = RepoResult(articleList, "")
            }
            if (!awaitArticle.isSuccessful) {
                _resultArticle.value = RepoResult(awaitArticle.message())
            }
        }
        return resultArticle
    }


}