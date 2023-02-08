package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.Article;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class WxArticleAdapter extends CommonAdapter<Article> {


    private Context mContext;

    public WxArticleAdapter(Context context, int layoutId, List<Article> datas) {
        super(context, layoutId, datas);
        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void convert(ViewHolder holder, Article article, int position) {
        holder.setText(R.id.name, article.getAuthor());
        TextView title = holder.getView(R.id.content);
        title.setText(Html.fromHtml(article.getTitle(),0));
        if (!TextUtils.isEmpty(article.getEnvelopePic())) {
            ImageView imageView = holder.getView(R.id.image);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(article.getEnvelopePic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
    }
}
