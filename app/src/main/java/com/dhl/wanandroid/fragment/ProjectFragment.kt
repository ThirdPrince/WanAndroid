package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.vm.ProjectViewModel
import com.google.android.material.tabs.TabLayout


/**
 * 项目Fragment
 * @author dhl
 * @Date 2023 1.11
 */
class ProjectFragment : BaseFragment() {

    /**
     * tabLayout
     */
    private val tabLayout: TabLayout by lazy {
        view!!.findViewById(R.id.tab_layout)
    }

    /**
     * viewPager
     */
    private val viewPager: ViewPager by lazy {
        view!!.findViewById(R.id.content_vp)
    }

    private var wxArticleTabFragments: MutableList<WxArticleTabFragment> = mutableListOf()

    private var tabIndicator: MutableList<String> = mutableListOf()


    private val projectViewModel: ProjectViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(view)
        getData()
    }

    /**
     * 获取数据
     */
    private fun getData() {
        projectViewModel.getProject().observe(this, {
            wxArticleTabFragments.clear()
            tabIndicator.clear()
            for (projectBean in it.result!!) {
                tabIndicator.add(projectBean.name)
                wxArticleTabFragments.add(WxArticleTabFragment.newInstance(projectBean.name, projectBean.id.toString() + ""))
            }
            viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
                override fun getItem(i: Int): Fragment {
                    return wxArticleTabFragments[i]
                }

                override fun getCount(): Int {
                    return wxArticleTabFragments.size
                }

                override fun getPageTitle(position: Int): CharSequence? {
                    return tabIndicator[position]
                }
            }
            viewPager.offscreenPageLimit = wxArticleTabFragments.size
            tabLayout.setupWithViewPager(viewPager)
        })
    }


    companion object {
        private const val TAG = "ProjectFragment"
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): ProjectFragment {
            val fragment = ProjectFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}