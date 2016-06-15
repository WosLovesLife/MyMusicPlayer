package com.zhangheng.mymusicplayer.engine;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import com.project.myutilslibrary.Dp2Px;
import com.project.myutilslibrary.ServiceStateUtils;
import com.project.myutilslibrary.Toaster;
import com.project.myutilslibrary.pictureloader.PictureLoader;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.broadcast.HeadsetOffBroadcast;
import com.zhangheng.mymusicplayer.broadcast.RemoteViewControlBroadcast;
import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.interfaces.IControll;
import com.zhangheng.mymusicplayer.listener.OnMediaPlayerStateChangedListener;
import com.zhangheng.mymusicplayer.listener.OnPlayerStateChangedListener;
import com.zhangheng.mymusicplayer.service.AudioPlayer;

import java.io.IOException;

/**
 * 控制器类,歌曲播放的逻辑判断在这里处理
 * 将播放状态反馈给UI层,
 * 监听MediaPlayerService的状态
 * 通过调度器Dispatcher获取歌曲资源
 * Created by zhangH on 2016/4/30.
 */
public class Controller{
    private static final String TAG = Constants.TAG;

    /** 单例的实例,由newsInstance构造,不为null时返回 */
    private static Controller sController;
    /** 保证单例不易被回收的ApplicationContext全局上下文对象 */
    private Context mContext;
    /** 调用Service方法的IBinder接口 */
    /** 调用Service方法的IBinder接口 */
    private IControll mIControll;
    /** 服务绑定状态的监听器 */
    private ControllerServiceConnection mControllerServiceConnection;

    //-- 沟通Service的变量/常量-start --
    /** Service状态的回调接口,通过此接口MediaPlayerService可以将当前的状态实时回传给本类实例进行相应的处理 */
    private static final int PLAYER_STATE_IDLE = 0x0;
    private static final int PLAYER_STATE_PLAYING = 0x1;
    private static final int PLAYER_STATE_PAUSE = 0x2;
    private int mCurrentPlayerState = PLAYER_STATE_IDLE;
    //-- 沟通Service的变量/常量-end --

    //-- 沟通UI的变量/常量-start --
    private OnPlayerStateChangedListener mOnPlayerStateChangedListener;
    private int mCurrentMusicDuration;
    private int mCurrentMusicProgress;
    //-- 沟通UI的变量/常量-end --

    //-- 沟通调度器的变量/常量-start --
    private MusicBean mCurrentMusicBean;
    private MusicDispatcher mMusicDispatcher;
    private HeadsetOffBroadcast mHeadsetOffBroadcast;
    private RemoteViewControlBroadcast mRemoteViewControlBroadcast;
    //-- 沟通调度器的变量/常量-end --

    private Controller(Context context) {
        mContext = context;
        initRelativeObj();
    }

    /** 创建并返回该单例的实例 */
    public static Controller newInstance(Context context) {
        if (sController == null) {
            synchronized (Controller.class) {
                if (sController == null) {
                    sController = new Controller(context.getApplicationContext());
                }
            }
        }
        return sController;
    }

    private void initRelativeObj() {

        /** 启动服务,单独启动服务是为了让服务独立运行,不伴随Context的生命周期 */
        Intent i = new Intent(mContext, AudioPlayer.class);
        if (!ServiceStateUtils.isRunning(mContext, AudioPlayer.class)) mContext.startService(i);

        /** 绑定服务 */
        mControllerServiceConnection = new ControllerServiceConnection();
        mContext.bindService(i, mControllerServiceConnection, Context.BIND_AUTO_CREATE);

        /** 保定和调度器的关联 */
        mMusicDispatcher = MusicDispatcher.newInstance(mContext);

        /** 初始化广播接收者 */
        mHeadsetOffBroadcast = new HeadsetOffBroadcast();
        registerHeadsetBroadcast(mHeadsetOffBroadcast);

        mRemoteViewControlBroadcast = new RemoteViewControlBroadcast();
        registerRemoteViewBroadcast(mRemoteViewControlBroadcast);
    }

    /** 获取上次应用结束后保存的进度. */
    public void setDefaultMusic(MusicBean musicBean) {
        if (musicBean != null) {
            mCurrentMusicBean = musicBean;
        }
        /** 当调度器数据加载完毕,通过反馈上次的播放状态,让UI同步到正确的位置 */
        updateUi();
    }

    /** 同步当前状态到UI层 */
    private void updateUi() {
        if (mOnPlayerStateChangedListener != null && mCurrentMusicBean != null) {
            mOnPlayerStateChangedListener.onPlayStateChanged(
                    mCurrentPlayerState == PLAYER_STATE_PLAYING,
                    mCurrentMusicDuration,
                    mCurrentMusicProgress,
                    mCurrentMusicBean);
        }
    }

    /** ServiceBind监听器,获取从Service层返回的控制器对象 */
    private class ControllerServiceConnection implements ServiceConnection {

        /** 当服务连接上的时候,强转Ibinder代理接口,用于调用PlayerService的方法 */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIControll = (IControll) service;
            mIControll.setOnAudioPlayerCreateListener(new ControllerOnMediaPlayerStateChangedListener());
            Log.w(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mContext.unbindService(mControllerServiceConnection);
            Log.w(TAG, "onServiceDisconnected: ");
        }
    }

    /** MediaPlayerService中的播放状态产生变化时触发响应的回调方法 */
    private class ControllerOnMediaPlayerStateChangedListener implements OnMediaPlayerStateChangedListener {

        @Override
        public void onResume(int maxProgress, int progress) {
            mCurrentMusicDuration = maxProgress;
            mCurrentMusicProgress = progress;
            mCurrentPlayerState = PLAYER_STATE_PLAYING;

            updateUi();
            updateRemoteView();
        }

        @Override
        public void onPause(int maxProgress, int progress) {
            mCurrentMusicDuration = maxProgress;
            mCurrentMusicProgress = progress;
            mCurrentPlayerState = PLAYER_STATE_PAUSE;

            updateUi();
            updateRemoteView();
        }

        @Override
        public void onPlaying(int currentProgress) {
            mCurrentMusicProgress = currentProgress;
            mCurrentPlayerState = PLAYER_STATE_PLAYING;
            if (mOnPlayerStateChangedListener != null) {
                mOnPlayerStateChangedListener.updateProgress(mCurrentMusicProgress);
            }
        }

        /**
         * 此类向服务发出播放的请求后,需要等到服务准备歌曲资源,因此设置该回调方法,
         * 当服务资源准备好后触发. 此方法更改播放状态,然后调用播放方法
         */
        @Override
        public void onPrepared() {

            /* 通过play()方法开启播放 */
            mCurrentPlayerState = PLAYER_STATE_PAUSE;
            play();
        }

        @Override
        public void onServiceStop() {
            Log.w(TAG, "onServiceStop: ");

            mContext.unbindService(mControllerServiceConnection);

            mMusicDispatcher.saveMusic();

            /* 卸载广播 */
            unregisterBroadcast(mHeadsetOffBroadcast);
            unregisterBroadcast(mRemoteViewControlBroadcast);
        }

        @Override
        public void onPlayComplete() {
            mCurrentPlayerState = PLAYER_STATE_IDLE;
            if (mOnPlayerStateChangedListener != null) {
                mOnPlayerStateChangedListener.onComplete();
            }

            //====== 这里未来要准备一下, 根据不同的播放模式(随机播放, 列表顺序播放等)========
            next();
        }
    }

    /** 通知AudioPlayer更新通知栏样式 */
    private void updateRemoteView() {

        Log.w(TAG, "updateRemoteView: ");
        PictureLoader.newInstance().setCacheBitmapFromMp3Idv3(
                new PictureLoader.OnPictureLoadHandleListener() {
                    @Override
                    public void onPictureLoadComplete(Bitmap bitmap) {
                        mIControll.setRemoteViewInfo(
                                mCurrentMusicBean.getMusicName(),
                                mCurrentMusicBean.getSinger(),
                                bitmap,
                                mCurrentPlayerState == PLAYER_STATE_PLAYING);
                    }
                },
                mCurrentMusicBean.getPath(), null, Dp2Px.toPX(mContext, 100), Dp2Px.toPX(mContext, 100));
    }

    /** UI层通过传递此回调接口, 当本类中的播放状态发生改变的时候,触发相应的回调方法. */
    public void setOnPlayerStateChangedListener(OnPlayerStateChangedListener onPlayerStateChangedListener) {
        mOnPlayerStateChangedListener = onPlayerStateChangedListener;
        /** 当UI界面启动时,如果注册了该监听,则向UI层反馈当前的播放状态,让UI同步到正确的位置 */
        updateUi();
    }

    public void releaseOnPlayerStateChangedListener(){
        mOnPlayerStateChangedListener = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /* 通知音乐服务播放歌曲,本方法执行完后会等待AudioPlayer177行的 onPrepared()方法的回调 */
    public void changeMusicTo(MusicBean musicBean) {
        mCurrentMusicBean = musicBean;

        if (mCurrentMusicBean == null) {
            Toaster.toast(mContext, "没有歌曲");
            return;
        }

        try {
            mIControll.prepare(mCurrentMusicBean.getPath());
        } catch (IOException e) {
            Toaster.toastLong(mContext, "歌曲:\"" + mCurrentMusicBean.getMusicName() + "\"丢失. 请尝试重新扫描本地歌曲");
            next();
        }
        Log.w(TAG, "jumpTo: ");
    }

    /** 调用此播放或暂停,暴露给UI层的方法,无需关注具体的判断处理 */
    public void play() {
        if (mCurrentPlayerState == PLAYER_STATE_PLAYING) {
            mIControll.pause();
        } else if (mCurrentPlayerState == PLAYER_STATE_PAUSE) {
            mIControll.resume();
        } else {
            if (mCurrentMusicBean != null) {
                changeMusicTo(mCurrentMusicBean);
            } else {
                mMusicDispatcher.getDefault();
            }
        }
        Log.w(TAG, "play: mCurrentPlayerState: " + mCurrentPlayerState);
    }

    /** UI层不需要调用该方法,这里专为广播接收者提供 */
    public void specialPlay() {
        if (mCurrentPlayerState == PLAYER_STATE_PAUSE) {
            mIControll.resume();
        }
    }

    /** UI层不需要调用该方法,这里专为广播接收者提供 */
    public void specialPause() {
        if (mCurrentPlayerState == PLAYER_STATE_PLAYING && mIControll != null) {
            mIControll.pause();
        }
    }

    /* 跳转到上一首歌曲,如果跳转前处于播放状态,则跳转后播放,如果跳转前未在播放,则跳转后也不播放 */
    public void pre() {
        mMusicDispatcher.getPre();
        Log.w(TAG, "pre: mCurrentMusicIndex: ");
    }

    /**
     * 跳转到下一首歌曲,如果跳转前处于播放状态,则跳转后播放,
     * 如果跳转前未在播放,则跳转后也不播放
     */
    public void next() {
        mMusicDispatcher.getNext();
        Log.w(TAG, "next: ");
    }

    /** 调用此方法,跳转播放歌曲的进度 */
    public void seekTo(int seekToProgress) {
        Log.w(TAG, "seekTo: mCurrentPlayerState: " + mCurrentPlayerState);
        if (mCurrentPlayerState != PLAYER_STATE_IDLE) {
            mIControll.seekProgress(seekToProgress);
        } else {
            /* 如果歌曲处于不可播放状态而用户拖动了进度条,则按照完成播放处理,将进度置0 */
            mOnPlayerStateChangedListener.onComplete();
        }
    }

    /////// Broadcast-start //////
    /* 注册耳机拔出事件的广播接收 */
    private void registerHeadsetBroadcast(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver == null) return;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    /* 注册通知栏按钮控制广播接收 */
    private void registerRemoteViewBroadcast(RemoteViewControlBroadcast remoteViewControlBroadcast) {
        if (remoteViewControlBroadcast == null) return;

        IntentFilter intentFilter = RemoteViewControlBroadcast.getIntentFilter();
        mContext.registerReceiver(remoteViewControlBroadcast, intentFilter);
    }

    /* 注销对于耳机拔出事件的广播接收 */
    private void unregisterBroadcast(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver == null) return;

        mContext.unregisterReceiver(broadcastReceiver);
    }
    /////// Broadcast-end //////
}
