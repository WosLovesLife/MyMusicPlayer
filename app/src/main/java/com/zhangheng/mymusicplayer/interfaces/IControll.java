package com.zhangheng.mymusicplayer.interfaces;

import android.graphics.Bitmap;

import com.zhangheng.mymusicplayer.listener.OnMediaPlayerStateChangedListener;

import java.io.IOException;

/**
 * Created by zhangH on 2016/4/30.
 */
public interface IControll {
    void prepare(String uri) throws IOException;
    void pause();
    void resume();
    void seekProgress(int progress);
    void setRemoteViewInfo(CharSequence musicName, CharSequence singer, Bitmap album, boolean isPlaying);
    void setOnAudioPlayerCreateListener(OnMediaPlayerStateChangedListener onMediaPlayerStateChangedListener);
}
