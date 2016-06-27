package com.zhangheng.mymusicplayer.engine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.project.myutilslibrary.PinyinUtils;
import com.project.myutilslibrary.SdcardEnableUtils;
import com.project.myutilslibrary.SharedPreferenceTool;
import com.project.myutilslibrary.Toaster;
import com.project.myutilslibrary.mp3agic.ID3v1;
import com.project.myutilslibrary.mp3agic.ID3v2;
import com.project.myutilslibrary.mp3agic.InvalidDataException;
import com.project.myutilslibrary.mp3agic.Mp3File;
import com.project.myutilslibrary.mp3agic.UnsupportedTagException;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.exception.PlayerException;
import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.listener.OnMusicDispatchDataChangedListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
    private boolean mIsSearching;

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
        AsyncTask4Database asyncTask4Database = new AsyncTask4Database();
        asyncTask4Database.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /** 异步任务,在子线程中读取数据库之前保存的歌曲列表. */
    private class AsyncTask4Database extends AsyncTask<Void, Void, ArrayList<MusicBean>> {

        private ArrayList<String> mTempIndexArray;
        MusicBean mMusicBean = null;

        /** 遍历数据库中的数据,得到当前的歌曲清单 */
        @Override
        protected ArrayList<MusicBean> doInBackground(Void[] params) {
            ArrayList<MusicBean> musicBeansArray = MusicDatabaseEngine.readAll(mContext);
            mTempIndexArray = buildIndexArray(musicBeansArray);
            Collections.sort(musicBeansArray);
            Collections.sort(mTempIndexArray);

            int lastIndex = SharedPreferenceTool.getInteger(mContext, Constants.KEY_LAST_SAVED_MUSIC, 0);

            if (musicBeansArray.size() > lastIndex) {
                mMusicBean = musicBeansArray.get(lastIndex);
                mCurrentIndex = lastIndex;
            }

            return musicBeansArray;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeansArray) {
            refreshMusicArray(musicBeansArray, mTempIndexArray);
            notifyFoundLastSavedMusic(mMusicBean);
        }
    }

    /** 根据提供的ListView<MusicBean> 生成对应的索引ArrayList<String> */
    private ArrayList<String> buildIndexArray(ArrayList<MusicBean> arrayList) {
        ArrayList<String> indexArray = new ArrayList<>();

        if (arrayList != null && arrayList.size() > 0) {
            for (MusicBean m : arrayList) {
                indexArray.add(m.getPinyin().charAt(0) + "");
            }
        }

        return indexArray;
    }

    /** 刷新歌曲列表内容 ,重新扫描SD卡并更新到数据库 */
    public void scanSdcardMusics() {
        if (mIsSearching) {
            Toaster.toastLong(mContext, "扫描中，请稍后...");
            return;
        }

        mIsSearching = true;

        long lastUpdatedTime = SharedPreferenceTool.getLong(mContext, Constants.KEY_LAST_DATABASE_UPDATED_TIME, 0L);
        long currentTime = System.currentTimeMillis();

        Log.w(TAG, "currentTime: " + currentTime + "; lastUpdatedTime: " + lastUpdatedTime);
        Log.w(TAG, "check4updateDatabase: 准备扫描SD卡");

        if (SdcardEnableUtils.isEnable()) {
            Toaster.toastLong(mContext, "正在扫描音乐,请稍候...");
            searchMusicFromSdcard();
            SharedPreferenceTool.saveLong(mContext, Constants.KEY_LAST_DATABASE_UPDATED_TIME, lastUpdatedTime);
        } else {
            Toaster.toast(mContext, "检测外部存储状态异常!");
            mIsSearching = false;
        }

    }

    private void searchMusicFromSdcard() {
        AsyncTask4ScanSdcard asyncTask4ScanSdcard = new AsyncTask4ScanSdcard();
        asyncTask4ScanSdcard.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /** 异步任务,在子线程中读取数据库之前保存的歌曲列表. */
    private class AsyncTask4ScanSdcard extends AsyncTask<Void, Void, ArrayList<MusicBean>> {

        int count;
        private ArrayList<String> mTempIndexArray;
        private ArrayList<MusicBean> mTempMusicBeansArray;

        /** 遍历SD卡,获取符合规范的歌曲 */
        @Override
        protected ArrayList<MusicBean> doInBackground(Void[] params) {
            mTempMusicBeansArray = new ArrayList<>();
            File sdDir = Environment.getExternalStorageDirectory();
            Log.w(TAG, "doInBackground: externalStoragePublicDirectory： " + sdDir.canRead());
            if (SdcardEnableUtils.isEnable()) {
                scanSdcard(sdDir);
                Collections.sort(mTempMusicBeansArray);
                MusicDatabaseEngine.updateContent(mContext, mTempMusicBeansArray);
            }
            mTempIndexArray = buildIndexArray(mTempMusicBeansArray);
            Collections.sort(mTempMusicBeansArray);
            Collections.sort(mTempIndexArray);
            return mTempMusicBeansArray;
        }

        private void scanSdcard(File dir) {
            if (dir == null) return;

            File[] files = dir.listFiles();

            if (files == null || files.length <= 0) return;

            for (File file : files) {
                if (file.isDirectory()) {
                    scanSdcard(file);
                    Log.w(TAG, "scanSdcard: scanSdcard 执行了:" + (count++));
                } else {
                    filterFile(file);
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeansArray) {
            refreshMusicArray(musicBeansArray, mTempIndexArray);

            mIsSearching = false;
        }

        private void filterFile(File file) {
            String filePath = file.getAbsolutePath();
            if (filePath.endsWith(".mp3")) {
                // 获取到文件名
                String unknownName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
                String musicName = unknownName;
                String singer = "未知";
//                // 依照虾米音乐和网易云音乐的命名规范赋音乐名和歌手名
//                int wyIndex = unknownName.indexOf("-");
//                int xmIndex = unknownName.indexOf("_");
//                if (wyIndex != -1) {//网易云音乐的命名方式,左边歌手名,右边歌曲名
//                    musicName = unknownName.substring(wyIndex + 1).trim();
//                    singer = unknownName.substring(0, wyIndex).trim();
//                    Log.w(TAG, "scanSdcard: 网易歌曲: 歌名: " + musicName + "; 歌手: " + singer);
//                } else if (xmIndex != -1) {//虾米音乐的命名方式,左边歌曲名,右边歌手名
//                    musicName = unknownName.substring(0, xmIndex).trim();
//                    singer = unknownName.substring(xmIndex + 1).trim();
//                    Log.w(TAG, "scanSdcard: 虾米歌曲: 歌名: " + musicName + "; 歌手: " + singer);
//                } else {
//                    Log.w(TAG, "scanSdcard: 未知歌曲: 歌名: " + musicName);
//                }
                try {
                    Mp3File m = new Mp3File(filePath);
                    int lengthInSeconds = (int) m.getLengthInSeconds();
                    if (lengthInSeconds > 90) {
                        if (m.hasId3v2Tag()) {
                            ID3v2 id3v2Tag = m.getId3v2Tag();
                            musicName = id3v2Tag.getTitle();
                            singer = id3v2Tag.getArtist();
                        } else if (m.hasId3v1Tag()) {
                            ID3v1 id3v1Tag = m.getId3v1Tag();
                            musicName = id3v1Tag.getTitle();
                            singer = id3v1Tag.getArtist();
                        }
                        mTempMusicBeansArray.add(new MusicBean(-1, musicName, singer, filePath, PinyinUtils.toPinyin(musicName), lengthInSeconds));
                    }
                    Log.w(TAG, "scanSdcard: getLengthInSeconds: " + lengthInSeconds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
