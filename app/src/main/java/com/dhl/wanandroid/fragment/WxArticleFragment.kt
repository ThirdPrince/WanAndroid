package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.util.SettingUtil
import com.dhl.wanandroid.vm.WxArticleViewModel
import com.google.android.material.tabs.TabLayout


/**
 * 公众号 Fragment
 * dhl
 * @Date 2023 1.11
 */
class WxArticleFragment : BaseFragment() {


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
    private var wxArticleTabFragmentList: MutableList<WxArticleTabFragment> = mutableListOf()


    /**
     * viewPage title
     */
    private var tabIndicator: MutableList<String> = mutableListOf()

    /**
     * viewModel
     */

    private val wxArticleViewModel: WxArticleViewModel by lazy {
        ViewModelProvider(this).get(WxArticleViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wx_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        toolbar.title = getString(R.string.title_wx)
        getData()

    }

    override fun onResume() {
        super.onResume()
        tabLayout.setBackgroundColor(SettingUtil.getColor())
    }

    /**
     * 获取data
     */
    private fun getData() {
        wxArticleViewModel.getWxArticleChapters().observe(viewLifecycleOwner) {
            wxArticleTabFragmentList.clear()
            tabIndicator.clear()
            it.result?.let { result ->
                for (baseData in result) {
                    tabIndicator.add(baseData.name)
                    wxArticleTabFragmentList.add(
                        WxArticleTabFragment.newInstance(
                            baseData.name,
                            baseData.id
                        )
                    )
                }
                viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
                    override fun getItem(i: Int): Fragment {
                        return wxArticleTabFragmentList[i]
                    }

                    override fun getCount(): Int {
                        return wxArticleTabFragmentList.size
                    }

                    override fun getPageTitle(position: Int): CharSequence {
                        return tabIndicator[position]
                    }
                }
                viewPager.offscreenPageLimit = wxArticleTabFragmentList.size
                tabLayout.setupWithViewPager(viewPager)
            }

        }
    }


    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): WxArticleFragment {
            val fragment = WxArticleFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}