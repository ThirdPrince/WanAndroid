package com.dhl.wanandroid.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.dhl.wanandroid.fragment.WxArticleFragment;
import com.dhl.wanandroid.fragment.WxArticleTabFragment;
import com.dhl.wanandroid.model.ProjectBean;

import java.util.ArrayList;
import java.util.List;

public class TabFragmentAdapter extends FragmentPagerAdapter {


    private List<WxArticleTabFragment> wxArticleTabFragmentList ;


    private List<String> tabIndicator ;
    private FragmentManager fragmentManager ;
    public TabFragmentAdapter(FragmentManager fm,List<WxArticleTabFragment> wxArticleTabFragmentList,List<String> tabIndicator) {
        super(fm);
        this.fragmentManager = fm;
        this.wxArticleTabFragmentList = wxArticleTabFragmentList ;
        this.tabIndicator = tabIndicator ;
    }

   /* @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        WxArticleTabFragment wxArticleTabFragment = (WxArticleTabFragment) super.instantiateItem(container, position);
        return wxArticleTabFragment;
    }*/

    @Override
    public Fragment getItem(int i) {

        return  wxArticleTabFragmentList.get(i);
       /* ProjectBean projectBean = projectBeanList.get(i);
        WxArticleTabFragment wxArticleTabFragment = WxArticleTabFragment.newInstance(projectBean.getName(), projectBean.getCid()+"");
        return wxArticleTabFragment ;*/
                //wxArticleTabFragmentList.get(i);
    }

    public void setFragments(List<WxArticleTabFragment> fragments) {
        if(this.wxArticleTabFragmentList != null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            for(Fragment f:this.wxArticleTabFragmentList){
                ft.remove(f);
            }
            ft.commit();
            ft=null;
            fragmentManager.executePendingTransactions();
        }
        this.wxArticleTabFragmentList = fragments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return wxArticleTabFragmentList.size();
    }


   /* @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }*/

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabIndicator.get(position);
    }
}
