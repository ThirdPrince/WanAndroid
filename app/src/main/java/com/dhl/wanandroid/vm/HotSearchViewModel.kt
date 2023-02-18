package com.dhl.wanandroid.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.model.HotSearchBean
import com.dhl.wanandroid.model.NavBean
import com.dhl.wanandroid.model.RepoResult
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
    public fun getSearchHot(): LiveData<RepoResult<MutableList<HotSearchBean>>>{
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

}