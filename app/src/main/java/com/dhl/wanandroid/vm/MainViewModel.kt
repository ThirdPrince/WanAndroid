package com.dhl.wanandroid.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.http.RetrofitManager
import com.dhl.wanandroid.model.BannerInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * @Title: MainViewModle$
 * @Package $
 * @Description: $(用一句话描述)
 * @author dhl$
 * @date 2022 12 21$
 * @version V1.0
 */
class MainViewModel : ViewModel() {

    val  TAG = "MainViewModel"

    private val api by lazy { RetrofitManager.apiService }
    var articlesLiveData: MutableLiveData<MutableList<BannerInfo>> = MutableLiveData()

    private var apiError: MutableLiveData<Throwable> = MutableLiveData()

    fun getBanner() {

        val exception = CoroutineExceptionHandler { coroutineContext, throwable ->
            apiError.postValue(throwable)
            Log.i("CoroutinesViewModel", throwable.message!!)
        }

        viewModelScope.launch(exception) {
            val response = api.getBanner()
            Log.i(TAG,  response?.body()!!.data!!.toString())

        }
    }




}