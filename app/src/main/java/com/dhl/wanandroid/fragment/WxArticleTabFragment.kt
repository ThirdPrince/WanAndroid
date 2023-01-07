package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.WxArticleAdapter
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.WxArticleBean
import com.dhl.wanandroid.util.APIUtil
import com.dhl.wanandroid.vm.WxArticleTabViewModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.LitePal.deleteAll
import org.litepal.LitePal.saveAll
import org.litepal.LitePal.where
import java.io.IOException

/**
 * 微信公众号TAb Fragment
 * @author dhl
 */
class WxArticleTabFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    var title: String? = null
        private set
    private var articleId: String? = null

    private val wxArticleAdapter: WxArticleAdapter by lazy {
        WxArticleAdapter(activity, R.layout.fragment_homepage_item, wxArticleBeanList)
    }
    private var wxArticleBeanList: MutableList<Article> = mutableListOf()

    private var isViewCreate = false
    private var isDataInited = false

    private val wxArticleTabViewModel by lazy {
        ViewModelProvider(this).get(WxArticleTabViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = arguments!!.getString(ARG_PARAM1)
            articleId = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wx_article_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
        initRcy(view)
        isViewCreate = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG, "onActivityCreated ::isViewCreate ==$isViewCreate::getUserVisibleHint()==$userVisibleHint")
        if (isViewCreate && userVisibleHint && !isDataInited) {
            onLoadData()
        }

    }

    /**
     * 加载数据
     */
    private fun onLoadData() {
        recyclerView.adapter = wxArticleAdapter
        refreshLayout.autoRefresh()
        refreshLayout.setOnRefreshListener { getData() }
        isDataInited = true
    }

    private fun getData(){
        wxArticleTabViewModel.getArticle(0,articleId!!.toInt()).observe(this,{
            if(it.isSuccess){
                wxArticleBeanList.addAll(it.result!!)
                wxArticleAdapter.notifyDataSetChanged()
                wxArticleAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                        val wxArticleBean = wxArticleBeanList!![position]
                        WebActivity.startActivity(activity, wxArticleBean.title, wxArticleBean.link)
                    }

                    override fun onItemLongClick(view: View, holder: RecyclerView.ViewHolder, position: Int): Boolean {
                        return false
                    }
                })
            }else{
                ToastUtils.showLong(it.errorMessage)
            }
            refreshLayout.finishRefresh()

        })
    }



    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isViewCreate && !isDataInited) {
            onLoadData()
        }
    }



    companion object {
        private const val TAG = "WxArticleTabFragment"
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String?, param2: String?): WxArticleTabFragment {
            val fragment = WxArticleTabFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}