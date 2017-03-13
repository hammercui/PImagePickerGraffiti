package com.hammer.anlib.pandroidutils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.permission;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/3/2
 * Description: 权限管理工具 sdk>16 主动申请权限
 * Fix History:
 * =============================
 */

public class PermissionUtil {

    //申请sd卡读取权限
    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 1;
    //申请通讯录读取全新啊
    public static final int REQUEST_READ_CONTACTS = 1;

    public static boolean checkPermission(Context context,final String permission,final int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
            return false;
        }
        return  true;
    }

    public static boolean checkPermissions(Context context,final String[] permissions,final int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            List<String> needPermissions = new ArrayList<>();
            //填充需要申请的权限
            for (int i=0,len = permissions.length;i<len;i++){
                if(ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED){
                    needPermissions.add(permissions[i]);
                }
            }

            if(needPermissions.size() == 0)
                return true;
            //批量申请权限
            ActivityCompat.requestPermissions((Activity) context, needPermissions.toArray(new String[needPermissions.size()]), requestCode);
            return false;
        }
        return  true;
    }

}
