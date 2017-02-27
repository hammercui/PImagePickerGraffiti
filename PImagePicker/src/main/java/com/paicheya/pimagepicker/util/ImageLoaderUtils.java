package com.paicheya.pimagepicker.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.paicheya.pimagepicker.R;

import java.io.File;


public class ImageLoaderUtils {


    public static DisplayImageOptions imageDisplayOptions = new DisplayImageOptions.Builder()//
            .showImageOnLoading(R.drawable.pip_default_image)         //设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.drawable.pip_default_image)       //设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.pip_default_image)            //设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)                                //设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                                  //设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565) // ARGB_8888  RGB_565
            .build();

    /**
     * 初始化ImageLoader的配置
     *
     * @param context
     */
    public static void initImageLoaderConfig(Context context) {

        File cacheFile = context.getCacheDir();
//        Log.i("cacheFile", cacheFile.getAbsolutePath());
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
            config.threadPoolSize(3);//default
            //config.memoryCacheExtraOptions(480, 800);// default = device screen dimensions
            config.threadPriority(Thread.NORM_PRIORITY - 2);
            config.denyCacheImageMultipleSizesInMemory();
            config.memoryCache(new LRULimitedMemoryCache(2 * 1024 * 1024));
            config.memoryCacheSize(4 * 1024 * 1024);
            config.memoryCacheSizePercentage(13); // default
            config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
            config.diskCache(new UnlimitedDiskCache(cacheFile)); //default
            config.diskCacheSize(100 * 1024 * 1024);
            config.diskCacheFileCount(10);
            config.tasksProcessingOrder(QueueProcessingType.LIFO).build();

            ImageLoaderConfiguration config1 = config.build();
            ImageLoaderConfiguration config2 = ImageLoaderConfiguration.createDefault(context);
            // Initialize ImageLoaderUtils with configuration.
            ImageLoader.getInstance().init(config2);
        }
    }
}
