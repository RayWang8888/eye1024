package com.raywang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * 实现了一些公共操作的activity基类
 * Created by Ray Wang on 2015/6/21.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState,int layoutId) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        iniHead();
        iniView();
    }

    protected void onCreate(Bundle savedInstanceState,int layoutId,boolean notitle) {
        super.onCreate(savedInstanceState);
        if(notitle){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        }
        setContentView(layoutId);
        iniHead();
        iniView();
    }

    /** 初始化头部*/
    protected void iniHead(){}

    /**
     * 初始化控件
     */
    protected void iniView(){

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

    /**
     * 控件的点击事件处理方法
     * @param id 控件的id
     */
    protected void click(int id){

    }
}
