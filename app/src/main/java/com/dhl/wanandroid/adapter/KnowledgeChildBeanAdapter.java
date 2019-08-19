package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.KnowledgeInfo;
import com.dhl.wanandroid.model.KnowledgeInfochild;
import com.dhl.wanandroid.model.KnowledgeInfochildBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;


/**
 * 知识体系子目录 adapter
 */
public class KnowledgeChildBeanAdapter extends CommonAdapter<KnowledgeInfochildBean> {


    public KnowledgeChildBeanAdapter(Context context, int layoutId, List<KnowledgeInfochildBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, KnowledgeInfochildBean knowledgeInfochildBean, int position) {
        holder.setText(R.id.name,knowledgeInfochildBean.getAuthor());

        TextView textView = holder.getView(R.id.content);
        textView.setText(Html.fromHtml(knowledgeInfochildBean.getTitle()));
    }
}
