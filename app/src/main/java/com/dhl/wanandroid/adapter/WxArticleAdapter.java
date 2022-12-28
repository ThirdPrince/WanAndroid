package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.Article;
import com.dhl.wanandroid.model.ArticleData;
import com.dhl.wanandroid.model.ArticlesBean;
import com.dhl.wanandroid.model.HomePageData;
import com.dhl.wanandroid.model.WxArticleBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class WxArticleAdapter extends CommonAdapter<Article> {


    private Context mContext;

    public WxArticleAdapter(Context context, int layoutId, List<Article> datas) {
        super(context, layoutId, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(ViewHolder holder, Article article, int position) {
        holder.setText(R.id.name, article.getAuthor());
        holder.setText(R.id.content, article.getTitle());
        if (!TextUtils.isEmpty(article.getEnvelopePic())) {
            ImageView imageView = holder.getView(R.id.image);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(article.getEnvelopePic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
    }
}
