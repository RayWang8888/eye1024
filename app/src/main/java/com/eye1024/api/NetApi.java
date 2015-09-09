package com.eye1024.api;

import android.content.Context;

import com.raywang.rayutils.HttpCoreThread;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取网络数据的类，拥有多个静态获取数据的方法，但是会优先判断缓存是否过期，防止给服务器带来压力
 * 同时节省流量，主要是服务器文章更新速度本来也不快
 * Created by Ray on 2015/6/30.
 */
public class NetApi {


    private HashMap<String,Object> params = new HashMap<String,Object>();
    private ArrayList<String> removeKeys;
    /** 分类过期时间，24小时*/
    private static final long TYPEOVERDUETIME = 86400000;
    /** 文章过期时间，2小时*/
    private static final long ARTICLEOVERDUETIME = 7200000;




    /**
      * 获取文章的列表
      * @param isRef 是否刷新数据
     *  @param context 程序上下文
     *  @param listener 回调监听
     *  @param params 请求的参数
     *  @param removeKeys 需要移除的关键字
     *
      * @return
      */
    public static HttpCoreThread getArticleList(boolean isRef,Context context,
                                                HttpCoreThread.HttpListener listener,
                                                HashMap<String,Object> params,
                                                ArrayList<String> removeKeys){


        return new HttpCoreThread(context, ApiURL.HOST+ ApiURL.GETARTICLELIST,listener,params,removeKeys,
                "page",ARTICLEOVERDUETIME).get(0,isRef);
    }

    /**
     * 获取文章分类
     */
    public static HttpCoreThread getType(HttpCoreThread.HttpListener listener,Context context){

        return new HttpCoreThread(context, ApiURL.HOST + ApiURL.TYPE,listener,null,null,null,
                TYPEOVERDUETIME).get(0,false);
    }

    /**
     * 意见与建议
     * @param context
     * @param listener
     * @param params
     * @return
     */
    public static HttpCoreThread commend(Context context, HttpCoreThread.HttpListener listener,
                        HashMap<String,Object> params){
        String url = ApiURL.HOST+ ApiURL.COMMEND;
        return new HttpCoreThread(context,url,listener,params,null,null).post();

    }

    /**
     * 检测新版本
     * @param context
     * @param listener
     * @return
     */
    public static HttpCoreThread checkVersion(Context context,HttpCoreThread.HttpListener listener,
                                              int requestCode){
        return new HttpCoreThread(context, ApiURL.HOST+ ApiURL.CHECKVERSION,listener,
                null,null,null,ARTICLEOVERDUETIME).get(requestCode,false);
    }
}
