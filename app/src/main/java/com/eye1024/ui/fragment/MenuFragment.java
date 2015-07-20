package com.eye1024.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eye1024.R;
import com.eye1024.api.SettingName;
import com.eye1024.ui.AboutActivity;
import com.eye1024.ui.MainActivity;
import com.eye1024.util.ImageLoadIni;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.raywang.fragment.BaseFragment;
import com.raywang.rayutils.SharedPreferencesUtil;
import com.raywang.rayutils.Util;
import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;

import java.io.File;

/**
 * 菜单的Fragment
 * Created by Administrator on 2015/6/26.
 */
public class MenuFragment extends BaseFragment {

    private Button clean;
    private DiskCache diskCache = null;
    private Switch showImg;
    private Switch isBlack;
    private GetImageCacheSizeAsync async;
    private SharedPreferencesUtil util;
    /**
     * 改变了是否显示图片的监听
     */
    private OnShowImgChange onShowImgChange;

    /**
     * 菜单被点击的监听
     */
    private OnMenuClick menuClick;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diskCache = ImageLoadIni.getImageLoad(getActivity()).getDiskCache();
        util = SharedPreferencesUtil.newInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
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
        view.findViewById(R.id.evaluation).setOnClickListener(this);
        AdManager.getInstance(getActivity()).init("9d06deb924971b87", "3ae12aefb372e962");

        OffersManager.getInstance(getActivity()).onAppLaunch();
        showImg = (Switch) view.findViewById(R.id.showImg);
        showImg.setChecked(util.getBoolean(SettingName.ISSHOWIMG, true));
        showImg.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                util.setBoolean(SettingName.ISSHOWIMG, checked);
                if (onShowImgChange != null) {
                    onShowImgChange.onShowImg(checked);
                }
            }
        });

        isBlack = (Switch) view.findViewById(R.id.isBlack);
        isBlack.setChecked(util.getBoolean(SettingName.ISBLACK,false));
        isBlack.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                view.setEnabled(false);
                util.setBoolean(SettingName.ISBLACK,checked);
                if(checked){
                    getActivity().setTheme(R.style.BlackMainActivityTheme);
                }else{
                    getActivity().setTheme(R.style.MainActivityTheme);
                }

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                view.setEnabled(true);
            }
        });

        super.iniView(view);
    }


    @Override
    protected void click(int id) {
        switch (id) {
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
                ///86400000
                OffersManager.getInstance(getActivity()).showOffersWall(null);
                break;
            case R.id.evaluation:

                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1.createChooser(intent1, "选择市场"));

                break;
            default:
                super.click(id);
                break;
        }
        if (menuClick != null) {
            menuClick.onClick();
        }
    }

    /**
     * 计算图片缓存
     */
    public void sumCacheSize() {
        if (async != null) {
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
    public interface OnShowImgChange {
        public void onShowImg(boolean isShow);
    }




    /**
     * 菜单被点击的监听，需要关闭右滑菜单
     */
    public interface OnMenuClick {
        public void onClick();
    }

    private class GetImageCacheSizeAsync extends AsyncTask<Void, Void, Long> {

        @Override
        protected Long doInBackground(Void... params) {
            long all = 0;
            File floder = diskCache.getDirectory();
            if (floder != null) {
                for (File file : floder.listFiles()) {
                    if (file.isFile()) {
                        all += file.length();
                    }
                }
            }
            return all;
        }

        @Override
        protected void onPostExecute(Long all) {
            clean.setText("清除图片缓存                " + all / 1024 + "kb");
        }
    }
}
