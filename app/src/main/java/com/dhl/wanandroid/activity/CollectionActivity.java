package com.dhl.wanandroid.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.adapter.CollectionAdapter;
import com.dhl.wanandroid.adapter.OnCollectionListener;
import com.dhl.wanandroid.adapter.WxArticleAdapter;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.CollectionBean;
import com.dhl.wanandroid.model.WxArticleBean;
import com.dhl.wanandroid.util.APIUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的收藏UI
 * @dhl
 */
public class CollectionActivity extends AppCompatActivity {

    private static final String TAG = "CollectionActivity";

    /**
     * smartRefresh
     */
    protected RefreshLayout refreshLayout ;
    protected ClassicsHeader mClassicsHeader;
    protected Drawable mDrawableProgress;
    /**
     * rcy
     */
    protected RecyclerView recyclerView ;

    private Toolbar toolbar ;

    private CollectionAdapter  collectionAdapter ;
    private List<CollectionBean> collectionBeanList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initRefresh();

    }

    private void initRefresh()
    {
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("收藏");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(R.id.rcy_view);
        refreshLayout = findViewById(R.id.refreshLayout);
        mClassicsHeader = (ClassicsHeader)refreshLayout.getRefreshHeader();
        //mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()-deta));
        mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        collectionBeanList = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(this,R.layout.fragment_homepage_item,collectionBeanList);
        recyclerView.setAdapter(collectionAdapter);
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(APIUtil.getCollectionList(0));
            }
        });

    }

    private void getData(String url )
    {
        OkHttpManager.getInstance().getCollectionList(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();
                Log.e(TAG,"json::"+json);

                JsonObject fromJson = new Gson().fromJson(json, JsonObject.class);
                JsonPrimitive errorCode = fromJson.getAsJsonPrimitive("errorCode");
                JsonElement jsonElement = new JsonParser().parse(json);

                if(errorCode.getAsInt()== 0)
                {
                    JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("data");
                    JsonArray jsonArray = jsonObject.getAsJsonArray("datas");
                    Gson gson = new Gson();
                    final List<CollectionBean> wxArticleBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<CollectionBean>>(){}.getType());
                    collectionBeanList.clear();
                    collectionBeanList.addAll(wxArticleBeans);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           /* collectionAdapter = new CollectionAdapter(CollectionActivity.this,R.layout.fragment_homepage_item,wxArticleBeans);
                            recyclerView.setAdapter(collectionAdapter);*/
                             collectionAdapter.notifyDataSetChanged();
                            refreshLayout.finishRefresh();
                            if(wxArticleBeans.size()<20)
                            {
                                refreshLayout.setEnableLoadMore(false);
                            }
                            setOnClickEvent();
                        }
                    });
                }else
                {
                    final JsonPrimitive  errorMsg = fromJson.getAsJsonPrimitive("errorMsg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ToastUtils.showLong(errorMsg.getAsString());
                            refreshLayout.finishRefresh();
                        }
                    });
                }



            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true ;
    }

    private void setOnClickEvent()
    {
        collectionAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                CollectionBean collectionBean = collectionBeanList.get(position);
                WebActivity.startActivity(CollectionActivity.this, collectionBean.getTitle(),collectionBean.getLink());

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
                return false;
            }
        });
        collectionAdapter.setOnCollectionListener(new OnCollectionListener() {
            @Override
            public void onCollectionClick(View view, int position) {
                CollectionBean collectionBean = collectionBeanList.get(position);
                OkHttpManager.getInstance().postUnCollection(APIUtil.unCollectionArticle(collectionBean.getId() + ""), collectionBean.getOriginId()+"", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String rsp = response.body().string();
                        JsonObject jsonObject = new Gson().fromJson(rsp,JsonObject.class);
                        JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive("errorCode");
                        if(jsonPrimitive.getAsInt() == 0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showLong("取消收藏成功");
                                }
                            });
                            getData(APIUtil.getCollectionList(0));
                        }

                    }
                });

            }
        });
    }
}
