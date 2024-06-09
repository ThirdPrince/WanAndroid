package com.dhl.wanandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.preferencesOf
import androidx.recyclerview.widget.RecyclerView
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity
import com.dhl.wanandroid.model.BannerBean
import com.dhl.wanandroid.module.GlideImageLoader
import com.youth.banner.Banner


class BannerAdapter(private val onBannerItemClickListener: OnBannerItemClickListener) : RecyclerView.Adapter<BannerViewHolder>() {

    private var bannerList: List<BannerBean> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_main_banner, parent, false)
        return BannerViewHolder(view,onBannerItemClickListener)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(bannerList)
    }

    override fun getItemCount(): Int = 1

    fun refreshAdapter( bannerList: List<BannerBean>){
        this.bannerList = bannerList
        notifyItemChanged(0)
    }
}

class BannerViewHolder(itemView: View, private val onBannerItemClickListener: OnBannerItemClickListener) : RecyclerView.ViewHolder(itemView) {
    fun bind(bannerList: List<BannerBean>) {
        val bannerView = itemView.findViewById<Banner>(R.id.banner)
        val imageUrlList: MutableList<String> = mutableListOf()
        for (banner in bannerList) {
            imageUrlList.add(banner.imagePath)
        }
        bannerView.setImages(imageUrlList).setImageLoader(GlideImageLoader()).start()
        bannerView.setOnBannerListener{pos->
            val bannerBean = bannerList[pos]
            onBannerItemClickListener.onItemClick(bannerBean)

        }

    }
}
