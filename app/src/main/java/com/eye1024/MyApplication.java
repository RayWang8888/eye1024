package com.eye1024;

import android.app.Application;

import com.testin.agent.TestinAgent;

/**
 * Created by Ray on 2015/7/16.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        TestinAgent.init(this, "c82b37f509c0e6bfe9fca04b8933abed", "qq");
    }
}
