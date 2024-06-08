package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.adapter.NavInfoListAdapter
import com.dhl.wanandroid.adapter.OnItemClickListener
import com.dhl.wanandroid.vm.NavViewModel

/**
 * NavFragment
 * @author dhl
 * @date 2023 1-07
 */
class NavFragment : BaseFragment() {

    /**
     * adapter
     */
    private val navInfoListAdapter: NavInfoListAdapter by lazy {
        NavInfoListAdapter(requireActivity(), R.layout.nav_info_item,this)
    }


    private val navViewModel: NavViewModel by lazy {
        ViewModelProvider(this).get(NavViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        toolbar.title = getString(R.string.title_nav)
        initRcy()
        setAdapter()
        getData()
        refreshLayout.setOnRefreshListener { getData() }
    }


    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = navInfoListAdapter
    }

    private fun getData() {
        navViewModel.getNav().observe(viewLifecycleOwner) { repo ->
            repo.result?.let {
                navInfoListAdapter.submitList(it)
            }
        }

    }


}