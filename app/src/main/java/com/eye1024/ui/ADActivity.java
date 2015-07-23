package com.eye1024.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.appx.BDBannerAd;
import com.baidu.appx.BDInterstitialAd;
import com.eye1024.R;
import com.raywang.activity.BaseActivity;
import com.raywang.rayutils.Util;

public class ADActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_ad,true);
    }

    @Override
    protected void iniHead() {
        TextView back = (TextView) findViewById(R.id.back);
        back.setText(R.string.ad);
        back.setOnClickListener(this);
        super.iniHead();
    }

    @Override
    protected void iniView() {
        super.iniView();
        BDBannerAd bannerAd = new BDBannerAd(this,"jFfXOH5BvHuTyd6kBPeD7mrc","VIUQxrCcGMr83BsLmlolAR0d");
        bannerAd.setAdListener(new BDBannerAd.BannerAdListener(){

            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                Util.logi("info","onAdvertisementDataDidLoadSuccess");
            }

            @Override
            public void onAdvertisementDataDidLoadFailure() {
                Util.logi("info","onAdvertisementDataDidLoadFailure");
            }

            @Override
            public void onAdvertisementViewDidShow() {
                Util.logi("info","onAdvertisementViewDidShow");
            }

            @Override
            public void onAdvertisementViewDidClick() {
                Util.logi("info","onAdvertisementViewDidClick");
            }

            @Override
            public void onAdvertisementViewWillStartNewIntent() {
                Util.logi("info","onAdvertisementViewWillStartNewIntent");
            }
        });
        RelativeLayout ad = (RelativeLayout) findViewById(R.id.ad);
        ad.addView(bannerAd);
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
