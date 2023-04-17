package com.dhl.wanandroid.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Nullable
import com.dhl.wanandroid.BuildConfig
import java.lang.reflect.Method


/**
 * @Title: ProcessUtils
 * @Package com.dhl.wanandroid.util
 * @Description: 进程工具类
 * @author $
 * @date 2023 4.17
 * @version V2.0
 */
object ProcessUtils {

    const val TAG = "ProcessUtils"

    private var currentProcessName: String? = null

    /**
     * @return 当前进程名
     */
    @Nullable
    fun getCurrentProcessName(context: Context): String? {
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }

        //1)通过Application的API获取当前进程名
        currentProcessName = currentProcessNameByApplication
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }

        //2)通过反射ActivityThread获取当前进程名
        currentProcessName = currentProcessNameByActivityThread
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }

        //3)通过ActivityManager获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityManager(context)
        return currentProcessName
    }

    /**
     * 通过Application新的API获取进程名，无需反射，无需IPC，效率最高。
     */
    private val currentProcessNameByApplication: String?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else null

    /**
     * 通过反射ActivityThread获取进程名，避免了ipc
     */
    private val currentProcessNameByActivityThread: String?
        @SuppressLint("DiscouragedPrivateApi")
        get() {
            var processName: String? = null
            try {
                val declaredMethod: Method = Class.forName(
                    "android.app.ActivityThread", false,
                    Application::class.java.getClassLoader()
                )
                    .getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
                declaredMethod.isAccessible = true
                val invoke: Any = declaredMethod.invoke(null, arrayOfNulls<Any>(0))
                if (invoke is String) {
                    processName = invoke
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return processName
        }

    /**
     * 通过ActivityManager 获取进程名，需要IPC通信
     */
    private fun getCurrentProcessNameByActivityManager(context: Context): String? {
        if (context == null) {
            return null
        }
        val pid: Int = android.os.Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (am != null) {
            val runningAppList = am.runningAppProcesses
            if (runningAppList != null) {
                for (processInfo in runningAppList) {
                    if (processInfo.pid == pid) {
                        return processInfo.processName
                    }
                }
            }
        }
        return null
    }

     fun isMainProcess(context: Context):Boolean {
        //获取当前进程名，并与主进程对比，来判断是否为主进程
        val processName = getCurrentProcessName(context);
        Log.e(TAG, "MainProcess processName=$processName");
        return BuildConfig.APPLICATION_ID == processName;
    }



}
