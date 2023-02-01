package com.dhl.wanandroid.viewmodel

import android.graphics.ImageDecoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.CacheDiskUtils
import com.dhl.wanandroid.dao.AppDataBase
import com.dhl.wanandroid.dao.ImageSplashDao
import com.dhl.wanandroid.model.ImageBean
import com.dhl.wanandroid.model.ImageSplash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litepal.LitePal
import org.litepal.LitePal.findLast

/**
 * @Title: 下载必应的图片
 * @Package $
 * @Description: 获取要展示的图片
 * @author dhl
 * @date 2023 1.11
 * @version V1.0
 */
class SplashViewModel:ViewModel() {

    private  val TAG = "SplashViewModel"
    private val imageBeanLiveData :MutableLiveData<ImageSplash>  by lazy {
        MutableLiveData<ImageSplash>().also {
            fetchImage()
        }
    }

    fun getImage():LiveData<ImageSplash>{
        return  imageBeanLiveData
    }


    private fun fetchImage(){
       viewModelScope.launch {
           withContext(Dispatchers.IO){
               val imageDao: ImageSplashDao = AppDataBase.instance.getImageDao()
               var imageSplash = imageDao.getLatestImage()
               Log.e(TAG,"imageSplash=$imageSplash")
               imageBeanLiveData.postValue(imageSplash)
           }
       }

    }

}