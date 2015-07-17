package com.raywang.interfaces;

/**
 * 用了ImageLoader加载图片的数据适配器接口
 * 用来暂停和
 * Created by Ray on 2015/7/2.
 */
public interface ImageLoaderAdapter {

    /** AdapterView 滚动时调用的方法*/
    public void onScroll();

    /** AdapterView 滚动结束时调用的方法*/
    public void onScrollEnd();

}
