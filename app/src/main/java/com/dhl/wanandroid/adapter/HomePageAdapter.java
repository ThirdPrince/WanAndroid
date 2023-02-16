package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.drawable.TextDrawable;
import com.dhl.wanandroid.drawable.util.ColorGenerator;
import com.dhl.wanandroid.model.Article;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 首页的文章
 */
public class HomePageAdapter extends CommonAdapter<Article> {

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    public void setOnCollectionListener(OnCollectionListener onCollectionListener) {
        this.onCollectionListener = onCollectionListener;
    }

    private OnCollectionListener onCollectionListener;


    public HomePageAdapter(Context context, int layoutId, List<Article> datas) {
        super(context, layoutId, datas);
        mDrawableBuilder = TextDrawable.builder()
                .round();
    }

    @Override
    protected void convert(ViewHolder holder, Article article, final int position) {
        ImageView headerImg = (ImageView) holder.getView(R.id.header_image);
        TextView nameTv = holder.getView(R.id.name);
        holder.setText(R.id.desc, article.getSuperChapterName() + " / " + article.getChapterName());
        TextView title = (TextView) holder.getView(R.id.content);
        title.setText(Html.fromHtml(article.getTitle()));
//        final ImageView collection = holder.getView(R.id.collection);
//        collection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onCollectionListener != null) {
//                    onCollectionListener.onCollectionClick(collection, position);
//                }
//            }
//        });
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
        if(article.isTop()){
            topTv.setVisibility(View.VISIBLE);
        }else {
            topTv.setVisibility(View.INVISIBLE);
        }



    }
}
