package com.paicheya.pimagepicker.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 *自定义文件类工具
 * Created by cly on 16/11/18.
 */

public class MyFileUtil {


    /**
     * 获得文件夹在包路径绝对地址
     * @param context
     * @param folder 给定文件夹
     * @return
     */
    public static String getFolderInAppPackage(Context context,String folder){
        String path = context.getFilesDir().getAbsolutePath()+"/"+folder;
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
        return path;
    }

    /**
     * 获得文件夹在sd卡绝对地址
     * @param folder 给定文件夹
     * @return
     */
    public static String getFolderInExternalStorage(String folder){
        String path = Environment.getExternalStorageDirectory()+"/DCIM/"+folder;
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
        return path;
    }

}
