package com.zhangheng.mymusicplayer.listener;

/**
 * Created by zhangH on 2016/4/30.
 */
public interface OnPlayerStateChangedListener {
    void onPlayStateChanged(boolean isPlaying,int duration,int progress,String musicName, String singer);
//    void onPlay(int duration,int progress,String musicName, String singer);
    void onComplete();
    void updateProgress(int currentProgress);
}
