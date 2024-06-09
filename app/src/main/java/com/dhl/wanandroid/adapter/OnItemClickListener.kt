package com.dhl.wanandroid.adapter

import com.dhl.wanandroid.model.Article
import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.model.KnowledgeTreeData

/**
 * some onClick event
 */
interface OnItemClickListener {
    fun onItemClick(article: Article)
}

interface OnKnowledgeClickListener {
    fun onItemClick(knowledgeTreeData: KnowledgeTreeData)
}

interface OnBannerItemClickListener {
    fun onItemClick(bannerBean: BannerBean)
}