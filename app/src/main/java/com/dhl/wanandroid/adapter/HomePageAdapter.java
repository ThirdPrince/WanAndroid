package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.app.LoginInfo;
import com.dhl.wanandroid.model.HomePageData;
import com.dhl.wanandroid.model.LoginBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class HomePageAdapter extends CommonAdapter<HomePageData> {


    public void setOnCollectionListener(OnCollectionListener onCollectionListener) {
        this.onCollectionListener = onCollectionListener;
    }

    private OnCollectionListener onCollectionListener ;
   /* public interface OnCollectionListener
    {
        void onCollectionClick(View view ,int position);
    }*/
    public HomePageAdapter(Context context, int layoutId, List<HomePageData> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, HomePageData homePageData, final int position) {
        holder.setText(R.id.name,homePageData.getAuthor());
        holder.setText(R.id.desc,homePageData.getSuperChapterName()+" / "+homePageData.getChapterName());
        TextView title = (TextView) holder.getView(R.id.content);
        title.setText(Html.fromHtml(homePageData.getTitle()));
        final ImageView collection = holder.getView(R.id.collection);
       /* LoginBean loginBean = LoginInfo.getInstance().getLoginBean();
        if(loginBean != null)
        {
            if(loginBean.getCollectIds().contains(homePageData.getCourseId()))
            {
                collection.setSelected(true);
            }else
            {
                collection.setSelected(false);
            }
        }*/
       if(homePageData.isCollect())
        {
            collection.setSelected(true);
        }else
        {
            collection.setSelected(false);
        }
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCollectionListener != null)
                {
                    onCollectionListener.onCollectionClick(collection,position);
                }
            }
        });
       // holder.setText(R.id.content,Html.fromHtml(homePageData.getTitle()));
    }
}
