package com.dhl.wanandroid.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dhl.wanandroid.R
import com.dhl.wanandroid.util.SettingUtil
import com.dhl.wanandroid.util.SystemBar

/**
 * BasicActivity
 * @author dhl
 * activity 基类
 */
abstract class BasicActivity : AppCompatActivity() {


    protected val toolbar: Toolbar by lazy {
        findViewById(R.id.tool_bar)
    }
    override fun onResume() {
        super.onResume()
        initColor()
    }

    private fun initColor(){
        SystemBar.setStatusBarColor(this, SettingUtil.getColor())
        if (this.supportActionBar != null) {
            this.supportActionBar?.setBackgroundDrawable(ColorDrawable(SettingUtil.getColor()))
        }
    }
}