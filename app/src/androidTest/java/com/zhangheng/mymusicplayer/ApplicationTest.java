package com.zhangheng.mymusicplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.project.myutilslibrary.Logger;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.engine.MusicDatabaseEngine;
import com.zhangheng.mymusicplayer.engine.MusicDispatcher;
import com.zhangheng.mymusicplayer.interfaces.IControll;
import com.zhangheng.mymusicplayer.service.AudioPlayer;

import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private static final String TAG = "ApplicationTest";
    private Context mContext;

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
    }

    public void play(){
        Intent i = new Intent(mContext, AudioPlayer.class);
        mContext.startService(i);
        mContext.bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IControll iControll = (IControll) service;

            assertEquals(iControll," ");
            Logger.w("onServiceConnected, iControll: "+iControll);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void testMusicDatabaseEngine(){
        ArrayList<MusicBean> musicBeen = MusicDatabaseEngine.readAll(mContext);
        Log.w(TAG, "testMusicDatabaseEngine: "+musicBeen  );
    }

    public void testCheck4updateDatabase(){
        MusicDispatcher musicDispatcher = MusicDispatcher.newInstance(mContext);
    }
}