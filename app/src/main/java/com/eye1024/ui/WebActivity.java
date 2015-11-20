package com.eye1024.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.eye1024.R;
import com.eye1024.api.ApiURL;
import com.eye1024.api.SettingName;
import com.eye1024.bean.ArticleResult;
import com.eye1024.db.ReadLogDao;
import com.raywang.rayutils.SharedPreferencesUtil;
import com.raywang.view.SwipeBackActivity;
import com.raywang.view.SwipeBackLayout;
import com.rey.material.widget.ProgressView;

/**
 * 显示网页的WebView
 * @author Ray
 * @date 2015年7月2日11:31:39
 */
public class WebActivity extends SwipeBackActivity implements View.OnClickListener{

    private boolean isRead = false;



    private WebView web;

    private ArticleResult.Article article;
    /** 进度条*/
    private ProgressView progressView;

    private SharedPreferencesUtil util;
    private ReadLogDao logDao;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.BlackMainActivityTheme);
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_web);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        iniHead();
        iniView();
    }

    protected void iniHead() {
        util = SharedPreferencesUtil.newInstance(this);
        article = (ArticleResult.Article) getIntent().getSerializableExtra("article");
        TextView back = (TextView) findViewById(R.id.back);
        back.setText("文章详情");
        back.setOnClickListener(this);
    }

    protected void iniView() {
        logDao = new ReadLogDao(this);
        progressView = (ProgressView) findViewById(R.id.progress);
        progressView.setProgress(0f);

        progressView.start();

        web = (WebView) findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        web.removeJavascriptInterface("searchBoxJavaBredge_");
        boolean isBlack = util.getBoolean(SettingName.ISBLACK,false);
        if(isBlack){
            web.setBackgroundColor(0);
        }

        web.loadUrl(ApiURL.HOST+ ApiURL.GETARTICLE+"?url="+article.getUrl()+"&isBlack="+isBlack);

        web.setWebViewClient(new WebViewClient());
        //加载完成后才加载图片
        web.getSettings().setBlockNetworkImage(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web.getSettings().setLoadsImagesAutomatically(true);
        } else {
            web.getSettings().setLoadsImagesAutomatically(false);
        }

        web.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                isRead = true;
                logDao.cacheReadLog(article.getUrl());
                progressView.setVisibility(View.GONE);
                if (util.getBoolean("showImg", true)) {
                    web.getSettings().setBlockNetworkImage(false);
                }
                web.getSettings().setLoadsImagesAutomatically(true);
            }


        });

        //更新进度条
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressView.setProgress((float) newProgress / 100);
            }
        });


    }

    protected void click(int id) {
        switch (id){
            case R.id.back:

                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        click(R.id.back);
    }

    @Override
    protected void onResume() {
        StatService.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        StatService.onPause(this);
        super.onPause();
    }


    /**
     * 为控件设置点击监听
     * @param viewId 控件的id集合
     */
    protected void setOnclickListener(int... viewId){
        if(viewId != null && viewId.length > 0){
            for(int id : viewId){
                findViewById(id).setOnClickListener(this);
            }
        }
    }

    /**
     * 为控件设置点击监听
     * @param views 控件集合
     */
    protected void setOnclickListener(View... views){
        if(views != null && views.length > 0){
            for(View view : views){
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);
        click(v.getId());
        v.setEnabled(true);
    }


    @Override
    public void finish() {
        if(isRead){
            Intent intent = new Intent();
            intent.putExtra("url",article.getUrl());
            setResult(Activity.RESULT_OK,intent);
        }
        super.finish();
    }
}
