package com.dhl.wanandroid.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dhl.wanandroid.MainActivity
import com.dhl.wanandroid.R
import com.dhl.wanandroid.model.ImageSplash
import com.dhl.wanandroid.util.SystemBar
import com.dhl.wanandroid.vm.AppScope
import com.dhl.wanandroid.vm.MainViewModel
import com.dhl.wanandroid.vm.SplashViewModel
import java.io.File

/**
 * 启动页
 * @author dhl
 */
class SplashActivity : AppCompatActivity() {


    private val splashViewModel: SplashViewModel by lazy {
        AppScope.getAppScopeViewModel(SplashViewModel::class.java)
        // ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    /**
     * viewModel
     */
    private val mainViewModel: MainViewModel by lazy {
        AppScope.getAppScopeViewModel(MainViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        //window.setBackgroundDrawable(null)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        initSystemBar()
        goAD()
//        splashViewModel.getImage().observe(this) {
//                imageBean -> showImage(imageBean) }
    }

    /**
     * 全面屏展示
     */
    private fun initSystemBar() {
        SystemBar.invasionStatusBar(this)
        SystemBar.invasionNavigationBar(this)
        SystemBar.setStatusBarColor(this, Color.TRANSPARENT)
    }


    companion object {
        private const val WHAT = 1024
        private const val WHAT_JUMP = 1025
    }


    private fun goAD() {
        //startActivity(Intent(this@SplashActivity, AdActivity::class.java))
        val activityOptions = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(
            Intent(this@SplashActivity, AdActivity::class.java),
            activityOptions.toBundle()
        )
        finish()
        //取消界面跳转时的动画
        //overridePendingTransition(0, 0)
    }
}