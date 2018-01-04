package net.togogo.newsclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.togogo.newsclient.R;
import net.togogo.newsclient.bean.NewsContent;
import net.togogo.newsclient.utils.ImageLoader;
import net.togogo.newsclient.utils.UrlToBitmap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lam on 2017/9/16.
 */

public class NewsAdapter extends RecyclerView.Adapter {
    private String TAG = "NewsAdapter";
    private Context mContext;
    private List<NewsContent> mNewsContentList;
    private UrlToBitmap mUrlToBitmap ;

    private int TYPE_HEADER = 1;
    private int TYPE_NORMAL = 2;
    private int TYPE_FOOTER = 3;

    private View mHeaderView;
    private View mFooterView;

    private ImageLoader mImageLoader;

    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public NewsAdapter(Context context, List<NewsContent> newsContentList) {
        mContext = context;
        mNewsContentList = newsContentList;
        mUrlToBitmap = new UrlToBitmap(mContext);
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.i(TAG, "onCreateViewHolder: viewType="+viewType);
        //如果headerview不为空并且viewType = headerType
        if (viewType==TYPE_HEADER&&mHeaderView!=null){
            return new NewsHolder(mHeaderView);
        }
        if (viewType==TYPE_FOOTER&&mFooterView!=null){
            return new NewsHolder(mFooterView);
        }
        View inflate = View.inflate(mContext, R.layout.item_news, null);
        return new NewsHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        Log.i(TAG, "onBindViewHolder: position = "+position);
        if (getItemViewType(position)==TYPE_HEADER){
            if (mHeaderView!=null)
                return;
        }else if (getItemViewType(position)==TYPE_FOOTER){
            if (mFooterView!=null){
                return;

            }
        }else {
            final NewsHolder viewHolder = (NewsHolder) holder;
            NewsContent newsContent = mNewsContentList.get(position);
            viewHolder.mTvNewsCtime.setText(newsContent.getCtime());
            viewHolder.mTvNewsTitle.setText(newsContent.getTitle());
            viewHolder.mTvNewsDesc.setText(newsContent.getDescription());
//        mUrlToBitmap.getBitmapFromUrl(newsContent.getPicUrl(),viewHolder.mIvNewsPic);
            mImageLoader.loadImage(viewHolder.mIvNewsPic,newsContent.getPicUrl());
//            Glide.with(mContext).load(newsContent.getPicUrl()).into(viewHolder.mIvNewsPic);
            if (mListener!=null) {
                viewHolder.llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(v,viewHolder.getLayoutPosition());
                    }
                });
            }

        }

        //glide 是已经帮我们实现了缓存
    }

    @Override
    public int getItemViewType(int position) {
        //headerview 作为头部 position = 0;
        //优先考虑
//        Log.i(TAG, "getItemViewType: position = "+position);
//        if (mHeaderView==null){
//            return TYPE_NORMAL;
//        }
//
//        if (position==0){
//            return TYPE_HEADER;
//        }

        if (mHeaderView!=null&&position==0){
            return TYPE_HEADER;
        }else if (mFooterView!=null&&position==getItemCount()-1){
            return TYPE_FOOTER;
        }else {
            return TYPE_NORMAL;
        }

    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        //代表通知视图去更新,position 0代表headerview
        notifyItemInserted(0);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(mNewsContentList.size()+1);
    }

    public void removeFooterView(){
        mFooterView = null;
        notifyItemRemoved(mNewsContentList.size());
    }

    public void add(NewsContent newsContent){
        mNewsContentList.add(newsContent);
        notifyItemInserted(mNewsContentList.size());
    }

    public void add(List<NewsContent> newsContentList){
        mNewsContentList.addAll(newsContentList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View v,int position);
    }


    @Override
    public int getItemCount() {
        return mNewsContentList.size();
    }


    class NewsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_news_title)
        TextView mTvNewsTitle;
        @BindView(R.id.tv_news_desc)
        TextView mTvNewsDesc;
        @BindView(R.id.tv_news_ctime)
        TextView mTvNewsCtime;
        @BindView(R.id.iv_news_pic)
        ImageView mIvNewsPic;
        @BindView(R.id.ll_newsItem_root)
        LinearLayout llRoot;
        public NewsHolder(View itemView) {
            super(itemView);
//            Log.i(TAG, "NewsHolder: onCreate");
            if (itemView==mHeaderView||itemView==mFooterView){

                return;
            }
            ButterKnife.bind(this,itemView);
        }

    }

}
