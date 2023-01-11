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
 * @Title: WxArticleViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description:指示体系的Tab
 * @author dhl
 * @date 2023 1 11
 * @version V2.0
 */
class WxArticleViewModel : ViewModel() {

    val  tag = "WxArticleTabViewModel"

    private val api by lazy { RetrofitManager.apiService }


    /**
     * 文章LiveData
     */
    private val _resultArticle = MutableLiveData<RepoResult<MutableList<BaseData>>>()
    private val resultArticle :LiveData<RepoResult<MutableList<BaseData>>>
        get() = _resultArticle


    /**
     * 获取文章
     */
    fun getWxArticleChapters():LiveData<RepoResult<MutableList<BaseData>>> {
        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultArticle.value = throwable.message?.let { RepoResult(it) }
            Log.e(tag, throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getWxArticleChapters()
            val data = response.body()?.data
            if (data !=null){
                _resultArticle.value = RepoResult(data,"")
            }else{
                _resultArticle.value = RepoResult(response.message())
            }


        }
        return resultArticle
    }




}