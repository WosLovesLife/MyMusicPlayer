package com.zhangheng.mymusicplayer;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;

import com.project.myutilslibrary.ServiceStateUtils;
import com.zhangheng.mymusicplayer.listener.OnOffTimerListener;
import com.zhangheng.mymusicplayer.service.AudioPlayer;

/**
 * Created by zhangH on 2016/6/7.
 */
public class MusicApp extends Application {

    public static long sTotalDate;

    private static OnOffTimerListener sOffTimerListener;

    private Handler mHandler = new Handler() {
        public static final String TAG = "MusicApp";

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // 定时关闭程序
                    sTotalDate -= DateUtils.SECOND_IN_MILLIS;
                    if (sOffTimerListener != null) {
                        sOffTimerListener.onOffTimer((sTotalDate));
                    }
                    timer();
                    break;
            }
        }
    };

    public void setOnOffTimerListener(OnOffTimerListener offTimerListener){
        sOffTimerListener = offTimerListener;
    }

    public void setOffTimer(long millis, OnOffTimerListener offTimerListener) {
        sOffTimerListener = offTimerListener;

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.removeCallbacksAndMessages(null);

                if (ServiceStateUtils.isRunning(MusicApp.this, AudioPlayer.class)) {
                    mHandler.removeCallbacksAndMessages(null);
                    Intent i = new Intent(MusicApp.this, AudioPlayer.class);
                    stopService(i);
                    System.exit(0);
                }
            }
        }, millis);

        sTotalDate = millis;

        mHandler.sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
    }

    private void timer() {
        mHandler.sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
    }

    public boolean isOffTimer(){
        return sTotalDate != 0;
    }

    public void cancelOffTimer() {
        mHandler.removeCallbacksAndMessages(null);
        sTotalDate = 0;
    }
}
