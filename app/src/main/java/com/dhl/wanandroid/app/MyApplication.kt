package com.dhl.wanandroid.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.Utils
import com.dhl.wanandroid.util.ProcessUtils
import com.dhl.wanandroid.util.Settings
import com.dhl.wanandroid.vm.AppScope
import com.dhl.wanandroid.vm.SplashViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * 程序入口
 * @author dhl
 */
class MyApplication : Application(), ViewModelStoreOwner {



    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    /**
     * 预加载广告图
     */
    private val splashViewModel: SplashViewModel by lazy {
        AppScope.getAppScopeViewModel(SplashViewModel::class.java)
        // ViewModelProvider(this).get(SplashViewModel::class.java)
    }
    override fun onCreate() {
        super.onCreate()
        context = this
        if(ProcessUtils.isMainProcess(this)){
            Utils.init(this)
            CrashUtils.init()
            AppScope.init(this)
            preloadSplashImage()
        }


    }

    companion object {
        @JvmField
        var context: Context? = null
    }



    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

    private  fun preloadSplashImage(){
        splashViewModel.getImage()
    }

}