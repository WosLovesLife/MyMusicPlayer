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

import org.greenrobot.eventbus.EventBus;

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

    /** 当前的音乐列表集合 */
    private static ArrayList<MusicBean> sMusicBeanArray;
    /** 当前的音乐列表的拼音排序集合,和音乐列表集合一一对应 */
    private static ArrayList<String> sMusicIndexArray;

    /** 当前的索引 = loopIndex % 数据集合.size() */
    private static int sCurrentIndex = -1;
    private MusicBean mCurrentMusic;

    private boolean mSearching;

    // Events
    public class DataChangedEvent {
        public ArrayList<MusicBean> mMusicBeanArray;
        public ArrayList<String> mMusicIndexArray;
        public int mCurrentIndex;

        public DataChangedEvent(ArrayList<MusicBean> sMusicBeanArray, ArrayList<String> sMusicIndexArray, int currentIndex) {
            this.mMusicBeanArray = sMusicBeanArray;
            this.mMusicIndexArray = sMusicIndexArray;
            this.mCurrentIndex = currentIndex;
        }
    }

    public class ItemChangedEvent {
        public int mCurrentIndex;

        public ItemChangedEvent(int currentIndex) {
            this.mCurrentIndex = currentIndex;
        }
    }

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
        if (sCurrentIndex > 0 && sMusicBeanArray.size() > sCurrentIndex) {
            mCurrentMusic = sMusicBeanArray.get(sCurrentIndex);
        } else if (sMusicBeanArray.size() > 0) {
            mCurrentMusic = sMusicBeanArray.get(0);
            sCurrentIndex = 0;
        } else {
            mCurrentMusic = null;
            sCurrentIndex = -1;
        }

        EventBus.getDefault().post(new DataChangedEvent(sMusicBeanArray, sMusicIndexArray, sCurrentIndex));
//        informPlay();
    }

    private void notifyFoundLastSavedMusic(MusicBean musicBean) {
        mCurrentMusic = musicBean;
        Controller.newInstance(mContext).setDefaultMusic(mCurrentMusic);
    }

    private void getMusicListFromDatabase() {
        if (mSearching) {
            Toaster.toast(mContext,"正在初始化数据,请稍等...");
            return;
        }
        mSearching = true;

        SearchMusics.getMusicListFromDatabase(mContext, (musicBeanList, musicIndexList, savedIndex) -> {
            if (musicBeanList.size() > savedIndex) {
                sCurrentIndex = savedIndex;

                notifyFoundLastSavedMusic(musicBeanList.get(savedIndex));
            }
            refreshMusicArray(musicBeanList, musicIndexList);

            mSearching = false;
        });
    }

    public void scanSdcardMusics(SearchMusics.OnMusicSearchingListener onMusicSearchingListener) {
        if (mSearching) {
            Toaster.toast(mContext,"正在扫描本地音乐,请稍等...");
            return;
        }
        mSearching = true;

        SearchMusics.getMusicListFromSdCard(mContext, (musicBeanList, musicIndexList, savedIndex) -> {
            refreshMusicArray(musicBeanList, musicIndexList);
            sCurrentIndex = savedIndex;

            mSearching = false;
        }, onMusicSearchingListener);
    }

    public void getSelectedItem(int position) throws PlayerException {
        sCurrentIndex = position;
        Log.w(TAG, "playSelectedItem: sCurrentIndex: " + sCurrentIndex);
        informPlay();
    }

    public void getDefault() {
        Log.w(TAG, "playSelectedItem: getDefault: " + sCurrentIndex);
        informPlay();
    }

    public void getPre() {
        Log.w(TAG, "playSelectedItem: getPre: " + sCurrentIndex);
        if (--sCurrentIndex < 0) {
            sCurrentIndex = sMusicIndexArray.size() - 1;
        }
        informPlay();
    }

    public void getNext() {
        Log.w(TAG, "playSelectedItem: getNext: " + sCurrentIndex);
        if (++sCurrentIndex >= sMusicBeanArray.size()) {
            sCurrentIndex = 0;
        }
        informPlay();
    }

    private void informPlay() {
        if (sMusicBeanArray.size() > 0) {
            mCurrentMusic = sMusicBeanArray.get(sCurrentIndex);
            Log.w(TAG, "informPlay: mDataChangedListenerList.onItemChanged(sCurrentIndex): " + sCurrentIndex);

            saveMusic();
        } else {
            mCurrentMusic = null;
            Toaster.toast(mContext, "没有歌曲,请尝试扫描本地歌曲");
        }

        Controller.newInstance(mContext).changeMusicTo(mCurrentMusic);
        EventBus.getDefault().post(new ItemChangedEvent(sCurrentIndex));
    }

    /** 调用该方法获取当前的状态, 如果当前还在初始化中, 则尝试查询数据库 */
    public void notifyMusicsEventPost() {
        if (sCurrentIndex > 0) {
            notifyDataSetChanged();
        } else {
            getMusicListFromDatabase();
        }
    }

    public void saveMusic() {
        SharedPreferenceTool.saveInteger(mContext, Constants.KEY_LAST_SAVED_MUSIC, sCurrentIndex);
    }

    public void getLrc() {
        MusicBean musicBean = sMusicBeanArray.get(sCurrentIndex);
        try {
            Mp3File mp3File = new Mp3File(musicBean.getPath());
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }
    }
}
