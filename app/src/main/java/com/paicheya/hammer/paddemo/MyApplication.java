package com.paicheya.hammer.paddemo;

import android.app.Application;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by cly on 17/2/8.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        initApp();

    }

    private void initApp(){
        initAppLog();
    }

    private void initAppLog(){
//        LogConfiguration config = new LogConfiguration
//                .Builder()
//                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
//                        : LogLevel.NONE)
//                .tag("MY_TAG")                                         // 指定 TAG，默认为 "X-LOG"
//                .t()                                                   // 允许打印线程信息，默认禁止
//                .st(2)                                                 // 允许打印深度为2的调用栈信息，默认禁止
//                .b()
//                .build();
//        XLog.init(config);
    }

}
