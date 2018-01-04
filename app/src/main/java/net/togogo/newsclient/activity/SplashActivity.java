package net.togogo.newsclient.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import net.togogo.newsclient.R;
import net.togogo.newsclient.utils.Constant;

public class SplashActivity extends Activity {

    public static final int ENTER_GUIDE_CODE  = 200;
    private static final int ENTER_MAIN_CODE = 300;
    private String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        initView();
        initEven();
    }

    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENTER_GUIDE_CODE:
                    startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                    finish();
                    break;
                case ENTER_MAIN_CODE:
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                    break;
            }
        }
    };

    private void initEven() {
        //两秒钟后进入其他activity
        boolean isEnteredGuideActivity = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE).getBoolean(Constant.SP_KEY_ENTERED_GUIDE, false);
        //判断是否已经进入过guideActivity,如果已经进入过,则直接进入mainActivity;
        if (isEnteredGuideActivity) {
            mHandler.sendEmptyMessageDelayed(ENTER_MAIN_CODE,2000);
        }else {
            mHandler.sendEmptyMessageDelayed(ENTER_GUIDE_CODE,2000);
        }


    }

    private void initView() {
        ActionBar actionBar = getActionBar();
        if (actionBar!=null) {
            actionBar.hide();
        }else {
            Log.e(TAG, "initView: actionbar == null" );
        }
    }
}
