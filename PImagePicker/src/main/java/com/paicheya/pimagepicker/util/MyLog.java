package com.paicheya.pimagepicker.util;

import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * 我的自定义输出
 * Created by cly on 16/11/16.
 */

public class MyLog {


    private static final String MYTAG = "测试";


    public static void log(String TAG,String msg){
        //Logger.i(TAG+MYTAG+"||  "+msg);
        //Logger.i(MYTAG+"|| "+msg);
        Log.d(TAG,msg);
    }

    public static void log(String msg){
        //Logger.i(TAG+MYTAG+"||  "+msg);
       // Logger.i(MYTAG+"|| "+msg);
        Log.d(MYTAG,msg);
    }

    public static void log(Object... args){
        //Logger.i(TAG+MYTAG,args);
        Logger.d(MYTAG,args);
    }
    public static void D(String msg){
        Log.d(MYTAG,msg);
    }
    public static void D(Object... msg){
        Logger.d(MYTAG,msg);
    }

}
