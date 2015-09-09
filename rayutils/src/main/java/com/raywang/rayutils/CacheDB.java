package com.raywang.rayutils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ray on 2015/8/4.
 */
public class CacheDB extends SQLiteOpenHelper {
    /** 数据库文件的名称*/
    private final static String DBNAME = "cache.db";

    private final static int VERSION = 1;
    private static CacheDB cacheDB;

    private CacheDB(Context context){
        super(context,DBNAME,null,VERSION);
    }

    public synchronized static CacheDB newInstance(Context context){

        if(cacheDB == null){
            cacheDB = new CacheDB(context);
        }
        return cacheDB;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists httpcache(content text,url text," +
                "lasttime varchar(20),page int)");
        db.execSQL("create table if not exists downLog(url varchar(200),thread integer,"
                + "whereleng varchar(20))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists httpcache");
        db.execSQL("delete table if exists downLog");
        onCreate(db);
    }

    /**
     * 缓存文章
     * @param content json数据
     * @param url 请求的网址，不带page参数
     */
    public synchronized void insert(String content,String url,int page){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        //删除之前的数据
        db.execSQL("delete from httpcache where url=? and page=?",new Object[]{url,page});
        db.execSQL("insert into httpcache(content,url,lasttime,page) values(?,?,?,?)",
                new Object[]{content,url,System.currentTimeMillis(),page});
        //防止数据错乱，删除后面的数据
        db.execSQL("delete from httpcache where url=? and page>?",new Object[]{url,page});
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
    public synchronized String findByUrl(String url,int page){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select content from httpcache where url=? and page=?"
                ,new String[]{url,""+page});
        if(cursor.moveToNext()){
            return cursor.getString(0);
        }
        cursor.close();
        db.close();
        return null;
    }

    /**
     * 查询数据
     * @param url 请求的URL，不带page参数
     * @param page page参数
     * @return
     */
    public synchronized boolean findByUrl(String url,int page,long overdueTime){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(url) from httpcache where url=? and page=?" +
                " and lasttime>?",new String[]{url,""+page,""+
                (System.currentTimeMillis()-overdueTime)});
        if(cursor.moveToNext()){
            return cursor.getInt(0)>0;
        }
        cursor.close();
        db.close();
        return false;
    }



    ///////////////////////////多线程下载的/////////////////////
    /**
     * 获取上次下载的进度
     * @param url
     * @return
     */
    public synchronized ConcurrentHashMap<Integer, Long> findLog(String url){
        SQLiteDatabase db = getReadableDatabase();
        ConcurrentHashMap<Integer, Long> map = new ConcurrentHashMap<Integer, Long>();

        Cursor cursor = db.rawQuery("select thread,whereleng from downLog where url=?", new String[]{url});
        while(cursor.moveToNext()){
            map.put(cursor.getInt(0), cursor.getLong(1));
        }
        cursor.close();
        db.close();
        return map;
    }

    /**
     * 下载完成后删除记录
     * @param url
     */
    public synchronized void deleteByUrl(String url){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from downLog where url=?",new String[]{url});
        db.close();
    }

    /**
     * 初始化下载进度
     * @param url
     * @param threadSize
     * @return
     */
    public synchronized boolean iniDown(String url,int threadSize){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.execSQL("delete from downLog where url=?",new String[]{url});
        for(int i = 0;i < threadSize ; i++){
            db.execSQL("insert into downLog(url,thread,whereleng) values(?,?,?)",
                    new Object[]{url,i,0});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public synchronized void update(String url,int threadId,long where){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update downLog set whereleng=? where url=? and thread=?",
                new Object[]{where,url,threadId});
        db.close();
    }
}
