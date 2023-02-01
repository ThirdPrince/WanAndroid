package com.dhl.wanandroid.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dhl.wanandroid.app.MyApplication
import com.dhl.wanandroid.dao.AppDataBase.Companion.instance
import com.dhl.wanandroid.model.ImageSplash

/**
 * @Title: $
 * @Package $
 * @Description: $(用一句话描述)
 * @author $
 * @date $
 * @version V1.0
 */


@Database(entities = [ImageSplash::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getImageDao(): ImageSplashDao


    companion object {

        val instance = Single.sin

    }

    private object Single {

        val sin: AppDataBase = Room.databaseBuilder(
                MyApplication.context,
                AppDataBase::class.java,
                "WanAndroid.db")
                .allowMainThreadQueries()
                .build()
    }

}
