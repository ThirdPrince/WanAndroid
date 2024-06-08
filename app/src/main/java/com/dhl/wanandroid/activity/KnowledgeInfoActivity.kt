package com.dhl.wanandroid.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.fragment.BaseFragment
import com.dhl.wanandroid.fragment.KnowledgeTabFragment
import com.dhl.wanandroid.model.KnowledgeTreeData
import com.dhl.wanandroid.util.SettingUtil
import com.google.android.material.tabs.TabLayout

/**
 * 知识体系 详情
 * @author dhl
 * @date 2022 12-28
 */
class KnowledgeInfoActivity : BasicActivity() {

    private val tabLayout: TabLayout by lazy {
        findViewById(R.id.tab_layout)
    }

    private val viewPager: ViewPager by lazy {
        findViewById(R.id.content_vp)
    }

    /**
     * TabLayout title
     */
    private var indicators: MutableList<String> = mutableListOf()

    private var fragmentList: MutableList<KnowledgeTabFragment> = mutableListOf()

    private lateinit var knowledgeTreeData: KnowledgeTreeData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge_info)
        initView()
        initData()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        tabLayout.setBackgroundColor(SettingUtil.getColor())
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        intent.let {
            knowledgeTreeData = it.getSerializableExtra("KnowledgeTreeData") as KnowledgeTreeData
            supportActionBar?.title = knowledgeTreeData.name
            for ((_, _, _, _, id, _, _, name) in knowledgeTreeData.children) {
                indicators.add(name)
                fragmentList.add(BaseFragment.newInstance(name, id.toString() + ""))
            }
            viewPager!!.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
                override fun getItem(i: Int): Fragment {
                    return fragmentList[i]
                }

                override fun getCount(): Int {
                    return fragmentList.size
                }

                override fun getPageTitle(position: Int): CharSequence? {
                    return indicators[position]
                }
            }
            tabLayout.setupWithViewPager(viewPager)
            viewPager.offscreenPageLimit = fragmentList.size
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 启动传参
     */
    companion object {
        fun startActivity(activity: Activity, knowledgeTreeData: KnowledgeTreeData?) {
            val intent = Intent(activity, KnowledgeInfoActivity::class.java)
            intent.putExtra("KnowledgeTreeData", knowledgeTreeData)
            activity.startActivity(intent)
        }
    }
}