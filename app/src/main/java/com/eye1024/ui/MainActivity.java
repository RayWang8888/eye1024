package com.eye1024.ui;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.eye1024.R;
import com.eye1024.api.NetApi;
import com.eye1024.api.SettingName;
import com.eye1024.bean.TypeResult;
import com.eye1024.db.ReadLogDao;
import com.eye1024.db.TypeDao;
import com.eye1024.ui.fragment.ArticleFragment;
import com.eye1024.ui.fragment.MenuFragment;
import com.eye1024.view.drawerlib.ActionBarDrawerToggle;
import com.eye1024.view.drawerlib.DrawerArrowDrawable;
import com.raywang.activity.BaseActivity;
import com.raywang.rayutils.SharedPreferencesUtil;
import com.raywang.rayutils.UIHlep;
import com.raywang.rayutils.Util;
import com.rey.material.widget.TabPageIndicator;

import net.youmi.android.offers.OffersManager;

import java.util.ArrayList;

/**
 * 主界面activity
 * @author Ray Wang
 * @date 2015年6月26日14:47:25
 * @version 1.0
 */
public class MainActivity extends BaseActivity {

    /** 抽屉控件*/
    private DrawerLayout drawerLayout;
    /** 滑出来的菜单FrameLayout*/
    private View mDrawerMenu;
    /** 动画的2个组件*/
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    /** 菜单的Fragment*/
    private MenuFragment menuFragment;

    /** 分类页面的adapter*/
    private PagerAdapter adapter;
    /**分类的数据*/
    private ArrayList<TypeResult.Type> types;
    /** 分类的数据库操作类对象*/
    private TypeDao typeDao;

    /** 分类的tab*/
    private TabPageIndicator ti;
    /** 分类的ViewPager*/
    private ViewPager pager;

    private ArticleFragment[] fragments;

    private GetTypeAsync async;

    private long lastTime = 0;
    private SharedPreferencesUtil util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        util = SharedPreferencesUtil.newInstance(this);
        if(util.getBoolean(SettingName.ISBLACK,false)){
            setTheme(R.style.BlackMainActivityTheme);
        }
        super.onCreate(savedInstanceState, R.layout.activity_main);
        new ReadLogDao(this).deleteLog();
    }


    @Override
    protected void iniHead() {
        //设置bar为自定义的bar
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.head);

        //设置bar上面的按钮的监听
        View view = bar.getCustomView();
//        view.findViewById(R.id.img).setOnClickListener(this);
        view.findViewById(android.R.id.home).setOnClickListener(this);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerMenu = findViewById(R.id.leftMenu);

        //主要设置图片旋转的动画
        drawerArrow = new DrawerArrowDrawable(this) {

            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                drawerArrow, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(menuFragment != null) {
                    menuFragment.sumCacheSize();
                }
                invalidateOptionsMenu();
            }


        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.closeDrawer(mDrawerMenu);
        mDrawerToggle.syncState();
        super.iniHead();
    }

    @Override
    protected void iniView() {
        iniFragment();
        pager = (ViewPager) findViewById(R.id.pager);
        ti = (TabPageIndicator) findViewById(R.id.main_tpi);
        iniData();
        if(types != null){
            adapter = new PagerAdapter(getFragmentManager());
            pager.setAdapter(adapter);
            ti.setViewPager(pager);
            pager.setOffscreenPageLimit(types.size());
        }
        super.iniView();
    }

    /**
     * 初始化数据
     */
    private void iniData(){
        //查询出分类信息
        typeDao = new TypeDao(this);
        types = typeDao.findAll();
        fragments = new ArticleFragment[types.size()];

        if(util.getLong(SettingName.TYPETIME) < System.currentTimeMillis() - TypeDao.OVERDUETIME){
            //分类数据超过1天了，需要重新获取
            async = new GetTypeAsync();
            async.execute();
        }
    }

    /**
     * 初始化Fragment，主要是菜单
     */
    protected void iniFragment(){
        FragmentManager fm = getFragmentManager();
        menuFragment = new MenuFragment();
        menuFragment.setOnShowImgChange(new MenuFragment.OnShowImgChange() {
            @Override
            public void onShowImg(boolean isShow) {
                for(ArticleFragment fragment : fragments){
                    if(fragment != null){
                        fragment.showImgChange(isShow);
                    }
                }
            }
        });
        menuFragment.setMenuClick(new MenuFragment.OnMenuClick() {
            @Override
            public void onClick() {
                if (drawerLayout.isDrawerOpen(mDrawerMenu)) {
                    drawerLayout.closeDrawer(mDrawerMenu);
                }
            }
        });
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.leftMenu, menuFragment);
        ft.commit();
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void click(int id) {
        switch (id) {
            case android.R.id.home:
            case R.id.img:
                if (drawerLayout.isDrawerOpen(mDrawerMenu)) {
                    drawerLayout.closeDrawer(mDrawerMenu);
                } else {
                    drawerLayout.openDrawer(mDrawerMenu);
                }
                break;

            default:
                super.click(id);
                break;
        }
    }



    /**
     * 文章分类的Page的适配器
     */
    private class PagerAdapter extends FragmentPagerAdapter {


        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if(types != null){
                return types.size();
            }
            return 0;
        }



        @Override
        public Fragment getItem(int i) {
            if(fragments[i] == null){
                fragments[i] = ArticleFragment.newInstance(types.get(i).getId());
            }
            return fragments[i];
        }




        @Override
        public CharSequence getPageTitle(int position) {
            return types.get(position).getName();
        }
    }

    /**
     * 获取分类的异步任务，获取完成之后，下次启动才起作用
     */
    private class GetTypeAsync extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            return NetApi.getType(MainActivity.this);
        }

        @Override
        protected void onPostExecute(String s) {
            if(Util.noNull(s)){
                UIHlep.toast(MainActivity.this,s);
            }
        }
    }

    @Override
    protected void onDestroy() {
        OffersManager.getInstance(this).onAppExit();
        adapter = null;
        pager = null;
        menuFragment = null;
        if(async != null){
            async.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragments[pager.getCurrentItem()].onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        long time = System.currentTimeMillis();
        if(time - 2000 > lastTime){
            lastTime = time;
            UIHlep.toast(this,R.string.exit_alert);
//            Snackbar.make(pager,R.string.exit_alert,Snackbar.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }


}
