package com.dhl.wanandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.fragment.HotSearchFragment
import com.dhl.wanandroid.fragment.SettingsFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            title = getString(R.string.setting)
            setNavigationIcon(R.mipmap.back)
            setNavigationOnClickListener {
                finish()
            }
        }
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.content,SettingsFragment()).commit()
        }




    }
}