package com.eye1024.api;

import android.content.Context;

import com.eye1024.bean.ArticleResult;
import com.eye1024.bean.TypeResult;
import com.eye1024.db.ArticleDao;
import com.eye1024.db.TypeDao;
import com.raywang.rayutils.HttpUtil;
import com.raywang.rayutils.SharedPreferencesUtil;
import com.raywang.rayutils.Util;

import java.util.ArrayList;

/**
 * 获取网络数据的类，拥有多个静态获取数据的方法，但是会优先判断缓存是否过期，防止给服务器带来压力
 * 同时节省流量，主要是服务器文章更新速度本来也不快
 * Created by Ray on 2015/6/30.
 */
public class NetApi {

    /** 过期时间，24小时*/
    private static final long TYPEOVERDUETIME = 86400000;

    /**
     * 获取分类数据，并缓存到本地，需要更新本地的缓存时间
     * @param context
     * @return 返回错误信息
     */
    public static String getType(Context context){
        SharedPreferencesUtil util = SharedPreferencesUtil.newInstance(context);
        //判断是否需要从服务器获取数据
        if(util.getLong(SettingName.TYPETIME) + TYPEOVERDUETIME > System.currentTimeMillis() ){
            //小于过期时间，就不获取
            return null;
        }
        TypeResult result = TypeResult.parse(HttpUtil.get(ApiURL.HOST+ApiURL.TYPE,null));
        if(result != null && result.getData() != null && result.getData().size() > 0){
            //缓存数据
            new TypeDao(context).insert(result.getData());
            //更新缓存时间
            util.setLong(SettingName.TYPETIME,System.currentTimeMillis());
            return null;
        }
        if(result == null){
            return "连接服务器失败";
        }
        return result.getErrmsg();
    }

    /**
     * 获取文章的列表
     * @param context
     * @param typeid 分类的id
     * @param page 当前页面索引
     * @param rows 每页显示的数据量
     * @param isRef 是否刷新数据
     * @return
     */
    public static ArticleResult getArticleList(Context context,int typeid,
                                                                  int page,int rows,boolean isRef){
        String url = ApiURL.HOST+ApiURL.GETARTICLELIST+"?type="+typeid+"&rows="+rows;
        ArticleDao dao = new ArticleDao(context);
        String data = dao.findByUrl(url,page);
        if(data == null || isRef){
            data = HttpUtil.get(url+"&page="+page,null);
            //缓存数据
            dao.insert(data,url,page);
            Util.logi("info","from internet");
        }else{
            Util.logi("info","from data");
        }

        ArticleResult result = ArticleResult.parse(data);

        return result;
    }

}
