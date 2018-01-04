package net.togogo.newsclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.togogo.newsclient.R;
import net.togogo.newsclient.fragments.BxqFragment;
import net.togogo.newsclient.fragments.DiscoverFragment;
import net.togogo.newsclient.fragments.LifeFragment;
import net.togogo.newsclient.service.MinaPushService;
import net.togogo.newsclient.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener{
    @BindView(R.id.ll_main_head)
    LinearLayout mLlMainHead;
    @BindView(R.id.ll_main_title)
    LinearLayout mLlMainTitle;
    @BindView(R.id.iv_main_search)
    ImageView mIvMainSearch;
    @BindView(R.id.fl_main_container)
    FrameLayout mFlMainContainer;
    @BindView(R.id.rb_main_life)
    RadioButton mRbMainLife;
    @BindView(R.id.rb_main_bxq)
    RadioButton mRbMainBxq;
    @BindView(R.id.rb_main_dicover)
    RadioButton mRbMainDicover;
    @BindView(R.id.rg_main_nav)
    RadioGroup mRgMainNav;


    String TAG = "MainActivity";
    private LifeFragment mLifeFragment;
    private BxqFragment mBxqFragment;
    private DiscoverFragment mDiscoverFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "MainActivity onCreate: " );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startService(new Intent(this, MinaPushService.class));
        initView();
    }



    private void initView() {
        //判断是否展示用户提示界面
        boolean isShowTips = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE).getBoolean(Constant.SP_KEY_ENTERED_TIPS, false);
        if (!isShowTips) {
            startActivity(new Intent(this, UserTipsActivity.class));
        }

        //系统配置发生改变的时候,fragment会保存下来,但是会创建新的fragmentManager
        //新的fragmentManager会首先去获取fragment队列恢复以前的状态
        FragmentManager fm = getSupportFragmentManager();
//        fm.findFragmentByTag()
        mLifeFragment = (LifeFragment) fm.findFragmentByTag("life");
        mBxqFragment = (BxqFragment) fm.findFragmentByTag("bxq");
        mDiscoverFragment = (DiscoverFragment) fm.findFragmentByTag("discover");

        if (mLifeFragment==null) {
            mLifeFragment = new LifeFragment();
            fm.beginTransaction().add(R.id.fl_main_container,mLifeFragment,"life").commit();
        }else {
            Log.e(TAG, "initView: mLifeFragment 不为空, 从fragment队列取出" );
        }
        if (mBxqFragment==null) {
            mBxqFragment = new BxqFragment();
            fm.beginTransaction().add(R.id.fl_main_container,mBxqFragment,"bxq").commit();
        }else {
            Log.e(TAG, "initView: mBxqFragment 不为空 从fragment队列取出" );
        }
        if (mDiscoverFragment==null) {
            mDiscoverFragment = new DiscoverFragment();
            fm.beginTransaction().add(R.id.fl_main_container,mDiscoverFragment,"discover").commit();
        }else {
            Log.e(TAG, "initView: mDiscoverFragment 不为空 从fragment队列取出" );
        }

        mRgMainNav.setOnCheckedChangeListener(this);
        mRbMainLife.setChecked(true);
    }

    @OnClick({R.id.ll_main_head, R.id.iv_main_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_main_head:
                startActivity(new Intent(this, PersonActivity.class));
                break;
            case R.id.iv_main_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAll(transaction);
        switch (checkedId) {
            case R.id.rb_main_life:
                if (mLifeFragment==null){
                    mLifeFragment = new LifeFragment();
                    transaction.add(R.id.fl_main_container,mLifeFragment,"life");
                }else{
                    transaction.show(mLifeFragment);
                }

                break;
            case R.id.rb_main_bxq:
                if (mBxqFragment==null){
                    mBxqFragment = new BxqFragment();
                    transaction.add(R.id.fl_main_container,mBxqFragment,"bxq");
                }else{
                    transaction.show(mBxqFragment);
                }
                break;
            case R.id.rb_main_dicover:
                if (mDiscoverFragment==null){
                    mDiscoverFragment = new DiscoverFragment();
                    transaction.add(R.id.fl_main_container,mDiscoverFragment,"discover");
                }else{
                    transaction.show(mDiscoverFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void hideAll(FragmentTransaction transaction) {

        if (mLifeFragment!=null) {
            transaction.hide(mLifeFragment);
        }

        if (mBxqFragment!=null) {
            transaction.hide(mBxqFragment);
        }

        if (mDiscoverFragment!=null) {
            transaction.hide(mDiscoverFragment);
        }

    }

}
