package com.eye1024.ui.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eye1024.app.R;
import com.eye1024.ui.AboutActivity;
import com.eye1024.util.ImageLoadIni;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.qq.e.appwall.GdtAppwall;
import com.raywang.fragment.BaseFragment;
import com.raywang.rayutils.SharedPreferencesUtil;
import com.raywang.rayutils.UIHlep;
import com.raywang.rayutils.Util;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Switch;

import java.io.File;

/**
 * 菜单的Fragment
 * Created by Administrator on 2015/6/26.
 */
public class MenuFragment extends BaseFragment {

    private Button clean;

    private DiskCache diskCache = null;

    private Switch showImg;

    private GetImageCacheSizeAsync async;

    private SharedPreferencesUtil util;
    /** 改变了是否显示图片的监听*/
    private OnShowImgChange onShowImgChange;
    /** 菜单被点击的监听*/
    private OnMenuClick menuClick;

    private GdtAppwall appwall;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diskCache = ImageLoadIni.getImageLoad(getActivity()).getDiskCache();
        util = SharedPreferencesUtil.newInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu,container,false);
        iniView(view);
        return view;
    }

    @Override
    protected void iniView(View view) {
        clean = (Button) view.findViewById(R.id.clean);
        clean.setOnClickListener(this);

        view.findViewById(R.id.about).setOnClickListener(this);

        view.findViewById(R.id.exit).setOnClickListener(this);
        view.findViewById(R.id.ad).setOnClickListener(this);

        appwall = new GdtAppwall(getActivity(),"1104770080","4090502591434214", false);

        showImg = (Switch) view.findViewById(R.id.showImg);
        showImg.setChecked(util.getBoolean("showImg", true));
        showImg.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                util.setBoolean("showImg",checked);
                if(onShowImgChange != null){
                    onShowImgChange.onShowImg(checked);
                }
            }
        });
        super.iniView(view);
    }


    @Override
    protected void click(int id) {
        switch (id){
            case R.id.clean:
                diskCache.clear();
                clean.setText("清除图片缓存                0kb");
                break;
            case R.id.about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.exit:
                getActivity().finish();
                break;
            case R.id.ad:
                appwall.doShowAppWall();
                break;
            default:
                super.click(id);
                break;
        }
        if(menuClick != null){
            menuClick.onClick();
        }
    }

    private class GetImageCacheSizeAsync extends AsyncTask<Void,Void,Long> {

        @Override
        protected Long doInBackground(Void... params) {
            long all = 0;
            File floder = diskCache.getDirectory();
            if(floder != null){
                for(File file : floder.listFiles()){
                    if(file.isFile()){
                        all += file.length();
                    }
                }
            }
            return all;
        }

        @Override
        protected void onPostExecute(Long all) {
            clean.setText("清除图片缓存                "+all/1024+"kb");
        }
    }

    /**
     * 计算图片缓存
     */
    public void sumCacheSize(){
        if(async != null){
            async.cancel(true);
        }
        async = new GetImageCacheSizeAsync();
        async.execute();
    }

    public void setOnShowImgChange(OnShowImgChange onShowImgChange) {
        this.onShowImgChange = onShowImgChange;
    }

    public void setMenuClick(OnMenuClick menuClick) {
        this.menuClick = menuClick;
    }

    /**
     * 显示菜单改变时的监听
     */
    public interface OnShowImgChange{
        public void onShowImg(boolean isShow);
    }

    /**
     * 菜单被点击的监听，需要关闭右滑菜单
     */
    public interface OnMenuClick{
        public void onClick();
    }
}
