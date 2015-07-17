package com.raywang.rayutils;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * SharedPreferences保存一些简单的设置的工具类
 * @author Ray
 * @date 2014年11月27日10:55:53
 * @version 1.0
 */
public class SharedPreferencesUtil {
    /** 用于单例模式*/
    private static SharedPreferencesUtil util;
    /** SharedPreferences对象*/
    private SharedPreferences shared;
    /** SharedPreferences的写入对象*/
    private Editor editor;

    private SharedPreferencesUtil(Context context){
        shared = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = shared.edit();
    }

    /**
     * 获取SharedPreferencesUtil对象（单例模式）
     * @param context
     * @return
     */
    public static SharedPreferencesUtil newInstance(Context context){
        if(util == null){
            util = new SharedPreferencesUtil(context);
        }
        return util;
    }


    /**
     * 设置一个boolean值
     * @param key 键
     * @param value 值
     */
    public void setBoolean(String key,boolean value){
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取保存的boolean值
     * @param key 键
     * @return 保存的值，如果没有保存则是false
     */
    public boolean getBoolean(String key){
        return shared.getBoolean(key, false);
    }

    /**
     * 获取保存的boolean值
     * @param key 键
     * @return 保存的值，如果没有保存则是false
     */
    public boolean getBoolean(String key,boolean defaults){
        return shared.getBoolean(key, defaults);
    }

    /**
     * 设置一个字符串
     * @param key 键
     * @param value 值
     */
    public void setString(String key,String value){
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key){
        return shared.getString(key, "");
    }

    public void setInt(String key,int value){
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key){
        return shared.getInt(key,0);
    }

    public int getInt(String key,int defValue){
        return shared.getInt(key, defValue);
    }

    public long getLong(String key){
        return shared.getLong(key,0);
    }

    public long getLong(String key,long defVal){
        return shared.getLong(key,defVal);
    }

    public void setLong(String key,long value){
        editor.putLong(key,value);
        editor.commit();
    }


    public String getGUID(){
        String guid = shared.getString("guid", "");
        if(guid.isEmpty()){
            guid = UUID.randomUUID().toString();
            editor.putString("guid", guid.replace("-", ""));
            editor.commit();
        }
        return guid;
    }
}
