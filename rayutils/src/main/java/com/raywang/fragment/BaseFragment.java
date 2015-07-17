package com.raywang.fragment;

import android.app.Fragment;
import android.view.View;

/**
 * 为了处理一些共用的地方的Fragment的基类
 * Created by Ray on 2015/6/26.
 */
public class BaseFragment extends Fragment implements View.OnClickListener{

    /**
     * 初始化控件的方法
     * @param view Fragment的主要内容的容器控件
     */
    protected void iniView(View view) {

    }


    public final void onClick(View v) {
        v.setEnabled(false);
        click(v.getId());
        v.setEnabled(true);
    }



    /**
     * 处理点击事件的方法
     * @param id 控件的id
     */
    protected void click(int id) {

    }
}
