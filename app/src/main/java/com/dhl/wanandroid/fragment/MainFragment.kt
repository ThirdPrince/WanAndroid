package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.HomePageAdapter
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.module.GlideImageLoader
import com.dhl.wanandroid.util.APIUtil
import com.dhl.wanandroid.vm.MainViewModel
import com.youth.banner.Banner
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

import java.io.IOException
import java.util.*

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

    /**
     * adapter
     */
    private val homePageAdapter: HomePageAdapter by lazy {
        HomePageAdapter(activity, R.layout.fragment_homepage_item, homePageDataList)
    }

    /**
     * 头
     */
    private val headerAndFooterWrapper: HeaderAndFooterWrapper<*> by lazy {
        HeaderAndFooterWrapper<Any?>(homePageAdapter)
    }

    private val homePageDataList: MutableList<Article> = mutableListOf()

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

    /**
     * viewModel
     */
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        initToolbar(view)
        initRcy(view)
        initData()

    }

    private fun initData(){
        recyclerView.adapter = headerAndFooterWrapper
        headerAndFooterWrapper.addHeaderView(mHeaderGroup)
        toolbar.title = "首页"
        refreshLayout.autoRefresh()
        refreshLayout.setOnRefreshListener {
            pageCount = 0
            getData(0)
        }
        refreshLayout.setOnLoadMoreListener {
            pageCount++
            getData(pageCount)
        }

    }

    /**
     * 初始化数据
     */
    private fun getData(pageNo:Int){
        mainViewModel.getBanner().observe(this,{
            if(it.isSuccess){
                bannerList = it.result!!
                setBanner()
                setHomePageAdapter()
            }else{
                ToastUtils.showLong(it.errorMessage)
            }
            refreshLayout.finishRefresh()
            refreshLayout.finishLoadMore()
        })

        mainViewModel.getArticle(pageNo).observe(this,{
            if(it.isSuccess){
                if(pageNo == 0){
                    homePageDataList.clear()
                }
                homePageDataList.addAll(it.result!!)
                headerAndFooterWrapper.notifyDataSetChanged() //一定要headerAndFooterWrapper 刷新

                setListOnClick()
            }else{
                ToastUtils.showLong(it.errorMessage)
            }
            refreshLayout.finishRefresh()
            refreshLayout.finishLoadMore()

        })
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
            WebActivity.startActivity(activity, bannerBean.title, bannerBean.url)
        }
    }

    /**
     * setAdapter
     */
    private fun setHomePageAdapter() {
        setListOnClick()
    }


    /**
     * onClick
     */
    private fun setListOnClick() {
            homePageAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
                override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                    val homePageData = homePageDataList[position - 1]
                    WebActivity.startActivity(activity, homePageData.title, homePageData.link)
                }

                override fun onItemLongClick(view: View, holder: RecyclerView.ViewHolder, position: Int): Boolean {
                    return false
                }
            })
            homePageAdapter.setOnCollectionListener { view, position ->
                val homePageData = homePageDataList[position - 1]
                OkHttpManager.getInstance().postCollectionOut(APIUtil.collectionArticleOut(), homePageData.title, homePageData.author, homePageData.link, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val rsp = response.body!!.string()
                        Log.e(TAG, "rsp=$rsp")
                        activity!!.runOnUiThread { view.isSelected = true }
                    }
                })
            }

    }

    companion object {
        private const val TAG = "MainFragment"

        /**
         *
         */
        private var pageCount = 0
        fun newInstance(param1: String?, param2: String?): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}