package com.dhl.wanandroid.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dhl.wanandroid.MainActivity;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.ImageBean;
import com.dhl.wanandroid.module.GlideApp;
import com.google.gson.JsonElement;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";
    private ImageView imageView ;

    private TextView splash_tv ;
   /* DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();*/
    private ObjectAnimator objectAnimator ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        imageView = findViewById(R.id.image);
        splash_tv = findViewById(R.id.splash_tv);
        //objectAnimator = ObjectAnimator.ofFloat(imageView,"alpha",0.6f,1.0f);

        ImageBean imageBean = LitePal.findLast(ImageBean.class);
        if(imageBean != null) {
            String imagePath = imageBean.getImagePath();
            //Environment.getExternalStorageDirectory().getAbsolutePath()+"/splash.jpg";
            Log.e(TAG, "imagePath ==" + imagePath);
            if (new File(imagePath).exists()) {
                Glide.with(this).load(imagePath).into(imageView);
                splash_tv.setText(imageBean.getCopyright());
                AnimatorSet set = new AnimatorSet();
                //ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.12f)
                set.playTogether(
                        ObjectAnimator.ofFloat(imageView,"alpha",0.88f,1f),
                        ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.12f),
                        ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.12f)
                );
                set.setDuration(2500);
                set.start();
                //imageView.startAnimation(anim);
                //GlideApp.with(this).load(imagePath).into(imageView);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();

            }
        },2500);
       // getImages();
    }


}
