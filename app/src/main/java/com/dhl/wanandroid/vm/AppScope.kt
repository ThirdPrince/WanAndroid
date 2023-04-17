package com.dhl.wanandroid.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dhl.wanandroid.app.MyApplication

/**
 * @Title: AppScope
 * @Package $
 * @Description:  构建全局 appScopeViewModel 可用于activity 通信
 * @author dhl
 * @date $
 * @version V2.0
 */
object AppScope {

    private lateinit var myApp: MyApplication
    fun init(application: MyApplication){
        myApp = application
    }

    /**
     * 获取进程共享的ViewModel
     */
    fun <T : ViewModel?> getAppScopeViewModel(modelClass: Class<T>): T {
        return ViewModelProvider(myApp).get(modelClass)
    }
}