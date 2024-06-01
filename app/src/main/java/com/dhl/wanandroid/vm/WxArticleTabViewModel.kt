package com.dhl.wanandroid.vm

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dhl.wanandroid.adapter.ArticlePagingSource
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.ArticleData
import com.dhl.wanandroid.model.HttpData
import kotlinx.coroutines.flow.Flow
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
class WxArticleTabViewModel : BaseViewModel() {

    /**
     * 获取文章
     */
    fun getArticles(id: Int): Flow<PagingData<Article>> {
        val apiCall: suspend (Int) -> Response<HttpData<ArticleData>> = { page ->
            api.getWxArticleList(id, page)
        }

        return Pager(PagingConfig(pageSize = 20)) {
            ArticlePagingSource(apiCall)
        }.flow.cachedIn(viewModelScope)
    }


}