package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.WxArticlePgAdapter
import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.module.GlideImageLoader
import com.dhl.wanandroid.vm.AppScope
import com.dhl.wanandroid.vm.MainViewModel
import com.youth.banner.Banner
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * @author dhl
 * 首页Fragment
 */
class MainFragment : BaseFragment() {


    /**
     * banner List
     */
    private var bannerList: MutableList<BannerBean> = mutableListOf()

    /**
     * banner ImageList
     */
    private val imageUrlList: MutableList<String> = mutableListOf()


    private val wxArticlePgAdapter: WxArticlePgAdapter by lazy {
        WxArticlePgAdapter(requireContext(), this)
    }




    /**
     * 头
     */
    private val headerAndFooterWrapper: HeaderAndFooterWrapper<*> by lazy {
        HeaderAndFooterWrapper<Any?>(wxArticlePgAdapter)
    }


    /**
     * banner Header
     */
    private val mHeaderGroup: LinearLayout by lazy {
        LayoutInflater.from(activity).inflate(R.layout.fragment_main_banner, null) as LinearLayout
    }

    /**
     * Banner
     */
    private val banner: Banner by lazy {
        mHeaderGroup.findViewById(R.id.banner)
    }

    private var pageCount = 0

    /**
     * viewModel
     */
    private val mainViewModel: MainViewModel by lazy {
        AppScope.getAppScopeViewModel(MainViewModel::class.java)
    }

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
        recyclerView.adapter = headerAndFooterWrapper
        headerAndFooterWrapper.addHeaderView(mHeaderGroup)
        toolbar.title = "首页"
        refreshLayout.setOnRefreshListener {
            pageCount = 0
            getData()
        }

        pageCount++
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
                setBanner()
            } else {
                ToastUtils.showLong(it.errorMessage)
            }

        }
        lifecycleScope.launch{
            mainViewModel.getArticles().collect {
                wxArticlePgAdapter.submitData(it)
            }
        }


    }

    /**
     * 给Banner 赋值
     */
    private fun setBanner() {
        imageUrlList.clear()
        for (banner in bannerList) {
            imageUrlList.add(banner.imagePath)
        }
        banner.setImages(imageUrlList).setImageLoader(GlideImageLoader()).start()
        banner.setOnBannerListener { position ->
            val bannerBean = bannerList[position]
            WebActivity.startActivity(requireActivity(), bannerBean.title, bannerBean.url)
        }
    }

}