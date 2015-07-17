package com.eye1024.util;

import android.content.Context;

import com.eye1024.app.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * 网络图片下载并缓存的Image_Loader的初始化类
 * @author Ray
 * @date 2015年7月1日10:36:28
 * @version 1.0
 */
public class ImageLoadIni {

    private static DisplayImageOptions options;

    private static ImageLoaderConfiguration config;

    private static void iniConfig(Context context){


        config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory().
                diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
    }

    private static void iniOption(){
        options = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.ic_empty)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_empty)
                .cacheInMemory(true)
                .cacheOnDisk(true)
//                .cacheInMemory()
//                .cacheOnDisc()
                .displayer(new RoundedBitmapDisplayer(0))
                .build();
    }

    public static ImageLoader getImageLoad(Context context){
        if(config == null){
            iniConfig(context);
        }
        ImageLoader loader = ImageLoader.getInstance();

        loader.init(config);

        return loader;
    }

    public static DisplayImageOptions getOption(){
        if(options == null){
            iniOption();
        }
        return options;
    }
}
