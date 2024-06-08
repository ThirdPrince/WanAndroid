package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.util.SettingUtil
import com.google.android.material.tabs.TabLayout


/**
 * 体系Fragment 包括知识体系和导航
 * @author dhl
 * @date 2023 0401
 * @version V2.0
 */
class SysFragment : BaseFragment() {

    /**
     * tabLayout
     */
    private val tabLayout: TabLayout by lazy {
        requireView().findViewById(R.id.tab_layout)
    }

    /**
     * viewPager
     */
    private val viewPager: ViewPager by lazy {
        requireView().findViewById(R.id.content_vp)
    }
    private var sysFragmentList: MutableList<BaseFragment> = mutableListOf()


    /**
     * viewPage title
     */
    private var tabIndicator: MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
        toolbar.title = getString(R.string.title_system)
        initFragment()
    }

    override fun onResume() {
        super.onResume()
        tabLayout.setBackgroundColor(SettingUtil.getColor())
    }

    /**
     *
     */
    private fun  initFragment(){
        tabIndicator.add(getString(R.string.title_knowledge))
        tabIndicator.add(getString(R.string.title_nav))
        sysFragmentList.add(newInstance<KnowledgeSysFragment>())
        sysFragmentList.add(newInstance<NavFragment>())
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(i: Int): Fragment {
                return sysFragmentList[i]
            }

            override fun getCount(): Int {
                return sysFragmentList.size
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabIndicator[position]
            }
        }
        viewPager.offscreenPageLimit = sysFragmentList.size
        tabLayout.setupWithViewPager(viewPager)
    }


}