package com.dhl.wanandroid.adapter

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.ArticleData
import com.dhl.wanandroid.model.HttpData
import retrofit2.Response

class ArticlePagingSource(
    private val apiCall: suspend (Int) -> Response<HttpData<ArticleData>>,
    private val cache: List<Article> = emptyList()
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: 0
        return try {
            Log.e(TAG,"load ->${position}")
            val articles =  apiCall(position).body()?.data?.datas ?: emptyList()
            LoadResult.Page(
                data = articles,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (articles.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            Log.e(TAG,"Exception ->${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }
    companion object{
        const val TAG = "ArticlePagingSource"
    }
}

