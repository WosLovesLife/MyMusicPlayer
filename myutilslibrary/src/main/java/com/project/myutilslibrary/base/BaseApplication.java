package com.project.myutilslibrary.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhangH on 2016/6/1.
 */
public class BaseApplication extends Application {


    private long mMainThreadID;
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mMainThreadID = Thread.currentThread().getId();

        sContext = this;
    }

    public static Context getGlobalContext(){
        return sContext;
    }
}
