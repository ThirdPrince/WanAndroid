package com.dhl.wanandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhl.wanandroid.R
import com.dhl.wanandroid.fragment.HotSearchFragment
import com.dhl.wanandroid.fragment.SettingsFragment

/**
 * 设置 Activity
 * @author dhl
 */
class SettingsActivity : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)
        toolbar.run {
            setTitle(getString(R.string.setting))
            setNavigationIcon(R.mipmap.back)
            setNavigationOnClickListener {
                finish()
            }
        }
        if(savedInstanceState ==null){
            supportFragmentManager.beginTransaction().replace(R.id.content,SettingsFragment()).commit()
        }

    }
}