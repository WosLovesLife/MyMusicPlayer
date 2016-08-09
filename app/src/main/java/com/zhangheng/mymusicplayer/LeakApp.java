package com.zhangheng.mymusicplayer;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by zhangh on 2016/8/9.
 */
public class LeakApp extends Application {

    private RefWatcher mInstall;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstall = LeakCanary.install(this);
    }
}
