package com.zhangheng.mymusicplayer.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zhangheng.mymusicplayer.broadcast.HeadsetOffBroadcast;
import com.zhangheng.mymusicplayer.broadcast.RemoteViewControlBroadcast;

/**
 * Created by zhangh on 2016/8/7.
 */
public class BroadcastManager {

    public static BroadcastManager sBroadcastManager;
    private Context mContext;

    /* 广播 */
    private HeadsetOffBroadcast mHeadsetOffBroadcast;
    private RemoteViewControlBroadcast mRemoteViewControlBroadcast;

    private BroadcastManager(Context context){
        mContext = context;
    }

    public static BroadcastManager getInstance(Context context){
        if (sBroadcastManager == null){
            synchronized (BroadcastManager.class){
                if (sBroadcastManager == null){
                    sBroadcastManager = new BroadcastManager(context);
                }
            }
        }
        return sBroadcastManager;
    }

    /** 注册各类广播事件 */
    public void registerBroadcasts() {

        /** 初始化广播接收者 */
        mHeadsetOffBroadcast = new HeadsetOffBroadcast();
        registerHeadsetBroadcast(mHeadsetOffBroadcast);

        mRemoteViewControlBroadcast = new RemoteViewControlBroadcast();
        registerRemoteViewBroadcast(mRemoteViewControlBroadcast);
    }

    /* 卸载广播 */
    public void unregisterBroadcasts() {
        unregisterBroadcast(mHeadsetOffBroadcast);
        unregisterBroadcast(mRemoteViewControlBroadcast);
    }

    /////// Broadcast-start //////
    /* 注册耳机拔出事件的广播接收 */
    private void registerHeadsetBroadcast(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver == null) return;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    /* 注册通知栏按钮控制广播接收 */
    private void registerRemoteViewBroadcast(RemoteViewControlBroadcast remoteViewControlBroadcast) {
        if (remoteViewControlBroadcast == null) return;

        IntentFilter intentFilter = RemoteViewControlBroadcast.getIntentFilter();
        mContext.registerReceiver(remoteViewControlBroadcast, intentFilter);
    }

    /* 注销对于耳机拔出事件的广播接收 */
    private void unregisterBroadcast(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver == null) return;

        mContext.unregisterReceiver(broadcastReceiver);
    }
    /////// Broadcast-end //////
}
