package com.dhl.wanandroid.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.ImageBean
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

    /**
     * 图片格式
     */
    private var simpleDateFormat: SimpleDateFormat? = null
    override fun onHandleIntent(intent: Intent) {
        if (intent != null) {
            val action = intent.action
            if (ACTION == action) {
                val imageUrl = intent.getStringExtra(IMAGE_URL)
                val imageBean = intent.getSerializableExtra(IMAGE_BEAN) as ImageBean
                handleActionFoo(imageUrl, imageBean)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(imageUrl: String, imageBean: ImageBean) {
        // TODO: Handle action Foo
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        OkHttpManager.getInstance()[imageUrl, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body?.byteStream()
                imagePath = getExternalFilesDir("image").toString() + "/" + simpleDateFormat!!.format(Date()) + "_splash.jpg"
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
                //LitePal.deleteAll(ImageBean.class);
                imageBean.imagePath = imagePath
                imageBean.save()
            }
        }]
    }

    companion object {
        // TODO: Rename actions, choose action names that describe tasks that this
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        private const val ACTION = "Download_image"

        // TODO: Rename parameters
        private const val IMAGE_URL = "image_url"
        private const val IMAGE_BEAN = "image_bean"

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startAction(context: Context, imageUrl: String?, imageBean: ImageBean?) {
            val intent = Intent(context, SplashImageService::class.java)
            intent.action = ACTION
            intent.putExtra(IMAGE_URL, imageUrl)
            intent.putExtra(IMAGE_BEAN, imageBean)
            context.startService(intent)
        }
    }
}