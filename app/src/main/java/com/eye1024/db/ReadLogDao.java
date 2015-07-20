package com.eye1024.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 已经查看过的文章的记录表相关操作
 * Created by Ray on 2015/7/17.
 */
public class ReadLogDao extends DataBase{
    /** 10天的毫秒数  864000000*/
    private static final long OVERTIME = 864000000;
    public ReadLogDao(Context context) {
        super(context);
    }

    /**
     * 缓存已经阅读过的文章记录
     * @param url
     */
    public void cacheReadLog(String url){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        db.execSQL("delete from readlog where url=?",new Object[]{url});
        db.execSQL("insert into readlog(url,lasttime) values(?,?)",new Object[]{url,
            System.currentTimeMillis()});

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 获取当前网址集合中哪些是被查看过的
     * @param urls
     * @return
     */
    public ArrayList<String> findIsRead(ArrayList<String> urls){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> list = new ArrayList<String>();
        String sql = "select url from readlog where url in(";
        for(String url : urls){
            sql += "'"+url+"',";
        }
        sql = sql.substring(0,sql.length() - 1);
        sql += ")";
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            list.add(cursor.getString(0));
        }

        return list;
    }

    /**
     * 删除10天之前的数据，避免占用空间
     */
    public void deleteLog(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from readlog where lasttime <?",
                new Object[]{System.currentTimeMillis() - OVERTIME});
    }
}
