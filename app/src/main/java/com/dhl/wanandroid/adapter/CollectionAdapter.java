package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.CollectionBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;


/**
 * @author dhl
 * 我的收藏
 */
public class CollectionAdapter extends CommonAdapter<CollectionBean> {


    private OnCollectionListener onCollectionListener;

    public void setOnCollectionListener(OnCollectionListener onCollectionListener) {
        this.onCollectionListener = onCollectionListener;
    }

    private Context mContext;

    public CollectionAdapter(Context context, int layoutId, List<CollectionBean> datas) {
        super(context, layoutId, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(ViewHolder holder, CollectionBean collectionBean, final int position) {
        holder.setText(R.id.name, collectionBean.getAuthor());

        holder.setText(R.id.content, collectionBean.getTitle());
        if (!TextUtils.isEmpty(collectionBean.getEnvelopePic())) {
            ImageView imageView = holder.getView(R.id.image);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(collectionBean.getEnvelopePic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }

//        ImageView collectionImg = holder.getView(R.id.collection);
//        collectionImg.setSelected(true);
//        collectionImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(onCollectionListener != null)
//                {
//                    onCollectionListener.onCollectionClick(view,position);
//                }
//            }
//        });


    }
}
