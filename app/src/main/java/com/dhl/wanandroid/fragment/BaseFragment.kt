package com.dhl.wanandroid.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.OnItemClickListener
import com.dhl.wanandroid.adapter.WxArticlePgAdapter
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.util.SettingUtil.getColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * @author dhl
 * Fragment 基类
 * Fragment 的存在，是 专注于承担 “视图控制器” 责任，
 * 以分担 Activity 责任、让 Activity 更专注于 “幕后协调者” 工作。
 */
abstract class BaseFragment : Fragment(), OnItemClickListener {
    /**
     * 共用ToolBar
     */
    protected val toolbar: Toolbar by lazy {
        requireView().findViewById(R.id.tool_bar)
    }

    /**
     * smartRefresh
     */
    protected val refreshLayout: SwipeRefreshLayout by lazy {
        requireView().findViewById(R.id.refreshLayout)
    }


    /**
     * rcy
     */
    protected val recyclerView: RecyclerView by lazy {
        requireView().findViewById(R.id.rcy_view)
    }

    // 使用 lazy 初始化适配器
    protected val basePgAdapter: WxArticlePgAdapter by lazy {
        WxArticlePgAdapter(requireContext(), this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        basePgAdapter.addLoadStateListener { loadState ->
            // 只在加载完成时停止刷新动画
            if (loadState.source.refresh is LoadState.NotLoading) {
                lifecycleScope.launch {
                    delay(500)
                    refreshLayout.isRefreshing = false
                }

            }
        }
    }


    protected fun initToolbar() {
        toolbar.background = (ColorDrawable(getColor()))
    }

    protected fun initRcy() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onItemClick(article: Article) {
        WebActivity.startActivity(requireActivity(), article.title, article.link)
    }

    companion object{
        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"
         inline fun <reified T : Fragment> newInstance(param1: String = "", param2: String = ""): T {
            val fragment = T::class.java.newInstance()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }



}