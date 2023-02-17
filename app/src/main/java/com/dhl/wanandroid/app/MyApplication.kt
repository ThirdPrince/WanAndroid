package com.dhl.wanandroid.app;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.Utils;

/**
 * 程序入口
 * @author dhl
 *
 */
public class MyApplication extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        CrashUtils.init();
        context = this;
    }
}
