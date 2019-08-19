package com.dhl.wanandroid.adapter;

import android.content.Context;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.KnowledgeInfo;
import com.dhl.wanandroid.model.KnowledgeInfochild;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class KnowledgeAdapter extends CommonAdapter<KnowledgeInfo> {


    public KnowledgeAdapter(Context context, int layoutId, List<KnowledgeInfo> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, KnowledgeInfo knowledgeInfo, int position) {
        holder.setText(R.id.name,knowledgeInfo.getName());
        StringBuilder sb = new StringBuilder();
        for(KnowledgeInfochild child :knowledgeInfo.getChildren())
        {
            sb.append(child.getName()+" ");
        }
        holder.setText(R.id.content,sb.toString());
    }
}
