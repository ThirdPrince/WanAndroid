package com.dhl.wanandroid.activity
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * 启动页
 * @author dhl
 */
class AdActivity : AppCompatActivity() {

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
                    //goMain()
                }
                WHAT_JUMP -> {
                    count--
                    jumpBtn.text = getString(R.string.splash_jump) + "(${count}s)"
                    sendEmptyMessageDelayed(WHAT_JUMP, 1000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        initSystemBar()
        splashViewModel.getImage().observe(this) { imageBean -> showImage(imageBean) }
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
                handler.sendEmptyMessageDelayed(WHAT_JUMP, 1000)
                handler.sendEmptyMessageDelayed(WHAT, 5000)
            }

        } else {
            handler.sendEmptyMessageDelayed(WHAT, 1500)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeMessages(WHAT)
    }

    companion object {
        private const val WHAT = 1024
        private const val WHAT_JUMP = 1025
    }

    private fun goMain() {
        handler.removeMessages(WHAT)
        handler.removeMessages(WHAT_JUMP)
        startActivity(Intent(this@AdActivity, MainActivity::class.java))
        finish()
        //取消界面跳转时的动画
        overridePendingTransition(0, 0)
    }
}