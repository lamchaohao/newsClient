package net.togogo.newsclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.togogo.newsclient.R;
import net.togogo.newsclient.adapter.LifeStyleAdapter;
import net.togogo.newsclient.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GuideActivity extends Activity {
    public static final String TAG = "GuideActivity";
    @BindView(R.id.iv_guide_arrow)
    ImageView mIvGuideArrow;
    @BindView(R.id.rl_guide_opening)
    RelativeLayout mRlGuideOpening;
    @BindView(R.id.rcv_guide_lifeStyle)
    RecyclerView mRcvGuideLifeStyle;
    @BindView(R.id.bt_guide_next)
    Button mBtGuideNext;
    @BindView(R.id.rl_guide_lifeStyle)
    LinearLayout mRlGuideLifeStyle;
    @BindView(R.id.iv_guide_man)
    ImageView mIvGuideMan;
    @BindView(R.id.iv_guide_man_choose_tip)
    ImageView mIvGuideManChooseTip;
    @BindView(R.id.tv_guide_man_name)
    TextView mTvGuideManName;
    @BindView(R.id.iv_guide_women)
    ImageView mIvGuideWomen;
    @BindView(R.id.iv_guide_women_choose_tip)
    ImageView mIvGuideWomenChooseTip;
    @BindView(R.id.tv_guide_women)
    TextView mTvGuideWomen;
    @BindView(R.id.bt_guide_enterMain)
    Button mBtGuideEnterMain;
    @BindView(R.id.rl_guide_choose_sex)
    RelativeLayout mRlGuideChooseSex;
    @BindView(R.id.fl_guide_man)
    FrameLayout mFlGuideMan;
    @BindView(R.id.fl_guide_women)
    FrameLayout mFlGuideWomen;

    //    private LinearLayout mLlLifeStyle;
//    private RelativeLayout mRlOpening;
//    private RecyclerView mRcvLifeStyle;
//    private Button mBtLifeStyleNext;
//    private ImageView mIvArrow;
//    private RelativeLayout mRlChooseSex;
    private float mY;
    private boolean isWomen = false; //默认值为false
    private boolean canNext;
    private LifeStyleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        tranlucentbar();
        initView();
        initAnim();
        initEvent();
    }

    private void tranlucentbar() {
        if (Build.VERSION.SDK_INT >= 21) {//android 5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }


    @OnClick(R.id.bt_guide_next)
    public void guideNext(View v) {
        if (canNext) {
            mRlGuideLifeStyle.setVisibility(View.GONE);
            mRlGuideChooseSex.setVisibility(View.VISIBLE);
            saveLifeStyleData();
            skipToChooseSex();
        }
    }
    //进入主界面
    @OnClick(R.id.bt_guide_enterMain)
    public void enterMain(View v) {
        //保存性别数据
        getSharedPreferences(Constant.SP_NAME,MODE_PRIVATE).edit().putBoolean(Constant.SP_KEY_SEX,isWomen).apply();
        //保存已经配置好,下次不要再进入GuideActivity
        getSharedPreferences(Constant.SP_NAME,MODE_PRIVATE).edit().putBoolean(Constant.SP_KEY_ENTERED_GUIDE,true).apply();
        //进入主界面
        startActivity(new Intent(this,MainActivity.class));
        finish();//以防按下返回键退回此activity
    }

    //选择男性
    @OnClick(R.id.fl_guide_man)
    public void chooseMan(View v){
        isWomen = false;
        mFlGuideMan.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mIvGuideManChooseTip.setVisibility(View.VISIBLE);
        mTvGuideManName.setTextColor(getResources().getColor(R.color.colorPrimary));

        mFlGuideWomen.setBackground(null);//设置为没有背景 null代表移除背景
        mIvGuideWomenChooseTip.setVisibility(View.GONE);
        mTvGuideWomen.setTextColor(getResources().getColor(R.color.colorText));

    }
    //选择女性
    @OnClick(R.id.fl_guide_women)
    public void chooseWomen(View v){
        isWomen = true;
        mFlGuideWomen.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mIvGuideWomenChooseTip.setVisibility(View.VISIBLE);
        mTvGuideWomen.setTextColor(getResources().getColor(R.color.colorPrimary));

        mFlGuideMan.setBackground(null);//设置为没有背景 null代表移除背景
        mIvGuideManChooseTip.setVisibility(View.GONE);
        mTvGuideManName.setTextColor(getResources().getColor(R.color.colorText));
    }


    /**
     * 初始化控件
     */
    private void initView() {
//        mIvArrow = (ImageView) findViewById(R.id.iv_guide_arrow);
//        mRlOpening = (RelativeLayout) findViewById(R.id.rl_guide_opening);
//        mLlLifeStyle = (LinearLayout) findViewById(R.id.rl_guide_lifeStyle);
//        mRcvLifeStyle = (RecyclerView) findViewById(R.id.rcv_guide_lifeStyle);
//        mBtLifeStyleNext = (Button) findViewById(R.id.bt_guide_next);
//        mRlChooseSex = (RelativeLayout) findViewById(R.id.rl_guide_choose_sex);

        mFlGuideMan.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mIvGuideManChooseTip.setVisibility(View.VISIBLE);
        mTvGuideManName.setTextColor(getResources().getColor(R.color.colorPrimary));

        initLifeStyleData();

    }

    /**
     * 初始化箭头闪烁效果动画
     */
    private void initAnim() {

        final AlphaAnimation topAnim = new AlphaAnimation(1, 1);//透明度动画,1表示完全可见,0表示完全透明
        topAnim.setDuration(1000);//设置动画的时长

        final AlphaAnimation bottomAnim = new AlphaAnimation(1, 1);
        bottomAnim.setDuration(1000);

        topAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {//动画开始方法回调

            }

            @Override
            public void onAnimationEnd(Animation animation) {//动画结束方法回调
                mIvGuideArrow.startAnimation(bottomAnim);
                mIvGuideArrow.setImageResource(R.drawable.splash_arrows_bottom);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { //动画循环时候方法回调

            }
        });
        //底部交换
        bottomAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvGuideArrow.startAnimation(topAnim);
                mIvGuideArrow.setImageResource(R.drawable.splash_arrows_top);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mIvGuideArrow.startAnimation(topAnim);

    }

    /**
     * 初始化选择生活方式的界面
     */
    private void initLifeStyleData() {
        int[] resId = {R.drawable.splash_life_01, R.drawable.splash_life_02, R.drawable.splash_life_03, R.drawable.splash_life_04,
                R.drawable.splash_life_05, R.drawable.splash_life_06, R.drawable.splash_life_07};
        //构造Adapter
        mAdapter = new LifeStyleAdapter(this, resId);
        mRcvGuideLifeStyle.setLayoutManager(new LinearLayoutManager(this));
        mRcvGuideLifeStyle.setAdapter(mAdapter);
//        mRcvLifeStyle.setItemAnimator();
        mAdapter.setOnItemClickListener(new LifeStyleAdapter.OnItemClickListener() {
            @Override
            public void onLifeItemClick(View v, int position, boolean isCheck) {
//                mAdapter.notifyItemChanged(position);//数据源已改变,需要手动调用notifyItemChanged()
//                mAdapter.notifyDataSetChanged();// 这个方法是当数据源不止一个item改变的时候,需要调用
                boolean[] isCheckArray = mAdapter.getIsCheck();
                boolean flag = false;
                for (boolean checked : isCheckArray) {
                    if (checked) {
                        flag = true;
                        mBtGuideNext.setText("下一步");
                        canNext = true;
                        break;
                    }
                }

                if (!flag) {
                    canNext = false;
                    mBtGuideNext.setText("选择你的生活方式");
                }

            }
        });

    }

    /**
     * 初始化事件操作
     */
    private void initEvent() {
        //第一幕的滑动事件监听
        mRlGuideOpening.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN://手指按下屏幕 , 记录当前 y坐标
                        mY = event.getY();
                        Log.e(TAG, "onTouch: ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE://手指在屏幕滑动
                        Log.e(TAG, "onTouch: ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP://手指抬起时候,再次获取 Y坐标
                        Log.e(TAG, "onTouch: ACTION_UP");
                        if ((mY - event.getY() > 100)) {
                            //向上滑动
                            mRlGuideOpening.setVisibility(View.GONE);
                            mRlGuideLifeStyle.setBackgroundResource(R.drawable.splash_sex_bg);
                            mRlGuideLifeStyle.setVisibility(View.VISIBLE);
                        }
                        break;

                }
                //true 代表消费这次事件,不消费事件的话,则无法监听到滑动事件
                return true;
            }
        });
        //选择生活方式的下一步按钮
//        mBtGuideNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //sharedPerfrence 保存的是配置,设置文件,小数据
//                //数据库  sqlite3 TB来
//                //如果有选择一个或以上的生活方式,才能进行下一步配置.
//                if (canNext) {
//                    mLlLifeStyle.setVisibility(View.GONE);
//                    mRlChooseSex.setVisibility(View.VISIBLE);
//                    saveLifeStyleData();
//                    skipToChooseSex();
//                }
//
//            }
//        });

    }

    //跳转到选择性别
    private void skipToChooseSex() {

    }

    private void saveLifeStyleData() {
        //保存设置到Sharedperference;
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE);
//        String 类型

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mAdapter.getIsCheck().length; i++) {
            if (mAdapter.getIsCheck()[i]) {
                //true 代表选中状态
                sb.append(i + ",");

            }
            sp.edit().putString(Constant.SP_KEY_LIFE_STYLE, sb.toString()).apply();//sp编辑后记得提交
            //原子操作,要么全部执行完毕,要么不执行
        }

    }


}
