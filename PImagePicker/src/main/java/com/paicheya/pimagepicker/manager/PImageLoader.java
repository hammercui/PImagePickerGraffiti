package com.paicheya.pimagepicker.manager;

import android.content.Context;
import android.widget.ImageView;

import com.hammer.anlib.pandroidutils.MyLog;


/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/24
 * Description: 显示图片管理类
 * Fix History:
 * =============================
 */

public class PImageLoader {


    private static ImageLoaderInterface imageLoader ;
    public  static void init(ImageLoaderInterface imageLoaderInterface){
        imageLoader = imageLoaderInterface;
    }

    public static void displayImage(Context context, String path, ImageView imageView,int width,int height){
        if(imageLoader  == null){
            MyLog.D("imageLoader is null");
            return;
        }
        //path = Uri.decode(path);//中文
        imageLoader.displayImage(context,path,imageView,width,height);
    }
    public static void clearMemoryCache(Context context){
        imageLoader.clearMemoryCache(context);
    }

    public static void stop(){
        if(imageLoader !=null)
            imageLoader.stop();
    }
}
