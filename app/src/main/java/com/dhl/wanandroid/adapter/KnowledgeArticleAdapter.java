package com.dhl.wanandroid.adapter;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.model.Article;
import com.dhl.wanandroid.model.Knowledge;
import com.dhl.wanandroid.model.KnowledgeInfo;
import com.dhl.wanandroid.model.KnowledgeInfochild;
import com.dhl.wanandroid.model.KnowledgeInfochildBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;


/**
 * 知识体系子目录 adapter
 */
public class KnowledgeArticleAdapter extends CommonAdapter<Article> {


    public KnowledgeArticleAdapter(Context context, int layoutId, List<Article> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, Article article, int position) {
        holder.setText(R.id.name, article.getAuthor());
        TextView textView = holder.getView(R.id.content);
        textView.setText(Html.fromHtml(article.getTitle()));
    }
}
