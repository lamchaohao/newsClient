package net.togogo.newsclient.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.togogo.newsclient.bean.NewsContent;

import java.util.List;

/**
 * Created by Lam on 2017/9/17.
 */

public class LunboAdapter extends PagerAdapter {
    private String TAG ="LunboPagerAdapter";
    private Context mContext;
    private List<NewsContent> mNewsContentList;

    public LunboAdapter(Context context, List<NewsContent> newsContentList) {
        Log.i(TAG, "LunboAdapter: ");
        mContext = context;
        mNewsContentList = newsContentList;
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount: "+mNewsContentList.size());
        return mNewsContentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Log.i(TAG, "isViewFromObject: ");
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem: ");
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(mContext).load(mNewsContentList.get(position).getPicUrl()).into(imageView);
        container.addView(imageView);
        Log.i(TAG, "instantiateItem: "+mNewsContentList.get(position).getPicUrl());
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i(TAG, "destroyItem: ");
        container.removeView((View) object);
    }
}
