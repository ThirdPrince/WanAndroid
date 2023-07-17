package com.dhl.wanandroid.image

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
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
 * @Title: ImageWork
 * @Package  com.dhl.wanandroid.image
 * @Description: 下载首页展示Image
 * @author dhl
 * @date 2023 0701
 * @version V1.0
 */
class ImageWork(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    private  val TAG = "ImageWork"

    /**
     * 图片下载 的路径
     */
    private var imagePath: String? = null

    private var imageInfoList: MutableList<ImageSplash> = mutableListOf()

    private val imageDao: ImageSplashDao by lazy {
        AppDataBase.instance.getImageDao()
    }
    private val simpleDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }

    override fun doWork(): Result {
        getImageUrl()
        return Result.success()
    }


    private fun getImageUrl() {
        val imageSplash = imageDao.getLatestImage()
        imageSplash?.let { imageSplash ->
            imageSplash.imagePath?.let {
                if (File(it).name.startsWith(simpleDateFormat.format(Date()))) {
                    Log.d(TAG, "今天的图片已经下载过")
                    return@getImageUrl
                }
            }

        }
        OkHttpManager.getInstance()[Constants.IMAGES_URL,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "response =${e.message}")
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.e(TAG, "response =$response")
                        val jsonElement = JsonParser().parse(response.body?.string())
                        val jsonObject = jsonElement.asJsonObject
                        val jsonArray = jsonObject.getAsJsonArray("images")
                        imageInfoList = Gson().fromJson(
                            jsonArray.toString(),
                            object : TypeToken<List<ImageSplash?>?>() {}.type
                        )
                        imageInfoList.let {
                            val imageInfo = it?.get(0)
                            Log.d(TAG, "imageInfo = $imageInfo")
                            val image = "http://s.cn.bing.net" + imageInfo?.url
                            downLoadImage(image, imageInfo)
                        }

                    }
                }]
    }

    private fun downLoadImage(imageUrl: String, imageSplash: ImageSplash) {

        OkHttpManager.getInstance()[imageUrl, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body?.byteStream()
                imagePath = appContext.getExternalFilesDir("image").toString() + "/" + simpleDateFormat.format(Date()) + "_splash.jpg"
                Log.d(TAG, "imagePath=$imagePath")
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
                Log.d(TAG, "imageSplash = $imageSplash")
                imageDao.insert(imageSplash)

            }
        }]
    }
}
