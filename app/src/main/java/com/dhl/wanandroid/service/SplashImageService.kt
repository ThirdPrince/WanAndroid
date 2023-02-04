package com.dhl.wanandroid.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dhl.wanandroid.app.Constants
import com.dhl.wanandroid.dao.AppDataBase
import com.dhl.wanandroid.dao.ImageSplashDao
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.ImageSplash
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author dhl
 * 负责下载 Splash 展示的图片(图片从必应上下载)
 */
class SplashImageService : IntentService("SplashImageService") {

    private val TAG = "SplashImageService"

    /**
     * 图片下载 的路径
     */
    private var imagePath: String? = null


    private var imageInfoList: MutableList<ImageSplash> = mutableListOf()

    /**
     * 图片格式
     */
    private val simpleDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }


    /**
     *
     */
    val imageDao: ImageSplashDao by lazy {
        AppDataBase.instance.getImageDao()
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
        val imageSplash = imageDao.getLatestImage()
        imageSplash.let {
            Log.e(TAG, "imagePath =${it.imagePath}")
            if ( File(it.imagePath).name.startsWith(simpleDateFormat.format(Date()))) {
                Log.e(TAG, "今天的图片已经下载过")
                return@getImageUrl
            }

        }
        OkHttpManager.getInstance()[Constants.IMAGES_URL,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.e(TAG, "response =$response")
                        val jsonElement = JsonParser().parse(response.body?.string())
                        val jsonObject = jsonElement.asJsonObject
                        val jsonArray = jsonObject.getAsJsonArray("images")
                        imageInfoList = Gson().fromJson(jsonArray.toString(), object : TypeToken<List<ImageSplash?>?>() {}.type)
                        imageInfoList.let {
                            val imageInfo = it?.get(0)
                            Log.e(TAG, "imageInfo = $imageInfo")
                            val image = "http://s.cn.bing.net" + imageInfo?.url
                            handleActionFoo(image, imageInfo)
                        }

                    }
                }]
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(imageUrl: String, imageSplash: ImageSplash) {
        // TODO: Handle action Foo

        OkHttpManager.getInstance()[imageUrl, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body?.byteStream()
                imagePath = getExternalFilesDir("image").toString() + "/" + simpleDateFormat.format(Date()) + "_splash.jpg"
                Log.e(TAG, "imagePath=$imagePath")
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
                imageSplash.imagePath = imagePath as String
                Log.e(TAG, "imageSplash = $imageSplash")
                imageDao.insert(imageSplash)

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