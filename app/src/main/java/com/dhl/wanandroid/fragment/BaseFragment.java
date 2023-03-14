package com.dhl.wanandroid.fragment;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.util.SettingUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author dhl
 * Fragment 基类
 * Fragment 的存在，是 专注于承担 “视图控制器” 责任，
 * 以分担 Activity 责任、让 Activity 更专注于 “幕后协调者” 工作。
 */
public abstract class BaseFragment extends Fragment {

    private static final String TITLE = "title";

    /**
     * 共用ToolBar
     */
    protected Toolbar toolbar;


    /**
     * smartRefresh
     */
    protected RefreshLayout refreshLayout;
    protected ClassicsHeader mClassicsHeader;
    protected Drawable mDrawableProgress;
    /**
     * rcy
     */
    protected RecyclerView recyclerView;




    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    protected void initToolbar(View view) {
        toolbar = view.findViewById(R.id.tool_bar);
        toolbar.setBackground(new ColorDrawable(SettingUtil.INSTANCE.getColor()));
    }

    protected void initRcy(View view) {
        recyclerView = view.findViewById(R.id.rcy_view);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        mClassicsHeader = (ClassicsHeader) refreshLayout.getRefreshHeader();
        //mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()-deta));
        mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

}
