package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.http.RetrofitManager
import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.model.KnowledgeTreeData
import com.dhl.wanandroid.model.RepoResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * @Title: KnowledgeSysViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description: 知识体系 Model
 * @author dhl
 * @date 2022 12 24
 * @version V2.0
 */
class KnowledgeSysViewModel:ViewModel() {

    private val TAG = "KnowledgeSysViewModel"

    private val _resultLiveData = MutableLiveData<RepoResult<MutableList<KnowledgeTreeData>>>()

    private val resultLiveData :LiveData<RepoResult<MutableList<KnowledgeTreeData>>>
        get() = _resultLiveData




    /**
     * 获取知识体系
     */
    fun getKnowledgeTree(): LiveData<RepoResult<MutableList<KnowledgeTreeData>>> {

        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultLiveData.value = throwable.message?.let { RepoResult(it) }
            Log.e("CoroutinesViewModel", throwable.message!!)
        }
        viewModelScope.launch(exception){
            var response = RetrofitManager.apiService.getKnowledge()
            Log.i(TAG, " response=${response}")
            val data = response.body()?.data
            if(data != null){
                _resultLiveData.value = RepoResult(data,"")
            }else{
                _resultLiveData.value = RepoResult(response.message())
            }

        }
        return resultLiveData
    }
}