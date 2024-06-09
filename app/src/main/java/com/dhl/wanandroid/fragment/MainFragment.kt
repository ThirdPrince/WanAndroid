package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.BannerAdapter
import com.dhl.wanandroid.adapter.OnBannerItemClickListener
import com.dhl.wanandroid.adapter.WxArticlePgAdapter
import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.module.GlideImageLoader
import com.dhl.wanandroid.vm.AppScope
import com.dhl.wanandroid.vm.MainViewModel
import com.youth.banner.Banner
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * @author dhl
 * 首页Fragment
 */
class MainFragment : BaseFragment(), OnBannerItemClickListener {


    /**
     * banner List
     */
    private var bannerList: MutableList<BannerBean> = mutableListOf()

    private val wxArticlePgAdapter: WxArticlePgAdapter by lazy {
        WxArticlePgAdapter(requireContext(), this)
    }


    /**
     * viewModel
     */
    private val mainViewModel: MainViewModel by lazy {
        AppScope.getAppScopeViewModel(MainViewModel::class.java)
    }
    private val bannerAdapter by lazy {
        BannerAdapter(bannerList, this)
    }
    private val concatAdapter = ConcatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initToolbar()
        initRcy()
        initData()

    }

    private fun initData() {
        recyclerView.adapter = concatAdapter
        toolbar.title = "首页"
        refreshLayout.setOnRefreshListener {
            getData()
        }

        getData()
        observeData()
    }

    /**
     * 初始化数据
     */
    private fun getData() {
        mainViewModel.getBanner()
    }

    private fun observeData() {
        mainViewModel.resultBanner.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                bannerList = it.result!!
                concatAdapter.addAdapter(bannerAdapter)
                concatAdapter.addAdapter(1, wxArticlePgAdapter)
            } else {
                ToastUtils.showLong(it.errorMessage)
            }

        }
        lifecycleScope.launchWhenStarted {
            mainViewModel.getArticles().collect {
                Log.d("MainViewModel", "collect over")
                wxArticlePgAdapter.submitData(it)
            }
        }


    }

    override fun onItemClick(bannerBean: BannerBean) {
        WebActivity.startActivity(requireActivity(), bannerBean.title, bannerBean.url)
    }


}