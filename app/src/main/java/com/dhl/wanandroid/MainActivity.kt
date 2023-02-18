package com.dhl.wanandroid


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.activity.CollectionActivity
import com.dhl.wanandroid.activity.HotActivity
import com.dhl.wanandroid.activity.MaterialLoginActivity
import com.dhl.wanandroid.fragment.*
import com.dhl.wanandroid.model.LoginBean
import com.dhl.wanandroid.service.SplashImageService.Companion.startDownLoadAction
import com.dhl.wanandroid.util.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 *@author dhl
 * 玩Android 主页面
 */

private const val MAIN_INDEX = 0x001

private const val KNOWLEDGE_INDEX = 0x002

private const val WX_ARTICLE_INDEX = 0x003

private const val NAV_INDEX = 0x004

private const val PROJECT_INDEX = 0x005

/**
 * @author dhl
 * 主页面
 * 包括五个tab ,首页，知识体系，公众号，导航，项目
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    /**
     * 底部Bottom
     */
    private val bottomNav: BottomNavigationView by lazy {
        findViewById(R.id.bottom_navigation)
    }
    private val drawerLayout: DrawerLayout by lazy {
        findViewById(R.id.drawerLayout)
    }
    private val toolbar: Toolbar by lazy {
        findViewById(R.id.tool_bar)
    }

    private val navView: NavigationView by lazy {
        findViewById(R.id.nav_view)
    }
    private var tv_login: TextView? = null

    /**
     * 首页Fragment
     */
    private lateinit var mainFragment: MainFragment

    /**
     * 知识体系fragment
     */
    private var knowledgeSysFragment: KnowledgeSysFragment? = null

    /**
     * 公众号Fragment
     */
    private var wxArticleFragment: WxArticleFragment? = null

    /**
     * 导航
     */
    private var navFragment: NavFragment? = null

    /**
     * 项目
     */
    private var projectFragment: ProjectFragment? = null

    /**
     * FragmentManager
     */
    private val fm: FragmentManager by lazy {
        supportFragmentManager
    }
    private val fragmentTransaction: FragmentTransaction by lazy {
        fm.beginTransaction()
    }
    var ft: FragmentTransaction? = null




    /**
     * sp
     */
    private val dataStore: DataStore<Preferences> by lazy {
        createDataStore(name = "settings")
    }

    private class MyHandler(context: MainActivity) : Handler() {
        private val mActivity: WeakReference<MainActivity> = WeakReference(context)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val mainActivity = mActivity.get()
            if (mainActivity != null) {
                when (msg.what) {
                    DOWNLOAD_IMAGE -> mainActivity.downLoadImage()
                    EXIT_APP -> {
                    }
                }
            }
        }

    }

    /**
     * Handler
     */
    private val myHandler: MyHandler by lazy {
        MyHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        mainFragment = MainFragment.newInstance("", "")
        if (savedInstanceState == null) {
            fragmentTransaction.add(R.id.content, mainFragment!!, MainFragment::class.java.simpleName).commit()
        } else {
            mainFragment = (fm.findFragmentByTag(MainFragment::class.java.simpleName) as MainFragment?)!!
            knowledgeSysFragment = fm.findFragmentByTag(KnowledgeSysFragment::class.java.simpleName) as KnowledgeSysFragment?
            wxArticleFragment = fm.findFragmentByTag(WxArticleFragment::class.java.simpleName) as WxArticleFragment?
            navFragment = fm.findFragmentByTag(NavFragment::class.java.simpleName) as NavFragment?
        }
        initEvent()
        downLoadImage()
    }

    private fun initView() {

        navView.setNavigationItemSelectedListener(this)
        val headerView = navView?.getHeaderView(0)
        tv_login = headerView?.findViewById(R.id.tv_login)
        toolbar.run {
            title = "首页"
            setSupportActionBar(this)
        }
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initEvent() {
        tv_login!!.setOnClickListener {
            val intent = Intent(this@MainActivity, MaterialLoginActivity::class.java)
            startActivityForResult(intent, 0)
        }
        bottomNav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            ft = fm!!.beginTransaction()
            hideFragments(ft!!)
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    selectTab(MAIN_INDEX)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_knowledge -> {
                    selectTab(KNOWLEDGE_INDEX)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_wx_article -> {
                    selectTab(WX_ARTICLE_INDEX)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_nav -> {
                    selectTab(NAV_INDEX)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_project -> {
                    selectTab(PROJECT_INDEX)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }


    /**
     * 选择 底部Tab 加载对应的fragment
     */
    private fun selectTab(index: Int) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        hideFragments(fragmentTransaction)
        when (index) {
            MAIN_INDEX -> {
                if (mainFragment == null) {
                    mainFragment = MainFragment.newInstance("", "")
                    fragmentTransaction.add(R.id.content, mainFragment, MainFragment::class.java.simpleName)
                } else {
                    fragmentTransaction.show(mainFragment)
                }
                toolbar!!.title = "首页"
            }
            KNOWLEDGE_INDEX -> {
                if (knowledgeSysFragment == null) {
                    knowledgeSysFragment = KnowledgeSysFragment.newInstance("", "")
                    fragmentTransaction.add(R.id.content, knowledgeSysFragment!!, KnowledgeSysFragment::class.java.simpleName)
                } else {
                    fragmentTransaction.show(knowledgeSysFragment!!)
                }
                toolbar!!.title = "知识体系"
            }
            WX_ARTICLE_INDEX -> {
                if (wxArticleFragment == null) {
                    wxArticleFragment = WxArticleFragment.newInstance("", "")
                    fragmentTransaction.add(R.id.content, wxArticleFragment!!, WxArticleFragment::class.java.simpleName)
                } else {
                    fragmentTransaction.show(wxArticleFragment!!)
                }
                toolbar!!.title = "公众号"
            }
            NAV_INDEX -> {
                if (navFragment == null) {
                    navFragment = NavFragment.newInstance("", "")
                    fragmentTransaction.add(R.id.content, navFragment!!, NavFragment::class.java.simpleName)
                } else {
                    fragmentTransaction.show(navFragment!!)
                }
                toolbar.title = "导航"
            }
            PROJECT_INDEX -> {
                if (projectFragment == null) {
                    projectFragment = ProjectFragment.newInstance("", "")
                    fragmentTransaction.add(R.id.content, projectFragment!!, ProjectFragment::class.java.simpleName)
                } else {
                    fragmentTransaction.show(projectFragment!!)
                }
                toolbar.title = "项目"
            }

        }

        fragmentTransaction.commit()

    }

    /**
     * 隐藏fragment
     */
    private fun hideFragments(fragmentTransaction: FragmentTransaction) {
        if (mainFragment != null) {
            fragmentTransaction.hide(mainFragment!!)
        }
        if (knowledgeSysFragment != null) {
            fragmentTransaction.hide(knowledgeSysFragment!!)
        }
        if (wxArticleFragment != null) {
            fragmentTransaction.hide(wxArticleFragment!!)
        }
        if (navFragment != null) {
            fragmentTransaction.hide(navFragment!!)
        }
        if (projectFragment != null) {
            fragmentTransaction.hide(projectFragment!!)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_collection -> {
                val intent = Intent(this@MainActivity, CollectionActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_night_mode -> {

                lifecycleScope.launch {
                   val isNight =  read(Settings.NightMode)
                    if(isNight == true){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        save(Settings.NightMode,false)
                    }else{
                        save(Settings.NightMode,true)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                    }
                    window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
                    recreate()
                }

            }
        }
        return true
    }

    /**
     * 保存 暗黑模式
     */
    private suspend fun save(key: String, boolean: Boolean) {
        val dataStoreKey = preferencesKey<Boolean>(key)
        dataStore.edit { settings ->
            Log.e("tag", Thread.currentThread().name)
            settings[dataStoreKey] = boolean
        }
    }

    /**
     * 读取暗黑模式
     */
    private suspend fun read(key: String): Boolean? {
        val dataStoreKey = preferencesKey<Boolean>(key)
        Log.e("tag", Thread.currentThread().name)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_search -> {
                val intent = Intent(this,HotActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (myHandler.hasMessages(EXIT_APP)) {
            finish()
        } else {
            ToastUtils.showShort("再按一次退出程序")
            myHandler.sendEmptyMessageDelayed(EXIT_APP, 2000)
        }
    }


    /**
     * 开启下载图片的service
     */
    private fun downLoadImage() {
        startDownLoadAction(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
                val loginBean = data!!.getSerializableExtra("loginResult") as LoginBean
                tv_login!!.text = loginBean.username
            }
        }
    }




    companion object {
        private const val TAG = "MainActivity"

        /**
         * 下载
         */
        private const val DOWNLOAD_IMAGE = 1024

        /**
         * 退出应用
         */
        private const val EXIT_APP = 1025

    }

}