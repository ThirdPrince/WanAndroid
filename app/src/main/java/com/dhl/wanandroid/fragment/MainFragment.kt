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
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.HomePageAdapter
import com.dhl.wanandroid.app.Constants
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.BannerInfo
import com.dhl.wanandroid.model.HomePageData
import com.dhl.wanandroid.module.GlideImageLoader
import com.dhl.wanandroid.util.APIUtil
import com.dhl.wanandroid.vm.MainViewModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.youth.banner.Banner
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.LitePal.deleteAll
import org.litepal.LitePal.findAll
import org.litepal.LitePal.saveAll
import java.io.IOException
import java.util.*

/**
 * @author dhl
 * 首页Fragment
 */
class MainFragment : BaseFragment() {
    private var banner: Banner? = null

    /**
     * banner List
     */
    private var bannerInfoList: List<BannerInfo>? = null
    private val imageUrlList: MutableList<String?> = ArrayList()
    private var homePageAdapter: HomePageAdapter? = null
    private var headerAndFooterWrapper: HeaderAndFooterWrapper<*>? = null
    private var homePageDataList: MutableList<HomePageData>? = null
    private var mHeaderGroup: LinearLayout? = null

    lateinit var  mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        bannerInfoList = findAll(BannerInfo::class.java)
        homePageDataList!!.addAll(findAll(HomePageData::class.java))
        if (bannerInfoList?.size!! > 0) {
            setBanner()
        }
        mainViewModel.getBanner()
        setHomePageAdapter()
        getBanner()
    }

    private fun initView(view: View) {
        homePageDataList = ArrayList()
        initToolbar(view)
        initRcy(view)
        mHeaderGroup = LayoutInflater.from(activity).inflate(R.layout.fragment_main_banner, null) as LinearLayout
        banner = mHeaderGroup!!.findViewById(R.id.banner)
        toolbar.title = "首页"
        refreshLayout.autoRefresh()
        refreshLayout.setOnRefreshListener {
            pageCount = 0
            getHomePage(pageCount, true)
        }
        refreshLayout.setOnLoadMoreListener {
            pageCount++
            getHomePage(pageCount, false)
        }
    }

    private fun setBanner() {
        imageUrlList.clear()
        for (banner in bannerInfoList!!) {
            imageUrlList.add(banner.imagePath)
        }
        banner!!.setImages(imageUrlList).setImageLoader(GlideImageLoader()).start()
        banner!!.setOnBannerListener { position ->
            val bannerInfo = bannerInfoList!![position]
            WebActivity.startActivity(activity, bannerInfo.title, bannerInfo.url)
        }
    }

    private fun setHomePageAdapter() {
        homePageAdapter = HomePageAdapter(activity, R.layout.fragment_homepage_item, homePageDataList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        headerAndFooterWrapper = HeaderAndFooterWrapper<Any?>(homePageAdapter)
        headerAndFooterWrapper?.addHeaderView(mHeaderGroup)
        recyclerView.adapter = headerAndFooterWrapper
        setListOnClick()
    }

    private fun getBanner() {
        OkHttpManager.getInstance()[Constants.BANNER_URL, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "IOException==" + e.message)
                activity!!.runOnUiThread { refreshLayout.finishRefresh() }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val jsonElement = JsonParser().parse(response.body!!.string())
                val jsonObject = jsonElement.asJsonObject
                val jsonArray = jsonObject.getAsJsonArray("data")
                bannerInfoList = Gson().fromJson(jsonArray.toString(), object : TypeToken<List<BannerInfo?>?>() {}.type)
                deleteAll(BannerInfo::class.java)
                saveAll(bannerInfoList!!)
                activity!!.runOnUiThread { setBanner() }
            }
        }]
    }

    /**
     * 下拉 上滑共用一个方法
     *
     * @param page
     * @param onRefresh
     */
    private fun getHomePage(page: Int, onRefresh: Boolean) {
        var page = page
        if (onRefresh) {
            page = 0
        }
        OkHttpManager.getInstance().getAddCookie(APIUtil.getHomePageUrl(page), true, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "IOException==" + e.message)
                activity!!.runOnUiThread { refreshLayout.finishRefresh() }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val rsp = response.body!!.string()
                Log.e(TAG, "response=$rsp")
                val jsonElement = JsonParser().parse(rsp)
                val jsonObject = jsonElement.asJsonObject
                val data = jsonObject.getAsJsonObject("data")
                val jsonArray = data.getAsJsonArray("datas")
                val pageDataList = Gson().fromJson<ArrayList<HomePageData>>(jsonArray.toString(), object : TypeToken<List<HomePageData?>?>() {}.type)
                if (onRefresh) {
                    homePageDataList!!.clear()
                }
                homePageDataList!!.addAll(pageDataList)
                if (onRefresh) {
                    deleteAll(HomePageData::class.java)
                    saveAll(homePageDataList!!)
                }
                activity!!.runOnUiThread { //homePageAdapter.notifyDataSetChanged();
                    headerAndFooterWrapper!!.notifyDataSetChanged() //一定要headerAndFooterWrapper 刷新
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                    setListOnClick()
                }
            }
        })
    }

    private fun setListOnClick() {
        if (homePageAdapter != null) {
            homePageAdapter!!.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
                override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                    val homePageData = homePageDataList!![position - 1]
                    WebActivity.startActivity(activity, homePageData.title, homePageData.link)
                }

                override fun onItemLongClick(view: View, holder: RecyclerView.ViewHolder, position: Int): Boolean {
                    return false
                }
            })
            homePageAdapter!!.setOnCollectionListener { view, position ->
                val homePageData = homePageDataList!![position - 1]
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