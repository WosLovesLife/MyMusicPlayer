// IOnMediaPlayerStateChangedListener.aidl
package com.zhangheng.mymusicplayer.listener;

// Declare any non-default types here with import statements

interface IOnMediaPlayerStateChangedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onResume(int maxProgress,int currentProgress);
        void onPause(int maxProgress,int currentProgress);
        void onPlaying(int currentProgress);
        void onPrepared();
        void onServiceStop();
        void onPlayComplete();
}
