package com.eye1024.ui;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;


import com.eye1024.app.BuildConfig;
import com.eye1024.app.R;
import com.raywang.activity.BaseActivity;

/**
 * 关于的Activity
 * Created by Ray on 2015/7/6.
 */
public class AboutActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState, R.layout.activity_about,true);
    }

    @Override
    protected void iniHead() {

        TextView back = (TextView) findViewById(R.id.back);
        back.setText(R.string.about_title);
        back.setOnClickListener(this);
        super.iniHead();
    }

    @Override
    protected void iniView() {
        ((TextView)findViewById(R.id.version)).setText("V "+ BuildConfig.VERSION_NAME);
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
