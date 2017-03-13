package com.hammer.anlib.pandroidutils;

import android.util.Log;


/**
 * 日志工具类
 */

public class MyLog {


    private static final String MYTAG = "测试";

    private static boolean debug = true;
    public static void setDebug(boolean isDebug){
        debug = isDebug;
    }

    public static void log(String TAG,String msg){
        if(!debug)
            return;
        //Logger.i(TAG+MYTAG+"||  "+msg);
        //Logger.i(MYTAG+"|| "+msg);
        Log.d(TAG,msg);
    }

    public static void log(String msg){
        if(!debug)
            return;
        //Logger.i(TAG+MYTAG+"||  "+msg);
       // Logger.i(MYTAG+"|| "+msg);
        Log.d(MYTAG,msg);
    }

    public static void log(Object... args){
        if(!debug)
            return;
        //Logger.i(TAG+MYTAG,args);
        //Logger.d(MYTAG,args);
        Log.d(MYTAG,args[0].toString());
    }
    public static void D(String msg){
        if(!debug)
            return;
        Log.d(MYTAG,msg);
    }
    public static void D(Object... msg){
        if(!debug)
            return;
        //Logger.d(MYTAG,msg);
        Log.d(MYTAG,msg[0].toString());
    }

    public static void E(String msg){
        Log.e(MYTAG,msg);
    }

}
