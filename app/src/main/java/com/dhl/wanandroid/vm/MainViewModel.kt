package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.http.RetrofitManager
import com.dhl.wanandroid.model.*
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

    private val _resultBanner = MutableLiveData<RepoResult<MutableList<BannerBean>>>()

    private val resultBanner :LiveData<RepoResult<MutableList<BannerBean>>>
        get() = _resultBanner


    private val _resultArticle = MutableLiveData<RepoResult<MutableList<Article>>>()
    private val resultArticle :LiveData<RepoResult<MutableList<Article>>>
        get() = _resultArticle


    /**
     * 获取Banner
     */
    fun getBanner():LiveData<RepoResult<MutableList<BannerBean>>>{

        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultBanner.value = throwable.message?.let { RepoResult(it) }
            Log.e("CoroutinesViewModel", throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getBanner()
            Log.i(tag, " response=${response}")
            var data = response.body()?.data
            if(data != null){
                _resultBanner.value = RepoResult(data!!,"")
            }else{
                _resultBanner.value = RepoResult(response.message())
            }

        }
        return resultBanner
    }

    /**
     * 获取文章
     */
    fun getArticle(pageNum: Int):LiveData<RepoResult<MutableList<Article>>> {
        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultArticle.value = throwable.message?.let { RepoResult(it) }
            Log.e(tag, throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getArticleList(pageNum)
            Log.i(tag, " response=${response}")
            val data = response.body()?.data
            if (data !=null){
                _resultArticle.value = RepoResult(response.body()?.data?.datas!!,"")
            }else{
                _resultArticle.value = RepoResult(response.message())
            }


        }
        return resultArticle
    }




}