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
import com.dhl.wanandroid.drawable.TextDrawable;
import com.dhl.wanandroid.drawable.util.ColorGenerator;
import com.dhl.wanandroid.model.Article;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class WxArticleAdapter extends CommonAdapter<Article> {

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    private Context mContext;

    public WxArticleAdapter(Context context, int layoutId, List<Article> datas) {
        super(context, layoutId, datas);
        this.mContext = context;
        mDrawableBuilder = TextDrawable.builder()
                .round();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void convert(ViewHolder holder, Article article, int position) {
        ImageView headerImg = (ImageView) holder.getView(R.id.header_image);
        TextView nameTv = holder.getView(R.id.name);
        holder.setText(R.id.name, article.getAuthor());
        TextView title = holder.getView(R.id.content);
        holder.setText(R.id.time_tv, article.getNiceDate());
        title.setText(Html.fromHtml(article.getTitle(), 0));
        if (!TextUtils.isEmpty(article.getEnvelopePic())) {
            ImageView imageView = holder.getView(R.id.image);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(article.getEnvelopePic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
        if (article.getAuthor().length() > 0) {
            TextDrawable drawable = mDrawableBuilder.build(String.valueOf(article.getAuthor().charAt(0)), mColorGenerator.getColor(article.getAuthor()));
            headerImg.setImageDrawable(drawable);
        } else {
            TextDrawable drawable = mDrawableBuilder.build("佚", mColorGenerator.getColor("佚"));
            headerImg.setImageDrawable(drawable);
        }

        if (TextUtils.isEmpty(article.getAuthor())) {
            nameTv.setText("佚名");
        } else {
            nameTv.setText(article.getAuthor());
        }
        TextView topTv = holder.getView(R.id.top_tv);
        if (article.isTop()) {
            topTv.setVisibility(View.VISIBLE);
        } else {
            topTv.setVisibility(View.GONE);
        }
        holder.setText(R.id.desc, article.getSuperChapterName() + " / " + article.getChapterName());
    }
}
