package com.raywang.rayutils;

import android.content.res.Resources;
import android.util.Log;

/**
 * 常用的一些操作的工具类
 * Created by Ray Wang on 2015/6/21.
 */
public class Util {
    private static final boolean isDebug = true;
    /**
     * 验证字符串是否为空
     * @param str
     * @return
     */
    public static boolean noNull(String str){
        if(str == null || str.isEmpty()){
            return false;
        }
        return true;
    }

    public static void logi(String tag,String v){
        if(isDebug)
            Log.i(tag,v);
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
