package com.dhl.wanandroid


import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.dhl.wanandroid.activity.*
import com.dhl.wanandroid.fragment.*
import com.dhl.wanandroid.model.LoginBean
import com.dhl.wanandroid.service.SplashImageService.Companion.startDownLoadAction
import com.dhl.wanandroid.util.SettingUtil
import com.dhl.wanandroid.util.Settings
import com.dhl.wanandroid.util.SystemBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
class MainActivity : BasicActivity(), NavigationView.OnNavigationItemSelectedListener {



    /**
     * 底部Bottom
     */
    private val bottomNav: BottomNavigationView by lazy {
        findViewById(R.id.bottom_navigation)
    }
    private val drawerLayout: DrawerLayout by lazy {
        findViewById(R.id.drawerLayout)
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
    private var sysFragment: SysFragment? = null

    /**
     * 公众号Fragment
     */
    private var wxArticleFragment: WxArticleFragment? = null

    /**
     * 导航
     */
    private var squareFragment: SquareFragment? = null

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


    private class MyHandler(context: MainActivity) : Handler() {
        private val mActivity: WeakReference<MainActivity> = WeakReference(context)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val mainActivity = mActivity.get()
            if (mainActivity != null) {
                when (msg.what) {
                    //DOWNLOAD_IMAGE -> mainActivity.downLoadImage()
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
        mainFragment = BaseFragment.newInstance()
        if (savedInstanceState == null) {
            fragmentTransaction.add(
                R.id.content,
                mainFragment!!,
                MainFragment::class.java.simpleName
            ).commit()
        } else {
            mainFragment =
                (fm.findFragmentByTag(MainFragment::class.java.simpleName) as MainFragment?)!!
            sysFragment =
                fm.findFragmentByTag(SysFragment::class.java.simpleName) as SysFragment?
            wxArticleFragment =
                fm.findFragmentByTag(WxArticleFragment::class.java.simpleName) as WxArticleFragment?
            squareFragment =
                fm.findFragmentByTag(SquareFragment::class.java.simpleName) as SquareFragment?
        }
        initEvent()
        downLoadImage()
    }

    private fun initView() {

        navView.setNavigationItemSelectedListener(this)
        val headerView = navView?.getHeaderView(0)
        tv_login = headerView?.findViewById(R.id.tv_login)
        toolbar.run {
            title = getString(R.string.title_home)
            setSupportActionBar(this)
            //supportActionBar?.setBackgroundDrawable(ColorDrawable(SettingUtil.getColor()))
        }
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.app_name, R.string.app_name
        )
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
                    mainFragment = BaseFragment.newInstance()
                    fragmentTransaction.add(
                        R.id.content,
                        mainFragment,
                        MainFragment::class.java.simpleName
                    )
                } else {
                    fragmentTransaction.show(mainFragment)
                }
                toolbar!!.title = getString(R.string.title_home)
            }
            KNOWLEDGE_INDEX -> {
                if (sysFragment == null) {
                    sysFragment = BaseFragment.newInstance()
                    fragmentTransaction.add(
                        R.id.content,
                        sysFragment!!,
                        KnowledgeSysFragment::class.java.simpleName
                    )
                } else {
                    fragmentTransaction.show(sysFragment!!)
                }
                toolbar!!.title = getString(R.string.title_system)
            }
            WX_ARTICLE_INDEX -> {
                if (wxArticleFragment == null) {
                    wxArticleFragment = BaseFragment.newInstance()
                    fragmentTransaction.add(
                        R.id.content,
                        wxArticleFragment!!,
                        WxArticleFragment::class.java.simpleName
                    )
                } else {
                    fragmentTransaction.show(wxArticleFragment!!)
                }
                toolbar.title = getString(R.string.title_wx)
            }
            NAV_INDEX -> {
                if (squareFragment == null) {
                    squareFragment = SquareFragment.newInstance("", "")
                    fragmentTransaction.add(
                        R.id.content,
                        squareFragment!!,
                        SquareFragment::class.java.simpleName
                    )
                } else {
                    fragmentTransaction.show(squareFragment!!)
                }
                toolbar.title = getString(R.string.title_square)
            }
            PROJECT_INDEX -> {
                if (projectFragment == null) {
                    projectFragment = BaseFragment.newInstance()
                    fragmentTransaction.add(
                        R.id.content,
                        projectFragment!!,
                        ProjectFragment::class.java.simpleName
                    )
                } else {
                    fragmentTransaction.show(projectFragment!!)
                }
                toolbar.title = getString(R.string.title_project)
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
        if (sysFragment != null) {
            fragmentTransaction.hide(sysFragment!!)
        }
        if (wxArticleFragment != null) {
            fragmentTransaction.hide(wxArticleFragment!!)
        }
        if (squareFragment != null) {
            fragmentTransaction.hide(squareFragment!!)
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
                    //val isNight =  read(Settings.NightMode)
//                    if(isNight == true){
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                      //  save(Settings.NightMode,false)
//                    }else{
//                      //  save(Settings.NightMode,true)
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//
//                    }
                    window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
                    recreate()
                }

            }

            R.id.nav_setting -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, HotActivity::class.java)
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
    @Deprecated("Use ImageWork")
    private fun downLoadImage() {
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
        /*
         * 退出应用
         */
        private const val EXIT_APP = 1025

    }

}