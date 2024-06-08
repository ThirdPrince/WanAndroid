package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.adapter.WxArticlePgAdapter
import com.dhl.wanandroid.model.HotSearchBean
import com.dhl.wanandroid.util.CommonUtils
import com.dhl.wanandroid.vm.HotSearchViewModel
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs


/**
 * HotSearchFragment
 * @author dhl
 * @date 2023 2.18
 */

private const val SEARCH_WHAT = 1024

class HotSearchFragment : BaseFragment() {



    private lateinit var searchView: SearchView

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
        requireView().findViewById(R.id.hot_search_flow_layout)
    }

    /**
     * 搜索结果
     */
    private val searchResultRcy: RecyclerView by lazy {
        requireView().findViewById(R.id.search_result_rcy)

    }

    private val searchPgAdapter: WxArticlePgAdapter by lazy {
        WxArticlePgAdapter(requireContext(), this)
    }

    private val searchScrollView: NestedScrollView by lazy {
        requireView().findViewById(R.id.search_scroll_view)

    }


    private val handlerUI: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SEARCH_WHAT -> {
                    val key = msg.obj.toString()
                    getSearchKey(key)

                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initView()
        initData()
        getHotData()
    }

    private fun initView() {
        searchResultRcy.run {
            adapter = searchPgAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (abs(dy) >= 15) {
                        if (KeyboardUtils.isSoftInputVisible(activity!!)) {
                            KeyboardUtils.hideSoftInput(searchResultRcy)
                        }

                    }

                }
            })
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.run {
            maxWidth = Integer.MAX_VALUE
            onActionViewExpanded()
            queryHint = "输入关键字搜索"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                //searchview的监听
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //当点击搜索时 输入法的搜索，和右边的搜索都会触发
                    query?.let { queryStr ->


                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    handlerUI.removeMessages(SEARCH_WHAT)
                    if (TextUtils.isEmpty(newText)) {
                        searchResultRcy.visibility = View.GONE
                        searchScrollView.visibility = View.VISIBLE
                    } else {
                        val message = Message.obtain()
                        message.obj = newText
                        message.what = SEARCH_WHAT
                        handlerUI.sendMessageDelayed(message, 400)
                    }

                    return false
                }
            })
            isSubmitButtonEnabled = false //右边是否展示搜索图标
            val field = javaClass.getDeclaredField("mGoButton")
            field.run {
                isAccessible = true
                val mGoButton = get(searchView) as ImageView
                mGoButton.setImageResource(R.drawable.ic_search_white_24dp)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
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
    private fun getHotData() {
        searchViewModel.getSearchHot().observe(viewLifecycleOwner) {
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

            hotSearchFlowLayout.setOnTagClickListener { view, position, parent ->
                val hotBean = it.result!![position]
                getSearchKey(hotBean.name)
                searchView.setQuery(hotBean.name, false)
                false
            }

        }

        searchViewModel.errorResponse.observe(viewLifecycleOwner) {
            ToastUtils.showLong(it.errorMessage)
        }
    }

    /**
     *
     */
    private fun getSearchKey(key: String) {
        if (!TextUtils.isEmpty(key)) {
            lifecycleScope.launch {
                searchViewModel.getSearchRes(key).collect {
                    searchPgAdapter.submitData(it)
                    searchResultRcy.visibility = View.VISIBLE
                    searchScrollView.visibility = View.GONE
                }
            }

        } else {
            searchResultRcy.visibility = View.GONE
            searchScrollView.visibility = View.VISIBLE
        }

    }


}