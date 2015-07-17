package com.eye1024.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * 文章的缓存数据库操作类
 * Created by Ray on 2015/6/29.
 */
public class ArticleDao extends DataBase{

    public ArticleDao(Context context) {
        super(context);
    }

    /**
     * 缓存文章
     * @param content json数据
     * @param url 请求的网址，不带page参数
     */
    public void insert(String content,String url,int page){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        //删除之前的数据
        db.execSQL("delete from article where url=? and page=?",new Object[]{url,page});
        db.execSQL("insert into article(content,url,lasttime,page) values(?,?,?,?)",
                new Object[]{content,url,System.currentTimeMillis(),page});
        //防止数据错乱，删除后面的数据
        db.execSQL("delete from article where url=? and page>?",new Object[]{url,page});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * 查询数据
     * @param url 请求的URL，不带page参数
     * @param page page参数
     * @return
     */
    public String findByUrl(String url,int page){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select content from article where url=? and page=?" +
                " and lasttime>?",new String[]{url,""+page,""+
                (System.currentTimeMillis()-OVERDUETIME)});
        if(cursor.moveToNext()){
            return cursor.getString(0);
        }

        return null;
    }
}
