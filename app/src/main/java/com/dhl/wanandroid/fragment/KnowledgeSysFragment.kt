package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.KnowledgeInfoActivity
import com.dhl.wanandroid.adapter.KnowledgeAdapter
import com.dhl.wanandroid.model.KnowledgeTreeData
import com.dhl.wanandroid.vm.KnowledgeSysViewModel
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter



/**
 * 知识体系Fragment
 * @author dhl
 * @date 2022 12 24
 * @version V2.0
 */
class KnowledgeSysFragment : BaseFragment() {
    /**
     *
     */
    private var knowledgeTreeDataList: MutableList<KnowledgeTreeData> = mutableListOf()

    /**
     * adapter
     */
    private val knowledgeAdapter: KnowledgeAdapter by lazy {
        KnowledgeAdapter(activity, R.layout.fragment_knowledge_item, knowledgeTreeDataList)
    }

    /**
     * viewModel
     */
    private val knowledgeSysViewModel: KnowledgeSysViewModel by lazy {
        ViewModelProvider(this).get(KnowledgeSysViewModel::class.java)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_knowledge_sys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar(view)
        toolbar.title = "知识体系"
        initRcy(view)
        recyclerView.adapter = knowledgeAdapter
        refreshLayout.autoRefresh()
        refreshLayout.setEnableLoadMore(false)
        refreshLayout.setOnRefreshListener {
            getData()
        }
    }

    /**
     * 获取数据
     */
    private fun getData() {
        knowledgeSysViewModel.getKnowledgeTree().observe(this, {
            knowledgeTreeDataList.clear()
            if (it.isSuccess) {
                knowledgeTreeDataList.addAll(it.result!!)
                knowledgeAdapter.notifyDataSetChanged()
                refreshLayout.finishRefresh()
                setOnClick()
            } else {
                ToastUtils.showLong(it.errorMessage)
            }
        })
    }


    /**
     * adapter onClick
     */
    private fun setOnClick() {
            knowledgeAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
                override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                    val knowledgeTreeData = knowledgeTreeDataList[position]
                    KnowledgeInfoActivity.startActivity(activity, knowledgeTreeData)
                }

                override fun onItemLongClick(view: View, holder: RecyclerView.ViewHolder, position: Int): Boolean {
                    return false
                }
            })

    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): KnowledgeSysFragment {
            val fragment = KnowledgeSysFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}