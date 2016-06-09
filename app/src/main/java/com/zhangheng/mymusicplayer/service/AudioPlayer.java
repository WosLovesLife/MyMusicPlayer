package com.zhangheng.mymusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.interfaces.IControll;
import com.zhangheng.mymusicplayer.listener.OnMediaPlayerStateChangedListener;

import java.io.IOException;

/**
 * Created by zhangH on 2016/4/30.
 */
public class AudioPlayer extends Service {

    private static final String TAG = Constants.TAG;
    private OnMediaPlayerStateChangedListener mOnMediaPlayerStateChangedListener;

    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private static Handler sHandler;

    private void init() {
        mMediaPlayer = new MediaPlayer();
        sHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        updateProgress();
                        break;
                }
            }
        };
//        Looper.loop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOnMediaPlayerStateChangedListener != null) {
            mOnMediaPlayerStateChangedListener.onServiceStop();
        }
        stop();
        if (sHandler != null) {
            sHandler.getLooper().quit();

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

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
        public void setOnAudioPlayerCreateListener(OnMediaPlayerStateChangedListener onMediaPlayerStateChangedListener) {
            AudioPlayer.this.setOnAudioPlayerCreateListener(onMediaPlayerStateChangedListener);
        }
    }

    private void prepare(String uri) throws IOException {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        stopRefreshProgress();
        mMediaPlayer.setDataSource(uri);
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mOnMediaPlayerStateChangedListener != null) {
                    mOnMediaPlayerStateChangedListener.onPrepared();
                }
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mOnMediaPlayerStateChangedListener != null) {
                    mp.stop();
                    mp.reset();
                    stopRefreshProgress();
                    mOnMediaPlayerStateChangedListener.onPlayComplete();
                }
            }
        });
    }

    private void resume() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mOnMediaPlayerStateChangedListener.onResume(mMediaPlayer.getDuration(),mMediaPlayer.getCurrentPosition());
            startRefreshProgress();
        }
    }

    private void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mOnMediaPlayerStateChangedListener.onPause(mMediaPlayer.getDuration(),mMediaPlayer.getCurrentPosition());
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

    private void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            stopRefreshProgress();
        }
    }

    private void startRefreshProgress() {
        sHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void stopRefreshProgress() {
        if (sHandler != null) {
            sHandler.removeCallbacksAndMessages(null);
        }
    }

    private void updateProgress() {
        if (mOnMediaPlayerStateChangedListener != null) {
            mOnMediaPlayerStateChangedListener.onPlaying(mMediaPlayer.getCurrentPosition());
        }
        sHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void setOnAudioPlayerCreateListener(OnMediaPlayerStateChangedListener onMediaPlayerStateChangedListener) {
        mOnMediaPlayerStateChangedListener = onMediaPlayerStateChangedListener;
    }
}
