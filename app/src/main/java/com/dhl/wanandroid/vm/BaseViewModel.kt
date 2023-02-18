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
open abstract class BaseViewModel : ViewModel() {



     val api by lazy { RetrofitManager.apiService }


}