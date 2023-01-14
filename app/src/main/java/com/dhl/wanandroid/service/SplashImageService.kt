package com.dhl.wanandroid.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.CacheDiskUtils
import com.dhl.wanandroid.app.Constants
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.ImageBean
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author dhl
 * 负责下载 Splash 展示的图片(图片从必应上下载)
 */
class SplashImageService : IntentService("SplashImageService") {
    /**
     * 图片下载 的路径
     */
    private var imagePath: String? = null


    private var imageInfoList: MutableList<ImageBean>? = mutableListOf()

    /**
     * 图片格式
     */
    private val simpleDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }
    override fun onHandleIntent(intent: Intent) {
        if (intent != null) {
            getImageUrl()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun getImageUrl() {
        Log.e("TAG", "getImageUrl =")
        val imageFile = getExternalFilesDir("image")
//        val files = imageFile.listFiles()
//        var isDownLoadImage = false
//        for (f in files) {
//            isDownLoadImage = !f.startsWith(simpleDateFormat!!.format(Date()))
//
//        }
//        if (!isDownLoadImage) {
//            return
//        }
        OkHttpManager.getInstance()[Constants.IMAGES_URL,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.e("TAG", "response =$response")
                        val jsonElement = JsonParser().parse(response.body?.string())
                        val jsonObject = jsonElement.asJsonObject
                        val jsonArray = jsonObject.getAsJsonArray("images")
                        imageInfoList = Gson().fromJson(jsonArray.toString(), object : TypeToken<List<ImageBean?>?>() {}.type)
                        imageInfoList.let {
                            val imageInfo = it?.get(0)
                            val image = "http://s.cn.bing.net" + imageInfo?.url
                            handleActionFoo(image, imageInfo!!)
                        }

                    }
                }]
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(imageUrl: String, imageBean: ImageBean) {
        // TODO: Handle action Foo

        OkHttpManager.getInstance()[imageUrl, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body?.byteStream()
                imagePath = getExternalFilesDir("image").toString() + "/" + simpleDateFormat!!.format(Date()) + "_splash.jpg"
                Log.e("imagePath", "imagePath=$imagePath")
                val fileOutputStream = FileOutputStream(imagePath)
                val bytes = ByteArray(1024)
                var len = 0
                while (inputStream?.read(bytes).also {
                            if (it != null) {
                                len = it
                            }
                        } != -1) {
                    fileOutputStream.write(bytes, 0, len)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream?.close()
                imageBean.imagePath = imagePath
                CacheDiskUtils.getInstance().put("SplashImage",imageBean)
               // imageBean.save()

            }
        }]
    }

    companion object {

        private const val ACTION = "Download_image"

        @JvmStatic
        fun startDownLoadAction(context: Context) {
            val intent = Intent(context, SplashImageService::class.java)
            intent.action = ACTION
            context.startService(intent)
        }
    }
}