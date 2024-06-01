package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.dhl.wanandroid.R
import com.dhl.wanandroid.adapter.WxArticlePgAdapter
import com.dhl.wanandroid.vm.WxArticleTabViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * 微信公众号TAb Fragment
 * @author dhl
 */
class WxArticleTabFragment : BaseFragment() {
    var title: String? = null
        private set
    private var articleId: Int = 0


    private val wxArticlePgAdapter: WxArticlePgAdapter by lazy {
        WxArticlePgAdapter(requireContext(), this)
    }
    private var isViewCreate = false
    private var isDataInited = false

    private val wxArticleTabViewModel by lazy {
        ViewModelProvider(this).get(WxArticleTabViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)
            articleId = requireArguments().getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wx_article_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcy()
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
        refreshLayout.isRefreshing = true
        getData()
        refreshLayout.setOnRefreshListener { getData() }
        isDataInited = true
    }

    /**
     * 获取数据
     */
    private fun getData() {
        lifecycleScope.launch {
            wxArticleTabViewModel.getArticles(articleId).collect {
                wxArticlePgAdapter.submitData(it)
            }

        }
        wxArticlePgAdapter.addLoadStateListener { loadState ->
            // 只在加载完成时停止刷新动画
            if (loadState.source.refresh is LoadState.NotLoading) {
                refreshLayout.isRefreshing = false
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
        fun newInstance(param1: String?, param2: Int): WxArticleTabFragment {
            val fragment = WxArticleTabFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putInt(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}