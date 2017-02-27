package com.paicheya.hammer.paddemo;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/27
 * Description:
 * Fix History:
 * =============================
 */

public class CrashManage {
    private static final String LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crash/log/";
    private static  String LOG_NAME ;
    private ArrayList<Activity> list = new ArrayList<Activity>();

    private  MyCatchExceptionHandler handler = null;
    private  static   CrashManage  ins;
    private CrashManage(){};

    public static CrashManage getIns(){
        if(ins == null){
            synchronized (CrashManage.class){
                if(ins == null)
                    ins = new CrashManage();
            }
        }
        return ins;
    }

    public  void init(String appName){
        LOG_NAME = appName +"_"+getCurrentDateString() + ".txt";

        if(handler  ==  null){
            handler = new MyCatchExceptionHandler();
        }
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }



    private class MyCatchExceptionHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            writeErrorLog(ex);
            exit();
        }
    }
    /**
     * 打印错误日志
     *
     * @param ex
     */
    protected void writeErrorLog(Throwable ex) {
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("crashlog", "崩溃信息\n" + info);
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, LOG_NAME);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取当前日期
     *
     * @return
     */
    private static String getCurrentDateString() {
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        Date nowDate = new Date();
        result = sdf.format(nowDate);
        return result;
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void exit() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
