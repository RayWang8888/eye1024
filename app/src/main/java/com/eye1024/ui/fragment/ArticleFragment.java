package com.eye1024.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eye1024.R;
import com.eye1024.api.NetApi;
import com.eye1024.bean.ArticleResult;
import com.eye1024.db.ReadLogDao;
import com.eye1024.ui.adapter.RecyclerArticleAdapter;
import com.raywang.fragment.BaseFragment;
import com.raywang.rayutils.HttpCoreThread;
import com.raywang.rayutils.UIHlep;
import com.raywang.rayutils.Util;
import com.raywang.view.DividerItemDecoration;
import com.raywang.view.SwipeRefRecyclerView;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 菜单的Fragment
 * Created by Ray on 2015/6/29.
 */
public class ArticleFragment extends BaseFragment {

    private static final String TAG = ArticleFragment.class.getName();
    /** 当前第几页*/
    private int page = 1;
    /** 文章分类*/
    private int typeid;
    /** 每次获取的数量*/
    private static final int ROWS = 20;
    /** 下拉刷新，上拉加载更多的控件*/
    private SwipeRefRecyclerView refRecyclerView;
    /** 没有缓存时请求网络数据的进度圈控件*/
    private ProgressView progress;
    /** 最大页数*/
    private int maxPage = 1;
    /** 显示网络错误的消息的textView*/
    private TextView msg;

    private RecyclerArticleAdapter adapter;

    private View loadmore;

    private ReadLogDao logDao;


    private HttpCoreThread httpCoreThread;
    /** 请求的参数*/
    private HashMap<String,Object> params;
    private ArrayList<String> removeKeys ;

    private HttpCoreThread.HttpListener httpListener = new HttpCoreThread.HttpListener() {
        @Override
        public void onCacheData(int requestCode,String data) {
            if(Util.noNull(data)){


                ArticleResult articleResult = ArticleResult.parse(data);
                onDataResult(articleResult);
            }else{
                onDataResult(null);
            }
        }

        @Override
        public void onError(int requestCode,int code, String e) {
            onDataResult(null);
        }

        @Override
        public void onInternet(int requestCode,String data) {
            if(Util.noNull(data)){
                ArticleResult articleResult = ArticleResult.parse(data);
                onDataResult(articleResult);
            }else{
                onDataResult(null);
            }
        }
    };


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
          getData(false);
          onIniDataFinsh();
    }

    @Override
    public void onDestroy() {
        //停止异步任务
        if(httpCoreThread != null){
            httpCoreThread.setStop();
        }
        if(adapter != null){
            adapter = null;
        }
        super.onDestroy();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater,container,savedInstanceState, R.layout.fragment_article);
    }


    protected void iniView(View view){
        super.iniView(view);
        refRecyclerView = (SwipeRefRecyclerView) view.findViewById(R.id.articles);
        progress = (ProgressView) view.findViewById(R.id.progress);
        progress.start();
        msg = (TextView) view.findViewById(R.id.msg);

        refRecyclerView.setOnRefreshListener(new SwipeRefRecyclerView.OnSwipeRefRecyclerViewListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getData(true);
            }

            @Override
            public void onLoadMore() {
                if (page < maxPage) {
                    getData(false);
                }
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        refRecyclerView.getRecyclerView().setHasFixedSize(true);
        refRecyclerView.setLayoutManager(llm);
        refRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        refRecyclerView.getRecyclerView().getItemAnimator().setAddDuration(1000);
        refRecyclerView.getRecyclerView().addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));



    }

    /**
     * 获取数据
     */
    private void getData(boolean isRef){

        if(params == null){
            params = new HashMap<String,Object>();
            removeKeys = new ArrayList<String>();
            removeKeys.add("token");
            removeKeys.add("page");
        }

        params.put("type",typeid);
        params.put("page",page+1);
        params.put("rows",ROWS);
        httpCoreThread = NetApi.getArticleList(isRef, getActivity(), httpListener, params,
                removeKeys);
    }


    private void onDataResult(ArticleResult articleResult){
        refRecyclerView.setRefreshing(false);
        refRecyclerView.loadingFinish();
        if(articleResult == null){
            //连接服务器失败了
            if(adapter == null || adapter.getItemCount() == 0) {
                progress.setVisibility(View.GONE);
                refRecyclerView.setVisibility(View.GONE);
                msg.setVisibility(View.VISIBLE);
                msg.setOnClickListener(ArticleFragment.this);
            }else{
                UIHlep.toast(getActivity(), R.string.connet_error);
            }
        }else{
            if(Util.noNull(articleResult.getErrcode())){
                //有错误产生
                UIHlep.toast(getActivity(), articleResult.getErrmsg());
                progress.setVisibility(View.GONE);
                refRecyclerView.setVisibility(View.GONE);
                msg.setVisibility(View.VISIBLE);
                if("2".equals(articleResult.getErrcode())){
                    msg.setText(articleResult.getErrmsg());
                }else{
                    msg.setText(articleResult.getErrmsg()+",点击重试");
                    msg.setOnClickListener(ArticleFragment.this);
                }
            }else{
                //获取已经查看过的文章

                page ++;
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
                msg.setVisibility(View.GONE);
                refRecyclerView.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                if(adapter == null){
                    adapter = new RecyclerArticleAdapter(getActivity(),articleResult.getData());
                    adapter.setReadLog(logs);
                    refRecyclerView.setAdapter(adapter);
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
       if(adapter != null){
           adapter.addReadUrl(url);
       }
   }

    @Override
    protected void click(int id) {
        switch (id){
            case R.id.msg:
                getData(false);
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
