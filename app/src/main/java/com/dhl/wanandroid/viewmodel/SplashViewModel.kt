package com.dhl.wanandroid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.wanandroid.model.ImageBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litepal.LitePal.findLast

/**
 * @Title: $
 * @Package $
 * @Description: 获取要展示的图片
 * @author $
 * @date $
 * @version V1.0
 */
class SplashViewModel:ViewModel() {

    private val imageBeanLiveData :MutableLiveData<ImageBean>  by lazy {
        MutableLiveData<ImageBean>().also {
            fetchImage()
        }
    }

    fun getImage():LiveData<ImageBean>{
        return  imageBeanLiveData
    }


    fun fetchImage(){
       viewModelScope.launch {
           withContext(Dispatchers.IO){
               val imageBean = findLast(ImageBean::class.java)
               imageBeanLiveData.postValue(imageBean)
           }
       }

    }

}