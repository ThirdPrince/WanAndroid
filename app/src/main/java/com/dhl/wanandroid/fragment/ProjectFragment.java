package com.dhl.wanandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.adapter.TabFragmentAdapter;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.ProjectBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class ProjectFragment extends BaseFragment {

    private static final String TAG = "ProjectFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<ProjectBean> projectBeanList ;


    /**
     * tabLayout
     */
    private TabLayout tabLayout ;


    /**
     * viewPager
     */
    private ViewPager viewPager ;

    private List<WxArticleTabFragment> wxArticleTabFragments ;

    private List<String> tabIndicator ;

    private TabFragmentAdapter tabFragmentAdapter ;

    public ProjectFragment() {
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
    public static ProjectFragment newInstance(String param1, String param2) {
        ProjectFragment fragment = new ProjectFragment();
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
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        initView(view);
        //toolbar.setTitle("项目");
        getProjectData();


    }
    // TODO: Rename method, update argument and hook method into UI event



    private void initView(View view)
    {
        // toolbar_title = view.findViewById(R.id.toolbar_title);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.content_vp);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void getProjectData()
    {

        tabIndicator = new ArrayList<>();
        wxArticleTabFragments = new ArrayList<>();
        projectBeanList  = LitePal.findAll(ProjectBean.class);
        for (ProjectBean projectBean : projectBeanList) {
            tabIndicator.add(projectBean.getName());
            wxArticleTabFragments.add(WxArticleTabFragment.newInstance(projectBean.getName(), projectBean.getCid()+""));
        }
        tabFragmentAdapter = new TabFragmentAdapter(getChildFragmentManager(),wxArticleTabFragments,tabIndicator);
        viewPager.setAdapter(tabFragmentAdapter);
       // viewPager.setOffscreenPageLimit(wxArticleTabFragments.size());
        tabLayout.setupWithViewPager(viewPager);
        /*if(projectBeanList.size()>0)
        {
            setViewPageTab();
        }*/
        OkHttpManager.getInstance().get(Constants.PROJECT_URL, new Callback() {
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
                projectBeanList.clear();
                List<ProjectBean> projectBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<ProjectBean>>(){}.getType());
                projectBeanList.addAll(projectBeans);
                LitePal.deleteAll(ProjectBean.class);
                LitePal.saveAll(projectBeanList);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewPageTab();
                    }
                });


            }
        });
    }




        private void setViewPageTab()
        {
            wxArticleTabFragments.clear();
            tabIndicator.clear();
            for (ProjectBean projectBean : projectBeanList) {
                tabIndicator.add(projectBean.getName());
                wxArticleTabFragments.add(WxArticleTabFragment.newInstance(projectBean.getName(), projectBean.getCid()+""));

            }
            tabIndicator.add(projectBeanList.get(projectBeanList.size()-1).getName());
            wxArticleTabFragments.add(WxArticleTabFragment.newInstance(projectBeanList.get(projectBeanList.size()-1).getName(), projectBeanList.get(projectBeanList.size()-1).getCid()+""));
            tabFragmentAdapter.setFragments(wxArticleTabFragments);
            tabLayout.setupWithViewPager(viewPager);
            //tabFragmentAdapter.notifyDataSetChanged();
            //viewPager.setCurrentItem(0);
            /*tabFragmentAdapter = new TabFragmentAdapter(getChildFragmentManager(),wxArticleTabFragments,projectBeanList);
            viewPager.setAdapter(tabFragmentAdapter);
            // viewPager.setOffscreenPageLimit(wxArticleTabFragments.size());
            viewPager.setOffscreenPageLimit(wxArticleTabFragments.size());
            tabLayout.setupWithViewPager(viewPager);*/
        }


}
