package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.KnowledgeArticleAdapter
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.vm.KnowledgeTabViewModel
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter

/**
 * @author dhl
 * 知识体系下的文章
 * 懒加载
 * @date 2022 12 28
 */
class KnowledgeTabFragment : BaseFragment() {
    var title: String? = null
        private set
    private var articleId: String? = null

    private val knowledgeChildBeanAdapter by lazy {
        KnowledgeArticleAdapter(activity, R.layout.fragment_homepage_item, knowledgeList)
    }
    private var knowledgeList: MutableList<Article> = mutableListOf()

    private var isViewCreate = false
    private var isDataInited = false

    private val knowledgeTabViewModel by lazy {
        ViewModelProvider(this).get(KnowledgeTabViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = arguments!!.getString(TITLE)
            articleId = arguments!!.getString(ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
        recyclerView.adapter = knowledgeChildBeanAdapter
        refreshLayout.autoRefresh()
        refreshLayout.setOnRefreshListener { getData() }
        isDataInited = true
    }

    /**
     * 获取数据
     */
    private fun getData() {
        knowledgeTabViewModel.getArticle(0, articleId!!.toInt()).observe(this, {
            if(it.isSuccess){
                knowledgeList.addAll(it.result!!)
                knowledgeChildBeanAdapter.notifyDataSetChanged()
                knowledgeChildBeanAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                        val article = knowledgeList[position]
                        WebActivity.startActivity(activity, article.title, article.link)
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
        private const val TAG = "KnowledgeTabFragment"
        private const val TITLE = "title"
        private const val ID = "id"

        @JvmStatic
        fun newInstance(param1: String?, param2: String?): KnowledgeTabFragment {
            val fragment = KnowledgeTabFragment()
            val args = Bundle()
            args.putString(TITLE, param1)
            args.putString(ID, param2)
            fragment.arguments = args
            return fragment
        }
    }
}