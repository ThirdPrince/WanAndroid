package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ConvertUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.model.HotSearchBean
import com.dhl.wanandroid.util.CommonUtils
import com.dhl.wanandroid.vm.HotSearchViewModel
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout


/**
 * HotSearchFragment
 * @author dhl
 * @date 2023 2.18
 */
class HotSearchFragment : BaseFragment() {


    /**
     * ViewModel
     */
    private val searchViewModel: HotSearchViewModel by lazy {
        ViewModelProvider(this).get(HotSearchViewModel::class.java)
    }

    /**
     * 热词
     */
    private val hotSearchFlowLayout: TagFlowLayout by lazy {
        view!!.findViewById(R.id.hot_search_flow_layout)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(view)
        initData()
        getData()

    }

    private fun initData() {
        toolbar.title = getString(R.string.action_search)
        setHasOptionsMenu(true)
        toolbar.run {
            //设置menu 关键代码
            (activity as AppCompatActivity).setSupportActionBar(this)
            setNavigationIcon(R.mipmap.back)
            setNavigationOnClickListener {
                activity?.finish()
            }
        }
    }

    /**
     * getData
     */
    private fun getData() {
        searchViewModel.getSearchHot().observe(this, Observer {
            hotSearchFlowLayout.adapter =
                (object : TagAdapter<HotSearchBean?>(it.result!! as List<HotSearchBean?>?) {
                    override fun getView(
                        parent: FlowLayout,
                        position: Int,
                        hotSearchBean: HotSearchBean?
                    ): View {
                        val tv = LayoutInflater.from(parent.context).inflate(
                            R.layout.flow_layout_tv,
                            hotSearchFlowLayout, false
                        ) as TextView

                        val name = hotSearchBean?.name
                        tv.setPadding(
                            ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f),
                            ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f)
                        )
                        tv.text = name
                        tv.setTextColor(CommonUtils.randomColor())
                        return tv
                    }
                })
        })
    }


}