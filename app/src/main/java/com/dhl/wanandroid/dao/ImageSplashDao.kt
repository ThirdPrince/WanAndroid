package com.dhl.wanandroid.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dhl.wanandroid.model.ImageSplash

/**
 * @Title: $
 * @Package $
 * @Description: $(用一句话描述)
 * @author $
 * @date $
 * @version V1.0
 */
@Dao
interface ImageSplashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element:ImageSplash)

    @Query("select * from ImageSplash")
    fun getAll():MutableList<ImageSplash>

    @Query("select * from ImageSplash  order by id desc ")
    fun getLatestImage():ImageSplash
}