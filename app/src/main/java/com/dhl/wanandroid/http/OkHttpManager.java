package com.dhl.wanandroid.http;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.bumptech.glide.RequestBuilder;
import com.dhl.wanandroid.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * 封装OKHttp
 * @author  dhl 2016 6.6
 */
public class OkHttpManager {

    private static final String TAG = "OkHttpManager";
    /**
     * 单例模式
     */
    private static OkHttpManager mInstance;
    private OkHttpClient okHttpClient ;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private OkHttpManager()
    {
        okHttpClient = new okhttp3.OkHttpClient.Builder()
                 .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
 /*       cookieJar(new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            cookieStore.put(httpUrl.host(), list);
            for(Cookie cookie:list){
                Log.e(TAG,"cookie Name:" + cookie.name());
                Log.e(TAG,"cookie Path:"+cookie.path());
            }


        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
            List<Cookie> cookies = cookieStore.get(httpUrl.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    })*/.build();
    }

    public static OkHttpManager getInstance()
    {
        if(mInstance == null)
        {
            synchronized (OkHttpManager.class)
            {
                if(mInstance == null)
                {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance ;
    }

    public void get(String url ,Callback callback)
    {
           Request request = new Request.Builder().url(url).build();
           okHttpClient.newCall(request).enqueue(callback);
    }
    public void getAddCookie(String url , boolean isCookie,Callback callback)
    {

         Request.Builder requestBuilder = new Request.Builder();
         if(isCookie) {
            String userName = SPUtils.getInstance().getString("userName");
            String password = SPUtils.getInstance().getString("password");
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                requestBuilder.addHeader("Cookie", "loginUserName=" + userName)
                        .addHeader("Cookie", "loginUserPassword=" + password);
            }
        }
        Request request = requestBuilder.url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public void login(String url ,String username,String password,Callback callback)
    {
        FormBody formBody = new FormBody
                .Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 注册
     */

    public void register(String url ,String username,String password,String repassword,Callback callback)
    {

        FormBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .add("repassword",repassword)
                .build();
        Request request = new Request.Builder().post(formBody).url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 获取收藏列表
     * @param url
     * @param callback
     */
    public void getCollectionList(String url ,Callback callback)
    {
        Request request = new Request.Builder()
                //.addHeader("JSESSIONID", "JSESSIONID="+SPUtils.getInstance().getString("JSESSIONID"))
                .addHeader("Cookie", "loginUserName="+SPUtils.getInstance().getString("userName"))
                .addHeader("Cookie","loginUserPassword="+SPUtils.getInstance().getString("password"))
                .url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 收藏站内
     * @param url
     * @param callback
     */
    public void postCollection(String url ,Callback callback)
    {

        RequestBody formBody = new FormBody.Builder()
                .build();
        //RequestBuilder requestBuilder =
        Request request = new Request.Builder()
                //.addHeader("JSESSIONID", "JSESSIONID="+SPUtils.getInstance().getString("JSESSIONID"))
                .addHeader("Cookie", "loginUserName="+SPUtils.getInstance().getString("userName"))
                .addHeader("Cookie","loginUserPassword="+SPUtils.getInstance().getString("password"))
                .url(url).post(formBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 收藏站外
     * @param url
     * @param callback
     */
    public void postCollectionOut(String url ,String title,String author,String link,Callback callback)
    {

       /* RequestBody formBody = new FormBody.Builder()
                .build();*/
        FormBody formBody = new FormBody
                .Builder()
                .add("title", title)
                .add("author", author)
                .add("link", link)
                .build();
        //RequestBuilder requestBuilder =
        Request request = new Request.Builder()
                //.addHeader("JSESSIONID", "JSESSIONID="+SPUtils.getInstance().getString("JSESSIONID"))
                .addHeader("Cookie", "loginUserName="+SPUtils.getInstance().getString("userName"))
                .addHeader("Cookie","loginUserPassword="+SPUtils.getInstance().getString("password"))
                .url(url).post(formBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 取消收藏 列表
     * @param url
     * @param title
     * @param author
     * @param link
     * @param callback
     */
    public void postUnCollection(String url ,String originId,Callback callback)
    {

       /* RequestBody formBody = new FormBody.Builder()
                .build();*/
        FormBody formBody = new FormBody
                .Builder()
                .add("originId", originId)
                .build();
        //RequestBuilder requestBuilder =
        Request request = new Request.Builder()
                //.addHeader("JSESSIONID", "JSESSIONID="+SPUtils.getInstance().getString("JSESSIONID"))
                .addHeader("Cookie", "loginUserName="+SPUtils.getInstance().getString("userName"))
                .addHeader("Cookie","loginUserPassword="+SPUtils.getInstance().getString("password"))
                .url(url).post(formBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}
