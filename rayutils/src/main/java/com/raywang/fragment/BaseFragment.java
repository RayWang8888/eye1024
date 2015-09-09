package com.raywang.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 为了处理一些共用的地方的Fragment的基类
 * Created by Ray on 2015/6/26.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{

    /** 是否已经显示*/
    private boolean isVisibleToUser = false;
    /** 是否已经获取了数据*/
    private boolean isIniData = false;
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState,int layoutId) {
        View view = inflater.inflate(layoutId,container,false);
        iniView(view);
        if(isVisibleToUser && !isIniData && getActivity() != null){
            iniData();
        }
        return view;
    }

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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        if(!isIniData && isVisibleToUser && getActivity() != null){
            iniData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    protected abstract void iniData();
    /** 初始化数据完成*/
    protected void onIniDataFinsh(){
        this.isIniData = true;
    }
    /** 需要获取数据*/
    protected void onNeedIniData(){
        this.isIniData = false;
    }
    /**
     * 处理点击事件的方法
     * @param id 控件的id
     */
    protected void click(int id) {

    }
}
