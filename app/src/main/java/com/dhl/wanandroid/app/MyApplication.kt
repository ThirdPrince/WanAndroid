package com.dhl.wanandroid.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
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

    private val mainScope = MainScope()


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
            setNightMode()
            preloadSplashImage()
        }


    }

    companion object {
        @JvmField
        var context: Context? = null
    }

    private fun setNightMode() {
        mainScope.launch {
            val isNight = false
            if (isNight == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }
            mainScope.cancel()
        }

    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

    private  fun preloadSplashImage(){
        splashViewModel.getImage()
    }

}