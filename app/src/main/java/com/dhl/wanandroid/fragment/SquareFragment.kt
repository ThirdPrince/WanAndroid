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
import com.dhl.wanandroid.vm.SquareViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * 广场Fragment
 * @author dhl
 * @date 2023 04 01
 * @version V2.0
 */
class SquareFragment : BaseFragment() {

    /**
     * viewModel
     */
    private val squareViewModel: SquareViewModel by lazy {
        ViewModelProvider(this).get(SquareViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_square, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRcy()
        recyclerView.adapter = basePgAdapter
        getData()
        refreshLayout.setOnRefreshListener {
            getData()
        }
    }

    /**
     * 获取数据
     */
    private fun getData() {
        lifecycleScope.launch {
            squareViewModel.getSquareList().collect {
                basePgAdapter.submitData(it)
            }

        }

    }


    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): SquareFragment {
            val fragment = SquareFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}