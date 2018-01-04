package net.togogo.newsclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.togogo.newsclient.App;
import net.togogo.newsclient.R;
import net.togogo.newsclient.activity.NewsDetailActivity;
import net.togogo.newsclient.adapter.NewsAdapter;
import net.togogo.newsclient.bean.NewsBean;
import net.togogo.newsclient.bean.NewsContent;
import net.togogo.newsclient.bean.NewsContentDao;
import net.togogo.newsclient.utils.Constant;
import net.togogo.newsclient.widget.LunboView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static net.togogo.newsclient.utils.Constant.CODE_OK;

public class TabNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG ="TabNewsFragment";
    private static final String ARG_TYPE = "newsType";
    private List<NewsContent> mNewsContentList;
    @BindView(R.id.rcv_newsTab_newslist)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl_newsTab_refresh)
    SwipeRefreshLayout mRefreshLayout;
//    @BindView(R.id.lunbo_newsTab)
    LunboView mLunboView;
    @BindView(R.id.ll_footer_loadmore)
    LinearLayout llLoadmoreview;
    @BindView(R.id.tv_loadMore_tips)
    TextView tvLoadMoreTips;
    Unbinder unbinder;
    private String mNewsType;
    private NewsAdapter mAdapter;
    private NewsApi mNewsApi;
    private NewsContentDao mNewsContentDao;
    private LinearLayoutManager mLinearLayoutManager;
    private int mLoadmoreCount = 10;
    private boolean isFirstLoadMore = true;
    private boolean canShowLoadView;
    private FrameLayout.LayoutParams mLayoutParams;
    private int mLoadViewInintialHeight;

    // TODO: Rename and change types and number of parameters
    public static TabNewsFragment newInstance(String newsType) {
        TabNewsFragment fragment = new TabNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE, newsType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated:" );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: type = " +mNewsType);
        View view = inflater.inflate(R.layout.fragment_news_tab, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        loadData();
        return view;
    }

    private void loadData() {
        //从本地缓存获取数据
        loadDataFromDb();
        //从网络获取数据
        loadDataFromInternet();
    }

    private void loadDataFromDb() {
        //获取daoSession
        mNewsContentDao = ((App) (getActivity().getApplication())).getDaoSession().getNewsContentDao();
        //查询数据库
        //select * from NEWS_CONTENT where newsType='tiyu';
        List<NewsContent> newsListDB = mNewsContentDao.queryBuilder()
                .where(NewsContentDao.Properties.NewsType.eq(mNewsType))
                .build()
                .list();
        //更新数据
        mNewsContentList.addAll(newsListDB);
        mAdapter.notifyDataSetChanged();
        setLunboNewsData(mNewsContentList);

    }
    //处理轮播图数据并将其加入到轮播图
    private void setLunboNewsData(List<NewsContent> newsList) {
        List<NewsContent> lunboNews = new ArrayList<>();
        for (int i = 0; i < newsList.size(); i++) {
            lunboNews.add(newsList.get(i));
            if (i==4) {
                break;
            }
        }
        mLunboView.setNewsData(lunboNews);
    }

    /**
     * 从网络获取数据
     */
    private void loadDataFromInternet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mNewsApi = retrofit.create(NewsApi.class);
        Call<NewsBean> newsBeanCall = mNewsApi.listNews(mNewsType, Constant.API_KEY, mLoadmoreCount);
        newsBeanCall.enqueue(new Callback<NewsBean>() {
            //访问http成功
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                //把http相应的字符串,Json格式,使用Gson转成相应JAVA Bean
                NewsBean newsBean = response.body();
                if (newsBean!=null) {
                    if (newsBean.getCode()==CODE_OK) {
                        List<NewsContent> newslist = newsBean.getNewslist();
                        //删除本地的缓存
                        mNewsContentDao.deleteInTx(mNewsContentList);
                        for (NewsContent newsContent: newslist) {
                            newsContent.setNewsType(mNewsType);
                        }
                        //将新的数据保存到数据库作为缓存
                        mNewsContentDao.insertInTx(newslist);
                        mNewsContentList.addAll(newslist);
                        mAdapter.notifyDataSetChanged();
                        setLunboNewsData(newslist);

                    }else{
                        Log.i(TAG, "onResponse: CODE_OK!=200");
                    }
                }else {
                    Log.i(TAG, "onResponse: newsBean ==null");
                }

            }
            //访问错误
            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {

            }
        });
    }

    private void initView() {
        mLunboView = new LunboView(getContext());
        mNewsContentList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new NewsAdapter(getContext(),mNewsContentList);
        mAdapter.setHeaderView(mLunboView);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //滑动状态
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Log.i(TAG, "onScrollStateChanged: SCROLL_STATE_DRAGGING");
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                        int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                        Log.i(TAG, "onScrollStateChanged: first="+firstVisibleItemPosition+",last="+lastVisibleItemPosition);
                        Log.i(TAG, "onScrollStateChanged: SCROLL_STATE_IDLE");
                        if (mAdapter.getItemCount()==lastVisibleItemPosition+1) {
                            Log.i(TAG, "onScrollStateChanged: 已经滑动到最后底部");
//                            if (isFirstLoadMore) {
//                                loadMore();
//                            }else {
//                                // TODO: 2017/9/23 手动上拉
//
//                            }
//                            canShowLoadView = true;
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Log.i(TAG, "onScrollStateChanged: SCROLL_STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //垂直方向上的滑动距离 dy
                Log.i(TAG, "onScrolled: dy=="+dy);
                int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();

                if (mAdapter.getItemCount()==lastVisibleItemPosition+1) {
                    Log.i(TAG, "onScrolled: 已经滑动到最后底部");
                    if (isFirstLoadMore) {
                        loadMore(true);//true代表显示footerview
                        isFirstLoadMore = false;
                    }else {
                        canShowLoadView = true;
                    }

                }else {
                    canShowLoadView = false;
                }
            }
        });


        Log.i(TAG, "initView: getHeight="+mRefreshLayout.getHeight()+","+mRecyclerView.getBottom());
        Log.i(TAG, "initView: getMeasuredHeight="+mRefreshLayout.getMeasuredHeight());

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            private int mMaxScrollDistance;
            private float mLastY;
            private boolean consumed;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastY = event.getY();
                        mLayoutParams = (FrameLayout.LayoutParams) llLoadmoreview.getLayoutParams();
                        mLoadViewInintialHeight = mRefreshLayout.getHeight();
                        mLayoutParams.topMargin=mRefreshLayout.getHeight();
                        llLoadmoreview.setVisibility(View.VISIBLE);
                        llLoadmoreview.setLayoutParams(mLayoutParams);
                        //最大的滑动距离
                        mMaxScrollDistance = mRefreshLayout.getHeight()/4;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = mLastY - event.getY();
                        mLastY = event.getY();
                        //如果滑动到了底部,可以继续上拉
                        if (canShowLoadView) {
                            if (deltaY>0){//向上拉
                                //判断向上拉的距离,如果还没达到最大的可滑动的距离,则可以继续上拉
                                if (mLoadViewInintialHeight - mLayoutParams.topMargin < mMaxScrollDistance){
                                    deltaY = deltaY/2;
                                    mLayoutParams.topMargin = (int) (mLayoutParams.topMargin-deltaY);
                                    llLoadmoreview.setLayoutParams(mLayoutParams);
                                    //判断loadmoreView的状态到达哪里,如果已经到达可刷新,则提示
                                    setLoadMoreTips();
                                }else {//否则不能上拉
                                    Log.i(TAG, "onTouch: 不能上拉");
                                }

                            }else {//向下拉
                                if (mLoadViewInintialHeight>mLayoutParams.topMargin){
                                    mLayoutParams.topMargin = (int) (mLayoutParams.topMargin - deltaY);
                                    llLoadmoreview.setLayoutParams(mLayoutParams);
                                    consumed = true;
                                    setLoadMoreTips();
                                }else {
                                    consumed = false;
                                    Log.i(TAG, "onTouch: 已经到达底部,不能往下拉");
                                }

                            }
                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        //判断滑动的距离是否到达可刷新的状态 触发刷新的距离为 mMaxScrollDistance/2
                        if (mLayoutParams==null){
                            //防止onclick事件设置后有空指针异常出现
                            return false;
                        }
                        if (mLoadViewInintialHeight - mLayoutParams.topMargin > mMaxScrollDistance/2){
                            loadMore(false);//false 代表不显示footerView
                        }else {
                            //如果没有到达可刷新的状态, 则打回原形
                            mLayoutParams.topMargin = mLoadViewInintialHeight;
                            llLoadmoreview.setLayoutParams(mLayoutParams);
//                            setLoadMoreTips();
                        }
                        consumed = false;
                        break;

                }


                return consumed;
            }

            private void setLoadMoreTips() {
                if (mLoadViewInintialHeight - mLayoutParams.topMargin > mMaxScrollDistance/2){
                    tvLoadMoreTips.setText("松手加载更多");
                }else {
                    tvLoadMoreTips.setText("继续上拉可加载更多");
                }
            }
        });


        mAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                NewsContent newsContent = mNewsContentList.get(position);
                String url = newsContent.getUrl();
                Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("title",newsContent.getTitle());
                startActivity(intent);
            }
        });

    }

    /**
     * 加载更多
     */
    private void loadMore(boolean isShowFooter) {

        if (isShowFooter){
            View inflate = View.inflate(getContext(), R.layout.item_footer, null);
            mAdapter.setFooterView(inflate);
        }

        mLoadmoreCount += 10;
        Log.i(TAG, "loadMore: 正在加载 mLoadmoreCount="+mLoadmoreCount);
        //api数据最大加载50条
        if (mLoadmoreCount>50) {
            Snackbar.make(mRefreshLayout,"没有更多了",Snackbar.LENGTH_LONG).show();
            mAdapter.removeFooterView();
            mLayoutParams.topMargin = mLoadViewInintialHeight;
            llLoadmoreview.setLayoutParams(mLayoutParams);
            return;
        }
        Call<NewsBean> newsBeanCall = mNewsApi.listNews(mNewsType, Constant.API_KEY, mLoadmoreCount);
        newsBeanCall.enqueue(new Callback<NewsBean>() {
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                NewsBean newsBean = response.body();

                if (newsBean!=null) {
                    if (newsBean.getCode()==CODE_OK) {
                        List<NewsContent> newslist = newsBean.getNewslist();
                        for (int i = newslist.size()-10; i < newslist.size(); i++) {
                            mAdapter.add(newslist.get(i));
                        }
                        mAdapter.removeFooterView();
                        //加载完成后, loadmoreView 继续隐藏
                        mLayoutParams.topMargin = mLoadViewInintialHeight;
                        llLoadmoreview.setLayoutParams(mLayoutParams);
                        Log.i(TAG, "loadMore: 已加载完成 newslist.size"+newslist.size());
                        Snackbar.make(mRefreshLayout,"数据已加载10条",Snackbar.LENGTH_LONG).setAction("撤回", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), "已撤回,假的", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {
                //加载失败后,loadmoreView继续隐藏
                mLayoutParams.topMargin = mLoadViewInintialHeight;
                llLoadmoreview.setLayoutParams(mLayoutParams);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 下拉刷新所回调方法
     */
    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");
        Call<NewsBean> newsBeanCall = mNewsApi.listNews(mNewsType, Constant.API_KEY, mLoadmoreCount);
        newsBeanCall.enqueue(new Callback<NewsBean>() {
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                NewsBean newsBean = response.body();
                if (newsBean!=null) {
                    if (newsBean.getCode()==CODE_OK) {
                        List<NewsContent> newslist = newsBean.getNewslist();
                        mNewsContentList.clear();
                        mNewsContentList.addAll(newslist);
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                        setLunboNewsData(newslist);
                        Snackbar.make(mRefreshLayout,"数据已刷新",Snackbar.LENGTH_LONG).setAction("撤回", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), "已撤回,假的", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {

            }
        });
    }

    interface NewsApi{
        @GET("/{type}")
        Call<NewsBean> listNews(@Path("type")String type, @Query("key")String APIkey, @Query("num") int num);
    }

}
