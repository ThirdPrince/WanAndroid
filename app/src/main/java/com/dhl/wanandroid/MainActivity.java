package com.dhl.wanandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dhl.wanandroid.activity.CollectionActivity;
import com.dhl.wanandroid.activity.LoginActivity;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.fragment.KnowledgeSysFragment;
import com.dhl.wanandroid.fragment.MainFragment;
import com.dhl.wanandroid.fragment.NavFragment;
import com.dhl.wanandroid.fragment.ProjectFragment;
import com.dhl.wanandroid.fragment.WxArticleFragment;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.ImageBean;
import com.dhl.wanandroid.model.LoginBean;
import com.dhl.wanandroid.service.SplashImageService;
import com.dhl.wanandroid.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.Permission;
import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.dhl.wanandroid.app.Constants.LOGIN_URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SD_PERM = 1024;

    /**
     * 底部Bottom
     */
    private BottomNavigationView bottom_navigation ;

    private DrawerLayout drawerLayout ;

    private    Toolbar toolbar ;

    private NavigationView nav_view ;

    private View headerLayout ;

    private TextView tv_login ;

    /**
     * 首页Fragment
     *
     */

    private MainFragment mainFragment ;

    /**
     * 知识体系fragment
     */

    private KnowledgeSysFragment knowledgeSysFragment ;

    /**
     * 公众号Fragment
     */

    private WxArticleFragment wxArticleFragment ;


    /**
     * 导航
     */

    private NavFragment navFragment ;

    /**
     * 项目
     */
    private ProjectFragment projectFragment ;


    private  FragmentManager fm ;

    private FragmentTransaction fragmentTransaction ;
    FragmentTransaction ft  ;

    private static final  int DOWNLOAD_IMAGE = 1024 ;
    /**
     * 必应图片
     */
      private static List<ImageBean> imageInfoList ;

      private LoginBean loginBean ;




    private static class MyHandler extends Handler
    {
        private final WeakReference<MainActivity> mActivity ;
        public MyHandler(MainActivity context)
        {
            mActivity = new WeakReference<MainActivity>(context) ;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = mActivity.get();
            if(mainActivity != null)
            {

                switch (msg.what)
                {
                    case DOWNLOAD_IMAGE:
                        mainActivity.downLoadImage();
                        break;
                }
            }

        }
    }

    private MyHandler myHandler ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        login();
        myHandler = new MyHandler(this);
        fm = getSupportFragmentManager();
        mainFragment = MainFragment.newInstance("","");
        fragmentTransaction = fm.beginTransaction();

        if(savedInstanceState == null)
        {
            fragmentTransaction.add(R.id.content,mainFragment,MainFragment.class.getSimpleName()).commit();
            Log.e(TAG,"add mainFragment == "+mainFragment);
        }else
        {
            mainFragment = (MainFragment) fm.findFragmentByTag(MainFragment.class.getSimpleName());
            knowledgeSysFragment = (KnowledgeSysFragment) fm.findFragmentByTag(KnowledgeSysFragment.class.getSimpleName());
            wxArticleFragment = (WxArticleFragment)fm.findFragmentByTag(WxArticleFragment.class.getSimpleName());
            navFragment = (NavFragment) fm.findFragmentByTag(NavFragment.class.getSimpleName());
        }
        // Ask for one permission
        EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_sd),
                RC_SD_PERM,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        getImages();

        //bottom_navigation.setSelectedItemId(R.id.navigation_home);
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                ft = fm.beginTransaction() ;
                hideFragments(ft);
                switch (menuItem.getItemId())
                {

                    case R.id.navigation_home:
                        if(mainFragment == null)
                        {
                            mainFragment = MainFragment.newInstance("","");
                            ft.add(R.id.content,mainFragment,MainFragment.class.getSimpleName());
                        }else
                        {
                            ft.show(mainFragment);
                        }
                        ft.commit();
                        toolbar.setTitle("首页");
                       return true ;
                    case R.id.navigation_knowledge:
                        if(knowledgeSysFragment == null)
                        {
                            knowledgeSysFragment = KnowledgeSysFragment.newInstance("","");
                            ft.add(R.id.content,knowledgeSysFragment,KnowledgeSysFragment.class.getSimpleName());
                        }else
                        {
                            ft.show(knowledgeSysFragment);
                        }
                        ft.commit();
                        toolbar.setTitle("知识体系");
                        return true ;
                    case R.id.nav_wx_article:
                        if(wxArticleFragment == null)
                        {
                            wxArticleFragment = WxArticleFragment.newInstance("","");
                            ft.add(R.id.content,wxArticleFragment,WxArticleFragment.class.getSimpleName());
                        }else
                        {
                            ft.show(wxArticleFragment);
                        }
                        ft.commit();
                        toolbar.setTitle("公众号");
                        return true ;
                    case R.id.navigation_nav :
                        if(navFragment == null)
                        {
                            navFragment = NavFragment.newInstance("","");
                            ft.add(R.id.content,navFragment,NavFragment.class.getSimpleName());
                        }else
                        {
                            ft.show(navFragment);
                        }
                        toolbar.setTitle("导航");
                        ft.commit();
                        return true ;

                    case R.id.navigation_project:
                        if(projectFragment == null)
                        {
                            projectFragment = ProjectFragment.newInstance("","");
                            ft.add(R.id.content,projectFragment,ProjectFragment.class.getSimpleName());
                        }else
                        {
                            ft.show(projectFragment);
                        }
                        ft.commit();
                        toolbar.setTitle("项目");
                        return true ;
                }

                return false;
            }
        });
    }

    private void initView()
    {
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setLabelVisibilityMode(1);
        drawerLayout = findViewById(R.id.drawerLayout);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        View headerView = nav_view.getHeaderView(0);
        tv_login = headerView.findViewById(R.id.tv_login);
       // headerLayout = nav_view.findViewById(R.id.nav_view);
        String userName = SPUtils.getInstance().getString("userName");
        if(!TextUtils.isEmpty(userName))
        {
            tv_login.setText(userName);
        }
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("首页");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


       /* byte[] bytes = EncodeUtils.base64Decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDABQODxIPDRQSEBIXFRQYHjIhHhwcHj0sLiQySUBMS0dA\\nRkVQWnNiUFVtVkVGZIhlbXd7gYKBTmCNl4x9lnN+gXz/2wBDARUXFx4aHjshITt8U0ZTfHx8fHx8\\nfHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHz/wAARCADcALIDASIA\\nAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA\\nAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\\nODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\\np6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\\nAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\\nBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\\nU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDs6KKO\\nlADJCMFcE8elIPn/AIhx0xRuy2VHJ9e9GCWUhcHvQMeuSORg0tFMkGQPrnHrQIb0DDBbPQinISMq\\neoqOR1VC4dYwO5rAv/EMKZWB5Zn9Qdi0DOmqBpo4nLSOoz0O4Vw39p3ckjM1xKqdSN5/Kq0kzTpv\\nBYuPvfT1oA7iTVbKFd0lwu70XmpotQgkUMHyO5Hb6154s+05ZQx7ZzSpcyLkh2BPJ5oA9JEysgaM\\nhgemDSNyRn5W9a4ey1K5s8SK2VPVfT3rctfEUU2BcKV4++o4z9KAN7Encj6in1Vtb63uQBHKrN6Z\\n5q1QIKKiOWLMvbgVIrbhmgBaKKKACiiigAooooAKZIwHykZyPyp9MXacvn73rQAm1mAyRgelPUkj\\n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\\u003d\\u003d");
        FileOutputStream fileOutputStream = null;
        String image = this.getExternalFilesDir(null)+"/"+"face"+System.currentTimeMillis()+".png";
        try {
            fileOutputStream =new FileOutputStream(image);

            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }*/
    }

    private void initEvent()
    {
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent,0);
            }
        });

    }

    private void hideFragments(FragmentTransaction fragmentTransaction)
    {
        if(mainFragment != null)
        {
            fragmentTransaction.hide(mainFragment);
        }
        if(knowledgeSysFragment != null)
        {
            fragmentTransaction.hide(knowledgeSysFragment);
        }
        if(wxArticleFragment != null)
        {
            fragmentTransaction.hide(wxArticleFragment);
        }
        if(navFragment != null)
        {
            fragmentTransaction.hide(navFragment);
        }
        if(projectFragment != null)
        {
            fragmentTransaction.hide(projectFragment);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        switch (id)
        {
            case R.id.nav_collection:
                Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }
    @AfterPermissionGranted(RC_SD_PERM)
    private void getImages()
    {
        OkHttpManager.getInstance().get(Constants.IMAGES_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JsonElement jsonElement = new JsonParser().parse(response.body().string());

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("images");
                imageInfoList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<ImageBean>>(){}.getType());
                Log.e(TAG,"imageInfoList == "+imageInfoList.size());
                myHandler.sendEmptyMessage(DOWNLOAD_IMAGE);

            }
        });
    }

    private  void downLoadImage()
    {
        ImageBean imageBean = LitePal.findFirst(ImageBean.class);
        if(imageBean != null) {
            String imagePath = imageBean.getUrl();
            if(imagePath.equals(imageInfoList.get(0).getUrl()))
                return ;
        }
        String image = "http://s.cn.bing.net"+ imageInfoList.get(0).getUrl();
        SplashImageService.startAction(this,image,imageInfoList.get(0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 0:
                if(resultCode == RESULT_OK)
                {
                    LoginBean loginBean = (LoginBean) data.getSerializableExtra("loginResult");
                    tv_login.setText(loginBean.getUsername());
                }
                break;
        }
    }

    private void login()
    {
        String userName  = SPUtils.getInstance().getString("userName");
        String password =  SPUtils.getInstance().getString("password");
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password))
        {
            return ;
        }

        OkHttpManager.getInstance().login(LOGIN_URL,  userName, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();
                Log.e(TAG, "response::" + json);
                Gson gson = new Gson();
                JsonPrimitive jsonPrimitive =  gson.fromJson(json, JsonObject.class).getAsJsonPrimitive("errorCode");
                int errorCode = jsonPrimitive.getAsInt();
                if(errorCode == 0)
                {
                    JsonObject jsonObject = gson.fromJson(json,JsonObject.class);
                    JsonObject data =  jsonObject.getAsJsonObject("data");
                    loginBean = gson.fromJson(data.toString(),LoginBean.class);
                    loginBean.setErrorCode(0);
                }
                if (response.isSuccessful()) { //response 请求成功
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    if (cookies.size() > 0) {
                        String session = cookies.get(0);
                        String result = session.substring(0, session.indexOf(";"));
                        String JSESSIONID = result.substring(result.indexOf("=")+1);
                        Log.e(TAG, "JSESSIONID::" + JSESSIONID);
                        SPUtils.getInstance().put("JSESSIONID",JSESSIONID);
                    }

                }


            }

        });
    }
}
