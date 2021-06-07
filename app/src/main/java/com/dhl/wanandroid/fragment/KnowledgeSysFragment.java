package com.dhl.wanandroid.fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.activity.KnowledgeInfoActivity;
import com.dhl.wanandroid.adapter.KnowledgeAdapter;
import com.dhl.wanandroid.app.Constants;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.KnowledgeInfo;
import com.dhl.wanandroid.model.KnowledgeInfochild;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * 知识体系Fragment
 * A simple {@link Fragment} subclass.
 * Use the {@link KnowledgeSysFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KnowledgeSysFragment extends BaseFragment {

    private static final String TAG = "KnowledgeSysFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private List<KnowledgeInfo> knowledgeInfoList ;




    private KnowledgeAdapter knowledgeAdapter ;
    public KnowledgeSysFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KnowledgeSysFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KnowledgeSysFragment newInstance(String param1, String param2) {
        KnowledgeSysFragment fragment = new KnowledgeSysFragment();
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
        return inflater.inflate(R.layout.fragment_knowledge_sys, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initToolbar(view);
        toolbar.setTitle("知识体系");
        initRcy(view);
        knowledgeInfoList = LitePal.findAll(KnowledgeInfo.class);

        for( KnowledgeInfo knowledge :knowledgeInfoList)
        {
            List<KnowledgeInfochild> list = LitePal.where("knowledgeInfo_id = "+knowledge.getId()).find(KnowledgeInfochild.class);
                    //LitePal.findAll(KnowledgeInfochild.class,knowledge.getId());
            knowledge.setChildren(list);
        }
        refreshLayout.finishRefresh();
        knowledgeAdapter = new KnowledgeAdapter(getActivity(),R.layout.fragment_knowledge_item,knowledgeInfoList);
        recyclerView.setAdapter(knowledgeAdapter);

        //initViews(view);
        refreshLayout.autoRefresh();
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getKnowledge();
            }
        });
        //setOnClick();
      /* if(knowledgeInfo.size()>0)
        {
           // refreshLayout.finishRefresh();
            knowledgeAdapter = new KnowledgeAdapter(getActivity(),R.layout.fragment_knowledge_item,knowledgeInfo);
            recyclerView.setAdapter(knowledgeAdapter);
        }*/


    }
    private void getKnowledge()
    {
        OkHttpManager.getInstance().get(Constants.KNOWLEDGE_URL, new Callback() {
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

                // Log.e(TAG,"response="+response.body().string());

                JsonElement jsonElement = new JsonParser().parse(response.body().string());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                knowledgeInfoList = gson.fromJson(jsonArray.toString(),new TypeToken<List<KnowledgeInfo>>(){}.getType());
                LitePal.deleteAll(KnowledgeInfochild.class);
                LitePal.deleteAll(KnowledgeInfo.class);
                for(KnowledgeInfo knowledgeInfo : knowledgeInfoList)
                {
                    List<KnowledgeInfochild> list = knowledgeInfo.getChildren();
                    LitePal.saveAll(list);
                }
                LitePal.saveAll(knowledgeInfoList);
                // Log.e(TAG,"list=="+knowledgeInfo.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh();
                        knowledgeAdapter = new KnowledgeAdapter(getActivity(),R.layout.fragment_knowledge_item,knowledgeInfoList);
                        recyclerView.setAdapter(knowledgeAdapter);
                        setOnClick();

                    }
                });


            }
        });
    }

    private void setOnClick()
    {
        if(knowledgeAdapter != null) {
            knowledgeAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    KnowledgeInfo knowledgeInfo = knowledgeInfoList.get(position);
                    KnowledgeInfoActivity.startActivity(getActivity(),knowledgeInfo);

                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
        }
    }
}
