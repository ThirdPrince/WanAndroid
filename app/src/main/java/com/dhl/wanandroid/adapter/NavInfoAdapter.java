package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.Article;
import com.dhl.wanandroid.model.NavBean;
import com.dhl.wanandroid.util.CommonUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

/**
 * 导航Adapter
 */
public class NavInfoAdapter extends CommonAdapter<NavBean> {

    public NavInfoAdapter(Context context, int layoutId, List<NavBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, NavBean navInfo, int position) {

        holder.setText(R.id.item_nav_tv, navInfo.getName());
        final TagFlowLayout mTagFlowLayout = holder.getView(R.id.item_nav_flow_layout);
        List<Article> mArticles = navInfo.getArticles();
        mTagFlowLayout.setAdapter(new TagAdapter<Article>(mArticles) {
            @Override
            public View getView(FlowLayout parent, int position, Article feedArticleData) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_layout_tv,
                        mTagFlowLayout, false);
                if (feedArticleData == null) {
                    return null;
                }
                String name = feedArticleData.getTitle();
                tv.setPadding(ConvertUtils.dp2px(10), ConvertUtils.dp2px(10),
                        ConvertUtils.dp2px(10), ConvertUtils.dp2px(10));
                tv.setText(name);
                tv.setTextColor(CommonUtils.randomColor());

                return tv;
            }
        });
        mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                return false;
            }
        });

    }
}
