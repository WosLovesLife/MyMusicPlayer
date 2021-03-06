package com.zhangheng.mymusicplayer;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;

import com.zhangheng.mymusicplayer.activity.MainPageActivity;
import com.zhangheng.mymusicplayer.engine.Controller;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhangH on 2016/6/7.
 */
public class MusicApp extends Application {
    public static final String TAG = "MusicApp";

    public static long sTotalDate;

    public class OffTimerEvent{
        public long timerDate;

        public OffTimerEvent(long timerDate) {
            this.timerDate = timerDate;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // 定时关闭程序
                    sTotalDate -= DateUtils.SECOND_IN_MILLIS;

                    Log.w(TAG, "handleMessage: " + sTotalDate);

                    notifyListener();
                    timer();
                    break;
            }
        }
    };

    /** 设置关闭播放器的定时器 */
    public void setOffTimer(long millis) {

        /* 移除原有的定时器,重新定时 */
        cancelOffTimer();

        /* 延迟定时秒数后执行关闭本程序的操作 */
        mHandler.postDelayed(() -> {
            Log.w(TAG, "run: ");
            cancelOffTimer();

            /* 退出所有Activity */
            Intent intent2KillAllActivity = MainPageActivity.getIntent2KillAllActivity(getApplicationContext());
            startActivity(intent2KillAllActivity);

            /* 关闭播放服务 */
            Controller.newInstance(MusicApp.this).stopAudioService();
        }, millis);

        sTotalDate = millis;

        notifyListener();

        mHandler.sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
    }

    private void notifyListener() {
        EventBus.getDefault().post(new OffTimerEvent(sTotalDate));
    }

    private void timer() {
        mHandler.sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
    }

    public boolean isOffTimer() {
        return sTotalDate != 0;
    }

    public void cancelOffTimer() {
        mHandler.removeCallbacksAndMessages(null);
        sTotalDate = 0;
    }
}
