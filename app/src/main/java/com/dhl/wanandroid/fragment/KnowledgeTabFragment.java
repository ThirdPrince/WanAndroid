package com.dhl.wanandroid.fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.activity.WebActivity;
import com.dhl.wanandroid.adapter.KnowledgeChildBeanAdapter;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.KnowledgeInfochildBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 @author  dhl
 知识体系下的文章
 */
public class KnowledgeTabFragment extends BaseFragment {

    private static final String TAG = "KnowledgeTabFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TITLE = "title";
    private static final String ID = "id";

    // TODO: Rename and change types of parameters
    private String title;
    private String articleId;

    private KnowledgeChildBeanAdapter knowledgeChildBeanAdapter ;

    private List<KnowledgeInfochildBean> knowledgeInfochildBeanList ;

    private boolean isViewCreate = false ;

    private boolean isDataInited = false ;

    public KnowledgeTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WxArticleTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KnowledgeTabFragment newInstance(String param1, String param2) {
        KnowledgeTabFragment fragment = new KnowledgeTabFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, param1);
        args.putString(ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            articleId = getArguments().getString(ID);
            //Log.e(TAG,"articleId =="+articleId);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wx_article_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Log.e(TAG,"onViewCreated");
        initRcy(view);
        //ViewCompat.setNestedScrollingEnabled(recyclerView, true);
        isViewCreate = true ;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.e(TAG,"onActivityCreated");
        if(isViewCreate && getUserVisibleHint() && !isDataInited)
        {
            onLoadData();
        }
    }

    /**
     * 加载数据
     */
    private void onLoadData()
   {
       knowledgeInfochildBeanList = new ArrayList<>();
       knowledgeChildBeanAdapter = new KnowledgeChildBeanAdapter(getActivity(),R.layout.fragment_homepage_item,knowledgeInfochildBeanList);
       recyclerView.setAdapter(knowledgeChildBeanAdapter);
       refreshLayout.autoRefresh();
       refreshLayout.setOnRefreshListener(new OnRefreshListener() {
           @Override
           public void onRefresh(@NonNull RefreshLayout refreshLayout) {
               getWxArticle(Integer.parseInt(articleId),1);
           }
       });
       isDataInited = true ;

   }
    private void getWxArticle(int id, final int page)
    {
        Log.e(TAG,"url=="+Constants.KNOWLEDGE_URL_LIST +id);

        OkHttpManager.getInstance().get(Constants.KNOWLEDGE_URL_LIST +id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                JsonElement jsonElement = new JsonParser().parse(response.body().string());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("datas");
                final List<KnowledgeInfochildBean> wxArticleBeans =  new Gson().fromJson(jsonArray.toString(),new TypeToken<List<KnowledgeInfochildBean>>(){}.getType());

                knowledgeInfochildBeanList.addAll(wxArticleBeans);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        knowledgeChildBeanAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefresh();
                        knowledgeChildBeanAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                                KnowledgeInfochildBean knowledgeInfochildBean = knowledgeInfochildBeanList.get(position);
                                WebActivity.startActivity(getActivity(), knowledgeInfochildBean.getTitle(),knowledgeInfochildBean.getLink());
                            }

                            @Override
                            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                                return false;
                            }
                        });

                    }
                });




            }

        });
    }

    public String getTitle()
    {
        return title;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
       // Log.e(TAG,"setUserVisibleHint :::this is "+title+"::::isVisible=="+isVisibleToUser);
        if(isVisibleToUser && isViewCreate && !isDataInited)
        {
            onLoadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e(TAG,"onDestroy");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       //Log.e(TAG,"onDestroyView");
    }
}
