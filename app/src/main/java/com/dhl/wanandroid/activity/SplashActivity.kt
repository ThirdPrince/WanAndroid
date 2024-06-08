package com.dhl.wanandroid.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dhl.wanandroid.MainActivity
import com.dhl.wanandroid.R
import com.dhl.wanandroid.model.ImageSplash
import com.dhl.wanandroid.util.SystemBar
import com.dhl.wanandroid.vm.AppScope
import com.dhl.wanandroid.vm.MainViewModel
import com.dhl.wanandroid.vm.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

/**
 * 启动页
 * @author dhl
 */
class SplashActivity : AppCompatActivity() {


    private val imageView: ImageView by lazy {
        findViewById(R.id.image)
    }
    private val splashTv: TextView by lazy {
        findViewById(R.id.splash_tv)
    }

    private val jumpBtn: TextView by lazy {
        findViewById(R.id.jump_btn)
    }
    private val splashViewModel: SplashViewModel by lazy {
        AppScope.getAppScopeViewModel(SplashViewModel::class.java)
    }


    /**
     * viewModel
     */
    private val mainViewModel: MainViewModel by lazy {
        AppScope.getAppScopeViewModel(MainViewModel::class.java)
    }

    /**
     * 广告倒计时
     */
    @Deprecated("instead of coroutines")
    private val countDownTimer = object : CountDownTimer(AD_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000 + 1;
            jumpBtn.text = getString(R.string.splash_jump) + "(${seconds})"

        }

        override fun onFinish() {
            goMain()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        //window.setBackgroundDrawable(null)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        initSystemBar()
        preLoad()
        splashViewModel.getImage().observe(this) { imageBean ->
            showImage(imageBean)
        }
        jumpBtn.setOnClickListener {
            goMain()
        }
    }

    /**
     * 全面屏展示
     */
    private fun initSystemBar() {
        SystemBar.invasionStatusBar(this)
        SystemBar.invasionNavigationBar(this)
        SystemBar.setStatusBarColor(this, Color.TRANSPARENT)
    }

    private fun preLoad() {
        mainViewModel.getBanner()
        mainViewModel.getArticles()
    }


    companion object {
        /**
         * 广告时长
         */
        private const val AD_TIME = 5000L

        /**
         * 没有广告时长
         */
        private const val JUMP_TIME = 1000L
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun showImage(imageBean: ImageSplash?) {
        if (imageBean != null) {
            val imagePath = imageBean.imagePath
            if (File(imagePath).exists()) {
                Glide.with(this).load(imagePath).into(imageView!!)
                splashTv.text = imageBean.copyright
                val set = AnimatorSet()
                set.playTogether(
                    ObjectAnimator.ofFloat(imageView, "alpha", 0.88f, 1f),
                    ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.12f)
                )
                set.duration = 2500
                set.start()
                jumpBtn.visibility = View.VISIBLE
                jumpBtn.text = getString(R.string.splash_jump) + "(5)"
                adShow()
            }

        } else {
            lifecycleScope.launch {
                delay(JUMP_TIME)
                goMain()
            }
        }

    }


    /**
     * 展示广告页
     */
    private fun adShow() {
        lifecycleScope.launch {
            for (i in 3 downTo 1) {
                delay(1000)
                jumpBtn.text = getString(R.string.splash_jump) + "(${i})"
            }
            goMain()

        }
    }

    private fun goMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        //取消界面跳转时的动画
        overridePendingTransition(0, 0)
    }


}