package com.dhl.wanandroid.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.activity.WebActivity;
import com.dhl.wanandroid.adapter.HomePageAdapter;
import com.dhl.wanandroid.adapter.OnCollectionListener;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.BannerInfo;
import com.dhl.wanandroid.model.HomePageData;
import com.dhl.wanandroid.module.GlideImageLoader;
import com.dhl.wanandroid.util.APIUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * 首页Fragment
 *
 */
public class MainFragment extends BaseFragment {
    private static final String TAG = "MainFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private ViewPager view_pager ;

    private Banner banner ;

    /**
     * banner List
     */
    private  List<BannerInfo> bannerInfoList;

    private List<String> imageUrlList =  new ArrayList<>();



    /**
     *
     */
    private static  int pageCount = 0 ;

    private HomePageAdapter homePageAdapter ;
    private  HeaderAndFooterWrapper headerAndFooterWrapper ;
    private List<HomePageData> homePageDataList ;


    private    LinearLayout mHeaderGroup ;
    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        bannerInfoList = LitePal.findAll(BannerInfo.class);
        homePageDataList.addAll(LitePal.findAll(HomePageData.class));
        if(bannerInfoList.size() >0)
        {
            setBanner();
        }
        setHomePageAdapter();
        getBanner();
        //setListOnClick();
        //getHomePage(pageCount);
    }

    private void initView(View view)
    {
        homePageDataList = new ArrayList<>();
        initToolbar(view);
        initRcy(view);
        mHeaderGroup = ((LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_banner, null));
        banner  = mHeaderGroup.findViewById(R.id.banner);
        toolbar.setTitle("首页");
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageCount = 0;
                getHomePage(pageCount,true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                pageCount++;
                getHomePage(pageCount,false);
            }
        });


    }
    private void setBanner()
    {
        imageUrlList.clear();
        for(BannerInfo banner : bannerInfoList)
        {
            imageUrlList.add(banner.getImagePath());
        }
        banner.setImages(imageUrlList).setImageLoader(new GlideImageLoader()).start();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

                BannerInfo bannerInfo = bannerInfoList.get(position);
                WebActivity.startActivity(getActivity(), bannerInfo.getTitle(),bannerInfo.getUrl());

            }
        });


    }

    private void setHomePageAdapter()
    {
        homePageAdapter = new HomePageAdapter(getActivity(),R.layout.fragment_homepage_item,homePageDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        headerAndFooterWrapper = new HeaderAndFooterWrapper(homePageAdapter);
        headerAndFooterWrapper.addHeaderView(mHeaderGroup);
        recyclerView.setAdapter(headerAndFooterWrapper);
        setListOnClick();
    }

    private void getBanner()
    {
        OkHttpManager.getInstance().get(Constants.BANNER_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e(TAG,"IOException=="+e.getMessage());
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
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                bannerInfoList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<BannerInfo>>(){}.getType());
                LitePal.deleteAll(BannerInfo.class);
                LitePal.saveAll(bannerInfoList);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBanner();
                    }
                });
            }
        });
    }

    /**
     * 下拉 上滑共用一个方法
     * @param page
     * @param onRefresh
     */
    private void getHomePage(int page,final boolean onRefresh)
    {
        if(onRefresh) {
            page = 0 ;
        }
        OkHttpManager.getInstance().getAddCookie(APIUtil.getHomePageUrl(page),true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e(TAG,"IOException=="+e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String rsp = response.body().string();
                 Log.e(TAG,"response="+rsp);
                JsonElement jsonElement = new JsonParser().parse(rsp);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("datas");
                ArrayList<HomePageData> pageDataList =  new Gson().fromJson(jsonArray.toString(),new TypeToken<List<HomePageData>>(){}.getType());
               /* HomePageData homePageData = new HomePageData();
                homePageData.setAuthor("charlie");
                homePageData.setTitle("test");
                pageDataList.add(0,homePageData);*/
                if(onRefresh)
                {
                    homePageDataList.clear();
                }
                homePageDataList.addAll(pageDataList);
                if(onRefresh) // 只保存 前面20条数据
                {
                    LitePal.deleteAll(HomePageData.class);
                    LitePal.saveAll(homePageDataList);
                }


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //homePageAdapter.notifyDataSetChanged();
                        headerAndFooterWrapper.notifyDataSetChanged(); //一定要headerAndFooterWrapper 刷新
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                        setListOnClick();
                    }
                });

            }
        });
    }

    private void setListOnClick()
    {
        if(homePageAdapter != null) {
            homePageAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    HomePageData homePageData = homePageDataList.get(position-1);
                    WebActivity.startActivity(getActivity(), homePageData.getTitle(),homePageData.getLink());

                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
            homePageAdapter.setOnCollectionListener(new OnCollectionListener() {
                public void onCollectionClick(final View view, int position) {
                    HomePageData homePageData = homePageDataList.get(position-1);
                    OkHttpManager.getInstance().postCollectionOut(APIUtil.collectionArticleOut(),homePageData.getTitle(),homePageData.getAuthor(),homePageData.getLink(),  new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            String rsp = response.body().string();
                            Log.e(TAG,"rsp="+rsp);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.setSelected(true);
                                }
                            });
                        }
                    });
                }
            });

        }
    }
}
