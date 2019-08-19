package com.dhl.wanandroid.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.ImageBean;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *  负责下载Splash 展示的图片
 */
public class SplashImageService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION = "Download_image";

    // TODO: Rename parameters
    private static final String IMAGE_URL = "image_url";

    private static final String IMAGE_BEAN = "image_bean";

    /**
     * 图片下载 的路径
     */

    private String  imagePath ;

    /**
     *  图片格式
     */

    private SimpleDateFormat simpleDateFormat ;


    public SplashImageService() {
        super("SplashImageService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startAction(Context context, String imageUrl,ImageBean imageBean) {
        Intent intent = new Intent(context, SplashImageService.class);
        intent.setAction(ACTION);
        intent.putExtra(IMAGE_URL, imageUrl);
        intent.putExtra(IMAGE_BEAN, imageBean);
        context.startService(intent);
    }




    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION.equals(action)) {
                final String imageUrl = intent.getStringExtra(IMAGE_URL);
                ImageBean imageBean = (ImageBean) intent.getSerializableExtra(IMAGE_BEAN);
                handleActionFoo(imageUrl,imageBean);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String imageUrl,final ImageBean imageBean) {
        // TODO: Handle action Foo
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        OkHttpManager.getInstance().get(imageUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                imagePath = getExternalFilesDir("image")+"/"+simpleDateFormat.format(new Date())+"_splash.jpg";
                FileOutputStream fileOutputStream = new FileOutputStream(imagePath);
                byte[] bytes = new byte[1024];
                int len = 0 ;
                while ((len =inputStream.read(bytes))!= -1)
                {
                    fileOutputStream.write(bytes,0,len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                //LitePal.deleteAll(ImageBean.class);
                imageBean.setImagePath(imagePath);
                imageBean.save();
            }
        });
    }


}
