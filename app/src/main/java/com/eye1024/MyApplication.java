package com.eye1024;

import android.app.Application;

//import com.testin.agent.TestinAgent;

/**
 * Created by Ray on 2015/7/16.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
//        TestinAgent.init(this, "6580ec30b90dbe701cb7657d3978f7e2", "qq");
    }
}
