package com.dhl.wanandroid.adapter;

import android.content.Context;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.Knowledge;
import com.dhl.wanandroid.model.KnowledgeTreeData;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 知识体系adapter
 */
public class KnowledgeAdapter extends CommonAdapter<KnowledgeTreeData> {

    public KnowledgeAdapter(Context context, int layoutId, List<KnowledgeTreeData> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, KnowledgeTreeData knowledgeInfo, int position) {
        holder.setText(R.id.name, knowledgeInfo.getName());
        StringBuilder sb = new StringBuilder();
        for (Knowledge child : knowledgeInfo.getChildren()) {
            sb.append(child.getName() + " ");
        }
        holder.setText(R.id.content, sb.toString());
    }
}
