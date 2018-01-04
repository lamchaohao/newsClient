package net.togogo.newsclient.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import net.togogo.newsclient.R;
import net.togogo.newsclient.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserTipsActivity extends Activity {

    @BindView(R.id.iv_user_tips_iKnow)
    ImageView mIvUserTipsIKnow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_tips);
        ButterKnife.bind(this);
//        initTranslucent();
    }


    @OnClick({R.id.iv_user_tips_iKnow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_user_tips_iKnow:
                //保存状态,避免下次启动MainActivity后再次进入
                getSharedPreferences(Constant.SP_NAME,MODE_PRIVATE).edit().putBoolean(Constant.SP_KEY_ENTERED_TIPS,true).apply();
                finish();
                break;

        }
    }
}
