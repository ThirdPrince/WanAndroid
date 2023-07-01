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
import android.util.Log
import android.view.View
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

    private  val TAG = "SplashActivity"

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

    var count = 5
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                WHAT -> {
                     goMain()
                }
                WHAT_JUMP -> {
                    if(count == 0){
                        goMain()
                    }else{
                        jumpBtn.text = getString(R.string.splash_jump) + "(${count})"
                        count--
                        sendEmptyMessageDelayed(WHAT_JUMP, 1000)
                    }

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //window.setBackgroundDrawable(null)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        initSystemBar()
        preLoad()
       // goAD()
        splashViewModel.getImage().observe(this) {
                imageBean ->
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
        mainViewModel.getArticle(0)
    }


    companion object {
        private const val WHAT = 1024
        private const val WHAT_JUMP = 1025
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
                handler.sendEmptyMessage(WHAT_JUMP)
                //handler.sendEmptyMessageDelayed(WHAT, 5000)
            }

        } else {
            handler.sendEmptyMessageDelayed(WHAT, 1500)
        }
    }



    private fun goMain() {
        handler.removeMessages(WHAT)
        handler.removeMessages(WHAT_JUMP)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        //取消界面跳转时的动画
        overridePendingTransition(0, 0)
    }
}