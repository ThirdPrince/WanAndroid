package com.dhl.wanandroid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.activity.CollectionActivity
import com.dhl.wanandroid.activity.MaterialLoginActivity
import com.dhl.wanandroid.app.Constants
import com.dhl.wanandroid.app.LoginInfo
import com.dhl.wanandroid.fragment.*
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.LoginBean
import com.dhl.wanandroid.service.SplashImageService.Companion.startDownLoadAction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
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
    private val bottomNav: BottomNavigationView by  lazy {
        findViewById(R.id.bottom_navigation)
    }
    private val drawerLayout: DrawerLayout by lazy {
        findViewById(R.id.drawerLayout)
    }
    private val toolbar: Toolbar by lazy {
        findViewById(R.id.tool_bar)
    }
    private var navView: NavigationView? = null
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

    private var loginBean: LoginBean? = null

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
        login()
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

        navView = findViewById(R.id.nav_view)
        navView?.setNavigationItemSelectedListener(this)
        val headerView = navView?.getHeaderView(0)
        tv_login = headerView?.findViewById(R.id.tv_login)
        val userName = SPUtils.getInstance().getString("userName")
        if (!TextUtils.isEmpty(userName)) {
            tv_login?.text = userName
        }
        toolbar.title = ("首页")
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
        bottomNav!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
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
        }
        return true
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

    private fun login() {
        val userName = SPUtils.getInstance().getString("userName")
        val password = SPUtils.getInstance().getString("password")
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return
        }
        OkHttpManager.getInstance().login(Constants.LOGIN_URL, userName, password, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                Log.e(TAG, "response::$json")
                val gson = Gson()
                val jsonPrimitive = gson.fromJson(json, JsonObject::class.java).getAsJsonPrimitive("errorCode")
                val errorCode = jsonPrimitive.asInt
                if (errorCode == 0) {
                    val jsonObject = gson.fromJson(json, JsonObject::class.java)
                    val data = jsonObject.getAsJsonObject("data")
                    loginBean = gson.fromJson(data.toString(), LoginBean::class.java)
                    LoginInfo.getInstance().setLoginInfo(loginBean)
                    loginBean?.setErrorCode(0)
                }
                if (response.isSuccessful) { //response 请求成功
                    val headers = response.headers
                    val cookies = headers.values("Set-Cookie")
                    if (cookies.size > 0) {
                        val session = cookies[0]
                        val result = session.substring(0, session.indexOf(";"))
                        val JSESSIONID = result.substring(result.indexOf("=") + 1)
                        Log.e(TAG, "JSESSIONID::$JSESSIONID")
                        SPUtils.getInstance().put("JSESSIONID", JSESSIONID)
                    }
                }
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SD_PERM = 1024

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