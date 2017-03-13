package com.paicheya.hammer.demo.imageloader;

import android.content.Context;

import com.hammer.anlib.pandroidutils.MyLog;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;


public class ImageLoaderUtils {

    /**
     * UTI的属性
     */
    public static DisplayImageOptions imageDisplayOptions = new DisplayImageOptions.Builder()//
            //.showImageOnLoading(R.drawable.pip_default_image)         //设置图片在下载期间显示的图片
            .showImageForEmptyUri(com.paicheya.pimagepicker.R.drawable.pip_default_image)       //设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(com.paicheya.pimagepicker.R.drawable.pip_default_image)            //设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)                                //设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                                  //设置下载的图片是否缓存在SD卡中
            //.bitmapConfig(Bitmap.Config.RGB_565) // ARGB_8888  RGB_565
            .build();

    public static void initConfig(Context context){
        initImageLoaderConfig(context);
    }

    /**
     * 初始化UIL的配置
     *
     * @param context
     */
    public static void initImageLoaderConfig(Context context) {
        File cacheFile = context.getCacheDir().getAbsoluteFile();
        MyLog.D("cacheFile:"+cacheFile.getAbsolutePath());
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config1 = new ImageLoaderConfiguration.Builder(context)
            .memoryCacheExtraOptions(480, 800)// default = device screen dimensions
            .diskCacheExtraOptions(480, 800, null)
            .threadPoolSize(3) //default
            .threadPriority(Thread.NORM_PRIORITY - 2) //default
            .tasksProcessingOrder(QueueProcessingType.FIFO)  //default
             .denyCacheImageMultipleSizesInMemory() //
            .memoryCache(new LRULimitedMemoryCache(4 * 1024 * 1024))
            .memoryCacheSize(4 * 1024 * 1024)
            .memoryCacheSizePercentage(13) // default
            .diskCache(new UnlimitedDiskCache(cacheFile)) //default
            .diskCacheSize(100 * 1024 * 1024)
            .diskCacheFileCount(20)
            .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //default
            .defaultDisplayImageOptions(imageDisplayOptions)   //显示配置
                     .writeDebugLogs()  //是否打印调试log
            .build();

            ImageLoaderConfiguration config2 = ImageLoaderConfiguration.createDefault(context);
            // Initialize ImageLoaderUtils with configuration.
            ImageLoader.getInstance().init(config2);
        }
    }


}
