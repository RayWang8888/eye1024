package com.eye1024.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eye1024.R;
import com.eye1024.api.NetApi;
import com.eye1024.bean.ArticleResult;
import com.eye1024.db.ReadLogDao;
import com.eye1024.ui.adapter.ArticleAdapter;
import com.raywang.fragment.BaseFragment;
import com.raywang.rayutils.UIHlep;
import com.raywang.rayutils.Util;
import com.raywang.view.SwipeRefListView;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

/**
 * 菜单的Fragment
 * Created by Ray on 2015/6/29.
 */
public class ArticleFragment extends BaseFragment{

    private static final String TAG = ArticleFragment.class.getName();
    /** 当前第几页*/
    private int page = 1;
    /** 文章分类*/
    private int typeid;
    /** 每次获取的数量*/
    private static final int ROWS = 20;
    /** 下拉刷新，上拉加载更多的控件*/
    private SwipeRefListView refListView;
    /** 没有缓存时请求网络数据的进度圈控件*/
    private ProgressView progress;
    /** 最大页数*/
    private int maxPage = 1;
    /** 获取文章的异步任务*/
    private GetArticleAsync async;
    /** 显示网络错误的消息的textView*/
    private TextView msg;

    private ArticleAdapter adapter;

    private View loadmore;

    private ReadLogDao logDao;

    /** 是否是第一次获取数据*/
    private boolean isFristGetData = true;

    public static ArticleFragment newInstance(int typeid){
        ArticleFragment af = new ArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("typeid",typeid);
        af.setArguments(bundle);
        return af;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        typeid = getArguments().getInt("typeid");
        logDao = new ReadLogDao(getActivity());

    }


    /**
     * 获取数据的方法，如果异步任务对象Async不是空，说明已经在获取了
     */
    protected void iniData(){
        if(async == null){
            async = new GetArticleAsync(false);
            async.execute();
        }
    }

    @Override
    public void onDestroy() {
        //停止异步任务
        if(async != null){
            async.cancel(true);
            async = null;
            page = 1;
        }
        if(adapter != null){
            adapter = null;
        }
        isFristGetData = true;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        //停止异步任务
        if(async != null){
            async.cancel(true);
            async = null;
            page = 1;
        }
        if(adapter != null){
            adapter = null;
        }
        isFristGetData = true;
        super.onDestroyView();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article,container,false);

        iniView(view);
        return view;
    }


    protected void iniView(View view){
        super.iniView(view);
        refListView = (SwipeRefListView) view.findViewById(R.id.articles);
        progress = (ProgressView) view.findViewById(R.id.progress);
        progress.start();
        msg = (TextView) view.findViewById(R.id.msg);

        refListView.setOnrefListener(new SwipeRefListView.OnRefListener() {
            @Override
            public void onLastShow() {
                if(page < maxPage){
                    page ++;
                    getData(false);
                }
            }

            @Override
            public void onRefresh() {
                page = 1;
                getData(true);
            }
        });

//        refListView.getListView().getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        refListView.getListView().computeScrollY();
//                    }
//                });

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        loadmore = inflater.inflate(R.layout.item_loadingmore,null);
        ((ProgressView)loadmore.findViewById(R.id.progress)).start();
        refListView.setFooter(loadmore);
    }

    /**
     * 获取数据
     */
    private void getData(boolean isRef){
        if(async != null){
            async.cancel(true);
        }
        async = new GetArticleAsync(isRef);
        async.execute();
    }
    /**
     * 获取文章的异步任务
     */
    private class GetArticleAsync extends AsyncTask<Void,Void,ArticleResult>{
        private boolean isRef = false;

        private GetArticleAsync(boolean isRef) {
            this.isRef = isRef;
        }


        @Override
        protected ArticleResult doInBackground(Void... params) {
            return NetApi.getArticleList(getActivity(),typeid,page,ROWS,isRef);
        }

        @Override
        protected void onPostExecute(ArticleResult articleResult) {
            refListView.setRefreshing(false);
            refListView.onLoadFinish();
            if(articleResult == null){
                //连接服务器失败了
                progress.setVisibility(View.GONE);
                refListView.setVisibility(View.GONE);
                msg.setVisibility(View.VISIBLE);
                msg.setOnClickListener(ArticleFragment.this);
            }else{
                if(Util.noNull(articleResult.getErrcode())){
                    //有错误产生
                    UIHlep.toast(getActivity(),articleResult.getErrmsg());
                    progress.setVisibility(View.GONE);
                    refListView.setVisibility(View.GONE);
                    msg.setVisibility(View.VISIBLE);
                    if("2".equals(articleResult.getErrcode())){
                        msg.setText(articleResult.getErrmsg());
                    }else{
                        msg.setText(articleResult.getErrmsg()+",点击重试");
                        msg.setOnClickListener(ArticleFragment.this);
                    }
                }else{
                    //获取已经查看过的文章
                    ArrayList<String> urls = new ArrayList<String>();
                    for(ArticleResult.Article article : articleResult.getData()){
                        urls.add(article.getUrl());
                    }
                    ArrayList<String> logs = logDao.findIsRead(urls);

                    //获取成功，计算最大页数
                    maxPage = articleResult.getCount() / ROWS;
                    if(articleResult.getCount() % ROWS != 0){
                        maxPage ++;
                    }
                    progress.setVisibility(View.GONE);
                    msg.setVisibility(View.GONE);
                    refListView.setVisibility(View.VISIBLE);
                    if(adapter == null){
                        adapter = new ArticleAdapter(getActivity(),articleResult.getData());
                        adapter.setReadLog(logs);
                        refListView.setAdapter(adapter);
                    }else{
                        if(page == 1){
                            adapter.setReadLog(logs);
                            adapter.setData(articleResult.getData());
                        }else{
                            adapter.addReadLog(logs);
                            adapter.addData(articleResult.getData());
                        }
                    }
                    //计算高度，用于获取listView的滚动位置
                    refListView.getListView().computeScrollY();
                }
            }
        }
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            addReadLog(data.getStringExtra("url"));
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

   public void addReadLog(String url){
       logDao.cacheReadLog(url);
       if(adapter != null){
           adapter.addReadUrl(url);
       }
   }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser && isFristGetData){
            isFristGetData = false;
            iniData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void click(int id) {
        switch (id){
            case R.id.msg:
                if(async != null){
                    async.cancel(true);
                    async = new GetArticleAsync(false);
                    async.execute();
                }
                progress.setVisibility(View.VISIBLE);
                msg.setVisibility(View.GONE);
                break;
            default:
                super.click(id);
                break;
        }
    }

    public void showImgChange(boolean isShowImg){
        if(adapter != null) {
            adapter.setShowImg(isShowImg);
        }
    }
}
