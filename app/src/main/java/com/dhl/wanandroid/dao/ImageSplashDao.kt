package com.dhl.wanandroid.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dhl.wanandroid.model.ImageSplash

/**
 * @Title: ImageSplashDao
 * @Package com.dhl.wanandroid.dao
 * @Description:首页 数据
 * @author dhl
 * @date 2023 2.1
 * @version V1.0
 */
@Dao
interface ImageSplashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element:ImageSplash)

    @Query("select * from ImageSplash")
    fun getAll():MutableList<ImageSplash>

    /**
     * 展示最新的图片
     */
    @Query("select * from ImageSplash  order by id desc ")
    fun getLatestImage():ImageSplash
}