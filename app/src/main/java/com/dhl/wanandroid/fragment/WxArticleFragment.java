package com.dhl.wanandroid.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.adapter.KnowledgeAdapter;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.KnowledgeInfo;
import com.dhl.wanandroid.model.KnowledgeInfochild;
import com.dhl.wanandroid.model.WxArticleInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WxArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WxArticleFragment extends BaseFragment {
    private static final String TAG = "WxArticleFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<WxArticleInfo> wxArticleInfoList ;

    private TextView toolbar_title ;

    /**
     * tabLayout
     */
    private TabLayout tabLayout ;


    /**
     * viewPager
     */
    private ViewPager viewPager ;

    private List<WxArticleTabFragment> wxArticleTabFragmentList ;

    private List<String> tabIndicator ;

    public WxArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WxArticleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WxArticleFragment newInstance(String param1, String param2) {
        WxArticleFragment fragment = new WxArticleFragment();
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
        return inflater.inflate(R.layout.fragment_wx_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        initView(view);
        toolbar.setTitle("公众号");
        //toolbar_title.setText("公众号");
        getWxArticleInfo();
    }
    private void initView(View view)
    {
       // toolbar_title = view.findViewById(R.id.toolbar_title);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.content_vp);
    }


    private void getWxArticleInfo()
    {
        wxArticleTabFragmentList = new ArrayList<>();
        tabIndicator = new ArrayList<>();
        wxArticleInfoList = LitePal.findAll(WxArticleInfo.class);
        if(wxArticleInfoList.size() > 0) {
            setViewPageTab();
        }

        OkHttpManager.getInstance().get(Constants.WX_ARTICLE_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                JsonElement jsonElement = new JsonParser().parse(response.body().string());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                if(wxArticleInfoList.size()  == 0)
                {
                    wxArticleInfoList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<WxArticleInfo>>(){}.getType());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setViewPageTab();
                        }
                    });

                }else
                {
                    wxArticleInfoList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<WxArticleInfo>>(){}.getType());
                    // Log.e(TAG,"list=="+knowledgeInfo.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //tabLayout.removeAllTabs();
                            //refreshLayout.finishRefresh();
                            wxArticleTabFragmentList.clear();
                            for(WxArticleInfo wxArticleInfo :wxArticleInfoList) {
                                // tabLayout.addTab(tabLayout.newTab().setText(wxArticleInfo.getName()));
                                wxArticleTabFragmentList.add(WxArticleTabFragment.newInstance(wxArticleInfo.getName(),wxArticleInfo.getId()+""));
                            }

                        }
                    });
                }
                LitePal.deleteAll(WxArticleInfo.class);
                LitePal.saveAll(wxArticleInfoList);

            }
        });


    }

    private void setViewPageTab()
    {
        wxArticleTabFragmentList.clear();
        for (WxArticleInfo wxArticleInfo : wxArticleInfoList) {
            tabIndicator.add(wxArticleInfo.getName());
            wxArticleTabFragmentList.add(WxArticleTabFragment.newInstance(wxArticleInfo.getName(), wxArticleInfo.getId()+""));
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return wxArticleTabFragmentList.get(i);
            }

            @Override
            public int getCount() {
                return wxArticleTabFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {

                return tabIndicator.get(position);
            }
        });
        viewPager.setOffscreenPageLimit(wxArticleTabFragmentList.size());
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }
}
