package com.dhl.wanandroid.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.Utils
import com.dhl.wanandroid.util.Settings
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * 程序入口
 * @author dhl
 */
class MyApplication : Application() {

    private val mainScope = MainScope()


    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        CrashUtils.init()
        context = this
        setNightMode()
    }

    companion object {
        @JvmField
        var context: Context? = null
    }

    private fun setNightMode(){
        mainScope.launch {
            val isNight = false
            if(isNight == true){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }
            mainScope.cancel()
        }

    }

}