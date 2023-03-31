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
 * @Title: ProjectViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description: 项目
 * @author dhl
 * @date 2023 1 11
 * @version V2.0
 */
class ProjectViewModel : BaseViewModel() {

    private val TAG = "ProjectViewModel"

    private val _resultLiveData = MutableLiveData<RepoResult<MutableList<BaseData>>>()

    private val resultLiveData: LiveData<RepoResult<MutableList<BaseData>>>
        get() = _resultLiveData


    /**
     * 获取知识体系
     */
    fun getProject(): LiveData<RepoResult<MutableList<BaseData>>> {

        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultLiveData.value = throwable.message?.let { RepoResult(it) }
            Log.e("CoroutinesViewModel", throwable.message!!)
        }
        viewModelScope.launch(exception) {
            var response = api.getProject()
            val data = response.body()?.data
            if (data != null) {
                _resultLiveData.value = RepoResult(data, "")
            } else {
                _resultLiveData.value = RepoResult(response.message())
            }

        }
        return resultLiveData
    }
}