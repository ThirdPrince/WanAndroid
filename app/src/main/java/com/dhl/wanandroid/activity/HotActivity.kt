package com.dhl.wanandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhl.wanandroid.R
import com.dhl.wanandroid.fragment.HotSearchFragment

/**
 * 热门搜索
 * SearchFragment 复用
 */
class HotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot)
        supportFragmentManager.beginTransaction().add(R.id.content,HotSearchFragment()).commit()
    }
}