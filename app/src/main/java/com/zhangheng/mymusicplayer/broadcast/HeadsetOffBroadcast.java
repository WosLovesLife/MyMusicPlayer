package com.zhangheng.mymusicplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zhangheng.mymusicplayer.engine.Controller;

/**
 * Created by zhangH on 2016/6/2.
 */
public class HeadsetOffBroadcast extends BroadcastReceiver {
    private static final String TAG = "HeadsetOffBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "onReceive: "+ intent.getIntExtra("state",0) );
        if (!intent.hasExtra("state")) return;

        switch (intent.getIntExtra("state", 0)) {
            case 0:
                Controller.newInstance(context).specialPause();
                break;
            case 1:
                break;
        }
    }
}