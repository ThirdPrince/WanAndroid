package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.KnowledgeInfoActivity
import com.dhl.wanandroid.adapter.KnowledgeAdapter
import com.dhl.wanandroid.model.KnowledgeTreeData
import com.dhl.wanandroid.util.SettingUtil
import com.dhl.wanandroid.vm.KnowledgeSysViewModel
import com.google.android.material.tabs.TabLayout
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter


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
        initToolbar(view)
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
        sysFragmentList.add(KnowledgeSysFragment.newInstance("",""))
        sysFragmentList.add(NavFragment.newInstance("",""))
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(i: Int): Fragment {
                return sysFragmentList[i]
            }

            override fun getCount(): Int {
                return sysFragmentList.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabIndicator[position]
            }
        }
        viewPager.offscreenPageLimit = sysFragmentList.size
        tabLayout.setupWithViewPager(viewPager)
    }


    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): SysFragment {
            val fragment = SysFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}