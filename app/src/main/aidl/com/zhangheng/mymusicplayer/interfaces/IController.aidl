// IController.aidl
package com.zhangheng.mymusicplayer.interfaces;

// Declare any non-default types here with import statements
import com.zhangheng.mymusicplayer.listener.IOnMediaPlayerStateChangedListener;

interface IController {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void prepare(String uri);
        void pause();
        void resume();
        void seekProgress(int progress);
        void setOnAudioPlayerCreateListener(IOnMediaPlayerStateChangedListener onMediaPlayerStateChangedListener);
}
