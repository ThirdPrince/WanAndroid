package com.dhl.wanandroid.adapter

import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.KnowledgeTreeData

interface OnItemClickListener {
    fun onItemClick(article: Article)
}

interface OnKnowledgeClickListener {
    fun onItemClick(knowledgeTreeData: KnowledgeTreeData)
}
