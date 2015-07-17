package com.eye1024.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.eye1024.app.R;
import com.raywang.activity.BaseActivity;

/**
 * 过度界面的activity
 */
public class LoadingActivity extends BaseActivity {

    private Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    handler.sendEmptyMessageDelayed(1, 1800);
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,R.layout.activity_loading,true);
        View view = findViewById(R.id.img);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.ini_anim);
        view.startAnimation(animation);

        handler.sendEmptyMessageDelayed(2,200);

//        FrameLayout content = (FrameLayout) findViewById(R.id.content);
//        new SplashAd(this,content,"1101152570","8863364436303842593",new SplashAdListener(){
//
//            @Override
//            public void onAdDismissed() {
//                //显示完成
//                Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onAdFailed(int i) {
//                Log.i("info","获取失败");
//                //拉去失败
//                Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onAdPresent() {
//
//            }
//        });
    }


    @Override
    public void onBackPressed() {

    }
}
