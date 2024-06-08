package com.dhl.wanandroid.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity.Companion.startActivity
import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.NavBean
import com.dhl.wanandroid.util.CommonUtils
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout


class NavInfoListAdapter(
    private val context: Context,
    private val layoutId: Int, private val onItemClickListener: OnItemClickListener
) : ListAdapter<NavBean, NaviViewHolder>(NavDiffCallback()) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NaviViewHolder {
        val view = inflater.inflate(layoutId, parent, false)
        return NaviViewHolder(view,onItemClickListener)
    }

    override fun onBindViewHolder(holder: NaviViewHolder, position: Int) {
        val navInfo = getItem(position)
        holder.bind(navInfo)
    }

}

class NaviViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    fun bind(navInfo: NavBean) {
        val textView = itemView.findViewById<TextView>(R.id.item_nav_tv)
        textView.text = navInfo.name
        val mTagFlowLayout: TagFlowLayout = itemView.findViewById(R.id.item_nav_flow_layout)
        val mArticles: List<Article> = navInfo.articles
        mTagFlowLayout.adapter = object : TagAdapter<Article?>(mArticles) {
            override fun getView(
                parent: FlowLayout,
                position: Int,
                feedArticleData: Article?
            ): TextView? {
                val tv = LayoutInflater.from(parent.context).inflate(
                    R.layout.flow_layout_tv,
                    mTagFlowLayout, false
                ) as TextView
                if (feedArticleData == null) {
                    return null
                }
                val name = feedArticleData.title
                tv.setPadding(
                    ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f),
                    ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f)
                )
                tv.text = name
                tv.setTextColor(CommonUtils.randomColor())
                return tv
            }
        }
        mTagFlowLayout.setOnTagClickListener { _, position, _ ->
            val article = mArticles[position];
            onItemClickListener.onItemClick(article)
            false
        }

    }
}

class NavDiffCallback : DiffUtil.ItemCallback<NavBean>() {
    override fun areItemsTheSame(oldItem: NavBean, newItem: NavBean): Boolean {
        return oldItem.cid == newItem.cid
    }

    override fun areContentsTheSame(
        oldItem: NavBean,
        newItem: NavBean
    ): Boolean {
        return oldItem == newItem
    }
}


