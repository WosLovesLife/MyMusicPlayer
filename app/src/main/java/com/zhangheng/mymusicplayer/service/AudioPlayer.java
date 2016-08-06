package com.zhangheng.mymusicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.activity.MainPageActivity;
import com.zhangheng.mymusicplayer.broadcast.HeadsetOffBroadcast;
import com.zhangheng.mymusicplayer.broadcast.RemoteViewControlBroadcast;
import com.zhangheng.mymusicplayer.interfaces.IControll;
import com.zhangheng.mymusicplayer.listener.OnMediaPlayerStateChangedListener;

import java.io.IOException;

/**
 * Created by zhangH on 2016/4/30.
 * 负责播放业务的实现, 将播放状态传递出去
 */
public class AudioPlayer extends Service {

    private static final String TAG = "AudioPlayer";

    /* 监听器 */
    private OnMediaPlayerStateChangedListener mOnMediaPlayerStateChangedListener;

    /* 流媒体服务 */
    private MediaPlayer mMediaPlayer;

    /* 远程服务 */
    private Notification mNotification;
    private RemoteViews mRemoteViews;

    /* 广播 */
    private HeadsetOffBroadcast mHeadsetOffBroadcast;
    private RemoteViewControlBroadcast mRemoteViewControlBroadcast;

    @Override
    public void onCreate() {
        super.onCreate();

        setMediaPlayer();

        registerBroadcast();
    }

    /** 注册各类广播事件 */
    private void registerBroadcast() {

        /** 初始化广播接收者 */
        mHeadsetOffBroadcast = new HeadsetOffBroadcast();
        registerHeadsetBroadcast(mHeadsetOffBroadcast);

        mRemoteViewControlBroadcast = new RemoteViewControlBroadcast();
        registerRemoteViewBroadcast(mRemoteViewControlBroadcast);
    }

    /** 创建系统的MediaPlayer对象 以及事件监听 */
    private void setMediaPlayer() {
        mMediaPlayer = new MediaPlayer();

        /* 当播放结束后重置进度,并且通知监听者 */
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopMediaPlayer();
            }
        });

        /* 当要播放的流准备完成后,通知监听者 */
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mOnMediaPlayerStateChangedListener != null) {
                    mOnMediaPlayerStateChangedListener.onPrepared();
                }
            }
        });
    }

    private void stopMediaPlayer() {
        mMediaPlayer.stop();
        stopRefreshProgress();
        mMediaPlayer.reset();
        if (mOnMediaPlayerStateChangedListener != null) {
            mOnMediaPlayerStateChangedListener.onPlayComplete();
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateProgress();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy: ");

        /* 销毁MediaPlayer对象 */
        if (mMediaPlayer != null) {
            stopMediaPlayer();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        /* 销毁Handler */
        if (mHandler != null) {
            stopRefreshProgress();
            mHandler = null;
        }

        /* 销毁通知栏通知和 */
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(2);
        stopForeground(true);

        /* 通知监听者服务销毁 */
        if (mOnMediaPlayerStateChangedListener != null) {
            mOnMediaPlayerStateChangedListener.onServiceStop();
            mOnMediaPlayerStateChangedListener = null;
        }

        /* 卸载广播 */
        unregisterBroadcast(mHeadsetOffBroadcast);
        unregisterBroadcast(mRemoteViewControlBroadcast);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    /** AIDL通信的Binder对象,不过目前并没有使用多线程通信机制 */
    private class MyBinder extends Binder implements IControll {

        @Override
        public void prepare(String uri) throws IOException {
            AudioPlayer.this.prepare(uri);
        }

        @Override
        public void pause() {
            AudioPlayer.this.pause();
        }

        @Override
        public void resume() {
            AudioPlayer.this.resume();
        }

        @Override
        public void seekProgress(int progress) {
            AudioPlayer.this.seekProgress(progress);
        }

        @Override
        public void setRemoteViewInfo(CharSequence musicName, CharSequence singer, Bitmap album, boolean isPlaying) {
            AudioPlayer.this.setForegroundService(musicName, singer, album, isPlaying);
        }

        @Override
        public void setOnAudioPlayerCreateListener(OnMediaPlayerStateChangedListener onMediaPlayerStateChangedListener) {
            AudioPlayer.this.setOnAudioPlayerCreateListener(onMediaPlayerStateChangedListener);
        }
    }

    /** 该方法负责加载音乐流,加载完成后调用回调方法通知Controller */
    private void prepare(String uri) throws IOException {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        stopRefreshProgress();
        mMediaPlayer.setDataSource(uri);
        mMediaPlayer.prepareAsync();
    }

    private void resume() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mOnMediaPlayerStateChangedListener.onResume(mMediaPlayer.getDuration(), mMediaPlayer.getCurrentPosition());
            startRefreshProgress();
        }
    }

    private void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mOnMediaPlayerStateChangedListener.onPause(mMediaPlayer.getDuration(), mMediaPlayer.getCurrentPosition());
            stopRefreshProgress();
        }
    }

    private void seekProgress(int progress) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(progress);
            mMediaPlayer.start();
        } else {
            mMediaPlayer.seekTo(progress);
            mMediaPlayer.pause();
        }
    }


    private void startRefreshProgress() {
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void stopRefreshProgress() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void updateProgress() {
        if (mOnMediaPlayerStateChangedListener != null) {
            mOnMediaPlayerStateChangedListener.onPlaying(mMediaPlayer.getCurrentPosition());
        }
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    /** 生成通知栏通知对象和RemoteView对象,应该只在成员变量为null时调用,避免重复创建对象 */
    private void createNotification() {
        mNotification = new Notification();

        /* 决定应用在状态栏显示的图标, 必须设置,否则不显示应用通知 */
        mNotification.icon = R.drawable.play_btn_play;
        /* 通知推到通知栏是在状态栏中显示的提示(就像QQ有消息时在状态栏显示的文字) */
        mNotification.tickerText = "WosLovesLife Play";

        /* 常驻通知栏, 使应用通知不可清除 */
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        /////// RemoteView-start //////
        /* 自定义通知栏的样式, 参1是应用包名,因为需要系统来托管此服务, 参2是自定义布局样式 */
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.remote_view);

        /* 给remoteView添加个总体布局添加一个点击事件,当点击是跳转到主页
        并且为了防止跳转回应用开启新的Activity,做了一下启动模式的设置 */
        Intent clickAim = new Intent(this, MainPageActivity.class);
        clickAim.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        clickAim.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent open = PendingIntent.getActivity(this, 0, clickAim, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_open, open);

        /* 为上一首按钮增加点击事件 */
        Intent preIntent = RemoteViewControlBroadcast.getIntent(RemoteViewControlBroadcast.EXTRA_STATE_PREVIOUS);
        PendingIntent preBtn = PendingIntent.getBroadcast(this, 1, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_pre_btn, preBtn);

        /* 为播放/暂停按钮增加点击事件 */
        Intent playIntent = RemoteViewControlBroadcast.getIntent(RemoteViewControlBroadcast.EXTRA_STATE_PLAY);
        PendingIntent playBtn = PendingIntent.getBroadcast(this, 2, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_play_btn, playBtn);

        /* 为下一首按钮增加点击事件 */
        Intent nextIntent = RemoteViewControlBroadcast.getIntent(RemoteViewControlBroadcast.EXTRA_STATE_NEXT);
        PendingIntent nextBtn = PendingIntent.getBroadcast(this, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_next_btn, nextBtn);
        ///////RemoteView-end//////

        /* 设置标准大小下的布局和大布局模式下的布局样式 */
        mNotification.contentView = mRemoteViews;
        mNotification.bigContentView = mRemoteViews;

        /* 还不清楚作用 */
        Intent intent = new Intent(this, MainPageActivity.class);
        /* PendingIntent.FLAG_UPDATE_CURRENT 表示如果当前描述的PendingIntent()如果已经存在了,则更新当前通知的状态.
        判断PendingIntent是否存在依据Intent和requestCode是否相同, Intent匹配规则如果ComponentName和intent-filter一致即为相同 */
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.contentIntent = pendingIntent;
    }

    /**
     * 将歌曲播放服务提升为前台服务,防止意外播放中断
     * 先通知栏常驻控制器, 可以显示专辑图片,以及歌曲控制按钮进行播放控制
     *
     * @param album     在通知栏显示专辑图片
     * @param musicName 歌曲名称
     * @param singer    歌手名称
     */
    public void setForegroundService(CharSequence musicName, CharSequence singer, Bitmap album, boolean isPlaying) {

        if (mNotification == null) {
            createNotification();
        }

        /* 表示在是什么时间显示,即当前的时间-立即显示 */
        mNotification.when = System.currentTimeMillis();

        /* 为通知栏自定义布局组件赋值.如果值有问题就赋默认值 */
        mRemoteViews.setTextViewText(R.id.remote_view_name, musicName);
        mRemoteViews.setTextViewText(R.id.remote_view_singer, singer);
        if (album == null) {
            mRemoteViews.setImageViewResource(R.id.remote_view_icon, R.mipmap.icon_launcher);
        } else {
            mRemoteViews.setImageViewBitmap(R.id.remote_view_icon, album);
        }
        if (isPlaying) {
            mRemoteViews.setImageViewResource(R.id.remote_view_play_btn, R.drawable.note_btn_pause_white);
        } else {
            mRemoteViews.setImageViewResource(R.id.remote_view_play_btn, R.drawable.note_btn_play_white);
        }

        /* 将通知推送到系统的通知栏
        notify()的第一个参数表示消息的序号,如果序号相同,表示为同一条通知,如果普通消息通知需要提示多个消息,则参1应该不同
        注意: 即使PendingIntent不同, 只要id相同 通知就会被替换. */
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(2, mNotification);
    }

    private void setOnAudioPlayerCreateListener(OnMediaPlayerStateChangedListener onMediaPlayerStateChangedListener) {
        mOnMediaPlayerStateChangedListener = onMediaPlayerStateChangedListener;
    }

    /////// Broadcast-start //////
    /* 注册耳机拔出事件的广播接收 */
    private void registerHeadsetBroadcast(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver == null) return;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    /* 注册通知栏按钮控制广播接收 */
    private void registerRemoteViewBroadcast(RemoteViewControlBroadcast remoteViewControlBroadcast) {
        if (remoteViewControlBroadcast == null) return;

        IntentFilter intentFilter = RemoteViewControlBroadcast.getIntentFilter();
        registerReceiver(remoteViewControlBroadcast, intentFilter);
    }

    /* 注销对于耳机拔出事件的广播接收 */
    private void unregisterBroadcast(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver == null) return;

        unregisterReceiver(broadcastReceiver);    }
    /////// Broadcast-end //////
}
