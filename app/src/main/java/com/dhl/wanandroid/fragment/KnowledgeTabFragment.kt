package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dhl.wanandroid.R
import com.dhl.wanandroid.adapter.WxArticlePgAdapter
import com.dhl.wanandroid.vm.KnowledgeTabViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author dhl
 * 知识体系下的文章
 * 懒加载
 * @date 2022 12 28
 */
class KnowledgeTabFragment : BaseFragment() {
    var title: String? = null
        private set
    private var articleId: String = ""

    private var isViewCreate = false
    private var isDataInited = false

    private val knowledgeTabViewModel by lazy {
        ViewModelProvider(this).get(KnowledgeTabViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)
            articleId = requireArguments().getString(ARG_PARAM2)?:""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
        recyclerView.adapter = basePgAdapter
        getData()
        refreshLayout.setOnRefreshListener { getData() }
        isDataInited = true
    }

    /**
     * 获取数据
     */
    private fun getData() {
        lifecycleScope.launch {
            knowledgeTabViewModel.getArticles(articleId.toInt()).collect {
                basePgAdapter.submitData(it)
            }
        }

    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isViewCreate && !isDataInited) {
            onLoadData()
        }
    }

}