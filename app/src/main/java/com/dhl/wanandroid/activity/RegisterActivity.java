package com.dhl.wanandroid.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.RegisterBean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 注册
 */
public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";


    FloatingActionButton fab;

    CardView cvAdd;


    EditText et_username;


    EditText et_password;


    EditText et_repeatpassword;


    Button bt_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register:
                String userName = et_username.getText().toString();
                String password = et_password.getText().toString();
                String repeatPassword = et_repeatpassword.getText().toString();
                register(userName, password, repeatPassword);
                break;

            case R.id.fab:
                animateRevealClose();
                break;
        }
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    /**
     * 注册
     */
    private void register(String userName, String password, String repeatPassword) {

        OkHttpManager.getInstance().register(Constants.REGISTER_URL, userName, password, repeatPassword, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rsp = response.body().string();
                Log.e(TAG, "rsp::" + rsp);
                Gson gson = new Gson();
                RegisterBean registerBean = gson.fromJson(rsp, RegisterBean.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (registerBean.getErrorCode() == 0) {
                            ToastUtils.showLong("注册成功");
                            animateRevealClose();
                        } else {
                            ToastUtils.showLong(registerBean.getErrorMsg());
                        }

                    }
                });


            }
        });
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
}
