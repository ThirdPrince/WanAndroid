package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.model.ArticleData
import com.dhl.wanandroid.model.HotSearchBean
import com.dhl.wanandroid.model.NavBean
import com.dhl.wanandroid.model.RepoResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


/**
 * @Title: SearchViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description:Nav
 * @author dhl
 * @date 2023 1-18
 * @version V2.0
 */
class HotSearchViewModel : BaseViewModel() {

    val  tag = "SearchViewModel"

    /**
     * 获取搜索热词
     */
     fun getSearchHot(): LiveData<RepoResult<MutableList<HotSearchBean>>>{
         val result = MutableLiveData<RepoResult<MutableList<HotSearchBean>>>()
        viewModelScope.launch {
           val response =  api.getHotSearch()
            val data = response.body()?.data
            if (data !=null){
                result.value = RepoResult(data,"")
            }else{
                result.value = RepoResult(response.message())
            }
        }
        return result
    }


    /**
     * 搜索 page key
     */
    fun getSearchResult(page:Int,key:String):LiveData<RepoResult<ArticleData>>{
        val result = MutableLiveData<RepoResult<ArticleData>>()
        val exception = CoroutineExceptionHandler { _, throwable ->
            result.value = throwable.message?.let { RepoResult(it) }
            Log.e("CoroutinesViewModel", throwable.message!!)
        }
        viewModelScope.launch(exception) {
            val response =  api.getSearchKey(page,key)
            if(response.isSuccessful){
                val data = response.body()!!.data
                if (data !=null){
                    result.value = RepoResult(data,"")
                }else{
                    result.value = RepoResult(response.message())
                }
            }else{
                result.value = RepoResult(response.message())
            }

        }
        return result
    }

}