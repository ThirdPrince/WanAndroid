package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.WxArticleAdapter
import com.dhl.wanandroid.adapter.WxArticlePgAdapter
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.vm.WxArticleTabViewModel
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * 微信公众号TAb Fragment
 * @author dhl
 */
class WxArticleTabFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    var title: String? = null
        private set
    private var articleId: String? = null



    private lateinit var wxArticlePgAdapter: WxArticlePgAdapter
    private var isViewCreate = false
    private var isDataInited = false

    private val wxArticleTabViewModel by lazy {
        ViewModelProvider(this).get(WxArticleTabViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)
            articleId = requireArguments().getString(ARG_PARAM2)
        }
        wxArticlePgAdapter = WxArticlePgAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wx_article_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcy(view)
        isViewCreate = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isViewCreate && userVisibleHint && !isDataInited) {
            onLoadData()
        }

    }

    /**
     * 加载数据
     */
    private fun onLoadData() {
        recyclerView.adapter = wxArticlePgAdapter
        refreshLayout.autoRefresh()
        refreshLayout.setOnRefreshListener { getData() }
        isDataInited = true
    }

    /**
     * 获取数据
     */
    private fun getData() {
        lifecycleScope.launch {
            wxArticleTabViewModel.getArticles(articleId!!.toInt()).collectLatest {
                wxArticlePgAdapter.submitData(it)

            }
        }

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