package com.dhl.wanandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.adapter.NavInfoAdapter;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.ArticlesBean;
import com.dhl.wanandroid.model.NavInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class NavFragment extends BaseFragment {

    private static final String TAG = "NavFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<NavInfo> navInfoList ;

    private   NavInfoAdapter navInfoAdapter ;

    public NavFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavFragment newInstance(String param1, String param2) {
        NavFragment fragment = new NavFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        toolbar.setTitle("导航");
        initRcy(view);
        refreshLayout.autoRefresh();
        refreshLayout.setEnableLoadMore(false);
        setAdapter();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getNavData();
            }
        });

    }
    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void getNavData()
    {


        OkHttpManager.getInstance().get(Constants.NAV_URL, new Callback() {
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

                JsonElement  jsonElement = new JsonParser().parse(response.body().string());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();


                final List<NavInfo> navInfos = gson.fromJson(jsonArray.toString(),new TypeToken<List<NavInfo>>(){}.getType());
                LitePal.deleteAll(ArticlesBean.class);
                LitePal.deleteAll(NavInfo.class);
                for(NavInfo navInfo :navInfos)
                {
                    LitePal.saveAll(navInfo.getArticles());
                }
                LitePal.saveAll(navInfos);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        navInfoList.clear();
                       // navInfoList = navInfos ;
                        navInfoList.addAll(navInfos);
                        refreshLayout.finishRefresh();
                        navInfoAdapter.notifyDataSetChanged();
                    }
                });


            }
        });
    }



    private void setAdapter()
    {
        navInfoList = LitePal.findAll(NavInfo.class);
        for(NavInfo navInfo :navInfoList)
        {
            List<ArticlesBean> articlesBeanList = LitePal.where("chapterid =" +navInfo.getCourseId()).find(ArticlesBean.class);
            navInfo.setArticles(articlesBeanList);
        }
        navInfoAdapter = new NavInfoAdapter(getActivity(),R.layout.nav_info_item,navInfoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(navInfoAdapter);
    }

}
