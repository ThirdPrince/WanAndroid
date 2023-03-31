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
 * @Title: NavViewModel
 * @Package com.dhl.wanandroid.vm
 * @Description:Nav
 * @author dhl
 * @date 2023 1-07
 * @version V2.0
 */
class NavViewModel : BaseViewModel() {

    val tag = "NavViewModel"


    /**
     * 文章LiveData
     */
    private val _resultNav = MutableLiveData<RepoResult<MutableList<NavBean>>>()
    private val resultNav: LiveData<RepoResult<MutableList<NavBean>>>
        get() = _resultNav


    /**
     * 获取文章
     */
    fun getNav(): LiveData<RepoResult<MutableList<NavBean>>> {
        val exception = CoroutineExceptionHandler { _, throwable ->
            _resultNav.value = throwable.message?.let { RepoResult(it) }
            Log.e(tag, throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getNav()
            Log.i(tag, " response=${response}")
            val data = response.body()?.data
            if (data != null) {
                _resultNav.value = RepoResult(data, "")
            } else {
                _resultNav.value = RepoResult(response.message())
            }


        }
        return resultNav
    }


}