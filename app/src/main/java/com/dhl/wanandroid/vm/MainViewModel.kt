package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.http.RetrofitManager
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.ArticleData
import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.model.RepoResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * @Title: MainViewModel
 * @Package $
 * @Description:
 * @author dhl$
 * @date 2022 12 21$
 * @version V1.0
 */
class MainViewModel : ViewModel() {

    val  tag = "MainViewModel"

    private val api by lazy { RetrofitManager.apiService }


    /**
     * 获取Banner
     */
    fun getBanner():LiveData<RepoResult<MutableList<BannerBean>>>{
        val resultLiveData: MutableLiveData<RepoResult<MutableList<BannerBean>>> = MutableLiveData<RepoResult<MutableList<BannerBean>>>()
        val exception = CoroutineExceptionHandler { _, throwable ->
            resultLiveData.value = throwable.message?.let { RepoResult(it) }
            Log.e("CoroutinesViewModel", throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getBanner()
            Log.e(tag, " response=${response}")
            var data = response.body()?.data
            if(data != null){
                resultLiveData.value = RepoResult(data!!,"")
            }else{
                resultLiveData.value = RepoResult(response.message())
            }

            //Log.i(tag, response?.body()!!.data!!.toString())

        }
        return resultLiveData
    }

    /**
     * 获取文章
     */
    fun getArticle(pageNum: Int):LiveData<RepoResult<MutableList<Article>>> {
        val resultLiveData: MutableLiveData<RepoResult<MutableList<Article>>> = MutableLiveData<RepoResult<MutableList<Article>>>()
        val exception = CoroutineExceptionHandler { _, throwable ->
            resultLiveData.value = throwable.message?.let { RepoResult(it) }
            Log.e(tag, throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getArticleList(pageNum)
            Log.e(tag, " response=${response}")
            val data = response.body()?.data
            if (data !=null){
                resultLiveData.value = RepoResult(response.body()?.data?.datas!!,"")
            }else{
                resultLiveData.value = RepoResult(response.message())
            }


        }
        return resultLiveData
    }




}