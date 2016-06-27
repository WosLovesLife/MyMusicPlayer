package com.zhangheng.mymusicplayer.listener;

import com.zhangheng.mymusicplayer.bean.MusicBean;

/**
 * Created by zhangH on 2016/4/30.
 */
public interface OnPlayerStateChangedListener {
    void onChangeMusic(boolean isPlaying, int duration, int progress, MusicBean musicBean);
    void onPlayStateChanged(boolean isPlaying, int duration, int progress, MusicBean musicBean);
//    void onPlay(int duration,int progress,String musicName, String singer);
    void onComplete();
    void updateProgress(int currentProgress);
}
