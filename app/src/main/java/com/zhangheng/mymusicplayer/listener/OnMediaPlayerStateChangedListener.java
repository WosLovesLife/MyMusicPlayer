package com.zhangheng.mymusicplayer.listener;

/**
 * Created by zhangH on 2016/5/16.
 */
public interface OnMediaPlayerStateChangedListener {
    void onResume(int maxProgress,int currentProgress);
    void onPause(int maxProgress,int currentProgress);
    void onPlaying(int currentProgress);
    void onPrepared();
    void onServiceStop();
    void onPlayComplete();
}
