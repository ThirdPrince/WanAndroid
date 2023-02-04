package com.dhl.wanandroid.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dhl.wanandroid.MainActivity;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.ImageSplash;
import com.dhl.wanandroid.util.SystemBar;
import com.dhl.wanandroid.viewmodel.SplashViewModel;

import java.io.File;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 启动页
 */
public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "WelcomeActivity";
    private ImageView imageView;

    private TextView splash_tv;

    private static final int RC_SD_PERM = 1000;

    private static final int WHAT = 1024;

    private SplashViewModel splashViewModel ;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    //取消界面跳转时的动画
                    overridePendingTransition(0, 0);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SystemBar.invasionStatusBar(this);
        SystemBar.invasionNavigationBar(this);
        SystemBar.setStatusBarColor(this, Color.TRANSPARENT);
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        imageView = findViewById(R.id.image);
        splash_tv = findViewById(R.id.splash_tv);
        splashViewModel.getImage().observe(this, new Observer<ImageSplash>() {
            @Override
            public void onChanged(ImageSplash imageBean) {
                showImage(imageBean);
            }
        });

    }

    @AfterPermissionGranted(RC_SD_PERM)
    private void getImage() {

    }

    private void showImage(ImageSplash imageBean){
        if (imageBean != null) {
            String imagePath = imageBean.getImagePath();
            if (new File(imagePath).exists()) {
                Glide.with(this).load(imagePath).into(imageView);
                splash_tv.setText(imageBean.getCopyright());
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(imageView, "alpha", 0.88f, 1f),
                        ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.12f),
                        ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.12f)
                );
                set.setDuration(2500);
                set.start();
            }
            handler.sendEmptyMessageDelayed(WHAT, 2500);
        } else {
            handler.sendEmptyMessageDelayed(WHAT, 500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(WHAT);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> list) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> list) {

        EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_sd),
                RC_SD_PERM,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }
}
