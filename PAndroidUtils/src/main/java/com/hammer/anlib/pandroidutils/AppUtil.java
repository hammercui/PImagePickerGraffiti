package com.hammer.anlib.pandroidutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/3/9
 * Description: 系统级工具类，譬如获得sd卡路径，app名称
 * Fix History:
 * =============================
 */

public class AppUtil {
    public static final String DCIM = "/DCIM";
    public static final String PICTURES = "/Pictures";
    /**
     * 获得sd卡的存储路径
     * @return
     */
    public static String  getStorageDirect(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获得相机照片的存储路径
     * @return
     */
    public static String getDCIMDirect(){
        return getStorageDirect() + DCIM;
    }

    public static String getPicturesDirect(){
        return getStorageDirect() + PICTURES;
    }

    public static List<String> appInfo;

    /**
     * 获得本apk的信息
     * @param context
     * @return
     */
    public static List<String> getAppInfo(Context context){
        if(appInfo == null){
            try {
                String packageName = context.getApplicationContext().getPackageName();
                PackageInfo pkg = context.getPackageManager().getPackageInfo(packageName, 0);
                String appName = pkg.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String versionName = pkg.versionName;
                appInfo = new ArrayList<>();
                appInfo.add(appName);
                appInfo.add(versionName);
                return appInfo;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return appInfo;
    }

    /**
     * 获得包路径
     * @param context
     * @return
     */
    public static String getPackageDirect(Context context){
        return context.getApplicationContext().getFilesDir().getAbsolutePath();
    }
}
