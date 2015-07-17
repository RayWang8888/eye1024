package com.eye1024.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作的基类，主要是创建基本的缓存表以及基础数据
 * 根据lasttime来判断是否需要重新获取数据
 * Created by Ray on 2015/6/29.
 */
public class DataBase extends SQLiteOpenHelper {

    /** 数据库文件的名称*/
    private final static String DBNAME = "data.db";

    private final static int VERSION = 1;

    public DataBase(Context context){
        super(context,DBNAME,null,VERSION);
    }

    /** 缓存过期时间目前为2个小时*/
    public final static long OVERDUETIME = 7200000;

    @Override
    public void onCreate(SQLiteDatabase db) {
        //分类的数据库缓存表，也会根据上次的获取时间来判断是否需要重新从服务器获取数据
        db.execSQL("create table if not exists type(id int,name varchar(10))");
        //缓存的文章数据的表context 接口返回的json字符，URL地址(不带page的参数，不然不好控制)，
        // lasttime 返回的时间，page 页面索引，如果当前URL下page=3进行了插入操作，那么当前URL下的
        // page>3都要删除，防止数据错乱，所以URL不能带page参数
        db.execSQL("create table if not exists article(content text,url varchar(200)," +
                "lasttime varchar(20),page int)");
        //插入分类的初始数据
        db.execSQL("insert into type(id,name) values(1,'文章')");
        db.execSQL("insert into type(id,name) values(2,'新闻')");
        db.execSQL("insert into type(id,name) values(3,'安全')");
        db.execSQL("insert into type(id,name) values(4,'人生')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //删除分类的缓存数据
        db.execSQL("drop table if exists type");
        //删除文章的数据表
        db.execSQL("drop table if exists article");
        onCreate(db);
    }
}
