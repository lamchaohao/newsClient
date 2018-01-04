package net.togogo.newsclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.togogo.newsclient.R;
import net.togogo.newsclient.adapter.NewsViewPagerAdapter;
import net.togogo.newsclient.utils.HttpRes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LifeFragment extends Fragment {

    @BindView(R.id.iv_life_gene)
    ImageView mIvLifeGene;
    @BindView(R.id.tl_life)
    TabLayout mTlLife;
    @BindView(R.id.vp_life)
    ViewPager mVpLife;
    Unbinder unbinder;
    String TAG = "LifeFragment";
    public LifeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LifeFragment newInstance(String param1, String param2) {
        LifeFragment fragment = new LifeFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated: " );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "LifeFragment onCreateView: " );
        View view = inflater.inflate(R.layout.fragment_life, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "LifeFragment onViewCreated: " );
    }

    private void initView() {

        //整理数据
        for (int i = 0; i < HttpRes.NEWS_TAB_ARRAY.length; i++) {
            mTlLife.addTab(mTlLife.newTab().setText(HttpRes.NEWS_TAB_ARRAY[i]));
        }

        //tab 可设置文字,图片,图文,自定义view
        mTlLife.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mVpLife.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        int tabCount = mTlLife.getTabCount();
        List<String> titles=new ArrayList<>();

        //2.转化成list数据结构
        List<Fragment> fragmentList = new ArrayList<>();

        for (int i = 0; i < HttpRes.NewsTypeArray.length; i++) {
            //每个标签创建一个fragment
            TabNewsFragment fg = TabNewsFragment.newInstance(HttpRes.NewsTypeArray[i]);
            fragmentList.add(fg);
            //把标签作为一个集合,传递给FragmentPagerAdapter
            titles.add(HttpRes.NEWS_TAB_ARRAY[i]);
        }
        //3.viewpager 设置adpter,绑定数据
        NewsViewPagerAdapter adapter = new NewsViewPagerAdapter(getActivity().getSupportFragmentManager(),fragmentList,titles);
        mVpLife.setAdapter(adapter);
        //4.设置tablayout与viewpager联动,同步tab切换与viewpager切换页面
        mTlLife.setupWithViewPager(mVpLife);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: " );
        unbinder.unbind();
    }

    @OnClick(R.id.iv_life_gene)
    public void onViewClicked() {

    }
}
