package com.dhl.wanandroid.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.dhl.wanandroid.model.Knowledge;
import com.dhl.wanandroid.model.KnowledgeTreeData;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;

import com.dhl.wanandroid.R;
import com.dhl.wanandroid.fragment.KnowledgeTabFragment;
import com.dhl.wanandroid.model.KnowledgeInfo;
import com.dhl.wanandroid.model.KnowledgeInfochild;

import java.util.ArrayList;
import java.util.List;


/**
 * 知识体系 详情
 * @author dhl
 * @date 2022 12-28
 */
public class KnowledgeInfoActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TabLayout tabLayout;

    private ViewPager viewPager;

    /**
     * TabLayout title
     */
    private List<String> indicators;

    private List<KnowledgeTabFragment> fragmentList;

    private KnowledgeTreeData knowledgeTreeData;

    public static void startActivity(Activity activity, KnowledgeTreeData knowledgeTreeData) {
        Intent intent = new Intent(activity, KnowledgeInfoActivity.class);
        intent.putExtra("KnowledgeTreeData", knowledgeTreeData);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_info);
        initView();
        initData();
    }

    private void initView() {
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.content_vp);
    }

    private void initData() {
        indicators = new ArrayList<>();
        fragmentList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            knowledgeTreeData = (KnowledgeTreeData) intent.getSerializableExtra("KnowledgeTreeData");
            getSupportActionBar().setTitle(knowledgeTreeData.getName());
            for (Knowledge knowledge : knowledgeTreeData.getChildren()) {
                indicators.add(knowledge.getName());
                fragmentList.add(KnowledgeTabFragment.newInstance(knowledge.getName(), knowledge.getId() + ""));
            }
            viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int i) {
                    return fragmentList.get(i);
                }

                @Override
                public int getCount() {
                    return fragmentList.size();
                }

                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    return indicators.get(position);
                }
            });
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(fragmentList.size());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
