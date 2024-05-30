package com.dhl.wanandroid.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dhl.wanandroid.api.ApiService
import com.dhl.wanandroid.http.RetrofitManager
import com.dhl.wanandroid.model.Article

class ArticlePagingSource(private val apiService: ApiService, val id:Int) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: 1
        return try {
            val response = apiService.getWxArticleList(id, position)
            val articles = response.body()?.data?.datas ?: emptyList()
            LoadResult.Page(
                data = articles,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (articles.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }
}
