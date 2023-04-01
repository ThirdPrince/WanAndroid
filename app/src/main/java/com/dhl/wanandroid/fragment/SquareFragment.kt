package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dhl.wanandroid.R
import com.dhl.wanandroid.adapter.HomePageAdapter
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.vm.SquareViewModel



/**
 * 广场Fragment
 * @author dhl
 * @date 2023 04 01
 * @version V2.0
 */
class SquareFragment : BaseFragment() {


    private val squareList: MutableList<Article> = mutableListOf()

    /**
     * adapter
     */
    private val homePageAdapter: HomePageAdapter by lazy {
        HomePageAdapter(activity, R.layout.fragment_homepage_item, squareList)
    }

    /**
     * viewModel
     */
    private val squareViewModel: SquareViewModel by lazy {
        ViewModelProvider(this).get(SquareViewModel::class.java)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_square, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRcy(view)
        recyclerView.adapter = homePageAdapter
        refreshLayout.autoRefresh()
        refreshLayout.setEnableLoadMore(false)
        refreshLayout.setOnRefreshListener {
            getData()
        }
    }

    /**
     * 获取数据
     */
    private fun getData() {
        squareViewModel.getSquareList(0).observe(viewLifecycleOwner, Observer {
            it.let {
                squareList.addAll(it.result!!)
            }
            refreshLayout.finishRefresh()
            homePageAdapter.notifyDataSetChanged()

        })
    }




    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): SquareFragment {
            val fragment = SquareFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}