package com.zhangheng.mymusicplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zhangheng.mymusicplayer.engine.Controller;

/**
 * Created by zhangH on 2016/6/11.
 */
public class RemoteViewControlBroadcast extends BroadcastReceiver {
    public static final String ACTION = "com.zhangheng.mymusicplayer.service.remote_view_control";

    public static final String EXTRA_NAME = "state";
    public static final int EXTRA_STATE_PREVIOUS = 0;
    public static final int EXTRA_STATE_PLAY = 1;
    public static final int EXTRA_STATE_NEXT = 2;

    public static Intent getIntent(int stateDode){
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra(EXTRA_NAME, stateDode);
        return intent;
    }

    public static IntentFilter getIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        return intentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.hasExtra(EXTRA_NAME)) return;

        switch (intent.getIntExtra(EXTRA_NAME,0)){
            case EXTRA_STATE_PREVIOUS:
                Controller.newInstance(context).pre();
                break;
            case EXTRA_STATE_PLAY:
                Controller.newInstance(context).play();
                break;
            case EXTRA_STATE_NEXT:
                Controller.newInstance(context).next();
                break;
        }
    }
}
