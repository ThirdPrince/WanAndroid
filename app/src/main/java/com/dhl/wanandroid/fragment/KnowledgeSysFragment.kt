package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.KnowledgeInfoActivity
import com.dhl.wanandroid.adapter.KnowledgeListAdapter
import com.dhl.wanandroid.adapter.OnKnowledgeClickListener
import com.dhl.wanandroid.model.KnowledgeTreeData
import com.dhl.wanandroid.vm.KnowledgeSysViewModel


/**
 * 知识体系Fragment
 * @author dhl
 * @date 2022 12 24
 * @version V2.0
 */
class KnowledgeSysFragment : BaseFragment(), OnKnowledgeClickListener {

    /**
     * adapter
     */
    private val knowledgeAdapter: KnowledgeListAdapter by lazy {
        KnowledgeListAdapter(requireActivity(), R.layout.fragment_knowledge_item, this)
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
        initToolbar()
        toolbar.title = getString(R.string.title_knowledge)
        initRcy()
        recyclerView.adapter = knowledgeAdapter
        refreshLayout.setOnRefreshListener {
            getData()
        }
    }

    /**
     * 获取数据
     */
    private fun getData() {
        knowledgeSysViewModel.getKnowledgeTree().observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                knowledgeAdapter.submitList(it.result)
            } else {
                ToastUtils.showLong(it.errorMessage)
            }
        }
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

    override fun onItemClick(knowledgeTreeData: KnowledgeTreeData) {
        KnowledgeInfoActivity.startActivity(requireActivity(), knowledgeTreeData)
    }
}