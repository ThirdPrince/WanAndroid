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
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.ArticleData
import com.dhl.wanandroid.model.HttpData
import com.dhl.wanandroid.model.RepoResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * @Title: SquareViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description:指示体系的Tab
 * @author dhl
 * @date 2023 0401
 * @version V2.0
 * LiveData的postValue和setValue方法是protected，而MutableLiveData这两个方法则是public，
 * 也就是说Livedata只允许调用observe方法被动监听数据变化，而MutableLiveData除了监听变化外，还可以用postValue和setValue方法发射数据。
 */
class SquareViewModel : BaseViewModel() {

    val tag = "SquareViewModel"


    /**
     * 文章LiveData
     */
    private val _resultArticle = MutableLiveData<RepoResult<MutableList<Article>>>()
    private val resultArticle: LiveData<RepoResult<MutableList<Article>>>
        get() = _resultArticle


    /**
     * 获取文章
     */
    fun getSquareList(pageNum: Int): LiveData<RepoResult<MutableList<Article>>> {

        viewModelScope.launch(exception) {
            val response = api.getSquareList(pageNum)
            Log.i(tag, " response=${response}")
            val data = response.body()?.data
            if (data != null) {
                _resultArticle.value = RepoResult(response.body()?.data?.datas!!, "")
            } else {
                _resultArticle.value = RepoResult(response.message())
            }

        }
        return resultArticle
    }

    fun getSquareList(): Flow<PagingData<Article>> {
        val apiCall: suspend (Int) -> Response<HttpData<ArticleData>> = { page ->
            api.getSquareList(page)
        }
        return Pager(PagingConfig(pageSize = 20)) {
            ArticlePagingSource(apiCall)
        }.flow.cachedIn(viewModelScope)
    }


}