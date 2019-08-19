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


      /*  byte[] bytes = EncodeUtils.base64Decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDABQODxIPDRQSEBIXFRQYHjIhHhwcHj0sLiQySUBMS0dA\n" +
                "                    RkVQWnNiUFVtVkVGZIhlbXd7gYKBTmCNl4x9lnN+gXz/2wBDARUXFx4aHjshITt8U0ZTfHx8fHx8\n" +
                "                    fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHz/wAARCADcALIDASIA\n" +
                "                    AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA\n" +
                "                    AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\n" +
                "                    ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\n" +
                "                    p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\n" +
                "                    AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\n" +
                "                    BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\n" +
                "                    U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\n" +
                "                    uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDsqKKK\n" +
                "                    ACmlSz5P3QOKcSB1IFNG4D19qABTg7T+BpwAAwKBg4OKKAGyNgYH3jwKE6nP3u/vRlS3PUVFc3EM\n" +
                "                    EbSvIqhOpzQMlVSDz0HSnFgoyxAHvXH3/i2YuVs41RezNyTWLLqVxfzj7RO7Dk4zinYR6BJqFqhx\n" +
                "                    9piyOo3ikTU7N+k6H6GvOAyzKwjXa6jOATyKYk0ifMrMMHGQaLDPU0dH5RgfoaVjtUmvMob+4hIK\n" +
                "                    SuvurYrobDxBNCUjum84kcgnBFILHUjcpBJyD+lSZ5x+dZcGuWVz8m/y26ANxitJMFcgg55yKAHU\n" +
                "                    UjEAcnFJuIIDd+9Ah1FAOelFABRRRQAUUUUAFFFNfDAqDg0AAwzk8EY/KkBI+XgnP6UFcHOdoxSp\n" +
                "                    yoY9aBjqrXt7FYw+ZMwUU+8uktLdpZDwOAPU1wOo3lxqEzsZC4zyoB+X6UAi9qviO4lYrb/uojyC\n" +
                "                    Dy1YTXs1wxLk47+9HmAL8/30OFUilkjAQcdRTAhd/UCnKgV1CuxkIyuBwD2pixNKzKvUDOD3pjvI\n" +
                "                    silshlAHvxTAeqyW7rJIjbc8+tLIysdiH5F6H196P9XA/wA4Yvj5Qc475NQqpJHl53elICaJXdiB\n" +
                "                    1AyPen/MzHOQ/vThHhG2ur9m29qVHIwswLjs/cUh2JPPBUBwd/Qk9DWtpOtzWLKjZlg7rnkfSsUE\n" +
                "                    OMH7wp8JMTbmyAQdp96APR7W5hvE8yJtykDgjkfUVKOBswDz0rhNM1Ga0kMsRLbT8yk/eGeRXb2d\n" +
                "                    1FeQiaFgwPH0oE9Cek3fNj2zQx2qT6Uxv9sZ47dqBElFNTcQORinUAFFFFABTJFyAT260rsCCNwB\n" +
                "                    o5H+0KBjIwW5PK9s1LQOlV9QuPstnLKOWAwo9T2oEcz4kvTc3LQxyALB2z1PeucIf5g5Me7k8fkK\n" +
                "                    nZmMx8zOWPz59DUM78FAQyjgE0FER/ezjbkgDGT1NXJEJj4GeOlNs7ckFq1YYcMGIqHItRMLYUYO\n" +
                "                    OSO3r7UOfs4P8aN9wE9B3+lbbWisclcZ6kdDWbeWJQnAzj9aakDiZvGflBH86mto5AyzIuCp6f3q\n" +
                "                    aIWDYUE+mKlB3NE2MNGBx0zzVXJsJCjLMSMqH4ce1THAUL1YffPv6UkMp8plmQALjaQMGmtlHLry\n" +
                "                    D196AAr0wal8xXOw52Hge3vTYzHhmbJXHGOooaE7d8ZDr6jtSGNLSWs2fut3x3FbHhvUfsd8EZsQ\n" +
                "                    TnBHo3asScs8eeu3gj2ptoQWYEkKoycdaZPkepFiScAEDrnvQBgZX06GqOh3gvLBHJy4+VvfFX1X\n" +
                "                    n2HSgQqjaOTk0MdozS0xirArnmgQu5/7lFOHTk0UAR8LkMMgnrSpwSBkr2p9FABXOeKbkt5FtG4B\n" +
                "                    3b2GeuOldHXE63ItxqUxzjafLU9RQNGaSRkSLkDseCKhaPoAKtkA4QHdt4z/AEpI13zDHrUtmiRf\n" +
                "                    s4QsYyKvqPTp9KiiTCgVZjBwM1lc0GmMYzUUtsGXpVwLkAUhUr9KYHPz2hgkEidM81X2KXYygELy\n" +
                "                    HHf0zXSSRK4PH4Vj39lIi5QEoOdvpVJiaMmddx+9nb05pYsSMinucUrKQPT604KihAycPyT3Hbir\n" +
                "                    M2hpIkVvlCMvI2jGfaoxuiwy5ww5X2qQIIz5gYSBTggZFV3cs5Y8H09KYh2/B45Boki8rIjzuIyR\n" +
                "                    6j0piKZGYKQMc4Jx+VOBdWIbIYHPPXNAHR+Drs+dJbE4LDI/CuyAwMCvNNKvBbarDOowAwDD+del\n" +
                "                    KQygjkGglinpTMExkY5pJQRhhR5hHDLzQAbH/v0Uu3P/AC0P50UAPooprMchVGT70CFdgiM54CjJ\n" +
                "                    rz+V/Nk3f3iW/Wu11KUDT5jjkrjFcE7bQe3AFJlxJYyNjP6kmprJN0gJ7VVU/u8D0rXsolUAEEkg\n" +
                "                    c1mzVFuMcVMoqMkRrk//AKqVJ0PU4+tKw7lhaDzSKwPQ08DIoAZjmkI/WnY5owaQGddaZHK+9RtP\n" +
                "                    cdjWNcWLo5CgqR2NdVkD73HvVe48hgQxBqk2JpHHTybBtEZUE/Pk8k1Ch8xwinknGa17+wRiTCSD\n" +
                "                    6Gsn7NJburM/luT8nGea1TMmhJFwjGOTdjhhjHFJHcMRtbDY6Z6ilRWD5lBjD5UkjFRO4LgKPkXg\n" +
                "                    UxEoJEmR65Fen6fOr6fbv2ZB+FeYAFiMZJr0bQBu0a1DcMFzikDNM4ZfrTFQsTv5xwKXYVOUOPan\n" +
                "                    KSRyMGgQbF9KKWigQE4BJ7VGT0LArkdqczEkqBkDrSAYAZcsMdDQMp6mW/s98AYHU+1cDOx8z8a7\n" +
                "                    7VlI0q56Z25rz58tIdoz6VLLiW7NPMJJPyghfrW7EpQDA496ytJT93jKuN3OD0NbWM4xwAPyqHua\n" +
                "                    JkYiUZJOSetTRwR4zk1XmnWMZbj2HJpk129vaCcx8FgoUt/P0poGXCm08GnKxHSs6C8mnneMxxgj\n" +
                "                    JAWTJxVuOQtg9qTTBalndxmkLcdaFOaYzYOKQxHy3eojboxyf0pXcjAGSWOBWPd6ncwzNEQqFSRg\n" +
                "                    oT9Oc96pJidkajQRA98e5qvc2kUq8qODwaJGnWCORgMsoJB4xToZDJtPIJ6ijYNzn76we2DhS0iu\n" +
                "                    c/T/AOvWY67SDjg13EkIdCCK5vWLPyWUqMAmqiyGiGyzuOOpBCn0JrvvDsf/ABJLdXyGAPXr1rjt\n" +
                "                    LG2AsEBcNg59K7jSoBFpsCocfLkfjzTvqS1oXEJyVPJHenUioF57+tI5GCM4zTIDevrRTh0ooAav\n" +
                "                    3jng/WkCnOOwOaVwchh16U5RgYoGRXUXn2ssX99CK89Szm81t0bAKSrdsdq9HJA6nFYmqWqpKJ9v\n" +
                "                    LcHPc0mVDsYWiRGPenTtzWwF4xVG1XbcSnGFOCK0F5rNmmxWktt5JpRB+7Kbsof4WFXFXrz0pSnH\n" +
                "                    rQtCjOitltXdoSQ79T3/ADqxFEVXccnNWFj3NyMY6UrAdPTpQ3cCNeD+FRsDvqUZJ9M0w8NzSAZJ\n" +
                "                    FvUH0qI2yyOrOAWXoTyRV1BSlB24pgVDb55ySfegQBTnGKt7SKawFICu1ZupoHaLcMgNWm4OTjpV\n" +
                "                    G7Hz8kEbeKZJC6LbwHykA9APU8Cuyto/Kt4o/wC4oH6VzFnb/aru2RugYO30HP8APFdXVxIn2DpT\n" +
                "                    Sud3Q56GnEZBFQ8ghUJBPUelUQO2gf8ALT9aKcOg+X9aKAFTOMN2paKRjgE0CGld7nPQcCqWrKWs\n" +
                "                    mB6owP4VfVwfY02WJZUZW6EYpDTszl4VkUDfyueCPerqdM1CYWi3xMeQcAU9SRgEVmb9SynvUnXF\n" +
                "                    Qoe9TL0oGFQythsAc1KehquzFWOACccZoAVQcc/limSZx05oinYkiWMxkfiDTZbhgwEUXmEnHXAF\n" +
                "                    AEkL87TU44qn8zOMDBHX2q2DxSAcahdsVLmoZDz0oAru2SeagJGSR3PWp2B5Lk1HBFJPkWylueR6\n" +
                "                    fjVEmpo0Qw8pXDcLWpUFlb/ZrdUJy3Vj71K3LYbp2q1oYvVifOuf4hnpSrtJLDr3pOUIBOVJx70+\n" +
                "                    mAUUUUCCo9z/AHsfLTnfacAZNCbdgHr2NAx2AR7UAYGM0xQTwein86fQIydaYW7QzY4ZtjHH5GqE\n" +
                "                    cgddwOQTV3xF/wAeZDEhSwwfesLSpG2yRNjCtlSDnIP/ANeoZrHY2UOOKmB4qqjYqbdUmhIWHrUT\n" +
                "                    EHrUcswB2k4qE3MYP3wfYUgLB5HPSmnj7pxVcXIOemPrQboLhgBjpyaY7Msq2BjpT0YHNZ7XcY/i\n" +
                "                    xmnwXIZ/l5pCL5bioJTk49+tOJOMnpULE96aELFGZJhF03Hn2rctLWO1iCRqAAOAKzdLh8ycyH7q\n" +
                "                    jH41s1aMpPUKCM9aKKogbsGe59qcTgZNFBGRg0AN3P8A3P1opPLPZ2ooGOA+bcDximMqhcclvagc\n" +
                "                    ZA4OePenqMDkc+tAC01+WC9BSsdqk0wFlPzHdkdBQBi+KXKWEYPP70fXoa5OGVrTUC2TsH3gOmK6\n" +
                "                    vxbKF0xWH3vMGPY+tcbMSQpHUgCl1KWx1cMgdQQcg9DVgHNYti7wv5LAkBA3I961EcMMg/hWbVjV\n" +
                "                    O4ssYbPfiq3kgHlQR9Kt5zQVBIpIrYgFtCRkEKT1GKPLhCkHGOuMVOYi3OTTfI96dx8xReIM+dvH\n" +
                "                    0q5bxhVAApTGFNKDgGkS2Okfjj0qBizlQvLMdtJK2VJ7DrV7QollaSY87SAv1ppXJbsjUsrcW1uq\n" +
                "                    fxdWPvUrnAGenenVHk5BB57itTHcFyAdnK+9SA5ANMxkkHj1x3p4GBgUAwoppcg9OPWnBgehzQIK\n" +
                "                    KKKAGqysc9COxqGe/tLbPnXEaY7Fua4LUNdvb5iWkMaf3EOB/wDXrN+eUkjLEc8nmnYDvpfE2npu\n" +
                "                    2SGTHHArKuvFtxG2UtkjiBxljuJrkll2MVfO1hhv8aSe5JHlRn90AByOvvRYZp6xrUuqEeYAAh4A\n" +
                "                    96pRzKs0EkilkQqWA9BVRCTu96tNKY4YtjcA/MB/WkNGppt4LjUZMEkOpPPrmtkoRhl69xXP6MIm\n" +
                "                    vWaJGTaCQSetdIvPJPNZy3NI7CRyZ4PBqcDHWq7xgnPAoV3U+tSWWRIVOKGkxVVpWJ+6aTzmPAU5\n" +
                "                    oAsk7unWq8sm3rxTZJHHYZ+vSok3E5Jz9aAE3t97pnse9Zc+s3mmXhFrLtUqCVIyDWrLjHHFc5qS\n" +
                "                    eZqCg9CvOO+M1cdyJ7GmPFupeT5jOhy2MbOlTDxZfCSPCxyKwyMrg1zTBJI2aNdhXquc5HrSiYpA\n" +
                "                    IypzzhvY9f5VoZHb2Pi+3kfF1E0ZP8SnIroILyC5jD28iyg/3TXlCEnGM1fhlls8sj5wRu2MQVNF\n" +
                "                    gPSuo+U9/umnjJfOMADFcPF4jvI8OZY3jx91vvf49a1bHxfBJhbuIxH+8vIpWA6WioEvbeRFdZVK\n" +
                "                    sMg57UUCPLcEkAck8U4xyRFtpVjjBCnJFCOY5Aw6qc0NEXctAc98Z+YVQyJp1k4nXPow6/8A16rV\n" +
                "                    PddUz/rNvz/WoKQiSBykqsOxzV55I5SQ67VYbt6jjFV0gjlKLC/7wgfKe59q1LHTX8tBcgALyF/x\n" +
                "                    qW7FpMn0uN1jDPjHRcDGR61tREYqoFAIA7VZjB2jFZN3NUiVhx1GKZjucEmpkPHWmFec9PagYgT8\n" +
                "                    aaUwQfQ1KpoZsc4xxyKAKgGGz/OniMLmpBGuT82SPahycD2oAqTnap9ayNRtS8alM+aoznP6VrSq\n" +
                "                    XfA7c1WkT1GKadiWrnMQuI5D5gO0gqwHWkdzI5Y8eg9BWlqdqHYSRqAx+9jv71mojNIEA5J71qmY\n" +
                "                    tW0LFtHJlZFA4PGSBk1JAw8/HRXypHpmmMSoRTwycZB/GppQCvmt/GowB3bvVARyKEwgHI+8fU1G\n" +
                "                    CaesMr9EOPU8VYSxbrIwGOcCgtU5S2RJHelIkXJ+VQKKeLaHAooL9hIllt45MlkGaoy2RGTGd3t3\n" +
                "                    q4bh0OJIyB7c05XVwSp4oOqUYT0ZhtEzMAoJbpjvTo0Cxsxj3sGAIPatnaokDlfmxgH1qvc25/10\n" +
                "                    BOe4Pce9JmEsO0ro0NPs4EQOqAMRzzmtLHT6YrOguAFAUAZFXoX3CsGRYcFy3FSqCpwKRFIbNTFc\n" +
                "                    jNAwQ+uPrSnmmKMU6kMco65zSMOMjkUopCecg0wGYA9c1HI3p1qU+uKhcfN1A9M0gIdnJIOfWmOe\n" +
                "                    MMM+mamHHXj1qCYjOB0FMRRmXcenSqVxDsDMg+Zvl+vtWntyc1DPcQrgudu0YDD/ADzVx3BRuVYb\n" +
                "                    ISRgTMN6jgA9R9aVpIbcYG0egFQyvLM22Dbs67geBSxW8cXzNiR/7zdB+FamsV/IvmOW5llOYI+A\n" +
                "                    fvNwKZIJSuGlyeeF6UlxclU3DJ7DJ4FU0un3Hc3B9ulIJSinaTuWxswMtJnvRS7ov7yf99UUByw7\n" +
                "                    mh5ZAyTyeeaqywsGBT5W6cd60MDBPuR+QpXiQrjHpVWOuUVLczRM6f6xSM9SO9TJICvYr3x2qSSN\n" +
                "                    VTj0zVW4iWJHdMgqfWkZvmhre6Jgdp46dqvW8lY8MjFwOx6/lWhD1HNZSRzzs9V1NmJgSCMA1ZXB\n" +
                "                    rNj4XqakDt61ncgtlcN1p2PaoIXJcA8irG7p0oANnpSbfWrAAIprgYpgQNwKpkFn5HA71Zn+4aqh\n" +
                "                    ypHv1pIB8jEIcnJPvVNjyc9BUkjktWdfSshwp4wT+VUtWJuwl7qSQxlIW/eE4OPSs99s7LM7ZBBL\n" +
                "                    DsCOv9KSJftqEzElg33hwelVJGaPfCp+TdWq0Mm2y7b3S+SN7AFW4U8AVE9+2/5VBX/aqlRQV7aV\n" +
                "                    kkTz3TzqFZVAHpUJXAzR2pQeKZm227sbRS0U7CP/2Q==");
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
