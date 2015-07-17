package com.eye1024.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eye1024.bean.TypeResult;

import java.sql.Types;
import java.util.ArrayList;

/**
 * 分类的数据库操作类
 * Created by Ray on 2015/6/29.
 */
public class TypeDao extends DataBase{

    /** 分类缓存过期的时间*/
    private static final long TYPEOVERDUE = 86400000;

    private Context context;
    public TypeDao(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 查询所有分类
     * @return
     */
    public ArrayList<TypeResult.Type> findAll(){
        ArrayList<TypeResult.Type> types = new ArrayList<TypeResult.Type>();
        types.add(new TypeResult.Type(0,"最新"));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select id,name from type",null);
        while(cursor.moveToNext()){
            types.add(new TypeResult.Type(cursor.getInt(0),cursor.getString(1)));
        }
        cursor.close();
        db.close();
        return types;
    }

    /**
     * 缓存分类信息
     * @param types
     */
    public void insert(ArrayList<TypeResult.Type> types){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from type");
        for(TypeResult.Type type : types){
            if(type.getParam1() == null) {
                //是文章的分类
                db.execSQL("insert into type(id,name) values(?,?)",
                        new Object[]{type.getId(), type.getName()});
            }
        }
        db.close();
    }
}
