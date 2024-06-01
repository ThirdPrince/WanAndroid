package com.dhl.wanandroid.vm

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
import com.dhl.wanandroid.model.HotSearchBean
import com.dhl.wanandroid.model.HttpData
import com.dhl.wanandroid.model.RepoResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response


/**
 * @Title: SearchViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description:Nav
 * @author dhl
 * @date 2023 1-18
 * @version V2.0
 */
class HotSearchViewModel : BaseViewModel() {

    val tag = "SearchViewModel"
    /**
     * 获取搜索热词
     */
    fun getSearchHot(): LiveData<RepoResult<MutableList<HotSearchBean>>> {
        val result = MutableLiveData<RepoResult<MutableList<HotSearchBean>>>()
        viewModelScope.launch(exception) {
            val response = api.getHotSearch()
            val data = response.body()?.data
            if (data != null) {
                result.value = RepoResult(data, "")
            } else {
                result.value = RepoResult(response.message())
            }
        }
        return result
    }


    /**
     * 搜索 page key
     */
    fun getSearchResult(page: Int, key: String): LiveData<RepoResult<ArticleData>> {
        val result = MutableLiveData<RepoResult<ArticleData>>()
        viewModelScope.launch(exception) {
            val response = api.getSearchKey(page, key)
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    result.value = RepoResult(data, "")
                } else {
                    result.value = RepoResult(response.message())
                }
            } else {
                _errorResponse.value = RepoResult(response.message())
            }

        }
        return result
    }

    fun getSearchRes(key: String): Flow<PagingData<Article>> {
        val apiCall: suspend (Int) -> Response<HttpData<ArticleData>> = { page ->
            api.getSearchKey(page, key)
        }

        return Pager(PagingConfig(pageSize = 20)) {
            ArticlePagingSource(apiCall)
        }.flow.cachedIn(viewModelScope)
    }

}