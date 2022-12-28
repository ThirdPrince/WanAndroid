package com.dhl.wanandroid.fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.activity.WebActivity;
import com.dhl.wanandroid.adapter.WxArticleAdapter;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.WxArticleBean;
import com.dhl.wanandroid.util.APIUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WxArticleTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WxArticleTabFragment extends BaseFragment {

    private static final String TAG = "WxArticleTabFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String title;
    private String articleId;

    private WxArticleAdapter wxArticleAdapter;

    private List<WxArticleBean> wxArticleBeanList;

    private boolean isViewCreate = false;

    private boolean isDataInited = false;

    public WxArticleTabFragment() {
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
    public static WxArticleTabFragment newInstance(String param1, String param2) {
        WxArticleTabFragment fragment = new WxArticleTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            articleId = getArguments().getString(ARG_PARAM2);
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

        Log.e(TAG, "onViewCreated");
        initRcy(view);
        //ViewCompat.setNestedScrollingEnabled(recyclerView, true);
        isViewCreate = true;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated ::isViewCreate ==" + isViewCreate + "::getUserVisibleHint()==" + getUserVisibleHint());
        if (isViewCreate && getUserVisibleHint()) {
            onLoadData();
        }
    }

    /**
     * 加载数据
     */
    private void onLoadData() {
        wxArticleBeanList = LitePal.where("chapterId =" + Integer.parseInt(articleId)).find(WxArticleBean.class);
        wxArticleAdapter = new WxArticleAdapter(getActivity(), R.layout.fragment_homepage_item, wxArticleBeanList);
        recyclerView.setAdapter(wxArticleAdapter);
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getWxArticle(Integer.parseInt(articleId), 1);
            }
        });
        isDataInited = true;

    }

    private void getWxArticle(int id, int page) {

        OkHttpManager.getInstance().get(APIUtil.getWxArticle(id, page), new Callback() {
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
                final List<WxArticleBean> wxArticleBeans = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<WxArticleBean>>() {
                }.getType());
                LitePal.deleteAll(WxArticleBean.class, "chapterId =" + Integer.parseInt(articleId));
                LitePal.saveAll(wxArticleBeans);
                wxArticleBeanList.clear();
                wxArticleBeanList.addAll(wxArticleBeans);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wxArticleAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefresh();
                        wxArticleAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                                WxArticleBean wxArticleBean = wxArticleBeanList.get(position);
                                WebActivity.startActivity(getActivity(), wxArticleBean.getTitle(), wxArticleBean.getLink());
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

    public String getTitle() {
        return title;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreate) {
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
