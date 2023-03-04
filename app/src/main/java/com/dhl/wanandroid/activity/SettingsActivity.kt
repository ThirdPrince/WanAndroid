package com.dhl.wanandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhl.wanandroid.R
import com.dhl.wanandroid.fragment.HotSearchFragment
import com.dhl.wanandroid.fragment.SettingsFragment

/**
 * 设置Activity
 * @author dhl
 */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot)
        if(savedInstanceState ==null){
            supportFragmentManager.beginTransaction().replace(R.id.content,SettingsFragment()).commit()
        }

    }
}