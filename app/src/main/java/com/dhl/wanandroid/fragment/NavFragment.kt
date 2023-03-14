package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.adapter.NavInfoAdapter
import com.dhl.wanandroid.model.NavBean
import com.dhl.wanandroid.vm.KnowledgeTabViewModel
import com.dhl.wanandroid.vm.NavViewModel

/**
 * NavFragment
 * @author dhl
 * @date 2023 1-07
 */
class NavFragment : BaseFragment() {

    private val navInfoList: MutableList<NavBean>  = mutableListOf()

    private val navInfoAdapter: NavInfoAdapter by lazy {
        NavInfoAdapter(activity, R.layout.nav_info_item, navInfoList)
    }

    private val navViewModel:NavViewModel by lazy {
        ViewModelProvider(this).get(NavViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(view)
        toolbar.title = "导航"
        initRcy(view)
        refreshLayout.autoRefresh()
        refreshLayout.setEnableLoadMore(false)
        setAdapter()
        refreshLayout.setOnRefreshListener { getData() }
    }



    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = navInfoAdapter
    }

    private fun getData(){
        navViewModel.getNav().observe(viewLifecycleOwner,{
            navInfoList.clear()
            navInfoList.addAll(it.result!!)
            navInfoAdapter.notifyDataSetChanged()
            refreshLayout.finishRefresh()
        })

    }

    companion object {
        private const val TAG = "NavFragment"
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): NavFragment {
            val fragment = NavFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}