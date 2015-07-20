package com.eye1024.ui;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.eye1024.BuildConfig;
import com.eye1024.R;
import com.eye1024.api.SettingName;
import com.raywang.activity.BaseActivity;
import com.raywang.rayutils.SharedPreferencesUtil;

/**
 * 关于的Activity
 * Created by Ray on 2015/7/6.
 */
public class AboutActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(SharedPreferencesUtil.newInstance(this).getBoolean(SettingName.ISBLACK,false)){
            setTheme(R.style.BlackMainActivityTheme);
        }
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
