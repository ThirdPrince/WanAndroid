package com.dhl.wanandroid.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.adapter.OnItemClickListener
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.util.SettingUtil.getColor


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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


}