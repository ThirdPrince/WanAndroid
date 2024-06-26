package com.dhl.wanandroid.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dhl.wanandroid.R
import com.dhl.wanandroid.drawable.TextDrawable
import com.dhl.wanandroid.drawable.util.ColorGenerator
import com.dhl.wanandroid.model.Article


class TopArticleListAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<Article, TopArticleListAdapter.TopArticleViewHolder>(TopArticleDiffCallback()) {

    private val colorGenerator: ColorGenerator = ColorGenerator.MATERIAL
    private val drawableBuilder: TextDrawable.IBuilder = TextDrawable.builder().round()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopArticleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_homepage_item, parent, false)
        return TopArticleViewHolder(view,onItemClickListener)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: TopArticleViewHolder, position: Int) {
        val navInfo = getItem(position)
        holder.bind(navInfo)
    }

    inner class TopArticleViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

        private val headerImg: ImageView = itemView.findViewById(R.id.header_image)
        private val nameTv: TextView = itemView.findViewById(R.id.name)
        private val titleTv: TextView = itemView.findViewById(R.id.content)
        private val timeTv: TextView = itemView.findViewById(R.id.time_tv)
        private val topTv: TextView = itemView.findViewById(R.id.top_tv)
        private val descTv: TextView = itemView.findViewById(R.id.desc)

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(article: Article) {
            nameTv.text = article.author
            titleTv.text = Html.fromHtml(article.title, Html.FROM_HTML_MODE_LEGACY)
            timeTv.text = article.niceDate

            if (!TextUtils.isEmpty(article.envelopePic)) {
                val imageView: ImageView = itemView.findViewById(R.id.image)
                imageView.visibility = View.VISIBLE
                Glide.with(context).load(article.envelopePic)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
            }

            val drawable = if (article.author.isNotEmpty()) {
                drawableBuilder.build(
                    article.author.first().toString(),
                    colorGenerator.getColor(article.author)
                )
            } else if (article.shareUser.isNotEmpty()) {
                drawableBuilder.build(
                    article.shareUser.first().toString(),
                    colorGenerator.getColor(article.shareUser)
                )
            } else {
                drawableBuilder.build("佚", colorGenerator.getColor("佚"))
            }
            headerImg.setImageDrawable(drawable)

            if (!TextUtils.isEmpty(article.author)) {
                nameTv.text = article.author
            } else if (!TextUtils.isEmpty(article.shareUser)) {
                nameTv.text = article.shareUser
            } else {
                nameTv.text = "佚名"
            }
            if (article.isTop) {
                topTv.visibility = View.VISIBLE
            } else {
                topTv.visibility = View.GONE
            }

            descTv.text = "${article.superChapterName} / ${article.chapterName}"

            itemView.setOnClickListener {
                onItemClickListener.onItemClick(article)

            }

        }
    }
}



class TopArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Article,
        newItem: Article
    ): Boolean {
        return oldItem == newItem
    }
}


