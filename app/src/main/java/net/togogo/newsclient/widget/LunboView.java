package net.togogo.newsclient.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.togogo.newsclient.R;
import net.togogo.newsclient.activity.NewsDetailActivity;
import net.togogo.newsclient.bean.NewsContent;
import net.togogo.newsclient.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 2017/9/17.
 */

public class LunboView extends RelativeLayout implements ViewPager.OnPageChangeListener{
    private String TAG = "LunboView";
    private Context mContext;
    private ViewPager mViewPager;
    private TextView mTvTitle;
    private LinearLayout mLlIndicator;
    private List<NewsContent> mLunboNews = new ArrayList<>();
    private LunboPagerAdapter mLunboPagerAdapter;

    private static final int LUNBO_MSG_CODE = 4001;
    private LunboAutoHandler mAutoPlayHandler;

    public LunboView(Context context) {
        super(context);
        mContext=context;
        initView();
        initEvent();
    }


    public LunboView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initView();
        initEvent();
    }

    public LunboView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        initView();
        initEvent();
    }


    private void initView() {
        inflate(mContext,R.layout.item_lunbo,this);
        mViewPager = (ViewPager) findViewById(R.id.vp_lunbo);
        mTvTitle = (TextView) findViewById(R.id.tv_lunbo_title);
        mLlIndicator = (LinearLayout)findViewById(R.id.ll_lunbo_indicator);
        Log.i(TAG, "initView: ");
    }

    private void initEvent() {
        Log.i(TAG, "initEvent: ");
        mAutoPlayHandler = new LunboAutoHandler();
        mLunboPagerAdapter = new LunboPagerAdapter();
        mViewPager.setAdapter(mLunboPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    public void setNewsData(List<NewsContent> newsContentList){
        if (newsContentList!=null) {
            Log.i(TAG, "setNewsData: ");
            mLunboNews.clear();
            mLunboNews.addAll(newsContentList);
            mLunboPagerAdapter.notifyDataSetChanged();
            initLunboIndicator();
        }

    }

    /**
     * 初始化轮播图指示器
     */
    private void initLunboIndicator() {
        //增加指示点数
        initPoint();
        //初始化状态,设置第一个标题,设置第一个点为选中
        initState();
        //开始自动播放
        mAutoPlayHandler.startLunbo();
    }

    private void initState() {
        if (mLlIndicator.getChildAt(0)!=null) {
            mLlIndicator.getChildAt(0).setEnabled(true);
            if (mLunboNews.get(0)!=null) {
                mTvTitle.setText(mLunboNews.get(0).getTitle());
            }
        }
    }

    private void initPoint() {
        mLlIndicator.removeAllViews();
        for (int i = 0; i < mLunboNews.size(); i++) {
            //新建一个view对象
            View pointView = new View(mContext);
            pointView.setBackgroundResource(R.drawable.lunbo_indicator_selector);
            pointView.setEnabled(false);
            //准备参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(mContext,5),DensityUtil.dip2px(mContext,5));
            params.leftMargin = DensityUtil.dip2px(mContext,8);
            //设置布局参数
            pointView.setLayoutParams(params);
            //将指示点加入容器
            mLlIndicator.addView(pointView);
            //
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //设置轮播图的标题
        mTvTitle.setText(mLunboNews.get(position).getTitle());
        //设置指示器的状态
        for (int i = 0; i < mLunboNews.size(); i++) {
            mLlIndicator.getChildAt(i).setEnabled(i==position);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class LunboAutoHandler extends Handler{

        public void startLunbo(){
            //先删除之前所有的消息和回调
            stopLunbo();
            //两秒后自动发送
            sendEmptyMessageDelayed(LUNBO_MSG_CODE,2000);
        }

        public void stopLunbo(){
            removeCallbacksAndMessages(null);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LUNBO_MSG_CODE:
                    if (mLunboPagerAdapter.getCount()!=0){
                        mViewPager.setCurrentItem((mViewPager.getCurrentItem()+1)%mLunboPagerAdapter.getCount());
                    }
                    sendEmptyMessageDelayed(LUNBO_MSG_CODE,2000);
                    break;
            }
        }
    }


    class LunboPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mLunboNews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mContext).load(mLunboNews.get(position).getPicUrl()).into(imageView);
            container.addView(imageView);
//            Log.i(TAG, "instantiateItem: "+mLunboNews.get(position).getPicUrl());
            imageView.setOnTouchListener(new OnTouchListener() {

                private long mDownTime;
                private float mDownY;
                private float mDownX;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mDownX = event.getX();
                            mDownY = event.getY();
                            mDownTime = System.currentTimeMillis();
                            Log.e(TAG, "onTouch: ACTION_DOWN---按下");
                            mAutoPlayHandler.stopLunbo();
                            break;
                        case MotionEvent.ACTION_UP:
                            float upX = event.getX();
                            float upY = event.getY();
                            if (mDownX==upX&&mDownY==upY){
                                if (System.currentTimeMillis()-mDownTime<1000){
                                    Log.i(TAG, "onTouch: click事件触发");
                                    NewsContent newsContent = mLunboNews.get(position);
                                    String url = newsContent.getUrl();
                                    Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                                    intent.putExtra("url",url);
                                    intent.putExtra("title",newsContent.getTitle());
                                    mContext.startActivity(intent);
                                }
                            }
                            Log.e(TAG, "onTouch: ACTION_UP ---松开");
                            mAutoPlayHandler.startLunbo();
                            break;
                        case MotionEvent.ACTION_CANCEL://划出控件所在的坐标
                            Log.e(TAG, "onTouch: ACTION_CANCEL ---取消");
                            mAutoPlayHandler.startLunbo();
                            break;
                    }
                    return true;
                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


}
