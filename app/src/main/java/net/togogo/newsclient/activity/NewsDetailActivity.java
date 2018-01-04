package net.togogo.newsclient.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.togogo.newsclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.pb_progress)
    ProgressBar mPbProgress;
    @BindView(R.id.ll_progress)
    LinearLayout mLlProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(title);
        }
        mWebView.loadUrl(url);

        //webview 配套常用两个类
        //webSettings 对webView进行配置
        WebSettings settings = mWebView.getSettings();
        //使用js
        settings.setJavaScriptEnabled(true);
        //支持双指缩放
        settings.setSupportZoom(true);
        //设置内置缩放空间,如果设置为false,则不可缩放
        settings.setBuiltInZoomControls(true);
        //隐藏原生的缩放控件
        settings.setDisplayZoomControls(false);

        //webViewClient 类
        //处理各种通知,事件
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //调用自身webview打开新的url
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 加载网页开始时候
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载网页完毕时候
//                mLlProgress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            //设置加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPbProgress.setProgress(newProgress);
                mTvProgress.setText("正在加载"+newProgress+"%");
                if (newProgress==100) {
                    mLlProgress.setVisibility(View.GONE);
                }
            }
            //设置网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (getSupportActionBar()!=null) {
                    getSupportActionBar().setTitle(title);
                }
            }
        });


    }

    @Override
    public void onBackPressed(){
        //webview回退
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }else {
            finish();
        }

    }
}
