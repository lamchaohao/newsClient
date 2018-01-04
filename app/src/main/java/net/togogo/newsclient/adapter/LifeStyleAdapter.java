package net.togogo.newsclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.togogo.newsclient.R;
import net.togogo.newsclient.utils.DensityUtil;

/**
 * Created by lamchaohao on 2017/9/9.
 */

public class LifeStyleAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int[] imgResId;
    private boolean[] isCheck;

    OnItemClickListener mOnItemClickListener;


    public LifeStyleAdapter(Context context, int[] imgResId) {
        mContext = context;
        this.imgResId = imgResId;
        isCheck = new boolean[imgResId.length];
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//创建viewholder
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_life_style, null);
        return new ViewHolder(view);
    }

    //每次绑定一个item会调用此方法
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {//绑定viewholder数据

        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mImageView.setImageResource(imgResId[position]);
        if (isCheck[position]) {
            viewHolder.mIvChooseTip.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mIvChooseTip.setVisibility(View.GONE);
        }
        runEnterAnim(viewHolder.rlRoot,position);
        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck[position]=!isCheck[position];
                //在此设置即可
                if (isCheck[position]) {
                    viewHolder.mIvChooseTip.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.mIvChooseTip.setVisibility(View.GONE);
                }
                mOnItemClickListener.onLifeItemClick(v,position,isCheck[position]);
            }
        });

    }

    private void runEnterAnim(View view,int position) {
        view.setTranslationY(DensityUtil.getScreenHeight(mContext));//把view在垂直方向下移一个屏幕高度
        view.animate()
                .translationY(0)//把view在垂直方向上移一个屏幕高度
                .setStartDelay(100*position)
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(1000)
                .start();

    }

    @Override
    public int getItemCount() {//返回item数量
        return imgResId.length;
    }

    public boolean[] getIsCheck() {
        return isCheck;
    }

    public interface OnItemClickListener{
        void onLifeItemClick(View v,int position,boolean isCheck);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        ImageView mIvChooseTip;
        RelativeLayout rlRoot;
        public ViewHolder(View itemView) {
            super(itemView);
            rlRoot = (RelativeLayout) itemView.findViewById(R.id.rl_item_lifeStyle_root);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_item_lifeStyle_choice);
            mIvChooseTip = (ImageView) itemView.findViewById(R.id.iv_lifeStyle_choose_tip);
        }
    }



}
