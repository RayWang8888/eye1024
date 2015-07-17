package com.eye1024.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.eye1024.api.ApiURL;
import com.eye1024.app.R;
import com.eye1024.bean.ArticleResult;
import com.raywang.activity.BaseActivity;
import com.raywang.rayutils.SharedPreferencesUtil;
import com.rey.material.widget.ProgressView;

/**
 * 显示网页的WebView
 * @author Ray
 * @date 2015年7月2日11:31:39
 */
public class WebActivity extends BaseActivity {


    private WebView web;

    private ArticleResult.Article article;
    /** 进度条*/
    private ProgressView progressView;

    private SharedPreferencesUtil util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_web,true);
    }

    @Override
    protected void iniHead() {
        util = SharedPreferencesUtil.newInstance(this);
        article = (ArticleResult.Article) getIntent().getSerializableExtra("article");
        TextView back = (TextView) findViewById(R.id.back);
        back.setText("文章详情");
        back.setOnClickListener(this);
        super.iniHead();
    }

    @Override
    protected void iniView() {
        progressView = (ProgressView) findViewById(R.id.progress);
        progressView.setProgress(0f);

        progressView.start();

        web = (WebView) findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(ApiURL.HOST+ApiURL.GETARTICLE+"?url="+article.getUrl());

        web.setWebViewClient(new WebViewClient());
        //加载完成后才加载图片
        web.getSettings().setBlockNetworkImage(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web.getSettings().setLoadsImagesAutomatically(true);
        } else {
            web.getSettings().setLoadsImagesAutomatically(false);
        }

        web.setWebViewClient(new WebViewClient(){

            public void onPageFinished(WebView view, String url) {
                progressView.setVisibility(View.GONE);
                if(util.getBoolean("showImg",true)) {
                    web.getSettings().setBlockNetworkImage(false);
                }
                web.getSettings().setLoadsImagesAutomatically(true);
            }


        });

        //更新进度条
        web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressView.setProgress((float)newProgress / 100);
            }
        });

        super.iniView();
    }

    @Override
    protected void click(int id) {
        switch (id){
            case R.id.back:
                finish();
                break;
            default:
                super.click(id);
                break;
        }
    }
}
