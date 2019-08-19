package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.HomePageData;
import com.dhl.wanandroid.model.WxArticleBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class WxArticleAdapter extends CommonAdapter<WxArticleBean> {


    private Context mContext ;
    public WxArticleAdapter(Context context, int layoutId, List<WxArticleBean> datas) {
        super(context, layoutId, datas);
        this.mContext = context ;
    }

    @Override
    protected void convert(ViewHolder holder, WxArticleBean wxArticleBean, int position) {
        holder.setText(R.id.name,wxArticleBean.getAuthor());

        holder.setText(R.id.content,wxArticleBean.getTitle());
        if(!TextUtils.isEmpty(wxArticleBean.getEnvelopePic()))
        {
            ImageView imageView = holder.getView(R.id.image);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(wxArticleBean.getEnvelopePic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
    }
}
