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
import com.dhl.wanandroid.activity.MaterialLoginActivity;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.app.LoginInfo;
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

    /**
     * 下载
     */
    private static final  int DOWNLOAD_IMAGE = 1024 ;

    /**
     * 退出应用
     */
    private static final  int EXIT_APP = 1025 ;
    /**
     * 必应图片
     */
      private static List<ImageBean> imageInfoList ;

      private  LoginBean loginBean ;




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
                    case EXIT_APP:

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
        initEvent();
        getImages();
        // Ask for one permission
        //bottom_navigation.setSelectedItemId(R.id.navigation_home);

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

    }

    private void initEvent()
    {
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MaterialLoginActivity.class);
                startActivityForResult(intent,0);
            }
        });

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
        //moveTaskToBack(true);
        if(myHandler.hasMessages(EXIT_APP))
        {
            finish();
        }else {
            ToastUtils.showShort("再按一次退出程序");
            myHandler.sendEmptyMessageDelayed(EXIT_APP, 2000);
        }
    }
    //@AfterPermissionGranted(RC_SD_PERM)
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
                    LoginInfo.getInstance().setLoginInfo(loginBean);
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
