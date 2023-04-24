package com.dhl.wanandroid.fragment

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhl.wanandroid.R
import com.dhl.wanandroid.util.SettingUtil.getColor
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


/**
 * @author dhl
 * Fragment 基类
 * Fragment 的存在，是 专注于承担 “视图控制器” 责任，
 * 以分担 Activity 责任、让 Activity 更专注于 “幕后协调者” 工作。
 */
abstract class BaseFragment : Fragment() {
    /**
     * 共用ToolBar
     */
    protected val toolbar: Toolbar by lazy {
        requireView().findViewById(R.id.tool_bar)
    }

    /**
     * smartRefresh
     */
    protected val refreshLayout: SmartRefreshLayout by lazy {
        requireView().findViewById(R.id.refreshLayout)
    }


    protected var materialHeader: MaterialHeader? =null
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


    protected fun initToolbar(view: View) {
        toolbar.background = (ColorDrawable(getColor()))
    }

    protected fun initRcy(view: View) {
        materialHeader = refreshLayout.refreshHeader as MaterialHeader ?
      //  mClassicsHeader!!.setTimeFormat(SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA))
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    companion object {
        private const val TITLE = "title"
    }
}