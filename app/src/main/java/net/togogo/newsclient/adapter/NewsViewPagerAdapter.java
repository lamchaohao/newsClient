package net.togogo.newsclient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by lamchaohao on 2017/9/10.
 */

public class NewsViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList;
    List<String> titles;
    public NewsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public NewsViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList,List<String> titles) {
        super(fm);
        mFragmentList = fragmentList;
        this.titles=titles;
    }

    @Override
    public int getCount() {

        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return titles.get(position);
    }
}
