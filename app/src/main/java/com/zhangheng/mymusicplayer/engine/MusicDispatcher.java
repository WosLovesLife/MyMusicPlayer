package com.zhangheng.mymusicplayer.engine;

import android.content.Context;
import android.util.Log;

import com.project.myutilslibrary.SharedPreferenceTool;
import com.project.myutilslibrary.Toaster;
import com.project.myutilslibrary.mp3agic.ID3v2;
import com.project.myutilslibrary.mp3agic.InvalidDataException;
import com.project.myutilslibrary.mp3agic.Mp3File;
import com.project.myutilslibrary.mp3agic.UnsupportedTagException;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.exception.PlayerException;
import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.listener.OnMusicDispatchDataChangedListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zhangH on 2016/5/17.
 * MusicListFragment通过设置监听,来通过ListView的Item的状态
 * Controller通过和此类交互获取歌曲的来源和上一曲下一曲的顺序等.
 */
public class MusicDispatcher {
    private static final String TAG = Constants.TAG;

    private static MusicDispatcher sMusicDispatcher;
    private Context mContext;

    private OnMusicDispatchDataChangedListener mDataChangedListenerList;

    /** 当前的音乐列表集合 */
    private static ArrayList<MusicBean> sMusicBeanArray;
    /** 当前的音乐列表的拼音排序集合,和音乐列表集合一一对应 */
    private static ArrayList<String> sMusicIndexArray;

    /** 当前的索引 = loopIndex % 数据集合.size() */
    private int mCurrentIndex = -1;
    private MusicBean mCurrentMusic;

    private MusicDispatcher(Context context) {
        mContext = context;
        initData();
    }

    public static MusicDispatcher newInstance(
            Context context) {
        if (sMusicDispatcher == null) {
            sMusicDispatcher = new MusicDispatcher(context.getApplicationContext());
        }
        return sMusicDispatcher;
    }

    public void setOnMusicDispatchDataChangedListener(OnMusicDispatchDataChangedListener onMusicDispatchDataChangedListener) {
        mDataChangedListenerList = onMusicDispatchDataChangedListener;
        notifyDataSetChanged();
    }

    private void initData() {
        sMusicBeanArray = new ArrayList<>();
        sMusicIndexArray = new ArrayList<>();
        getMusicListFromDatabase();
    }

    private void refreshMusicArray(ArrayList<MusicBean> array, ArrayList<String> indexArray) {
        sMusicBeanArray.clear();
        sMusicBeanArray.addAll(array);
        sMusicIndexArray.clear();
        sMusicIndexArray.addAll(indexArray);
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        if (mDataChangedListenerList != null) {
            mDataChangedListenerList.onDispatchDataChanged(sMusicBeanArray, sMusicIndexArray, mCurrentIndex);
        }
    }

    private void notifyFoundLastSavedMusic(MusicBean musicBean) {
        mCurrentMusic = musicBean;
        Controller.newInstance(mContext).setDefaultMusic(mCurrentMusic);
    }

    private void getMusicListFromDatabase() {
        SearchMusics.getMusicListFromDatabase(mContext, new SearchMusics.OnFinishedListener() {
            @Override
            public void onFinished(ArrayList<MusicBean> musicBeanList, ArrayList<String> musicIndexList, int savedIndex) {
                if (musicBeanList.size() > savedIndex) {
                    mCurrentIndex = savedIndex;

                    notifyFoundLastSavedMusic(musicBeanList.get(savedIndex));
                }
                refreshMusicArray(musicBeanList, musicIndexList);
            }
        });
    }

    public void scanSdcardMusics() {
        SearchMusics.getMusicListFromSdCard(mContext, new SearchMusics.OnFinishedListener() {
            @Override
            public void onFinished(ArrayList<MusicBean> musicBeanList, ArrayList<String> musicIndexList, int savedIndex) {
                refreshMusicArray(musicBeanList, musicIndexList);
                mCurrentIndex = savedIndex;
            }
        });
    }


    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    public void playSelectedItem(int position) throws PlayerException {
        mCurrentIndex = position;
        Log.w(TAG, "playSelectedItem: mCurrentIndex: " + mCurrentIndex);
        informPlay();
    }

    public void getDefault() {
        Log.w(TAG, "playSelectedItem: getDefault: " + mCurrentIndex);
        informPlay();
    }

    public void getPre() {
        Log.w(TAG, "playSelectedItem: getPre: " + mCurrentIndex);
        if (--mCurrentIndex < 0) {
            mCurrentIndex = sMusicIndexArray.size() - 1;
        }
        informPlay();
    }

    public void getNext() {
        Log.w(TAG, "playSelectedItem: getNext: " + mCurrentIndex);
        if (++mCurrentIndex >= sMusicBeanArray.size()) {
            mCurrentIndex = 0;
        }
        informPlay();
    }

    private void informPlay() {
        if (sMusicBeanArray.size() > 0) {
            mCurrentMusic = sMusicBeanArray.get(mCurrentIndex);
            Controller.newInstance(mContext).changeMusicTo(mCurrentMusic);

            if (mDataChangedListenerList != null) {
                mDataChangedListenerList.onItemChanged(mCurrentIndex);
            }
            Log.w(TAG, "informPlay: mDataChangedListenerList.onItemChanged(mCurrentIndex): " + mCurrentIndex);

            saveMusic();
        } else {
            Toaster.toast(mContext, "没有歌曲,请尝试扫描本地歌曲");
        }

    }

    public void saveMusic() {
        SharedPreferenceTool.saveInteger(mContext, Constants.KEY_LAST_SAVED_MUSIC, mCurrentIndex);
    }

    public void getLrc() {
        MusicBean musicBean = sMusicBeanArray.get(mCurrentIndex);
        try {
            Mp3File mp3File = new Mp3File(musicBean.getPath());
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }
}
