package com.dhl.wanandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhl.wanandroid.R
import com.dhl.wanandroid.model.KnowledgeTreeData

class KnowledgeListAdapter(
    context: Context,
    private val layoutId: Int,
    private val knowledgeClickListener: OnKnowledgeClickListener
) : ListAdapter<KnowledgeTreeData, ViewHolder>(KnowledgeDiffCallback()) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val knowledgeInfo = getItem(position)
        holder.setText(R.id.name, knowledgeInfo.name)
        val sb = StringBuilder()
        for (child in knowledgeInfo.children) {
            sb.append(child.name).append(" ")
        }
        holder.setText(R.id.content, sb.toString())
        holder.itemView.setOnClickListener {
            knowledgeClickListener.onItemClick(knowledgeInfo)
        }
    }
}

class KnowledgeDiffCallback : DiffUtil.ItemCallback<KnowledgeTreeData>() {
    override fun areItemsTheSame(oldItem: KnowledgeTreeData, newItem: KnowledgeTreeData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: KnowledgeTreeData,
        newItem: KnowledgeTreeData
    ): Boolean {
        return oldItem == newItem
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setText(viewId: Int, text: String) {
        val textView: TextView = itemView.findViewById(viewId)
        textView.text = text
    }
}

