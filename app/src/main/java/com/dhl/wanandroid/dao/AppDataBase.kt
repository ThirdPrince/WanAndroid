package com.dhl.wanandroid.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dhl.wanandroid.app.MyApplication
import com.dhl.wanandroid.dao.AppDataBase.Companion.instance
import com.dhl.wanandroid.model.ImageSplash

/**
 * @Title: AppDataBase
 * @Package com.dhl.wanandroid.dao
 * @Description: wan android databaseß
 * @author dhl
 * @date 2023 2-2
 * @version V2.0
 */


@Database(entities = [ImageSplash::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    /**
     * 首页图片展示
     */
    abstract fun getImageDao(): ImageSplashDao


    companion object {
        val instance = Single.sin
    }

    private object Single {

        val sin: AppDataBase = Room.databaseBuilder(
                MyApplication.context!!,
                AppDataBase::class.java,
                "WanAndroid.db")
                .allowMainThreadQueries()
                .build()
    }

}
